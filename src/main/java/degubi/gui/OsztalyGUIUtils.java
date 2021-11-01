package degubi.gui;

import degubi.*;
import degubi.db.*;
import degubi.model.*;
import javafx.scene.*;
import javafx.scene.control.*;
import javafx.stage.*;

public final class OsztalyGUIUtils {
    private OsztalyGUIUtils() {}

    public static void showEditorDialog(TableView<Osztaly> table) {
        var megnevezesField = new TextField();
        var okButtonBinding = Components.createEmptyFieldBinding(megnevezesField);

        var components = Components.newFormGridPane();
        var stage = new Stage();
        components.add(Components.newLabel("Megnevezés:"), 0, 0);
        components.add(megnevezesField, 1, 0);
        components.add(Components.newBottomButtonPanel("Hozzáad", stage, okButtonBinding, e -> handleAddButtonClick(megnevezesField, stage, table)), 0, 6, 2, 1);

        stage.setScene(new Scene(components, 400, 400));
        stage.setTitle("Új Osztály");
        stage.getScene().getRoot().setStyle(Components.windowTheme);
        stage.show();
    }

    public static TableView<Osztaly> createTable() {
        return Components.newTable(false, Components.newNumberColumn("Azonosító", Osztaly.fieldMappings),
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


    private static void handleAddButtonClick(TextField megnevezesField, Stage window, TableView<Osztaly> table) {
        OsztalyDBUtils.add(megnevezesField.getText());
        window.hide();
        refreshTable(table);
    }
}