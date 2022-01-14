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

        panel.add(new JLabel("Lifespan:"));
        panel.add(lifespanTextField);
        panel.add(new JLabel("Min Temperature:"));
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
