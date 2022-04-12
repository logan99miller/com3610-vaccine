/**
 * Used to display different tables in the database as tables on the screen. If the table being displayed has many useful values
 * associated with it found in other tables that it links to (e.g. a vaccination centre with multiple booking values in the
 * bookings table), these values are accessed through a subKeys HashMap in the format
 * HashMap<subTableName, List<keyOfSubTable>>. Note subTableName is the table name given in the Data class, it does not
 * necessarily have to be the name in the SQL database, however it usually is.
 */
package UserInterface;

import Data.Data;
import Data.Utils;
import Core.VaccineSystem;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.sql.SQLException;
import java.util.*;
import java.util.List;

public class ViewPage extends Page {

    private LoggedInPage loggedInPage;
    private JButton backButton, refreshButton;
    private JScrollPane tableScrollPane;
    private JLabel noDataLabel;
    private HashMap<String, HashMap<String, Object>> maps;
    private String mapKey;
    private List<String> keys;
    private List<String> headings;
    private JFrame frame;
    private Data data;

    /**
     * The constructor used when a view page is created in the main window.
     * @param vaccineSystem used to access data in the Data class and to delete records if requested by the user
     * @param loggedInPage used to return to the SelectViewPage page if the user presses the back button
     * @param tableName which table is to be displayed (a key associated with the allMaps hash map)
     * @param keys the keys in the hashmap of data to be displayed (makes up the columns to be displayed)
     * @param headings the headings for each column. Should be the same length as the keys list
     */
    public ViewPage(VaccineSystem vaccineSystem, LoggedInPage loggedInPage, String tableName, List<String> keys, List<String> headings) {
        super(vaccineSystem);

        this.loggedInPage = loggedInPage;
        this.mapKey = tableName;
        this.keys = keys;
        this.headings = headings;

        data = vaccineSystem.getData();

        // A HashMap of all maps that could be displayed, in the format
        // HashMap<tableName, HashMap<primaryKeyValue, HashMap<columName, databaseValue>>
        HashMap<String, HashMap<String, HashMap<String, Object>>> allMaps = getAllMaps();
        maps = allMaps.get(tableName);

        mainPanel.add(createButtonPanel());

        if (maps.isEmpty()) {
            noDataLabel = new JLabel(" No data");
            mainPanel.add(noDataLabel);
        }
        else {
            tableScrollPane = createTableScrollPane();
            mainPanel.add(tableScrollPane);
        }
    }

    /**
     * The constructor used when a pop up view page is used. Record when a row in the main window would have many values
     * with the same heading associated with it, for example a person may have many medical conditions, so clicking on a button
     * next to the person's name to view all medical conditions would be needed.
     * @param vaccineSystem used to access data in the Data class and to delete records if requested by the user
     * @param maps the maps containing the data to be displayed to the screen, in the format HashMap<primaryKeyValue, HashMap<columName, databaseValue>
     * @param tableName which table is to be displayed (a key associated with a value in the subKeys and subHeadings maps)
     * @param frame teh frame the table is displayed in, used to close the frame when the user presses the back button
     */
    public ViewPage(VaccineSystem vaccineSystem, HashMap<String, HashMap<String, Object>> maps, String tableName, JFrame frame) {
        super(vaccineSystem);

        this.maps = maps;
        this.frame = frame;
        keys = getSubKeys(tableName);
        headings = getSubHeadings(tableName);

        if (maps.isEmpty()) {
            mainPanel.add(new JLabel(" No data"));
        }
        else {
            tableScrollPane = createTableScrollPane();
            mainPanel.add(tableScrollPane);
        }
    }

    /**
     * Creates a hash map of all maps from the data class that could be displayed, in the format
     * HashMap<tableName, HashMap<primaryKeyValue, HashMap<columName, databaseValue>>.
     * @return the hashmap of all maps
     */
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

    /**
     * Returns a list of keys (i.e column names) which reference the data we wish to display in the data hash map.
     * @param key the key which will get the required list of keys (i.e. the sub table's table name in the Data class)
     * @return a list of keys for the pop-up table
     */
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

