package org.example;

import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

public class PerlinNoiseMapAnimation extends Application {

    private double z = 0.0;
    private final double z_increase = 0.01;

    @Override
    public void start(Stage stage) {
        final int width = 720;
        final int height = 720;
        Canvas canvas = new Canvas(width, height);
        GraphicsContext gc = canvas.getGraphicsContext2D();

        StackPane root = new StackPane(canvas);
        Scene scene = new Scene(root);
        stage.setTitle("Perlin Noise Map");
        stage.setScene(scene);
        stage.show();

        new AnimationTimer() {
            private long lastUpdate = 0;

            @Override
            public void handle(long now) {
                if (lastUpdate == 0) {
                    lastUpdate = now;
                    generateNoiseMap(gc, width, height, z);
                } else if (now - lastUpdate >= 1_000_000_000L) { // 1_000_000_000L nanoseconds == 1 second
                    z += z_increase;
                    generateNoiseMap(gc, width, height, z);
                    lastUpdate = now;
                }
            }
        }.start();
    }

    private void generateNoiseMap(GraphicsContext gc, int width, int height, double z) {
        double scale = 0.01;
        //gc.setImageSmoothing(false);

        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                double noiseValue = PerlinNoise.noise(x * scale, y * scale, z);
                // Normalize the noise value to the range [0, 1] for grayscale conversion
                double normalizedValue = sharpenValueTransitions(noiseValue);
                gc.setFill(Color.gray(normalizedValue));
                gc.fillRect(x, y, 1, 1);
            }
        }
    }

    private double adjustValues(double value) {
        // Normalize the value from [-1, 1] to [0, 1]
        return (value + 1) / 2;
    }

    private double sharpenValueTransitions(double value) {
        double normalizedValue = adjustValues(value);

        // Apply the specified adjustments
        if (normalizedValue > 0.45 && normalizedValue <= 0.5) {
            normalizedValue = 0.45;
        } else if (normalizedValue > 0.5 && normalizedValue <= 0.55) {
            normalizedValue = 0.55;
        }

        return normalizedValue;
    }

    public static void main(String[] args) {
        launch(args);
    }
}
