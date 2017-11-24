package model;

import java.awt.Point;

public class Line {
    private Point P1;
    private Point P2;
    public Line(Point _P1, Point _P2) {
        P1 = _P1;
        P2 = _P2;
    }

    public double getAngleWith(Line otherLine) {
        double angle = Math.acos((otherLine.getWidth() * getWidth() + otherLine.getHeight() * getHeight()) / (getNorm() * otherLine.getNorm()));
        while(angle < 0) {
            angle += Math.PI * 2;
        }

        while(angle > Math.PI * 2) {
            angle -= Math.PI * 2;
        }

        return angle;
    }

    public int getHeight() {
        return P2.y - P1.y;
    }

    public int getNorm() {
        return (int)Math.sqrt(getHeight() * getHeight() + getWidth() * getWidth());
    }

    public int getWidth() {
        return P2.x - P1.x;
    }

    public void rotateAroundP1 (double angle) {
        P2.setLocation(
                P1.x + getWidth() * Math.cos(angle) - getHeight() * Math.sin(angle),
                P1.y + getWidth() * Math.sin(angle) + getHeight() * Math.cos(angle)
            );
    }

    public Point getP1() {
        return P1;
    }

    public Point getP2() {
        return P2;
    }
}
