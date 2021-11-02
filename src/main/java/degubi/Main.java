package degubi;

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
    private static final String classesTabLabel = "Osztályok";
    private static final String studentsTabLabel = "Diákok";
    private static final String fullTimetableTabLabel = "Teljes Órarend";

    public static final ComboBox<String> searchFilterSelectorBox = new ComboBox<>();
    public static final ComboBox<Tanar> teachersComboBox = new ComboBox<>();
    public static final ComboBox<Osztaly> classesComboBox = new ComboBox<>();
    public static final TextField searchTextField = new TextField();
    public static final Button addButton = new Button("Hozzáadás");
    public static final Label loadingLabel = new Label("Töltés...");

    @Override
    public void start(Stage stage) {
        var teacherTimetable = Components.newTimetableGridPane();
        var classTimetable = Components.newTimetableGridPane();

        var teachersTab = Components.newTab(teachersTabLabel, TanarGUIUtils.createTable(), Tanar.fieldMappings, TanarGUIUtils::refreshTable);
        var qualificationsTab = Components.newTab(qualificationsTabLabel, KepzettsegGUIUtils.createTable(), Kepzettseg.fieldMappings, KepzettsegGUIUtils::refreshTable);
        var roomsTab = Components.newTab(roomsTabLabel, TeremGUIUtils.createTable(), Terem.fieldMappings, TeremGUIUtils::refreshTable);
        var classesTab = Components.newTab(classesTabLabel, OsztalyGUIUtils.createTable(), Osztaly.fieldMappings, OsztalyGUIUtils::refreshTable);
        var studentsTab = Components.newTab(studentsTabLabel, DiakGUIUtils.createTable(), Diak.fieldMappings, DiakGUIUtils::refreshTable);
        var fullTimetableTab = Components.newTab(fullTimetableTabLabel, OraGUIUtils.createTable(), Ora.fieldMappings, OraGUIUtils::refreshTable);
        var teacherTimetableTab = Components.newTab("Tanári Órarend", teacherTimetable, OraGUIUtils::handleTeacherTableSwitch);
        var classTimetableTab = Components.newTab("Osztály Órarend", classTimetable, OraGUIUtils::handleClassTableSwitch);

        var darkModeSwitchButton = new Button(null, Components.dayIcon);
        var tabPane = new TabPane(teacherTimetableTab, classTimetableTab, fullTimetableTab, teachersTab, studentsTab, qualificationsTab, roomsTab, classesTab);

        addButton.setOnAction(e -> handleAddButtonClick(tabPane));
        searchTextField.setPromptText("Keresés");
        searchTextField.setOnKeyReleased(e -> handleSearchFieldTyping(tabPane, searchTextField));
        darkModeSwitchButton.setTooltip(new Tooltip("Világos/Sötét Mód"));
        darkModeSwitchButton.setOnAction(e -> handleDarkModeSwitch(stage, darkModeSwitchButton));

        teachersComboBox.valueProperty().addListener((obs, oldVal, newVal) -> {
            if(oldVal != null & newVal != null) {
                OraGUIUtils.refreshTeacherTable(teacherTimetable);
            }
        });
        classesComboBox.valueProperty().addListener((obs, oldVal, newVal) -> {
            if(oldVal != null & newVal != null) {
                OraGUIUtils.refreshClassTable(classTimetable);
            }
        });

        var bottomPanel = new BorderPane(null, null, new HBox(16, loadingLabel, darkModeSwitchButton), null, new HBox(16, teachersComboBox, classesComboBox, searchFilterSelectorBox, searchTextField, addButton));
        bottomPanel.setPadding(new Insets(5));

        tabPane.setTabClosingPolicy(TabClosingPolicy.UNAVAILABLE);
        stage.setTitle("Adatb");
        stage.setScene(new Scene(new BorderPane(null, tabPane, null, bottomPanel, null), 800, 600));
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
    private static void handleAddButtonClick(TabPane tabPane) {
        var table = (TableView<?>) tabPane.getSelectionModel().getSelectedItem().getContent();

        switch(tabPane.getSelectionModel().getSelectedItem().getText()) {
            case teachersTabLabel:       TanarGUIUtils.showEditorDialog(null, (TableView<Tanar>) table);           break;
            case qualificationsTabLabel: KepzettsegGUIUtils.showEditorDialog(null, (TableView<Kepzettseg>) table); break;
            case roomsTabLabel:          TeremGUIUtils.showEditorDialog(null, (TableView<Terem>) table);           break;
            case classesTabLabel:        OsztalyGUIUtils.showEditorDialog(null, (TableView<Osztaly>) table);       break;
            case studentsTabLabel:       DiakGUIUtils.showEditorDialog(null, (TableView<Diak>) table);             break;
            case fullTimetableTabLabel:  OraGUIUtils.showEditorDialog(null, (TableView<Ora>) table);               break;
        }
    }

    private static void handleSearchFieldTyping(TabPane tabPane, TextField searchTextField) {
        loadingLabel.setVisible(true);

        var searchedText = searchTextField.getText();
        var searchedFieldName = searchFilterSelectorBox.getValue();
        var activeTabLabel = tabPane.getSelectionModel().getSelectedItem().getText();
        var selectedTable = (TableView<?>) tabPane.getSelectionModel().getSelectedItem().getContent();

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
            default: return () -> {};
        }
    }

    public static void main(String[] args) { launch(); }
}