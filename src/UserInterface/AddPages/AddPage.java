/**
 * The parent class of all add pages, which are used to add data directly to the database.
 */
package UserInterface.AddPages;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import Core.VaccineSystem;
import UserInterface.AddUtils.Insert;
import UserInterface.LoggedInPage;
import UserInterface.Page;

import static UserInterface.AddUtils.CheckInputs.*;

public class AddPage extends Page {

    protected LoggedInPage loggedInPage;
    protected JButton backButton;
    protected JPanel inputPanel, inputGridPanel;
    protected JButton submitButton;
    protected String values;

    // A list of all insert statements to execute
    protected ArrayList<Insert> inserts;

    /**
     * The class constructor
     * @param vaccineSystem used to read and write to the database
     * @param loggedInPage used to go back to the "select add page" when the user presses the back button or successfully
     *                     adds new data
     * @param title the pages title (e.g. "Add Factories")
     */
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

    /**
     * Used when the user presses the back button or successfully submits their new data
     */
    protected void returnToSelectPage() {

        // Prevents the next add page having lots of insert statements
        inserts = new ArrayList<>();

        loggedInPage.setPageName("add");
        loggedInPage.updatePage();
    }

    /**
     * Executes an insert statement on the database using the given columnNames, values and tableName and then executes
     * a select statement to get the inserted record's ID. Used when we are creating a table that is then linked to other tables,
     * as the records in the other tables cannot be created without the initial table's ID.
     * For example when creating a record in the location table we need its ID to create records in the opening times table.
     * @param columnNames the list of column names to insert values to in the database
     * @param values the list of values to insert into the database, linked to the columnNames by their index (e.g. columnNames[0]
     *               link to values[0]
     * @param tableName the table to insert the values to
     * @param IDFieldName the ID / primary key field name for the table being inserted to. Used to find the ID after insertion
     * @return the ID / primary key of the record that was inserted
     */
    protected String insertAndGetID(String[] columnNames, Object[] values, String tableName, String IDFieldName) {

        if (checkInputConditions(false)) {
            try {
                vaccineSystem.insert(columnNames, values, tableName);

                final String maxID = "MAX(" + IDFieldName + ")";
                HashMap<String, HashMap<String, Object>> resultSet = vaccineSystem.select(new String[]{maxID}, tableName);
                String key = resultSet.keySet().iterator().next();

                return (String) resultSet.get(key).get(maxID);
            }
            catch (SQLException e) {
                e.printStackTrace();
            }
        }

        // Return
        return "";
    }

    /**
     * Performs a select statement using the given column names and table name and then formats the output as
     * "{ID}: {other values acquired}" (e.g. "1: Astra-Zeneca"). Used when creating drop-down selection menus, e.g. selecting
     * which vaccine a manufacturer should produce
     * @param columnNames the list of column names to insert values to in the database
     * @param tableName the table to insert the values to
     * @return a list of records found by the select statement, formatted to be suitable for a drop-down selection menu
     */
    protected ArrayList<String> getFormattedSelect(String[] columnNames, String tableName) {
        ArrayList<String> output = new ArrayList<>();

        try {
            HashMap<String, HashMap<String, Object>> resultSet = vaccineSystem.select(columnNames, tableName);

            for (String key : resultSet.keySet()) {

                HashMap<String, Object> record = resultSet.get(key);

                String addToOutput = (String) record.get(columnNames[0]);

                // If the record contains more than just 1 element, separate the first element by a colon
                if (record.size() > 1) {
                    addToOutput += ":";
                }

                // Separate the remaining elements by a space
                for (int i = 1; i < record.size(); i++) {
                    addToOutput += " " + record.get(columnNames[i]);
                }

                output.add(addToOutput);
            }
        } catch (SQLException ignored) {}

        return (output);
    }

    /**
     * Converts the given ArrayList to a ListModel object. Used when adding a list to a JList element
     * @param arrayList the list to be converted
     * @return the converted list
     */
    public static ListModel ArrayListToListModel(ArrayList<String> arrayList) {
        DefaultListModel<String> listModel = new DefaultListModel<>();

        for (Object listItem : arrayList) {
            listModel.addElement((String) listItem);
        }
        return (listModel);
    }

    /**
     * Checks the user's input against several input criteria (specified by the input's label (the text before the input area).
     * @param displayError if an error message should be displayed to the user, setting it to false can prevent multiple
     *                     error messages being displayed if the input conditions are checked several times (e.g. if they
     *                     have to be checked while the user still has more data to input)
     * @return true if the input conditions meet all the criteria, false otherwise.
     */
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

    /**
     * Iterates through the list of insert objects, and performs an insert SQL statement for each one.
     * Returns to the select add page after all statements have been performed.
     */
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
