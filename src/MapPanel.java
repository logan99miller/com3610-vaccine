import javax.swing.*;
import java.awt.*;
import java.util.HashMap;

public class MapPanel extends JPanel {

    public MapPanel(VaccineSystem vaccineSystem, HashMap<String, float[]> coordinateRange) {
        this.setPreferredSize(vaccineSystem.getSize());

        // Long = x, lat = y

        int panelWidth = vaccineSystem.getWidth();
        int panelHeight = vaccineSystem.getHeight();

        float minLongitude = coordinateRange.get("longitude")[0];
        float maxLongitude = coordinateRange.get("longitude")[1];
        float longitudeRange = maxLongitude - minLongitude;

        float minLatitude = coordinateRange.get("latitude")[0];
        float maxLatitude = coordinateRange.get("latitude")[1];
        float latitudeRange = maxLatitude - minLatitude;

        System.out.println(minLongitude + ", " + maxLongitude);
        System.out.println(minLatitude + ", " + maxLatitude);

        System.out.println(panelWidth + ", " + longitudeRange);
        System.out.println(panelHeight + ", " + latitudeRange);

        System.out.println(panelWidth / longitudeRange); // how many pixels per longitude cord
        System.out.println(panelHeight / latitudeRange); // how many pixel per latitude cord
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
