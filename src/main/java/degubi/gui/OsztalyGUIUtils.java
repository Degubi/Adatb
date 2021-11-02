package degubi.gui;

import degubi.*;
import degubi.db.*;
import degubi.model.*;
import javafx.scene.*;
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

        stage.setScene(new Scene(components, 400, 400));
        stage.setTitle("Új Osztály");
        stage.getScene().getRoot().setStyle(Components.windowTheme);
        stage.show();
    }

    public static TableView<Osztaly> createTable() {
        return Components.newTable(OsztalyGUIUtils::showEditorDialog, OsztalyGUIUtils::handleDeleteButtonClick,
                                   Components.newNumberColumn("Azonosító", Osztaly.fieldMappings),
                                   Components.newStringColumn("Megnevezés", Osztaly.fieldMappings));
    }

    public static void refreshTable(TableView<Osztaly> table) {
        OsztalyDBUtils.listAll()
                      .thenAccept(table::setItems)
                      .thenRun(() -> Main.loadingLabel.setVisible(false));
    }

    public static void refreshFilteredTable(String fieldName, String value, TableView<Osztaly> table) {
        OsztalyDBUtils.listFiltered(Osztaly.fieldMappings.get(fieldName), value)
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
            OsztalyDBUtils.delete(table.getItems().get(index));
            refreshTable(table);
        });
    }
}