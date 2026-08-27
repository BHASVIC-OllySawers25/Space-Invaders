//imports
import javax.swing.JFrame;
import javax.swing.JPanel;

//main class
public class main {
    public static void main(String[] args) {
        JFrame window = new JFrame();
            window.setTitle("Space Invaders");
            window.setSize(700, 900);
            window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        JPanel gamePanel = new GamePanel();
            window.add(gamePanel);
            window.setVisible(true);
    }
}