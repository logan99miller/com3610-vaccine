import javax.swing.*;
import java.awt.*;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

public class MapPage extends Page {

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
            String[] storageLocationNames = {"locationID"};
            String[] factoryColumnNames = {"factoryID", "storageLocationID"};
            String[] transporterLocationColumnNames = {"transporterLocationID", "locationID"};
            String[] distributionCentreColumnNames = {"distributionCentreID", "storageLocationID"};
            String[] vaccinationCentresColumnNames = {"vaccinationCentreID", "storageLocationID"};

            ArrayList<ArrayList<String>> locations = vaccineSystem.executeSelect(locationColumnNames, "location");
            ArrayList<ArrayList<String>> storageLocations = vaccineSystem.executeSelect(storageLocationNames, "storageLocation");
            ArrayList<ArrayList<String>> factories = vaccineSystem.executeSelect(factoryColumnNames, "factory");
            ArrayList<ArrayList<String>> transporterLocations = vaccineSystem.executeSelect(transporterLocationColumnNames, "transporterLocation");
            ArrayList<ArrayList<String>> distributionCentres = vaccineSystem.executeSelect(distributionCentreColumnNames, "distributionCentre");
            ArrayList<ArrayList<String>> vaccinationCentres = vaccineSystem.executeSelect(vaccinationCentresColumnNames, "vaccinationCentre");

            HashMap<String, float[]> coordinateRange = coordinateRange(locations);

            MapPanel mapPanel = new MapPanel(vaccineSystem, coordinateRange);
            mainPanel.add(mapPanel, BorderLayout.CENTER);

            for (ArrayList<String> transporterLocation : transporterLocations) {

            }

//            MapPanel mapPanel = new MapPanel(coordinateRange);
//            mainPanel.add(mapPanel);
//
//            mapPanel.setSize(mapPanel.getMaximumSize());
//            System.out.println(mapPanel.getSize());
//
//            mapPanel.add(new JButton("Temp"));


//            ArrayList<String> locationIDs = getFacilityIDs(locations, 0);
//            ArrayList<String> factoryIDs = getFacilityIDs(factories, 0);
//            ArrayList<String> transpoterLocationIDs = getFacilityIDs(transporterLocations, 0);
//            ArrayList<String> distributionCentreIDs = getFacilityIDs(distributionCentres, 0);
//            ArrayList<String> vaccinationCentreIDs = getFacilityIDs(vaccinationCentres, 0);

        }
        catch (SQLException e) {
            e.printStackTrace();
        }

    }

    private HashMap<String, float[]> coordinateRange(ArrayList<ArrayList<String>> locations) {
        ArrayList<Float> longitudes = new ArrayList<>();
        ArrayList<Float> latitudes = new ArrayList<>();
        for (ArrayList<String> location : locations) {
            longitudes.add(Float.valueOf(location.get(1)));
            latitudes.add(Float.valueOf(location.get(2)));
        }

        HashMap<String, float[]> ranges = new HashMap<>();
        ranges.put("longitude", arrayListRange(longitudes));
        ranges.put("latitude", arrayListRange(latitudes));

        return ranges;
    }

    private float[] arrayListRange(ArrayList<Float> arrayList) {
        float min = Collections.min(arrayList);
        float max = Collections.max(arrayList);
        return new float[] {min, max};
    }

//    private ArrayList<String> getFacilityIDs(ArrayList<ArrayList<String>> facilities, int index) {
//        ArrayList<String> facilityIDs = new ArrayList<>();
//        for (ArrayList<String> facility : facilities) {
//            facilityIDs.add(facility.get(index));
//        }
//        return facilityIDs;
//    }

}
