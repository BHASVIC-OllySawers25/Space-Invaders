//imports
import java.awt.*;
import javax.swing.ImageIcon;

public class Invader {

    //variables
    int invaderX;
    int invaderY;
    Image invaderImage;
    Image[] brokenImages = new Image[3];
    boolean broken = false;
    int brokenFrame = 0;
    int brokenCounter = 0;
    int brokenDelay = 5;

    //constructor
    public Invader(int x, int y) {
        invaderX = x;
        invaderY = y;
        invaderImage = new ImageIcon("images/invaderIdle.png").getImage();
        brokenImages[0] = new ImageIcon("images/invaderBroken1.png").getImage();
        brokenImages[1] = new ImageIcon("images/invaderBroken2.png").getImage();
        brokenImages[2] = new ImageIcon("images/invaderBroken3.png").getImage();
    }

    //methods
    public void move(int direction) {
        invaderX += direction;
    }

    public boolean animationDel() {
        return brokenFrame >= 3;
    }

    public void update() {
        if (broken) {
            brokenCounter++;
            if (brokenCounter >= brokenDelay) {
                brokenCounter = 0;
                brokenFrame++;
            }
        }
    }

    public void draw(Graphics g) {
        if (broken) {
            if (brokenFrame < brokenImages.length) {
                g.drawImage(brokenImages[brokenFrame], invaderX, invaderY, null);
            }
        } else {
            g.drawImage(invaderImage, invaderX, invaderY, null);
        }
    }
}