    /**
     * Returns a list of headings to be displayed in the pop-up sub table
     * @param key the key which will get the required list of headings
     * @return a list of headings for the pop-up table
     */
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

    /**
     * Scroll panel used in-case all the data to be displayed cannot fit in the window
     */
    private JScrollPane createTableScrollPane() {
        JPanel scrollPanel = new JPanel();
        JPanel tablePanel = createTablePanel();
        scrollPanel.add(tablePanel);

        JScrollPane scrollPane = new JScrollPane(scrollPanel);
        scrollPane.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));

        return scrollPane;
    }

    /**
     * Only add delete buttons if the record/rows ID is given, otherwise record is a complementary/sub record (e.g. a vaccine lifespan
     * record) and should not be deleted
     * @return true if a delete button should be added, false otherwise
     */
    private boolean addDeleteButtons() {
        String IDFieldName = Utils.getIDFieldName(keys);
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

    /**
     * Adds the headings to the table panel
     * @param tablePanel the table panel to add to
     * @param addDeleteButtons true if a "Delete" heading should be added, false otherwise
     * @return the modified table panel
     */
    private JPanel addHeadings(JPanel tablePanel, boolean addDeleteButtons) {
        for (String heading : headings) {
            tablePanel.add(new JLabel(heading));
        }

        if (addDeleteButtons) {
            tablePanel.add(new JLabel("Delete"));
        }

        return tablePanel;
    }

    /**
     * Adds the contents to the table panel
     * @param tablePanel the table panel to add to
     * @param addDeleteButtons true if a "Delete" heading should be added, false otherwise
     * @return the modified table panel
     */
    private JPanel addContent(JPanel tablePanel, boolean addDeleteButtons) {

        for (String keyI : maps.keySet()) {
            HashMap<String, Object> map = maps.get(keyI);

            for (String keyJ : keys) {

                // If the value can be cast to a string, it is a value to be displayed, otherwise add a button to view
                // a sub table
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

    /**
     * Creates a button where if clicked will create a pop-up window displaying the sub table. A sub table is used when
     * the table being displayed has many useful values associated with it found in other tables that it links to (e.g.
     * a vaccination centre with multiple booking values in the bookings table)
     * @param map the original table's map
     * @param mapKey the key in the original table's map that the view button is for
     * @return the view button
     */
    private JButton createViewButton(HashMap<String, Object> map, String mapKey) {
        JButton button = new JButton("View");

        // The code to execute if the view button is pressed
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

    /**
     * Creates a button to delete the given record (represented by the given map) if the user clicks it
     * @param map a map representing the record in the format HashMap<columName, databaseValue>
     * @return the delete button
     */
    private JButton createDeleteButton(HashMap<String, Object> map) {
        JButton deleteButton = new JButton("Delete");

        // The code to execute if the delete button is pressed
        deleteButton.addActionListener(e -> {

            // The ID of the record being deleted
            String IDFieldName = Utils.getIDFieldName(keys);

            // Get the table name by removing the ID part of the ID field name (e.g. factoryID -> factory)
            String tableName = IDFieldName.substring(0, IDFieldName.length() - 2);
            tableName = capitalizeFirstLetter(tableName);

            String ID = (String) map.get(tableName + "." + IDFieldName);

            try {
                vaccineSystem.delete(IDFieldName, ID, tableName);
            } catch (SQLException ex) {
                ex.printStackTrace();
            }

            // Allows the user to see the record has been deleted
            refreshPage();
        });
        return deleteButton;
    }

    /**
     * Capitalizes the first letter of the given string. Used when creating the delete button as table names begin with
     * a capital letter.
     * @param string the string to capitalize
     * @return the modified string
     */
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

        // If there already is data, remove it
        if (tableScrollPane != null) {
            mainPanel.remove(tableScrollPane);
        }

        // If there is a message saying there is no data, remove it
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

        // When the back button is pressed, If the page is in the main window, return to the select view page, else we are
        // a pop-up frame so close the pop-up frame
        else if (e.getSource() == backButton) {
            if (frame == null) {
                loggedInPage.setPageName("view");
                loggedInPage.updatePage();
            }
            else {
                frame.setVisible(false);
            }
        }
    }
}
