package degubi.gui;

import degubi.*;
import degubi.db.*;
import degubi.model.*;
import javafx.scene.*;
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
        components.add(Components.newBottomButtonPanel(toEdit != null ? "Módosít" : "Hozzáad", stage, okButtonBinding,
                                                       e -> handleInteractButtonClick(megnevezesField, toEdit, stage, table)), 0, 6, 2, 1);

        stage.setScene(new Scene(components, 400, 400));
        stage.setTitle("Új Képzettség");
        stage.getScene().getRoot().setStyle(Components.windowTheme);
        stage.show();
    }

    public static TableView<Kepzettseg> createTable() {
        var table = Components.newTable(KepzettsegGUIUtils::showEditorDialog,
                                        Components.newNumberColumn("Azonosító", Kepzettseg.fieldMappings),
                                        Components.newStringColumn("Megnevezés", Kepzettseg.fieldMappings));

        table.getColumns().add(Components.newButtonColumn("Törlés", i -> handleDeleteButtonClick(table, i)));
        return table;
    }

    public static void refreshTable(TableView<Kepzettseg> table) {
        KepzettsegDBUtils.listAll()
                         .thenAccept(table::setItems)
                         .thenRun(() -> Main.loadingLabel.setVisible(false));
    }

    public static void refreshFilteredTable(String fieldName, String value, TableView<Kepzettseg> table) {
        KepzettsegDBUtils.listFiltered(Kepzettseg.fieldMappings.get(fieldName), value)
                         .thenAccept(table::setItems)
                         .thenRun(() -> Main.loadingLabel.setVisible(false));
    }


    private static void handleInteractButtonClick(TextField megnevezesField, Kepzettseg toEdit, Stage window, TableView<Kepzettseg> table) {
        if(toEdit != null) {
            KepzettsegDBUtils.update(toEdit, megnevezesField.getText());
        }else {
            KepzettsegDBUtils.add(megnevezesField.getText());
        }

        window.hide();
        refreshTable(table);
    }

    private static void handleDeleteButtonClick(TableView<Kepzettseg> table, int index) {
        Components.showConfirmation("Biztos törlöd ezt az képzettséget?", () -> {
            KepzettsegDBUtils.delete(table.getItems().get(index));
            refreshTable(table);
        });
    }
}