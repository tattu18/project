package uet.oop.spaceshootergamejavafx.entities;

import javafx.geometry.Bounds;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.shape.Rectangle;

import java.util.ArrayList;
import java.util.List;

/**
 * Player class representing the main character of the space shooter game.
 */
public class Player extends GameObject {

    private int playerId;

    // Hitbox dimensions
    private static final int WIDTH = 50;
    private static final int HEIGHT = 50;

    // Movement speed
    private static final double SPEED = 3.0;

    // Movement flags
    private boolean moveLeft;
    private boolean moveRight;
    private boolean moveForward;
    private boolean moveBackward;

    private long lastShotTime = 0;
    private static final long SHOOT_COOLDOWN = 200; // Cooldown thời gian giữa các lần bắn

    // Player health
    private int health;
    private static final int INITIAL_HEALTH = 3;

    private static final double GAME_AREA_MIN_X = 0;
    private static final double GAME_AREA_MIN_Y = 0;

    // State flag for removal
    private boolean dead;
    private Image sprite;


    /**
     * Constructs a Player at the given position.
     * 
     * @param x initial X position
     * @param y initial Y position
     */
    public Player(int playerId, double x, double y) {
        super(x, y, WIDTH, HEIGHT);
        this.playerId = playerId;
        this.health = INITIAL_HEALTH;
        this.dead = false;
        this.moveLeft = false;
        this.moveRight = false;
        this.moveForward = false;
        this.moveBackward = false;
        sprite = new Image(getClass().getResourceAsStream("player.png"));
    }

    /** Returns the width of the player. */
    @Override
    public double getWidth() {
        return WIDTH;
    }

    /** Returns the height of the player. */
    @Override
    public double getHeight() {
        return HEIGHT;
    }

    /** Returns current health of the player. */
    public int getHealth() {
        return health;
    }

    /** Sets player's health. */
    public void setHealth(int health) {
        this.health = Math.max(health, 0);
        if (this.health <= 0) {
            this.setDead(true);
        }
    }

    /** Updates player position based on movement flags. */
    @Override
    public void update() {
        if (isDead()) {
            return;
        }

        double dx = 0;
        double dy = 0;

        if (moveLeft)
            dx -= SPEED;
        if (moveRight)
            dx += SPEED;
        if (moveForward)
            dy -= SPEED;
        if (moveBackward)
            dy += SPEED;

        this.x = Math.max(GAME_AREA_MIN_X, Math.min(this.x + dx, SpaceShooter.SCREEN_WIDTH - WIDTH));
        this.y = Math.max(GAME_AREA_MIN_Y, Math.min(this.y + dy, SpaceShooter.SCREEN_HEIGHT - HEIGHT));

        if (this.health <= 0) {
            this.setDead(true);
        }
    }


    /** Renders the player on the canvas. */
    @Override
    public void render(GraphicsContext gc) {
        gc.drawImage(sprite, this.x, this.y, getWidth(), getHeight());
    }

    /** Sets movement flags. */
    public void setMoveLeft(boolean moveLeft) {
        this.moveLeft = moveLeft;
    }

    public void setMoveRight(boolean moveRight) {
        this.moveRight = moveRight;
    }

    public void setMoveForward(boolean moveForward) {
        this.moveForward = moveForward;
    }

    public void setMoveBackward(boolean moveBackward) {
        this.moveBackward = moveBackward;
    }

    /** Shoots a bullet from the player. */
    public void shoot(List<GameObject> bullets) {
        long currentTime = System.currentTimeMillis();
        if (currentTime - lastShotTime > SHOOT_COOLDOWN) {
            bullets.add(new Bullet(x + WIDTH / 2, y, true)); // Điều chỉnh vị trí viên đạn
            lastShotTime = currentTime;
        }
    }

    /** Marks the player as dead. */
    public void setDead(boolean dead) {
        this.dead = dead;
    }

    /** Checks whether the player is dead. */
    @Override
    public boolean isDead() {
        return dead;
    }
    public void handleKeyPress(KeyEvent event, List<GameObject> bullets) {
    if (playerId == 1) {
        if (event.getCode() == KeyCode.W) moveForward = true;
        if (event.getCode() == KeyCode.S) moveBackward = true;
        if (event.getCode() == KeyCode.A) moveLeft = true;
        if (event.getCode() == KeyCode.D) moveRight = true;
        if (event.getCode() == KeyCode.SPACE) shoot(bullets);
    } else if (playerId == 2) {
        if (event.getCode() == KeyCode.UP) moveForward = true;
        if (event.getCode() == KeyCode.DOWN) moveBackward = true;
        if (event.getCode() == KeyCode.LEFT) moveLeft = true;
        if (event.getCode() == KeyCode.RIGHT) moveRight = true;
        if (event.getCode() == KeyCode.ENTER) shoot(bullets);
    }
}
public void handleKeyRelease(KeyEvent event) {
    if (playerId == 1) {
        if (event.getCode() == KeyCode.W) moveForward = false;
        if (event.getCode() == KeyCode.S) moveBackward = false;
        if (event.getCode() == KeyCode.A) moveLeft = false;
        if (event.getCode() == KeyCode.D) moveRight = false;
    } else if (playerId == 2) {
        if (event.getCode() == KeyCode.UP) moveForward = false;
        if (event.getCode() == KeyCode.DOWN) moveBackward = false;
        if (event.getCode() == KeyCode.LEFT) moveLeft = false;
        if (event.getCode() == KeyCode.RIGHT) moveRight = false;
    }
}
@Override
    public Bounds getBounds() {
        return new Rectangle((int)x + 10, (int)y + 10, 40, 30).getBoundsInLocal();
    }
}