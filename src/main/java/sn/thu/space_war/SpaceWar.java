package sn.thu.space_war;

import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Pair;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class SpaceWar extends Application {
    /**
     * asteroids are labeled as moons, because I think its funny to use a moon image as an asteroid.
     * Sprite images couldn't load properly, that's why I'll use an online source via Imgur
     */


    /**
     * SOURCES:
     * https://www.youtube.com/watch?v=N2EmtYGLh4U
     * https://www.google.com/search?q=javafx+game+main+menu&client=opera&ei=f3pqY8-tL6aCxc8P8-ufqAg&oq=javafx+game+main&gs_lcp=Cgxnd3Mtd2l6LXNlcnAQAxgAMgUIIRCgAToKCAAQRxDWBBCwAzoFCAAQogQ6BQgAEIAEOgYIABAWEB46BAghEBVKBAhBGABKBAhGGABQtRFY5SxgmDZoAXABeACAAZQBiAHgB5IBAzQuNZgBAKABAcgBCMABAQ&sclient=gws-wiz-serp
     * https://www.google.com/search?q=javafx+objectproperty&client=opera&hs=cnU&ei=zXhqY8GpHfq8xc8PyZ240AU&oq=javaFX+respawn+object&gs_lcp=Cgxnd3Mtd2l6LXNlcnAQARgAMggIABCiBBCwAzIICAAQogQQsANKBAhBGAFKBAhGGABQAFgAYIw1aAFwAHgAgAEAiAEAkgEAmAEAyAECwAEB&sclient=gws-wiz-serp
     * https://www.baeldung.com/javafx-button-eventhandler
     */

    /**
     * TODO:
     * moons not spawn near Ship
     * Ship x moon = dead => vel.set(0)
     * check High-score with score => Text: Best Score (Math.max(high-score,score);
     * Moon increases:
     * => <200 pts: 5 moons | speed: 50
     * => <500: 7 moons |sp 75
     * => <1000: 12 moons | sp 125
     * timer
     * ----
     * esc => title menu => vel.set(0)
     * --> 2 Buttons:
     * ----> resumeBtn / press ESC again: start game.
     * ExitBtn: close Game.
     */


    private final int winWidth = 1000, winHeights = 660;
    private final double deltaTime = 1 / 60.0;
    
    //MainMenu
    private List<Pair<String, Runnable>> menuData = Arrays.asList(new Pair<String, Runnable>("Resume to Game", () -> {
    }), new Pair<String, Runnable>("Exit to Desktop", Platform::exit));


    int asteroidCounter, asteroidSpawnCount = 3;

    boolean isGamePaused = false, gameOver = false;
    int score = 0, highscore;


    @Override
    public void start(Stage stage) throws IOException {

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
        ArrayList<String> keyPressList = new ArrayList<String>();
        //discrete inputs
        ArrayList<String> keyJustPressedList = new ArrayList<String>();

        ArrayList<Sprite> laserList = new ArrayList<Sprite>();
        ArrayList<Sprite> moonList = new ArrayList<Sprite>();


        isKeyPressed(scene, keyPressList, keyJustPressedList);
///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
        //Generate Sprites

        //Space
        Sprite bg = new Sprite("https://i.imgur.com/x2kwpXR.jpg");
        bg.pos.set(winWidth / 2, winHeights / 2);

        //Ship
        Sprite ship = new Sprite("https://i.imgur.com/eKHaPbT.png");
        ship.pos.set(winWidth / 2, winHeights / 2);

        /**+++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++*/
        //event handler
        score = 0;
        AnimationTimer gameLoop = new AnimationTimer() {

            @Override
            public void handle(long now) {

                //user input
                playerMov(keyPressList, ship);
                //shooting
                playerShoot(keyJustPressedList, ship, laserList);

                //difficulty change:
                if (score == 200) asteroidSpawnCount = 5;
                if (score == 500) asteroidSpawnCount = 7;
                if (score == 1000) asteroidSpawnCount = 12;

                System.out.println(asteroidCounter + " | " + asteroidSpawnCount);
                // spawn wanted asteroids
                if (asteroidCounter < asteroidSpawnCount)
                    spawnAsteroids(ship, moonList);

                /***************************************************************************************************/

                //Pause=> MainMenu();
                if (keyJustPressedList.contains("ESCAPE")) {
                    /*
                    if (isGamePaused == false) {
                        isGamePaused = true;
                        //initMainMenu(stage);
                    } else {
                        isGamePaused = false;
                    }*/

                    addTitle("SpaceWars");
                }


                //clear Key justPressedList
                keyJustPressedList.clear();

                moonHit(laserList, moonList);

                updateGraphics(ship, laserList, moonList);
                renderGraphics(context, bg, ship, laserList, moonList);


                drawScore(context);

            }
        };

////////////////////////////////////////////////////
        //end of launch()
        gameLoop.start();
        stage.show();
    }

    /**
     * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++METHODS STARTING+++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
     */
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

    private void spawnAsteroids(Sprite ship, ArrayList<Sprite> moonList) {
        if (asteroidCounter <= asteroidSpawnCount)
            asteroidCounter++;

        //Making asteroids not spawn on the ship!
        double shipX = ship.pos.x;
        double shipY = ship.pos.y;

        Sprite moon = new Sprite("https://i.imgur.com/M8SOU8I.png");

        double x = winWidth / 2 * Math.random();
        double y = winHeights / 2 * Math.random();

//        if
        moon.pos.set(x, y);

        double angle = 360 * Math.random();
        double v = 80 * Math.random() + 10;

        moon.vel.setLength(v);
        moon.vel.setAngle(angle);

        moonList.add(moon);

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


    //if moon == hit from laser => BOOM
    private void moonHit(ArrayList<Sprite> laserList, ArrayList<Sprite> moonList) {
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

    private void updateGraphics(Sprite ship, ArrayList<Sprite> laserList, ArrayList<Sprite> moonList) {

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

    //Rendering The Graphics
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

    //Draw Score on Screen
    private void drawScore(GraphicsContext context) {

        context.setFill(Color.WHITE);
        context.setStroke(Color.TEAL);
        context.setFont(new Font("Arial Black", 36));
        context.setLineWidth(3);

        String txt = "Score: " + score;
        int txtX = 10;
        int txtY = 50;

        context.fillText(txt, txtX, txtY);
        context.strokeText(txt, txtX, txtY);
    }


    private void drawHighscore(GraphicsContext context) {
        context.setFill(Color.WHITE);
        context.setStroke(Color.TEAL);
        context.setFont(new Font("Arial Black", 50));
        context.setLineWidth(3);

        String txt = "HighScore: " + highscore;
        int txtX = winWidth / 2;
        int txtY = winHeights / 2;

        context.fillText(txt, txtX, txtY);
        context.strokeText(txt, txtX, txtY);

    }

    private void addTitle(String name) {
        SpaceWarsTitle swTitle = new SpaceWarsTitle(name);
        swTitle.setTranslateX(winWidth / 2 - swTitle.getTitleWidth() / 2);
        swTitle.setTranslateY(winHeights / 2);
    }

    /**
     * protected void initMainMenu(Stage mainMenuRoot) {
     * Boundary background = new Boundary(winWidth, winHeights);
     * Font font = Font.font(72);
     * //
     * Button btnStart = new Button("Resume");
     * btnStart.setFont(font);
     * btnStart.setOnAction(event -> startGame());
     * //
     * Button btnExit = new Button("Exit");
     * btnExit.setFont(font);
     * btnExit.setOnAction(event -> mainMenuRoot.close());
     * //
     * VBox vBox = new VBox(50, btnStart, btnExit);
     * vBox.setTranslateX(winWidth / 2);
     * vBox.setTranslateY(winHeights / 2);
     * }
     * <p>
     * public void startGame() {
     * }
     */

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