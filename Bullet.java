//imports
import java.awt.*;
import javax.swing.ImageIcon;

public class Bullet {

    //variables
    int bulletX;
    int bulletY;
    int bulletSpeed = 8;
    Image bulletImage;
    boolean hit = false;
    Image bulletHitImage;
    int hitCounter = 0;

    //construtor
    public Bullet(int x, int y) {
        bulletX = x;
        bulletY = y;
        bulletImage = new ImageIcon("images/bulletSimpleIdle.png").getImage();
        bulletHitImage = new ImageIcon("images/bulletSimpleHit.png").getImage();
    }

    //methods
    public void move() {
        if (!hit){
        bulletY -= bulletSpeed; //so the y decreases by the speed value, letting it travel upscreen
        }
    }

    //checks if the bullet is hit and helps delete it after 5 game cycles
    public boolean updateHit() {
        if (hit) {
            hitCounter++;
            if (hitCounter >= 5) {
                return true;
            }
        }
        return false;
    }

    public void draw(Graphics g) {
        if (hit) {
            g.drawImage(bulletHitImage, bulletX, bulletY, null);
        } else {
            g.drawImage(bulletImage, bulletX, bulletY, null);
        }
    }

}
