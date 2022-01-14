import javax.swing.*;

public class AddVaccineLifespanPanel {

    JPanel allSpinners;
    JSpinner lifespanSpinner, lowestTempSpinner, highestTempSpinner;
    int lifespan, lowestTemperature, highestTemperature;

    public AddVaccineLifespanPanel() {
        lifespanSpinner = AddPage.createJSpinner(0, 100000, 6);
        lowestTempSpinner = AddPage.createJSpinner(-100, 100, 3);
        highestTempSpinner = AddPage.createJSpinner(-100, 100, 3);

        lowestTempSpinner.setValue(0);
        highestTempSpinner.setValue(0);

        allSpinners = new JPanel();
        allSpinners.add(lifespanSpinner);
        allSpinners.add(new JLabel("between"));
        allSpinners.add(lowestTempSpinner);
        allSpinners.add(new JLabel("and"));
        allSpinners.add(highestTempSpinner);
        allSpinners.add(new JLabel("(degrees C)"));
    }

    public JPanel getAllSpinners() {
        return allSpinners;
    }
}

