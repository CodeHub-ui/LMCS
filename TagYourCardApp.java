import javax.swing.*;
import java.awt.*;

public class TagYourCardApp {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            // Create the main window (JFrame)
            JFrame frame = new JFrame("Tag Your Card App");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setSize(400, 300);  // Set window size
            frame.setLocationRelativeTo(null);  // Center the window on screen
            
            // Create a panel (the "box") with BorderLayout for centering
            JPanel panel = new JPanel();
            panel.setLayout(new BorderLayout());
            
            // Add a label with the text, centered in the panel
            JLabel label = new JLabel("Tag Your Card", SwingConstants.CENTER);
            panel.add(label, BorderLayout.CENTER);
            
            // Add the panel to the frame
            frame.add(panel);
            
            // Make the window visible
            frame.setVisible(true);
        });
    }
}
