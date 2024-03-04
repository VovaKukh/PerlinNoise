package org.example;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.stage.Stage;

import java.util.Random;

public class RandomNoiseGraphSmooth extends Application {

    @Override
    public void start(Stage stage) {
        stage.setTitle("Random Noise Graph");

        // Defining the axes
        final NumberAxis xAxis = new NumberAxis();
        final NumberAxis yAxis = new NumberAxis();
        xAxis.setLabel("Time (Arbitrary Units)");
        yAxis.setLabel("Value");

        // Creating the line chart
        final LineChart<Number, Number> lineChart = new LineChart<>(xAxis, yAxis);
        lineChart.setTitle("Random Noise Visualization");

        // Defining a series to hold the Perlin noise data
        XYChart.Series series = new XYChart.Series();
        series.setName("Random Noise");

        // Initialize Random object for noise generation
        Random random = new Random();

        // Populate the series with random noise data
        for (int i = 0; i < 1000; i++) {
            double noiseValue = -1 + random.nextDouble() * 2; // Generates a value between -1 and 1
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
