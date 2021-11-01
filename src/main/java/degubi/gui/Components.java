package degubi.gui;

import degubi.*;
import java.util.*;
import java.util.function.*;
import java.util.regex.*;
import javafx.application.*;
import javafx.beans.binding.*;
import javafx.beans.property.*;
import javafx.beans.value.*;
import javafx.collections.*;
import javafx.event.*;
import javafx.geometry.*;
import javafx.scene.control.*;
import javafx.scene.control.Alert.*;
import javafx.scene.control.cell.*;
import javafx.scene.image.*;
import javafx.scene.layout.*;
import javafx.scene.paint.*;
import javafx.scene.text.*;
import javafx.stage.*;
import javafx.util.converter.*;

public final class Components {
    private static final Border tableBorder = new Border(new BorderStroke(Color.GRAY, BorderStrokeStyle.SOLID, CornerRadii.EMPTY, BorderWidths.DEFAULT));
    private static final Pattern numberPattern = Pattern.compile("\\d*");
    private static final Pattern timePattern = Pattern.compile("^(0[0-9]|1[0-9]|2[0-3]):[0-5][0-9]$");

    public static final ImageView dayIcon = new ImageView(new Image(Main.class.getResource("/assets/day.png").toString(), 32, 32, true, true));
    public static final ImageView nightIcon = new ImageView(new Image(Main.class.getResource("/assets/night.png").toString(), 32, 32, true, true));

    public static String windowTheme = "";
    public static String textColor = "";

    private static final String errorComponentStyle = "-fx-focus-color: #d35244;" +
                                                      "-fx-faint-focus-color: #d3524422;" +
                                                      "-fx-highlight-fill: -fx-accent;" +
                                                      "-fx-highlight-text-fill: white;" +
                                                      "-fx-background-color:" +
                                                      "   -fx-focus-color," +
                                                      "   -fx-control-inner-background," +
                                                      "   -fx-faint-focus-color," +
                                                      "    linear-gradient(from 0px 0px to 0px 5px, derive(-fx-control-inner-background, -9%), -fx-control-inner-background);" +
                                                      "-fx-background-insets: -0.2, 1, -1.4, 3;" +
                                                      "-fx-background-radius: 3, 2, 4, 0;" +
                                                      "-fx-prompt-text-fill: transparent;";
    private Components() {}

    public static FlowPane newBottomButtonPanel(String leftButtonText, Stage stage, ObservableValue<Boolean> okButtonBinding, EventHandler<ActionEvent> okButtonEvent) {
        var okButton = new Button(leftButtonText);
        okButton.setDefaultButton(true);
        okButton.setOnAction(okButtonEvent);
        okButton.disableProperty().bind(okButtonBinding);

        var cancelButton = new Button("Vissza");
        cancelButton.setCancelButton(true);
        cancelButton.setOnAction(e -> stage.close());

        var buttonPane = new FlowPane(okButton, cancelButton);
        buttonPane.setOrientation(Orientation.HORIZONTAL);
        buttonPane.setHgap(15);
        buttonPane.setAlignment(Pos.CENTER);

        return buttonPane;
    }

    public static Button newButton(String text, EventHandler<ActionEvent> event) {
        var butt = new Button(text);
        butt.setOnAction(event);
        return butt;
    }

    @SafeVarargs
    public static<T> TableView<T> newTable(BiConsumer<T, TableView<T>> onEditRequested, TableColumn<T, ?>... columns) {
        var table = new TableView<T>();
        table.setEditable(false);
        table.setBorder(tableBorder);
        table.getColumns().addAll(columns);
        table.setOnMousePressed(e -> {
            if(e.isPrimaryButtonDown() && e.getClickCount() == 2) {
                var selection = table.getSelectionModel().getSelectedItem();

                if(selection != null) {
                    onEditRequested.accept(selection, table);
                }
            }
        });
        return table;
    }

    public static TextField newNumberTextField() {
        var field = new TextField();
        field.setTextFormatter(new TextFormatter<>(k -> numberPattern.matcher(k.getControlNewText()).matches() ? k : null));
        return field;
    }

    public static<T> TableColumn<T, Void> newButtonColumn(String buttonText, IntConsumer buttonIndexActionFunction) {
        var col = new TableColumn<T, Void>(buttonText);
        col.setStyle("-fx-alignment: CENTER;");
        col.setMaxWidth(150);
        col.setCellFactory(param -> new ButtonTableCell<>(buttonText, buttonIndexActionFunction));
        return col;
    }

