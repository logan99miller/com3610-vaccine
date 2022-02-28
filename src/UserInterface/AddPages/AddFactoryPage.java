package UserInterface.AddPages;

import Core.VaccineSystem;
import UserInterface.MainPage;
import javax.swing.*;
import java.awt.event.ActionEvent;

public class AddFactoryPage extends AddStorageLocationPage {

    private JTextField vaccinesPerMinTextField;
    private JComboBox manufacturersComboBox;

    public AddFactoryPage(VaccineSystem vaccineSystem, MainPage mainPage) {
        super(vaccineSystem, mainPage, "Add Factory:");

        vaccinesPerMinTextField = new JTextField();

        String[] columnNames = {"manufacturerID", "name"};
        manufacturersComboBox = new JComboBox(getFormattedSelect(columnNames, "Manufacturer").toArray());

        addLabelledComponent(inputGridPanel, "-*Vaccines per minute:", vaccinesPerMinTextField);
        addLabelledComponent(inputGridPanel, "Manufacturer:", manufacturersComboBox);

        setMaxWidthMinHeight(inputPanel);
    }

    protected void createStatements() {
        super.createStatements();

        String vaccinesPerMin = vaccinesPerMinTextField.getText();
        String manufacturer = (String) manufacturersComboBox.getSelectedItem();

        int manufacturerID = Integer.parseInt(manufacturer.split(":")[0]);

        values = storageLocationID + ", " + manufacturerID + ", " + vaccinesPerMin;
        statements.add("INSERT INTO Factory (storageLocationID, manufacturerID, vaccinesPerMin) VALUES (" + values + ");");
    }

    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == submitButton) {
            createStatements();
        }
        super.actionPerformed(e);
    }
}
