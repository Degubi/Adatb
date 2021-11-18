package degubi;

import degubi.gui.*;
import degubi.mapping.*;
import degubi.model.*;
import java.util.*;
import java.util.function.*;
import javafx.application.*;
import javafx.collections.*;
import javafx.event.*;
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

    private static final ComboBox<String> searchFilterSelectorBox = new ComboBox<>();

    @Override
    public void start(Stage stage) {
        var timetableTab = new Tab("Órarendek");
        var tablesTab = new Tab("Táblák");
        var statsTab = new Tab("Statisztikák");

        var teachersTab = newDBTableTab(teachersTabLabel, tablesTab, TanarGUIUtils.createTable(), Tanar.fieldMappings, TanarGUIUtils::refreshTable);
        var qualificationsTab = newDBTableTab(qualificationsTabLabel, tablesTab, KepzettsegGUIUtils.createTable(), Kepzettseg.fieldMappings, KepzettsegGUIUtils::refreshTable);
        var roomsTab = newDBTableTab(roomsTabLabel, tablesTab, TeremGUIUtils.createTable(), Terem.fieldMappings, TeremGUIUtils::refreshTable);
        var subjectsTab = newDBTableTab(subjectsTabLabel, tablesTab, TantargyGUIUtils.createTable(), Tantargy.fieldMappings, TantargyGUIUtils::refreshTable);
        var classesTab = newDBTableTab(classesTabLabel, tablesTab, OsztalyGUIUtils.createTable(), Osztaly.fieldMappings, OsztalyGUIUtils::refreshTable);
        var studentsTab = newDBTableTab(studentsTabLabel, tablesTab, DiakGUIUtils.createTable(), Diak.fieldMappings, DiakGUIUtils::refreshTable);
        var fullTimetableTab = newDBTableTab(fullTimetableTabLabel, tablesTab, OraGUIUtils.createTable(), Ora.fieldMappings, OraGUIUtils::refreshTable);

        var teacherTimetable = Components.newTimetableGridPane();
        var classTimetable = Components.newTimetableGridPane();
        var classesComboBox = new ComboBox<Osztaly>();
        var teachersComboBox = new ComboBox<Tanar>();

        var teacherTimetableTab = newTab("Tanáronkénti", timetableTab, teacherTimetable, k -> OraGUIUtils.handleTeacherTableSwitch(k, teachersComboBox));
        var classTimetableTab = newTab("Osztályonkénti", timetableTab, classTimetable, k -> OraGUIUtils.handleClassTableSwitch(k, classesComboBox));

        var subjectsFrequencySeries = new XYChart.Series<String, Number>();
        var subjectsFrequencyTab = newTab("Tantárgyak", statsTab, StatGUIUtils.createBarChart("Tantárgy", "Gyakoriság", "Tantárgyak Gyakorisága", subjectsFrequencySeries),
                                          k -> StatGUIUtils.refreshTantargyFrequencyChart(subjectsFrequencySeries));

        var classesPerDayChart = StatGUIUtils.createPieChart("Órák Naponkénti Eloszlása");
        var classesPerDayTab = newTab("Órák", statsTab, classesPerDayChart, k -> StatGUIUtils.refreshClassesPerDayChart(classesPerDayChart));

        timetableTab.setContent(newTabPane(teacherTimetableTab, classTimetableTab));
        timetableTab.setOnSelectionChanged(Main::handleTopLevelTabSelection);
        tablesTab.setContent(newTabPane(fullTimetableTab, teachersTab, studentsTab, subjectsTab, qualificationsTab, roomsTab, classesTab));
        tablesTab.setOnSelectionChanged(Main::handleTopLevelTabSelection);
        statsTab.setContent(newTabPane(subjectsFrequencyTab, classesPerDayTab));
        statsTab.setOnSelectionChanged(Main::handleTopLevelTabSelection);

        var tablesTabBinding = tablesTab.selectedProperty();
        var mainTabPane = newTabPane(timetableTab, tablesTab, statsTab);

        var addRecordButton = Components.newButton("Hozzáadás", e -> handleAddButtonClick(mainTabPane));
        addRecordButton.visibleProperty().bind(tablesTabBinding);
        addRecordButton.managedProperty().bind(tablesTabBinding);

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
            if(oldVal != null && newVal != null) {
                OraGUIUtils.refreshTeacherTable(teacherTimetable, newVal);
            }
        });

        var classesComboBoxBinding = timetableTab.selectedProperty().and(classTimetableTab.selectedProperty());
        classesComboBox.visibleProperty().bind(classesComboBoxBinding);
        classesComboBox.managedProperty().bind(classesComboBoxBinding);
        classesComboBox.valueProperty().addListener((obs, oldVal, newVal) -> {
            if(oldVal != null && newVal != null) {
                OraGUIUtils.refreshClassTable(classTimetable, newVal);
            }
        });

        var loadingLabel = new Label("Töltés...");
        loadingLabel.visibleProperty().bind(TimetableDB.loading);

        var bottomPanel = new BorderPane(null, null, new HBox(16, loadingLabel, darkModeSwitchButton), null, new HBox(16, teachersComboBox, classesComboBox, searchFilterSelectorBox, searchTextField, addRecordButton));
        bottomPanel.setPadding(new Insets(5));

        stage.setTitle("Órarend");
        stage.setScene(new Scene(new BorderPane(null, mainTabPane, null, bottomPanel, null), 800, 600));
        stage.getIcons().add(Components.logo);
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
            case subjectsTabLabel:       TantargyGUIUtils.showEditorDialog(null, (TableView<Tantargy>) table);     break;
        }
    }

    private static void handleTopLevelTabSelection(Event event) {
        var tab = (Tab) event.getSource();

        if(tab.isSelected()) {
            var childTabPane = (TabPane) tab.getContent();

            childTabPane.getSelectionModel().getSelectedItem().getOnSelectionChanged().handle(null);
        }
    }

    private static void handleSearchFieldTyping(TabPane mainTabPane, TextField searchTextField) {
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

    private static<T> Tab newDBTableTab(String title, Tab parentTab, TableView<T> content, Map<String, String> filters, Consumer<TableView<T>> onSelectedDataRefresher) {
        var tab = new Tab(title, content);
        var filterComboBoxes = FXCollections.observableArrayList(new TreeSet<>(filters.keySet()));

        tab.setOnSelectionChanged(e -> {
            if(parentTab.isSelected() && tab.isSelected()) {
                onSelectedDataRefresher.accept(content);

                searchFilterSelectorBox.setItems(filterComboBoxes);
                searchFilterSelectorBox.getSelectionModel().selectFirst();
            }
        });

        return tab;
    }

    private static<T extends Node> Tab newTab(String title, Tab parentTab, T content, Consumer<T> onSelectedDataRefresher) {
        var tab = new Tab(title, content);

        tab.setOnSelectionChanged(e -> {
            if(parentTab.isSelected() && tab.isSelected()) {
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