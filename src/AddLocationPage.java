import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.sql.SQLException;
import java.time.LocalTime;
import java.util.ArrayList;

public class AddLocationPage extends AddPage {

    private JTextField longitudeTextField, latitudeTextField;
    private ArrayList<AddOpeningTime> addOpeningTimes;
    protected int locationID;
    private String longitude, latitude;

    public AddLocationPage(VaccineSystem vaccineSystem, MainPage mainPage, String title) {
        super(vaccineSystem, mainPage, title);
        createLocationPanel();
        createOpeningTimesPanel();
        inputFieldsPanel.setMaximumSize(inputFieldsPanel.getMinimumSize());
    }

    private void createLocationPanel() {
        JPanel locationPanel = new JPanel(new GridLayout(0, 2));

        longitudeTextField = new JTextField();
        latitudeTextField = new JTextField();

        locationPanel.add(new JLabel("-*Longitude:"));
        locationPanel.add(longitudeTextField);
        locationPanel.add(new JLabel("-*Latitude:"));
        locationPanel.add(latitudeTextField);

        inputFieldsPanel.add(locationPanel);
    }

    private void createOpeningTimesPanel() {
        inputFieldsPanel.add(new JLabel("Opening Times:"));

        JPanel openingTimesPanel = new JPanel(new GridLayout(0, 3));

        addOpeningTimes = new ArrayList<>();
        addOpeningTimes.add(new AddOpeningTime("Monday", 9, 17));
        addOpeningTimes.add(new AddOpeningTime("Tuesday", 9, 17));
        addOpeningTimes.add(new AddOpeningTime("Wednesday", 9, 17));
        addOpeningTimes.add(new AddOpeningTime("Thursday", 9, 17));
        addOpeningTimes.add(new AddOpeningTime("Friday", 9, 17));
        addOpeningTimes.add(new AddOpeningTime("Saturday", 0, 0));
        addOpeningTimes.add(new AddOpeningTime("Sunday", 0, 0));

        for (AddOpeningTime addOpeningTime: addOpeningTimes) {
            openingTimesPanel.add(addOpeningTime.getDayPanel());
            openingTimesPanel.add(addOpeningTime.getStartTimePanel());
            openingTimesPanel.add(addOpeningTime.getEndTimePanel());
        }

        inputFieldsPanel.add(openingTimesPanel);
    }

    private boolean checkCoordinate(String coordinate) {

        if (coordinate.length() > 8) {
            return false;
        }
        else {
            try {
                float coordinateFloat = Float.parseFloat(coordinate);
                if ((coordinateFloat > 90) || (coordinateFloat < -90)) {
                    return false;
                }
            }
            catch (NumberFormatException e) {
                return false;
            }

        }
        return true;
    }

    protected boolean checkCoordinates() {
        updateCoordinates();
        if ((checkCoordinate(longitude)) && (checkCoordinate(latitude))) {
            return true;
        }
        else {
            return false;
        }
    }

    protected void createStatements() {

        if (checkCoordinates()) {
            try {
                values = longitude + ", " + latitude;
                String statement = "INSERT INTO Location (longitude, latitude) VALUES (" + values + ");";
                vaccineSystem.executeUpdate(statement);
                String[] columnNames = new String[]{"MAX(locationID)"};
                ArrayList<ArrayList<String>> resultSet = vaccineSystem.executeSelect(columnNames, "Location");
                locationID = Integer.parseInt(resultSet.get(0).get(0));
            } catch (SQLException ex) {
            } catch (NumberFormatException ex) {
            }

            for (AddOpeningTime addOpeningTime : addOpeningTimes) {
                String day = addOpeningTime.getDay();
                LocalTime startTime = addOpeningTime.getStartTime();
                LocalTime endTime = addOpeningTime.getEndTime();

                values = locationID + ", \"" + day + "\", '" + startTime + "', '" + endTime + "'";
                statements.add("INSERT INTO OpeningTime (locationID, day, startTime, endTime) VALUES (" + values + ");");
            }
        }
    }

    protected void updateCoordinates() {
        longitude = longitudeTextField.getText();
        latitude = latitudeTextField.getText();
    }

    private void coordinateFieldMessage() {
        String message = "Longitude & latitude must be between -90 & 90 and have only up to 6 decimal places";
        String title = "Error";
        JOptionPane.showMessageDialog(null, message, title, JOptionPane.ERROR_MESSAGE);
    }

    public void actionPerformed(ActionEvent e) {

        if (e.getSource() == submitButton) {
            if (checkCoordinates()) {
                super.actionPerformed(e);
            }
            else {
                coordinateFieldMessage();
            }
        }
        else {
            super.actionPerformed(e);
        }
    }
}
