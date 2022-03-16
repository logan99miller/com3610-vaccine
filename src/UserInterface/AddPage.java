package UserInterface;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import Core.VaccineSystem;
import UserInterface.AddUtils.Insert;
import static UserInterface.AddUtils.CheckInputs.*;
import static UserInterface.Utils.errorMessage;

public class AddPage extends Page {

    protected LoggedInPage loggedInPage;
    protected JButton backButton;
    protected JPanel inputPanel, inputGridPanel;
    protected JButton submitButton;
    protected ArrayList<Insert> inserts;
    protected String values;

    public AddPage(VaccineSystem vaccineSystem, LoggedInPage loggedInPage, String title) {
        super(vaccineSystem);
        this.loggedInPage = loggedInPage;

        inserts = new ArrayList<>();

        initializePage(title);

        // Excluded form initializePage() as popup add pages don't make use of it
        inputGridPanel = new JPanel(new GridLayout(0, 2));
        inputPanel.add(inputGridPanel);
    }

    public AddPage() {
        super();
    }

    protected void initializePage(String title) {

        backButton = new JButton("Back");
        addButton(backButton, mainPanel);

        mainPanel.add(new JLabel(title));

        inputPanel = new JPanel();
        inputPanel.setLayout(new BoxLayout(inputPanel, BoxLayout.Y_AXIS));
        inputPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        mainPanel.add(inputPanel);

        submitButton = new JButton("Submit");
        addButton(submitButton, mainPanel);

        mainPanel.add(new JLabel("Fields marked with a * are required"));
        mainPanel.add(new JLabel("Fields marked with a # require an integer input"));
        mainPanel.add(new JLabel("Fields marked with a - require a numeric input"));
    }

    protected void returnToSelectPage() {
        inserts = new ArrayList<>();
        loggedInPage.setPageName("add");
        loggedInPage.updatePage();
    }

    protected String insertAndGetID(String[] columnNames, Object[] values, String tableName, String IDFieldName) {

        if (checkInputConditions(false)) {
            try {
                vaccineSystem.insert(columnNames, values, tableName);
                final String maxID = "MAX(" + IDFieldName + ")";
                HashMap<String, HashMap<String, Object>> resultSet = vaccineSystem.select(new String[]{maxID}, tableName);
                String key = resultSet.keySet().iterator().next();

                return (String) resultSet.get(key).get(maxID);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return "";
    }

    protected ArrayList<String> getFormattedSelect(String[] columnNames, String tableName) {
        ArrayList<String> output = new ArrayList<>();

        try {
            HashMap<String, HashMap<String, Object>> resultSet = vaccineSystem.select(columnNames, tableName);

            for (String key : resultSet.keySet()) {
                HashMap<String, Object> record = resultSet.get(key);
                String addToOutput = (String) record.get(columnNames[0]);

                if (record.size() > 1) {
                    addToOutput += ":";
                }

                for (int i = 1; i < record.size(); i++) {
                    addToOutput += " " + record.get(columnNames[i]);
                }

                output.add(addToOutput);
            }
        } catch (SQLException ignored) {}

        return (output);
    }

    protected boolean checkInputConditions(boolean displayError) {
        Component previousComponent = new JPanel();

        for (Component component : inputGridPanel.getComponents()) {
            if (previousComponent instanceof JLabel) {

                String label = ((JLabel) previousComponent).getText().toLowerCase();

                if (component instanceof JTextField) {
                    String text = ((JTextField) component).getText();
                    if (!checkRequiredInput(label, text)) {
                        errorMessage("Fields marked with a * must be filled", displayError);
                        return false;
                    }
                    else if (!checkNumericInput(label, text)) {
                        errorMessage("Fields marked with a - must have a numeric value", displayError);
                        return false;
                    }
                    else if (!checkIntegerInput(label, text)) {
                        errorMessage("Fields marked with a # must have an integer value", displayError);
                        return false;
                    }
                    else if (!checkDateInput(label, text)) {
                        errorMessage("Dates must be input in the format YYYY-MM-DD", displayError);
                        return false;
                    }
                }
                else if (component instanceof JComboBox) {
                    JComboBox comboBox = (JComboBox) component;
                    if (comboBox.getSelectedItem() == null) {
                        errorMessage("Combo box is empty", displayError);
                        return false;
                    }
                }
            }
            previousComponent = component;
        }
        return true;
    }

    protected void performStatements() {

        try {
            for (Insert insert : inserts) {
                vaccineSystem.insert(insert.getColumnNames(), insert.getValues(), insert.getTableName());
            }
            returnToSelectPage();
        }
        catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == backButton) {
            returnToSelectPage();
        }
        else if (e.getSource() == submitButton) {
            if (checkInputConditions(true)) {
                performStatements();
            }
        }
    }
}
