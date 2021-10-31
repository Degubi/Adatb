package degubi.gui;

import degubi.*;
import degubi.db.*;
import degubi.model.*;
import java.util.*;
import javafx.scene.*;
import javafx.scene.control.*;
import javafx.stage.*;

public final class TanarokGUIUtils {
    public static final Map<String, String> filters = Map.of("Személyi Szám", "szemelyiSzam", "Név", "nev", "Képzettség", "kepzettseg");
    private TanarokGUIUtils() {}

    public static void showNewTanarDialog(TableView<Tanar> table) {
        var stage = new Stage();

        var szemelyiField = new TextField();
        var nevField = new TextField();
        var kepzettsegField = new TextField();

        var okButtonBinding = Components.createEmptyFieldBinding(szemelyiField)
                                        .or(Components.createEmptyFieldBinding(nevField))
                                        .or(Components.createEmptyFieldBinding(kepzettsegField));

        var alsoGombPanel = Components.newBottomButtonPanel("Hozzáad", stage, okButtonBinding, e -> handleAddButtonPress(szemelyiField, nevField, kepzettsegField, stage, table ));
        var components = Components.newFormGridPane();
        components.add(Components.newLabel("Személyi:"), 0, 0);
        components.add(szemelyiField, 1, 0);
        components.add(Components.newLabel("Név:"), 0, 1);
        components.add(nevField, 1, 1);
        components.add(Components.newLabel("Képzettség:"), 0, 2);
        components.add(kepzettsegField, 1, 2);
        components.add(alsoGombPanel, 0, 6, 2, 1);

        stage.setScene(new Scene(components, 400, 400));
        stage.setTitle("Új Tanár");
        stage.getScene().getRoot().setStyle(Components.windowTheme);
        stage.show();
    }

    public static TableView<Tanar> createTable() {
        var table = Components.<Tanar>newTable(false, Components.newStringColumn("Személyi Szám", "szemelyiSzam"),
                                                      Components.newStringColumn("Név", "nev"),
                                                      Components.newStringColumn("Képzettség", "kepzettseg"));

        table.getColumns().add(Components.newButtonColumn("Törlés", i -> handleDeleteButtonPress(table, i)));
        return table;
    }

    public static void refreshTanarokTable(TableView<Tanar> table) {
        TanarokDBUtils.listAll()
                      .thenAccept(table::setItems)
                      .thenRun(() -> Main.loadingLabel.setVisible(false));
    }


    private static void handleAddButtonPress(TextField szemelyiField, TextField nevField, TextField kepzettsegField, Stage window, TableView<Tanar> table) {
        TanarokDBUtils.add(szemelyiField.getText(), nevField.getText(), kepzettsegField.getText());
        window.hide();
        refreshTanarokTable(table);
    }

    private static void handleDeleteButtonPress(TableView<Tanar> table, int index) {
        Components.showConfirmation("Biztos törlöd ezt az tanárt?", () -> {
            TanarokDBUtils.delete(table.getItems().get(index).szemelyiSzam);
            refreshTanarokTable(table);
        });
    }
}