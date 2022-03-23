/**
 * Panel displaying the location of each factory, distribution centre, vaccination centre and transporter location and a line
 * indicating any van moving between them. Used by the map page.
 */
package UserInterface.Map;

import Data.Data;
import Data.Utils;
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

        // Allows us to easily iterate through all location to determine the maps scale and minimum coordinate values
        allLocations = new HashMap<>();
        Utils.mergeMaps(allLocations, factories, "f");
        Utils.mergeMaps(allLocations, transporterLocations, "t");
        Utils.mergeMaps(allLocations, distributionCentres, "d");
        Utils.mergeMaps(allLocations, vaccinationCentres, "v");
        Utils.mergeMaps(allLocations, factories, "f");

        setScaleAndMin();
    }

    /**
     * Sets the scale (how many pixels per coordinate integer) and the minimum x and y values for all the locations
     */
    private void setScaleAndMin() {

        if (allLocations.size() > 0) {

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

            final int BORDER = 10;

            // (ICON_SIZE * 2) subtracted from width and height as the icon's x and y values are for the top left of the icon
            xScale = (this.getPreferredSize().width - (ICON_SIZE * 2) - BORDER) / longitudeRange;
            yScale = (this.getPreferredSize().height - (ICON_SIZE * 2) - BORDER) / latitudeRange;
        }
    }

    /**
     * Draws all the given facilities to the map panel
     * @param g required to draw to the panel
     * @param facilities the list of facilities to draw (e.g. all factories), represented by a hashmap of hashmaps in
     *                   the format HashMap<primaryKeyValue, HashMap<columName, databaseValue>>
     * @param facilityType the type of facility to draw (used to determine which shape to use)
     */
    private void drawFacilities(Graphics g, HashMap<String, HashMap<String, Object>> facilities, String facilityType) {
        for (String key : facilities.keySet()) {
            drawFacility(g, facilities.get(key), facilityType);
        }
    }

    /**
     * Draws the given facility to the map panel
     * @param g required to draw to the panel
     * @param facility the facility to draw, represented by a hashmap in the format HashMap<columName, databaseValue>
     * @param facilityType the type of facility to draw (used to determine which shape to use)
     */
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

    /**
     * Draws a circle
     */
    private void drawFactory(Graphics g, int x, int y) {
        x -= HALF_ICON_SIZE;
        y -= HALF_ICON_SIZE;
        g.fillOval(x, y, ICON_SIZE, ICON_SIZE);
    }

    /**
     * Draws a square
     */
    private void drawTransporterLocation(Graphics g, int x, int y) {
        x -= HALF_ICON_SIZE;
        y -= HALF_ICON_SIZE;
        g.fillRect(x, y, ICON_SIZE, ICON_SIZE);
    }

    /**
     * Draws a triangle
     */
    private void drawDistributionCentre(Graphics g, int x, int y) {
        int[] xPoints = new int[] {x - HALF_ICON_SIZE, x, x + HALF_ICON_SIZE};
        int[] yPoints = new int[] {y + HALF_ICON_SIZE, y - HALF_ICON_SIZE, y + HALF_ICON_SIZE};
        g.fillPolygon(xPoints, yPoints, 3);
    }

    /**
     * Draws a diamond
     */
    private void drawVaccinationCentre(Graphics g, int x, int y) {
        int[] xPoints = new int[] {x - HALF_ICON_SIZE, x, x + HALF_ICON_SIZE, x};
        int[] yPoints = new int[] {y, y - HALF_ICON_SIZE, y, y + HALF_ICON_SIZE};
        g.fillPolygon(xPoints, yPoints, 4);
    }

    /**
     * Draws lines between the locations that vans are driving between to show a delivery is taking place
     */
    private void drawDeliveries(Graphics g) {
        HashMap<String, Object> origin = new HashMap<>();
        HashMap<String, Object> destination = new HashMap<>();
        HashMap<String, Object> transporterLocation = new HashMap<>();

        // Draws a line for each van in the toOrigin delivery stage or toDestination delivery stage
        for (String keyI : vans.keySet()) {

            HashMap<String, Object> van = vans.get(keyI);

            String deliveryStage = (String) van.get("Van.deliveryStage");
            String originID = (String) van.get("Van.originID");
            String destinationID = (String) van.get("Van.destinationID");
            String vansTransporterLocationID = (String) van.get("Van.transporterLocationID");


            // Gets the hash maps representing the origin, destination and transporter location for the current van
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

            // Draws a delivery line between the transporter location and the origin
            if (deliveryStage.equals("toOrigin")) {
                drawDeliveryLine(g, transporterLocation, origin, getProgress(van));
            }

            // Draws a delivery line between the origin and the destination
            else if (deliveryStage.equals("toDestination")) {
                drawDeliveryLine(g, origin, destination, getProgress(van));
            }
        }
    }

    /**
     * Gets the progress of the given van's journey as a percentage, where 0 is no progress and 1 means van has arrived.
     * Used to show how far along the journey the van is.
     * @param van the van to calculate its progress
     * @return  the progress as a percentage
     */
    private float getProgress(HashMap<String, Object> van) {
        float totalTime = Float.valueOf((String) van.get("Van.totalTime"));
        float remainingTime = Float.valueOf((String) van.get("Van.remainingTime"));
        return remainingTime / totalTime;
    }

    // Progress is a percentage from 0 to 1

    /**
     * Draws a black line between the two given locations to represent a delivery and a purple line covering a certain percent
     * of the black line to show the progress the van has made so far on it's delivery
     * @param g required to draw to the panel
     * @param locationA the location to van is coming from
     * @param locationB the location the van is going to
     * @param progress how far the van is on its journey as a percentage from 0 to 1
     */
    private void drawDeliveryLine(Graphics g, HashMap<String, Object> locationA, HashMap<String, Object> locationB, float progress) {
        int aX = getX(locationA);
        int aY = getY(locationA);
        int bX = getX(locationB);
        int bY = getY(locationB);

        g.drawLine(aX, aY, bX, bY);

        // Draw the progress line
        g.setColor(Color.MAGENTA);
        int midX = Math.round((aX * (1 - progress)) + (bX * progress));
        int midY = Math.round((aY * (1 - progress)) + (bY * progress));
        g.drawLine(aX, aY, midX, midY);
    }

    /**
     * Draws a line representing 1km on the x and y axis
     * @param g required to draw to the panel
     */
    private void drawScale(Graphics g) {
        final int KM_PER_COORDINATE = 111;

        final int SIDE_LINE_HEIGHT = 5;

        int longitudeLength = Math.round((xScale / KM_PER_COORDINATE));
        int latitudeLength = Math.round((yScale / KM_PER_COORDINATE));

        drawLongitudeScale(g, 0, longitudeLength, 0, SIDE_LINE_HEIGHT, 20);
        drawLatitudeScale(g, 0, SIDE_LINE_HEIGHT, SIDE_LINE_HEIGHT * 2, latitudeLength, 50);

    }

    /**
     * Draws a line which represents a km in the x-axis
     */
    private void drawLongitudeScale(Graphics g, int startX, int xLength, int startY, int yLength, int labelHeight) {
        int endX = startX + xLength;

        startY = startY + labelHeight;

        int midY = startY + (yLength / 2);
        int endY = startY + yLength;

        g.drawLine(startX, startY, startX, endY);
        g.drawLine(startX, midY, endX, midY);
        g.drawLine(endX, startY, endX, endY);
        g.drawString("1 km", startX, startY - labelHeight);
    }

    /**
     * Draws a line which represents a km in the y-axis
     */
    private void drawLatitudeScale(Graphics g, int startX, int xLength, int startY, int yLength, int labelWidth) {

        startX = startX + labelWidth;

        int midX = startX + (xLength / 2);
        int endX = startX + xLength;

        int endY = startY + yLength;

        g.drawLine(startX, startY, endX, startY);
        g.drawLine(midX, startY, midX, endY);
        g.drawLine(startX, endY, endX, endY);
        g.drawString("1 km", startX - labelWidth, startY);
    }

    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        drawDeliveries(g);
        drawFacilities(g, factories, "factory");
        drawFacilities(g, transporterLocations, "transporterLocation");
        drawFacilities(g, distributionCentres, "distributionCentre");
        drawFacilities(g, vaccinationCentres, "vaccinationCentre");
        drawScale(g);
    }
}
