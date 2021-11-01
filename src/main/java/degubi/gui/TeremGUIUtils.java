package degubi.gui;

import degubi.*;
import degubi.db.*;
import degubi.model.*;
import javafx.scene.*;
import javafx.scene.control.*;
import javafx.stage.*;

public final class TeremGUIUtils {
    private TeremGUIUtils() {}

    public static void showEditorDialog(TableView<Terem> table) {
        var teremSzamField = Components.newNumberTextField();
        var epuletSzamField = Components.newNumberTextField();
        var ferohelyekField = Components.newNumberTextField();
        var vanEProjektorCheckBox = new CheckBox();
        var okButtonBinding = Components.createEmptyFieldBinding(teremSzamField)
                                        .or(Components.createEmptyFieldBinding(epuletSzamField))
                                        .or(Components.createEmptyFieldBinding(ferohelyekField));

        var components = Components.newFormGridPane();
        var stage = new Stage();
        components.add(Components.newLabel("Terem Szám:"), 0, 0);
        components.add(teremSzamField, 1, 0);
        components.add(Components.newLabel("Épület Szám:"), 0, 1);
        components.add(epuletSzamField, 1, 1);
        components.add(Components.newLabel("Férőhelyek Száma:"), 0, 2);
        components.add(ferohelyekField, 1, 2);
        components.add(Components.newLabel("Van-e Projektor:"), 0, 3);
        components.add(vanEProjektorCheckBox, 1, 3);
        components.add(Components.newBottomButtonPanel("Hozzáad", stage, okButtonBinding, e -> handleAddButtonClick(teremSzamField, epuletSzamField, ferohelyekField, vanEProjektorCheckBox, stage, table)), 0, 6, 2, 1);

        stage.setScene(new Scene(components, 400, 400));
        stage.setTitle("Új Terem");
        stage.getScene().getRoot().setStyle(Components.windowTheme);
        stage.show();
    }

    public static TableView<Terem> createTable() {
        var table = Components.newTable(false, Components.newNumberColumn("Azonosító", Terem.fieldMappings),
                                               Components.newNumberColumn("Terem", Terem.fieldMappings),
                                               Components.newNumberColumn("Épület", Terem.fieldMappings),
                                               Components.newNumberColumn("Férőhelyek Száma", Terem.fieldMappings),
                                               Components.newBooleanColumn("Van-E Projektor", Terem::getVanEProjektor));

        table.getColumns().add(Components.newButtonColumn("Törlés", i -> handleDeleteButtonClick(table, i)));
        return table;
    }

    public static void refreshTable(TableView<Terem> table) {
        TeremDBUtils.listAll()
                    .thenAccept(table::setItems)
                    .thenRun(() -> Main.loadingLabel.setVisible(false));
    }

    public static void refreshFilteredTable(String fieldName, String value, TableView<Terem> table) {
        TeremDBUtils.listFiltered(Terem.fieldMappings.get(fieldName), value)
                    .thenAccept(table::setItems)
                    .thenRun(() -> Main.loadingLabel.setVisible(false));
    }


    private static void handleAddButtonClick(TextField teremSzamField, TextField epuletSzamField, TextField ferohelyekField, CheckBox vanEProjektorCheckBox,
                                             Stage window, TableView<Terem> table) {

        TeremDBUtils.add(teremSzamField.getText(), epuletSzamField.getText(), ferohelyekField.getText(), vanEProjektorCheckBox.isSelected());
        window.hide();
        refreshTable(table);
    }

    private static void handleDeleteButtonClick(TableView<Terem> table, int index) {
        Components.showConfirmation("Biztos törlöd ezt az termet?", () -> {
            TeremDBUtils.delete(table.getItems().get(index));
            refreshTable(table);
        });
    }
}