// SwingUtilities ensures Swing windows are created on Swing's event-dispatch thread.
import javax.swing.SwingUtilities;

public class Main {
    public static void main(String[] args) {
        // invokeLater schedules the UI startup safely for Swing's single UI thread.
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                // The first screen asks the player to select a character.
                new MainFrame();
            }
        });
    }
}
