package degubi.gui;

import degubi.mapping.*;
import degubi.model.*;
import java.util.*;
import java.util.concurrent.*;
import java.util.function.*;
import java.util.stream.*;
import javafx.application.*;
import javafx.collections.*;
import javafx.geometry.*;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.text.*;
import javafx.stage.*;

public final class OraGUIUtils {
    private OraGUIUtils() {}

    public static void showEditorDialog(Ora toEdit, TableView<Ora> table) {
        var napComboBox = new ComboBox<>(FXCollections.observableArrayList("Hétfő", "Kedd", "Szerda", "Csütörtök", "Péntek"));
        var idopontField = new TextField();
        var tantargyComboBox = new ComboBox<>(TimetableDB.listAll(Tantargy.class).join());
        var osztalyComboBox = new ComboBox<>(TimetableDB.listAll(Osztaly.class).join());
        var teremComboBox = new ComboBox<>(TimetableDB.listAll(Terem.class).join());
        var tanarComboBox = new ComboBox<>(TimetableDB.listAll(Tanar.class).join());

        var okButtonBinding = Components.createEmptyComboBoxBinding(napComboBox)
                                        .or(Components.createTimeFieldBinding(idopontField))
                                        .or(Components.createEmptyComboBoxBinding(tantargyComboBox))
                                        .or(Components.createEmptyComboBoxBinding(osztalyComboBox))
                                        .or(Components.createEmptyComboBoxBinding(teremComboBox))
                                        .or(Components.createEmptyComboBoxBinding(tanarComboBox));
        if(toEdit != null) {
            napComboBox.setValue(toEdit.nap);
            idopontField.setText(toEdit.idopont);
            tantargyComboBox.setValue(toEdit.tantargy);
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
        components.add(Components.newLabel("Tantárgy:"), 0, 2);
        components.add(tantargyComboBox, 1, 2);
        components.add(Components.newLabel("Osztály"), 0, 3);
        components.add(osztalyComboBox, 1, 3);
        components.add(Components.newLabel("Terem"), 0, 4);
        components.add(teremComboBox, 1, 4);
        components.add(Components.newLabel("Tanár"), 0, 5);
        components.add(tanarComboBox, 1, 5);
        components.add(Components.newEditorButtonPanel(toEdit != null, stage, okButtonBinding,
                                                       e -> handleInteractButtonClick(napComboBox, idopontField, tantargyComboBox, osztalyComboBox, teremComboBox, tanarComboBox, toEdit, stage, table )), 0, 6, 2, 1);

        Components.showEditorWindow("Óra Szerkesztő", components, stage);
    }

    public static TableView<Ora> createTable() {
        return Components.newTable(OraGUIUtils::showEditorDialog, OraGUIUtils::handleDeleteButtonClick,
                                   Components.newNumberColumn("Azonosító", Ora.fieldMappings),
                                   Components.newStringColumn("Nap", Ora.fieldMappings),
                                   Components.newStringColumn("Időpont", Ora.fieldMappings),
                                   Components.newStringColumn("Tantárgy", Ora.fieldMappings),
                                   Components.newStringColumn("Osztály", Ora.fieldMappings),
                                   Components.newStringColumn("Terem", Ora.fieldMappings),
                                   Components.newStringColumn("Tanár", Ora.fieldMappings));
    }

    public static void refreshTable(TableView<Ora> table) {
        TimetableDB.listAll(Ora.class)
                   .thenAccept(table::setItems);
    }

    public static void refreshFilteredTable(String labelName, String value, TableView<Ora> table) {
        TimetableDB.listFilteredOra(Ora.fieldMappings.get(labelName), value)
                   .thenAccept(table::setItems);
    }

    public static void handleTeacherTableSwitch(GridPane timetable, ComboBox<Tanar> teachersComboBox) {
        teachersComboBox.setItems(TimetableDB.listAll(Tanar.class).join());
        teachersComboBox.getSelectionModel().selectFirst();

        refreshTeacherTable(timetable, teachersComboBox.getValue());
    }

    public static void handleClassTableSwitch(GridPane timeTable, ComboBox<Osztaly> classesComboBox) {
        classesComboBox.setItems(TimetableDB.listAll(Osztaly.class).join());
        classesComboBox.getSelectionModel().selectFirst();

        refreshClassTable(timeTable, classesComboBox.getValue());
    }

    public static void refreshTeacherTable(GridPane timetable, Tanar selected) {
        if(selected != null) {
            refreshTimeTable(timetable, OraGUIUtils::createTimetableLabelForTeacher, () -> TimetableDB.listOraFor(selected));
        }
    }

    public static void refreshClassTable(GridPane timetable, Osztaly selected) {
        if(selected != null) {
            refreshTimeTable(timetable, OraGUIUtils::createTimetableLabelForStudent, () -> TimetableDB.listOraFor(selected));
        }
    }


    @SuppressWarnings("boxing")
    private static void refreshTimeTable(GridPane timetable, Function<Ora, String> labelCreator, Supplier<CompletableFuture<ObservableList<Ora>>> oraListaSupplier) {
        timetable.getChildren().clear();
        timetable.add(newCenteredLabel("Hétfő"), 0, 0);
        timetable.add(newCenteredLabel("Kedd"), 1, 0);
        timetable.add(newCenteredLabel("Szerda"), 2, 0);
        timetable.add(newCenteredLabel("Csütörtök"), 3, 0);
        timetable.add(newCenteredLabel("Péntek"), 4, 0);

        oraListaSupplier.get()
                        .thenApply(k -> k.stream().collect(Collectors.groupingBy(m -> m.napIndex)))
                        .thenAccept(k -> {
                            addClassesForDay(0, labelCreator, k, timetable);
                            addClassesForDay(1, labelCreator, k, timetable);
                            addClassesForDay(2, labelCreator, k, timetable);
                            addClassesForDay(3, labelCreator, k, timetable);
                            addClassesForDay(4, labelCreator, k, timetable);
                        });
    }

    private static String createTimetableLabelForTeacher(Ora rend) {
        return "Időpont: " + rend.idopont + "\n" +
                "Tárgy: " + rend.tantargy.nev + "\n" +
                "Osztály: " + rend.osztaly.megnevezes + "\n" +
                "Terem: " + rend.terem.toString();
    }

    private static String createTimetableLabelForStudent(Ora rend) {
        return "Időpont: " + rend.idopont + "\n" +
                "Tárgy: " + rend.tantargy.nev + "\n" +
                "Tanár: " + rend.tanar.nev + "\n" +
                "Terem: " + rend.terem.toString();
    }


    @SuppressWarnings("boxing")
    private static void addClassesForDay(int dayIndex, Function<Ora, String> labelCreator, Map<Integer, List<Ora>> data, GridPane timetable) {
        var kek = data.getOrDefault(dayIndex, List.of());

        IntStream.range(0, kek.size())
                 .forEach(rowIndex -> {
                     var labelText = labelCreator.apply(kek.get(rowIndex));

                     Platform.runLater(() -> timetable.add(newCenteredLabel(labelText), dayIndex, rowIndex + 1));
                 });
    }

    private static Text newCenteredLabel(String text) {
        var label = Components.newLabel(text);
        GridPane.setHalignment(label, HPos.CENTER);
        return label;
    }

    private static void handleInteractButtonClick(ComboBox<String> napComboBox, TextField idopontField, ComboBox<Tantargy> tantargyComboBox, ComboBox<Osztaly> osztalyComboBox,
                                                  ComboBox<Terem> teremComboBox, ComboBox<Tanar> tanarComboBox, Ora toEdit, Stage window, TableView<Ora> table) {
        if(toEdit != null) {
            TimetableDB.update(toEdit, new Ora(toEdit.azonosito, napComboBox.getSelectionModel().getSelectedIndex(), idopontField.getText(), tantargyComboBox.getValue(),
                                           tanarComboBox.getValue(), osztalyComboBox.getValue(), teremComboBox.getValue()));
        }else {
            TimetableDB.add(new Ora(0, napComboBox.getSelectionModel().getSelectedIndex(), idopontField.getText(), tantargyComboBox.getValue(),
                                   tanarComboBox.getValue(), osztalyComboBox.getValue(), teremComboBox.getValue()));
        }

        window.hide();
        refreshTable(table);
    }

    private static void handleDeleteButtonClick(TableView<Ora> table, int index) {
        Components.showConfirmation("Biztos törlöd ezt az órát?", () -> {
            TimetableDB.delete(table.getItems().get(index));
            refreshTable(table);
        });
    }
}