import javax.swing.*;
import java.awt.*;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

public class MapPage extends Page {

    private float xScale, yScale, xAddition, yAddition;

    public MapPage(VaccineSystem vaccineSystem, MainPage mainPage) {
        super(vaccineSystem);

        mainPanel = new JPanel();

//        setMaxWidthMinHeight(mapPanel);
//        mapPanel.setPreferredSize(new Dimension(400, 400));
//        mapPanel.setPreferredSize(vaccineSystem.getSize());
//        mapPanel.revalidate();
//        mapPanel.repaint();

        try {
            String[] locationColumnNames = {"locationID", "longitude", "latitude"};
            String[] storageLocationNames = {"storageLocationID", "locationID"};
            String[] factoryColumnNames = {"factoryID", "storageLocationID"};
            String[] transporterLocationColumnNames = {"transporterLocationID", "locationID"};
            String[] distributionCentreColumnNames = {"distributionCentreID", "storageLocationID"};
            String[] vaccinationCentresColumnNames = {"vaccinationCentreID", "storageLocationID"};

//            ArrayList<HashMap<String, String>> locations = createMaps(locationColumnNames, "location");
//            ArrayList<HashMap<String, String>> storageLocations = createMaps(storageLocationNames, "storageLocation");
            ArrayList<HashMap<String, String>> locations = vaccineSystem.executeSelect2(locationColumnNames, "location");
            ArrayList<HashMap<String, String>> storageLocations = vaccineSystem.executeSelect2(storageLocationNames, "storageLocation");

            if (locations.size() > 0) {
                HashMap<String, Float> coordinateRange = coordinateRange(locations);

                final int BORDER = 20;

                final int NAV_PANEL_HEIGHT = mainPage.getNavPanel().getPreferredSize().height;

                final int MAP_PANEL_WIDTH = vaccineSystem.getWidth() - BORDER;
                final int MAP_PANEL_HEIGHT = vaccineSystem.getHeight() - (2 * NAV_PANEL_HEIGHT);

                xScale = (MAP_PANEL_WIDTH - BORDER) / coordinateRange.get("longitudeRange");
                yScale = (MAP_PANEL_HEIGHT - BORDER) / coordinateRange.get("latitudeRange");

                xAddition = -BORDER;

                if (coordinateRange.get("longitudeMin") < 0) {
                    xAddition = Math.abs(coordinateRange.get("longitudeMin"));
                }
                if (coordinateRange.get("latitudeMin") < 0) {
                    yAddition = Math.abs(coordinateRange.get("latitudeMin"));
                }

                ArrayList<HashMap<String, String>> factories = createFacilityMaps(factoryColumnNames, "factory", locations, storageLocations);
                ArrayList<HashMap<String, String>> transporterLocations = createFacilityMaps(transporterLocationColumnNames, "transporterLocation", locations);
                ArrayList<HashMap<String, String>> distributionCentres = createFacilityMaps(distributionCentreColumnNames, "distributionCentre", locations, storageLocations);
                ArrayList<HashMap<String, String>> vaccinationCentres = createFacilityMaps(vaccinationCentresColumnNames, "vaccinationCentre", locations, storageLocations);

                MapPanel mapPanel = new MapPanel(vaccineSystem, factories, transporterLocations, distributionCentres, vaccinationCentres);
                mainPanel.add(mapPanel, BorderLayout.CENTER);

                mapPanel.setBorder(BorderFactory.createLineBorder(Color.black));

                mapPanel.setPreferredSize(new Dimension(MAP_PANEL_WIDTH, MAP_PANEL_HEIGHT));
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }

    }

    private ArrayList<HashMap<String, String>> createFacilityMaps(String[] columnNames, String tableName,
     ArrayList<HashMap<String, String>> locationMaps, ArrayList<HashMap<String, String>> storageLocationMaps) throws SQLException {

//        ArrayList<HashMap<String, String>> maps = createMaps(columnNames, tableName);
        ArrayList<HashMap<String, String>> maps = vaccineSystem.executeSelect2(columnNames, tableName);

        if (maps.size() > 0) {
            if (!maps.get(0).containsKey("locationID")) {
                addValues("storageLocationID", new String[]{"locationID"}, maps, storageLocationMaps);
            }
            addValues("locationID", new String[]{"longitude", "latitude"}, maps, locationMaps);
            addScreenCoordinates(maps);
        }
        return maps;
    }

    private void addScreenCoordinates(ArrayList<HashMap<String, String>> facilities) {
        for (HashMap<String, String> facility : facilities) {
            facility.put("x", String.valueOf((int) ((Float.parseFloat(facility.get("longitude")) + xAddition) * xScale)));
            facility.put("y", String.valueOf((int) ((Float.parseFloat(facility.get("latitude")) + yAddition) * yScale)));
        }
    }

    private ArrayList<HashMap<String, String>> createFacilityMaps(String[] columnNames, String tableName,
      ArrayList<HashMap<String, String>> locationMaps) throws SQLException {
        return createFacilityMaps(columnNames, tableName, locationMaps, null);
    }

//    private ArrayList<HashMap<String, String>> createMaps(String[] columnNames, String tableName) throws SQLException {
//        ArrayList<ArrayList<String>> arrayLists = vaccineSystem.executeSelect(columnNames, tableName);
//        return arrayListToMap(arrayLists, columnNames);
//    }

    private ArrayList<HashMap<String, String>> arrayListToMap(ArrayList<ArrayList<String>> arrayLists, String[] columnNames) {
        ArrayList<HashMap<String, String>> maps = new ArrayList<>();
        for (ArrayList<String> arrayList : arrayLists) {
            HashMap<String, String> map = new HashMap<>();
            for (int i = 0; i < columnNames.length; i++) {
                map.put(columnNames[i], arrayList.get(i));
            }
            maps.add(map);
        }
        return maps;
    }

    private void addValues(String foreignKey, String[] valuesToAdd,
                           ArrayList<HashMap<String, String>> primaryMaps, ArrayList<HashMap<String, String>> secondaryMaps) {

        for (HashMap<String, String> primaryMap : primaryMaps) {
            for (HashMap<String, String> secondaryMap : secondaryMaps) {
                if (primaryMap.get(foreignKey).equals(secondaryMap.get(foreignKey))) {
                    for (String valueToAdd : valuesToAdd) {
                        primaryMap.put(valueToAdd, secondaryMap.get(valueToAdd));
                    }
                }
            }
        }
    }

    private HashMap<String, Float> coordinateRange(ArrayList<HashMap<String, String>> locations) {
        ArrayList<Float> longitudes = new ArrayList<>();
        ArrayList<Float> latitudes = new ArrayList<>();
        for (HashMap<String, String> location : locations) {
            longitudes.add(Float.valueOf(location.get("longitude")));
            latitudes.add(Float.valueOf(location.get("latitude")));
        }

        float longitudeMin = Collections.min(longitudes);
        float latitudeMin = Collections.min(latitudes);

        HashMap<String, Float> ranges = new HashMap<>();
        ranges.put("longitudeMin", longitudeMin);
        ranges.put("latitudeMin", latitudeMin);
        ranges.put("longitudeRange", Collections.max(longitudes) - longitudeMin);
        ranges.put("latitudeRange", Collections.max(latitudes) - latitudeMin);

        return ranges;
    }
}
