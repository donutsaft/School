package com.example.scrollingplatformer;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;
import java.util.*;

public class ScrollingPlatformer extends Application {

    private GameWorld gameWorld;

    @Override
    public void start(Stage stage) {
        gameWorld = new GameWorld(1200, 700);
        Scene scene = gameWorld.createScene();

        stage.setTitle("v0.0.1.4");
        stage.setScene(scene);
        stage.setResizable(false);
        stage.show();

        gameWorld.startGameLoop();
    }

    public static void main(String[] args) {
        launch(args);
    }
}