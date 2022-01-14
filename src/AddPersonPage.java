import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;

public class AddPersonPage extends AddPage {

    JTextField forenameTextField, surnameTextField, DoBTextField;

    public AddPersonPage(VaccineSystem vaccineSystem, MainPage mainPage) {
        super(vaccineSystem, mainPage, "Add Person:");

        createInputFieldsGridPanel();
        fitPanelToMainPanel(inputFieldsPanel);
    }

    private void createInputFieldsGridPanel() {
        JPanel inputFieldsGridPanel = new JPanel(new GridLayout(0, 2));

        forenameTextField = new JTextField();
        surnameTextField = new JTextField();
        DoBTextField = new JTextField();

        inputFieldsGridPanel.add(new JLabel("Forename:"));
        inputFieldsGridPanel.add(forenameTextField);
        inputFieldsGridPanel.add(new JLabel("*Surname:"));
        inputFieldsGridPanel.add(surnameTextField);
        inputFieldsGridPanel.add(new JLabel("*DoB (YYYY-MM-DD):"));
        inputFieldsGridPanel.add(DoBTextField);

        inputFieldsPanel.add(inputFieldsGridPanel);
    }

    private void createStatements() {
        String forename = forenameTextField.getText();
        String surname = surnameTextField.getText();
        String DoB = DoBTextField.getText();

        values = "\"" + forename + "\", \"" + surname + "\", '" + DoB + "'";
        statements.add("INSERT INTO Person (forename, surname, DoB) VALUES (" + values + ");");
    }

    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == submitButton) {
            if (checkDate(DoBTextField.getText())) {
                createStatements();
                super.actionPerformed(e);
            }
            else {
                incorrectDateMessage();
            }
        }
        else {
            super.actionPerformed(e);
        }
    }
}
