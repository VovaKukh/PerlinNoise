package org.example;

import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

import java.util.ArrayList;

public class PerlinNoiseGraphSmoothAnimationX extends Application {

    private double xOffset = 0.0;
    private final double xOffsetIncrement = 1;
    private final double scale = 0.05;
    private final Label xLabel = new Label();
    private final int values = 100;
    private LineChart<Number, Number> lineChart;
    private XYChart.Series<Number, Number> series;
    private NumberAxis xAxis, yAxis;

    @Override
    public void start(Stage stage) {
        stage.setTitle("Perlin Noise Graph");

        xAxis = new NumberAxis(0, values, 10);
        yAxis = new NumberAxis(-1.25, 1.25, 0.25); // Set the bounds and tick unit
        xAxis.setLabel("Time (Arbitrary Units)");
        yAxis.setLabel("Value");

        lineChart = new LineChart<>(xAxis, yAxis);
        lineChart.setTitle("Perlin Noise Visualization");

        series = new XYChart.Series();
        series.setName("Perlin Noise");

        // Initial population of the series
        populateSeries(series);

        // Configure and position the y value label
        xLabel.setText(String.format("x = %.2f", xOffset));
        xLabel.setStyle("-fx-font-size: 14px; -fx-text-fill: black; -fx-background-color: yellow; -fx-padding: 5px;");
        xLabel.setMinSize(Label.USE_PREF_SIZE, Label.USE_PREF_SIZE);
        StackPane.setAlignment(xLabel, Pos.TOP_RIGHT);
        StackPane.setMargin(xLabel, new Insets(10, 10, 0, 0));

        // Diagnostic y console output
        System.out.println("Initial x value set to label: " + xLabel.getText());
        System.out.println("Increment x value set to: " + xOffset);

        // Using StackPane to overlay the chart and other labels
        StackPane root = new StackPane();
        root.getChildren().addAll(lineChart, xLabel);

        Scene scene = new Scene(root, 800, 600);
        lineChart.getData().add(series);

        stage.setScene(scene);
        stage.show();

        // Animation to update z-coordinate
        new AnimationTimer() {
            private long lastUpdate = 0;

            @Override
            public void handle(long now) {
                if (now - lastUpdate >= 1_000_000_000L) { // 1 second, in nanoseconds
                    updateChart(); // Call updateChart directly
                    xLabel.setText(String.format("x = %.2f", xOffset)); // Update the label with the new x value
                    lastUpdate = now;
                }
            }
        }.start();
    }

    private void populateSeries(XYChart.Series<Number, Number> series) {
        for (int i = 0; i < values; i++) {
            double x = i + xOffset; // Directly use xOffset and i for the x value
            double noiseValue = PerlinNoise.noise(x * scale, 0, 0); // Calculate noise
            noiseValue = noiseValue * 2; // Adjusting to range -1 to 1, if needed
            series.getData().add(new XYChart.Data<>(x, noiseValue));
            System.out.println("Adding value x = " + x + ", noiseValue = " + noiseValue);
        }
    }

    private void updateChart() {
        // Increment xOffset for the next cycle
        xOffset += xOffsetIncrement;

        // Add a new data point at the end based on the updated xOffset
        double newYValue = getNextYValue(xOffset + values);
        series.getData().add(new XYChart.Data<>(xOffset + values, newYValue));

        // Remove the oldest data point to maintain a sliding window
        if (series.getData().size() > values) {
            series.getData().remove(0);
        }

        // Update the x-axis range to "scroll" the chart
        xAxis.setLowerBound(xAxis.getLowerBound() + xOffsetIncrement);
        xAxis.setUpperBound(xAxis.getUpperBound() + xOffsetIncrement);
    }

    private double getNextYValue(double x) {
        double noiseValue = PerlinNoise.noise(x * scale, 0, 0); // Example
        return noiseValue * 2; // Adjust as necessary
    }

    public static void main(String[] args) {
        launch(args);
    }
}
