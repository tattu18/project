package uet.oop.spaceshootergamejavafx.entities;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.image.Image;

/**
 * Skeleton for EnemyBullet. Students must implement movement,
 * rendering, and state management.
 */
public class EnemyBullet extends GameObject {

    // Dimensions of the enemy bullet
    public static final int WIDTH = 4;
    public static final int HEIGHT = 20;

    // Movement speed of the bullet
    private static final double SPEED = 2.5;

    // Flag indicating whether the bullet should be removed
    private boolean dead;
    private Image sprite;

    private boolean enemyBullet;

    private static final double GAME_AREA_HEIGHT = SpaceShooter.SCREEN_HEIGHT;

    /**
     * Constructs an EnemyBullet at the given position.
     * 
     * @param x initial X position
     * @param y initial Y position
     */
    public EnemyBullet(double x, double y, boolean enemyBullet) {
        super(x, y, WIDTH, HEIGHT);
        this.dead = false;
        sprite = new Image(getClass().getResourceAsStream("enemybullet1.png"));
        this.enemyBullet = enemyBullet;

        // TODO: initialize dead flag if needed
    }

    /**
     * Updates bullet position each frame.
     */
    @Override
    public void update() {
        if (isDead()) {
            return;
        }
        this.y += SPEED;

        if (this.y - HEIGHT / 2.0 >= GAME_AREA_HEIGHT) {
            setDead(true);
        }

        // TODO: move bullet vertically by SPEED
    }

    /**
     * Renders the bullet on the canvas.
     * 
     * @param gc the GraphicsContext to draw on
     */
    @Override
    public void render(GraphicsContext gc) {
        double topLeftX = this.x - this.getWidth() / 2.0;
        double topLeftY = this.y - this.getHeight() / 2.0;
        gc.drawImage(sprite, topLeftX, topLeftY, this.getWidth(), this.getHeight());
        // TODO: draw bullet (e.g., filled rectangle or sprite)
    }

    /**
     * Returns the width of the bullet.
     * 
     * @return WIDTH
     */
    @Override
    public double getWidth() {
        // TODO: return width
        return WIDTH;
    }

    /**
     * Returns the height of the bullet.
     * 
     * @return HEIGHT
     */
    @Override
    public double getHeight() {
        // TODO: return height
        return HEIGHT;
    }

    /**
     * Marks this bullet as dead (to be removed).
     * 
     * @param dead true if bullet should be removed
     */
    public void setDead(boolean dead) {
        this.dead = dead;
        // TODO: update dead flag
    }

    /**
     * Checks if this bullet is dead.
     * 
     * @return true if dead, false otherwise
     */
    @Override
    public boolean isDead() {
        return dead;
    }
    public boolean isEnemyBullet() {
         return this instanceof EnemyBullet;
    }
}
