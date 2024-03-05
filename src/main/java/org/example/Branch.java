package org.example;

import java.util.ArrayList;
import java.util.List;

public class Branch {
    private Branch parent;
    private Dot startCurrent;
    private Dot endCurrent;
    private Dot start;
    private Dot end;
    private int connections = 0;
    private List<Branch> children = new ArrayList<>();

    public Branch(Dot start, Dot end, Dot startCurrent, Dot endCurrent, Branch parent) {
        this.start = start;
        this.end = end;
        this.startCurrent = startCurrent;
        this.endCurrent = endCurrent;
        this.parent = parent;
    }

    public Dot getEnd() {
        // If this branch has children, return the end Dot of the first child
        if (children != null && !children.isEmpty()) {
            addConnection();
            return children.get(0).getEnd();
        }
        return this.end;
    }

    public void addConnection() {
        this.connections++;
        /*if (this.parent != null) {
            System.out.println("Updating parent connection");
            this.parent.addConnection(); // Recursively update the parent
        }*/
    }

    public void addChild(Branch branch) {
        children.add(branch);
        //addConnection();
    }

    public Branch getParent() {
        return parent;
    }

    public Dot getStart() {
        return start;
    }

    public int getConnections() {
        return connections;
    }

    public List<Branch> getChildren() {
        return children;
    }

    public void setStart(Dot start) {
        this.start = start;
    }

    public void setEnd(Dot end) {
        this.end = end;
    }

    public Dot getStartCurrent() {
        return startCurrent;
    }

    public Dot getEndCurrent() {
        return endCurrent;
    }

    public void setConnections(int connections) {
        this.connections = connections;
    }

    public void setChildren(List<Branch> children) {
        this.children = children;
    }

    public double calculateThickness() {
        return 1.0 + 0.2 * this.connections;
    }

    public boolean isCloseEnough(Dot dot, double threshold) {
        // Check if the dot is close enough to the line segment from start to end of the branch
        double distance = distanceToLineSegment(dot, this.start, this.end);
        return distance < threshold;
    }

    public void setEndCurrent(Dot endCurrent) {
        this.endCurrent = endCurrent;
    }

    public void setStartCurrent(Dot startCurrent) {
        this.startCurrent = startCurrent;
    }

    private double distanceToLineSegment(Dot dot, Dot lineStart, Dot lineEnd) {
        // Calculate the distance from the dot to the line segment defined by lineStart and lineEnd
        double lengthSquared = calculateDistance(lineStart, lineEnd) * calculateDistance(lineStart, lineEnd);
        if (lengthSquared == 0) return calculateDistance(dot, lineStart); // Line segment is a point

        // Consider the line extending the segment, parameterized as lineStart + t (lineEnd - lineStart).
        // We find projection of point p onto the line.
        // It falls where t = [(p-lineStart) . (lineEnd-lineStart)] / |lineEnd-lineStart|^2
        double t = ((dot.getX() - lineStart.getX()) * (lineEnd.getX() - lineStart.getX()) +
                (dot.getY() - lineStart.getY()) * (lineEnd.getY() - lineStart.getY())) / lengthSquared;
        t = Math.max(0, Math.min(1, t)); // Clamp t to the range [0, 1]

        // Project to find the closest point on the segment
        double closestX = lineStart.getX() + t * (lineEnd.getX() - lineStart.getX());
        double closestY = lineStart.getY() + t * (lineEnd.getY() - lineStart.getY());

        // Calculate the distance to the segment
        return calculateDistance(new Dot(closestX, closestY, 0), dot);
    }

    private double calculateDistance(Dot dot1, Dot dot2) {
        return Math.sqrt(Math.pow(dot1.getX() - dot2.getX(), 2) + Math.pow(dot1.getY() - dot2.getY(), 2));
    }

    public void setParent(Branch parent) {
        this.parent = parent;
    }
}

