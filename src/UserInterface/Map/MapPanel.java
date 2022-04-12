/**
 * Panel displaying the location of each factory, distribution centre, vaccination centre and transporter location and a line
 * indicating any van moving between them. Used by the map page.
 */
package UserInterface.Map;

import Data.Data;
import Data.Utils;
import Core.VaccineSystem;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

public class MapPanel extends JPanel {

    private HashMap<String, HashMap<String, Object>> allLocations, factories, transporterLocations, distributionCentres, vaccinationCentres, vans;
    private float xMin, yMin, xScale, yScale;
    private final int HALF_ICON_SIZE = 10;
    private final int ICON_SIZE = 2 * HALF_ICON_SIZE;
    private Image backgroundImage, vaccinationCentreImage, distributionCentreImage, transporterLocationImage, factoryImage;

    public MapPanel(VaccineSystem vaccineSystem, int width, int height) {
        this.setPreferredSize(new Dimension(width, height));

        Border border = BorderFactory.createLineBorder(Color.black);
        this.setBorder(border);

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

        try {
            backgroundImage = ImageIO.read(new File("src/UserInterface/Map/mapBackground.png"));
            vaccinationCentreImage = ImageIO.read(new File("src/UserInterface/Map/vaccinationCentre.png"));
            distributionCentreImage = ImageIO.read(new File("src/UserInterface/Map/distributionCentre.png"));
            transporterLocationImage = ImageIO.read(new File("src/UserInterface/Map/transporterLocation.png"));
            factoryImage = ImageIO.read(new File("src/UserInterface/Map/factory.png"));
        }
        catch (IOException e) {
            e.printStackTrace();
        }

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
                g.drawImage(factoryImage, x, y, ICON_SIZE, ICON_SIZE, null);
//                drawFactory(g, x, y);
                break;
            case "transporterLocation":
                g.drawImage(transporterLocationImage, x, y, ICON_SIZE, ICON_SIZE, null);
//                drawTransporterLocation(g, x, y);
                break;
            case "distributionCentre":
                g.drawImage(distributionCentreImage, x, y, ICON_SIZE, ICON_SIZE, null);
//                drawDistributionCentre(g, x, y);
                break;
            case "vaccinationCentre":
                g.drawImage(vaccinationCentreImage, x, y, ICON_SIZE, ICON_SIZE, null);
//                drawVaccinationCentre(g, x, y);
                break;
        }
    }

    private int getX(HashMap<String, Object> facility) {
        float longitude = Float.parseFloat((String) facility.get("Location.longitude"));
        return Math.round((longitude - xMin) * xScale);
//        return HALF_ICON_SIZE + Math.round((longitude - xMin) * xScale);
    }

    private int getY(HashMap<String, Object> facility) {
        float latitude = Float.parseFloat((String) facility.get("Location.latitude"));
        return Math.round((latitude - yMin) * yScale);
//        return HALF_ICON_SIZE + Math.round((latitude - yMin) * yScale);
    }

    private int getLineX(HashMap<String, Object> facility) {
        return HALF_ICON_SIZE + getX(facility);
    }

    private int getLineY(HashMap<String, Object> facility) {
        return HALF_ICON_SIZE + getY(facility);
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
        BasicStroke stroke = new BasicStroke(2);
        Graphics2D g2d = (Graphics2D) g;
        g2d.setStroke(stroke);

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
                drawDeliveryLine(g2d, transporterLocation, origin, getProgress(van));
            }

            // Draws a delivery line between the origin and the destination
            else if (deliveryStage.equals("toDestination")) {
                drawDeliveryLine(g2d, origin, destination, getProgress(van));
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
     * @param g2d required to draw to the panel
     * @param locationA the location to van is coming from
     * @param locationB the location the van is going to
     * @param progress how far the van is on its journey as a percentage from 0 to 1
     */
    private void drawDeliveryLine(Graphics2D g2d, HashMap<String, Object> locationA, HashMap<String, Object> locationB, float progress) {
        int aX = getLineX(locationA);
        int aY = getLineY(locationA);
        int bX = getLineX(locationB);
        int bY = getLineY(locationB);

        g2d.drawLine(aX, aY, bX, bY);

        // Draw the progress line
        g2d.setColor(Color.RED);
        int midX = Math.round((aX * (1 - progress)) + (bX * progress));
        int midY = Math.round((aY * (1 - progress)) + (bY * progress));
        g2d.drawLine(aX, aY, midX, midY);
    }

    /**
     * Draws a line representing 1km on the x and y axis
     * @param g required to draw to the panel
     */
    private void drawScale(Graphics g) {
        final int KM_PER_COORDINATE = 111;
        final int BORDER = 10;

        final int WIDTH = this.getPreferredSize().width;
        final int HEIGHT = this.getPreferredSize().height;

        int longitudeLength = Math.round((xScale / KM_PER_COORDINATE));
        int latitudeLength = Math.round((yScale / KM_PER_COORDINATE));

        int startX = BORDER;
        int startY = HEIGHT - BORDER;

        g.drawLine(startX, startY, startX + longitudeLength, startY);
        g.drawString("1km", startX + (longitudeLength / 2), startY - 2);

        startX = BORDER;
        startY = HEIGHT - (2 * BORDER);

        g.drawLine(startX, startY, startX, startY - latitudeLength);
        g.drawString("1km", startX + 2, startY - (latitudeLength / 2));
    }

    public void paintComponent(Graphics g) {
        super.paintComponent(g);

        g.drawImage(backgroundImage, 0, 0, null);

        drawDeliveries(g);
        drawFacilities(g, factories, "factory");
        drawFacilities(g, transporterLocations, "transporterLocation");
        drawFacilities(g, distributionCentres, "distributionCentre");
        drawFacilities(g, vaccinationCentres, "vaccinationCentre");
        drawScale(g);
    }
}
