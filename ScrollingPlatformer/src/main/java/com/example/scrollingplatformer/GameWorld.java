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
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

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

    private ImageView background;

    public GameWorld(double width, double height) {
        this.WIDTH = width;
        this.HEIGHT = height;
    }

    public Scene createScene() {
        root = new Pane();
        scene = new Scene(root, WIDTH, HEIGHT, Color.LIGHTBLUE);

        // ===============================
        // HINTERGRUND
        // ===============================
        Image bgImage = new Image(getClass().getResourceAsStream("/backgrounds/bg_placeholder2.png"));
        background = new ImageView(bgImage);
        background.setFitWidth(WIDTH);
        background.setFitHeight(HEIGHT);
        background.setPreserveRatio(false);
        background.setSmooth(false); // für Pixel-Look
        root.getChildren().add(background);

        // ===============================
        // PLATTFORMEN
        // ===============================

        platforms.add(new Platform(0, HEIGHT - 200, 1000, 200, "/platforms/ground_placeholder.png"));
        platforms.add(new Platform(1100, HEIGHT - 300, 250, 300, "/platforms/ground_placeholder.png"));
        platforms.add(new Platform(1500, HEIGHT - 350, 250, 350, "/platforms/street-export.png"));

        // ===============================
        // CHECKPOINTS
        // ===============================

        checkpoints.add(new Checkpoint(1200, HEIGHT - 360));
        checkpoints.add(new Checkpoint(100, HEIGHT - 260));

        // ===============================
        // ALLES ZUR SZENE HINZUFÜGEN (RICHTIGE REIHENFOLGE)
        // ===============================
        // Plattformen zuerst (Sprite + unsichtbare Hitbox)
        for (Platform p : platforms) root.getChildren().add(p.getShape());

        // Danach Checkpoints
        for (Checkpoint c : checkpoints) root.getChildren().add(c.getShape());

        // Spieler zuletzt (damit er über allem liegt)
        startX = WIDTH / 2 - 25;
        startY = HEIGHT - 300;
        player = new Player(this, startX, startY);
        root.getChildren().addAll(player.getSprite(), player.getShape());

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
            for (Checkpoint c : checkpoints) c.moveX(dx);
            cameraOffsetX += dx;

            // ===============================
            // EINFACHES PARALLAX-SCROLLING
            // ===============================
            double parallaxSpeed = 0.1; // 0.0 = still, 1.0 = bewegt sich gleich schnell wie Plattformen
            background.setTranslateX(cameraOffsetX * parallaxSpeed);
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

                // Spieler mittig auf die Unterkante des Checkpoints setzen
                startX = c.getShape().getX() + c.getShape().getWidth() / 2 - player.getWidth() / 2;
                startY = c.getShape().getY() + c.getShape().getHeight() - player.getHeight();
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