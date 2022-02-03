import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.ArrayList;

public class AddManufacturerPage extends AddPage {

    private JTextField nameTextField;
    private JList<String> vaccinesList;

    public AddManufacturerPage(VaccineSystem vaccineSystem, MainPage mainPage) {
        super(vaccineSystem, mainPage, "Add Manufacturer:");

        JPanel listPanel = new JPanel(new GridLayout(0, 2));
        inputPanel.add(listPanel);

        ListModel vaccines = ArrayListToListModel(getFormattedSelect(new String[] {"vaccineID", "name"}, "Vaccine"));

        nameTextField = new JTextField();
        vaccinesList = new JList(vaccines);

        addLabelledComponent(inputGridPanel, "*Name:", nameTextField);
        addLabelledComponent(listPanel, "Vaccines produced: ", vaccinesList);

        setMaxWidthMinHeight(inputPanel);
    }

    private void createStatements() {
        statements = new ArrayList<>();

        String values = "\"" + nameTextField.getText() + "\"";
        String statement = "INSERT INTO Manufacturer (name) VALUES (" + values + ");";
        int manufacturerID = insertAndGetID(statement, "manufacturerID", "Manufacturer");

        for (String vaccine : vaccinesList.getSelectedValuesList()) {
            int vaccineID = Integer.parseInt(vaccine.split(":")[0]);
            values = manufacturerID + ", " + vaccineID;
            statements.add("INSERT INTO ManufacturerVaccine (manufacturerID, vaccineID) VALUES (" + values + ");");
        }
    }

    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == submitButton) {
            createStatements();
        }
        super.actionPerformed(e);
    }
}
