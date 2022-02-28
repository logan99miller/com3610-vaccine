package UserInterface.AddPages;

import UserInterface.AddPage;
import UserInterface.Page;

import javax.swing.*;
import java.time.LocalTime;

public class AddOpeningTime extends Page {

    private String day;
    private JSpinner startTimeHourSpinner, startTimeMinuteSpinner, endTimeHourSpinner, endTimeMinuteSpinner;

    public AddOpeningTime(String day, int defaultOpeningTime, int defaultClosingTime) {
        this.day = day;

        startTimeHourSpinner = AddPage.createJSpinner(0, 24, 2);
        startTimeMinuteSpinner = AddPage.createJSpinner(0, 60, 2);
        endTimeHourSpinner = AddPage.createJSpinner(0, 24, 2);
        endTimeMinuteSpinner = AddPage.createJSpinner(0, 60, 2);

        startTimeHourSpinner.setValue(defaultOpeningTime);
        endTimeHourSpinner.setValue(defaultClosingTime);
    }

    public JPanel getDayPanel() {
        JPanel panel = new JPanel();
        panel.add(new JLabel(day + ":"));
        return panel;
    }

    public JPanel getStartTimePanel() {
        JPanel panel = new JPanel();
        panel.add(new JLabel("Start (HH:MM):"));
        panel.add(createTimePanel(startTimeHourSpinner, startTimeMinuteSpinner, 9, 0));
        return panel;
    }

    public JPanel getEndTimePanel() {
        JPanel panel = new JPanel();
        panel.add(new JLabel("End (HH:MM):"));
        panel.add(createTimePanel(endTimeHourSpinner, endTimeMinuteSpinner, 17, 0));
        return panel;
    }

    public String getDay() {
        return day;
    }

    public LocalTime getStartTime() {
        int hour = (int) startTimeHourSpinner.getValue();
        int minute = (int) startTimeMinuteSpinner.getValue();
        return LocalTime.of(hour, minute);
    }

    public LocalTime getEndTime() {
        int hour = (int) endTimeHourSpinner.getValue();
        int minute = (int) endTimeMinuteSpinner.getValue();
        return LocalTime.of(hour, minute);
    }
}
