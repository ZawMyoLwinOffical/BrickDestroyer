import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.effect.DropShadow;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;
import java.io.File;
import java.io.FileWriter;
import java.util.Scanner;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * UNIVERSITY PROJECT: BRICK DESTROYER (BREAKOUT)
 * DEADLINE: June 3, 2026
 * FEATURES: RESPONSIVE GUI, MULTI-BALL, LOCAL AUTHENTICATION,
 */
public class BrickDestroyer extends Application {

    private Stage primaryStage;
    private double paddleX = 250;
    private int score = 0;
    private int highScore = 0;
    private String highScoreHolder = "None";
    private double currentSpeed = 2.0;
    private String currentGamerTag = "Player";

    private final String darkGradient = "-fx-background-color: linear-gradient(to bottom right, #1a1a2e, #16213e, #0f3460);";
    private final String inputStyle = "-fx-background-radius: 12; -fx-padding: 12; -fx-background-color: rgba(255, 255, 255, 0.07); -fx-text-fill: white; -fx-prompt-text-fill: #94a3b8; -fx-border-color: rgba(255, 255, 255, 0.2); -fx-border-radius: 12; -fx-font-size: 14px;";
    private final String btnStyle = "-fx-background-radius: 25; -fx-background-color: linear-gradient(#4e54c8, #8f94fb); -fx-text-fill: white; -fx-font-weight: bold; -fx-cursor: hand;";

    private Label scoreLabel;
    private Pane gameRoot;
    private Rectangle paddle;
    private final List<Rectangle> bricks = new ArrayList<>();
    private final List<Ball> balls = new ArrayList<>();
    private AnimationTimer gameLoop;// used to run code repeatedly

    private final Color[] rowColors = {Color.web("#ff4b2b"), Color.web("#ff416c"), Color.web("#f9d423"), Color.web("#00b09b"), Color.web("#00d2ff")};

    class Ball extends Circle {
        double dx, dy;
        Ball(double x, double y, double speed) {
            super(x, y, 8, Color.WHITE);
            setStroke(Color.BLACK);
            this.dx = speed * (Math.random() > 0.5 ? 1 : -1) * (0.8 + Math.random() * 0.4);//Randomly choose left or right”
            this.dy = -speed;
        }
        void move() {
            setCenterX(getCenterX() + dx);
            setCenterY(getCenterY() + dy);
        }
    }

    @Override
    public void start(Stage stage) {
        this.primaryStage = stage;
        loadScore();

        stage.setWidth(800);
        stage.setHeight(600);
        stage.setMinWidth(600);
        stage.setMinHeight(500);

        showLoginScreen();
        stage.setTitle("Brick Destroyer - Elite Edition");

        stage.addEventHandler(javafx.scene.input.KeyEvent.KEY_PRESSED, event -> {
            if (event.getCode() == javafx.scene.input.KeyCode.F11) {
                stage.setFullScreen(!stage.isFullScreen());
            }
        });
        stage.show();
    }

    // ==========================================
    //   LOCAL AUTHENTICATION METHODS
    // ==========================================

    private boolean validInformations(String username, String password) {
        File file = new File("users.txt");
        try {
            if (!file.exists()) {
                file.createNewFile();
            }

            // Read and check if user exists
            Scanner scanner = new Scanner(file);
            while (scanner.hasNextLine()) {
                String line = scanner.nextLine();
                String[] parts = line.split(":");
                if (parts.length == 2) { //Only continue if the line has exactly 2 parts after splitting.
                    if (parts[0].equalsIgnoreCase(username)) { //Check username.
                        boolean match = parts[1].equals(password);//Check password.
                        scanner.close();
                        return match; // Returns true if password matches, false if wrong
                    }
                }
            }
            scanner.close();

            // If user doesn't exist, register them on the fly
            FileWriter writer = new FileWriter(file, true);
            writer.write(username + ":" + password + "\n");
            writer.close();
            return true;

        } catch (Exception e) {
            System.out.println("Authentication structural handling error");
            return false;
        }
    }

    // ==========================================
    //   SIMPLE SCORE FILE PROCESSING METHODS
    // ==========================================

    private void saveScore() {
        if (score > highScore) {
            highScore = score;
            highScoreHolder = currentGamerTag;
            try {
                FileWriter writer = new FileWriter("highscore.txt");
                writer.write(highScoreHolder + "\n" + highScore);
                writer.close();
            } catch (Exception e) {
                System.out.println("Error saving file");
            }
        }
    }

