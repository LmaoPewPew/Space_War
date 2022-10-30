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

        //continues inputs
        ArrayList<String> keyPressList = new ArrayList<String>();
        //discrete inputs
        ArrayList<String> keyJustPressedList = new ArrayList<String>();

        scene.setOnKeyPressed((KeyEvent event) -> {
            String keyName = event.getCode().toString();
            //avoid duplicates
            if (!keyPressList.contains(keyName)) {

                keyPressList.add(keyName);
                keyJustPressedList.add(keyName);
            }
        });

        scene.setOnKeyReleased((KeyEvent event) -> {
            String keyName = event.getCode().toString();
            if (keyPressList.contains(keyName)) {

                keyPressList.remove(keyName);
                keyJustPressedList.remove(keyName);
            }
        });


        //Space
        Sprite bg = new Sprite("https://i.imgur.com/x2kwpXR.jpg");
        bg.pos.set(400, 300);

        //Ship
        Sprite ship = new Sprite("https://i.imgur.com/eKHaPbT.png");
        ship.pos.set(400, 300);


        ArrayList<Sprite> laserList = new ArrayList<Sprite>();
        ArrayList<Sprite> moonList = new ArrayList<Sprite>();


        AnimationTimer gameLoop = new AnimationTimer() {

            @Override
            public void handle(long now) {

                //user input
                if (keyPressList.contains("LEFT") || keyPressList.contains("A")) ship.rot -= 1.5;
                if (keyPressList.contains("RIGHT") || keyPressList.contains("D")) ship.rot += 1.5;

                if (keyPressList.contains("UP") || keyPressList.contains("W")) {
                    ship.vel.setLength(75);
                    ship.vel.setAngle(ship.rot);
                } else {
                    ship.vel.setLength(0);
                }

                //shooting
                if (keyJustPressedList.contains("SPACE")) {
                    Sprite laser = new Sprite("https://i.imgur.com/WgY7t46.png");
                    laser.pos.set(ship.pos.x, ship.pos.y);

                    laser.vel.setLength(400);
                    laser.vel.setAngle(ship.rot);

                    laserList.add(laser);
                }


                //clear Key justPressedList
                keyJustPressedList.clear();

                ship.update(1 / 60.0);

                for (int i = 0; i < laserList.size(); i++) {
                    Sprite laser = laserList.get(i);
                    laser.update(1 / 60.0);

                    if (laser.elapsedTime > 1 ) {
                        laserList.remove(i);
                    }
                }


                bg.render(context);
                ship.render(context);
                for (Sprite laser : laserList) {
                    laser.render(context);
                }

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