package degubi.gui;

import degubi.mapping.*;
import degubi.model.stat.*;
import java.util.stream.*;
import javafx.application.*;
import javafx.collections.*;
import javafx.scene.chart.*;

public final class StatGUIUtils {
    private StatGUIUtils() {}

    @SuppressWarnings({ "boxing", "unchecked" })
    public static void refreshTantargyFrequencyChart(XYChart.Series<String, Number> series) {
        TimetableDB.getTantargyFrequencyMap()
                   .thenApply(k -> k.stream().map(m -> new XYChart.Data<>(m.nev, m.count)).toArray(XYChart.Data[]::new))
                   .thenAccept(k -> Platform.runLater(() -> series.setData(FXCollections.observableArrayList(k))));
    }

    public static void refreshClassesPerDayChart(PieChart chart) {
        TimetableDB.getNaponkentiOrakMap()
                   .thenApply(k -> IntStream.range(0, 5)
                                            .mapToObj(dayIndex -> new PieChart.Data(TimetableDB.getNapFromIndex(dayIndex), getCountForDay(k, dayIndex)))
                                            .toArray(PieChart.Data[]::new))
                   .thenAccept(k -> Platform.runLater(() -> {
                       chart.setData(FXCollections.observableArrayList(k));
                       chart.layout();
                   }));
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

    public static PieChart createPieChart(String title) {
        var chart = new PieChart();
        chart.setTitle(title);
        chart.setAnimated(false);
        return chart;
    }


    private static long getCountForDay(ObservableList<NaponkentiOrakStat> data, int dayIndex) {
        return data.stream()
                   .filter(m -> m.napIndex == dayIndex)
                   .findFirst()
                   .orElse(new NaponkentiOrakStat(0, dayIndex))
                   .count;
    }
}