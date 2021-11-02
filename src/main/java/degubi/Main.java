package degubi;

import degubi.gui.*;
import degubi.model.*;
import java.util.*;
import java.util.function.*;
import javafx.application.*;
import javafx.collections.*;
import javafx.geometry.*;
import javafx.scene.*;
import javafx.scene.chart.*;
import javafx.scene.control.*;
import javafx.scene.control.TabPane.*;
import javafx.scene.layout.*;
import javafx.stage.*;

public final class Main extends Application {
    private static final String teachersTabLabel = "Tanárok";
    private static final String qualificationsTabLabel = "Képzettségek";
    private static final String roomsTabLabel = "Termek";
    private static final String subjectsTabLabel = "Tantárgyak";
    private static final String classesTabLabel = "Osztályok";
    private static final String studentsTabLabel = "Diákok";
    private static final String fullTimetableTabLabel = "Órák";

    public static final ComboBox<String> searchFilterSelectorBox = new ComboBox<>();
    public static final Label loadingLabel = new Label("Töltés...");

    @Override
    public void start(Stage stage) {
        var teacherTimetable = Components.newTimetableGridPane();
        var classTimetable = Components.newTimetableGridPane();

        var classesComboBox = new ComboBox<Osztaly>();
        var teachersComboBox = new ComboBox<Tanar>();

        var teachersTab = newDBTableTab(teachersTabLabel, TanarGUIUtils.createTable(), Tanar.fieldMappings, TanarGUIUtils::refreshTable);
        var qualificationsTab = newDBTableTab(qualificationsTabLabel, KepzettsegGUIUtils.createTable(), Kepzettseg.fieldMappings, KepzettsegGUIUtils::refreshTable);
        var roomsTab = newDBTableTab(roomsTabLabel, TeremGUIUtils.createTable(), Terem.fieldMappings, TeremGUIUtils::refreshTable);
        var subjectsTab = newDBTableTab(subjectsTabLabel, TantargyGUIUtils.createTable(), Tantargy.fieldMappings, TantargyGUIUtils::refreshTable);
        var classesTab = newDBTableTab(classesTabLabel, OsztalyGUIUtils.createTable(), Osztaly.fieldMappings, OsztalyGUIUtils::refreshTable);
        var studentsTab = newDBTableTab(studentsTabLabel, DiakGUIUtils.createTable(), Diak.fieldMappings, DiakGUIUtils::refreshTable);
        var fullTimetableTab = newDBTableTab(fullTimetableTabLabel, OraGUIUtils.createTable(), Ora.fieldMappings, OraGUIUtils::refreshTable);

        var teacherTimetableTab = newTab("Tanáronkénti", teacherTimetable, k -> OraGUIUtils.handleTeacherTableSwitch(k, teachersComboBox));
        var classTimetableTab = newTab("Osztályonkénti", classTimetable, k -> OraGUIUtils.handleClassTableSwitch(k, classesComboBox));

        var subjectsFrequencySeries = new XYChart.Series<String, Number>();
        var subjectsFrequencyTab = newTab("Tantárgyak", StatGUIUtils.createBarChart("Tantárgy", "Gyakoriság", "Tantárgyak Gyakorisága", subjectsFrequencySeries),
                                          k -> StatGUIUtils.refreshTantargyFrequencyChart(subjectsFrequencySeries));

        var timetableTab = new Tab("Órarendek", newTabPane(teacherTimetableTab, classTimetableTab));
        var tablesTab = new Tab("Táblák", newTabPane(fullTimetableTab, teachersTab, studentsTab, subjectsTab, qualificationsTab, roomsTab, classesTab));
        var statsTab = new Tab("Statisztikák", newTabPane(subjectsFrequencyTab));

        var tablesTabBinding = tablesTab.selectedProperty();
        var mainTabPane = newTabPane(timetableTab, tablesTab, statsTab);

        var addButton = Components.newButton("Hozzáadás", e -> handleAddButtonClick(mainTabPane));
        addButton.visibleProperty().bind(tablesTabBinding);
        addButton.managedProperty().bind(tablesTabBinding);

        var searchTextField = new TextField();
        searchTextField.setPromptText("Keresés");
        searchTextField.setOnKeyReleased(e -> handleSearchFieldTyping(mainTabPane, searchTextField));
        searchTextField.visibleProperty().bind(tablesTabBinding);
        searchTextField.managedProperty().bind(tablesTabBinding);

        searchFilterSelectorBox.visibleProperty().bind(tablesTabBinding);
        searchFilterSelectorBox.managedProperty().bind(tablesTabBinding);

        var darkModeSwitchButton = new Button(null, Components.dayIcon);
        darkModeSwitchButton.setTooltip(new Tooltip("Világos/Sötét Mód"));
        darkModeSwitchButton.setOnAction(e -> handleDarkModeSwitch(stage, darkModeSwitchButton));

        var teachersComboBoxBinding = timetableTab.selectedProperty().and(teacherTimetableTab.selectedProperty());
        teachersComboBox.visibleProperty().bind(teachersComboBoxBinding);
        teachersComboBox.managedProperty().bind(teachersComboBoxBinding);
        teachersComboBox.valueProperty().addListener((obs, oldVal, newVal) -> {
            if(oldVal != null & newVal != null) {
                OraGUIUtils.refreshTeacherTable(teacherTimetable, teachersComboBox);
            }
        });

        var classesComboBoxBinding = timetableTab.selectedProperty().and(classTimetableTab.selectedProperty());
        classesComboBox.visibleProperty().bind(classesComboBoxBinding);
        classesComboBox.managedProperty().bind(classesComboBoxBinding);
        classesComboBox.valueProperty().addListener((obs, oldVal, newVal) -> {
            if(oldVal != null & newVal != null) {
                OraGUIUtils.refreshClassTable(classTimetable, classesComboBox);
            }
        });

        var bottomPanel = new BorderPane(null, null, new HBox(16, loadingLabel, darkModeSwitchButton), null, new HBox(16, teachersComboBox, classesComboBox, searchFilterSelectorBox, searchTextField, addButton));
        bottomPanel.setPadding(new Insets(5));

        stage.setTitle("Adatb");
        stage.setScene(new Scene(new BorderPane(null, mainTabPane, null, bottomPanel, null), 800, 600));
        stage.show();
    }


