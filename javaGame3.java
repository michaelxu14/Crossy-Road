//imports
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import javax.imageio.ImageIO;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import java.awt.event.KeyAdapter;



//Main class

public class javaGame3 extends JFrame {

    private static final String MAIN_MENU = "Main Menu";
    private static final String CHARACTER_SELECT = "Character Select";
    private static final String GAME_PANEL = "Game";

    //lists

    private static BufferedImage[] frames;  // 2D array for frames
    static int[] nextLeftPic  = {0, 1, 2, 3};
    static int[] nextUpPic = {4, 5, 6, 7}; 
    static int[] nextDownPic = {8, 9, 10, 11}; 

    private static int[][] obstacles; 


    private static JPanel cards;
    private JPanel mainMenuPanel;
    private JPanel characterSelectPanel;
    private static JPanel gamePanel;

    static final int WIDTH = 1000;
    static final int HEIGHT = 600;

    static int whichChar = 0;
    static int score = 0;
    static int lives = 3; //temporary, 100000 for testing

    static int speedMin = 1;
    static int speedMax = 2;
    static int numLinesGen = 3;
    static int levelUpSpot = 975;



    //Use JPanel to create screens for each part of the game

    //MENU
    //Create individual methods for each panel
    private void createMainMenuPanel() {
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setPreferredSize(new Dimension(WIDTH, HEIGHT));
        setTitle("Crossy Road");

        mainMenuPanel = new JPanel();
        characterSelectPanel = createCharacterSelectPanel();
        gamePanel = createGamePanel();

        cards = new JPanel(new CardLayout());
        cards.add(mainMenuPanel, MAIN_MENU);
        cards.add(characterSelectPanel, CHARACTER_SELECT);
        cards.add(gamePanel, GAME_PANEL);

        setContentPane(cards);

        pack();
        setLocationRelativeTo(null);
        setVisible(true);

        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));

        JButton startButton = new JButton("Start Game");
        JButton exitButton = new JButton("Exit");

        // Start button
        startButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                showCard(CHARACTER_SELECT);
            }
        });

        // Exit button
        exitButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                System.exit(0);
            }
        });

        ImageIcon image = new ImageIcon("title.PNG");
        JLabel imageLabel = new JLabel(image);
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));

        panel.add(imageLabel);
        panel.add(Box.createRigidArea(new Dimension(0, 10)));
        panel.add(startButton);
        panel.add(Box.createRigidArea(new Dimension(0, 10)));
        panel.add(exitButton);
        panel.add(Box.createVerticalGlue());

        mainMenuPanel.add(panel);
        
    }

    //CHARACTER SELECT
    private JPanel createCharacterSelectPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        JButton character1Button = new JButton("Select Character 1");
        panel.add(Box.createRigidArea(new Dimension(0, 10)));
        JButton character2Button = new JButton("Select Character 2");
        panel.add(Box.createRigidArea(new Dimension(0, 10)));
       
        panel.add(Box.createVerticalGlue());

        //Use this for changing character


        //CASE 1: pick dog
        character1Button.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                whichChar = 1;
                frames = new BufferedImage[12];
                for (int i = 0; i < 12; i++) {
                    String imagePath = "sprites/dog/dog" + i + ".png"; 
                    try {
                        frames[i] = ImageIO.read(new File(imagePath));
                    } catch (Exception ex) {
                    }
                }
                showCard(GAME_PANEL);
            }
        });
        
        //CASE 2: pick dog
        character2Button.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                whichChar = 2;
                frames = new BufferedImage[12];
                for (int i = 0; i < 12; i++) {
                    String imagePath = "sprites/cat/cat" + i + ".png";  
                    try {
                        frames[i] = ImageIO.read(new File(imagePath));
                    } catch (Exception ex) {
                    }
                }
                // For testing purposes
                // JOptionPane.showMessageDialog(javaGame3.this, "Character 2 selected!");
                showCard(GAME_PANEL);
            }
        });

        //Format screen
        ImageIcon image = new ImageIcon("title.PNG");
        JLabel imageLabel = new JLabel(image);
        panel.add(imageLabel);
        panel.add(Box.createVerticalGlue());
        panel.add(character1Button);
        panel.add(Box.createRigidArea(new Dimension(0, 10)));
        panel.add(character2Button);
        panel.add(Box.createRigidArea(new Dimension(0, 10)));
        panel.add(Box.createVerticalGlue());

        return panel;
    }

    //GAME
    private JPanel createGamePanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BorderLayout());
    
        GraphicsPanel graphicsPanel = new GraphicsPanel();
        
        panel.add(graphicsPanel, BorderLayout.CENTER);
        graphicsPanel.setLayout(null);
    
        graphicsPanel.addKeyListener(new MyKeyListener(graphicsPanel, obstacles));
        graphicsPanel.setFocusable(true);
        graphicsPanel.requestFocusInWindow();

        // Do this to call my moveObstacle method every 20 ms, to move the obstacles
        Timer timer = new Timer(20, new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                moveObstacles(obstacles);
                graphicsPanel.repaint();
            }
        });
        
        timer.start();
        return panel;
    }

    private static void showCard(String cardName) {
        CardLayout cardLayout = (CardLayout) cards.getLayout();
        cardLayout.show(cards, cardName);

        // Add the following code to request focus on the GraphicsPanel after card switch
        if (cardName.equals(GAME_PANEL)) {
            GraphicsPanel graphicsPanel = (GraphicsPanel) gamePanel.getComponent(0);
            graphicsPanel.requestFocusInWindow(); 
        }
    }

    //Play music for the game
    private void playMusic(){
        try {
        File audioFile = new File("music/music.wav");  
        AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(audioFile);
        Clip clip = AudioSystem.getClip();
        clip.open(audioInputStream);
        clip.start();
        clip.loop(Clip.LOOP_CONTINUOUSLY);
        } catch (Exception ex) {
        }

    }



