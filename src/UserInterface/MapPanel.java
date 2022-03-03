package UserInterface;

import Core.Data;
import Core.VaccineSystem;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

public class MapPanel extends JPanel {

    private HashMap<String, HashMap<String, Object>> factories, transporterLocations, distributionCentres, vaccinationCentres;
    private float xMin, yMin, xScale, yScale;
    private final int HALF_ICON_SIZE = 5;
    private final int ICON_SIZE = 2 * HALF_ICON_SIZE;


    public MapPanel(VaccineSystem vaccineSystem, int width, int height) {
        this.setPreferredSize(new Dimension(width, height));

        Data data = vaccineSystem.getData();
        factories = data.getFactories();
        transporterLocations = data.getTransporterLocations();
        distributionCentres = data.getDistributionCentres();
        vaccinationCentres = data.getVaccinationCentres();

        setScaleAndOffset();
    }

    // The longitude / latitude distance of one pixel
    private void setScaleAndOffset() {
        HashMap<String, HashMap<String, Object>> allLocations = getAllLocations();

        ArrayList<Float> longitudes = new ArrayList<>();
        ArrayList<Float> latitudes = new ArrayList<>();

        for (String key : allLocations.keySet()) {
            HashMap<String, Object> location = allLocations.get(key);
            longitudes.add(Float.parseFloat((String) location.get("Location.longitude")));
            latitudes.add(Float.parseFloat((String) location.get("Location.latitude")));
        }

        xMin = Collections.min(longitudes);
        yMin = Collections.min(latitudes);

        float longitudeRange = Math.abs(Collections.max(longitudes) - xMin);
        float latitudeRange = Math.abs(Collections.max(latitudes) - yMin);

        xScale = (this.getPreferredSize().width - (ICON_SIZE * 2)) / longitudeRange;
        yScale = (this.getPreferredSize().height - (ICON_SIZE * 2)) / latitudeRange;

        System.out.println("xMin, xScale, longitudeRange: " + xMin + ", " + xScale + ", " + longitudeRange);
        System.out.println("yMin, yScale, latitudeRange: " + yMin + ", " + yScale + ", " + latitudeRange);
    }

    private HashMap<String, HashMap<String, Object>> getAllLocations() {
        HashMap<String, HashMap<String, Object>> allLocations = new HashMap<>();
        Data.mergeMaps(allLocations, factories, "f");
        Data.mergeMaps(allLocations, transporterLocations, "t");
        Data.mergeMaps(allLocations, distributionCentres, "d");
        Data.mergeMaps(allLocations, vaccinationCentres, "v");
        Data.mergeMaps(allLocations, factories, "f");
        return allLocations;
    }

    private void drawFacilities(Graphics g, HashMap<String, HashMap<String, Object>> facilities, String facilityType) {
        for (String key : facilities.keySet()) {
            drawFacility(g, facilities.get(key), facilityType);
        }
    }

    private void drawFacility(Graphics g, HashMap<String, Object> facility, String facilityType) {
        g.setColor(Color.BLACK);

        float longitude = Float.parseFloat((String) facility.get("Location.longitude"));
        float latitude = Float.parseFloat((String) facility.get("Location.latitude"));

        int x = HALF_ICON_SIZE + Math.round((longitude - xMin) * xScale);
        int y = HALF_ICON_SIZE + Math.round((latitude - yMin) * yScale);

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

    private void drawFactory(Graphics g, int x, int y) {
        x += HALF_ICON_SIZE;
        y += HALF_ICON_SIZE;
        g.fillOval(x, y, ICON_SIZE, ICON_SIZE);
    }

    private void drawTransporterLocation(Graphics g, int x, int y) {
        x += HALF_ICON_SIZE;
        y += HALF_ICON_SIZE;
        g.fillRect(x, y, ICON_SIZE, ICON_SIZE);
    }

    private void drawDistributionCentre(Graphics g, int x, int y) {
        int[] xPoints = new int[] {x - HALF_ICON_SIZE, x, x + HALF_ICON_SIZE};
        int[] yPoints = new int[] {y + HALF_ICON_SIZE, y - HALF_ICON_SIZE, y + HALF_ICON_SIZE};
        g.fillPolygon(xPoints, yPoints, 3);
    }

    private void drawVaccinationCentre(Graphics g, int x, int y) {
        int[] xPoints = new int[] {x - HALF_ICON_SIZE, x, x + HALF_ICON_SIZE, x};
        int[] yPoints = new int[] {y, y - HALF_ICON_SIZE, y, y + HALF_ICON_SIZE};
        g.fillPolygon(xPoints, yPoints, 4);
    }

    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        System.out.println("Paint component");
        drawFacilities(g, factories, "factory");
        drawFacilities(g, transporterLocations, "transporterLocation");
        drawFacilities(g, distributionCentres, "distributionCentre");
        drawFacilities(g, vaccinationCentres, "vaccinationCentre");
    }


}
