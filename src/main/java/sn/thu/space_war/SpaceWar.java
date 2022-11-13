package sn.thu.space_war;

import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.io.*;
import java.util.ArrayList;

public class SpaceWar extends Application {

    /**
     * SIDENOTES:
     * Asteroids are labeled as moons, because I think its funny to use a moon image as an asteroid. moons == asteroids!
     * -------
     * ERRORS:
     * Sprite images couldn't load properly, that's why I'll use an online source via Imgur!!
     * Any kind of path files wouldn't load, making the Highscore functions ineffective!
     * Death Screen Buttons don't work!
     * Pause Menu, "Game Paused!" Text doesn't appear
     */

    private final int winWidth = 1000, winHeights = 660;
    private int asteroidCounter, asteroidSpawnCount = 3;
    private boolean gamePaused = false, gameOver = false;
    private int score = 0;
    private String highscore;
    String filePath = "sn/thu/space_war/highscore.txt";
    File scoreFile = new File(filePath);

    @Override
    public void start(Stage stage) {
        stage.getIcons().add(new Image("https://i.imgur.com/DkhjfKu.png"));
        stage.setTitle("SpaceWars!");

        BorderPane root = new BorderPane();
        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.setResizable(false);
        stage.initStyle(StageStyle.UTILITY);

        //ArrayList
        Canvas canvas = new Canvas(winWidth, winHeights);
        GraphicsContext context = canvas.getGraphicsContext2D();
        root.setCenter(canvas);

        //continues inputs
        ArrayList<String> keyPressList = new ArrayList<>();
        //discrete inputs
        ArrayList<String> keyJustPressedList = new ArrayList<>();

        ArrayList<Sprite> laserList = new ArrayList<>();
        ArrayList<Sprite> moonList = new ArrayList<>();

        isKeyPressed(scene, keyPressList, keyJustPressedList);

        ////////////////////////////////////////////////////////////////////////////////////////////////////////////////
        //Sprite bg = new Sprite("img/space.jpg");
        Sprite bg = new Sprite("https://i.imgur.com/x2kwpXR.jpg");
        bg.pos.set(winWidth / 2, winHeights / 2);

        //Ship
        Sprite ship = new Sprite("https://i.imgur.com/eKHaPbT.png");
        ship.pos.set(winWidth / 2, winHeights / 2);

        /**+++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++*/
        //event handler
        score = 0;
        AnimationTimer gameLoop = new AnimationTimer() {
            int i = 0;

            @Override
            public void handle(long now) {
                if (!gameOver) {
                    //user input
                    playerMov(keyPressList, ship);
                    playerShoot(keyJustPressedList, ship, laserList);
                    checkMainMenu(keyJustPressedList, context, stage, canvas, root);
                }

                gameDifficulty();
                if (asteroidCounter < asteroidSpawnCount) spawnAsteroids(ship, moonList);

                keyJustPressedList.clear();

                moonHit(laserList, moonList);
                collisionDetection(ship, moonList);

                updateGraphics(ship, laserList, moonList);
                renderGraphics(context, bg, ship, laserList, moonList);

                drawScore(context);

                if (gameOver) {
                    drawDeathScreen(context);
                    i++;
                    if (i == 1) getEndCard(root, canvas, stage);
                }
                if (gamePaused) {
                    drawPausedScreen(context, "GAME PAUSED!");

                } else drawPausedScreen(context, "");
            }

        };
        //end of launch()
        gameLoop.start();
        stage.show();
    }

    /**********************************************GRAPHICS************************************************************/
    private void updateGraphics(Sprite ship, ArrayList<Sprite> laserList, ArrayList<Sprite> moonList) {
        double deltaTime = 1 / 60.0;

        ship.update(deltaTime, winWidth, winHeights);
        for (Sprite moon : moonList) {
            moon.update(deltaTime, winWidth, winHeights);
        }

        //update lasers; destroy after 1sec
        for (int i = 0; i < laserList.size(); i++) {
            Sprite laser = laserList.get(i);
            laser.update(deltaTime, winWidth, winHeights);

            if (laser.elapsedTime > 1) {
                laserList.remove(i);
            }
        }
    }

    /**********************************************PLAYER**************************************************************/
    private void isKeyPressed(Scene scene, ArrayList<String> keyPressList, ArrayList<String> keyJustPressedList) {
        //isKeyPressed
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
    }

    private void playerMov(ArrayList<String> keyPressList, Sprite ship) {
        if (keyPressList.contains("LEFT") || keyPressList.contains("A")) ship.rot -= 1.5;
        if (keyPressList.contains("RIGHT") || keyPressList.contains("D")) ship.rot += 1.5;
        if (keyPressList.contains("UP") || keyPressList.contains("W")) {
            ship.vel.setLength(75);
            ship.vel.setAngle(ship.rot);
        } else {
            ship.vel.setLength(0);
        }
    }

