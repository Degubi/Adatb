package degubi.gui;

import degubi.*;
import degubi.db.*;
import degubi.model.*;
import javafx.collections.*;
import javafx.scene.*;
import javafx.scene.control.*;
import javafx.stage.*;

public final class OraGUIUtils {
    private OraGUIUtils() {}

    public static void showEditorDialog(TableView<Ora> table) {
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
        components.add(Components.newBottomButtonPanel("Hozzáad", stage, okButtonBinding, e -> handleAddButtonClick(napComboBox, idopontField, nevField, osztalyComboBox, teremComboBox, tanarComboBox, stage, table )), 0, 6, 2, 1);

        stage.setScene(new Scene(components, 400, 400));
        stage.setTitle("Új Óra");
        stage.getScene().getRoot().setStyle(Components.windowTheme);
        stage.show();
    }

    public static TableView<Ora> createTable() {
        var table = Components.<Ora>newTable(false, Components.newStringColumn("Nap", Ora.fieldMappings),
                                                    Components.newStringColumn("Időpont", Ora.fieldMappings),
                                                    Components.newStringColumn("Név", Ora.fieldMappings),
                                                    Components.newStringColumn("Osztály", Ora.fieldMappings),
                                                    Components.newStringColumn("Terem", Ora.fieldMappings),
                                                    Components.newStringColumn("Tanár", Ora.fieldMappings));

        table.getColumns().add(Components.newButtonColumn("Törlés", i -> handleDeleteButtonClick(table, i)));
        return table;
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


    private static void handleAddButtonClick(ComboBox<String> napComboBox, TextField idopontField, TextField nevField, ComboBox<Osztaly> osztalyComboBox,
                                             ComboBox<Terem> teremComboBox, ComboBox<Tanar> tanarComboBox, Stage window, TableView<Ora> table) {

        OraDBUtils.add(napComboBox.getSelectionModel().getSelectedIndex(), idopontField.getText(), nevField.getText(),
                       tanarComboBox.getValue().szemelyiSzam, osztalyComboBox.getValue().azonosito, teremComboBox.getValue().azonosito);
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