package UserInterface;

import Core.Data;
import Core.DataUtils;
import Core.VaccineSystem;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.sql.SQLException;
import java.util.*;
import java.util.List;

public class ViewPage extends Page {

    private MainPage mainPage;
    private JButton backButton, refreshButton;
    private JScrollPane tableScrollPane;
    private JLabel noDataLabel;
    private HashMap<String, HashMap<String, Object>> maps;
    private String mapKey;
    private List<String> keys;
    private List<String> headings;
    private JFrame frame;
    private Data data;

    public ViewPage(VaccineSystem vaccineSystem, MainPage mainPage, String mapKey, List<String> keys, List<String> headings) {
        super(vaccineSystem);

        this.mainPage = mainPage;
        this.mapKey = mapKey;
        this.keys = keys;
        this.headings = headings;

        data = vaccineSystem.getData();
        HashMap<String, HashMap<String, HashMap<String, Object>>> allMaps = getAllMaps();
        maps = allMaps.get(mapKey);

        mainPanel.add(createButtonPanel());

        if (maps.isEmpty()) {
            noDataLabel = new JLabel("No data");
            mainPanel.add(noDataLabel);
        }
        else {
            tableScrollPane = createTableScrollPane();
            mainPanel.add(tableScrollPane);
        }
    }

    public ViewPage(VaccineSystem vaccineSystem, HashMap<String, HashMap<String, Object>> maps, String mapKey, JFrame frame) {
        super(vaccineSystem);

        this.maps = maps;
        this.frame = frame;
        keys = getSubKeys(mapKey);
        headings = getSubHeadings(mapKey);

        if (maps.isEmpty()) {
            mainPanel.add(new JLabel("No data"));
        }
        else {
            tableScrollPane = createTableScrollPane();
            mainPanel.add(tableScrollPane);
        }
    }

    private HashMap<String, HashMap<String, HashMap<String, Object>>> getAllMaps() {
        HashMap<String, HashMap<String, HashMap<String, Object>>> allMaps = new HashMap<>();
        allMaps.put("Factories", data.getFactories());
        allMaps.put("Transporter Locations", data.getTransporterLocations());
        allMaps.put("Distribution Centres", data.getDistributionCentres());
        allMaps.put("Vaccination Centres", data.getVaccinationCentres());
        allMaps.put("Vaccines", data.getVaccines());
        allMaps.put("People", data.getPeople());
        allMaps.put("Vans", data.getVans());
        allMaps.put("Bookings", data.getBookings());
        return allMaps;
    }

    private List<String> getSubKeys(String key) {
        HashMap<String, List<String>> subKeys = new HashMap<>();
        subKeys.put("stores", Arrays.asList("Store.temperature", "Store.capacity", "vaccinesInStorage"));
        subKeys.put("vaccinesInStorage", Arrays.asList("VaccineInStorage.vaccineID", "VaccineInStorage.stockLevel", "VaccineInStorage.creationDate", "VaccineInStorage.expirationDate"));
        subKeys.put("openingTimes", Arrays.asList("OpeningTime.day", "OpeningTime.startTime", "OpeningTime.endTime"));
        subKeys.put("lifespans", Arrays.asList("VaccineLifespan.lifespan", "VaccineLifespan.lowestTemperature", "VaccineLifespan.highestTemperature"));
        subKeys.put("exemptions", Arrays.asList("VaccineExemption.medicalConditionID", "MedicalCondition.name", "MedicalCondition.vulnerabilityLevel"));
        subKeys.put("bookings", Arrays.asList("Booking.bookingID", "Booking.personID", "Booking.vaccinationCentreID", "Booking.date"));
        subKeys.put("medicalConditions", Arrays.asList("PersonMedicalCondition.medicalConditionID"));
        subKeys.put("vaccinesReceived", Arrays.asList("VaccineReceived.vaccineID", "VaccineReceived.date"));
        return(subKeys.get(key));
    }

    private List<String> getSubHeadings(String key) {
        HashMap<String, List<String>> subHeadings = new HashMap<>();
        subHeadings.put("stores", Arrays.asList("Temperature", "Capacity", "Vaccines In Storage"));
        subHeadings.put("vaccinesInStorage", Arrays.asList("Vaccine ID", "Stock Level", "Creation Date", "Expiration Date"));
        subHeadings.put("openingTimes", Arrays.asList("Day", "Start Time", "End Time"));
        subHeadings.put("lifespans", Arrays.asList("Lifespan", "Lowest Temperature", "Highest Temperature"));
        subHeadings.put("exemptions", Arrays.asList("Medical Condition ID", "Medical Condition Name", "Vulnerability Level"));
        subHeadings.put("bookings", Arrays.asList("Booking ID", "Person ID", "Vaccination Centre ID", "Date"));
        subHeadings.put("medicalConditions", Arrays.asList("Medical Condition ID"));
        subHeadings.put("vaccinesReceived", Arrays.asList("Vaccine ID", "Date"));
        return subHeadings.get(key);
    }

