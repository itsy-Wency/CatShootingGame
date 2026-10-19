// GamePanel is both the visual canvas and the controller for the game rules.
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;

public class GamePanel extends JPanel implements KeyListener, ActionListener {
    // Images are loaded once and reused while the game is running.
    private Image background;
    private Image playerImage;
    private Image shootImage;
    private Image explosionImage;
    private Image healthImage;

    // Player position, size, health, and movement speed are the player model.
    private int playerX = 120;
    private int playerY = 300;
    private int playerWidth = 85;
    private int playerHeight = 85;
    private int playerHealth = 5;
    private int moveSpeed = 5;
    private int facing = 1; // 1 means right, -1 means left

    // These flags remember which keys are held down between timer ticks.
    private boolean upPressed;
    private boolean downPressed;
    private boolean leftPressed;
    private boolean rightPressed;
    private boolean spacePressed;

    // Timer counters make temporary effects last for a fixed number of game ticks.
    private int shootEffectTimer = 0;
    private int explosionTimer = 0;
    private int explosionX = 0;
    private int explosionY = 0;
    private int shootCooldown = 0;
    private boolean gameOver = false;
    private int waveNumber = 1;

    // ArrayList allows the number of enemies to grow when new waves appear.
    private ArrayList<Enemy> enemies = new ArrayList<Enemy>();
    private Timer gameTimer;

    public GamePanel(String characterFile, String areaFile) {
        // Prepare the panel to receive keyboard input and draw a 1280x720 game area.
        setPreferredSize(new Dimension(1280, 720));
        setFocusable(true);
        addKeyListener(this);

        // Load the assets selected by the previous screens.
        background = new ImageIcon("assets/" + areaFile).getImage();
        playerImage = new ImageIcon("assets/" + characterFile).getImage();
        shootImage = new ImageIcon("assets/ShootFX.gif").getImage();
        explosionImage = new ImageIcon("assets/ExplosionFX.gif").getImage();

        // Two different enemy types, each with idle and attack animations.
        enemies.add(new Enemy(760, 180, "CatBoxing-Idle.gif", "CatBoxing-Fight.gif"));
        enemies.add(new Enemy(1010, 390, "Enemy-Idle.gif", "Enemy-Attack.gif"));
        enemies.add(new Enemy(900, 520, "CatBoxing-Idle.gif", "CatBoxing-Fight.gif"));
        enemies.add(new Enemy(600, 480, "Enemy-Idle.gif", "Enemy-Attack.gif"));
        enemies.add(new Enemy(1100, 210, "CatBoxing-Idle.gif", "CatBoxing-Fight.gif"));
        enemies.add(new Enemy(700, 350, "Enemy-Idle.gif", "Enemy-Attack.gif"));

        // A Swing Timer calls actionPerformed about every 30 ms, creating the game loop.
        gameTimer = new Timer(30, this);
        gameTimer.start();
    }

    public void paintComponent(Graphics g) {
        // Swing calls this method whenever the panel needs to be redrawn.
        super.paintComponent(g);

        // Draw the selected background across the game area.
        g.drawImage(background, 0, 0, getWidth(), getHeight(), this);

        // Draw the player. Flip the image when facing left.
        if (facing == 1) {
            g.drawImage(playerImage, playerX, playerY, playerWidth, playerHeight, this);
        } else {
            Graphics2D g2 = (Graphics2D) g;
            g2.drawImage(playerImage, playerX + playerWidth, playerY,
                    -playerWidth, playerHeight, this);
        }

        // Draw each enemy and its health.
        // Draw only living enemies; defeated enemies remain in the list for simple bookkeeping.
        for (int i = 0; i < enemies.size(); i++) {
            Enemy enemy = enemies.get(i);
            if (enemy.alive) {
                g.drawImage(enemy.currentImage(), enemy.x, enemy.y, enemy.width, enemy.height, this);
                g.setColor(Color.BLACK);
                g.fillRect(enemy.x + 35, enemy.y - 5, 55, 7);
                g.setColor(Color.RED);
                g.fillRect(enemy.x + 35, enemy.y - 5, enemy.health * 11, 7);
            }
        }

        // Show the shooting effect briefly after pressing Space.
        if (shootEffectTimer > 0) {
            int effectX = facing == 1 ? playerX + playerWidth - 5 : playerX - 65;
            g.drawImage(shootImage, effectX, playerY + 20, 70, 45, this);
        }

        // Show the explosion at the enemy that was hit.
        if (explosionTimer > 0) {
            g.drawImage(explosionImage, explosionX, explosionY, 100, 100, this);
        }

        // Health images are provided as 0Bar.png through 5Bar.png.
        // Select the health-bar image whose filename matches the current health value.
        healthImage = new ImageIcon("assets/" + playerHealth + "Bar.png").getImage();
        g.drawImage(healthImage, 15, 15, 144, 65, this);

        g.setColor(Color.WHITE);
        g.setFont(new Font("SansSerif", Font.BOLD, 16));
        g.drawString("Move: WASD / Arrow Keys     Shoot: SPACE     Restart: R", 175, 45);
        g.drawString("Wave: " + waveNumber, 175, 68);

        if (gameOver) {
            g.setColor(new Color(0, 0, 0, 180));
            g.fillRect(0, 0, getWidth(), getHeight());
            g.setColor(Color.WHITE);
            g.setFont(new Font("SansSerif", Font.BOLD, 42));
            g.drawString("GAME OVER", 500, 320);
            g.setFont(new Font("SansSerif", Font.PLAIN, 22));
            g.drawString("Press R to restart", 530, 365);
        }
    }

