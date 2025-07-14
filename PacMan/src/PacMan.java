// Import required libraries for graphics, events, collections, and GUI components
import java.awt.*;  // For graphics drawing and colors
import java.awt.event.*;  // For handling keyboard events
import java.util.HashSet;  // For storing unique blocks like walls, foods, and ghosts
import java.util.Random;  // For generating random directions for ghosts
import javax.swing.*;  // For using JPanel and other Swing components

// Main class extending JPanel and implementing ActionListener and KeyListener for game loop and keyboard input
public class PacMan extends JPanel implements ActionListener, KeyListener {

    // Block class represents walls, ghosts, Pac-Man, or food items
    class Block {
        int x;  // X-coordinate
        int y;  // Y-coordinate
        int width;  // Width of the block
        int height;  // Height of the block
        Image image;  // Image associated with this block

        int startX;  // Initial X position (used for resetting)
        int startY;  // Initial Y position (used for resetting)
        char direction = 'U';  // Initial direction (U = Up)
        int velocityX = 0;  // Horizontal velocity
        int velocityY = 0;  // Vertical velocity

        // Constructor initializing block properties
        Block(Image image, int x, int y, int width, int height) {
            this.image = image;
            this.x = x;
            this.y = y;
            this.width = width;
            this.height = height;
            this.startX = x;
            this.startY = y;
        }

        // Update block's velocity based on its current direction
        void updateVelocity() {
            if (this.direction == 'U') {
                this.velocityX = 0;
                this.velocityY = -tileSize / 4;
            } else if (this.direction == 'D') {
                this.velocityX = 0;
                this.velocityY = tileSize / 4;
            } else if (this.direction == 'L') {
                this.velocityX = -tileSize / 4;
                this.velocityY = 0;
            } else if (this.direction == 'R') {
                this.velocityX = tileSize / 4;
                this.velocityY = 0;
            }
        }

        // Reset block's position to its initial coordinates
        void reset() {
            this.x = this.startX;
            this.y = this.startY;
        }
    }

    // Board configuration variables
    private int rowCount = 21;  // Number of rows in the grid
    private int columnCount = 19;  // Number of columns in the grid
    private int tileSize = 32;  // Size of each tile in pixels
    private int boardWidth = columnCount * tileSize;  // Width of the game board
    private int boardHeight = rowCount * tileSize;  // Height of the game board

    // Image variables for game elements
    private Image wallImage;
    private Image blueGhostImage;
    private Image orangeGhostImage;
    private Image pinkGhostImage;
    private Image redGhostImage;
    private Image pacmanUpImage;
    private Image pacmanDownImage;
    private Image pacmanLeftImage;
    private Image pacmanRightImage;

    // Layout of the game using text characters
    private String[] tileMap = {
            "XXXXXXXXXXXXXXXXXXX",
            "X        X        X",
            "X XX XXX X XXX XX X",
            "X                 X",
            "X XX X XXXXX X XX X",
            "X    X       X    X",
            "XXXX XXXX XXXX XXXX",
            "OOOX X       X XOOO",
            "XXXX X XXrXX X XXXX",
            "O       bpo       O",
            "XXXX X XXXXX X XXXX",
            "OOOX X       X XOOO",
            "XXXX X XXXXX X XXXX",
            "X        X        X",
            "X XX XXX X XXX XX X",
            "X  X     P     X  X",
            "XX X X XXXXX X X XX",
            "X    X   X   X    X",
            "X XXXXXX X XXXXXX X",
            "X                 X",
            "XXXXXXXXXXXXXXXXXXX"
    };

    // Game state collections and variables
    HashSet<Block> walls;  // Stores wall blocks
    HashSet<Block> foods;  // Stores food blocks
    HashSet<Block> ghosts;  // Stores ghost blocks
    Block pacman;  // Pac-Man block

    Timer gameLoop;  // Timer object to control the game loop
    char[] directions = {'U', 'D', 'L', 'R'};  // Possible directions for ghosts
    Random random = new Random();  // Random number generator
    int score = 0;  // Player's score
    int lives = 3;  // Player's lives
    boolean gameOver = false;  // Flag to check if the game is over

