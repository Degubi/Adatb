package degubi.gui;

import degubi.mapping.*;
import degubi.model.*;
import javafx.scene.control.*;
import javafx.stage.*;

public final class KepzettsegGUIUtils {
    private KepzettsegGUIUtils() {}

    public static void showEditorDialog(Kepzettseg toEdit, TableView<Kepzettseg> table) {
        var megnevezesField = new TextField();
        var okButtonBinding = Components.createEmptyFieldBinding(megnevezesField);

        if(toEdit != null) {
            megnevezesField.setText(toEdit.megnevezes);
        }

        var components = Components.newFormGridPane();
        var stage = new Stage();
        components.add(Components.newLabel("Megnevezés:"), 0, 0);
        components.add(megnevezesField, 1, 0);
        components.add(Components.newEditorButtonPanel(toEdit != null, stage, okButtonBinding,
                                                       e -> handleInteractButtonClick(megnevezesField, toEdit, stage, table)), 0, 6, 2, 1);

        Components.showEditorWindow("Képzettség Szerkesztő", components, stage);
    }

    public static TableView<Kepzettseg> createTable() {
        return Components.newTable(KepzettsegGUIUtils::showEditorDialog, KepzettsegGUIUtils::handleDeleteButtonClick,
                                   Components.newNumberColumn("Azonosító", Kepzettseg.fieldMappings),
                                   Components.newStringColumn("Megnevezés", Kepzettseg.fieldMappings));
    }

    public static void refreshTable(TableView<Kepzettseg> table) {
        TimetableDB.listAll(Kepzettseg.class)
                   .thenAccept(table::setItems);
    }

    public static void refreshFilteredTable(String labelName, String value, TableView<Kepzettseg> table) {
        TimetableDB.listFiltered(Kepzettseg.fieldMappings.get(labelName), value, Kepzettseg.class)
                   .thenAccept(table::setItems);
    }


    private static void handleInteractButtonClick(TextField megnevezesField, Kepzettseg toEdit, Stage window, TableView<Kepzettseg> table) {
        if(toEdit != null) {
            TimetableDB.update(toEdit, new Kepzettseg(toEdit.azonosito, megnevezesField.getText()));
        }else {
            TimetableDB.add(new Kepzettseg(0, megnevezesField.getText()));
        }

        window.hide();
        refreshTable(table);
    }

    private static void handleDeleteButtonClick(TableView<Kepzettseg> table, int index) {
        Components.showConfirmation("Biztos törlöd ezt az képzettséget?", () -> {
            TimetableDB.delete(table.getItems().get(index));
            refreshTable(table);
        });
    }
}