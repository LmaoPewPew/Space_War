package sn.thu.space_war;

import javafx.animation.AnimationTimer;
import javafx.application.Application;
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

import java.util.ArrayList;

public class SpaceWar extends Application {
    /**
     * asteroids are labeled as moons, because I think its funny to use a moon image as an asteroid. moons == asteroids!
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
     * moons not spawn near Ship            | on-Hold
     * check High-score with score          | planned next: siehe flappy bird project
     * <p>
     * endCart: => show highscore, play again button
     * => Text: Best Score (Math.max(high-score,score);
     * ----
     * esc => title menu => vel.set(0)
     * --> 2 Buttons:
     * ----> resumeBtn / press ESC again: start game.
     * ExitBtn: close Game.
     */


    private final int winWidth = 1000, winHeights = 660;

    /*
   MainMenu
    private List<Pair<String, Runnable>> menuData = Arrays.asList(new Pair<String, Runnable>("Resume to Game", () -> {
   }), new Pair<String, Runnable>("Exit to Desktop", Platform::exit));
   */


    int asteroidCounter, asteroidSpawnCount = 3;

    boolean isGamePaused = false, gameOver = false;
    int score = 0;
    String highscore;


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

                if (!gameOver) {
                    //user input
                    playerMov(keyPressList, ship);
                    playerShoot(keyJustPressedList, ship, laserList);
                    mainMenu(keyJustPressedList);
                }


                //difficulty change:
                if (score == 200) asteroidSpawnCount = 5;
                if (score == 500) asteroidSpawnCount = 7;
                if (score == 1000) asteroidSpawnCount = 12;
                if (asteroidCounter < asteroidSpawnCount) spawnAsteroids(ship, moonList);

                /***************************************************************************************************/
                //clear Key justPressedList
                keyJustPressedList.clear();

                moonHit(laserList, moonList);
                collisionDetection(ship, moonList);

                updateGraphics(ship, laserList, moonList);
                renderGraphics(context, bg, ship, laserList, moonList);

                drawScore(context);
                if (gameOver) {
                    drawDeathScreen(context);
                }
            }
        };
///////////////////////////////////////////////////////////////////
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
        //Making asteroids not spawn on the ship!
        //declare ship pos and 50 radius "anti-spawn bubble"
        double shipX = ship.pos.x;
        double shipY = ship.pos.y;

        if (asteroidCounter <= asteroidSpawnCount) asteroidCounter++;
        Sprite moon = new Sprite("https://i.imgur.com/M8SOU8I.png");

        // moon position
        double x = winWidth * Math.random();
        double y = winHeights * Math.random();

        moon.pos.set(spawnBubble(x, shipX), spawnBubble(y, shipY));

        //moon.pos.set(x, y);

        double angle = 360 * Math.random();
        double v = asteroidsSpeed();
        //v = 0;

        moon.vel.setLength(v);
        moon.vel.setAngle(angle);

        moonList.add(moon);
    }


    //TODO: ALL MOONS SPAWN IN A SINGLE CORNER!!
    private double spawnBubble(double pos, double sPos) {

        final int pow = (int) (Math.random() * 2) + 1;
        final int alt = (int) Math.pow((-1), pow);

        if (sPos + 50 >= pos || sPos - 50 <= pos) pos += sPos * alt;

        //out of bounds spawn
        if (pos <= 0) pos = winWidth - 100;
        if (pos >= winWidth) pos = 100;

        return pos;
    }


    //changes asteroids spawn rate and asteroids speed;
    private double asteroidsSpeed() {
        double speed = 20 * Math.random() + 5;

        if (score >= 200) speed = 40 * Math.random() + speed;
        if (score >= 500) speed = 80 * Math.random() + speed;
        if (score >= 1000) speed = 100 * Math.random() + speed;

        speed = Math.floor(speed);
        return speed;
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

    private void mainMenu(ArrayList<String> keyJustPressedList) {
        if (keyJustPressedList.contains("ESCAPE")) {
            if (!isGamePaused) {
                addTitle();

                System.out.println("is the game paused? " + isGamePaused);

                isGamePaused = true;
            } else {


                isGamePaused = false;
            }
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
                    score += 100;
                    asteroidCounter--;
                }
            }
        }
    }


    //TODO: moon collision!
    private void collisionDetection(Sprite ship, ArrayList<Sprite> moonList) {

        for (Sprite moon : moonList) {
            if (ship.overlaps(moon)) {
                gameOver = true;
                ship.vel.setLength(0);
                moon.vel.setLength(0);

                setHighscore();
                getEndCart();
            }
        }
    }

    //TODO
    private void setHighscore() {
        /*


        try {
            File hsFile = new File("sn/thu/space_war/highscore.txt");
            Scanner myReader = new Scanner(hsFile);

            while (myReader.hasNextLine()) {
                String data = myReader.nextLine();
                System.out.println(data);
            }
            myReader.close();
        } catch (FileNotFoundException e) {
            System.out.println("An error occurred.");
            e.printStackTrace();
        }
        /*
        if (score > Integer.parseInt(highscore)) {
            highscore = String.valueOf(score);

            try {
                FileWriter writer = new FileWriter("sn/thu/space_war/highscore.txt", false);
                BufferedWriter bufferedWriter = new BufferedWriter(writer);
                bufferedWriter.write(highscore);
                bufferedWriter.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

         */
    }

    private void getEndCart() {
    }


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


    private void drawDeathScreen(GraphicsContext context) {
        //setHighscore();

        //DrawHighscore
        String txt = "High Score: " + highscore;
        int txtX = winWidth / 2 - 200;
        int txtY = winHeights / 2 - 125;
        setText(context, txt, 40, txtX, txtY, Color.WHITE);

        //draw Death screen:
        setText(context, "GAME OVER!", 75, txtX - 100, txtY + 100, Color.RED);

    }

    private void addTitle() {
        SpaceWarsTitle swTitle = new SpaceWarsTitle("SpaceWars");
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