// GAME HERE
//------------------------------------------------------------------------------  
    //Just to run the game
    public static void main(String[] args) {
        /*Creates an instance of the javaGame3 class. The new javaGame3() part allocates memory for a
         new object of type javaGame3 ( the main class ), and the game variable is assigned to reference that object. */
        javaGame3 game = new javaGame3();
        game.createMainMenuPanel();
        game.playMusic();
    }
    

    static class GraphicsPanel extends JPanel {
        private int level = 1;
        private int playerX = WIDTH / 2 + 100;
        private int playerY = HEIGHT / 2 + 300;  // Set Player position
        private int currentFrame = 0;
        private String charState = "facing right";

        public GraphicsPanel() {

            setFocusable(true);
            requestFocusInWindow();
            obstacles = createVerticalObstacleStream();
            
        }


        public void paintComponent(Graphics g) {
            // Draw the background
            g.setColor(Color.black);
            g.fillRect(0, 0, WIDTH, HEIGHT);
            
    
            super.paintComponent(g);
         
            //SCOREBOARD

            g.setFont(new Font("Arial", Font.BOLD, 20));
            g.drawString("Score: " + score, 10, 20);
            //LIVES
            g.setFont(new Font("Arial", Font.BOLD, 20));
            g.drawString("Lives: " + lives, 10, 50);

            //LEVEL
            g.setFont(new Font("Arial", Font.BOLD, 20));
            g.drawString("Level: " + level, 10, 80);

            
            // Draw the player
            drawPlayer(g);

        
            // Render the obstacles at their current positions
            drawObstacles(g, obstacles);
        }
        
        //Life system
        public void lifeSystem() {
            lives--;
        
            if (lives <= 0) {
                // Game over if no lives remaining
                JOptionPane.showMessageDialog(this, "Game over! You ran out of lives. \n Your score was: " + score + "\n You got to level: " + level);
                score = 0;
                lives = 3; // Reset lives
                level = 0;
                speedMin = 1;
                speedMax = 2;
                returnToMainMenu();

                 
            } else {
                // Continue the game with the updated lives
                resetPositions();
            }
        
            repaint();
        }

        private void drawPlayer(Graphics g) {
            int frameIndex = 0;


            switch (charState) {
                case "facing right": // standing left
                    frameIndex = nextLeftPic[currentFrame];
                    break;
                case "facing down": // moving up
                    frameIndex = nextUpPic[currentFrame];
                    break;
                case "facing up": // moving down
                    frameIndex = nextDownPic[currentFrame];
                    break;
            }

            if (frames != null && frameIndex < frames.length) {
                // initialize width and height of sprite
                int spriteWidth = 60;  
                int spriteHeight = 60;   
                g.drawImage(frames[frameIndex], playerX, playerY, spriteWidth, spriteHeight, this);
            } else {
                  
            }
         }

    
        public int[][] createVerticalObstacleStream() {
            int numLines = numLinesGen;
            int obstaclesPerLine = 6;
            int startY = 500;
        
            int[][] obstacleArray = new int[numLines * obstaclesPerLine][5];
        
            for (int line = 0; line < numLines; line++) {
                int speed = getRandomNumber(speedMin, speedMax);
                int lineDirection = (line % 2 == 0) ? 1 : -1;
                int gapBetweenLines = 100;
        
                for (int i = 0; i < obstaclesPerLine; i++) {
                    int startX = 400 + line * gapBetweenLines;
                    int startYForLine = startY - i * (getRandomNumber(70, 120));
                    int index = line * obstaclesPerLine + i;
                    obstacleArray[index][0] = startX;
                    obstacleArray[index][1] = startYForLine;
                    obstacleArray[index][2] = speed;
                    obstacleArray[index][3] = lineDirection;
                    obstacleArray[index][4] = getRandomNumber(20, 40);
                }
            }
        
            return obstacleArray;
        }

        public void movePlayer(int deltaX, int deltaY) {
            playerX += deltaX;
            playerY += deltaY;

            repaint();
        }

        public void moveObstacles(int[][] obstacles) {
            for (int[] obstacle : obstacles) {
                obstacle[1] += obstacle[2] * obstacle[3];
        
                // Wrap around when reaching the top or bottom of the screen
                if (obstacle[1] <= 0) {
                    obstacle[1] = HEIGHT;
                } else if (obstacle[1] >= HEIGHT) {
                    obstacle[1] = 0;
                }
            }
        
            checkCollisions(obstacles);
            repaint();
        }

        //Check for collisions between obstacles and the player character
        public void checkCollisions(int[][] obstacles) {
            Rectangle playerRect = new Rectangle(playerX, playerY, 30, 30);
    
            for (int[] obstacle : obstacles) {
                Rectangle obstacleRect = new Rectangle(obstacle[0], obstacle[1], 30, obstacle[4]);
                if (playerRect.intersects(obstacleRect)) {
                    handleCollision();
                }
            }

            if (playerX > levelUpSpot) {
                if (level % 3 == 0){
                    numLinesGen++;
                }
                levelUp();
                
            }
        }

        //What to do when you level up?

        private void levelUp(){
            score += 100;
            level ++;
            resetPositions();
            levelUpSpot = playerX + 975;
            speedMin += 2;
            speedMax += 2;
        }

        public void handleCollision() {
            //For test purposes
            //JOptionPane.showMessageDialog(this, "Game Over! You collided with an obstacle. \n Score: " + score);
            //score = 0;
            lifeSystem();
            resetPositions();
            repaint();
        }

        //Send player back to position at the start of the game
        public void resetPositions() {
            playerX = WIDTH / 2 + 100;
            playerY = HEIGHT / 2 + 300;

            obstacles = createVerticalObstacleStream();
        }
    }

    //Go back to main menu
    private static void returnToMainMenu() {
        showCard(MAIN_MENU);
    }


