package uet.oop.spaceshootergamejavafx.entities;

import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.effect.BlurType;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.Image;
import javafx.scene.input.KeyCode;
import javafx.scene.layout;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.TextAlignment;
import javafx.stage.Stage;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

public class SpaceShooter extends Application {
    public static final int SCREEN_WIDTH = 500;
    public static final int SCREEN_HEIGHT = 700;
    public static int numLivesInitial = 3;

    private int score;
    private int lives;
    private int lives1;
    private int lives2;
    private boolean bossExists;
    private boolean gameRunning;
    private boolean gameOver;
    private int currentLevel = 1;
    private int scoreForNextLevel = 200;
    private int scoreForBoss = 150;

    private Label scoreLabel;
    private Label player1livesLabel;
    private Label player2livesLabel;
    private Label levelLabel;
    private Label tempMessageLabel;
    private AnimationTimer tempMessageTimer;
    private VBox uiOverlay;

    private List<GameObject> gameObjects = new ArrayList<>();
    private Player player1;
    private Player player2;
    private int currentMode = 1;
    private BossEnemy currentBoss;


    private Pane gameRoot;
    private Canvas gameCanvas;
    private GraphicsContext gc;
    private Scene gameScene;
    private Scene menuScene;
    private Button singlePlayerButton;
    private Button twoPlayerButton;
    private Stage primaryStage;

    //AI
    private AIController aiController;

    private final Random random = new Random();

    private long lastEnemySpawnTime;
    private long enemySpawnCooldown = 1500;
    private long lastPowerUpSpawnTime;
    private long powerUpSpawnCooldown = 10000;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        this.primaryStage = primaryStage;
        this.primaryStage.setTitle("Space Shooter");

        try {
            Image appIcon = new Image(getClass().getResourceAsStream("game_icon.png"));
            if (appIcon != null && !appIcon.isError()) {
                primaryStage.getIcons().add(appIcon);
            } else {
                System.err.println("Không thể tải icon ứng dụng: game_icon.png");
                if (appIcon != null && appIcon.getException() != null) {
                    System.err.println("Lỗi cụ thể: " + appIcon.getException().getMessage());
                }
            }
        } catch (Exception e) {
            System.err.println("Lỗi ngoại lệ khi tải icon ứng dụng: " + e.getMessage());
            e.printStackTrace();
        }

        gameRoot = new Pane();
        gameCanvas = new Canvas(SCREEN_WIDTH, SCREEN_HEIGHT);
        gc = gameCanvas.getGraphicsContext2D();
        gameRoot.getChildren().add(gameCanvas);

        scoreLabel = new Label("Score: 0");
        scoreLabel.setFont(Font.font("Verdana", 20));
        scoreLabel.setTextFill(Color.WHITE);

        player1livesLabel = new Label("P1 Lives: " + player1livesLabel);
        player1livesLabel.setFont(Font.font("Verdana", 20));
        player1livesLabel.setTextFill(Color.WHITE);

        player2livesLabel = new Label("P2 Lives: " + player2livesLabel);
        player2livesLabel.setFont(Font.font("Verdana", 20));
        player2livesLabel.setTextFill(Color.WHITE);
        player2livesLabel.setVisible(false);

        levelLabel = new Label("Level: 1");
        levelLabel.setFont(Font.font("Verdana", 20));
        levelLabel.setTextFill(Color.WHITE);


        tempMessageLabel = new Label("");
        tempMessageLabel.setFont(Font.font("Verdana", 30));
        tempMessageLabel.setTextFill(Color.YELLOW);
        tempMessageLabel.setAlignment(Pos.CENTER);
        tempMessageLabel.setTextAlignment(TextAlignment.CENTER);
        tempMessageLabel.setMinWidth(SCREEN_WIDTH);

