package org.example;

import javafx.application.Application;
import javafx.embed.swing.SwingFXUtils;
import javafx.scene.Scene;
import javafx.scene.SnapshotParameters;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.WritableImage;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.scene.shape.StrokeLineCap;
import javafx.scene.shape.StrokeLineJoin;
import javafx.stage.Stage;

import javax.imageio.ImageIO;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;

public class PerlinNoiseArrowMapFF extends Application {

    private final int dotSpacing = 1;
    private final int lineSpacing = 5;
    private final double noiseScale = 0.007;
    List<Line> lines = new ArrayList<>();

    @Override
    public void start(Stage stage) {
        final int width = 1080;
        final int height = 1080;
        Canvas canvas = new Canvas(width, height);
        GraphicsContext gc = canvas.getGraphicsContext2D();

        // Create dots based on Perlin noise
        Dot[][] dots = createDots(width, height, 0.2);

        // Average the directions of each dot with its neighbors
        //averageDirections(dots);
        //medianDirections(dots);
        //randomNeighborDirection(dots);
        //randomNeighborDirectionWithVariation(dots);
        randomAverageNeighborDirection(dots);

        // Draw the dots and their arrows
        //drawDotsAndArrows(gc, dots);

        // Draw a random dot and find the closest grid dot to it
        //Dot randomDot = drawRandomDot(gc, width, height);
        //Dot closestDot = findClosestDot(dots, randomDot);
        //randomDot.setAngle(closestDot.getAverageAngle());

        // Highlight the closest dot or take other actions as needed
        //gc.setStroke(Color.BLUE);
        //gc.strokeLine(randomDot.getX(), randomDot.getY(), closestDot.getX(), closestDot.getY());

        // Draw a random starting dot, find the closest grid dot, and repeat the line drawing process
        for (int i = 0; i < 30000; i++) {
            Dot randomStartDot = createRandomDot(width, height);
            //drawDot(gc, randomStartDot);
            randomStartDot.setAngle(findClosestDot(dots, randomStartDot).getAngle()); // Set its direction
            drawLineAndRepeat(gc, dots, randomStartDot, 30); // Draw line and repeat the process X times
        }

        StackPane root = new StackPane(canvas);
        Scene scene = new Scene(root);
        stage.setTitle("Perlin Noise Arrow Map");
        stage.setScene(scene);
        stage.show();
    }

    private void drawLineAndRepeat(GraphicsContext gc, Dot[][] dots, Dot startDot, int iterations) {
        Dot currentDot = startDot;
        for (int i = 0; i < iterations; i++) {
            // Calculate the end point of the line
            double endX = currentDot.getX() + lineSpacing * Math.cos(currentDot.getAngle());
            double endY = currentDot.getY() + lineSpacing * Math.sin(currentDot.getAngle());

            // Check if the end point is outside the canvas
            if (endX < 0 || endX >= gc.getCanvas().getWidth() || endY < 0 || endY >= gc.getCanvas().getHeight()) {
                break; // Stop if the next dot would be outside the canvas
            }

            // Set the line color and opacity
            gc.setStroke(Color.rgb(0, 0, 0, 0.08));

            // Set the line thickness
            gc.setLineWidth(1);

            gc.setLineCap(StrokeLineCap.ROUND);   // Set the line cap style to
            gc.setLineJoin(StrokeLineJoin.ROUND); // Set the line join style to round

            // Draw the line
            gc.strokeLine(currentDot.getX(), currentDot.getY(), endX, endY);

            // Place a new dot at the end of the line
            Dot newDot = new Dot(endX, endY, 0); // Temporarily set angle to 0
            Dot closestDot = findClosestDot(dots, newDot); // Find the closest grid dot to this new dot
            newDot.setAngle(closestDot.getAverageAngle()); // Set the new dot's angle to the closest grid dot's angle

            // Prepare for the next iteration
            currentDot = newDot;
        }
    }

