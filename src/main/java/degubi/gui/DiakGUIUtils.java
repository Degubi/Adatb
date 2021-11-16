package degubi.gui;

import degubi.*;
import degubi.mapping.*;
import degubi.model.*;
import javafx.scene.control.*;
import javafx.stage.*;

public final class DiakGUIUtils {
    private DiakGUIUtils() {}

    public static void showEditorDialog(Diak toEdit, TableView<Diak> table) {
        var neptunKodField = new TextField();
        var osztalyComboBox = new ComboBox<>(TimetableDB.listAllOrderedBy("megnevezes", Osztaly.class).join());
        var nevField = new TextField();

        var okButtonBinding = Components.createFixedTextFieldLengthBinding(neptunKodField, 6)
                                        .or(Components.createEmptyFieldBinding(nevField))
                                        .or(Components.createEmptyComboBoxBinding(osztalyComboBox));
        if(toEdit != null) {
            neptunKodField.setText(toEdit.neptunKod);
            osztalyComboBox.setValue(toEdit.osztaly);
            nevField.setText(toEdit.nev);

            neptunKodField.setEditable(false);
        }

        var components = Components.newFormGridPane();
        var stage = new Stage();
        components.add(Components.newLabel("Neptun Kód:"), 0, 0);
        components.add(neptunKodField, 1, 0);
        components.add(Components.newLabel("Osztály:"), 0, 1);
        components.add(osztalyComboBox, 1, 1);
        components.add(Components.newLabel("Név:"), 0, 2);
        components.add(nevField, 1, 2);
        components.add(Components.newEditorButtonPanel(toEdit != null, stage, okButtonBinding,
                                                       e -> handleInteractButtonClick(neptunKodField, osztalyComboBox, nevField, toEdit, stage, table )), 0, 6, 2, 1);

        Components.showEditorWindow("Diák Szerkesztő", components, stage);
    }

    public static TableView<Diak> createTable() {
        return Components.newTable(DiakGUIUtils::showEditorDialog, DiakGUIUtils::handleDeleteButtonClick,
                                   Components.newStringColumn("Neptun Kód", Diak.fieldMappings),
                                   Components.newStringColumn("Név", Diak.fieldMappings),
                                   Components.newStringColumn("Osztály", Diak.fieldMappings));
    }

    public static void refreshTable(TableView<Diak> table) {
        TimetableDB.listAll(Diak.class)
                   .thenAccept(table::setItems)
                   .thenRun(() -> Main.loadingLabel.setVisible(false));
    }

    public static void refreshFilteredTable(String labelName, String value, TableView<Diak> table) {
        TimetableDB.listFilteredDiak(Diak.fieldMappings.get(labelName), value)
                   .thenAccept(table::setItems)
                   .thenRun(() -> Main.loadingLabel.setVisible(false));
    }


    private static void handleInteractButtonClick(TextField neptunKodField, ComboBox<Osztaly> osztalyComboBox, TextField nevField,
                                                  Diak toEdit, Stage window, TableView<Diak> table) {
        if(toEdit != null) {
            TimetableDB.update(toEdit, new Diak(toEdit.neptunKod, osztalyComboBox.getValue(), nevField.getText()));
        }else{
            TimetableDB.add(new Diak(neptunKodField.getText(), osztalyComboBox.getValue(), nevField.getText()));
        }

        window.hide();
        refreshTable(table);
    }

    private static void handleDeleteButtonClick(TableView<Diak> table, int index) {
        Components.showConfirmation("Biztos törlöd ezt az diákot?", () -> {
            TimetableDB.delete(table.getItems().get(index));
            refreshTable(table);
        });
    }
}