//Keybinds
//------------------------------------------------------------------------------  
    static class MyKeyListener extends KeyAdapter {
        private final GraphicsPanel graphicsPanel;
        private final int[][] obstacles;

        public MyKeyListener(GraphicsPanel graphicsPanel, int[][] obstacles) {
            this.graphicsPanel = graphicsPanel;
            this.obstacles = obstacles;
        }

         
        public void keyPressed(KeyEvent e) {
            int key = e.getKeyCode();
            switch (key) {
                case KeyEvent.VK_UP:
                    graphicsPanel.charState = "facing up";
                    graphicsPanel.movePlayer(0, -30);
                    graphicsPanel.currentFrame = (graphicsPanel.currentFrame + 7) % 4;
                    break;
                case KeyEvent.VK_DOWN:
                    graphicsPanel.charState = "facing down";
                    graphicsPanel.movePlayer(0, 30);
                    graphicsPanel.currentFrame = (graphicsPanel.currentFrame + 3) % 4;
                    break;
                case KeyEvent.VK_RIGHT:
                    graphicsPanel.charState = "facing right";
                    graphicsPanel.movePlayer(30, 0);
                    // Update the current frame for the walk cycle
                    graphicsPanel.currentFrame = (graphicsPanel.currentFrame + 1) % 4;
                    score += 10;
                    break;
                /*case KeyEvent.VK_LEFT:
                    graphicsPanel.movePlayer(-30, 0);
                    break; */
            }
            graphicsPanel.repaint();
            graphicsPanel.checkCollisions(obstacles);
        }
    
        public void keyReleased(KeyEvent e) {
            int key = e.getKeyCode();
            // handle key released
        }
    
        public void keyTyped(KeyEvent e) {
            char keyChar = e.getKeyChar();
            // handle key typed
        }
    }