    private void drawLineAndRepeatSVG(GraphicsContext gc, Dot[][] dots, Dot startDot, int iterations) {
        Dot currentDot = startDot;
        for (int i = 0; i < iterations; i++) {
            double endX = currentDot.getX() + lineSpacing * Math.cos(currentDot.getAngle());
            double endY = currentDot.getY() + lineSpacing * Math.sin(currentDot.getAngle());

            if (endX < 0 || endX >= gc.getCanvas().getWidth() || endY < 0 || endY >= gc.getCanvas().getHeight()) {
                break;
            }

            // Instead of drawing, add line data to the list
            lines.add(new Line(currentDot.getX(), currentDot.getY(), endX, endY));

            // Set the angle for the new dot based on the closest grid dot
            currentDot = new Dot(endX, endY, findClosestDot(dots, new Dot(endX, endY, 0)).getAverageAngle());
        }
    }


    private Dot createRandomDot(int width, int height) {
        // Generate random position within the canvas
        int x = (int) (Math.random() * width);
        int y = (int) (Math.random() * height);

        // Return a new Dot instance representing this random dot (angle not important here)
        return new Dot(x, y, 0); // Initial angle set to 0
    }

    private void drawDot(GraphicsContext gc, Dot dot) {
        gc.setFill(Color.GREEN); // Set fill color for the dot
        gc.fillOval(dot.getX() - 2, dot.getY() - 2, 4, 4); // Draw the dot
    }

    private Dot findClosestDot(Dot[][] dots, Dot randomDot) {
        // Calculate the approximate grid position
        int gridX = (int) (randomDot.getX() / dotSpacing);
        int gridY = (int) (randomDot.getY() / dotSpacing);

        // Ensure the calculated indices are within the array bounds
        gridX = Math.max(0, Math.min(gridX, dots.length - 1));
        gridY = Math.max(0, Math.min(gridY, dots[0].length - 1));

        Dot closestDot = dots[gridX][gridY]; // Start with the approximate position
        double minDistance = calculateDistance(randomDot, closestDot);

        // Check immediate neighbors to ensure we find the closest dot
        int[] dx = {-1, 0, 1};
        int[] dy = {-1, 0, 1};
        for (int j : dx) {
            for (int k : dy) {
                int neighborX = gridX + j;
                int neighborY = gridY + k;

                // Check bounds
                if (neighborX >= 0 && neighborX < dots.length && neighborY >= 0 && neighborY < dots[0].length) {
                    Dot neighborDot = dots[neighborX][neighborY];
                    double distance = calculateDistance(randomDot, neighborDot);
                    if (distance < minDistance) {
                        minDistance = distance;
                        closestDot = neighborDot;
                    }
                }
            }
        }

        return closestDot;
    }

    private double calculateDistance(Dot dot1, Dot dot2) {
        return Math.sqrt(Math.pow(dot1.getX() - dot2.getX(), 2) + Math.pow(dot1.getY() - dot2.getY(), 2));
    }

    private Dot[][] createDots(int width, int height, double z) {
        int numCirclesX = width / dotSpacing;
        int numCirclesY = height / dotSpacing;
        Dot[][] dots = new Dot[numCirclesX][numCirclesY];

        for (int x = 0; x < numCirclesX; x++) {
            for (int y = 0; y < numCirclesY; y++) {
                int centerX = x * dotSpacing + dotSpacing / 2;
                int centerY = y * dotSpacing + dotSpacing / 2;
                double noiseValue = PerlinNoise.noise(centerX * noiseScale, centerY * noiseScale, z);
                double angle = (noiseValue + 0.5) * 2 * Math.PI;
                dots[x][y] = new Dot(centerX, centerY, angle);
            }
        }

        return dots;
    }

