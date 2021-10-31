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
    public static final ComboBox<String> searchFilterSelectorBox = new ComboBox<>();
    public static final Label loadingLabel = new Label("Töltés...");

    @Override
    public void start(Stage stage) {
        var teachersTable = TanarokGUIUtils.createTable();
        var teachersTab = Components.newTab("Teachers", teachersTable, TanarokGUIUtils.filters, TanarokGUIUtils::refreshTanarokTable);

        var searchTextField = new TextField();
        var addButton = Components.newButton("Hozzáadás", e -> TanarokGUIUtils.showNewTanarDialog(teachersTable));
        var darkModeSwitchButton = new Button(null, Components.dayIcon);
        var tabPane = new TabPane(teachersTab);

        searchTextField.setPromptText("Keresés");
        searchTextField.setOnKeyReleased(e -> handleSearchFieldTyping(teachersTab, tabPane, searchTextField));
        darkModeSwitchButton.setTooltip(new Tooltip("Világos/Sötét Mód"));
        darkModeSwitchButton.setOnAction(e -> handleDarkModeSwitch(stage, darkModeSwitchButton));

        var bottomPanel = new BorderPane(null, null, new HBox(16, loadingLabel, darkModeSwitchButton), null, new HBox(16, searchFilterSelectorBox, searchTextField, addButton));
        bottomPanel.setPadding(new Insets(5));

        tabPane.setTabClosingPolicy(TabClosingPolicy.UNAVAILABLE);
        stage.setTitle("Adatb");
        stage.setScene(new Scene(new VBox(11, tabPane, bottomPanel), 800, 600));
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }


    private static void handleDarkModeSwitch(Stage stage, Button switchButton) {
        var isDay = switchButton.getGraphic() == Components.dayIcon;
        Components.windowTheme = isDay ? "-fx-base:black" : "";
        Components.textColor = isDay ? "-fx-fill: white;" : "-fx-fill: black;";

        stage.getScene().getRoot().setStyle(Components.windowTheme);
        switchButton.setGraphic(isDay ? Components.nightIcon : Components.dayIcon);
    }

    @SuppressWarnings("unchecked")
    private static void handleSearchFieldTyping(Tab teachersTab, TabPane tabPane, TextField searchTextField) {
        var searchedText = searchTextField.getText();
        var activeTab = tabPane.getSelectionModel().getSelectedItem();
        var selectedTable = (TableView<?>) tabPane.getSelectionModel().getSelectedItem().getContent();

        loadingLabel.setVisible(true);

        if(activeTab == teachersTab) {
            var teachersTable = (TableView<Tanar>) selectedTable;
            var tanarokSource = !searchedText.isBlank() ? TanarokDBUtils.listFiltered(TanarokGUIUtils.filters.get(searchFilterSelectorBox.getValue()), searchedText)
                                                        : TanarokDBUtils.listAll();

            tanarokSource.thenAccept(teachersTable::setItems)
                         .thenRun(() -> Main.loadingLabel.setVisible(false));
        }
    }
}