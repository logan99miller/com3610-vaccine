/**
 * Page used to insert a transporter into the system's database
 */
package UserInterface.AddPages;

import Core.VaccineSystem;
import UserInterface.AddUtils.Insert;
import UserInterface.LoggedInPage;
import javax.swing.*;
import java.awt.event.ActionEvent;
import java.util.ArrayList;

public class AddTransporterPage extends AddPage {

    JTextField nameTextField;

    public AddTransporterPage(VaccineSystem vaccineSystem, LoggedInPage loggedInPage) {
        super(vaccineSystem, loggedInPage, "Add Transporter:");
        nameTextField = new JTextField();
        addLabelledComponent(inputGridPanel, "*Name:", nameTextField);
        setMaxWidthMinHeight(inputPanel);
    }

    /**
     * Creates the SQL statements required and adds them to the inserts list
     */
    private void createStatements() {
        inserts = new ArrayList<>();

        String[] columnNames = {"name"};
        Object[] values = {nameTextField.getText()};
        inserts.add(new Insert(columnNames, values, "Transporter"));
    }

    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == submitButton) {
            createStatements();
        }
        super.actionPerformed(e);
    }
}