    private void drawDotsAndArrows(GraphicsContext gc, Dot[][] dots) {
        int arrowLength = 13;

        gc.setFill(Color.BLACK);
        gc.setStroke(Color.RED);
        gc.setLineWidth(1);

        for (int x = 0; x < dots.length; x++) {
            for (int y = 0; y < dots[0].length; y++) {
                Dot dot = dots[x][y];
                gc.fillOval(dot.getX() - 2, dot.getY() - 2, 4, 4);
                drawArrow(gc, dot.getX(), dot.getY(), arrowLength, dot.getAngle());
            }
        }
    }

    private void averageDirections(Dot[][] dots) {
        int[] dx = {-1, 0, 1, -1, 1, -1, 0, 1};
        int[] dy = {-1, -1, -1, 0, 0, 1, 1, 1};

        for (int x = 0; x < dots.length; x++) {
            for (int y = 0; y < dots[0].length; y++) {
                double sumAngle = 0;
                int count = 0;

                for (int i = 0; i < dx.length; i++) {
                    int nx = x + dx[i];
                    int ny = y + dy[i];

                    // Check bounds
                    if (nx >= 0 && nx < dots.length && ny >= 0 && ny < dots[0].length) {
                        sumAngle += dots[nx][ny].getAngle();
                        count++;
                    }
                }

                if (count > 0) {
                    double avgAngle = sumAngle / count;
                    dots[x][y].setAverageAngle(avgAngle);
                }
            }
        }
    }

    private void medianDirections(Dot[][] dots) {
        int[] dx = {-1, 0, 1, -1, 1, -1, 0, 1};
        int[] dy = {-1, -1, -1, 0, 0, 1, 1, 1};

        for (int x = 0; x < dots.length; x++) {
            for (int y = 0; y < dots[0].length; y++) {
                List<Double> angles = new ArrayList<>();

                for (int i = 0; i < dx.length; i++) {
                    int nx = x + dx[i];
                    int ny = y + dy[i];

                    // Check bounds
                    if (nx >= 0 && nx < dots.length && ny >= 0 && ny < dots[0].length) {
                        angles.add(dots[nx][ny].getAngle());
                    }
                }

                // Calculate the median angle
                if (!angles.isEmpty()) {
                    Collections.sort(angles);
                    double medianAngle;
                    int middle = angles.size() / 2;
                    if (angles.size() % 2 == 1) {
                        medianAngle = angles.get(middle);
                    } else {
                        medianAngle = (angles.get(middle - 1) + angles.get(middle)) / 2.0;
                    }
                    dots[x][y].setAverageAngle(medianAngle);
                }
            }
        }
    }

    private void randomNeighborDirection(Dot[][] dots) {
        int[] dx = {-1, 0, 1, -1, 1, -1, 0, 1};
        int[] dy = {-1, -1, -1, 0, 0, 1, 1, 1};
        Random rand = new Random();

        for (int x = 0; x < dots.length; x++) {
            for (int y = 0; y < dots[0].length; y++) {
                List<Double> neighborAngles = new ArrayList<>();

                for (int i = 0; i < dx.length; i++) {
                    int nx = x + dx[i];
                    int ny = y + dy[i];

                    // Check bounds and ensure we're not adding the current dot's angle
                    if (nx >= 0 && nx < dots.length && ny >= 0 && ny < dots[0].length) {
                        neighborAngles.add(dots[nx][ny].getAngle());
                    }
                }

                // Randomly select one of the neighbor's angles if there are any neighbors
                if (!neighborAngles.isEmpty()) {
                    int randomIndex = rand.nextInt(neighborAngles.size());
                    double randomNeighborAngle = neighborAngles.get(randomIndex);
                    dots[x][y].setAverageAngle(randomNeighborAngle);
                }
            }
        }
    }

