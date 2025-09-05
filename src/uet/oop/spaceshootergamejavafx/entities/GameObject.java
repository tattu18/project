package uet.oop.spaceshootergamejavafx.entities;

import javafx.scene.canvas.GraphicsContext;
import javafx.geometry.Bounds;
import javafx.scene.shape.Rectangle;

/**
 * Skeleton for GameObject. Base class for all game objects.
 * Subclasses must implement the abstract methods below.
 */
public abstract class GameObject {
    // Position and size
    protected double x;
    protected double y;
    protected double width;
    protected double height;

    protected boolean dead = false;

    // ... constructor và các phương thức khác giữ nguyên

    public void setDead(boolean dead) {
        this.dead = dead;
    }

    /**
     * Constructs a GameObject at the specified position with dimensions.
     * 
     * @param x      initial X position
     * @param y      initial Y position
     * @param width  object width
     * @param height object height
     */
    public GameObject(double x, double y, double width, double height) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
    }

    /**
     * Updates the game object's state each frame.
     */
    public abstract void update();

    /**
     * Renders the game object on the canvas.
     * 
     * @param gc graphics context
     */
    public abstract void render(GraphicsContext gc);

    /**
     * Checks whether this object should be removed from the game.
     * 
     * @return true if dead/removed
     */
    public abstract boolean isDead();

    /**
     * Returns the current X coordinate.
     * 
     * @return x position
     */
    public double getX() {
        return x;
    }

    public void setX(double x) {
        this.x = x;
    }

    /**
     * Returns the current Y coordinate.
     * 
     * @return y position
     */
    public double getY() {
        return y;
    }

    public void setY(double y) {
        this.y = y;
    }

    /**
     * Returns the bounding box for collision detection.
     * 
     * @return bounds of this object
     */
    public Bounds getBounds() {
        return new Rectangle(
                x - width / 2,
                y - height / 2,
                width,
                height).getBoundsInLocal();
    }

    /**
     * Returns the width of the object.
     * 
     * @return width
     */
    public double getWidth() {
        return width;
    }

    /**
     * Returns the height of the object.
     * 
     * @return height
     */
    public double getHeight() {
        return height;
    }

    /**
     * Checks collision with another game object.
     * 
     * @param other the other game object to check collision with
     * @return true if collision occurs
     */
    public boolean collidesWith(GameObject other) {
        if (other == null || this.isDead() || other.isDead()) {
            return false;
        }

        return this.getBounds().intersects(other.getBounds());
    }
}