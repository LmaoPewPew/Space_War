package sn.thu.space_war;

import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.ArrayList;

public class SpaceWar extends Application {

    /**
     * https://www.youtube.com/watch?v=9xsT6Z6HQfw (42:20)  Part 1
     * https://www.youtube.com/watch?v=7Vb9StpxFtw          Part 1
     */


    @Override
    public void start(Stage stage) throws IOException {
        stage.getIcons().add(new Image("https://i.imgur.com/DkhjfKu.png"));
        stage.setTitle("SpaceWars!");

        BorderPane root = new BorderPane();
        Scene scene = new Scene(root);
        stage.setScene(scene);


        Canvas canvas = new Canvas(800, 600);
        GraphicsContext context = canvas.getGraphicsContext2D();
        root.setCenter(canvas);

        ArrayList<String> keys = new ArrayList<String>();

        scene.setOnKeyPressed((KeyEvent event) -> {
            String keyName = event.getCode().toString();
            //avoid duplicates
            if (!keys.contains(keyName)) keys.add(keyName);
        });

        scene.setOnKeyReleased((KeyEvent event) -> {
            String keyName = event.getCode().toString();
            if (keys.contains(keyName)) keys.remove(keyName);
        });

        //Space
        Sprite bg = new Sprite("https://i.imgur.com/x2kwpXR.jpg");
        bg.pos.set(400, 300);

        //Ship
        Sprite ship = new Sprite("https://i.imgur.com/eKHaPbT.png");
        ship.pos.set(400, 300);

        AnimationTimer gameLoop = new AnimationTimer() {


            @Override
            public void handle(long now) {

                //user input
                if (keys.contains("LEFT") || keys.contains("A")) ship.rot -= 1.5;
                if (keys.contains("RIGHT") || keys.contains("D")) ship.rot += 1.5;

                if (keys.contains("UP") || keys.contains("W")) {
                    ship.vel.setLength(75);
                    ship.vel.setAngle(ship.rot);
                } else {
                    ship.vel.setLength(0);
                }

                //shoot
                // if (keys.contains("SPA")) ship.rot -= 3;


                ship.update(1 / 60.0);

                bg.render(context);
                ship.render(context);

            }
        };


        gameLoop.start();
        stage.show();
    }


    public static void main(String[] args) {
        try {
            launch();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            System.exit(0);
        }

    }
}