    private void randomNeighborDirectionWithVariation(Dot[][] dots) {
        int[] dx = {-1, 0, 1, -1, 1, -1, 0, 1};
        int[] dy = {-1, -1, -1, 0, 0, 1, 1, 1};
        Random rand = new Random();

        for (int x = 0; x < dots.length; x++) {
            for (int y = 0; y < dots[0].length; y++) {
                List<Double> neighborAngles = new ArrayList<>();

                for (int i = 0; i < dx.length; i++) {
                    int nx = x + dx[i];
                    int ny = y + dy[i];

                    // Check bounds and ensure we're not adding the current dot's angle
                    if (nx >= 0 && nx < dots.length && ny >= 0 && ny < dots[0].length) {
                        neighborAngles.add(dots[nx][ny].getAngle());
                    }
                }

                // Randomly select one of the neighbor's angles if there are any neighbors
                if (!neighborAngles.isEmpty()) {
                    int randomIndex = rand.nextInt(neighborAngles.size());
                    double randomNeighborAngle = neighborAngles.get(randomIndex);

                    // X% chance to adjust the angle
                    if (rand.nextDouble() < 0.1) {
                        // Randomly decide to add or subtract 30%
                        boolean addAngle = rand.nextBoolean();
                        double adjustmentFactor = 0.05 * randomNeighborAngle;
                        randomNeighborAngle += addAngle ? adjustmentFactor : -adjustmentFactor;
                    }

                    dots[x][y].setAverageAngle(randomNeighborAngle);
                }
            }
        }
    }

    private void randomAverageNeighborDirection(Dot[][] dots) {
        int[] dx = {-1, 0, 1, -1, 1, -1, 0, 1};
        int[] dy = {-1, -1, -1, 0, 0, 1, 1, 1};
        Random rand = new Random();

        for (int x = 0; x < dots.length; x++) {
            for (int y = 0; y < dots[0].length; y++) {
                double currentAngle = dots[x][y].getAngle();
                List<Double> neighborAngles = new ArrayList<>();

                for (int i = 0; i < dx.length; i++) {
                    int nx = x + dx[i];
                    int ny = y + dy[i];

                    // Check bounds and ensure we're not adding the current dot's angle
                    if (nx >= 0 && nx < dots.length && ny >= 0 && ny < dots[0].length) {
                        neighborAngles.add(dots[nx][ny].getAngle());
                    }
                }

                // Randomly select one of the neighbor's angles if there are any neighbors
                if (!neighborAngles.isEmpty()) {
                    int randomIndex = rand.nextInt(neighborAngles.size());
                    double randomNeighborAngle = neighborAngles.get(randomIndex);
                    dots[x][y].setAverageAngle((randomNeighborAngle + currentAngle) / 2);
                }
            }
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

    private void saveCanvasToFile(Canvas canvas, String baseFileName) {
        WritableImage writableImage = new WritableImage((int) canvas.getWidth(), (int) canvas.getHeight());
        canvas.snapshot(new SnapshotParameters(), writableImage);

        File file = new File(baseFileName + ".png");
        int counter = 1;
        while (file.exists()) { // If file exists, adjust the name
            file = new File(baseFileName + "_" + (counter++) + ".png");
        }

        try {
            ImageIO.write(SwingFXUtils.fromFXImage(writableImage, null), "png", file);
            System.out.println("Saved image to: " + file.getAbsolutePath());
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    private void saveAsSvg(List<Line> lines, String filePath) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePath))) {
            writer.write("<svg xmlns=\"http://www.w3.org/2000/svg\" version=\"1.1\" ");
            writer.write("width=\"1080\" height=\"1080\" viewBox=\"0 0 1080 1080\">\n");

            double strokeOpacity = 0.1;

            for (Line line : lines) {
                String lineSvg = String.format(Locale.US,
                        "<line x1=\"%f\" y1=\"%f\" x2=\"%f\" y2=\"%f\" stroke=\"black\" stroke-width=\"1\" stroke-opacity=\"%f\" />\n",
                        line.getStartX(), line.getStartY(), line.getEndX(), line.getEndY(), strokeOpacity);
                writer.write(lineSvg);
            }

            writer.write("</svg>");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}