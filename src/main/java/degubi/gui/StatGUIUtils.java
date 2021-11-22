package degubi.gui;

import degubi.mapping.*;
import degubi.model.stat.*;
import java.util.*;
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

    @SuppressWarnings({ "boxing", "unchecked", "cast" })
    public static void refreshEpicBubbleChart(BubbleChart<Number, Number> epicChart) {
        TimetableDB.getNaponkentiTermenkentiOrakSzamaMap()
                   .thenApply(k -> k.stream()
                                    .collect(Collectors.groupingBy(m -> m.napIndex,
                                             Collectors.mapping(m -> new XYChart.Data<>(calculateDayPos(m.napIndex), (Number) m.teremSzam, m.count), Collectors.toList()))))
                   .thenApply(k -> FXCollections.observableArrayList(
                           new XYChart.Series<>("Hétfő", FXCollections.observableArrayList(k.getOrDefault(0, List.of()))),
                           new XYChart.Series<>("Kedd", FXCollections.observableArrayList(k.getOrDefault(1, List.of()))),
                           new XYChart.Series<>("Szerda", FXCollections.observableArrayList(k.getOrDefault(2, List.of()))),
                           new XYChart.Series<>("Csütörtök", FXCollections.observableArrayList(k.getOrDefault(3, List.of()))),
                           new XYChart.Series<>("Péntek", FXCollections.observableArrayList(k.getOrDefault(4, List.of())))
                       )
                    )
                   .thenAccept(k -> Platform.runLater(() -> epicChart.setData(k)));
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

    public static BubbleChart<Number, Number> newBubiChart() {
        var xAxis = new NumberAxis(0, 50, 1);
        var yAxis = new NumberAxis();

        xAxis.setLabel("Nap");
        yAxis.setTickUnit(1);
        yAxis.setLabel("Teremszám");

        var chart = new BubbleChart<>(xAxis, yAxis);
        chart.setTitle("Terem-Nap-Óraszám");
        chart.setAnimated(false);

        return chart;
    }


    // Java pls
    @SuppressWarnings("boxing")
    private static Number calculateDayPos(int napIndex) {
        return napIndex * 10 + 5;
    }

    private static long getCountForDay(ObservableList<NaponkentiOrakStat> data, int dayIndex) {
        return data.stream()
                   .filter(m -> m.napIndex == dayIndex)
                   .findFirst()
                   .orElse(new NaponkentiOrakStat(0, dayIndex))
                   .count;
    }
}