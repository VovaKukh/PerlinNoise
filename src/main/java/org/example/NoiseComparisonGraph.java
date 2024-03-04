package org.example;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.stage.Stage;

import java.util.Random;

public class NoiseComparisonGraph extends Application {

    @Override
    public void start(Stage stage) {
        stage.setTitle("Noise Comparison Graph");

        // Defining the axes
        final NumberAxis xAxis = new NumberAxis();
        final NumberAxis yAxis = new NumberAxis();
        xAxis.setLabel("Time (Arbitrary Units)");
        yAxis.setLabel("Value");

        // Creating the line chart
        final LineChart<Number, Number> lineChart = new LineChart<>(xAxis, yAxis);
        lineChart.setTitle("Perlin Noise vs Random Noise");

        // Series for Perlin Noise
        XYChart.Series perlinSeries = new XYChart.Series();
        perlinSeries.setName("Perlin Noise");

        // Series for Random Noise
        XYChart.Series randomSeries = new XYChart.Series();
        randomSeries.setName("Random Noise");

        // Populate the Perlin noise series
        double scale = 0.1; // Scale for noise input
        for (int i = 0; i < 100; i++) {
            double perlinValue = PerlinNoise.noise(i * scale, 0, 0);
            perlinSeries.getData().add(new XYChart.Data(i, perlinValue));
        }

        // Populate the random noise series
        Random random = new Random();
        for (int i = 0; i < 100; i++) {
            double randomValue = random.nextDouble() * 2 - 1; // Generate values between -1 and 1
            randomSeries.getData().add(new XYChart.Data(i, randomValue));
        }

        Scene scene = new Scene(lineChart, 800, 600);
        lineChart.getData().addAll(perlinSeries, randomSeries);

        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}

