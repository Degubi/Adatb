package degubi.gui;

import degubi.mapping.*;
import degubi.model.*;
import javafx.scene.control.*;
import javafx.stage.*;

public final class TanarGUIUtils {
    private TanarGUIUtils() {}

    public static void showEditorDialog(Tanar toEdit, TableView<Tanar> table) {
        var szemelyiField = new TextField();
        var nevField = new TextField();
        var kepzettsegComboBox = new ComboBox<>(TimetableDB.listAll(Kepzettseg.class).join());

        var okButtonBinding = Components.createEmptyFieldBinding(szemelyiField)
                                        .or(Components.createEmptyFieldBinding(nevField))
                                        .or(Components.createEmptyComboBoxBinding(kepzettsegComboBox));
        if(toEdit != null) {
            szemelyiField.setText(toEdit.szemelyiSzam);
            nevField.setText(toEdit.nev);
            kepzettsegComboBox.setValue(toEdit.kepzettseg);

            szemelyiField.setEditable(false);
        }

        var components = Components.newFormGridPane();
        var stage = new Stage();
        components.add(Components.newLabel("Személyi:"), 0, 0);
        components.add(szemelyiField, 1, 0);
        components.add(Components.newLabel("Név:"), 0, 1);
        components.add(nevField, 1, 1);
        components.add(Components.newLabel("Képzettség:"), 0, 2);
        components.add(kepzettsegComboBox, 1, 2);
        components.add(Components.newEditorButtonPanel(toEdit != null, stage, okButtonBinding,
                                                       e -> handleInteractButtonClick(szemelyiField, nevField, kepzettsegComboBox, toEdit, stage, table)), 0, 6, 2, 1);

        Components.showEditorWindow("Tanár Szerkesztő", components, stage);
    }

    public static TableView<Tanar> createTable() {
        return Components.newTable(TanarGUIUtils::showEditorDialog, TanarGUIUtils::handleDeleteButtonClick,
                                   Components.newStringColumn("Személyi Szám", Tanar.fieldMappings),
                                   Components.newStringColumn("Név", Tanar.fieldMappings),
                                   Components.newStringColumn("Képzettség", Tanar.fieldMappings));
    }

    public static void refreshTable(TableView<Tanar> table) {
        TimetableDB.listAll(Tanar.class)
                   .thenAccept(table::setItems);
    }

    public static void refreshFilteredTable(String labelName, String value, TableView<Tanar> table) {
        TimetableDB.listFilteredTanar(Tanar.fieldMappings.get(labelName), value)
                   .thenAccept(table::setItems);
    }


    private static void handleInteractButtonClick(TextField szemelyiField, TextField nevField, ComboBox<Kepzettseg> kepzettsegComboBox,
                                                  Tanar toEdit, Stage window, TableView<Tanar> table) {
        if(toEdit != null) {
            TimetableDB.update(toEdit, new Tanar(szemelyiField.getText(), nevField.getText(), kepzettsegComboBox.getValue()));
        }else {
            TimetableDB.add(new Tanar(szemelyiField.getText(), nevField.getText(), kepzettsegComboBox.getValue()));
        }

        window.hide();
        refreshTable(table);
    }

    private static void handleDeleteButtonClick(TableView<Tanar> table, int index) {
        Components.showConfirmation("Biztos törlöd ezt az tanárt?", () -> {
            TimetableDB.delete(table.getItems().get(index));
            refreshTable(table);
        });
    }
}