package org.example;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.stage.Stage;

public class PerlinNoiseGraph extends Application {

    @Override
    public void start(Stage stage) {
        stage.setTitle("Perlin Noise Graph");

        // Defining the axes
        final NumberAxis xAxis = new NumberAxis();
        final NumberAxis yAxis = new NumberAxis();
        xAxis.setLabel("Time (Arbitrary Units)");
        yAxis.setLabel("Value");

        // Creating the line chart
        final LineChart<Number, Number> lineChart = new LineChart<>(xAxis, yAxis);
        lineChart.setTitle("Perlin Noise Visualization");

        // Defining a series to hold the Perlin noise data
        XYChart.Series series = new XYChart.Series();
        series.setName("Perlin Noise");

        // Populate the series with Perlin noise data
        double scale = 0.1; // Scale for noise input
        for (int i = 0; i < 100; i++) {
            double noiseValue = PerlinNoise.noise(i * scale, 0, 0);
            series.getData().add(new XYChart.Data(i, noiseValue));
        }

        Scene scene = new Scene(lineChart, 800, 600);
        lineChart.getData().add(series);

        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
