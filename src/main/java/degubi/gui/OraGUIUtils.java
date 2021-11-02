package degubi.gui;

import degubi.*;
import degubi.db.*;
import degubi.model.*;
import java.util.*;
import java.util.concurrent.*;
import java.util.function.*;
import java.util.stream.*;
import javafx.application.*;
import javafx.collections.*;
import javafx.geometry.*;
import javafx.scene.*;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.text.*;
import javafx.stage.*;

public final class OraGUIUtils {
    private OraGUIUtils() {}

    public static void showEditorDialog(Ora toEdit, TableView<Ora> table) {
        var napComboBox = new ComboBox<>(FXCollections.observableArrayList("Hétfő", "Kedd", "Szerda", "Csütörtök", "Péntek"));
        var idopontField = new TextField();
        var nevField = new TextField();
        var osztalyComboBox = new ComboBox<>(OsztalyDBUtils.listAll().join());
        var teremComboBox = new ComboBox<>(TeremDBUtils.listAll().join());
        var tanarComboBox = new ComboBox<>(TanarDBUtils.listAll().join());

        var okButtonBinding = Components.createEmptyComboBoxBinding(napComboBox)
                                        .or(Components.createTimeFieldBinding(idopontField))
                                        .or(Components.createEmptyFieldBinding(nevField))
                                        .or(Components.createEmptyComboBoxBinding(osztalyComboBox))
                                        .or(Components.createEmptyComboBoxBinding(teremComboBox))
                                        .or(Components.createEmptyComboBoxBinding(tanarComboBox));
        if(toEdit != null) {
            napComboBox.setValue(toEdit.nap);
            idopontField.setText(toEdit.idopont);
            nevField.setText(toEdit.nev);
            osztalyComboBox.setValue(toEdit.osztaly);
            teremComboBox.setValue(toEdit.terem);
            tanarComboBox.setValue(toEdit.tanar);
        }

        var components = Components.newFormGridPane();
        var stage = new Stage();
        components.add(Components.newLabel("Nap:"), 0, 0);
        components.add(napComboBox, 1, 0);
        components.add(Components.newLabel("Időpont:"), 0, 1);
        components.add(idopontField, 1, 1);
        components.add(Components.newLabel("Név:"), 0, 2);
        components.add(nevField, 1, 2);
        components.add(Components.newLabel("Osztály"), 0, 3);
        components.add(osztalyComboBox, 1, 3);
        components.add(Components.newLabel("Terem"), 0, 4);
        components.add(teremComboBox, 1, 4);
        components.add(Components.newLabel("Tanár"), 0, 5);
        components.add(tanarComboBox, 1, 5);
        components.add(Components.newEditorButtonPanel(toEdit != null, stage, okButtonBinding,
                                                       e -> handleInteractButtonClick(napComboBox, idopontField, nevField, osztalyComboBox, teremComboBox, tanarComboBox, toEdit, stage, table )), 0, 6, 2, 1);

        stage.setScene(new Scene(components, 400, 400));
        stage.setTitle("Új Óra");
        stage.getScene().getRoot().setStyle(Components.windowTheme);
        stage.show();
    }

    public static TableView<Ora> createTable() {
        return Components.newTable(OraGUIUtils::showEditorDialog, OraGUIUtils::handleDeleteButtonClick,
                                   Components.newNumberColumn("Azonosító", Ora.fieldMappings),
                                   Components.newStringColumn("Nap", Ora.fieldMappings),
                                   Components.newStringColumn("Időpont", Ora.fieldMappings),
                                   Components.newStringColumn("Név", Ora.fieldMappings),
                                   Components.newStringColumn("Osztály", Ora.fieldMappings),
                                   Components.newStringColumn("Terem", Ora.fieldMappings),
                                   Components.newStringColumn("Tanár", Ora.fieldMappings));
    }

    public static void refreshTable(TableView<Ora> table) {
        OraDBUtils.listAll()
                  .thenAccept(table::setItems)
                  .thenRun(() -> Main.loadingLabel.setVisible(false));
    }

    public static void refreshFilteredTable(String fieldName, String value, TableView<Ora> table) {
        OraDBUtils.listFiltered(Ora.fieldMappings.get(fieldName), value)
                  .thenAccept(table::setItems)
                  .thenRun(() -> Main.loadingLabel.setVisible(false));
    }