    private static void handleDarkModeSwitch(Stage stage, Button switchButton) {
        var isDay = switchButton.getGraphic() == Components.dayIcon;
        Components.windowTheme = isDay ? "-fx-base:black" : "";
        Components.textColor = isDay ? "-fx-fill: white;" : "-fx-fill: black;";

        stage.getScene().getRoot().setStyle(Components.windowTheme);
        switchButton.setGraphic(isDay ? Components.nightIcon : Components.dayIcon);
    }

    @SuppressWarnings("unchecked")
    private static void handleAddButtonClick(TabPane mainTabPane) {
        var selectedSecondaryTabPane = (TabPane) mainTabPane.getSelectionModel().getSelectedItem().getContent();
        var selectedSecondLevelTab = selectedSecondaryTabPane.getSelectionModel().getSelectedItem();
        var table = (TableView<?>) selectedSecondLevelTab.getContent();

        switch(selectedSecondLevelTab.getText()) {
            case teachersTabLabel:       TanarGUIUtils.showEditorDialog(null, (TableView<Tanar>) table);           break;
            case qualificationsTabLabel: KepzettsegGUIUtils.showEditorDialog(null, (TableView<Kepzettseg>) table); break;
            case roomsTabLabel:          TeremGUIUtils.showEditorDialog(null, (TableView<Terem>) table);           break;
            case classesTabLabel:        OsztalyGUIUtils.showEditorDialog(null, (TableView<Osztaly>) table);       break;
            case studentsTabLabel:       DiakGUIUtils.showEditorDialog(null, (TableView<Diak>) table);             break;
            case fullTimetableTabLabel:  OraGUIUtils.showEditorDialog(null, (TableView<Ora>) table);               break;
            case subjectsTabLabel:       TantargyGUIUtils.showEditorDialog(null, (TableView<Tantargy>) table);      break;
        }
    }

