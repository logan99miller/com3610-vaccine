/**
 * Allows the user to change simulation values.
 * Actual booking rate: the percentage of the population who book an appointment, e.g. 0.5 means 50% of the population book
 * vaccine appointments, used when simulating bookings
 * Actual attendance rate: the percentage of the population who attend the appointment they booked, used when simulating bookings
 * Predicated vaccination rate: the predicted percentage of the population who get vaccinated, used when vaccination centres
 * and distribution centres are ordering vaccines
 */
package UserInterface;

import Core.VaccineSystem;
import Data.Data;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.HashMap;

import static Data.Utils.getLocalDate;

public class SimulationPage extends Page {

    private Data data;
    private JPanel inputPanel;
    private JTextField actualBookingRateTextField, actualAttendanceRateTextField, predictedVaccinationRateTextField, currentDateTextField;
    private JSpinner hourSpinner, minuteSpinner;
    private JButton submitButton;
    private String actualBookingRate, actualAttendanceRate, predictedVaccinationRate;
    private LocalDate currentDate;
    private int currentHour, currentMinute;

    public SimulationPage(VaccineSystem vaccineSystem) {
        super(vaccineSystem);
        initializeValues();
        setupPage();
    }

    private void initializeValues() {

        data = vaccineSystem.getData();
        actualBookingRate = data.getActualBookingRate();
        actualAttendanceRate = data.getActualAttendanceRate();
        predictedVaccinationRate = data.getPredictedVaccinationRate();

        currentDate = data.getCurrentDate();

        String currentTime = String.valueOf(data.getCurrentTime());

        currentHour = Integer.parseInt(currentTime.split(":")[0]);
        currentMinute =  Integer.parseInt(currentTime.split(":")[1]);
    }

    private void setupPage() {
        inputPanel = new JPanel(new GridLayout(0, 2));
        inputPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        mainPanel.add(inputPanel);

        actualBookingRateTextField = new JTextField();
        actualAttendanceRateTextField = new JTextField();
        predictedVaccinationRateTextField = new JTextField();
        currentDateTextField = new JTextField();

        hourSpinner = Page.createJSpinner(0, 24, 2);
        minuteSpinner = Page.createJSpinner(0, 60, 2);

        actualBookingRateTextField.setText(actualBookingRate);
        actualAttendanceRateTextField.setText(actualAttendanceRate);
        predictedVaccinationRateTextField.setText(predictedVaccinationRate);
        currentDateTextField.setText(String.valueOf(currentDate));

        submitButton = new JButton("Submit");

        addLabelledComponent(inputPanel, "Actual Booking Rate: ", actualBookingRateTextField);
        addLabelledComponent(inputPanel, "Actual Attendance Rate: ", actualAttendanceRateTextField);
        addLabelledComponent(inputPanel, "Predicted Vaccination Rate: ", predictedVaccinationRateTextField);
        addLabelledComponent(inputPanel, "Current Date (YYYY-MM-DD): ", currentDateTextField);
        addLabelledComponent(inputPanel, "Current Time: ", createTimePanel(hourSpinner, minuteSpinner, currentHour, currentMinute));

        addButton(submitButton, mainPanel);

        setMaxWidthMinHeight(inputPanel);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        super.actionPerformed(e);
        if (e.getSource() == submitButton) {

            HashMap<String, HashMap<String, Object>> simulations = data.getSimulations();
            HashMap<String, Object> simulation;
            String firstKey;

            // If there are not already simulation values stored in the database, create a new record
            try {
                firstKey = simulations.keySet().iterator().next();
                simulation = simulations.get(firstKey);
            }
            catch (Exception ex) {
                firstKey = "newID";
                simulation = new HashMap<>();
            }

            simulation.put("Simulation.actualBookingRate", actualBookingRateTextField.getText());
            simulation.put("Simulation.actualAttendanceRate", actualAttendanceRateTextField.getText());
            simulation.put("Simulation.predictedVaccinationRate", predictedVaccinationRateTextField.getText());
            simulation.put("Simulation.change", "change");

            simulations.put(firstKey, simulation);
            data.setSimulations(simulations);

            data.setCurrentDate(getLocalDate(currentDateTextField.getText()));

            LocalTime currentTime = LocalTime.of(currentHour, currentMinute);
            data.setCurrentTime(currentTime);

            JOptionPane.showMessageDialog(null, "Changes made successfully", "Changes Made", JOptionPane.ERROR_MESSAGE);
        }
    }
}
