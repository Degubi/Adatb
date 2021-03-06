package degubi.gui;

import degubi.mapping.*;
import degubi.model.*;
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

        Components.showEditorWindow("Tantárgy Szerkesztő", components, stage);
    }

    public static TableView<Tantargy> createTable() {
        return Components.newTable(TantargyGUIUtils::showEditorDialog, TantargyGUIUtils::handleDeleteButtonClick,
                                   Components.newNumberColumn("Azonosító", Tantargy.fieldMappings),
                                   Components.newStringColumn("Név", Tantargy.fieldMappings));
    }

    public static void refreshTable(TableView<Tantargy> table) {
        TimetableDB.listAll(Tantargy.class)
                  .thenAccept(table::setItems);
    }

    public static void refreshFilteredTable(String labelName, String value, TableView<Tantargy> table) {
        TimetableDB.listFiltered(Tantargy.fieldMappings.get(labelName), value, Tantargy.class)
                   .thenAccept(table::setItems);
    }


    private static void handleInteractButtonClick(TextField megnevezesField, Tantargy toEdit, Stage window, TableView<Tantargy> table) {
        if(toEdit != null) {
            TimetableDB.update(toEdit, new Tantargy(toEdit.azonosito, megnevezesField.getText()));
        }else {
            TimetableDB.add(new Tantargy(0, megnevezesField.getText()));
        }

        window.hide();
        refreshTable(table);
    }

    private static void handleDeleteButtonClick(TableView<Tantargy> table, int index) {
        Components.showConfirmation("Biztos törlöd ezt az képzettséget?", () -> {
            TimetableDB.delete(table.getItems().get(index));
            refreshTable(table);
        });
    }
}