    private void loadScore() {
        try {
            File file = new File("highscore.txt");
            if (file.exists()) {
                Scanner scanner = new Scanner(file);
                if (scanner.hasNextLine()) highScoreHolder = scanner.nextLine();
                if (scanner.hasNextInt()) highScore = scanner.nextInt();
                scanner.close();
            } else {
                highScore = 0;
                highScoreHolder = "None";
            }
        } catch (Exception e) {
            System.out.println("Error loading file");
        }
    }

    private void resetScoreHistory() {
        try {
            File file = new File("highscore.txt");
            if (file.exists()) {
                file.delete();
            }
            highScore = 0;
            highScoreHolder = "None";
        } catch (Exception e) {
            System.out.println("Error deleting file");
        }
    }

    // ==========================================

    private void showLoginScreen() {
        StackPane outerRoot = new StackPane();
        outerRoot.setStyle(darkGradient);

        VBox contentBox = new VBox(30);
        contentBox.setAlignment(Pos.CENTER);
        contentBox.setMaxWidth(400);

        Label title = new Label("BRICK\nDESTROYER");
        title.setStyle("-fx-font-size: 44px; -fx-font-weight: 900; -fx-text-fill: white; -fx-text-alignment: center;");
        title.setEffect(new DropShadow(25, Color.web("#4e54c8")));

        VBox form = new VBox(15);
        form.setAlignment(Pos.CENTER);

        TextField name = new TextField();
        name.setPromptText("Gamer Tag");
        name.setStyle(inputStyle);

        PasswordField pass = new PasswordField();
        pass.setPromptText("Security Key");
        pass.setStyle(inputStyle);

        Label errorLabel = new Label("");
        errorLabel.setStyle("-fx-text-fill: #ff4b2b; -fx-font-weight: bold; -fx-font-size: 13px;");

        Button loginBtn = new Button("ENTER ARENA");
        loginBtn.setStyle(btnStyle + "-fx-font-size: 16px; -fx-padding: 14 60;");
        loginBtn.setMaxWidth(Double.MAX_VALUE);

        loginBtn.setOnAction(e -> {
            String userTxt = name.getText().trim();
            String passTxt = pass.getText().trim();

            if (userTxt.isEmpty() || passTxt.isEmpty()) {
                errorLabel.setText("Credentials cannot be empty!");
                return;
            }

            if (validInformations(userTxt, passTxt)) {
                this.currentGamerTag = userTxt;
                errorLabel.setText("");
                showLevelScreen();
            } else {
                errorLabel.setText("Incorrect Security Key for this Gamer Tag!");
            }
        });

        form.getChildren().addAll(name, pass, errorLabel, loginBtn);
        contentBox.getChildren().addAll(title, form);
        outerRoot.getChildren().add(contentBox);
        updateScene(new Scene(outerRoot));
    }

    private void showLevelScreen() {
        StackPane outerRoot = new StackPane();
        outerRoot.setStyle(darkGradient);

        VBox contentBox = new VBox(25);
        contentBox.setAlignment(Pos.CENTER);
        contentBox.setMaxWidth(400);

        Label lbl = new Label("CHOOSE YOUR GEAR, " + currentGamerTag.toUpperCase());
        lbl.setStyle("-fx-font-size: 20px; -fx-text-fill: white; -fx-font-weight: bold; -fx-text-alignment: center;");
        lbl.setWrapText(true);//makes text go to next line automatically when it is too long.

        VBox scoreInfo = new VBox(5);
        scoreInfo.setAlignment(Pos.CENTER);

        Label recordLbl = new Label("HIGH SCORE: " + highScoreHolder.toUpperCase() + " (" + highScore + " pts)");
        recordLbl.setStyle("-fx-font-size: 14px; -fx-text-fill: #94a3b8; -fx-font-weight: bold;");

        Button resetBtn = new Button("RESET HISTORY");
        resetBtn.setStyle("-fx-background-color: transparent; -fx-text-fill: #ff4b2b; -fx-font-size: 11px; -fx-cursor: hand; -fx-border-color: #ff4b2b; -fx-border-radius: 5;");
        resetBtn.setOnAction(e -> {
            resetScoreHistory();
            recordLbl.setText("HIGH SCORE: NONE (0 pts)");
        });

        scoreInfo.getChildren().addAll(recordLbl, resetBtn);

        VBox buttons = new VBox(15);
        buttons.setAlignment(Pos.CENTER);
        buttons.getChildren().addAll(
                createLevelBtn("RECRUIT (Normal)", "#00b09b", 3.0),
                createLevelBtn("VETERAN (Medium)", "#f39c12", 4.0),
                createLevelBtn("ELITE (Hard)", "#ff416c", 5.0)
        );

        // LOGOUT BUTTON IMPLEMENTATION
        Button logoutBtn = new Button("LOGOUT");
        logoutBtn.setStyle("-fx-background-color: transparent; -fx-text-fill: #94a3b8; -fx-font-size: 13px; -fx-cursor: hand; -fx-border-color: rgba(255,255,255,0.3); -fx-border-radius: 20; -fx-padding: 8 30;");
        logoutBtn.setOnAction(e -> showLoginScreen());

        contentBox.getChildren().addAll(lbl, scoreInfo, buttons, logoutBtn);
        outerRoot.getChildren().add(contentBox);
        updateScene(new Scene(outerRoot));
    }

