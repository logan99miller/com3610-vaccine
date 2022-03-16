package UserInterface.AddUtils;

import UserInterface.AddPage;

import javax.swing.*;

public class AddStore {

    private JSpinner temperatureSpinner;
    private JTextField capacityTextField;

    public AddStore() {
        capacityTextField = new JTextField();
        temperatureSpinner = AddPage.createJSpinner(-100, 100, 3);

        capacityTextField.setColumns(7);
        temperatureSpinner.setValue(0);
    }

    public JPanel getPanel() {
        JPanel panel = new JPanel();

        panel.add(new JLabel("Capacity:"));
        panel.add(capacityTextField);
        panel.add(new JLabel("Temperature (degrees C):"));
        panel.add(temperatureSpinner);

        return panel;
    }

    public Object getTemperature() {
        return temperatureSpinner.getValue();
    }

    public String getCapacity() {
        return capacityTextField.getText();
    }
}
