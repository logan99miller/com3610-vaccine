package UserInterface.AddPages;

import UserInterface.AddPage;
import Core.VaccineSystem;
import UserInterface.AddUtils.AddOpeningTime;
import UserInterface.AddUtils.Insert;
import UserInterface.LoggedInPage;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.time.LocalTime;
import java.util.ArrayList;

import static UserInterface.AddUtils.CheckInputs.checkCoordinates;
import static UserInterface.Utils.*;

public class AddLocationPage extends AddPage {

    private JTextField longitudeTextField, latitudeTextField;
    private ArrayList<AddOpeningTime> addOpeningTimes;
    protected String locationID;

    public AddLocationPage(VaccineSystem vaccineSystem, LoggedInPage loggedInPage, String title) {
        super(vaccineSystem, loggedInPage, title);

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

    protected boolean checkInputConditions(boolean displayError) {
        if (super.checkInputConditions(displayError)) {
            Component previousComponent = new JPanel();

            for (Component component : inputGridPanel.getComponents()) {
                if (previousComponent instanceof JLabel) {
                    String label = ((JLabel) previousComponent).getText().toLowerCase();
                    if (component instanceof JTextField) {
                        String text = ((JTextField) component).getText();
                        if (!checkCoordinates(label, text)) {
                            errorMessage("Coordinates values must be between -90 and 90", displayError);
                            return false;
                        }
                    }
                }
                previousComponent = component;
            }
            return true;
        }
        else {
            return false;
        }
    }

    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == submitButton) {
            if (checkInputConditions(true)) {
                super.actionPerformed(e);
            }
        }
        else {
            super.actionPerformed(e);
        }
    }
}
