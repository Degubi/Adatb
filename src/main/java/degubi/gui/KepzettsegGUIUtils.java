package degubi.gui;

import degubi.*;
import degubi.db.*;
import degubi.model.*;
import javafx.scene.*;
import javafx.scene.control.*;
import javafx.stage.*;

public final class KepzettsegGUIUtils {
    private KepzettsegGUIUtils() {}

    public static void showEditorDialog(TableView<Kepzettseg> table) {
        var megnevezesField = new TextField();
        var okButtonBinding = Components.createEmptyFieldBinding(megnevezesField);

        var components = Components.newFormGridPane();
        var stage = new Stage();
        components.add(Components.newLabel("Megnevezés:"), 0, 0);
        components.add(megnevezesField, 1, 0);
        components.add(Components.newBottomButtonPanel("Hozzáad", stage, okButtonBinding, e -> handleAddButtonClick(megnevezesField, stage, table)), 0, 6, 2, 1);

        stage.setScene(new Scene(components, 400, 400));
        stage.setTitle("Új Képzettség");
        stage.getScene().getRoot().setStyle(Components.windowTheme);
        stage.show();
    }

    public static TableView<Kepzettseg> createTable() {
        var table = Components.<Kepzettseg>newTable(false, Components.newNumberColumn("Azonosító", Kepzettseg.fieldMappings),
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


    private static void handleAddButtonClick(TextField megnevezesField, Stage window, TableView<Kepzettseg> table) {
        KepzettsegDBUtils.add(megnevezesField.getText());
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