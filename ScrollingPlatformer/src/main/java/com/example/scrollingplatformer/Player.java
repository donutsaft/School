package com.example.scrollingplatformer;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

class Player {
    private final Rectangle shape;     // Unsichtbare Hitbox
    private final ImageView sprite;    // Sichtbares Sprite
    private final GameWorld world;

    public Player(GameWorld world, double x, double y) {
        this.world = world;

        // Hitbox (Kollision & Physik)
        shape = new Rectangle(25, 50, Color.TRANSPARENT); //Eigentlich unsichtbar / .TRANSPARENT
        shape.setX(x);
        shape.setY(y);

        // Sprite (Anzeige)
        Image image = new Image(getClass().getResourceAsStream("/sprites/platzhalter50-25.png"));
        sprite = new ImageView(image);
        sprite.setFitWidth(40);
        sprite.setFitHeight(60);
        sprite.setX(x - 7); // leicht zentrieren über der Hitbox
        sprite.setY(y - 10);
    }

    // ===== GETTER =====
    public Rectangle getShape() { return shape; }
    public ImageView getSprite() { return sprite; }

    public double getX() { return shape.getX(); }
    public double getY() { return shape.getY(); }
    public double getWidth() { return shape.getWidth(); }
    public double getHeight() { return shape.getHeight(); }
    public double getBottom() { return shape.getY() + shape.getHeight(); }

    // ===== BEWEGUNG =====
    public void setY(double y) {
        shape.setY(y);
        sprite.setY(y - 10); // Sprite mitbewegen
    }

    public void setPosition(double x, double y) {
        shape.setX(x);
        shape.setY(y);
        sprite.setX(x - 7);
        sprite.setY(y - 10);
    }

    public void moveY(double dy) {
        setPosition(getX(), getY() + dy);
    }

    // ===== KOLLISION =====
    public boolean intersects(Platform plat) {
        return shape.getBoundsInParent().intersects(plat.getShape().getBoundsInParent());
    }

    public boolean intersectsCheckpoint(Checkpoint c) {
        return shape.getBoundsInParent().intersects(c.getShape().getBoundsInParent());
    }

    public boolean wouldIntersectWithTolerance(Platform plat, double dx, double dy, double tolerance) {
        Rectangle temp = new Rectangle(
                shape.getX() + dx + tolerance,
                shape.getY() + dy,
                shape.getWidth() - 2 * tolerance,
                shape.getHeight()
        );
        return temp.getBoundsInParent().intersects(plat.getShape().getBoundsInParent());
    }

    // ===== TOD =====
    public void die() {
        world.resetToCheckpoint();
    }
}
