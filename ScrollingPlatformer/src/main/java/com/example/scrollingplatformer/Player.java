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
        shape = new Rectangle(32, 100, Color.TRANSPARENT); //Eigentlich unsichtbar / .TRANSPARENT
        shape.setX(x);
        shape.setY(y);

        // Sprite (Anzeige)
        Image image = new Image(getClass().getResourceAsStream("/sprites/player_placeholder.png"));
        /**
         * Skin "Galerie":
         * 1. player_placeholder.png // Halt Platzhalter
         * 2. platzhalter50-25.png // Halt Platzhalter
         * 3. platzhalter32-16.png // Halt Platzhalter
         * 4. player_gpt.png // Gibt noch paar Probleme mit
         * 5. tram.png // Eigentlich nicht dafür gedacht
         */
        sprite = new ImageView(image);
        sprite.setSmooth(false);
        sprite.setFitWidth(32*1.5);
        sprite.setFitHeight(64*1.5);
        sprite.setX(x + (shape.getWidth() - sprite.getFitWidth()) / 2);
        sprite.setY(y + (shape.getHeight() - sprite.getFitHeight()));
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
        sprite.setY(y + (shape.getHeight() - sprite.getFitHeight()));
    }


    public void setPosition(double x, double y) {
        shape.setX(x);
        shape.setY(y);

        // Sprite zentriert auf die Hitbox ausrichten
        sprite.setX(x + (shape.getWidth() - sprite.getFitWidth()) / 2);
        sprite.setY(y + (shape.getHeight() - sprite.getFitHeight()));
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
