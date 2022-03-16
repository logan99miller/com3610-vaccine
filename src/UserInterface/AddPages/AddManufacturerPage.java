package UserInterface.AddPages;

import UserInterface.AddPage;
import Core.VaccineSystem;
import UserInterface.AddUtils.Insert;
import UserInterface.LoggedInPage;
import javax.swing.*;
import java.awt.event.ActionEvent;
import java.util.ArrayList;
import static UserInterface.Utils.*;

public class AddManufacturerPage extends AddPage {

    private JTextField nameTextField;
    private JComboBox vaccineComboBox;

    public AddManufacturerPage(VaccineSystem vaccineSystem, LoggedInPage loggedInPage) {
        super(vaccineSystem, loggedInPage, "Add Manufacturer:");

        String[] vaccineColumnNames = new String[] {"vaccineID", "name"};

        nameTextField = new JTextField();
        vaccineComboBox = new JComboBox(getFormattedSelect(vaccineColumnNames, "Vaccine").toArray());

        addLabelledComponent(inputGridPanel, "*Name:", nameTextField);
        addLabelledComponent(inputGridPanel, "Vaccines produced: ", vaccineComboBox);

        setMaxWidthMinHeight(inputPanel);
    }

    private void createStatements() {
        inserts = new ArrayList<>();

        String vaccine = (String) vaccineComboBox.getSelectedItem();
        int vaccineID = Integer.parseInt(vaccine.split(":")[0]);

        String[] columnNames = new String[] {"name", "vaccineID"};
        Object[] values = new Object[] {nameTextField.getText(), vaccineID};
        inserts.add(new Insert(columnNames, values, "Manufacturer"));
    }

    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == submitButton) {
            createStatements();
        }
        super.actionPerformed(e);
    }
}