//Obstacle stuff

//------------------------------------------------------------------------------  
    private static int createObstacleX(int line, int obstaclesPerLine) {
        int gapBetweenLines = 100;
        return 400 + line * gapBetweenLines;
    }

    private static int createObstacleY(int i) {
        return 500 - i * (getRandomNumber(70, 120));
    }

    private static int createObstacleSpeed() {
        return getRandomNumber(speedMin, speedMax);
    }

    private static int createObstacleDirection(int line) {
        return (line % 2 == 0) ? 1 : -1;
    }

    private static int createObstacleSize() {
        return getRandomNumber(20, 40);
    }

    // Method to create an array of obstacles
    private static int[][] createObstacleArray() {
        int numLines = numLinesGen;
        int obstaclesPerLine = 6;

        int[][] obstacleArray = new int[numLines * obstaclesPerLine][5];

        for (int line = 0; line < numLines; line++) {
            for (int i = 0; i < obstaclesPerLine; i++) {
                int index = line * obstaclesPerLine + i;
                obstacleArray[index][0] = createObstacleX(line, obstaclesPerLine);
                obstacleArray[index][1] = createObstacleY(i);
                obstacleArray[index][2] = createObstacleSpeed();
                obstacleArray[index][3] = createObstacleDirection(line);
                obstacleArray[index][4] = getRandomNumber(20, 40);
            }
        }

        return obstacleArray;
    }

    // Method to move obstacles
    private static void moveObstacles(int[][] obstacles) {
        for (int i = 0; i < obstacles.length; i++) {
            obstacles[i][1] += obstacles[i][2] * obstacles[i][3];

            // Wrap around when reaching the top or bottom of the screen
            if (obstacles[i][1] <= 0) {
                obstacles[i][1] = HEIGHT;
            } else if (obstacles[i][1] >= HEIGHT) {
                obstacles[i][1] = 0;
            }
        }
    }

    // Method to draw obstacles
    private static void drawObstacles(Graphics g, int[][] obstacles) {
        g.setColor(Color.RED);
        for (int[] obstacle : obstacles) {
            g.fillRect(obstacle[0], obstacle[1], 30, obstacle[4]);
        }
    }
// Ends here

    //Simplify random generation as a function
    public static int getRandomNumber(int min, int max) {
        return (int) ((Math.random() * (max - min)) + min);
    }
}