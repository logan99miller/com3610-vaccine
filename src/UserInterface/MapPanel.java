package UserInterface;

import Core.Data;
import Core.VaccineSystem;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

public class MapPanel extends JPanel {

    private HashMap<String, HashMap<String, Object>> allLocations, factories, transporterLocations, distributionCentres, vaccinationCentres, vans;
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
        vans = data.getVans();

        setScaleAndOffset();
    }

    // The longitude / latitude distance of one pixel
    private void setScaleAndOffset() {
        HashMap<String, HashMap<String, Object>> allLocations = setAllLocations();

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
    }

    private HashMap<String, HashMap<String, Object>> setAllLocations() {
        allLocations = new HashMap<>();
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

        int x = getX(facility);
        int y = getY(facility);

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

    private int getX(HashMap<String, Object> facility) {
        float longitude = Float.parseFloat((String) facility.get("Location.longitude"));
        return HALF_ICON_SIZE + Math.round((longitude - xMin) * xScale);
    }

    private int getY(HashMap<String, Object> facility) {
        float latitude = Float.parseFloat((String) facility.get("Location.latitude"));
        return HALF_ICON_SIZE + Math.round((latitude - yMin) * yScale);
    }

    private void drawFactory(Graphics g, int x, int y) {
        x -= HALF_ICON_SIZE;
        y -= HALF_ICON_SIZE;
        g.fillOval(x, y, ICON_SIZE, ICON_SIZE);
    }

    private void drawTransporterLocation(Graphics g, int x, int y) {
        x -= HALF_ICON_SIZE;
        y -= HALF_ICON_SIZE;
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

    private void drawDeliveries(Graphics g) {
        HashMap<String, Object> origin = new HashMap<>();
        HashMap<String, Object> destination = new HashMap<>();
        HashMap<String, Object> transporterLocation = new HashMap<>();

        for (String keyI : vans.keySet()) {
            HashMap<String, Object> van = vans.get(keyI);
            String deliveryStage = (String) van.get("Van.deliveryStage");
            String originID = (String) van.get("Van.originID");
            String destinationID = (String) van.get("Van.destinationID");
            String vansTransporterLocationID = (String) van.get("Van.transporterLocationID");

            for (String keyJ : allLocations.keySet()) {

                HashMap<String, Object> location = allLocations.get(keyJ);
                String locationID = (String) location.get("Location.locationID");
                String locationsTransporterLocationID = (String) location.get("TransporterLocation.transporterLocationID");

                if (originID.equals(locationID)) {
                    origin = location;
                }
                else if (destinationID.equals(locationID)) {
                    destination = location;
                }
                if (vansTransporterLocationID.equals(locationsTransporterLocationID)) {
                    transporterLocation = location;
                }
            }

            if (deliveryStage.equals("toOrigin")) {
                drawDeliveryLine(g, transporterLocation, origin, getProgress(van));
            }
            else if (deliveryStage.equals("toDestination")) {
                drawDeliveryLine(g, origin, destination, getProgress(van));
            }
        }
    }
    
    private float getProgress(HashMap<String, Object> van) {
        float totalTime = Float.valueOf((String) van.get("Van.totalTime"));
        float remainingTime = Float.valueOf((String) van.get("Van.remainingTime"));
        return remainingTime / totalTime;
    }

    // Progress is a percentage from 0 to 1
    private void drawDeliveryLine(Graphics g, HashMap<String, Object> locationA, HashMap<String, Object> locationB, float progress) {
        int aX = getX(locationA);
        int aY = getY(locationA);
        int bX = getX(locationB);
        int bY = getY(locationB);

        g.drawLine(aX, aY, bX, bY);

        g.setColor(Color.MAGENTA);
        progress = 0.3F;
        int midX = Math.round((aX * (1 - progress)) + (bX * progress));
        int midY = Math.round((aY * (1 - progress)) + (bY * progress));
        g.drawLine(aX, aY, midX, midY);
    }

    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        drawDeliveries(g);
        drawFacilities(g, factories, "factory");
        drawFacilities(g, transporterLocations, "transporterLocation");
        drawFacilities(g, distributionCentres, "distributionCentre");
        drawFacilities(g, vaccinationCentres, "vaccinationCentre");
    }
}