    private Button createLevelBtn(String text, String color, double speed) {
        Button btn = new Button(text);
        btn.setStyle("-fx-background-color: " + color + "; -fx-text-fill: white; -fx-font-weight: bold; -fx-background-radius: 15; -fx-font-size: 15px; -fx-padding: 14 0;");
        btn.setMaxWidth(Double.MAX_VALUE);
        btn.setOnAction(e -> startGame(speed));
        return btn;//button goes back to: buttons.getChildren().add(...)
    }

    private void startGame(double speed) {
        this.currentSpeed = speed;
        gameRoot = new Pane();
        gameRoot.setStyle(darkGradient);
        Scene scene = new Scene(gameRoot);
        if (gameLoop != null) gameLoop.stop();//If old game running → stop it
        setupGameLogic();

        scene.setOnMouseMoved(e -> {
            if (paddle != null) {
                //we move paddle so mouse lands in middle of paddle
                //mouse gives the center point. We convert it into a left-edge position by subtracting half the paddle width,
                // so when the paddle is drawn, the mouse naturally ends up in the middle.
                paddleX = e.getX() - (paddle.getWidth() / 2);//convert to paddle position (“Redraw this paddle at X  position)
                double maxPaddleX = gameRoot.getWidth() - paddle.getWidth();//Find the furthest right position the paddle is allowed to go
                paddle.setX(Math.max(0, Math.min(maxPaddleX, paddleX)));//“Move paddle, but don’t let it go outside screen.”
            }
        });

        //Adjusting paddle and bricks according to height and weight.
        gameRoot.heightProperty().addListener((obs, oldVal, newVal) -> {
            if (paddle != null) paddle.setY(newVal.doubleValue() - 50);
        });
        gameRoot.widthProperty().addListener((obs, oldVal, newVal) -> repositionBricks(newVal.doubleValue()));

        gameLoop = new AnimationTimer() {
            @Override
            public void handle(long now) { update(); }
        }; //AnimationTimer? -->JavaFX built-in game loop system(~60 times per second)
        gameLoop.start();
        updateScene(scene);
    }

    private void setupGameLogic() {
        gameRoot.getChildren().clear();
        bricks.clear();
        balls.clear();
        score = 0;
        scoreLabel = new Label("POINTS: 0");
        scoreLabel.setLayoutX(20); scoreLabel.setLayoutY(15);
        scoreLabel.setStyle("-fx-font-size: 22px; -fx-font-weight: 900; -fx-text-fill: white;");

        double activeWidth = primaryStage.getWidth() > 0 ? primaryStage.getWidth() : 800;
        double activeHeight = primaryStage.getHeight() > 0 ? primaryStage.getHeight() : 600;

        paddle = new Rectangle(activeWidth / 2.0 - 60, activeHeight - 50, 120, 15);
        paddle.setFill(Color.web("#00d2ff"));
        paddle.setArcWidth(15); paddle.setArcHeight(15);
        paddle.setEffect(new DropShadow(15, Color.CYAN));

        gameRoot.getChildren().addAll(paddle, scoreLabel);
        repositionBricks(activeWidth);
        Ball initialBall = new Ball(activeWidth / 2.0, activeHeight - 80, currentSpeed);
        balls.add(initialBall);//Put ball into the ball list.
        gameRoot.getChildren().add(initialBall);//Ball becomes visible.
    }

