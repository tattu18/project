package uet.oop.spaceshootergamejavafx.entities;

import java.util.List;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.image.Image;

/**
 * Skeleton for Enemy. Students must implement movement, rendering,
 * and death state without viewing the original implementation.
 */
public class Enemy extends GameObject {

    // Hitbox dimensions
    protected static final int WIDTH = 30;
    protected static final int HEIGHT = 30;

    // Movement speed
    public static double SPEED = 1.5;
    private List<GameObject> gameObjects;

    // Flag to indicate if enemy should be removed
    private boolean dead;
    private Image sprite;

    private static final double GAME_AREA_HEIGHT = SpaceShooter.SCREEN_HEIGHT;

    private long lastShootTime;
    private static final long SHOOT_INTERVAL = 2000; // 2 seconds

    private static final int MAX_BULLETS = 5;
    private int bulletsFired = 0;

    /**
     * Constructs an Enemy at the given coordinates.
     * 
     * @param x initial X position
     * @param y initial Y position
     */
    public Enemy(double x, double y) {
        super(x, y, WIDTH, HEIGHT);
        this.dead = false;
        this.lastShootTime = System.currentTimeMillis();
        sprite = new Image(getClass().getResourceAsStream("enemy.png"));
    }

    public Enemy(double x, double y, List<GameObject> gameObjects) {
        super(x, y, WIDTH, HEIGHT);
        this.dead = false;
        this.lastShootTime = System.currentTimeMillis();
        this.gameObjects = gameObjects;
        sprite = new Image(getClass().getResourceAsStream("enemy.png"));
    }

    /**
     * Updates enemy position each frame.
     */
    @Override
    public void update() {
        if (isDead()) {
            return;
        }
        this.y += SPEED;

        if ((this.y + this.getHeight() / 2.0) >= GAME_AREA_HEIGHT) {
            setDead(true);
            bulletsFired = 0;
        }
    }

    public void shoot(List<GameObject> newObjects) {
        long now = System.currentTimeMillis();
        if (bulletsFired < MAX_BULLETS && now - lastShootTime >= SHOOT_INTERVAL) {
            newObjects.add(new EnemyBullet(x, y + HEIGHT / 2, false));
            lastShootTime = now;
            bulletsFired++;
        }
    }

    /**
     * Renders the enemy on the canvas.
     * 
     * @param gc the GraphicsContext to draw on
     */
    @Override
    public void render(GraphicsContext gc) {
        double topLeftX = this.x - this.getWidth() / 2.0;
        double topLeftY = this.y - this.getHeight() / 2.0;
        gc.drawImage(sprite, topLeftX, topLeftY, this.getWidth(), this.getHeight());
    }

    /**
     * Returns the current width of the enemy.
     * 
     * @return WIDTH
     */
    @Override
    public double getWidth() {
        return WIDTH;
    }

    /**
     * Returns the current height of the enemy.
     * 
     * @return HEIGHT
     */
    @Override
    public double getHeight() {
        return HEIGHT;
    }

    /**
     * Marks this enemy as dead (to be removed).
     * 
     * @param dead true if enemy should be removed
     */
    public void setDead(boolean dead) {
        this.dead = dead;
    }

    /**
     * Checks if this enemy is dead.
     * 
     * @return true if dead, false otherwise
     */
    @Override
    public boolean isDead() {
        return dead;
    }

    public int getScoreValue() {
        return 20;
    }
   
}
