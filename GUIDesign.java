// GUIDesign.java: Initializes and launches the Pac-Man game window
import javax.swing.JFrame;

public class GUIDesign {
    public static void main(String[] args) throws Exception {
        // Define game board dimensions
        int rowCount = 21;
        int columnCount = 19;
        int tileSize = 32; // Pixel size of each tile
        int boardWidth = columnCount * tileSize;
        int boardHeight = rowCount * tileSize;

        // Create the game window with title and settings
        JFrame frame = new JFrame("Pac Man by: Rayon");
        frame.setSize(boardWidth, boardHeight);
        frame.setLocationRelativeTo(null); // Center window on screen
        frame.setResizable(false);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // Create PacMan game instance and add it to the frame
        PacMan pacmanGame = new PacMan();
        frame.add(pacmanGame);
        frame.pack(); // Adjusts window size to fit content
        pacmanGame.requestFocus(); // Focus for keyboard inputs
        frame.setVisible(true); // Display the window
    }
}