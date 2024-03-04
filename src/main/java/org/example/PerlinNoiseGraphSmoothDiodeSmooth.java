package org.example;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.stage.Stage;

public class PerlinNoiseGraphSmoothDiodeSmooth extends Application {

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
        double scale = 0.01; // Scale for noise input
        double prevValue = 0; // Track the previous noise value
        double threshold = 0.015; // Example starting value
        double smoothingFactor = 0.5; // Example starting value

        for (int i = 0; i < 1000; i++) {
            double noiseValue = PerlinNoise.noise(i * scale, 0, 1) * 2; // Original noise value

            // Flip negative values to positive
            if (noiseValue < 0) noiseValue = -noiseValue;

            // Smooth transition if there's a significant change
            if ((Math.abs(noiseValue - prevValue) > threshold)) {
                noiseValue = interpolate(prevValue, noiseValue, smoothingFactor);
            }

            series.getData().add(new XYChart.Data(i, noiseValue));
            prevValue = noiseValue; // Update previous value
        }

        Scene scene = new Scene(lineChart, 800, 600);
        lineChart.getData().add(series);

        stage.setScene(scene);
        stage.show();
    }

    // Linear interpolation function
    private double interpolate(double start, double end, double factor) {
        return start + (end - start) * factor;
    }

    public static void main(String[] args) {
        launch(args);
    }
}
