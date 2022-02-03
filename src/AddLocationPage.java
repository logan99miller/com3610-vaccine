import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.time.LocalTime;
import java.util.ArrayList;

public class AddLocationPage extends AddPage {

    private JTextField longitudeTextField, latitudeTextField;
    private ArrayList<AddOpeningTime> addOpeningTimes;
    protected int locationID;

    public AddLocationPage(VaccineSystem vaccineSystem, MainPage mainPage, String title) {
        super(vaccineSystem, mainPage, title);

        longitudeTextField = new JTextField();
        latitudeTextField = new JTextField();

        addLabelledComponent(inputGridPanel, "-*Longitude:", longitudeTextField);
        addLabelledComponent(inputGridPanel, "-*Latitude:", latitudeTextField);

        createOpeningTimesPanel();
    }

    private void createOpeningTimesPanel() {
        inputPanel.add(new JLabel("Opening Times:"));

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

        inputPanel.add(openingTimesPanel);
    }

    protected void createStatements() {
        String values = longitudeTextField.getText() + ", " + latitudeTextField.getText();
        String statement = "INSERT INTO Location (longitude, latitude) VALUES (" + values + ");";
        locationID = insertAndGetID(statement, "locationID", "Location");

        for (AddOpeningTime addOpeningTime : addOpeningTimes) {
            String day = addOpeningTime.getDay();
            LocalTime startTime = addOpeningTime.getStartTime();
            LocalTime endTime = addOpeningTime.getEndTime();

            values = locationID + ", \"" + day + "\", '" + startTime + "', '" + endTime + "'";
            statements.add("INSERT INTO OpeningTime (locationID, day, startTime, endTime) VALUES (" + values + ");");
        }
    }

    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == submitButton) {
            createStatements();
        }
        super.actionPerformed(e);
    }
}