    private void playerShoot(ArrayList<String> keyJustPressedList, Sprite ship, ArrayList<Sprite> laserList) {
        if (keyJustPressedList.contains("SPACE")) {
            Sprite laser = new Sprite("https://i.imgur.com/WgY7t46.png");
            laser.pos.set(ship.pos.x, ship.pos.y);
            laser.vel.setLength(400);
            laser.vel.setAngle(ship.rot);
            laserList.add(laser);
        }
    }

    private void gameDifficulty() {
        //difficulty change:
        if (score == 200) asteroidSpawnCount = 5;
        if (score == 500) asteroidSpawnCount = 7;
        if (score == 1000) asteroidSpawnCount = 12;
    }

    /**************************************************ASTEROIDS*******************************************************/
    private void spawnAsteroids(Sprite ship, ArrayList<Sprite> moonList) {
        //Making asteroids not spawn on the ship!
        //declare ship pos and 50 radius "anti-spawn bubble"
        double shipX = ship.pos.x;
        double shipY = ship.pos.y;

        if (asteroidCounter <= asteroidSpawnCount) asteroidCounter++;
        Sprite moon = new Sprite("https://i.imgur.com/M8SOU8I.png");

        // moon position
        double x = winWidth * Math.random();
        double y = winHeights * Math.random();

        moon.pos.set(spawnPadding(x, shipX), spawnPadding(y, shipY));

        //movement
        double angle = 360 * Math.random();
        double v = asteroidsSpeed();

        moon.vel.setLength(v);
        moon.vel.setAngle(angle);

        moonList.add(moon);
    }

    private double asteroidsSpeed() {
        if (gamePaused) return 0;
        //changes asteroids spawn rate and asteroids speed;
        double speed = 20 * Math.random() + 5;
        if (score >= 200) speed = 40 * Math.random() + speed;
        if (score >= 500) speed = 80 * Math.random() + speed;
        if (score >= 1000) speed = 100 * Math.random() + speed;

        speed = Math.floor(speed);

        return speed;
    }

    private void moonHit(ArrayList<Sprite> laserList, ArrayList<Sprite> moonList) {
        //if moon == hit from laser => BOOM
        for (int laserNum = 0; laserNum < laserList.size(); laserNum++) {
            Sprite laser = laserList.get(laserNum);
            for (int moonNum = 0; moonNum < moonList.size(); moonNum++) {
                Sprite moon = moonList.get(moonNum);
                if (laser.overlaps(moon)) {
                    laserList.remove(laserNum);
                    moonList.remove(moonNum);
                    score += 50;
                    asteroidCounter--;
                }
            }
        }
    }

    private double spawnPadding(double pos, double sPos) {
        // 1 || -1
        final int alt = (int) Math.pow((-1), ((int) (Math.random() * 2) + 1));

        if (sPos + 100 >= pos || sPos - 100 <= pos) pos = (sPos * 0.5) * alt;
        //out of bounds spawn
        if (pos <= 0) pos = (winWidth - 300) - (winHeights - 300);
        if (pos >= winWidth || pos >= winHeights) pos = 300;

        return pos;
    }

    /*******************************************HIGHSCORE**************************************************************/
    //TODO   ###############################################################################################################
    private void setHighscore() {
        highscore = this.getHighscore();
        if (score > Integer.parseInt(highscore)) {
            highscore = "" + score;
            saveHighscore();
        }
    }

    public String getHighscore() {
        FileReader readFile;
        BufferedReader reader = null;

        try {
            readFile = new FileReader(scoreFile);
            reader = new BufferedReader(readFile);
            return reader.readLine();
        } catch (Exception e) {
            return String.valueOf(score);
        } finally {
            try {
                if (reader != null) reader.close();
            } catch (IOException e) {
                //e.printStackTrace();
                System.out.println("GetHS, finally");
            }
        }
    }

