package uet.oop.spaceshootergamejavafx.entities;

import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;

public class AIController {
   private static final double DODGE_DISTANCE = 60.0;

   public AIController() { }

   public void update(Player player, List<GameObject> gameObjects, List<EnemyBullet> enemyBullets) {
      double playerX = player.getX();
      double playerY = player.getY();

      // Lọc danh sách kẻ thù từ danh sách gameObjects
      List<Enemy> enemies = gameObjects.stream()
              .filter(obj -> obj instanceof Enemy || obj instanceof BossEnemy)
              .map(obj -> (Enemy) obj)
              .collect(Collectors.toList());

      Iterator<EnemyBullet> bulletIterator = enemyBullets.iterator();

      EnemyBullet incomingBullet;
      double bulletDistanceX;
      double bulletDistanceY;

      // Xác định viên đạn địch nguy hiểm
      do {
         if (!bulletIterator.hasNext()) {
            Iterator<Enemy> enemyIterator = enemies.iterator();
            Enemy enemyTarget;

            // Kiểm tra kẻ địch ở gần để bắn
            do {
               if (!enemyIterator.hasNext()) {
                  player.setMoveLeft(false);
                  player.setMoveRight(false);
                  return;
               }

               enemyTarget = enemyIterator.next();
            } while (!(Math.abs(enemyTarget.getX() - playerX) < 20.0));

            player.shoot(gameObjects); // Player tự động bắn
            return;
         }

         incomingBullet = bulletIterator.next();
         bulletDistanceX = Math.abs(incomingBullet.getX() - playerX);
         bulletDistanceY = incomingBullet.getY() - playerY;
      } while (!(bulletDistanceY > 0.0) || !(bulletDistanceY < DODGE_DISTANCE) || !(bulletDistanceX < 40.0));

      // Né tránh nếu viên đạn đang lao tới
      if (incomingBullet.getX() < playerX) {
         player.setMoveRight(true);
      } else {
         player.setMoveLeft(true);
      }
   }
}