    // Constructor for PacMan game setup
    PacMan() {
        setPreferredSize(new Dimension(boardWidth, boardHeight));
        setBackground(Color.BLACK);
        addKeyListener(this);
        setFocusable(true);

        // Load all images
        wallImage = new ImageIcon(getClass().getResource("./wall.png")).getImage();
        blueGhostImage = new ImageIcon(getClass().getResource("./blueGhost.png")).getImage();
        orangeGhostImage = new ImageIcon(getClass().getResource("./orangeGhost.png")).getImage();
        pinkGhostImage = new ImageIcon(getClass().getResource("./pinkGhost.png")).getImage();
        redGhostImage = new ImageIcon(getClass().getResource("./redGhost.png")).getImage();
        pacmanUpImage = new ImageIcon(getClass().getResource("./pacmanUp.png")).getImage();
        pacmanDownImage = new ImageIcon(getClass().getResource("./pacmanDown.png")).getImage();
        pacmanLeftImage = new ImageIcon(getClass().getResource("./pacmanLeft.png")).getImage();
        pacmanRightImage = new ImageIcon(getClass().getResource("./pacmanRight.png")).getImage();

        loadMap();  // Load map based on tileMap

        // Assign random directions to ghosts
        for (Block ghost : ghosts) {
            ghost.direction = directions[random.nextInt(4)];
            ghost.updateVelocity();
        }

        gameLoop = new Timer(50, this);  // Set up timer for game loop
        gameLoop.start();
    }

    // Loads the game map by reading tileMap and creating walls, ghosts, Pac-Man, and food blocks
public void loadMap() {
    walls = new HashSet<>();  // Initialize empty set to store wall blocks
    foods = new HashSet<>();  // Initialize empty set to store food blocks
    ghosts = new HashSet<>(); // Initialize empty set to store ghost blocks

    // Loop through each row in the tile map
    for (int r = 0; r < rowCount; r++) {
        // Loop through each column in the tile map
        for (int c = 0; c < columnCount; c++) {
            // Get the character at the current row and column from tileMap
            char tileMapChar = tileMap[r].charAt(c);

            // Calculate the x and y position in pixels for this block
            int x = c * tileSize;
            int y = r * tileSize;

            // Check what type of block this character represents
            if (tileMapChar == 'X') {
                // 'X' means wall block
                walls.add(new Block(wallImage, x, y, tileSize, tileSize));
            } else if (tileMapChar == 'b') {
                // 'b' means blue ghost block
                ghosts.add(new Block(blueGhostImage, x, y, tileSize, tileSize));
            } else if (tileMapChar == 'o') {
                // 'o' means orange ghost block
                ghosts.add(new Block(orangeGhostImage, x, y, tileSize, tileSize));
            } else if (tileMapChar == 'p') {
                // 'p' means pink ghost block
                ghosts.add(new Block(pinkGhostImage, x, y, tileSize, tileSize));
            } else if (tileMapChar == 'r') {
                // 'r' means red ghost block
                ghosts.add(new Block(redGhostImage, x, y, tileSize, tileSize));
            } else if (tileMapChar == 'P') {
                // 'P' means Pac-Man block
                pacman = new Block(pacmanRightImage, x, y, tileSize, tileSize);
            } else if (tileMapChar == ' ') {
                // ' ' (space) means food block (small pellet)
                foods.add(new Block(null, x + 14, y + 14, 4, 4)); // Food is smaller and centered
            }
        }
    }
}


  // paintComponent is automatically called by Java to redraw the game screen.
// It ensures the game graphics are refreshed when needed (e.g., after movement or resizing).
public void paintComponent(Graphics g) {
    super.paintComponent(g);  // Clears the previous frame and prepares for new drawing
    draw(g);  // Calls the custom draw method to render all game elements (Pac-Man, ghosts, walls, food, text)
}
// Custom method to draw all game elements onto the screen.
// This method is called inside paintComponent() to update the visual layout.
public void draw(Graphics g) {
    // Draw Pac-Man character using its current image and position
    g.drawImage(pacman.image, pacman.x, pacman.y, pacman.width, pacman.height, null);

    // Draw all ghost blocks with their images and positions
    for (Block ghost : ghosts) {
        g.drawImage(ghost.image, ghost.x, ghost.y, ghost.width, ghost.height, null);
    }

    // Draw all wall blocks with their images and positions
    for (Block wall : walls) {
        g.drawImage(wall.image, wall.x, wall.y, wall.width, wall.height, null);
    }

    // Set the color to white for drawing food and text
    g.setColor(Color.WHITE);

    // Draw all food blocks as small white rectangles
    for (Block food : foods) {
        g.fillRect(food.x, food.y, food.width, food.height);
    }

    // Set the font style and size for game status text
    g.setFont(new Font("Arial", Font.PLAIN, 18));

    // Display "Game Over" message with score if the game is over
    if (gameOver) {
        g.drawString("Game Over: " + score, tileSize / 2, tileSize / 2);
    } 
    // Otherwise, display remaining lives and current score
    else {
        g.drawString("x" + lives + " Score: " + score, tileSize / 2, tileSize / 2);
    }
}


