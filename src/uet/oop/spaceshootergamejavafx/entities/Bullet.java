package uet.oop.spaceshootergamejavafx.entities;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.image.Image;

/**
 * Skeleton for Bullet. Students must implement movement,
 * rendering, and state management.
 */
public class Bullet extends GameObject {

    // Width and height of the bullet
    public static final int WIDTH = 4;
    public static final int HEIGHT = 15;

    // Movement speed of the bullet
    private static final double SPEED = 7;

    // Flag to indicate if bullet should be removed
    private boolean dead;
    private Image sprite;

    private boolean playerBullet;



    private static final double TOP_BOUNDARY = 0;

    /**
     * Constructs a Bullet at the given position.
     * 
     * @param x initial X position
     * @param y initial Y position
     */
    public Bullet(double x, double y, boolean playerBullet) {
        super(x, y, WIDTH, HEIGHT);
        this.dead = false;
        sprite = new Image(getClass().getResourceAsStream("bullet2.png"));
        this.playerBullet = playerBullet;

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
        this.y -= SPEED;

        if (this.y + this.getHeight() < TOP_BOUNDARY) {
            this.setDead(true);
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
        gc.drawImage(sprite, this.x, this.y, this.getWidth(), this.getHeight());
        // TODO: draw bullet (e.g., filled rectangle or sprite)
    }

    /**
     * Returns current width of the bullet.
     * 
     * @return WIDTH
     */
    @Override
    public double getWidth() {
        // TODO: return bullet width
        return WIDTH;
    }

    /**
     * Returns current height of the bullet.
     * 
     * @return HEIGHT
     */
    @Override
    public double getHeight() {
        // TODO: return bullet height
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
        // TODO: return dead flag
        return dead;
    }
    public boolean isPlayerBullet() {
        return playerBullet;
    }


}