    private void repositionBricks(double width) {
        if (bricks.isEmpty()) {
            for (int j = 0; j < 5; j++) {
                for (int i = 0; i < 8; i++) {
                    Rectangle brick = new Rectangle(0, j * 30 + 70, 1, 20); //Y changes because of j
                    // 70 px = starting gap from top  30 px = distance between each row
                    brick.setFill(rowColors[j]);
                    brick.setArcWidth(10); brick.setArcHeight(10);
                    brick.setStroke(Color.web("#ffffff", 0.2));
                    bricks.add(brick); // Store brick in ArrayList for game logic
                    gameRoot.getChildren().add(brick);// Display brick on screen
                }
            }
        }
        //Take bricks from the list and arrange them like a grid on the screen
        double padding = 20;
        double gap = 10;
        double availableWidth = width - (padding * 2) - (gap * 7);
        double brickWidth = Math.max(40, availableWidth / 8);//Each brick gets equal width
        int index = 0;
        // Position bricks
        for (int j = 0; j < 5; j++) {
            for (int i = 0; i < 8; i++) {
                if (index < bricks.size()) {
                    Rectangle brick = bricks.get(index);
                    brick.setX(padding + i * (brickWidth + gap)); //X changes because of i:
                    brick.setWidth(brickWidth);//Make all bricks same size
                    index++;
                }
            }
        }
    }

    private void update() {
        double currentWidth = gameRoot.getWidth();
        double currentHeight = gameRoot.getHeight();
        Iterator<Ball> ballIterator = balls.iterator();
        List<Ball> spawnedBalls = new ArrayList<>();//Temporary storage for new balls created during the frame”

        while (ballIterator.hasNext()) {
            Ball b = ballIterator.next();
            b.move();
            if (b.getCenterX() <= b.getRadius() || b.getCenterX() >= currentWidth - b.getRadius()) {
                b.dx *= -1;//Reverse left-right direction”
                b.setCenterX(Math.max(b.getRadius(), Math.min(currentWidth - b.getRadius(), b.getCenterX())));//Make sure ball stays inside screen
            }
            if (b.getCenterY() <= b.getRadius()) {
                b.dy *= -1;
                b.setCenterY(b.getRadius());//“Push ball slightly down so it is not stuck in wall
            }
            if (b.dy > 0 && b.getBoundsInParent().intersects(paddle.getBoundsInParent())) {
                b.dy *= -1;
                b.setCenterY(paddle.getY() - b.getRadius() - 1);//Move the ball just above the paddle, so it doesn’t get stuck inside it.
            }
            Iterator<Rectangle> brickIterator = bricks.iterator();
            while (brickIterator.hasNext()) {
                Rectangle brick = brickIterator.next();
                if (b.getBoundsInParent().intersects(brick.getBoundsInParent())) {
                    b.dy *= -1;
                    brick.setVisible(false);
                    gameRoot.getChildren().remove(brick);
                    brickIterator.remove();//Removes from bricks list safely

                    score += 10;
                    scoreLabel.setText("POINTS: " + score);
                    if (score > 0 && score % 100 == 0) spawnedBalls.add(new Ball(b.getCenterX(), b.getCenterY(), currentSpeed));
                    break;
                }
            }
            if (b.getCenterY() > currentHeight) {   //ball goes below screen
                gameRoot.getChildren().remove(b);
                ballIterator.remove();
            }
        }
        balls.addAll(spawnedBalls);
        for (Ball nb : spawnedBalls) gameRoot.getChildren().add(nb);

        if (bricks.isEmpty()) {

            endGame("VICTORY UNLOCKED");
        }
        else if (balls.isEmpty()) {

            endGame("SYSTEM FAILURE");
        }
    }

    private void endGame(String message) {
        gameLoop.stop();
        saveScore();
        VBox endBox = new VBox(25);
        endBox.setAlignment(Pos.CENTER);
        endBox.prefWidthProperty().bind(gameRoot.widthProperty());
        endBox.prefHeightProperty().bind(gameRoot.heightProperty());
        endBox.setStyle("-fx-background-color: rgba(10, 10, 25, 0.9);");
        Label endText = new Label(message + "\nSCORE: " + score);
        endText.setStyle("-fx-font-size: 38px; -fx-text-fill: white; -fx-font-weight: 900; -fx-text-alignment: center;");
        endText.setEffect(new DropShadow(20, Color.web("#4e54c8")));
        Button btn = new Button("RELOAD ARENA");
        btn.setStyle(btnStyle + "-fx-padding: 12 50;");
        btn.setOnAction(e -> showLevelScreen());
        endBox.getChildren().addAll(endText, btn);
        gameRoot.getChildren().add(endBox);
    }

    private void updateScene(Scene newScene) {
        boolean isFull = primaryStage.isFullScreen();//"Is the window fullscreen now?"
        primaryStage.setScene(newScene);
        if (isFull) primaryStage.setFullScreen(true);//If user WAS fullscreen before changing scene make fullscreen again.
    }

    public static void main(String[] args) {
        launch(args);
    }
}
