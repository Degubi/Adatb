package degubi.gui;

import degubi.*;
import degubi.db.*;
import degubi.model.*;
import javafx.scene.*;
import javafx.scene.control.*;
import javafx.stage.*;

public final class TanarGUIUtils {
    private TanarGUIUtils() {}

    public static void showEditorDialog(TableView<Tanar> table) {
        var stage = new Stage();

        var szemelyiField = new TextField();
        var nevField = new TextField();
        var kepzettsegComboBox = new ComboBox<>(KepzettsegDBUtils.listAll().join());

        var okButtonBinding = Components.createEmptyFieldBinding(szemelyiField)
                                        .or(Components.createEmptyFieldBinding(nevField))
                                        .or(Components.createEmptyComboBoxBinding(kepzettsegComboBox));

        var components = Components.newFormGridPane();
        components.add(Components.newLabel("Személyi:"), 0, 0);
        components.add(szemelyiField, 1, 0);
        components.add(Components.newLabel("Név:"), 0, 1);
        components.add(nevField, 1, 1);
        components.add(Components.newLabel("Képzettség:"), 0, 2);
        components.add(kepzettsegComboBox, 1, 2);
        components.add(Components.newBottomButtonPanel("Hozzáad", stage, okButtonBinding, e -> handleAddButtonClick(szemelyiField, nevField, kepzettsegComboBox, stage, table )), 0, 6, 2, 1);

        stage.setScene(new Scene(components, 400, 400));
        stage.setTitle("Új Tanár");
        stage.getScene().getRoot().setStyle(Components.windowTheme);
        stage.show();
    }

    public static TableView<Tanar> createTable() {
        var table = Components.<Tanar>newTable(false, Components.newStringColumn("Személyi Szám", "szemelyiSzam"),
                                                      Components.newStringColumn("Név", "nev"),
                                                      Components.newStringColumn("Képzettség", "kepzettseg"));

        table.getColumns().add(Components.newButtonColumn("Törlés", i -> handleDeleteButtonClick(table, i)));
        return table;
    }

    public static void refreshTable(TableView<Tanar> table) {
        TanarDBUtils.listAll()
                      .thenAccept(table::setItems)
                      .thenRun(() -> Main.loadingLabel.setVisible(false));
    }


    private static void handleAddButtonClick(TextField szemelyiField, TextField nevField, ComboBox<Kepzettseg> kepzettsegComboBox, Stage window, TableView<Tanar> table) {
        TanarDBUtils.add(szemelyiField.getText(), nevField.getText(), kepzettsegComboBox.getValue().azonosito);
        window.hide();
        refreshTable(table);
    }

    private static void handleDeleteButtonClick(TableView<Tanar> table, int index) {
        Components.showConfirmation("Biztos törlöd ezt az tanárt?", () -> {
            TanarDBUtils.delete(table.getItems().get(index).szemelyiSzam);
            refreshTable(table);
        });
    }
}