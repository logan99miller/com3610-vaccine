import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.sql.SQLException;
import java.util.ArrayList;

public class AddManufacturerPage extends AddPage {

    private JTextField nameTextField;
    private JList<String> vaccinesList;
    private Object[] vaccines;

    public AddManufacturerPage(VaccineSystem vaccineSystem, MainPage mainPage) {
        super(vaccineSystem, mainPage, "Add Manufacturer:");
        createNamePanel();
        createVaccinesPanel();
        fitPanelToMainPanel(inputFieldsPanel);
    }

    private void createNamePanel() {
        JPanel namePanel = new JPanel(new GridLayout(0, 2));

        nameTextField = new JTextField();

        namePanel.add(new JLabel("*Name:"));
        namePanel.add(nameTextField);

        inputFieldsPanel.add(namePanel);
    }

    private void createVaccinesPanel() {
        JPanel vaccinesPanel = new JPanel(new GridLayout(0, 2));

        String[] columnNames = {"vaccineID", "name"};
        DefaultListModel<String> listModel = new DefaultListModel<>();
        vaccines = getColumns(columnNames, "Vaccine");
        for (Object vaccine : vaccines) {
            listModel.addElement((String) vaccine);
        }
        vaccinesList = new JList<>(listModel);

        vaccinesPanel.add(new JLabel("Vaccines produced:"));
        vaccinesPanel.add(vaccinesList);

        inputFieldsPanel.add(vaccinesPanel);
    }

    private void createStatements() {
        try {
            values = "\"" + nameTextField.getText() + "\"";
            String statement = "INSERT INTO Manufacturer (name) VALUES (" + values + ");";
            vaccineSystem.executeUpdate(statement);
            String[] columnNames = new String[]{"MAX(manufacturerID)"};
            ArrayList<ArrayList<String>> resultSet = vaccineSystem.executeSelect(columnNames, "Manufacturer");
            int manufacturerID = Integer.parseInt(resultSet.get(0).get(0));

            for (String vaccine : vaccinesList.getSelectedValuesList()) {
                int vaccineID = Integer.parseInt(vaccine.split(":")[0]);
                values = manufacturerID + ", " + vaccineID;
                statements.add("INSERT INTO ManufacturerVaccine (manufacturerID, vaccineID) VALUES (" + values + ");");
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void actionPerformed(ActionEvent e) {
        createStatements();
        super.actionPerformed(e);
    }
}
