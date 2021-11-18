package degubi.gui;

import static java.lang.Integer.*;

import degubi.mapping.*;
import degubi.model.*;
import javafx.scene.control.*;
import javafx.stage.*;

public final class TeremGUIUtils {
    private TeremGUIUtils() {}

    public static void showEditorDialog(Terem toEdit, TableView<Terem> table) {
        var teremSzamField = Components.newNumberTextField();
        var epuletSzamField = Components.newNumberTextField();
        var ferohelyekField = Components.newNumberTextField();
        var vanEProjektorCheckBox = new CheckBox();
        var okButtonBinding = Components.createEmptyFieldBinding(teremSzamField)
                                        .or(Components.createEmptyFieldBinding(epuletSzamField))
                                        .or(Components.createEmptyFieldBinding(ferohelyekField));
        if(toEdit != null) {
            teremSzamField.setText(String.valueOf(toEdit.teremSzam));
            epuletSzamField.setText(String.valueOf(toEdit.epuletSzam));
            ferohelyekField.setText(String.valueOf(toEdit.ferohelyekSzama));
            vanEProjektorCheckBox.setSelected(toEdit.vanEProjektor);
        }

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
        components.add(Components.newEditorButtonPanel(toEdit != null, stage, okButtonBinding,
                                                       e -> handleInteractButtonClick(teremSzamField, epuletSzamField, ferohelyekField, vanEProjektorCheckBox, toEdit, stage, table)), 0, 6, 2, 1);

        Components.showEditorWindow("Terem Szerkesztő", components, stage);
    }

    public static TableView<Terem> createTable() {
        return Components.newTable(TeremGUIUtils::showEditorDialog, TeremGUIUtils::handleDeleteButtonClick,
                                   Components.newNumberColumn("Azonosító", Terem.fieldMappings),
                                   Components.newNumberColumn("Terem", Terem.fieldMappings),
                                   Components.newNumberColumn("Épület", Terem.fieldMappings),
                                   Components.newNumberColumn("Férőhelyek Száma", Terem.fieldMappings),
                                   Components.newBooleanColumn("Van-E Projektor", Terem::getVanEProjektor));
    }

    public static void refreshTable(TableView<Terem> table) {
        TimetableDB.listAll(Terem.class)
                   .thenAccept(table::setItems);
    }

    public static void refreshFilteredTable(String labelName, String value, TableView<Terem> table) {
        TimetableDB.listFiltered(Terem.fieldMappings.get(labelName), value, Terem.class)
                   .thenAccept(table::setItems);
    }


    private static void handleInteractButtonClick(TextField teremSzamField, TextField epuletSzamField, TextField ferohelyekField, CheckBox vanEProjektorCheckBox,
                                                  Terem toEdit, Stage window, TableView<Terem> table) {
        if(toEdit != null) {
            TimetableDB.update(toEdit, new Terem(toEdit.azonosito, parseInt(teremSzamField.getText()), parseInt(epuletSzamField.getText()), parseInt(ferohelyekField.getText()), vanEProjektorCheckBox.isSelected()));
        }else {
            TimetableDB.add(new Terem(0, parseInt(teremSzamField.getText()), parseInt(epuletSzamField.getText()), parseInt(ferohelyekField.getText()), vanEProjektorCheckBox.isSelected()));
        }

        window.hide();
        refreshTable(table);
    }

    private static void handleDeleteButtonClick(TableView<Terem> table, int index) {
        Components.showConfirmation("Biztos törlöd ezt az termet?", () -> {
            TimetableDB.delete(table.getItems().get(index));
            refreshTable(table);
        });
    }
}