    // This method updates all game movements: Pac-Man, ghosts, and checks for collisions.
public void move() {
    // Move Pac-Man by adding his current velocity to his position
    pacman.x += pacman.velocityX;
    pacman.y += pacman.velocityY;

    // Check collision between Pac-Man and any wall
    for (Block wall : walls) {
        if (collision(pacman, wall)) {
            // If Pac-Man hits a wall, move him back to the previous position
            pacman.x -= pacman.velocityX;
            pacman.y -= pacman.velocityY;
            break;  // Exit loop early since collision happened
        }
    }

    // Move all ghosts and check for collisions with Pac-Man and walls
    for (Block ghost : ghosts) {
        if (collision(ghost, pacman)) {
            // If Pac-Man collides with a ghost, reduce lives
            lives--;
            if (lives == 0) {
                // If no lives left, set game over
                gameOver = true;
                return;  // Stop further movement
            }
            resetPositions();  // Reset positions after collision
        }

        // Move ghost by adding its velocity to its position
        ghost.x += ghost.velocityX;
        ghost.y += ghost.velocityY;

        // Check if ghost hits a wall or crosses board limits
        for (Block wall : walls) {
            if (collision(ghost, wall) || ghost.x <= 0 || ghost.x + ghost.width >= boardWidth) {
                // Move ghost back to previous position
                ghost.x -= ghost.velocityX;
                ghost.y -= ghost.velocityY;
                // Assign a new random direction and update ghost's velocity
                ghost.direction = directions[random.nextInt(4)];
                ghost.updateVelocity();
            }
        }
    }

    // Check if Pac-Man eats food
    Block foodEaten = null;
    for (Block food : foods) {
        if (collision(pacman, food)) {
            foodEaten = food;  // Mark the food as eaten
            score += 10;  // Increase score
        }
    }

    // Remove the eaten food from the food collection
    foods.remove(foodEaten);

    // If all food is eaten, reload the map and reset positions
    if (foods.isEmpty()) {
        loadMap();
        resetPositions();
    }
}


   // This method checks if two blocks (Pac-Man, ghost, wall, or food) are colliding using rectangle overlap logic
public boolean collision(Block a, Block b) {
    // Returns true if the two blocks' rectangles overlap on both X and Y axes
    return a.x < b.x + b.width &&    // a's left edge is to the left of b's right edge
           a.x + a.width > b.x &&    // a's right edge is to the right of b's left edge
           a.y < b.y + b.height &&   // a's top edge is above b's bottom edge
           a.y + a.height > b.y;     // a's bottom edge is below b's top edge
}

// This method resets Pac-Man and ghosts back to their starting positions after collision or game reset
public void resetPositions() {
    pacman.reset();            // Set Pac-Man's position back to its starting point
    pacman.velocityX = 0;      // Stop Pac-Man's horizontal movement
    pacman.velocityY = 0;      // Stop Pac-Man's vertical movement

    // Reset all ghosts to their initial positions and assign them new random directions
    for (Block ghost : ghosts) {
        ghost.reset();                              // Set ghost's position back to its starting point
        ghost.direction = directions[random.nextInt(4)];  // Assign a random direction from U, D, L, R
        ghost.updateVelocity();                    // Update ghost's velocity based on new direction
    }
}

  // actionPerformed is automatically called by the Timer every 50ms (based on gameLoop settings)
// It updates the game state and redraws the screen
@Override
public void actionPerformed(ActionEvent e) {
    move();          // Move Pac-Man and ghosts, handle collisions and game logic
    repaint();       // Refresh the screen with updated positions and score
    if (gameOver) {  // If game is over, stop the timer to pause everything
        gameLoop.stop();
    }
}

// keyTyped is part of the KeyListener interface but unused here
// It must be included to fully implement KeyListener, even if left empty
@Override
public void keyTyped(KeyEvent e) {}

// keyPressed is triggered when the player presses a key
@Override
public void keyPressed(KeyEvent e) {
    if (gameOver) return;  // If game is over, ignore key presses

    // Check which arrow key was pressed and update Pac-Man's direction, velocity, and image accordingly
    if (e.getKeyCode() == KeyEvent.VK_UP) {
        pacman.direction = 'U';
        pacman.updateVelocity();
        pacman.image = pacmanUpImage;
    } else if (e.getKeyCode() == KeyEvent.VK_DOWN) {
        pacman.direction = 'D';
        pacman.updateVelocity();
        pacman.image = pacmanDownImage;
    } else if (e.getKeyCode() == KeyEvent.VK_LEFT) {
        pacman.direction = 'L';
        pacman.updateVelocity();
        pacman.image = pacmanLeftImage;
    } else if (e.getKeyCode() == KeyEvent.VK_RIGHT) {
        pacman.direction = 'R';
        pacman.updateVelocity();
        pacman.image = pacmanRightImage;
    }
}

// keyReleased is triggered when a key is released
@Override
public void keyReleased(KeyEvent e) {
    if (gameOver) {
        loadMap();            // Reload the map with initial setup
        resetPositions();     // Reset all positions for Pac-Man and ghosts
        lives = 3;            // Reset lives to 3
        score = 0;            // Reset score to 0
        gameOver = false;     // Mark the game as active again
        gameLoop.start();     // Restart the game loop timer
    }
}
}