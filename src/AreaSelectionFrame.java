// Swing provides the window, panels, labels, buttons, and image components.
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

// This frame lets the player choose the stage before the game begins.
public class AreaSelectionFrame extends JFrame {
    // The selected character is carried from the previous screen into the game.
    private String characterFile;

    public AreaSelectionFrame(String characterFile) {
        // Store the character choice so it can be passed to GameFrame later.
        this.characterFile = characterFile;

        setTitle("Cat Game - Choose an Area");
        setSize(1000, 620);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        // BorderLayout divides the screen into north, center, south, east, and west regions.
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBackground(new Color(42, 25, 29));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JLabel title = new JLabel("CHOOSE YOUR AREA", SwingConstants.CENTER);
        title.setForeground(Color.WHITE);
        title.setFont(new Font("SansSerif", Font.BOLD, 28));
        mainPanel.add(title, BorderLayout.NORTH);

        JPanel areaPanel = new JPanel(new GridLayout(2, 2, 12, 12));
        areaPanel.setOpaque(false);

        // Create one card for each available area. The loop avoids repeating nearly identical UI code.
        for (int i = 2; i <= 5; i++) {
            final String areaFile = "Area" + i + ".png";
            JPanel card = new JPanel(new BorderLayout(5, 5));
            card.setBackground(new Color(76, 48, 53));

            // Load and resize a preview image so all cards have a consistent appearance.
            ImageIcon original = new ImageIcon("assets/" + areaFile);
            Image scaled = original.getImage().getScaledInstance(400, 190, Image.SCALE_SMOOTH);
            JLabel preview = new JLabel(new ImageIcon(scaled), SwingConstants.CENTER);
            JLabel name = new JLabel("Area " + i, SwingConstants.CENTER);
            name.setForeground(Color.WHITE);
            name.setFont(new Font("SansSerif", Font.BOLD, 16));

            // An anonymous listener runs when the user clicks this area's button.
            JButton playButton = new JButton("Play Here");
            playButton.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    // Open the actual game with both choices, then close this selection screen.
                    new GameFrame(characterFile, areaFile);
                    dispose();
                }
            });

            card.add(name, BorderLayout.NORTH);
            card.add(preview, BorderLayout.CENTER);
            card.add(playButton, BorderLayout.SOUTH);
            areaPanel.add(card);
        }

        mainPanel.add(areaPanel, BorderLayout.CENTER);
        add(mainPanel);
        setVisible(true);
    }
}