    public void actionPerformed(ActionEvent e) {
        // This is one update step: read input, update objects, resolve rules, then repaint.
        if (!gameOver) {
            movePlayer();
            moveEnemies();
            checkEnemyContact();
            if (spacePressed && shootCooldown == 0) {
                shoot();
            }
            checkForNextWave();
        }

        if (shootEffectTimer > 0) {
            shootEffectTimer--;
        }
        if (explosionTimer > 0) {
            explosionTimer--;
        }
        if (shootCooldown > 0) {
            shootCooldown--;
        }

        if (playerHealth <= 0) {
            playerHealth = 0;
            gameOver = true;
        }

        repaint();
    }

    private void movePlayer() {
        // Apply every direction currently held. Holding two keys permits diagonal movement.
        if (upPressed) {
            playerY -= moveSpeed;
        }
        if (downPressed) {
            playerY += moveSpeed;
        }
        if (leftPressed) {
            playerX -= moveSpeed;
            facing = -1;
        }
        if (rightPressed) {
            playerX += moveSpeed;
            facing = 1;
        }

        // Clamp coordinates so the player cannot leave the visible play area.
        if (playerX < 0) playerX = 0;
        if (playerY < 70) playerY = 70;
        if (playerX > getWidth() - playerWidth) playerX = getWidth() - playerWidth;
        if (playerY > getHeight() - playerHeight) playerY = getHeight() - playerHeight;
    }

    private void moveEnemies() {
        // Update each enemy independently using the distance to the player.
        for (int i = 0; i < enemies.size(); i++) {
            Enemy enemy = enemies.get(i);
            if (enemy.alive) {
                int playerCenterX = playerX + playerWidth / 2;
                int playerCenterY = playerY + playerHeight / 2;
                int enemyCenterX = enemy.x + enemy.width / 2;
                int enemyCenterY = enemy.y + enemy.height / 2;

                int dx = playerCenterX - enemyCenterX;
                int dy = playerCenterY - enemyCenterY;
                double distance = Math.sqrt(dx * dx + dy * dy);

                // Enemies chase the player only when the player is nearby.
                if (distance > 80 && distance < 650) {
                    // Move toward the player while the player is within detection range.
                    if (dx > 0) enemy.x += 2;
                    if (dx < 0) enemy.x -= 2;
                    if (dy > 0) enemy.y += 2;
                    if (dy < 0) enemy.y -= 2;
                    enemy.attacking = false;
                } else if (distance <= 80) {
                    enemy.attacking = true;
                } else {
                    enemy.attacking = false;
                }
            }
        }
    }

    private void checkEnemyContact() {
        // Smaller hit rectangles make collisions feel fairer than using the full images.
        Rectangle playerBounds = new Rectangle(playerX + 15, playerY + 15,
                playerWidth - 30, playerHeight - 20);

        for (int i = 0; i < enemies.size(); i++) {
            Enemy enemy = enemies.get(i);
            Rectangle enemyBounds = new Rectangle(enemy.x + 30, enemy.y + 25,
                    enemy.width - 60, enemy.height - 40);

            // canDamage is a short invulnerability cooldown that prevents damage every tick.
            if (enemy.alive && enemyBounds.intersects(playerBounds) && enemy.canDamage) {
                playerHealth--;
                enemy.canDamage = false;
                enemy.damageTimer = 25;
            }

            if (enemy.damageTimer > 0) {
                enemy.damageTimer--;
            } else {
                enemy.canDamage = true;
            }
        }
    }

