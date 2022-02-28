package UserInterface.AddPages;

import UserInterface.AddPage;
import Core.VaccineSystem;
import UserInterface.MainPage;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.time.LocalTime;
import java.util.ArrayList;

public class AddLocationPage extends AddPage {

    private JTextField longitudeTextField, latitudeTextField;
    private ArrayList<AddOpeningTime> addOpeningTimes;
    protected String locationID;

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
        String[] columnNames = new String[] {"longitude", "latitude"};
        Object[] values = new Object[] {longitudeTextField.getText(), latitudeTextField.getText()};
        locationID = insertAndGetID(columnNames, values, "Location", "locationID");

        for (AddOpeningTime addOpeningTime : addOpeningTimes) {
            String day = addOpeningTime.getDay();
            LocalTime startTime = addOpeningTime.getStartTime();
            LocalTime endTime = addOpeningTime.getEndTime();

            columnNames = new String[] {"locationID", "day", "startTime", "endTime"};
            values = new Object[] {locationID, day, startTime, endTime};
            inserts.add(new Insert(columnNames, values, "OpeningTime"));
        }
    }

    private boolean checkCoordinates(String label, String text) {
        if (label.contains("longitude") || label.contains("latitude")) {
            try {
                float coordinate = Float.parseFloat(text);
                if ((coordinate > 90) || (coordinate < -90)) {
                    return false;
                }
            } catch (NumberFormatException e) {
                return false;
            }
        }
        return true;
    }

    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == submitButton) {
            Component previousComponent = new JPanel();

            for (Component component : inputGridPanel.getComponents()) {
                if (previousComponent instanceof JLabel) {
                    String label = ((JLabel) previousComponent).getText().toLowerCase();
                    if (component instanceof JTextField) {
                        String text = ((JTextField) component).getText();
                        if (!checkCoordinates(label, text)) {
                            errorMessage("Coordinates values must be between -90 and 90");
                            break;
                        }
                    }
                }
                previousComponent = component;
            }
            super.actionPerformed(e);
        }
        else {
            super.actionPerformed(e);
        }
    }
}
