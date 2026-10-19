// Enemy is a small model object: it stores position, state, health, and images.
import javax.swing.ImageIcon;
import java.awt.Image;

public class Enemy {
    // Public fields are used directly by GamePanel for this small educational project.
    public int x;
    public int y;
    public int width = 120;
    public int height = 105;
    public int health = 5;
    public boolean alive = true;
    public boolean attacking = false;
    public boolean canDamage = true;
    public int damageTimer = 0;

    // Keep separate images for the normal and attacking states.
    private Image idleImage;
    private Image attackImage;

    public Enemy(int x, int y, String idleFile, String attackFile) {
        // The constructor receives the enemy's starting position and asset filenames.
        this.x = x;
        this.y = y;
        // ImageIcon loads the file; getImage() gives Swing an Image that can be drawn.
        idleImage = new ImageIcon("assets/" + idleFile).getImage();
        attackImage = new ImageIcon("assets/" + attackFile).getImage();
    }

    public Image currentImage() {
        // Returning one image based on state is a simple form of animation/state selection.
        if (attacking) {
            return attackImage;
        }
        return idleImage;
    }
}