    private void shoot() {
        // Shooting is instant: find one valid target, then reduce its health.
        shootEffectTimer = 7;
        shootCooldown = 12;

        // Find the nearest living enemy in front of the player.
        Enemy target = null;
        double closestDistance = 650;

        int playerCenterX = playerX + playerWidth / 2;
        int playerCenterY = playerY + playerHeight / 2;

        for (int i = 0; i < enemies.size(); i++) {
            Enemy enemy = enemies.get(i);

            if (enemy.alive) {
                int enemyCenterX = enemy.x + enemy.width / 2;
                int enemyCenterY = enemy.y + enemy.height / 2;

                int dx = enemyCenterX - playerCenterX;
                int dy = enemyCenterY - playerCenterY;
                double distance = Math.sqrt(dx * dx + dy * dy);

                boolean isInFront = false;

                if (facing == 1 && dx > 0) {
                    isInFront = true;
                } else if (facing == -1 && dx < 0) {
                    isInFront = true;
                }

                // The shot can hit enemies up to 650 pixels away.
                // The vertical check keeps the shot from hitting enemies far above or below.
                if (isInFront && distance < closestDistance && Math.abs(dy) < 150) {
                    closestDistance = distance;
                    target = enemy;
                }
            }
        }

        // Only apply damage when the search found a target in the firing direction.
        if (target != null) {
            target.health--;
            explosionX = target.x + 20;
            explosionY = target.y + 20;
            explosionTimer = 12;

            if (target.health <= 0) {
                target.alive = false;
            }
        }
    }

    private void checkForNextWave() {
        // Scan the list to determine whether the current wave has been cleared.
        boolean anyEnemyAlive = false;

        for (int i = 0; i < enemies.size(); i++) {
            if (enemies.get(i).alive) {
                anyEnemyAlive = true;
            }
        }

        // If every enemy is defeated, create a larger wave.
        if (!anyEnemyAlive) {
            waveNumber++;
            spawnWave();
        }
    }

    private void spawnWave() {
        // Replace the old wave with a larger set of enemies.
        enemies.clear();

        // Each new wave adds two more enemies.
        int enemyCount = 6 + (waveNumber - 1) * 2;

        // Arithmetic based on i arranges enemies in rows and alternates enemy types.
        for (int i = 0; i < enemyCount; i++) {
            int x = 450 + (i % 4) * 180;
            int y = 120 + (i / 4) * 150;

            if (i % 2 == 0) {
                enemies.add(new Enemy(x, y,
                        "CatBoxing-Idle.gif", "CatBoxing-Fight.gif"));
            } else {
                enemies.add(new Enemy(x, y,
                        "Enemy-Idle.gif", "Enemy-Attack.gif"));
            }
        }
    }

    private void restartGame() {
        // Restore every piece of mutable game state to its initial value.
        playerX = 120;
        playerY = 300;
        playerHealth = 5;
        facing = 1;
        upPressed = false;
        downPressed = false;
        leftPressed = false;
        rightPressed = false;
        spacePressed = false;
        shootEffectTimer = 0;
        explosionTimer = 0;
        shootCooldown = 0;
        gameOver = false;
        waveNumber = 1;

        enemies.clear();
        enemies.add(new Enemy(760, 180, "CatBoxing-Idle.gif", "CatBoxing-Fight.gif"));
        enemies.add(new Enemy(1010, 390, "Enemy-Idle.gif", "Enemy-Attack.gif"));
        enemies.add(new Enemy(900, 520, "CatBoxing-Idle.gif", "CatBoxing-Fight.gif"));
        enemies.add(new Enemy(600, 480, "Enemy-Idle.gif", "Enemy-Attack.gif"));
        enemies.add(new Enemy(1100, 210, "CatBoxing-Idle.gif", "CatBoxing-Fight.gif"));
        enemies.add(new Enemy(700, 350, "Enemy-Idle.gif", "Enemy-Attack.gif"));
    }

    public void keyPressed(KeyEvent e) {
        // keyPressed marks a key as held; movement happens on subsequent timer ticks.
        int key = e.getKeyCode();

        if (key == KeyEvent.VK_W || key == KeyEvent.VK_UP) upPressed = true;
        if (key == KeyEvent.VK_S || key == KeyEvent.VK_DOWN) downPressed = true;
        if (key == KeyEvent.VK_A || key == KeyEvent.VK_LEFT) leftPressed = true;
        if (key == KeyEvent.VK_D || key == KeyEvent.VK_RIGHT) rightPressed = true;
        if (key == KeyEvent.VK_SPACE) spacePressed = true;
        if (key == KeyEvent.VK_R) restartGame();
    }

    public void keyReleased(KeyEvent e) {
        // keyReleased clears the matching flag so movement stops when the key is released.
        int key = e.getKeyCode();

        if (key == KeyEvent.VK_W || key == KeyEvent.VK_UP) upPressed = false;
        if (key == KeyEvent.VK_S || key == KeyEvent.VK_DOWN) downPressed = false;
        if (key == KeyEvent.VK_A || key == KeyEvent.VK_LEFT) leftPressed = false;
        if (key == KeyEvent.VK_D || key == KeyEvent.VK_RIGHT) rightPressed = false;
        if (key == KeyEvent.VK_SPACE) spacePressed = false;
    }

    public void keyTyped(KeyEvent e) {
        // Character typing is not needed; this game uses key codes instead.
    }
}
