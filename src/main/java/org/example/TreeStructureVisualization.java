package org.example;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class TreeStructureVisualization extends Application {
    private final int dotSpacing = 1;
    private final int lineSpacing = 1;
    private final double noiseScale = 0.004;
    private final int threshold = 2;
    private final int branchIterations = 1500;
    private final int lineIterations = 1000;

    List<Branch> branchesToDraw = new LinkedList<>();

    @Override
    public void start(Stage stage) {
        final int width = 720;
        final int height = 720;
        Canvas canvas = new Canvas(width, height);
        GraphicsContext gc = canvas.getGraphicsContext2D();

        // Create dots based on Perlin noise
        Dot[][] dots = createDots(width, height, 0.3);

        // Average the directions of each dot with its neighbors
        averageDirections(dots);

        List<Branch> branches = new ArrayList<>();
        for (int i = 0; i < branchIterations; i++) {
            Dot randomStartDot = createRandomDot(width, height);
            randomStartDot.setAngle(findClosestDot(dots, randomStartDot).getAngle());
            drawLineAndRepeat(gc, branches, dots, randomStartDot, lineIterations);
            System.out.println("Current branches size: " + branches.size());
        }

        // Draw branches
        for (Branch branch : branchesToDraw) {
            drawBranch(gc, branch);
        }

        stage.setTitle("Tree Structure Visualization");
        stage.setScene(new Scene(new StackPane(canvas), width, height));
        stage.show();
    }

    private void drawDot(GraphicsContext gc, Dot dot, boolean start, boolean special) {
        if (start) {
            gc.setFill(Color.GREEN);
            gc.fillOval(dot.getX() - 5, dot.getY() - 5, 10, 10);
        } else if (special) {
            gc.setFill(Color.AQUA);
            gc.fillOval(dot.getX() - 5, dot.getY() - 5, 10, 10);
        } else {
            gc.setFill(Color.LIGHTGREEN);
            gc.fillOval(dot.getX() - 4, dot.getY() - 4, 8, 8);
        }
    }

    private Dot createRandomDot(int width, int height) {
        // Generate random position within the canvas
        int x = (int) (Math.random() * width);
        int y = (int) (Math.random() * height);

        // Return a new Dot instance representing this random dot (angle not important here)
        return new Dot(x, y, 0);
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

    private void drawLineAndRepeat(GraphicsContext gc, List<Branch> branches, Dot[][] dots, Dot startDot, int iterations) {
        Dot currentDot = startDot;

        Branch mainParent = null;
        Branch prevBranch = null;

        for (int i = 0; i < iterations; i++) {
            double angle = currentDot.getAngle();
            double x_calc = currentDot.getX() + lineSpacing * Math.cos(angle);
            double y_calc = currentDot.getY() + lineSpacing * Math.sin(angle);

            // Stop if outside bounds
            if (x_calc <= -10 || y_calc <= -10) return;

            // Create new dots position
            Dot endDot = new Dot(x_calc, y_calc, angle);

            // Create new branch for this dot
            Branch newBranch = null;

            // Iterate through all branches to find the closest one to endDot
            if (mainParent != null) {
                newBranch = new Branch(mainParent.getStart(), endDot, currentDot, endDot, prevBranch);
                DotAndBranch result = findClosestOtherDot(branches, endDot, mainParent);
                Dot closestDot22 = result.getDot();
                if (closestDot22 != null && (endDot.distanceTo(closestDot22) < threshold)) {
                    //System.out.println("closest one");

                    newBranch = new Branch(result.getBranch().getStart(), result.getBranch().getEnd(), currentDot, closestDot22, prevBranch);
                    result.getBranch().addConnection();
                    newBranch.addConnection();

                    //drawDot(gc, newBranch.getEndCurrent(), false, true);
                    //drawDot(gc, newBranch.getStartCurrent(), true, false);
                    //drawDot(gc, newBranch.getEndCurrent(), true, false);

                    // Update branch children
                    prevBranch.addChild(newBranch);
                    mainParent.addChild(newBranch);

                    // Draw the new branch to the draw list
                    branchesToDraw.add(newBranch);
                    return;
                }
            } else {
                newBranch = new Branch(currentDot, endDot, currentDot, endDot, prevBranch);
            }

            // Draw new dot if we did not attach it to a different one
            //drawDot(gc, endDot, false, false);

            // Attach previous branch as a parent
            if (prevBranch != null) {
                prevBranch.addChild(newBranch);
                mainParent.addChild(newBranch);
            }
            else branches.add(newBranch);

            // Update angle based on the closest dot from the Perlin noise map if not directly connected
            Dot closestDot = findClosestDot(dots, endDot);
            endDot.setAngle(closestDot.getAverageAngle());

            // Draw the new branch to the draw list
            branchesToDraw.add(newBranch);

            // Prepare for the next iteration
            currentDot = endDot;
            prevBranch = newBranch;
            if (i == 0) mainParent = newBranch;
        }
    }

    private DotAndBranch findClosestOtherDot(List<Branch> branches, Dot endDot, Branch sourceBranch) {
        double minDistance = Double.MAX_VALUE;
        Dot closestDot = null;
        Branch closestBranch = null;

        for (Branch branch : branches) {
            if (branch.equals(sourceBranch) || sourceBranch.getChildren().contains(branch)) {
                continue;
            }

            double distanceToStart = endDot.distanceTo(branch.getStartCurrent());
            if (distanceToStart < minDistance) {
                minDistance = distanceToStart;
                closestDot = branch.getStartCurrent();
                closestBranch = branch;
            }

            double distanceToEnd = endDot.distanceTo(branch.getEndCurrent());
            if (distanceToEnd < minDistance) {
                minDistance = distanceToEnd;
                closestDot = branch.getEndCurrent();
                closestBranch = branch;
            }

            for (Branch child : branch.getChildren()) {
                double distanceToChildStart = endDot.distanceTo(child.getStartCurrent());
                if (distanceToChildStart < minDistance) {
                    minDistance = distanceToChildStart;
                    closestDot = child.getStartCurrent();
                    closestBranch = child;
                }

                double distanceToChildEnd = endDot.distanceTo(child.getEndCurrent());
                if (distanceToChildEnd < minDistance) {
                    minDistance = distanceToChildEnd;
                    closestDot = child.getEndCurrent();
                    closestBranch = child;
                }
            }
        }

        return new DotAndBranch(closestDot, closestBranch);
    }

    private Branch findClosestOtherBranch(List<Branch> branches, Dot endDot, Branch sourceBranch) {
        double minDistance = Double.MAX_VALUE;
        Branch closestBranch = null;

        System.out.println("There are branches: " + branches.size());
        for (Branch branch : branches) {
            // Skip the source branch and its children
            if (branch == sourceBranch || sourceBranch.getChildren().contains(branch)) {
                continue;
            }

            // Checking the distance to all branches, not just children of other branches
            double distance = endDot.distanceTo(branch.getStart());
            if (distance < minDistance) {
                minDistance = distance;
                closestBranch = branch;
            }

            // Checking distance to all children of the current branch
            for (Branch childBranch : branch.getChildren()) {
                distance = endDot.distanceTo(childBranch.getStart());
                if (distance < minDistance) {
                    minDistance = distance;
                    closestBranch = branch;
                }
            }
        }

        return closestBranch;
    }

    private void drawBranch(GraphicsContext gc, Branch branch) {
        gc.setLineWidth(1);
        if (branch.getConnections() > 0) {
            gc.setStroke(Color.rgb(128, 0, 0, 0.8));
        } else if (branch.getConnections() > 2) {
            gc.setStroke(Color.rgb(0, 128, 0, 0.8));
        } else gc.setStroke(Color.rgb(0, 0, 128, 0.2));
        gc.strokeLine(branch.getStartCurrent().getX(), branch.getStartCurrent().getY(), branch.getEndCurrent().getX(), branch.getEndCurrent().getY());
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

    public static void main(String[] args) {
        launch(args);
    }
}