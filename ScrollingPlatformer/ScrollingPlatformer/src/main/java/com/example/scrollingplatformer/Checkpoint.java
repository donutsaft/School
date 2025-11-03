package com.example.scrollingplatformer;

import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

class Checkpoint {
    private final Rectangle shape;
    private boolean activated = false;

    public Checkpoint(double x, double y) {
        shape = new Rectangle(30, 60, Color.YELLOW);
        shape.setX(x);
        shape.setY(y);
    }

    public Rectangle getShape() { return shape; }

    public boolean isActivated() { return activated; }

    public void activate() {
        activated = true;
        shape.setFill(Color.LIMEGREEN); // Farbe ändern als visuelles Feedback
    }

    public void moveX(double dx) {
        shape.setX(shape.getX() + dx);
    }
}