    public void saveHighscore() {
//if scoreFile doesn't exist, create new one!
        if (!scoreFile.exists()) {
            try {
                scoreFile.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        try {
            FileWriter writeFile = new FileWriter(scoreFile);
            BufferedWriter writer = new BufferedWriter(writeFile);
            writer.write(this.highscore);
            writer.close();
        } catch (Exception e) {
            System.out.println("saveHigh");
        }
    }

    private void renderGraphics(GraphicsContext context, Sprite bg, Sprite ship, ArrayList<Sprite> laserList, ArrayList<Sprite> moonList) {
        bg.render(context);
        ship.render(context);
        for (Sprite laser : laserList) {
            laser.render(context);
        }
        for (Sprite moon : moonList) {
            moon.render(context);
        }
        for (Sprite laser : laserList) {
            laser.render(context);
        }
    }

    /**********************************************SCORE***************************************************************/
    private void setText(GraphicsContext context, String txt, int size, int txtX, int txtY, Color fill) {
        context.setFill(fill);
        context.setStroke(Color.BLACK);
        context.setFont(new Font("Arial Black", size));
        context.setLineWidth(3);
        context.fillText(txt, txtX, txtY);
        context.strokeText(txt, txtX, txtY);
    }

    //Draw Score on Screen
    private void drawScore(GraphicsContext context) {
        String txt = "Score: " + score;
        int txtX, txtY;
        if (!gameOver) {
            txtX = 10;
            txtY = 50;
        } else {
            txtX = winWidth / 2 - 125;
            txtY = winHeights / 2 - 200;
        }
        setText(context, txt, 36, txtX, txtY, Color.WHITE);
    }

    /**************************************************DEATH-METHODS***************************************************/
    private void collisionDetection(Sprite ship, ArrayList<Sprite> moonList) {
        for (Sprite moon : moonList) {
            if (ship.overlaps(moon)) {
                gameOver = true;

                ship.vel.setLength(0);
                moon.vel.setLength(0);

                setHighscore();
            }
        }
    }

    private void drawDeathScreen(GraphicsContext context) {
        //DrawHighscore
        String txt = "High Score: " + highscore;
        int txtX = winWidth / 2 - 200;
        int txtY = winHeights / 2 - 125;
        setText(context, txt, 40, txtX, txtY, Color.WHITE);

        //draw Death screen:
        setText(context, "GAME OVER!", 75, txtX - 100, txtY + 100, Color.RED);
    }

    private void getEndCard(BorderPane pane, Canvas canvas, Stage stage) {
        //respawn / exit
        Button rsBtn = new Button("Play Again");
        Button exBtn = new Button("Exit Game");

        StackPane stackPane = new StackPane(canvas, btnConfig(new VBox(), stage, rsBtn, exBtn));
        pane.setCenter(stackPane);
    }

    /********************************************MAIN-MENU*************************************************************/
    private void checkMainMenu(ArrayList<String> keyJustPressedList, GraphicsContext context, Stage s, Canvas c, BorderPane p) {
        if (keyJustPressedList.contains("ESCAPE")) {
            gamePaused = true;
            drawMainMenu(s, c, p);
        }
    }

    private void drawMainMenu(Stage stage, Canvas canvas, BorderPane pane) {
        //restart / resume / exit
        Button rsBtn = new Button("New Game");
        Button resBtn = new Button("Resume");
        Button exBtn = new Button("Exit Game");

        StackPane stackPane = new StackPane(canvas, btnConfig(new VBox(), stage, rsBtn, resBtn, exBtn));
        pane.setCenter(stackPane);
    }

    private void drawPausedScreen(GraphicsContext context, String txt) {
        int txtX = winWidth / 2 - 300;
        int txtY = winHeights / 2 - 100;

        setText(context, txt, 75, txtX, txtY, Color.CADETBLUE);
    }

    private VBox btnConfig(VBox vBox, Stage stage, Button rsBtn, Button exBtn) {
        //vBox endCard
        rsBtn.setPrefSize(200, 50);
        exBtn.setPrefSize(200, 50);

        rsBtn.setFont(new Font("Arial Black", 20));
        exBtn.setFont(new Font("Arial Black", 20));

        rsBtn.setOnAction(e -> restartGame(stage));
        exBtn.setOnAction(e -> stage.close());

        vBox.getChildren().addAll(rsBtn, exBtn);

        vBox.setSpacing(10);
        vBox.setAlignment(Pos.CENTER_RIGHT);
        vBox.setPadding(new Insets(225, winWidth / 2 - 67, 125, 0));
        return vBox;
    }

    private VBox btnConfig(VBox vBox, Stage stage, Button rsBtn, Button resBtn, Button exBtn) {
        //vBoxMenu
        resBtn.setPrefSize(200, 50);
        resBtn.setFont(new Font("Arial Black", 20));

        VBox finalVBox = vBox;
        resBtn.setOnAction(e -> resumeGame(finalVBox, rsBtn, resBtn, exBtn));

        vBox.getChildren().addAll(resBtn);
        vBox = btnConfig(vBox, stage, rsBtn, exBtn);

        return vBox;
    }

    /**************************************BUTTON-METHODS**************************************************************/
    private void resumeGame(VBox vb, Button rsBtn, Button resBtn, Button exBtn) {
        vb.getChildren().remove(rsBtn);
        vb.getChildren().remove(resBtn);
        vb.getChildren().remove(exBtn);
        gamePaused = false;
    }

    private void restartGame(Stage s) {
        System.out.println("Restart");
        s.close();
    }


    public static void main(/*String[] args*/) {
        try {
            launch();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}