    private static void handleSearchFieldTyping(TabPane mainTabPane, TextField searchTextField) {
        loadingLabel.setVisible(true);

        var searchedText = searchTextField.getText();
        var searchedFieldName = searchFilterSelectorBox.getValue();
        var selectedSecondaryTabPane = (TabPane) mainTabPane.getSelectionModel().getSelectedItem().getContent();
        var selectedSecondLevelTab = selectedSecondaryTabPane.getSelectionModel().getSelectedItem();
        var activeTabLabel = selectedSecondLevelTab.getText();
        var selectedTable = (TableView<?>) selectedSecondLevelTab.getContent();

        getSearchFieldChangeHandler(activeTabLabel, searchedText.isBlank(), searchedFieldName, searchedText, selectedTable).run();
    }

    @SuppressWarnings("unchecked")
    private static Runnable getSearchFieldChangeHandler(String activeTabLabel, boolean isEmpty, String field, String value, TableView<?> table) {
        switch(activeTabLabel) {
            case teachersTabLabel:       return isEmpty ? () -> TanarGUIUtils.refreshTable((TableView<Tanar>) table)
                                                        : () -> TanarGUIUtils.refreshFilteredTable(field, value, (TableView<Tanar>) table);
            case roomsTabLabel:          return isEmpty ? () -> TeremGUIUtils.refreshTable((TableView<Terem>) table)
                                                        : () -> TeremGUIUtils.refreshFilteredTable(field, value, (TableView<Terem>) table);
            case qualificationsTabLabel: return isEmpty ? () -> KepzettsegGUIUtils.refreshTable((TableView<Kepzettseg>) table)
                                                        : () -> KepzettsegGUIUtils.refreshFilteredTable(field, value, (TableView<Kepzettseg>) table);
            case classesTabLabel:        return isEmpty ? () -> OsztalyGUIUtils.refreshTable((TableView<Osztaly>) table)
                                                        : () -> OsztalyGUIUtils.refreshFilteredTable(field, value, (TableView<Osztaly>) table);
            case studentsTabLabel:       return isEmpty ? () -> DiakGUIUtils.refreshTable((TableView<Diak>) table)
                                                        : () -> DiakGUIUtils.refreshFilteredTable(field, value, (TableView<Diak>) table);
            case fullTimetableTabLabel:  return isEmpty ? () -> OraGUIUtils.refreshTable((TableView<Ora>) table)
                                                        : () -> OraGUIUtils.refreshFilteredTable(field, value, (TableView<Ora>) table);

            case subjectsTabLabel:       return isEmpty ? () -> TantargyGUIUtils.refreshTable((TableView<Tantargy>) table)
                                                        : () -> TantargyGUIUtils.refreshFilteredTable(field, value, (TableView<Tantargy>) table);
            default: return () -> {};
        }
    }

    private static<T> Tab newDBTableTab(String title, TableView<T> content, Map<String, String> filters, Consumer<TableView<T>> onSelectedDataRefresher) {
        var tab = new Tab(title, content);
        var filterComboBoxes = FXCollections.observableArrayList(new TreeSet<>(filters.keySet()));

        tab.setOnSelectionChanged(e -> {
            if(tab.isSelected()) {
                Main.loadingLabel.setVisible(true);
                onSelectedDataRefresher.accept(content);

                Main.searchFilterSelectorBox.setItems(filterComboBoxes);
                Main.searchFilterSelectorBox.getSelectionModel().selectFirst();
            }
        });

        return tab;
    }

    private static<T extends Node> Tab newTab(String title, T content, Consumer<T> onSelectedDataRefresher) {
        var tab = new Tab(title, content);

        tab.setOnSelectionChanged(e -> {
            if(tab.isSelected()) {
                Main.loadingLabel.setVisible(true);
                onSelectedDataRefresher.accept(content);
            }
        });

        return tab;
    }

    private static TabPane newTabPane(Tab... tabs) {
        var pane = new TabPane(tabs);
        pane.setTabClosingPolicy(TabClosingPolicy.UNAVAILABLE);
        return pane;
    }

    public static void main(String[] args) { launch(); }
}