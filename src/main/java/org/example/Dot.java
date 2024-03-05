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

    public void setX(double x) {
        this.x = x;
    }

    public void setY(double y) {
        this.y = y;
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

    public double distanceTo(Dot other) {
        return Math.sqrt(Math.pow(this.x - other.x, 2) + Math.pow(this.y - other.y, 2));
    }
}
