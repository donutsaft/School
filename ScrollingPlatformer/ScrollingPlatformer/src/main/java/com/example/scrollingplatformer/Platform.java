package com.example.scrollingplatformer;

import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

class Platform {
    private final Rectangle shape;

    public Platform(double x, double y, double width, double height, Color farbe) {
        shape = new Rectangle(width, height);
        shape.setFill(farbe);
        shape.setX(x);
        shape.setY(y);
    }

    public Rectangle getShape() {
        return shape;
    }

    public void moveX(double dx) {
        shape.setX(shape.getX() + dx);
    }

    public double getTop() {
        return shape.getY();
    }

}