package degubi.gui;

import degubi.*;
import degubi.db.*;
import degubi.model.*;
import javafx.scene.control.*;
import javafx.stage.*;

public final class OsztalyGUIUtils {
    private OsztalyGUIUtils() {}

    public static void showEditorDialog(Osztaly toEdit, TableView<Osztaly> table) {
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

        Components.showEditorWindow("Új Osztály", components, stage);
    }

    public static TableView<Osztaly> createTable() {
        return Components.newTable(OsztalyGUIUtils::showEditorDialog, OsztalyGUIUtils::handleDeleteButtonClick,
                                   Components.newNumberColumn("Azonosító", Osztaly.fieldMappings),
                                   Components.newStringColumn("Megnevezés", Osztaly.fieldMappings));
    }

    public static void refreshTable(TableView<Osztaly> table) {
        DBUtils.listAll(Osztaly.class)
               .thenAccept(table::setItems)
               .thenRun(() -> Main.loadingLabel.setVisible(false));
    }

    public static void refreshFilteredTable(String labelName, String value, TableView<Osztaly> table) {
        DBUtils.listFiltered(Osztaly.fieldMappings.get(labelName), value, Osztaly.class)
               .thenAccept(table::setItems)
               .thenRun(() -> Main.loadingLabel.setVisible(false));
    }


    private static void handleInteractButtonClick(TextField megnevezesField, Osztaly toEdit, Stage window, TableView<Osztaly> table) {
        if(toEdit != null) {
            OsztalyDBUtils.update(toEdit, megnevezesField.getText());
        }else {
            OsztalyDBUtils.add(megnevezesField.getText());
        }

        window.hide();
        refreshTable(table);
    }

    private static void handleDeleteButtonClick(TableView<Osztaly> table, int index) {
        Components.showConfirmation("Biztos törlöd ezt az osztályt?", () -> {
            DBUtils.delete(table.getItems().get(index));
            refreshTable(table);
        });
    }
}