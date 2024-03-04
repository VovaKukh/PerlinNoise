package org.example;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

public class RandomNoiseArrowMap extends Application {

    @Override
    public void start(Stage stage) {
        final int width = 512;
        final int height = 512;
        Canvas canvas = new Canvas(width, height);
        GraphicsContext gc = canvas.getGraphicsContext2D();

        drawCircles(gc, width, height);

        StackPane root = new StackPane(canvas);
        Scene scene = new Scene(root);
        stage.setTitle("Perlin Noise Arrow Map");
        stage.setScene(scene);
        stage.show();
    }

    private void drawCircles(GraphicsContext gc, int width, int height) {
        int circleRadius = 3;
        int spacing = 40;
        int arrowLength = 20;

        gc.setFill(Color.BLACK);

        // Calculate the number of circles to fit in the width and height
        int numCirclesX = width / spacing + 1;
        int numCirclesY = height / spacing + 1;

        // Draw the circles and arrows
        for (int x = 0; x < numCirclesX; x++) {
            for (int y = 0; y < numCirclesY; y++) {
                int centerX = x * spacing + spacing / 2; // Center position of the circle in X
                int centerY = y * spacing + spacing / 2; // Center position of the circle in Y
                gc.fillOval(centerX - circleRadius, centerY - circleRadius, circleRadius * 2, circleRadius * 2);

                // Draw an arrow from each circle in a random direction
                drawArrow(gc, centerX, centerY, arrowLength);
            }
        }
    }

    private void drawArrow(GraphicsContext gc, int startX, int startY, int length) {
        double angle = Math.random() * 2 * Math.PI; // Random angle in radians

        // Calculate the end point of the arrow
        int endX = (int) (startX + length * Math.cos(angle));
        int endY = (int) (startY + length * Math.sin(angle));

        // Draw the line for the arrow
        gc.setStroke(Color.RED); // Set arrow color
        gc.setLineWidth(2); // Set arrow thickness
        gc.strokeLine(startX, startY, endX, endY);

        // Optionally, draw an arrowhead at the end
        drawArrowHead(gc, startX, startY, endX, endY);
    }

    private void drawArrowHead(GraphicsContext gc, int startX, int startY, int endX, int endY) {
        double phi = Math.toRadians(40);
        int arrowHeadLength = 5;

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