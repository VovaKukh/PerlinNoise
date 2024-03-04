package org.example;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

import java.util.ArrayList;
import java.util.List;

public class PerlinNoiseArrowMapF extends Application {

    @Override
    public void start(Stage stage) {
        final int width = 720;
        final int height = 720;
        Canvas canvas = new Canvas(width, height);
        GraphicsContext gc = canvas.getGraphicsContext2D();

        List<Dot> dots = createDots(gc, width, height, 0);
        drawDotsAndArrows(gc, dots);

        StackPane root = new StackPane(canvas);
        Scene scene = new Scene(root);
        stage.setTitle("Perlin Noise Arrow Map");
        stage.setScene(scene);
        stage.show();
    }

    private List<Dot> createDots(GraphicsContext gc, int width, int height, double z) {
        int spacing = 20;
        double scale = 0.003;
        List<Dot> dots = new ArrayList<>();

        int numCirclesX = width / spacing + 1;
        int numCirclesY = height / spacing + 1;

        for (int x = 0; x < numCirclesX; x++) {
            for (int y = 0; y < numCirclesY; y++) {
                int centerX = x * spacing + spacing / 2;
                int centerY = y * spacing + spacing / 2;
                double noiseValue = PerlinNoise.noise(centerX * scale, centerY * scale, z);
                double angle = (noiseValue + 0.5) * 2 * Math.PI;
                dots.add(new Dot(centerX, centerY, angle));
            }
        }

        return dots;
    }

    private void drawDotsAndArrows(GraphicsContext gc, List<Dot> dots) {
        int arrowLength = 13;
        int circleRadius = 2;

        gc.setFill(Color.BLACK);
        gc.setStroke(Color.RED);
        gc.setLineWidth(1);

        for (Dot dot : dots) {
            gc.fillOval(dot.getX() - circleRadius, dot.getY() - circleRadius, circleRadius * 2, circleRadius * 2);
            drawArrow(gc, dot.getX(), dot.getY(), arrowLength, dot.getAngle());
        }
    }

    private void drawArrow(GraphicsContext gc, double startX, double startY, int length, double angle) {
        // Calculate the end point of the arrow using the angle
        int endX = (int) (startX + length * Math.cos(angle));
        int endY = (int) (startY + length * Math.sin(angle));

        // Draw the line for the arrow
        gc.setStroke(Color.RED); // Set arrow color
        gc.setLineWidth(1); // Set arrow thickness
        gc.strokeLine(startX, startY, endX, endY);

        // Optionally, draw an arrowhead at the end
        drawArrowHead(gc, startX, startY, endX, endY);
    }

    private void drawArrowHead(GraphicsContext gc, double startX, double startY, int endX, int endY) {
        double phi = Math.toRadians(40);
        int arrowHeadLength = 4;

        double angle = Math.atan2(endY - startY, endX - startX);

        // Points for the arrowhead
        double x1 = endX - arrowHeadLength * Math.cos(angle - phi);
        double y1 = endY - arrowHeadLength * Math.sin(angle - phi);
        double x2 = endX - arrowHeadLength * Math.cos(angle + phi);
        double y2 = endY - arrowHeadLength * Math.sin(angle + phi);

        // Draw the arrowhead lines
        gc.strokeLine(endX, endY, x1, y1);
        gc.strokeLine(endX, endY, x2, y2);
    }

    public static void main(String[] args) {
        launch(args);
    }
}