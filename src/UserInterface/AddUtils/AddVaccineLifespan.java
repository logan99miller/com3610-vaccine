/**
 * Used for the add vaccine lifespan pop-up page to create panels containing input fields for the lifespan (in days) for a
 * user given temperature range, and by the add vaccine page to get the lifespans at different ranges.
 * As there can be 1 to many stores per storage location this helps reduce code reuse.
 */
package UserInterface.AddUtils;

import UserInterface.AddPages.AddPage;
import javax.swing.*;

public class AddVaccineLifespan {

    private JTextField lifespanTextField;
    private JSpinner minTempSpinner, maxTempSpinner;

    public AddVaccineLifespan() {
        lifespanTextField = new JTextField();
        minTempSpinner = AddPage.createJSpinner(-100, 100, 3);
        maxTempSpinner = AddPage.createJSpinner(-100, 100, 3);

        lifespanTextField.setColumns(7);
        minTempSpinner.setValue(0);
        maxTempSpinner.setValue(0);
    }

    public JPanel getPanel() {
        JPanel panel = new JPanel();

        panel.add(lifespanTextField);
        panel.add(new JLabel(" days at, Min Temperature:"));
        panel.add(minTempSpinner);
        panel.add(new JLabel("Max Temperature:"));
        panel.add(maxTempSpinner);
        panel.add(new JLabel("(degrees C)"));

        return panel;
    }

    public String getLifespan() {
        return lifespanTextField.getText();
    }

    public Object getMinimumTemperature() {
        return minTempSpinner.getValue();
    }

    public Object getMaximumTemperature() {
        return maxTempSpinner.getValue();
    }
}
