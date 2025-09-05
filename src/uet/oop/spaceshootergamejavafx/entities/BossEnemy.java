package uet.oop.spaceshootergamejavafx.entities;

import javafx.geometry.Bounds;
import javafx.scene.canvas.GraphicsContext;

import java.util.ArrayList;
import java.util.List;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

import java.util.Random;
import javafx.scene.image.Image;

public class BossEnemy extends Enemy {

    private double initialY;

    private int health;
    public static final int INITIAL_BOSS_HEALTH = 1000;

    public static final int BOSS_WIDTH = 150;
    public static final int BOSS_HEIGHT = 150;

    private double horizontalSpeed;
    private static final double INITIAL_HORIZONTAL_SPEED = 2.0;
    private static final double VERTICAL_SPEED_BOSS = 0.5;

    private long lastShotTimeMillis;
    private static final long SHOT_COOLDOWN_MS = 1000; // GIẢM TỐC ĐỘ BẮN
    private List<EnemyBullet> activeBullets = new ArrayList<>();
    private static final int MAX_BULLETS = 1;

    private Random random = new Random();

    private static final double GAME_AREA_MIN_X = 0;

    private static final double INITIAL_BOSS_Y = 100;

    private Image sprite;

    public BossEnemy(double x, double y) {
        super(x, y);
        this.width = BOSS_WIDTH;
        this.height = BOSS_HEIGHT;
        this.health = INITIAL_BOSS_HEALTH;
        this.horizontalSpeed = INITIAL_HORIZONTAL_SPEED;
        this.lastShotTimeMillis = System.currentTimeMillis();
        this.initialY = INITIAL_BOSS_Y; // đặt vị trí ban đầu hợp lý
        this.y = INITIAL_BOSS_Y;        // tránh che chữ
        sprite = new Image(getClass().getResourceAsStream("boss.png"));
    }

    @Override
    public double getWidth() {
        return BOSS_WIDTH;
    }

    @Override
    public double getHeight() {
        return BOSS_HEIGHT;
    }

    // Thêm các biến sau vào class BossEnemy (nếu chưa có)
private boolean phase50Triggered = false;
private boolean phase25Triggered = false;
private boolean isInSpecialPhase = false;
private boolean goingUpAfterSin = false;
private double sinTime = 0;
private double targetY = SpaceShooter.SCREEN_HEIGHT - 100; // gần đáy

public void update(List<GameObject> gameObjects) {
    if (isDead()) return;

    // Xử lý phase đặc biệt khi máu ≤ 50% hoặc ≤ 25%
    if (!phase50Triggered && health <= 0.5 * INITIAL_BOSS_HEALTH) {
        isInSpecialPhase = true;
        goingUpAfterSin = false;
        phase50Triggered = true;
        sinTime = 0;
    } else if (!phase25Triggered && health <= 0.25 * INITIAL_BOSS_HEALTH) {
        isInSpecialPhase = true;
        goingUpAfterSin = false;
        phase25Triggered = true;
        sinTime = 0;
    }

    if (isInSpecialPhase) {
        if (!goingUpAfterSin) {
            // Boss rơi xuống theo hình sin
            sinTime += 0.05;
            this.x += Math.sin(sinTime * 2) * 10;
            this.y += 2;

            double halfWidth = getWidth() / 2.0;
        if (this.x - halfWidth < GAME_AREA_MIN_X) {
            this.x = GAME_AREA_MIN_X + halfWidth;
        } else if (this.x + halfWidth > SpaceShooter.SCREEN_WIDTH) {
            this.x = SpaceShooter.SCREEN_WIDTH- halfWidth;
        }

            if (this.y >= targetY) {
                goingUpAfterSin = true;
            }
        } else {
            // Sau khi chạm đáy, boss bay lên thẳng
            this.y -= 2;
            this.x += Math.sin(sinTime * 2) * 10;
            double halfWidth = getWidth() / 2.0;
        if (this.x - halfWidth < GAME_AREA_MIN_X) {
            this.x = GAME_AREA_MIN_X + halfWidth;
        } else if (this.x + halfWidth > SpaceShooter.SCREEN_WIDTH) {
            this.x = SpaceShooter.SCREEN_WIDTH- halfWidth;
        }

            if (this.y <= 100) { // vị trí ổn định phía dưới score
                isInSpecialPhase = false;
            }
        }
    } else {
        // Di chuyển trái – phải bình thường
        this.x += horizontalSpeed;
        double halfWidth = getWidth() / 2.0;

        if (this.x - halfWidth <= GAME_AREA_MIN_X) {
            this.x = GAME_AREA_MIN_X + halfWidth;
            horizontalSpeed *= -1;
        } else if (this.x + halfWidth >= SpaceShooter.SCREEN_WIDTH) {{
            this.x = SpaceShooter.SCREEN_WIDTH - halfWidth;
            horizontalSpeed *= -1;
        }

        // Bắn theo cooldown
        long currentTime = System.currentTimeMillis();
        if (currentTime - lastShotTimeMillis > SHOT_COOLDOWN_MS) {
            shoot(gameObjects);
            lastShotTimeMillis = currentTime;
        }
    }

    activeBullets.removeIf(EnemyBullet::isDead); }
    }


    @Override
    public void update() {
        update(new ArrayList<>());
    }

    public void takeDamage(int damageAmount) {
        if (isDead()) {
            return;
        }
        this.health -= damageAmount;
        if (this.health <= 0) {
            this.health = 0;
            this.setDead(true);
        }
    }

    public void takeDamage() {
        takeDamage(20);
    }

    public void shoot(List<GameObject> newObjects) {
        if (isDead() || newObjects == null) return;

        // CHO BOSS BẮN THƯA HƠN VÀ GIỚI HẠN TỐC ĐỘ BẮN
        if (activeBullets.size() < MAX_BULLETS) {
                double bulletX = this.x;
                double bulletY = this.y + getHeight() / 2.0 + EnemyBullet.HEIGHT / 2.0 + 5;

                EnemyBullet bullet = new EnemyBullet(bulletX, bulletY, false);
                newObjects.add(bullet);
                activeBullets.add(bullet);
            }
        }

    @Override
    public void render(GraphicsContext gc) {
        double topLeftX = this.x - getWidth() / 2.0;
        double topLeftY = this.y - getHeight() / 2.0;
        gc.drawImage(sprite, topLeftX, topLeftY, getWidth(), getHeight());

        if (this.health > 0) {
            double healthBarMaxWidth = getWidth();
            double healthPercentage = (double) this.health / INITIAL_BOSS_HEALTH;
            double currentHealthBarWidth = healthBarMaxWidth * healthPercentage;

            double healthBarY = topLeftY - 15;
            double healthBarHeight = 10;

            gc.setFill(Color.GRAY);
            gc.fillRect(topLeftX, healthBarY, healthBarMaxWidth, healthBarHeight);

            if (healthPercentage > 0.5) {
                gc.setFill(Color.GREEN);
            } else if (healthPercentage > 0.25) {
                gc.setFill(Color.YELLOW);
            } else {
                gc.setFill(Color.RED);
            }
            gc.fillRect(topLeftX, healthBarY, currentHealthBarWidth, healthBarHeight);
        }
    }

    public long getLastShotTimeMillis() {
        return lastShotTimeMillis;
    }

    public static long getShotCooldownMs() {
        return SHOT_COOLDOWN_MS;
    }
    @Override
    public Bounds getBounds() {
        return new Rectangle((int)x - BOSS_WIDTH/2, (int)y - BOSS_HEIGHT/2, 130, 130).getBoundsInLocal();
    } 
}