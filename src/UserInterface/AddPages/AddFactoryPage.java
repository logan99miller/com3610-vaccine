/**
 * Page used to insert a factory into the system's database
 */
package UserInterface.AddPages;

import Core.VaccineSystem;
import UserInterface.AddUtils.Insert;
import UserInterface.LoggedInPage;
import javax.swing.*;
import java.awt.event.ActionEvent;

public class AddFactoryPage extends AddStorageLocationPage {

    private JTextField vaccinesPerMinTextField;
    private JComboBox manufacturersComboBox;

    public AddFactoryPage(VaccineSystem vaccineSystem, LoggedInPage loggedInPage) {
        super(vaccineSystem, loggedInPage, "Add Factory:");

        vaccinesPerMinTextField = new JTextField();

        String[] columnNames = {"manufacturerID", "name"};
        manufacturersComboBox = new JComboBox(getFormattedSelect(columnNames, "Manufacturer").toArray());

        addLabelledComponent(inputGridPanel, "-*Vaccines per minute:", vaccinesPerMinTextField);
        addLabelledComponent(inputGridPanel, "Manufacturer:", manufacturersComboBox);

        setMaxWidthMinHeight(inputPanel);
    }

    /**
     * Creates the SQL statements required and adds them to the inserts list
     */
    protected void createStatements() {
        super.createStatements();

        String vaccinesPerMin = vaccinesPerMinTextField.getText();
        String manufacturer = (String) manufacturersComboBox.getSelectedItem();

        int manufacturerID = Integer.parseInt(manufacturer.split(":")[0]);

        String[] columnNames = new String[] {"storageLocationID", "manufacturerID", "vaccinesPerMin"};
        Object[] values = new Object[] {storageLocationID, manufacturerID, vaccinesPerMin};
        inserts.add(new Insert(columnNames, values, "Factory"));
    }

    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == submitButton) {
            createStatements();
        }
        super.actionPerformed(e);
    }
}