    public static void handleTeacherTableSwitch(GridPane timetable, ComboBox<Tanar> teachersComboBox) {
        teachersComboBox.setItems(TanarDBUtils.listAll().join());
        teachersComboBox.getSelectionModel().selectFirst();

        refreshTeacherTable(timetable, teachersComboBox);
    }

    public static void handleClassTableSwitch(GridPane timeTable, ComboBox<Osztaly> classesComboBox) {
        classesComboBox.setItems(OsztalyDBUtils.listAll().join());
        classesComboBox.getSelectionModel().selectFirst();

        refreshClassTable(timeTable, classesComboBox);
    }

    public static void refreshTeacherTable(GridPane timetable, ComboBox<Tanar> teachersComboBox) {
        refreshTimeTable(timetable, () -> OraDBUtils.listFor(teachersComboBox.getValue()));
    }

    public static void refreshClassTable(GridPane timetable, ComboBox<Osztaly> classesComboBox) {
        refreshTimeTable(timetable, () -> OraDBUtils.listFor(classesComboBox.getValue()));
    }


    @SuppressWarnings("boxing")
    private static void refreshTimeTable(GridPane timetable, Supplier<CompletableFuture<ObservableList<Ora>>> oraListaSupplier) {
        timetable.getChildren().clear();
        timetable.add(newCenteredLabel("Hétfő"), 0, 0);
        timetable.add(newCenteredLabel("Kedd"), 1, 0);
        timetable.add(newCenteredLabel("Szerda"), 2, 0);
        timetable.add(newCenteredLabel("Csütörtök"), 3, 0);
        timetable.add(newCenteredLabel("Péntek"), 4, 0);

        oraListaSupplier.get()
                        .thenApply(k -> k.stream().collect(Collectors.groupingBy(m -> m.napIndex)))
                        .thenAccept(k -> {
                            addClassesForDay(0, k, timetable);
                            addClassesForDay(1, k, timetable);
                            addClassesForDay(2, k, timetable);
                            addClassesForDay(3, k, timetable);
                            addClassesForDay(4, k, timetable);
                        })
                        .thenRun(() -> Main.loadingLabel.setVisible(false));
    }


    @SuppressWarnings("boxing")
    private static void addClassesForDay(int dayIndex, Map<Integer, List<Ora>> data, GridPane timetable) {
        var kek = data.getOrDefault(dayIndex, List.of());

        IntStream.range(0, kek.size())
                 .forEach(rowIndex -> {
                     var rend = kek.get(rowIndex);
                     var labelText = "Időpont: " + rend.idopont + "\n" +
                                     "Tárgy: " + rend.nev + "\n" +
                                     "Osztály: " + rend.osztaly.megnevezes + "\n" +
                                     "Terem: " + rend.terem.toString();

                     Platform.runLater(() -> timetable.add(newCenteredLabel(labelText), dayIndex, rowIndex + 1));
                 });
    }

    private static Text newCenteredLabel(String text) {
        var label = new Text(text);
        GridPane.setHalignment(label, HPos.CENTER);
        return label;
    }

    private static void handleInteractButtonClick(ComboBox<String> napComboBox, TextField idopontField, TextField nevField, ComboBox<Osztaly> osztalyComboBox,
                                                  ComboBox<Terem> teremComboBox, ComboBox<Tanar> tanarComboBox, Ora toEdit, Stage window, TableView<Ora> table) {
        if(toEdit != null) {
            OraDBUtils.update(toEdit, napComboBox.getSelectionModel().getSelectedIndex(), idopontField.getText(), nevField.getText(),
                              tanarComboBox.getValue(), osztalyComboBox.getValue(), teremComboBox.getValue());
        }else {
            OraDBUtils.add(napComboBox.getSelectionModel().getSelectedIndex(), idopontField.getText(), nevField.getText(),
                           tanarComboBox.getValue(), osztalyComboBox.getValue(), teremComboBox.getValue());
        }

        window.hide();
        refreshTable(table);
    }

    private static void handleDeleteButtonClick(TableView<Ora> table, int index) {
        Components.showConfirmation("Biztos törlöd ezt az órát?", () -> {
            OraDBUtils.delete(table.getItems().get(index));
            refreshTable(table);
        });
    }
}