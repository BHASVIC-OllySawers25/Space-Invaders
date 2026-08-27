//imports
import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.ArrayList;

//GamePanel class
public class GamePanel extends JPanel implements KeyListener { //the implements part js means the gpanel can now recieve keyboard evets

    //variabels
    Timer gameTimer;
    Player player;
    boolean leftPressed = false;
    boolean upPressed = false;
    boolean downPressed = false;
    boolean rightPressed = false;
    ArrayList<Invader> invaders = new ArrayList<>();
    int invaderDirection = 1;
    int invaderSpeed = 2;
    int playerHitCooldown = 0;
    Image hp3;
    Image hp2;
    Image hp1;
    Image hp0;
    ArrayList<InvaderBullet> invaderBullets = new ArrayList<>();
    int score = 0;
    int highScore = 0;
    String gameState = "MENU";
    int menuOption = 0;
    Image menu;
    Image game;

    //constructor
    public GamePanel() {
        player = new Player();
        addKeyListener(this);
        setFocusable(true); //essentially js ensures gpanel is recieving the key presses

        //lambda syntax thing, basically means run the code inside the loop, from W3schools's page
        gameTimer = new Timer(16, e -> {
            updateGame(); //to see wats moved/changed
            repaint(); //to redraw the sceeen after every loop
            });
        gameTimer.start();

        createInvaders();

        game = new ImageIcon("images/game.jpg").getImage();
        menu = new ImageIcon("images/menu.jpg").getImage();

        hp3 = new ImageIcon("images/hp3.png").getImage();
        hp2 = new ImageIcon("images/hp2.png").getImage();
        hp1 = new ImageIcon("images/hp1.png").getImage();
        hp0 = new ImageIcon("images/hp0.png").getImage();
    }

    //methods

    //like the main game state updater, is the cycles
    public void updateGame() {
        //System.out.println("this is a test please work");

        if (!gameState.equals("GAME")) {
            return;
        }

        //checks if the player has died and the animation has fully played
        if (player.dying && player.deathFrame >= 3) {
            gameState = "MENU";
            menuOption = 0;
        }

        //more advanced/nicer feeling movement
        if (leftPressed) {
            player.moveLeft();
        }
        if (rightPressed) {
            player.moveRight();
        }
        if (upPressed) {
            player.moveUp();
        }
        if (downPressed) {
            player.moveDown();
        }
        player.updateBullets();
        checkBulletCollisions();
        checkPlayerCollision();
        player.update();

        //to delete the bullets after 5 cycles
        for (int i = 0; i < player.bullets.size(); i++) {
            Bullet bullet = player.bullets.get(i);
            if (bullet.updateHit()) {
                player.bullets.remove(i);
                i--;
            }
        }

        for (int i = 0; i < invaders.size(); i++) { //herd like movement for the invaders
            Invader invader = invaders.get(i);
            invader.move(invaderDirection * invaderSpeed);
        }

        invaderEdgeDetect();

        // deleting the invaders after frame 3
        for (int i = 0; i < invaders.size(); i++) {
            Invader invader = invaders.get(i);
            if (invader.animationDel()) {
                invaders.remove(i);
                i--;
            }
        }

        //invader shooting system with randomised chances
        if (Math.random() < 0.01 && invaders.size() > 0) {
            int randomInvader = (int)(Math.random() * invaders.size());
            Invader invader = invaders.get(randomInvader);
            invaderBullets.add(new InvaderBullet(invader.invaderX, invader.invaderY + 98));
        }

        //respawning the invaders
        boolean invadersOffScreen = true;
        for (int i = 0; i < invaders.size(); i++) { //basically checks if either they are all off the screen or all dead
            if (invaders.get(i).invaderY < getHeight()) {
                invadersOffScreen = false;
                break;
            }
        }
        if (invaders.size() == 0 || invadersOffScreen) {
            createInvaders();
        }

        //moving invader bullets and deleting them
        for (int i = 0; i < invaderBullets.size(); i++) {
            invaderBullets.get(i).move();
            if (invaderBullets.get(i).bulletY > getHeight()) { //specifically here is the delete part
                invaderBullets.remove(i);
                i--;
            }
        }

        checkInvaderBulletCollisions();

        //updating invader states
        for (int i = 0; i < invaders.size(); i++) {
            Invader invader = invaders.get(i);
            invader.update();
        }
    }//end of game updater

