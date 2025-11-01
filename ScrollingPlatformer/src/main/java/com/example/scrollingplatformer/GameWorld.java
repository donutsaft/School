package com.example.scrollingplatformer;

import javafx.animation.AnimationTimer;
import javafx.scene.Scene;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;


class GameWorld {
    private final double WIDTH;
    private final double HEIGHT;

    private final double GRAVITY = 0.6;
    private final double JUMP_STRENGTH = -12;
    private final double MOVE_SPEED = 5;
    private final double DEATH_Y = 900;

    private double velocityY = 0;
    private boolean onGround = false;
    private double cameraOffsetX = 0;

    private final Set<KeyCode> pressedKeys = new HashSet<>();
    private final List<Platform> platforms = new ArrayList<>();
    private final List<Checkpoint> checkpoints = new ArrayList<>();

    private Player player;
    private Pane root;
    private Scene scene;
    private AnimationTimer loop;

    private double startX, startY; // Respawn Position
    private double checkpointCameraOffset = 0;

    public GameWorld(double width, double height) {
        this.WIDTH = width;
        this.HEIGHT = height;
    }

    public Scene createScene() {
        root = new Pane();
        scene = new Scene(root, WIDTH, HEIGHT, Color.LIGHTBLUE);

        // Spieler
        startX = WIDTH / 2 - 25;
        startY = HEIGHT - 225;
        player = new Player(this, startX, startY);
        root.getChildren().addAll(player.getSprite(), player.getShape());


        // Plattformen
        platforms.add(new Platform(0, HEIGHT - 100, 1000, 100, Color.GREY));
        platforms.add(new Platform(0, HEIGHT - 150, 1000, 50, Color.BROWN));
        platforms.add(new Platform(0, HEIGHT - 175, 1000, 25, Color.GREEN));
        platforms.add(new Platform(1100, HEIGHT - 200, 150, 40, Color.GREEN));
        platforms.add(new Platform(-10, HEIGHT - 500, 10, 500, Color.LIGHTBLUE));//Unsichtbare Barriere
        platforms.add(new Platform(1350, HEIGHT - 300, 150, 40, Color.GREEN));

        // Beispiel-Checkpoints
        checkpoints.add(new Checkpoint(1400, HEIGHT - 360));
        checkpoints.add(new Checkpoint(100, HEIGHT - 235));

        // Alles in Szene laden
        for (Platform p : platforms) root.getChildren().add(p.getShape());
        for (Checkpoint c : checkpoints) root.getChildren().add(c.getShape());

        // Tasteneingaben
        scene.setOnKeyPressed(e -> pressedKeys.add(e.getCode()));
        scene.setOnKeyReleased(e -> pressedKeys.remove(e.getCode()));

        return scene;
    }

    public void startGameLoop() {
        loop = new AnimationTimer() {
            @Override
            public void handle(long now) {
                update();
            }
        };
        loop.start();
    }

    private void update() {
        double dx = 0;

        // Spieler-Steuerung
        if (pressedKeys.contains(KeyCode.A)) dx = MOVE_SPEED;
        if (pressedKeys.contains(KeyCode.D)) dx = -MOVE_SPEED;

        // Springen
        if (pressedKeys.contains(KeyCode.SPACE) && onGround) {
            velocityY = JUMP_STRENGTH;
            onGround = false;
        }

        // Schwerkraft
        velocityY += GRAVITY;
        player.moveY(velocityY);
        onGround = false;

        // ------------------------------
        // Horizontale Bewegung & Kollision
        // ------------------------------
        boolean xBlocked = false;

        for (Platform plat : platforms) {
            boolean standingOn = player.getBottom() <= plat.getTop() + 1 &&
                    player.getBottom() >= plat.getTop() - 2;

            if (!standingOn && player.wouldIntersectWithTolerance(plat, -dx, 0, 2)) {
                xBlocked = true;
                break;
            }
        }

        if (!xBlocked) {
            for (Platform plat : platforms) plat.moveX(dx);
            for (Checkpoint c : checkpoints) c.moveX(dx); // Checkpoints bewegen sich mit
            cameraOffsetX += dx;
        }

        // ------------------------------
        // Vertikale Kollision
        // ------------------------------
        for (Platform plat : platforms) {
            if (player.intersects(plat)) {
                if (player.getBottom() - velocityY <= plat.getTop()) {
                    player.setY(plat.getTop() - player.getHeight());
                    velocityY = 0;
                    onGround = true;
                }
            }
        }

        // ------------------------------
        // Checkpoint-Aktivierung
        // ------------------------------

        for (Checkpoint c : checkpoints) {
            if (!c.isActivated() && player.intersectsCheckpoint(c)) {
                c.activate();

                // Fester Respawnpunkt direkt beim Checkpoint
                startX = c.getShape().getX() + c.getShape().getWidth() / 2 - player.getWidth() / 2;
                startY = c.getShape().getY();
                checkpointCameraOffset = cameraOffsetX;

                System.out.println("Checkpoint aktiviert -> Respawn bei X=" + startX + " | Y=" + startY);
            }
        }


        // --- SPIELER GEFALLEN ---
        if (player.getY() > DEATH_Y) {
            resetToCheckpoint();
        }
    }

    // ===============================
    // SPIELER UND KAMERA ZURÜCKSETZEN
    // ===============================
    public void resetToCheckpoint() {
        player.setPosition(startX, startY);
        velocityY = 0;

        // Korrektur: Welt auf gespeicherte Kameraposition zurücksetzen
        double offsetDiff = cameraOffsetX - checkpointCameraOffset;
        for (Platform plat : platforms) plat.moveX(-offsetDiff);
        for (Checkpoint c : checkpoints) c.moveX(-offsetDiff);

        cameraOffsetX = checkpointCameraOffset;
    }
}