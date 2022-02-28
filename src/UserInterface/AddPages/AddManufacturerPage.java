package UserInterface.AddPages;

import UserInterface.AddPage;
import Core.VaccineSystem;
import UserInterface.MainPage;
import javax.swing.*;
import java.awt.event.ActionEvent;
import java.util.ArrayList;

public class AddManufacturerPage extends AddPage {

    private JTextField nameTextField;
    private JComboBox vaccineComboBox;

    public AddManufacturerPage(VaccineSystem vaccineSystem, MainPage mainPage) {
        super(vaccineSystem, mainPage, "Add Manufacturer:");

        String[] vaccineColumnNames = new String[] {"vaccineID", "name"};

        nameTextField = new JTextField();
        vaccineComboBox = new JComboBox(getFormattedSelect(vaccineColumnNames, "Vaccine").toArray());

        addLabelledComponent(inputGridPanel, "*Name:", nameTextField);
        addLabelledComponent(inputGridPanel, "Vaccines produced: ", vaccineComboBox);

        setMaxWidthMinHeight(inputPanel);
    }

    private void createStatements() {
        statements = new ArrayList<>();

        String vaccine = (String) vaccineComboBox.getSelectedItem();
        int vaccineID = Integer.parseInt(vaccine.split(":")[0]);

        String values = "\"" + nameTextField.getText() + "\", " + vaccineID;

        statements.add("INSERT INTO Manufacturer (name, vaccineID) VALUES (" + values + ");");
    }

    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == submitButton) {
            createStatements();
        }
        super.actionPerformed(e);
    }
}