    private JPanel createButtonPanel() {
        JPanel buttonPanel = new JPanel(new GridLayout(0, 2));

        backButton = new JButton("Back");
        refreshButton = new JButton("Refresh");

        addButton(backButton, buttonPanel);
        addButton(refreshButton, buttonPanel);

        setMaxWidthMinHeight(buttonPanel);

        return buttonPanel;
    }

    private JScrollPane createTableScrollPane() {
        JPanel scrollPanel = new JPanel();
        JPanel tablePanel = createTablePanel();
        scrollPanel.add(tablePanel);

        JScrollPane scrollPane = new JScrollPane(scrollPanel);
        scrollPane.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));

        return scrollPane;
    }

    private boolean addDeleteButtons() {
        String IDFieldName = DataUtils.getIDFieldName(keys);
        if (IDFieldName == "") {
            return false;
        }
        return true;
    }

    private JPanel createTablePanel() {
        boolean addDeleteButtons = addDeleteButtons();

        JPanel tablePanel = new JPanel();

        int columns = headings.size();
        if (addDeleteButtons) {
            columns += 1;
        }

        tablePanel.setLayout(new GridLayout(0, columns));

        tablePanel = addHeadings(tablePanel, addDeleteButtons);
        tablePanel = addContent(tablePanel, addDeleteButtons);

        setMaxWidthMinHeight(tablePanel);

        return tablePanel;
    }

    private JPanel addHeadings(JPanel tablePanel, boolean addDeleteButtons) {
        for (String heading : headings) {
            tablePanel.add(new JLabel(heading));
        }

        if (addDeleteButtons) {
            tablePanel.add(new JLabel("Delete"));
        }

        return tablePanel;
    }

    private JPanel addContent(JPanel tablePanel, boolean addDeleteButtons) {
        for (String keyI : maps.keySet()) {
            HashMap<String, Object> map = maps.get(keyI);
            for (String keyJ : keys) {
                try {
                    String value = (String) map.get(keyJ);
                    tablePanel.add(new JLabel(value));
                } catch (ClassCastException e) {
                    tablePanel.add(createViewButton(map, keyJ));
                }
            }

            if (addDeleteButtons) {
                tablePanel.add(createDeleteButton(map));
            }
        }
        return tablePanel;
    }

    private JButton createViewButton(HashMap<String, Object> map, String mapKey) {
        JButton button = new JButton("View");

        button.addActionListener(e1 -> {
            JFrame frame = new JFrame();
            frame.setResizable(false);

            HashMap<String, HashMap<String, Object>> subMaps = (HashMap<String, HashMap<String, Object>>) map.get(mapKey);

            ViewPage viewPage = new ViewPage(vaccineSystem, subMaps, mapKey, frame);
            frame.add(viewPage.getPanel());
            createPopupFrame(frame, viewPage.getPanel(), 800, 500);
        });
        return button;
    }

    private JButton createDeleteButton(HashMap<String, Object> map) {
        JButton deleteButton = new JButton("Delete");
        deleteButton.addActionListener(e -> {

            String IDFieldName = DataUtils.getIDFieldName(keys);
            String tableName = IDFieldName.substring(0, IDFieldName.length() - 2);
            tableName = capitalizeFirstLetter(tableName);

            String ID = (String) map.get(tableName + "." + IDFieldName);

            try {
                vaccineSystem.delete(IDFieldName, ID, tableName);
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
            refreshPage();
        });
        return deleteButton;
    }



    private String capitalizeFirstLetter(String string) {
        String firstLetter = string.substring(0, 1);
        String remainingLetters = string.substring(1);
        firstLetter = firstLetter.toUpperCase();
        return firstLetter + remainingLetters;
    }

    private void refreshPage() {
        try {
            data.read();
        }
        catch (SQLException e) {
            e.printStackTrace();
        }

        data = vaccineSystem.getData();
        HashMap<String, HashMap<String, HashMap<String, Object>>> allMaps = getAllMaps();
        maps = allMaps.get(mapKey);

        if (tableScrollPane != null) {
            mainPanel.remove(tableScrollPane);
        }
        else if (noDataLabel != null) {
            mainPanel.remove(noDataLabel);
        }

        tableScrollPane = createTableScrollPane();
        mainPanel.add(tableScrollPane);

        vaccineSystem.invalidate();
        vaccineSystem.validate();
        vaccineSystem.repaint();
    }

    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == refreshButton) {
            refreshPage();
        }
        else if (e.getSource() == backButton) {
            if (frame == null) {
                mainPage.setPageName("view");
                mainPage.updatePage();
            }
            else {
                frame.setVisible(false);
            }
        }
    }
}
