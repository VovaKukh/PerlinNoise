package org.example;

import javafx.application.Platform;
import javafx.geometry.Pos;
import javafx.scene.SnapshotParameters;
import javafx.scene.control.Button;
import javafx.application.Application;
import javafx.embed.swing.SwingFXUtils;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.WritableImage;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

import javax.imageio.ImageIO;
import java.io.File;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

public class PerlinNoiseMapVideo extends Application {

    private final double z = -2;
    private final double z_increase = 0.001;

    @Override
    public void start(Stage stage) {
        final int width = 720;
        final int height = 720;
        Canvas canvas = new Canvas(width, height);

        Button generateFramesButton = new Button("Generate Frames");
        generateFramesButton.setOnAction(e -> {
            new Thread(() -> {
                try {
                    generateAndSaveFrames(width, height, z, 3000);
                } catch (InterruptedException | ExecutionException ex) {
                    throw new RuntimeException(ex);
                }
            }).start(); // Generate X frames in a background thread
        });

        StackPane root = new StackPane();
        root.getChildren().addAll(canvas, generateFramesButton);
        StackPane.setAlignment(generateFramesButton, Pos.BOTTOM_CENTER); // Position the button

        Scene scene = new Scene(root, width, height + 50); // Adjust scene size to accommodate button
        stage.setTitle("Perlin Noise Map");
        stage.setScene(scene);
        stage.show();
    }

    private double adjustValues(double value) {
        // Normalize the value from [-1, 1] to [0, 1]
        return (value + 1) / 2;
    }

    private double quantizeValueTransitions(double value) {
        double normalizedValue = adjustValues(value);

        normalizedValue  = (double) Math.round(normalizedValue * 15) / 15;

        return normalizedValue;
    }

    private double sharpenValueTransitions(double value) {
        double normalizedValue = adjustValues(value);

        if (normalizedValue > 0.47 && normalizedValue <= 0.5) {
            normalizedValue = 0.47;
        } else if (normalizedValue > 0.5 && normalizedValue <= 0.53) {
            normalizedValue = 0.53;
        }

        return normalizedValue;
    }

    private void generateAndSaveFrames(int width, int height, double initialZ, int frameCount) throws InterruptedException, ExecutionException {
        Canvas canvas = new Canvas(width, height);
        GraphicsContext gc = canvas.getGraphicsContext2D();

        for (int i = 0; i < frameCount; i++) {
            double z = initialZ + (i * z_increase);
            generateNoiseMap(gc, width, height, z);
            CompletableFuture<Void> saveFuture = saveCanvasToFile(canvas, "frame_" + i + ".png");
            saveFuture.get(); // Wait for the save operation to complete before continuing
            //System.out.println("Frame " + i + ", init z = " + initialZ + ", curr z = " + z);
        }
    }

    private void generateNoiseMap(GraphicsContext gc, int width, int height, double z) {
        double scale = 0.01;
        //gc.setImageSmoothing(false);

        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                double noiseValue = PerlinNoise.noise(x * scale, y * scale, z);
                // Normalize the noise value to the range [0, 1] for grayscale conversion
                double normalizedValue = quantizeValueTransitions(noiseValue);
                gc.setFill(Color.gray(normalizedValue));
                gc.fillRect(x, y, 1, 1);
            }
        }

        // Draw the current z value
        gc.setFill(Color.WHITE); // Text color
        gc.fillText("z: " + String.format("%.2f", z), width - 50, 20); // Adjust position as needed
    }

    private CompletableFuture<Void> saveCanvasToFile(Canvas canvas, String filename) {
        CompletableFuture<Void> future = new CompletableFuture<>();
        Platform.runLater(() -> {
            try {
                WritableImage writableImage = new WritableImage((int) canvas.getWidth(), (int) canvas.getHeight());
                SnapshotParameters params = new SnapshotParameters();
                params.setFill(Color.TRANSPARENT);
                canvas.snapshot(params, writableImage);
                File file = new File(filename);
                ImageIO.write(SwingFXUtils.fromFXImage(writableImage, null), "png", file);
                future.complete(null); // Mark the future as complete upon success
            } catch (Exception e) {
                future.completeExceptionally(e); // Propagate the exception if something goes wrong
            }
        });
        return future;
    }

    public static void main(String[] args) {
        launch(args);
    }
}
