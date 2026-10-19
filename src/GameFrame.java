// This class owns the main game window and places the drawing/input panel inside it.
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class GameFrame extends JFrame {
    public GameFrame(String characterFile, String areaFile) {
        // Configure the window before adding the game content.
        setTitle("Cat Game - WASD / Arrow Keys to Move, Space to Shoot");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1280, 760);
        setResizable(false);
        setLocationRelativeTo(null);

        // GamePanel contains the game loop, rendering, rules, and keyboard controls.
        GamePanel gamePanel = new GamePanel(characterFile, areaFile);
        add(gamePanel);

        // Request focus after the window opens so keyboard events go to the game panel.
        addWindowListener(new WindowAdapter() {
            public void windowOpened(WindowEvent e) {
                gamePanel.requestFocusInWindow();
            }
        });

        setVisible(true);
        gamePanel.requestFocusInWindow();
    }
}