        VBox centerMessageLayout = new VBox(tempMessageLabel);
        centerMessageLayout.setAlignment(Pos.CENTER);
        centerMessageLayout.setPrefHeight(SCREEN_HEIGHT);
        centerMessageLayout.setMouseTransparent(true);

        HBox topBar = new HBox(20, scoreLabel, player1livesLabel, player2livesLabel, levelLabel);
        topBar.setPadding(new Insets(10));
        topBar.setAlignment(Pos.TOP_CENTER);

        uiOverlay = new VBox(topBar, centerMessageLayout);
        uiOverlay.setSpacing(10);
        uiOverlay.setMouseTransparent(true);
        gameRoot.getChildren().add(uiOverlay);

        menuScene = new Scene(createMenu(), SCREEN_WIDTH, SCREEN_HEIGHT);
        gameScene = new Scene(gameRoot, SCREEN_WIDTH, SCREEN_HEIGHT);
        gameScene.setFill(Color.BLACK);


        AnimationTimer gameLoop = new AnimationTimer() {
            private long lastUpdate = 0;

            @Override
            public void handle(long now) {
                if (lastUpdate == 0) {
                    lastUpdate = now;
                    return;
                }
                lastUpdate = now;

                if (gameRunning && !gameOver) {
                    updateGame();
                    renderGame();
                }
            }
        };

