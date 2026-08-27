//imports
import java.awt.*;
import javax.swing.ImageIcon;
import java.util.ArrayList;

public class Player {

    //variables
    Image playerImage;
    int playerX = 300;
    int playerY = 680;
    ArrayList<Bullet> bullets = new ArrayList<>();
    Image brokenImage1;
    Image brokenImage2;
    Image[] deathImages = new Image[3];
    int playerHealth = 3;
    boolean dying = false;
    int deathFrame = 0;
    int deathCounter = 0;
    int deathDelay = 15;


    //constructor
    public Player() {
        playerImage = new ImageIcon("images/playerIdle.png").getImage();
        brokenImage1 = new ImageIcon("images/broken1.png").getImage();
        brokenImage2 = new ImageIcon("images/broken2.png").getImage();
        deathImages[0] = new ImageIcon("images/death1.png").getImage();
        deathImages[1] = new ImageIcon("images/death2.png").getImage();
        deathImages[2] = new ImageIcon("images/death3.png").getImage();
    }

    //methods
    public void moveLeft() {
        if (playerX > 0) {
            playerX -= 6;
        }
    }

    public void moveRight() {
        if (playerX < 610) {
            playerX += 6;
        }
    }

    public void moveUp() {
        if (playerY > 550) {
            playerY -= 4;
        }
    }

    public void moveDown() {
        if (playerY < 700) {
            playerY += 4;
        }
    }

    public void shoot() {
        Bullet bullet = new Bullet(playerX, playerY);
        bullets.add(bullet);
    }

    public void updateBullets() {
        for (int i = 0; i < bullets.size(); i++) {
            Bullet bullet = bullets.get(i);
            bullet.move();
            if (bullet.bulletY < 0) {
                bullets.remove(i);
                i--; //so bullets in the AL arent skipped
            }
        }
    }

    //animation
    public void update() {
        if (dying) {
            deathCounter++;
            if (deathCounter >= deathDelay) {
                deathCounter = 0;
                deathFrame++;
            }
        }
    }

    public void draw(Graphics g) { //making the spaceship of the player show up
        if (dying) {
            if (deathFrame < 3) {
                g.drawImage(deathImages[deathFrame], playerX, playerY, null);
            }
        } else if (playerHealth == 3) {
            g.drawImage(playerImage, playerX, playerY, null);
        } else if (playerHealth == 2) {
            g.drawImage(brokenImage1, playerX, playerY, null);
        } else if (playerHealth == 1) {
            g.drawImage(brokenImage2, playerX, playerY, null);
        }
        for (int i = 0; i < bullets.size(); i++) {
            Bullet bullet = bullets.get(i);
            bullet.draw(g);
    }
}}

