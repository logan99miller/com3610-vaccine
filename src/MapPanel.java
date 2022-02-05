import javax.swing.*;
import java.awt.*;
import java.util.HashMap;

public class MapPanel extends JPanel {

//    public MapPanel(HashMap<String, float[]> coordinateRange) {
    public MapPanel() {

    }

    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        g.setColor(Color.YELLOW);
        g.fillOval(10, 10, 200, 200);
        // draw Eyes
        g.setColor(Color.BLACK);
        g.fillOval(55, 65, 30, 30);
        g.fillOval(135, 65, 30, 30);
        // draw Mouth
        g.fillOval(50, 110, 120, 60);
        // adding smile
        g.setColor(Color.YELLOW);
        g.fillRect(50, 110, 120, 30);
        g.fillOval(50, 120, 120, 40);
    }

}
