//imports
import javax.swing.ImageIcon;

public class InvaderBullet extends Bullet {

    //constructor
    public InvaderBullet(int x, int y) {
        super(x, y);
        bulletImage = new ImageIcon("images/invaderBulletIdle.png").getImage();
    }

    public void move() { //overiding the move method from normal bullet
        bulletY += bulletSpeed;
    }
}