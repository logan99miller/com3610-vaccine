import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.HashMap;

public class MapPanel extends JPanel {

    private ArrayList<HashMap<String, String>> factories, transporterLocations, distributionCentres, vaccinationCentres;
    final private int SCALE = 1;

    public MapPanel(VaccineSystem vaccineSystem, ArrayList<HashMap<String, String>> factories,
        ArrayList<HashMap<String, String>> transporterLocations, ArrayList<HashMap<String, String>> distributionCentres,
        ArrayList<HashMap<String, String>> vaccinationCentres) {

        this.factories = factories;
        this.transporterLocations = transporterLocations;
        this.distributionCentres = distributionCentres;
        this.vaccinationCentres = vaccinationCentres;
    }

    private void drawFacility(Graphics g, ArrayList<HashMap<String, String>> facilities, String facilityType) {
        g.setColor(Color.BLACK);
        for (HashMap<String, String> facility : facilities) {
            int x = Integer.parseInt(facility.get("x"));
            int y = Integer.parseInt(facility.get("y"));
            switch(facilityType) {
                case "factory":
                    drawFactory(g, x, y);
                    break;
                case "transporterLocation":
                    drawTransporterLocation(g, x, y);
                    break;
                case "distributionCentre":
                    drawDistributionCentre(g, x, y);
                    break;
                case "vaccinationCentre":
                    drawVaccinationCentre(g, x, y);
                    break;
            }
        }
    }

    private void drawFactory(Graphics g, int x, int y) {
        int[] xPoints = {0,  20, 20, 12, 12, 6, 6, 0};
        int[] yPoints = {15, 15, 3,  6,  3,  6, 0, 0};
        fillPolygon(g, xPoints, yPoints, x, y);
    }

    private void drawTransporterLocation(Graphics g, int x, int y) {
        int[] xPoints = {0,  16, 16, 20, 20, 16, 16, 0};
        int[] yPoints = {15, 15, 12, 12, 5,  2,  0,  0};
        fillPolygon(g, xPoints, yPoints, x, y);
    }

    private void drawDistributionCentre(Graphics g, int x, int y) {
        int[] xPoints = {0,  20, 20, 14, 0};
        int[] yPoints = {15, 15, 10, 2,  10};
        fillPolygon(g, xPoints, yPoints, x, y);
    }

    private void drawVaccinationCentre(Graphics g, int x, int y) {
        int[] xPoints = {4,   4,  8, 8, 4, 4, 8, 0, 4, 4, 0, 0,  4};
        int[] yPoints = {15, 11, 11, 3, 3, 0, 0, 0, 0, 3, 3, 11, 11};
        fillPolygon(g, xPoints, yPoints, x, y);
    }

    private void fillPolygon(Graphics g, int[] xPoints, int[] yPoints, int panelX, int panelY) {
        for (int i = 0; i < xPoints.length; i++) {
            xPoints[i] = (xPoints[i] + panelX) * SCALE;
            yPoints[i] = (yPoints[i] + panelY) * SCALE;
        }
        g.drawPolygon(xPoints, yPoints, xPoints.length);
    }

    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        drawFacility(g, factories, "factory");
        drawFacility(g, transporterLocations, "transporterLocation");
        drawFacility(g, distributionCentres, "distributionCentre");
        drawFacility(g, vaccinationCentres, "vaccinationCentre");
    }
}