    public static<T> TableColumn<T, Boolean> newBooleanColumn(String label, Function<T, SimpleBooleanProperty> valueSelector) {
        var col = new TableColumn<T, Boolean>(label);
        col.setMaxWidth(150);
        col.setCellFactory(CheckBoxTableCell.forTableColumn(col));
        col.setCellValueFactory(k -> valueSelector.apply(k.getValue()));
        return col;
    }

    public static<T> TableColumn<T, String> newStringColumn(String label, Map<String, String> fieldMappings) {
        var col = new TableColumn<T, String>(label);
        col.setStyle("-fx-alignment: CENTER;");
        col.setMaxWidth(150);
        col.setCellFactory(TextFieldTableCell.forTableColumn());
        col.setCellValueFactory(new PropertyValueFactory<>(fieldMappings.get(label)));
        return col;
    }

    public static<T> TableColumn<T, Number> newNumberColumn(String label, Map<String, String> fieldMappings) {
        var col = new TableColumn<T, Number>(label);
        col.setStyle("-fx-alignment: CENTER;");
        col.setMaxWidth(150);
        col.setCellFactory(TextFieldTableCell.forTableColumn(new NumberStringConverter()));
        col.setCellValueFactory(new PropertyValueFactory<>(fieldMappings.get(label)));
        return col;
    }

    public static GridPane newFormGridPane() {
        var gridPane = new GridPane();
        gridPane.setVgap(10);
        gridPane.setHgap(10);
        gridPane.setPadding(new Insets(10));
        return gridPane;
    }

    @SuppressWarnings("boxing")
    public static BooleanBinding createEmptyComboBoxBinding(ComboBox<?> box) {
        var binding = Bindings.createBooleanBinding(() -> box.getValue() == null || box.getValue().toString().isBlank(), box.valueProperty());
        box.styleProperty().bind(Bindings.when(binding).then(errorComponentStyle).otherwise(""));
        return binding;
    }

    @SuppressWarnings("boxing")
    public static BooleanBinding createFixedTextFieldLengthBinding(TextField field, int length) {
        var binding = Bindings.createBooleanBinding(() -> field.getText().length() != length, field.textProperty());
        field.styleProperty().bind(Bindings.when(binding).then(errorComponentStyle).otherwise(""));
        return binding;
    }

    @SuppressWarnings("boxing")
    public static BooleanBinding createEmptyFieldBinding(TextField field) {
        var binding = Bindings.createBooleanBinding(() -> field.getText().isBlank(), field.textProperty());
        field.styleProperty().bind(Bindings.when(binding).then(errorComponentStyle).otherwise(""));
        return binding;
    }

    @SuppressWarnings("boxing")
    public static BooleanBinding createTimeFieldBinding(TextField field) {
        var binding = Bindings.createBooleanBinding(() -> !timePattern.matcher(field.getText()).matches(), field.textProperty());
        field.styleProperty().bind(Bindings.when(binding).then(errorComponentStyle).otherwise(""));
        return binding;
    }

    public static Text newLabel(String text) {
        var label = new Text(text);
        label.setStyle(textColor);
        return label;
    }

    public static<T> Tab newTab(String title, TableView<T> content, Map<String, String> filters, Consumer<TableView<T>> onSelectedDataRefresher) {
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

    public static void showErrorDialog(String message) {
        Platform.runLater(() -> {
            var alert = new Alert(AlertType.ERROR);
            alert.setTitle("Hiba");
            alert.setHeaderText(null);
            alert.setContentText(message);
            alert.showAndWait();
        });
    }

    public static void showConfirmation(String message, Runnable onConfirm) {
        Platform.runLater(() -> {
            var alert = new Alert(AlertType.WARNING, message, ButtonType.OK, ButtonType.CANCEL);
            alert.setTitle("Figyelmeztetés");
            alert.setHeaderText(null);

            if(alert.showAndWait().get() == ButtonType.OK) {
                onConfirm.run();
            }
        });
    }

    private static final class ButtonTableCell<T> extends TableCell<T, Void> {

        private final Button button;

        public ButtonTableCell(String text, IntConsumer buttonIndexActionFunction) {
            this.button = newButton(text, e -> buttonIndexActionFunction.accept(getIndex()));
        }

        @Override
        public void updateItem(Void item, boolean empty) {
            super.updateItem(item, empty);
            if (empty) {
                setGraphic(null);
            } else {
                setGraphic(button);
            }
        }
    }
}