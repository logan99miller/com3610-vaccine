import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;

public class AddVaccinationCentrePage extends AddStorageLocationPage {

    JTextField nameTextField;

    public AddVaccinationCentrePage(VaccineSystem vaccineSystem, MainPage mainPage) {
        super(vaccineSystem, mainPage, "Add VaccinationCentre Centre:");
        createNamePanel();
        fitPanelToMainPanel(inputFieldsPanel);
    }

    private void createNamePanel() {
        JPanel namePanel = new JPanel(new GridLayout(0, 2));

        nameTextField = new JTextField();

        namePanel.add(new JLabel("*Name:"));
        namePanel.add(nameTextField);

        inputFieldsPanel.add(namePanel);
    }

    protected void createStatements() {
        String name = nameTextField.getText();
        values = storageLocationID + ", \"" + name + "\"";
        statements.add("INSERT INTO VaccinationCentre (storageLocationID, name) VALUES (" + values + ");");
    }

    public void actionPerformed(ActionEvent e) {
        if ((e.getSource() == submitButton) && (checkCoordinates()) && (fieldConditionsMet())) {
            super.createStatements();
            createStatements();
            super.actionPerformed(e);
        }
        else {
            super.actionPerformed(e);
        }
    }
}
