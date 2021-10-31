package degubi;

import degubi.db.*;
import degubi.gui.*;
import degubi.model.*;
import javafx.application.*;
import javafx.geometry.*;
import javafx.scene.*;
import javafx.scene.control.*;
import javafx.scene.control.TabPane.*;
import javafx.scene.layout.*;
import javafx.stage.*;

public final class Main extends Application {
    private static final String teachersTabLabel = "Tanárok";
    private static final String qualificationsTabLabel = "Képzettségek";
    private static final String roomsTabLabel = "Termek";

    public static final ComboBox<String> searchFilterSelectorBox = new ComboBox<>();
    public static final Label loadingLabel = new Label("Töltés...");

    @Override
    public void start(Stage stage) {
        var teachersTable = TanarGUIUtils.createTable();
        var qualificationsTable = KepzettsegGUIUtils.createTable();
        var roomsTable = TeremGUIUtils.createTable();
        var teachersTab = Components.newTab(teachersTabLabel, teachersTable, Tanar.fieldMappings, TanarGUIUtils::refreshTable);
        var qualificationsTab = Components.newTab(qualificationsTabLabel, qualificationsTable, Kepzettseg.fieldMappings, KepzettsegGUIUtils::refreshTable);
        var roomsTab = Components.newTab(roomsTabLabel, roomsTable, Terem.fieldMappings, TeremGUIUtils::refreshTable);

        var searchTextField = new TextField();
        var darkModeSwitchButton = new Button(null, Components.dayIcon);
        var tabPane = new TabPane(teachersTab, qualificationsTab, roomsTab);
        var addButton = Components.newButton("Hozzáadás", e -> handleAddButtonClick(tabPane, qualificationsTable, roomsTable, teachersTable));

        searchTextField.setPromptText("Keresés");
        searchTextField.setOnKeyReleased(e -> handleSearchFieldTyping(tabPane, searchTextField));
        darkModeSwitchButton.setTooltip(new Tooltip("Világos/Sötét Mód"));
        darkModeSwitchButton.setOnAction(e -> handleDarkModeSwitch(stage, darkModeSwitchButton));

        var bottomPanel = new BorderPane(null, null, new HBox(16, loadingLabel, darkModeSwitchButton), null, new HBox(16, searchFilterSelectorBox, searchTextField, addButton));
        bottomPanel.setPadding(new Insets(5));

        tabPane.setTabClosingPolicy(TabClosingPolicy.UNAVAILABLE);
        stage.setTitle("Adatb");
        stage.setScene(new Scene(new VBox(11, tabPane, bottomPanel), 800, 600));
        stage.show();
    }


    private static void handleDarkModeSwitch(Stage stage, Button switchButton) {
        var isDay = switchButton.getGraphic() == Components.dayIcon;
        Components.windowTheme = isDay ? "-fx-base:black" : "";
        Components.textColor = isDay ? "-fx-fill: white;" : "-fx-fill: black;";

        stage.getScene().getRoot().setStyle(Components.windowTheme);
        switchButton.setGraphic(isDay ? Components.nightIcon : Components.dayIcon);
    }

    private static void handleAddButtonClick(TabPane tabPane, TableView<Kepzettseg> qualificationsTable, TableView<Terem> roomsTable,
                                             TableView<Tanar> teachersTable) {

        var activeTabLabel = tabPane.getSelectionModel().getSelectedItem().getText();

        if(activeTabLabel.equals(teachersTabLabel)) {
            TanarGUIUtils.showEditorDialog(teachersTable);
        }else if(activeTabLabel.equals(qualificationsTabLabel)) {
            KepzettsegGUIUtils.showEditorDialog(qualificationsTable);
        }else if(activeTabLabel.equals(roomsTabLabel)) {
            TeremGUIUtils.showEditorDialog(roomsTable);
        }
    }

    @SuppressWarnings("unchecked")
    private static void handleSearchFieldTyping(TabPane tabPane, TextField searchTextField) {
        var searchedText = searchTextField.getText();
        var activeTabLabel = tabPane.getSelectionModel().getSelectedItem().getText();
        var selectedTable = (TableView<?>) tabPane.getSelectionModel().getSelectedItem().getContent();

        loadingLabel.setVisible(true);

        if(activeTabLabel.equals(teachersTabLabel)) {
            var teachersTable = (TableView<Tanar>) selectedTable;
            var tanarokSource = !searchedText.isBlank() ? TanarDBUtils.listFiltered(Tanar.fieldMappings.get(searchFilterSelectorBox.getValue()), searchedText)
                                                        : TanarDBUtils.listAll();

            tanarokSource.thenAccept(teachersTable::setItems)
                         .thenRun(() -> Main.loadingLabel.setVisible(false));
        }
    }

    public static void main(String[] args) { launch(); }
}