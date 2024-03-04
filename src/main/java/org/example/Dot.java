package org.example;

import java.util.List;

public class Dot {
    private double x, y;
    private double angle;
    private double averageAngle;
    private List<Dot> neighbors;

    public Dot(double x, double y, double angle) {
        this.x = x;
        this.y = y;
        this.angle = angle;
    }

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }

    public double getAngle() {
        return angle;
    }

    public double getAverageAngle() {
        return averageAngle;
    }

    public List<Dot> getNeighbors() {
        return neighbors;
    }

    public void setNeighbors(List<Dot> neighbors) {
        this.neighbors = neighbors;
    }

    public void setAngle(double angle) {
        this.angle = angle;
    }

    public void setAverageAngle(double averageAngle) {
        this.averageAngle = averageAngle;
    }
}
