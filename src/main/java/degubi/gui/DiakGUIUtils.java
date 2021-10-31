package degubi.gui;

import degubi.*;
import degubi.db.*;
import degubi.model.*;
import javafx.scene.*;
import javafx.scene.control.*;
import javafx.stage.*;

public final class DiakGUIUtils {
    private DiakGUIUtils() {}

    public static void showEditorDialog(TableView<Diak> table) {
        var neptunKodField = new TextField();
        var osztalyComboBox = new ComboBox<>(OsztalyDBUtils.listAll().join());
        var nevField = new TextField();

        var okButtonBinding = Components.createFixedTextFieldLengthBinding(neptunKodField, 6)
                                        .or(Components.createEmptyFieldBinding(nevField))
                                        .or(Components.createEmptyComboBoxBinding(osztalyComboBox));

        var components = Components.newFormGridPane();
        var stage = new Stage();
        components.add(Components.newLabel("Neptun Kód:"), 0, 0);
        components.add(neptunKodField, 1, 0);
        components.add(Components.newLabel("Osztály:"), 0, 1);
        components.add(osztalyComboBox, 1, 1);
        components.add(Components.newLabel("Név:"), 0, 2);
        components.add(nevField, 1, 2);
        components.add(Components.newBottomButtonPanel("Hozzáad", stage, okButtonBinding, e -> handleAddButtonClick(neptunKodField, osztalyComboBox, nevField, stage, table )), 0, 6, 2, 1);

        stage.setScene(new Scene(components, 400, 400));
        stage.setTitle("Új Tanár");
        stage.getScene().getRoot().setStyle(Components.windowTheme);
        stage.show();
    }

    public static TableView<Diak> createTable() {
        var table = Components.<Diak>newTable(false, Components.newStringColumn("Neptun Kód", "neptunKod"),
                                                     Components.newStringColumn("Név", "nev"),
                                                     Components.newStringColumn("Osztály", "osztalyMegnevezes"));

        table.getColumns().add(Components.newButtonColumn("Törlés", i -> handleDeleteButtonClick(table, i)));
        return table;
    }

    public static void refreshTable(TableView<Diak> table) {
        DiakDBUtils.listAll()
                   .thenAccept(table::setItems)
                   .thenRun(() -> Main.loadingLabel.setVisible(false));
    }


    private static void handleAddButtonClick(TextField neptunKodField, ComboBox<Osztaly> osztalyComboBox, TextField nevField, Stage window, TableView<Diak> table) {
        DiakDBUtils.add(neptunKodField.getText(), osztalyComboBox.getValue().azonosito, nevField.getText());
        window.hide();
        refreshTable(table);
    }

    private static void handleDeleteButtonClick(TableView<Diak> table, int index) {
        Components.showConfirmation("Biztos törlöd ezt az diákot?", () -> {
            DiakDBUtils.delete(table.getItems().get(index).neptunKod);
            refreshTable(table);
        });
    }
}