        primaryStage.setScene(menuScene);
        primaryStage.setResizable(false);
        primaryStage.show();
        gameLoop.start();
    }

    private void initializeNewGame(int mode) {
        score = 0;
        lives1 = numLivesInitial;
        lives2 = numLivesInitial;
        lives = (player2 != null) ? lives1 + lives2 : lives1;
        currentLevel = 1;
        scoreForNextLevel = 200;
        scoreForBoss = 150;
        bossExists = false;
        currentBoss = null;
        gameOver = false;

        if (gameObjects == null) {
        gameObjects = new ArrayList<>();
    }
        gameObjects.clear();
        player1 = new Player(1, SCREEN_WIDTH / 2.0, SCREEN_HEIGHT - 50);
        gameObjects.add(player1);
        if(mode == 2) {
            player2 = new Player(2, SCREEN_WIDTH / 1.5, SCREEN_HEIGHT - 50);
            gameObjects.add(player2);
            player2livesLabel.setVisible(true);
            player2livesLabel.setVisible(false);

            aiController = new AIController();
        } else {
            player2 = null;
            aiController = null;
        }

        gameObjects.add(new Enemy(100, 50, gameObjects));
        lastEnemySpawnTime = System.currentTimeMillis();
        lastPowerUpSpawnTime = System.currentTimeMillis();

        updateUI();
        initEventHandlers(gameScene);

        HBox topBar = (HBox) ((VBox) uiOverlay).getChildren().get(0);
        if (mode == 2 && !topBar.getChildren().contains(player2livesLabel)) {
            topBar.getChildren().add(2, player2livesLabel); // Thêm vào vị trí thứ 3
        } else if (mode == 1 && topBar.getChildren().contains(player2livesLabel)) {
            topBar.getChildren().remove(player2livesLabel);
        }
    }

    private void updateGame() {
        List<GameObject> newObjects = new ArrayList<>();

        if (player1 != null) player1.update();
        if (player2 != null) player2.update();

        if (aiController != null && player2 != null) {
        aiController.update(player2, gameObjects, getEnemyBullets()); // 🔥 Đảm bảo danh sách đúng kiểu EnemyBullet
}




        // Duyệt danh sách bằng bản sao để tránh lỗi ConcurrentModificationException
        for (GameObject obj : new ArrayList<>(gameObjects)) {
            obj.update();
            if (obj instanceof Enemy) {
                ((Enemy) obj).shoot(newObjects);
            }
        }

        // Thêm các viên đạn vào gameObjects sau khi vòng lặp kết thúc
        gameObjects.addAll(newObjects);

        // Kiểm tra va chạm và trạng thái game
        spawnEnemy();
        spawnPowerUp();
        checkCollisions();
        checkEnemiesReachingBottom();

        gameObjects.removeIf(GameObject::isDead);

        // Kiểm tra nếu Boss bị tiêu diệt
        if (currentBoss != null && currentBoss.isDead()) {
            score += 50;
            showTempMessage("BOSS DEFEATED!", SCREEN_WIDTH / 2.0, SCREEN_HEIGHT / 2.0, 3);
            currentBoss = null;
            bossExists = false;
        }

        // Cập nhật level nếu điểm số đủ
        if (score >= scoreForNextLevel && currentLevel < 10) {
            currentLevel++;
            scoreForNextLevel += 100 * currentLevel;
            scoreForBoss += (scoreForNextLevel - 50);
            enemySpawnCooldown = Math.max(150, enemySpawnCooldown - 50);
            Enemy.SPEED = Math.min(3, Enemy.SPEED + 0.2);
            BossEnemy.SPEED = Math.min(2, BossEnemy.SPEED + 0.1);
            showTempMessage("LEVEL " + currentLevel + "!", SCREEN_WIDTH / 2.0, SCREEN_HEIGHT / 2.0, 3);
            updateUI();
        }

        // Cập nhật UI
        updateUI();

        // Kiểm tra nếu người chơi hết mạng
        if (lives <= 0 && !gameOver) {
            resetGame();
        }
    }

    private void renderGame() {
        gc.setFill(Color.BLACK);
        gc.fillRect(0, 0, SCREEN_WIDTH, SCREEN_HEIGHT);
        for (GameObject obj : gameObjects) {
            obj.render(gc);
        }
    }

    private void updateUI() {
        scoreLabel.setText("Score: " + score);
        levelLabel.setText("Level: " + currentLevel);
        player1livesLabel.setText("P1 Lives: " + lives1);
        player1livesLabel.setAlignment(Pos.CENTER_LEFT);
        if (player2 != null) {
        player2livesLabel.setText("P2 Lives: " + lives2);
        player2livesLabel.setVisible(true);
        player2livesLabel.setAlignment(Pos.CENTER_RIGHT);
        } else {
            player2livesLabel.setVisible(false);
        }
        
    }

    private void spawnEnemy() {
        long currentTime = System.currentTimeMillis();
        if (bossExists)
            return;

        if (score >= scoreForBoss && !bossExists && currentBoss == null) {
            spawnBossEnemy();
            bossExists = true;
            return;
        }

        if (currentTime - lastEnemySpawnTime > enemySpawnCooldown) {
            double spawnX = random.nextDouble() * (SCREEN_WIDTH - Enemy.WIDTH) + Enemy.WIDTH / 2.0;
            double spawnY = -Enemy.HEIGHT / 2.0;
            gameObjects.add(new Enemy(spawnX, spawnY));
            lastEnemySpawnTime = currentTime;
        }
    }

    private void spawnPowerUp() {
        long currentTime = System.currentTimeMillis();
        if (currentTime - lastPowerUpSpawnTime > powerUpSpawnCooldown) {
            if (random.nextDouble() < 0.15) {
                double spawnX = random.nextDouble() * (SCREEN_WIDTH - PowerUp.WIDTH) + PowerUp.WIDTH / 2.0;
                double spawnY = -PowerUp.HEIGHT / 2.0;
                gameObjects.add(new PowerUp(spawnX, spawnY));
            }
            lastPowerUpSpawnTime = currentTime;
        }
    }

    private void spawnBossEnemy() {
        if (!bossExists && currentBoss == null) {
            currentBoss = new BossEnemy(SCREEN_WIDTH / 2.0, BossEnemy.HEIGHT / 2.0);
            gameObjects.add(currentBoss);
            bossExists = true;
            showTempMessage("BOSS APPEARS!", SCREEN_WIDTH / 2.0, SCREEN_HEIGHT / 2.0, 3);
        }
    }

    private void checkCollisions() {
        List<Bullet> playerBullets = gameObjects.stream()
                .filter(obj -> obj instanceof Bullet && ((Bullet) obj).isPlayerBullet())
                .map(obj -> (Bullet) obj)
                .collect(Collectors.toList());

        List<EnemyBullet> enemyBullets = gameObjects.stream()
                .filter(obj -> obj instanceof EnemyBullet)
                .map(obj -> (EnemyBullet) obj)
                .collect(Collectors.toList());

        List<Enemy> enemies = gameObjects.stream()
                .filter(obj -> obj instanceof Enemy && !(obj instanceof BossEnemy))
                .map(obj -> (Enemy) obj)
                .collect(Collectors.toList());

        List<PowerUp> powerUps = gameObjects.stream()
                .filter(obj -> obj instanceof PowerUp)
                .map(obj -> (PowerUp) obj)
                .collect(Collectors.toList());

        for (Bullet bullet : playerBullets) {
            for (Enemy enemy : enemies) {
                if (!bullet.isDead() && !enemy.isDead() && bullet.collidesWith(enemy)) {
                    bullet.setDead(true);
                    enemy.setDead(true);
                    score += 10;
                }
            }
            if (currentBoss != null && !bullet.isDead() && !currentBoss.isDead() && bullet.collidesWith(currentBoss)) {
                bullet.setDead(true);
                currentBoss.takeDamage(10);
            }
        }
        checkPlayerCollision(player1, enemyBullets, enemies, powerUps);
    if (player2 != null) {
        checkPlayerCollision(player2, enemyBullets, enemies, powerUps);
    }  
    }
    private void checkPlayerCollision(Player player, List<EnemyBullet> enemyBullets, List<Enemy> enemies, List<PowerUp> powerUps) {
    if (!player.isDead()) {
        for (EnemyBullet enemyBullet : enemyBullets) {
            if (!enemyBullet.isDead() && enemyBullet.collidesWith(player)) {
                enemyBullet.setDead(true);
                playerHit(player);
            }
        }

        for (Enemy enemy : enemies) {
            if (!enemy.isDead() && player.collidesWith(enemy)) {
                enemy.setDead(true);
                playerHit(player);
            }
        }

        if (currentBoss != null && !currentBoss.isDead() && player.collidesWith(currentBoss)) {
            playerHit(player);
        }

        for (PowerUp powerUp : powerUps) {
            if (!powerUp.isDead() && player.collidesWith(powerUp)) {
                powerUp.setDead(true);
                applyPowerUp(player);
                score += 15;
            }
        }
    }
}

    private void playerHit(Player player) {
    if (player.isDead()) return;

    // Giảm mạng của từng người chơi riêng biệt
    if (player == player1) {
        lives1--;
        showTempMessage("Player 1 HIT! Lives: " + lives1, SCREEN_WIDTH / 4, SCREEN_HEIGHT - 100, 2);
    } else if (player == player2) {
        lives2--;
        showTempMessage("Player 2 HIT! Lives: " + lives2, SCREEN_WIDTH * 3 / 4, SCREEN_HEIGHT - 100, 2);
    }

    // Giảm tổng số mạng (có thể dùng để xác định khi game kết thúc)
    lives--;

    // Cập nhật UI để hiển thị số mạng đúng
    updateUI();

    // Kiểm tra nếu người chơi hết mạng
    if (lives1 <= 0) player1.setDead(true);
    if (lives2 <= 0) player2.setDead(true);
}

    private void applyPowerUp(Player player) {
        if (player == player1 && lives1 < 10) {
        lives1++;
        lives++;
        showTempMessage("POWER UP! +1 LIFE", SCREEN_WIDTH / 4, SCREEN_HEIGHT / 2.0, 2);
    } else if (player == player2 && lives2 < 10) {
        lives2++;
        lives++;
        showTempMessage("POWER UP! +1 LIFE", SCREEN_WIDTH * 3 / 4, SCREEN_HEIGHT / 2.0, 2);
    }
    updateUI();
    }

    private void checkEnemiesReachingBottom() {
        for (GameObject obj : gameObjects) {
            if (obj instanceof Enemy && !obj.isDead()) {
                if (obj.getY() + obj.getHeight() / 2.0 >= SCREEN_HEIGHT && !(obj instanceof BossEnemy)) {
                    if (!gameOver) {
                        lives--;
                        showTempMessage("ENEMY REACHED BOTTOM! Lives: " + lives, SCREEN_WIDTH / 2.0,
                                SCREEN_HEIGHT - 100, 2);
                        updateUI();
                        if (lives <= 0) {
                            resetGame();
                            return;
                        }
                    }
                    obj.setDead(true);
                }
            }
        }
    }

    private void showLosingScreen() {
        VBox losingPane = new VBox(20);
        losingPane.setAlignment(Pos.CENTER);
        losingPane.setPadding(new Insets(50));
        losingPane.setStyle("-fx-background-color: rgba(0, 0, 0, 0.8);");

        Label gameOverLabel = new Label("GAME OVER");
        gameOverLabel.setFont(Font.font("Verdana", 50));
        gameOverLabel.setTextFill(Color.RED);

        Label finalScoreLabel = new Label("Final Score: " + score);
        finalScoreLabel.setFont(Font.font("Verdana", 30));
        finalScoreLabel.setTextFill(Color.WHITE);

        Button restartButton = new Button("Restart");
        restartButton.setFont(Font.font("Verdana", 20));
        restartButton.setOnAction(e -> restartGame());

        Button menuButton = new Button("Main Menu");
        menuButton.setFont(Font.font("Verdana", 20));
        menuButton.setOnAction(e -> primaryStage.setScene(menuScene));

        losingPane.getChildren().addAll(gameOverLabel, finalScoreLabel, restartButton, menuButton);

        // bind layoutX/Y để luôn ở giữa:
        losingPane.layoutXProperty().bind(
        gameRoot.widthProperty()
                .subtract(losingPane.widthProperty())
                .divide(2)
        );
        losingPane.layoutYProperty().bind(
        gameRoot.heightProperty()
                .subtract(losingPane.heightProperty())
                .divide(2)
        );
        gameRoot.getChildren()
                .removeIf(node -> node instanceof VBox && ((VBox) node).getChildren().contains(gameOverLabel));
        gameRoot.getChildren().add(losingPane);
    }

    private void restartGame() {
        gameRunning = true;
        gameOver = false;
        gameRoot.getChildren().removeIf(
                node -> node instanceof VBox && ((VBox) node).getChildren().size() > 2 && !node.equals(uiOverlay));
        initializeNewGame(currentMode);
        primaryStage.setScene(gameScene);
    }

    private void resetGame() {
        gameRunning = false;
        gameOver = true;
        updateUI();
        showLosingScreen();
    }

    private void initEventHandlers(Scene scene) {
        scene.setOnKeyPressed(event -> {
            if (!gameRunning)
                return;
            if(player1 != null && !player1.isDead()) {
                if (event.getCode() == KeyCode.A) {
                    player1.setMoveLeft(true);
                } else if (event.getCode() == KeyCode.D) {
                    player1.setMoveRight(true);
                } else if (event.getCode() == KeyCode.W) {
                    player1.setMoveForward(true);
                } else if (event.getCode() == KeyCode.S) {
                    player1.setMoveBackward(true);
                } else if (event.getCode() == KeyCode.SPACE) {
                    player1.shoot(gameObjects);
                }
            }

            if (player2 != null && !player2.isDead()) {
                if (event.getCode() == KeyCode.LEFT) {
                    player2.setMoveLeft(true);
                } else if (event.getCode() == KeyCode.RIGHT) {
                    player2.setMoveRight(true);
                } else if (event.getCode() == KeyCode.UP) {
                    player2.setMoveForward(true);
                } else if (event.getCode() == KeyCode.DOWN) {
                    player2.setMoveBackward(true);
                } else if (event.getCode() == KeyCode.ENTER) {
                    player2.shoot(gameObjects);
                }
            }
        });

        scene.setOnKeyReleased(event -> {
            if (player1 != null && !player1.isDead()) {
                if (event.getCode() == KeyCode.A) {
                    player1.setMoveLeft(false);
                } else if (event.getCode() == KeyCode.D) {
                    player1.setMoveRight(false);
                } else if (event.getCode() == KeyCode.W) {
                    player1.setMoveForward(false);
                } else if (event.getCode() == KeyCode.S) {
                    player1.setMoveBackward(false);
                }
            }

            if (player2 != null && !player2.isDead()) {
                if (event.getCode() == KeyCode.LEFT) {
                    player2.setMoveLeft(false);
                } else if (event.getCode() == KeyCode.RIGHT) {
                    player2.setMoveRight(false);
                } else if (event.getCode() == KeyCode.UP) {
                    player2.setMoveForward(false);
                } else if (event.getCode() == KeyCode.DOWN) {
                    player2.setMoveBackward(false);
                }
            }
        });
    }

    private StackPane createMenu() {
        VBox menuElementsLayout = new VBox(25);
        menuElementsLayout.setAlignment(Pos.CENTER);

        Label titleLabel = new Label("Welcome to\nSpace Shooter!");
        titleLabel.setFont(Font.font("Arial Black", FontWeight.EXTRA_BOLD, 56));
        titleLabel.setTextFill(Color.rgb(160, 255, 255));

        DropShadow titleGlow = new DropShadow();
        titleGlow.setColor(Color.rgb(0, 255, 255, 0.9));
        titleGlow.setRadius(25);
        titleGlow.setSpread(0.3);
        titleGlow.setBlurType(BlurType.GAUSSIAN);
        titleLabel.setEffect(titleGlow);

        titleLabel.setAlignment(Pos.CENTER);
        titleLabel.setTextAlignment(TextAlignment.CENTER);

        singlePlayerButton = new Button("1 Player Mode");
        styleMenuButton(singlePlayerButton);
        twoPlayerButton = new Button("2 Player Mode");
        styleMenuButton(twoPlayerButton);

        singlePlayerButton.setOnAction(e -> startGame(1));
        twoPlayerButton.setOnAction(e -> startGame(2));


        Button instructionsButton = new Button("INSTRUCTIONS");
        styleMenuButton(instructionsButton);
        instructionsButton.setOnAction(e -> showInstructions());

        Button exitButton = new Button("QUIT");
        styleMenuButton(exitButton);
        exitButton.setOnAction(e -> primaryStage.close());

        menuElementsLayout.getChildren().addAll(titleLabel, singlePlayerButton, twoPlayerButton, instructionsButton, exitButton);

        StackPane menuRootStackPane = new StackPane();
        menuRootStackPane.setStyle("-fx-background-color: #0D47A1;");
        menuRootStackPane.getChildren().add(menuElementsLayout);

        return menuRootStackPane;
    }

    private void styleMenuButton(Button button) {
        button.setFont(Font.font("Arial", FontWeight.BOLD, 22));

        String baseStyle = "-fx-background-radius: 50em; " +
                "-fx-border-radius: 50em; " +
                "-fx-border-width: 2px; " +
                "-fx-padding: 12 40 12 40; " +
                "-fx-text-fill: white; ";

        String normalBg = "-fx-background-color: linear-gradient(to right, #5e35b1, #1e88e5);";
        String hoverBg = "-fx-background-color: linear-gradient(to right, #7e57c2, #42a5f5);";

        String normalBorder = "-fx-border-color: white;";
        String hoverBorder = "-fx-border-color: #e3f2fd;";

        button.setStyle(baseStyle + normalBg + normalBorder);
        button.setOnMouseEntered(e -> button.setStyle(baseStyle + hoverBg + hoverBorder));
        button.setOnMouseExited(e -> button.setStyle(baseStyle + normalBg + normalBorder));
    }

    private void showInstructions() {
        VBox instructionsPane = new VBox(20);
        instructionsPane.setAlignment(Pos.CENTER);
        instructionsPane.setPadding(new Insets(50));
        instructionsPane.setStyle(
                "-fx-background-color: rgba(13, 71, 161, 0.9); -fx-border-color: #90caf9; -fx-border-width: 2; -fx-background-radius: 15; -fx-border-radius: 15;");

        Label title = new Label("Space Shooter Instructions");
        title.setFont(Font.font("Arial Black", FontWeight.BOLD, 30));
        title.setTextFill(Color.LIGHTCYAN);

        Label controls = new Label(
                "Use the A, W, S, and D keys or the arrow keys to move your spaceship.\n" +
                        "Press SPACE to shoot bullets and destroy the enemies.\n" +
                        "If an enemy reaches the bottom of the screen, you lose a life.\n" +
                        "The game resets if you lose all lives.\n" +
                        "Collect power-ups to increase your score.\n" +
                        "Defeat the boss enemy to level up and increase the difficulty.\n\n" +
                        "Good luck and have fun!");
        controls.setFont(Font.font("Arial", 18));
        controls.setTextFill(Color.WHITE);
        controls.setTextAlignment(TextAlignment.CENTER);

        Button backButton = new Button("BACK");
        styleMenuButton(backButton);
        backButton.setOnAction(e -> {
            if (menuScene.getRoot() instanceof StackPane) {
                ((StackPane) menuScene.getRoot()).getChildren().remove(instructionsPane);
            }
        });
        instructionsPane.getChildren().addAll(title, controls, backButton);

        StackPane menuRoot = (StackPane) menuScene.getRoot();
        if (!menuRoot.getChildren().contains(instructionsPane)) {
            menuRoot.getChildren().add(instructionsPane);
        }
    }

    private void showTempMessage(String message, double x, double y, double durationSeconds) {
        tempMessageLabel.setText(message);
        tempMessageLabel.setVisible(true);

        if (tempMessageTimer != null) {
            tempMessageTimer.stop();
        }

        tempMessageTimer = new AnimationTimer() {
            private long startTime = -1;

            @Override
            public void handle(long now) {
                if (startTime == -1)
                    startTime = now;
                if (now - startTime > durationSeconds * 1_000_000_000) {
                    tempMessageLabel.setVisible(false);
                    tempMessageLabel.setText("");
                    this.stop();
                    tempMessageTimer = null;
                }
            }
        };
        tempMessageTimer.start();
    }

    private void startGame(int mode) {
        initializeNewGame(mode);
        currentMode = mode;
        gameRunning = true;
        gameOver = false;
        gameObjects.clear();

        player1 = new Player(1, SCREEN_WIDTH / 2, SCREEN_HEIGHT - 50);
        gameObjects.add(player1);
        if (mode == 2) { // Nếu chọn chế độ 2 người chơi
        player2 = new Player(2, SCREEN_WIDTH * 2 / 3, SCREEN_HEIGHT - 50);
        gameObjects.add(player2);
    } else {
        player2 = null; // Đảm bảo `player2` không bị sử dụng sai
    }

    updateUI();
    initEventHandlers(gameScene);
    primaryStage.setScene(gameScene);
    }
    private List<EnemyBullet> getEnemyBullets() {
    return gameObjects.stream()
            .filter(obj -> obj instanceof EnemyBullet) // ✅ Lọc đúng lớp EnemyBullet
            .map(obj -> (EnemyBullet) obj)
            .collect(Collectors.toList());
}
}