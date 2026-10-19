// This is the first screen shown when the application starts.
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class MainFrame extends JFrame {
    // Store the current selection so it can be passed to the area-selection screen.
    private String selectedCharacter = "Character1.gif";

    public MainFrame() {
        // Configure the main window and center it on the user's screen.
        setTitle("Cat Game - Choose Your Character");
        setSize(900, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        // The main panel provides the overall layout and background color.
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBackground(new Color(42, 25, 29));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(25, 25, 25, 25));

        JLabel title = new JLabel("CAT GAME: CHARACTER SELECT", SwingConstants.CENTER);
        title.setForeground(Color.WHITE);
        title.setFont(new Font("SansSerif", Font.BOLD, 28));
        mainPanel.add(title, BorderLayout.NORTH);

        JPanel characterPanel = new JPanel(new GridLayout(1, 4, 15, 15));
        characterPanel.setOpaque(false);

        // Build four character cards from the four character image files.
        for (int i = 1; i <= 4; i++) {
            final String fileName = "Character" + i + ".gif";
            JPanel card = new JPanel(new BorderLayout(5, 5));
            card.setBackground(new Color(76, 48, 53));
            card.setBorder(BorderFactory.createEmptyBorder(12, 8, 12, 8));

            JLabel image = new JLabel(new ImageIcon("assets/" + fileName), SwingConstants.CENTER);
            JLabel name = new JLabel("Character " + i, SwingConstants.CENTER);
            name.setForeground(Color.WHITE);
            name.setFont(new Font("SansSerif", Font.BOLD, 16));

            // The button listener records the choice and moves to the next screen.
            JButton chooseButton = new JButton("Select");
            chooseButton.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    selectedCharacter = fileName;
                    // Pass the selected character forward and close the old frame.
                    new AreaSelectionFrame(selectedCharacter);
                    dispose();
                }
            });

            card.add(image, BorderLayout.CENTER);
            card.add(name, BorderLayout.NORTH);
            card.add(chooseButton, BorderLayout.SOUTH);
            characterPanel.add(card);
        }

        JLabel instructions = new JLabel("Choose one character to continue.", SwingConstants.CENTER);
        instructions.setForeground(Color.WHITE);
        instructions.setFont(new Font("SansSerif", Font.PLAIN, 16));

        mainPanel.add(characterPanel, BorderLayout.CENTER);
        mainPanel.add(instructions, BorderLayout.SOUTH);
        add(mainPanel);
        setVisible(true);
    }
}
