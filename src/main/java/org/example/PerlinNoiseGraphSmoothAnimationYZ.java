package org.example;

import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

public class PerlinNoiseGraphSmoothAnimationYZ extends Application {

    private double y = 0;
    private final double y_increment = 0.01;
    private final Label yLabel = new Label();
    private double z = 0;
    private final double z_increment = 0.01;
    private final Label zLabel = new Label();

    @Override
    public void start(Stage stage) {
        stage.setTitle("Perlin Noise Graph");

        final NumberAxis xAxis = new NumberAxis();
        final NumberAxis yAxis = new NumberAxis(-1.25, 1.25, 0.25); // Set the bounds and tick unit
        xAxis.setLabel("Time (Arbitrary Units)");
        yAxis.setLabel("Value");

        final LineChart<Number, Number> lineChart = new LineChart<>(xAxis, yAxis);
        lineChart.setTitle("Perlin Noise Visualization");

        XYChart.Series series = new XYChart.Series();
        series.setName("Perlin Noise");

        // Initial population of the series
        populateSeries(series, y, z);

        // Configure and position the y value label
        yLabel.setText(String.format("y = %.2f", y));
        yLabel.setStyle("-fx-font-size: 14px; -fx-text-fill: black; -fx-background-color: yellow; -fx-padding: 5px;");
        yLabel.setMinSize(Label.USE_PREF_SIZE, Label.USE_PREF_SIZE);
        StackPane.setAlignment(yLabel, Pos.TOP_RIGHT);
        StackPane.setMargin(yLabel, new Insets(10, 10, 0, 0));

        // Diagnostic y console output
        System.out.println("Initial y value set to label: " + yLabel.getText());
        System.out.println("Increment y value set to: " + y_increment);

        // Configure and position the z value label
        zLabel.setText(String.format("z = %.2f", z));
        zLabel.setStyle("-fx-font-size: 14px; -fx-text-fill: black; -fx-background-color: yellow; -fx-padding: 5px;");
        zLabel.setMinSize(Label.USE_PREF_SIZE, Label.USE_PREF_SIZE);
        StackPane.setAlignment(zLabel, Pos.TOP_RIGHT);
        StackPane.setMargin(zLabel, new Insets(30, 10, 0, 0));

        // Diagnostic z console output
        System.out.println("Initial z value set to label: " + zLabel.getText());
        System.out.println("Increment z value set to: " + z_increment);

        // Using StackPane to overlay the chart and other labels
        StackPane root = new StackPane();
        root.getChildren().addAll(lineChart, yLabel, zLabel);

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
                    y += y_increment; // Increment y-coordinate
                    z += z_increment; // Increment z-coordinate
                    series.getData().clear(); // Clear previous data
                    populateSeries(series, y, z); // Update to include y and z
                    yLabel.setText(String.format("y = %.2f", y)); // Update the label with the new y value
                    zLabel.setText(String.format("z = %.2f", z)); // Update the label with the new z value
                    lastUpdate = now;
                }
            }
        }.start();
    }

    private void populateSeries(XYChart.Series series, double y, double z) {
        double scale = 0.02; // Scale for noise input
        for (int i = 0; i < 500; i++) {
            double noiseValue = PerlinNoise.noise(i * scale, y, z);
            noiseValue = noiseValue * 2; // Adjusting to range -1 to 1
            series.getData().add(new XYChart.Data(i, noiseValue));
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}
