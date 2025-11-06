package com.example.scrollingplatformer;

import javafx.scene.image.Image;
import javafx.scene.paint.ImagePattern;
import javafx.scene.shape.Rectangle;

class Platform {
    private final Rectangle shape;

    public Platform(double x, double y, double width, double height, String imagePath) {
        shape = new Rectangle(width, height);
        shape.setX(x);
        shape.setY(y);

        // Textur laden
        Image img = new Image(getClass().getResourceAsStream(imagePath));
        shape.setFill(new ImagePattern(img, 0, 0, img.getWidth(), img.getHeight(), false));
    }

    public Rectangle getShape() { return shape; }

    public void moveX(double dx) {
        shape.setX(shape.getX() + dx);

        // Textur soll nicht "mitrutschen" beim Verschieben:
        var pattern = (ImagePattern) shape.getFill();
        shape.setFill(new ImagePattern(pattern.getImage(), shape.getX(), shape.getY(),
                pattern.getImage().getWidth(), pattern.getImage().getHeight(), false));
    }

    public double getTop() { return shape.getY(); }
}