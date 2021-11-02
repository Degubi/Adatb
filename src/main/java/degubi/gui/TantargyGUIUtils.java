package degubi.gui;

import degubi.*;
import degubi.db.*;
import degubi.model.*;
import javafx.scene.*;
import javafx.scene.control.*;
import javafx.stage.*;

public final class TantargyGUIUtils {
    private TantargyGUIUtils() {}

    public static void showEditorDialog(Tantargy toEdit, TableView<Tantargy> table) {
        var nevField = new TextField();
        var okButtonBinding = Components.createEmptyFieldBinding(nevField);

        if(toEdit != null) {
            nevField.setText(toEdit.nev);
        }

        var components = Components.newFormGridPane();
        var stage = new Stage();
        components.add(Components.newLabel("Név:"), 0, 0);
        components.add(nevField, 1, 0);
        components.add(Components.newEditorButtonPanel(toEdit != null, stage, okButtonBinding,
                                                       e -> handleInteractButtonClick(nevField, toEdit, stage, table)), 0, 6, 2, 1);

        stage.setScene(new Scene(components, 400, 400));
        stage.setTitle("Új Képzettség");
        stage.getScene().getRoot().setStyle(Components.windowTheme);
        stage.show();
    }

    public static TableView<Tantargy> createTable() {
        return Components.newTable(TantargyGUIUtils::showEditorDialog, TantargyGUIUtils::handleDeleteButtonClick,
                                   Components.newNumberColumn("Azonosító", Tantargy.fieldMappings),
                                   Components.newStringColumn("Név", Tantargy.fieldMappings));
    }

    public static void refreshTable(TableView<Tantargy> table) {
        TantargyDBUtils.listAll()
                       .thenAccept(table::setItems)
                       .thenRun(() -> Main.loadingLabel.setVisible(false));
    }

    public static void refreshFilteredTable(String fieldName, String value, TableView<Tantargy> table) {
        TantargyDBUtils.listFiltered(Tantargy.fieldMappings.get(fieldName), value)
                       .thenAccept(table::setItems)
                       .thenRun(() -> Main.loadingLabel.setVisible(false));
    }


    private static void handleInteractButtonClick(TextField megnevezesField, Tantargy toEdit, Stage window, TableView<Tantargy> table) {
        if(toEdit != null) {
            TantargyDBUtils.update(toEdit, megnevezesField.getText());
        }else {
            TantargyDBUtils.add(megnevezesField.getText());
        }

        window.hide();
        refreshTable(table);
    }

    private static void handleDeleteButtonClick(TableView<Tantargy> table, int index) {
        Components.showConfirmation("Biztos törlöd ezt az képzettséget?", () -> {
            TantargyDBUtils.delete(table.getItems().get(index));
            refreshTable(table);
        });
    }
}