    public void createInvaders() {
        invaders.clear();
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 8; j++) {
                int x = 30 + j * 80;
                int y = 50 + i * 80;
                invaders.add(new Invader(x, y));
            }
        }
    }

    //to restart the player up, resetting the sprite too
    public void resetPlayer() {
        player.playerX = 300;
        player.playerY = 680;
        player.playerHealth = 3;
        player.dying = false;
    }

    //kinda scruffy collision detection for the bullets (coord based)
    public void checkBulletCollisions() {
        for (int i = 0; i < player.bullets.size(); i++) {
            Bullet bullet = player.bullets.get(i);
            for (int j = 0; j < invaders.size(); j++) {
                Invader invader = invaders.get(j);
                if (bullet.bulletX >= invader.invaderX &&
                        bullet.bulletX <= invader.invaderX + 76 &&
                        bullet.bulletY >= invader.invaderY &&
                        bullet.bulletY <= invader.invaderY + 98) {
                    bullet.hit = true;
                    invader.broken = true;
                    invader.brokenFrame = 0;
                    invader.brokenCounter = 0;
                    score += 100;
                        if (score > highScore) {
                            highScore = score;
                        }
                    break;
                }
            }
        }
    }

    //same collision detetction but for the invaders hitting the player
    public void checkPlayerCollision() {
        if (playerHitCooldown > 0) {
            playerHitCooldown--;
            return;
        }
        for (int i = 0; i < invaders.size(); i++) {
            Invader invader = invaders.get(i);
            if (player.playerX < invader.invaderX + 76 &&
                    player.playerX + 94 > invader.invaderX &&
                    player.playerY < invader.invaderY + 98 &&
                    player.playerY + 154 > invader.invaderY) {

                System.out.println("u got hit lil vro");

                player.playerHealth--;
                playerHitCooldown = 100;

                if (player.playerHealth <= 0) {
                    player.dying = true;

                    //reset the cooldown so your health isnt rinsed by a single invder overlap
                }
                break;
            }
        }
    }

    //no way its the same collision detection but with the invader bullets
    public void checkInvaderBulletCollisions() {
        if (player.dying || playerHitCooldown > 0) {
            return;
        }
        for (int i = 0; i < invaderBullets.size(); i++) {
            InvaderBullet bullet = invaderBullets.get(i);
            if (bullet.bulletX < player.playerX + 50 &&
                    bullet.bulletX + 10 > player.playerX &&
                    bullet.bulletY < player.playerY + 50 &&
                    bullet.bulletY + 20 > player.playerY) {

                player.playerHealth--;
                invaderBullets.remove(i);

                playerHitCooldown = 100;
                if (player.playerHealth <= 0) {
                    player.dying = true;
                }
                break;
            }
        }
    }

    //edge detection for the invaders so they dont go off the screen
    public void invaderEdgeDetect() {
        for (int i = 0; i < invaders.size(); i++) {
            Invader invader = invaders.get(i);
            if (invader.invaderX >= 650 || invader.invaderX <= 0) {
                invaderDirection *= -1;
                for (int j = 0; j < invaders.size(); j++) {
                    Invader invaderTemp = invaders.get(j);
                    invaderTemp.invaderY += 20;
                }
                break;
            }
        }
    }

    //also methods but like the key presses
    public void keyTyped(KeyEvent e) {
    }

    public void keyPressed(KeyEvent e) {
        int key = e.getKeyCode();

        //menu navigation
        if (key == KeyEvent.VK_ESCAPE) {
            if (gameState.equals("GAME")) {
                gameState = "PAUSE";
            }
            else if (gameState.equals("PAUSE")) {
                gameState = "GAME";
            }
        }

        //main menu
        if (gameState.equals("MENU")) {
            if (key == KeyEvent.VK_UP) {
                menuOption--;
                if (menuOption < 0) {
                    menuOption = 1;
                }
            }
            if (key == KeyEvent.VK_DOWN) {
                menuOption++;
                if (menuOption > 1) {
                    menuOption = 0;
                }
            }
            if (key == KeyEvent.VK_ENTER) {
                if (menuOption == 0) {
                    createInvaders();
                    resetPlayer();
                    invaderBullets.clear();
                    score = 0;
                    gameState = "GAME";
                }
                if (menuOption == 1) {
                    System.exit(0);
                }
            }
            return;
        }

        //pause menu
        if (gameState.equals("PAUSE")) {
            if (key == KeyEvent.VK_UP) {
                menuOption--;
                if (menuOption < 0) {
                    menuOption = 2;
                }
            }
            if (key == KeyEvent.VK_DOWN) {
                menuOption++;
                if (menuOption > 2) {
                    menuOption = 0;
                }
            }
            if (key == KeyEvent.VK_ENTER) {
                if (menuOption == 0) {
                    gameState = "GAME"; //resume
                }
                if (menuOption == 1) {
                    createInvaders(); //restart
                    resetPlayer();
                    player.playerHealth = 3;
                    score = 0;
                    gameState = "GAME";
                }
                if (menuOption == 2) {
                    gameState = "MENU"; //main menu
                }
            }
            return;
        }

        //controls
        if (e.getKeyCode() == KeyEvent.VK_A) {
            leftPressed = true;
        }
        if (e.getKeyCode() == KeyEvent.VK_D) {
            rightPressed = true;
        }
        if (e.getKeyCode() == KeyEvent.VK_SPACE) {
            player.shoot();
        }
        if (e.getKeyCode() == KeyEvent.VK_W) {
            upPressed = true;
        }
        if (e.getKeyCode() == KeyEvent.VK_S) {
            downPressed = true;
        }
    }

    public void keyReleased(KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.VK_A) {
            leftPressed = false;
        }
        if (e.getKeyCode() == KeyEvent.VK_D) {
            rightPressed = false;
        }
        if (e.getKeyCode() == KeyEvent.VK_W) {
            upPressed = false;
        }
        if (e.getKeyCode() == KeyEvent.VK_S) {
            downPressed = false;
        }
    }

    //appearing the things
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        g.setColor(Color.WHITE); //js make all the text white bcs backgrounds are black

        //backgrounds
        if (gameState.equals("MENU")||gameState.equals("PAUSE")){
            g.drawImage(menu, 0, 0, getWidth(), getHeight(), null);
        }
        if (gameState.equals("GAME")) {
            g.drawImage(game, 0, 0, getWidth(), getHeight(), null);
        }

        //menu screen
        if (gameState.equals("MENU")) {
            g.setFont(new Font("ARCADECLASSIC", Font.PLAIN, 40));
            g.drawString("PLAY", 100, 300);

            g.setFont(new Font("ARCADECLASSIC", Font.PLAIN, 20));
            g.drawString("QUIT", 110, 350);

            //selecting the things, with the > sign
            if (menuOption == 0) {
                g.drawString(">", 70, 300);
            }
            if (menuOption == 1) {
                g.drawString(">", 80, 350);
            }
            return;
        }

        //pause screen
        if (gameState.equals("PAUSE")) {
            g.setFont(new Font("ARCADECLASSIC", Font.PLAIN, 40));
            g.drawString("PAUSED", 100, 250);

            g.setFont(new Font("ARCADECLASSIC", Font.PLAIN, 20));
            g.drawString("RESUME", 110, 400);
            g.drawString("RESTART", 110, 480);
            g.drawString("MAIN MENU", 110, 560);

            //also selecting the things
            if (menuOption == 0) {
                g.drawString(">", 80, 400);
            }

            if (menuOption == 1) {
                g.drawString(">", 75, 480);
            }

            if (menuOption == 2) {
                g.drawString(">", 55, 560);
            }
            return;
        }

        player.draw(g);

        //drawring the invaders swarm/pattern things
        for (int i = 0; i < invaders.size(); i++) {
            Invader invader = invaders.get(i);
            invader.draw(g);
        }
        if (player.playerHealth == 3) {
            g.drawImage(hp3, 20, 20, 180, 60, null);
        } else if (player.playerHealth == 2) {
            g.drawImage(hp2, 20, 20, 180, 60, null);
        } else if (player.playerHealth == 1) {
            g.drawImage(hp1, 20, 20, 180, 60, null);
        } else {
            g.drawImage(hp0, 20, 20, 180, 60, null);
        }
        for (int i = 0; i < invaderBullets.size(); i++) {
            invaderBullets.get(i).draw(g);
        }
        //score text
            g.setFont(new Font("ARCADECLASSIC", Font.PLAIN, 60));
            String scoreText = "SCORE: " + score;
            int scoreWidth = g.getFontMetrics().stringWidth(scoreText);
            g.drawString(scoreText, getWidth() - scoreWidth - 20, 65);

        //high score text
            g.setFont(new Font("ARCADECLASSIC", Font.PLAIN, 40));
            String highScoreText = "HI:" + highScore;
            int highScoreWidth = g.getFontMetrics().stringWidth(highScoreText);
            g.drawString(highScoreText, getWidth() - highScoreWidth - 20, 100);


    }
}
