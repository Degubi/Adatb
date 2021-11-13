package degubi.gui;

import degubi.*;
import degubi.mapping.*;
import javafx.application.*;
import javafx.scene.chart.*;

public final class StatGUIUtils {
    private StatGUIUtils() {}

    @SuppressWarnings("boxing")
    public static void refreshTantargyFrequencyChart(XYChart.Series<String, Number> series) {
        series.getData().clear();

        DBUtils.getTantargyFrequencyMap()
               .thenAccept(m -> Platform.runLater(() -> m.forEach(k -> series.getData().add(new XYChart.Data<>(k.nev, k.count)))))
               .thenRun(() -> Main.loadingLabel.setVisible(false));
    }

    public static BarChart<String, Number> createBarChart(String xAxisLabel, String yAxisLabel, String chartLabel, XYChart.Series<String, Number> series) {
        var xAxis = new CategoryAxis();
        xAxis.setLabel(xAxisLabel);
        var yAxis = new NumberAxis();
        yAxis.setLabel(yAxisLabel);

        var chart = new BarChart<>(xAxis, yAxis);
        series.setName(chartLabel);
        chart.setAnimated(false);
        chart.getData().add(series);

        return chart;
    }
}