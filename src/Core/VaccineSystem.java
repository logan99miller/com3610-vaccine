/**
 * The main class for this system. Acts as the main JFrame and a way for other classes to access each other, particularly
 * for classes to access the database through DatabaseManager (a static class) and data through Data.
 * Stores the URL and credentials for the database (given by the user).
 * Includes methods to generate delete, insert, update and select SQL statements that are then passed to the DatabaseManager
 */
package Core;

import Data.Data;
import UserInterface.LoginPage;
import UserInterface.LoggedInPage;

import javax.swing.*;
import java.awt.*;
import java.sql.*;
import java.util.HashMap;

public class VaccineSystem extends JFrame {

    final private String URL = "jdbc:mysql://127.0.0.1:3306/vaccine_system";
    private String pageName, user, password;
    private int updateRate, simulationSpeed;
    private Data data;
    private AutomateSystem automateSystem;
    private ActivityLog activityLog;
    private LoggedInPage loggedInPage;
    private CardLayout cardLayout;
    private JPanel cards, loginPanel;

    /**
     * Starts the system
     */
    public static void main(String[] args) {
        new VaccineSystem("Vaccine System", true);
    }

    /**
     * Initializes the program by creating core classes, configuring user interface and beginning the automation thread
     * @param runAutomation used in unit testing
     */
    public VaccineSystem(String titleBarText, boolean runAutomation) {
        super(titleBarText);

        user = "";
        password = "";

        configureWindow();

        LoginPage loginPage = new LoginPage(this);
        loginPanel = loginPage.getPanel();
        this.add(loginPanel);

        // How often the system updates in milliseconds
        updateRate = 250;

        // How much current time is multiplied by to increase speed
        // E.g. simulationSpeed = 2 means every 1 minute in real life is 2 minutes in the system
        simulationSpeed = 1;

    }

    /**
     * Begins a thread which will run at a given update rate to automate vaccine supply, distribution and inoculation of vaccines
     * to a population and automatically refresh the map and activity log pages
     */
    private void runAutomation() {
        Thread automationThread = new Thread() {
            public void run() {
                while (true) {
                    automateSystem.run();
                    loggedInPage.autoRefresh();
                    try {
                        Thread.sleep(updateRate);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        };

        automateSystem = new AutomateSystem();
        automateSystem.start(activityLog, this);
        automationThread.start();
    }

    private void configureWindow() {
        this.setSize(1200, 700);
        this.setLocationRelativeTo(null); // Sets window to centre of screen
        this.setVisible(true);
        this.setResizable(true);
        this.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
    }

    private void createInterface() {
        this.remove(loginPanel);

        cardLayout = new CardLayout();
        cards = new JPanel(cardLayout);

        LoginPage loginPage = new LoginPage(this);
        JPanel loginPanel = loginPage.getPanel();
        cards.add(loginPanel, "login");

        loggedInPage = new LoggedInPage(this);
        JPanel mainPanel = loggedInPage.getPanel();
        cards.add(mainPanel, "main");

        cardLayout.last(cards);

        this.add(cards);
    }

    public void delete(String IDFieldName, String ID, String tableName) throws SQLException {
        String statementText = "DELETE FROM " + tableName + " WHERE " + IDFieldName + " = " + ID;
        DatabaseManager.executeUpdate(statementText, URL, user, password);
    }

    public void insert(String[] columnNames, Object[] values, String tableName) throws SQLException {
        String columnNamesText = getColumnNamesText(columnNames);
        String valuesText = getValuesText(values);
        String statementText = "INSERT INTO " + tableName + " (" + columnNamesText + ") VALUES (" + valuesText + ");";
        DatabaseManager.executeUpdate(statementText, URL, user, password);
    }

    public void update(String[] columnNames, Object[] values, String tableName, String where) throws SQLException {
        String statementText = "UPDATE " + tableName + " SET " + getOnText(columnNames, values) + " WHERE " + where;
        DatabaseManager.executeUpdate(statementText, URL, user, password);
    }

    /**
     * Generates the SQL select statement text based on the given parameters and passes it to the DatabaseManager's executeSelect
     * method along with the database URl and credentials
     * @param columnNames The column names in the database table to be read
     * @param tableName The table to be read
     * @param innerJoins
     *      Data required to perform SQL inner joins to gather the correct data from the database.
     *      Contains the foreignKey, foreignTableName and localTableName used in
     *      "INNER JOIN foreignTableName ON localTableName.foreignKey = foreignTableName.foreignKey".
     * @param where the where part of the select statement, e.g. (vaccineID = 1)
     * @return The data gathered from the SQL select command
     */
    public HashMap<String, HashMap<String, Object>> select(String[] columnNames, String tableName, HashMap<String, String>[] innerJoins, String where) throws SQLException {
        String statementText = createSelectStatementText(columnNames, tableName, innerJoins, where);
        return DatabaseManager.executeSelect(statementText, columnNames, URL, user, password);
    }

    // Different version of select which take different combinations of parameters

    public HashMap<String, HashMap<String, Object>> select(String[] columnNames, String tableName) throws SQLException {
        return select(columnNames, tableName, null, null);
    }

    public HashMap<String, HashMap<String, Object>> select(String[] columnNames, String tableName, HashMap<String, String>[] innerJoins) throws SQLException {
        return select(columnNames, tableName, innerJoins, null);
    }

    public HashMap<String, HashMap<String, Object>> select(String[] columnNames, String tableName, String where) throws SQLException {
        return select(columnNames, tableName, null, where);
    }

    /**
     * Converts an array of column names into a string containing the list of column names in the format required for an SQL statement
     * @param columnNames column names used in an SQL statement
     * @return the columns part of an SQL statement
     */
    public static String getColumnNamesText(String[] columnNames) {
        String columnNamesText = columnNames[0];

        for (int i = 1; i < columnNames.length; i++) {
            columnNamesText += ", " + columnNames[i];
        }
        return columnNamesText;
    }

    /**
     * Converts an array of values into a string containing the list of values in the format required for an SQL statement
     * @param values values used in an SQL statement
     * @return the values part of an SQL statement
     */
    public static String getValuesText(Object[] values) {
        String valuesText = "";
        valuesText += getValuesText(values[0], "");

        for (int i = 1; i < values.length; i++) {
            valuesText += getValuesText(values[i], ", ");
        }
        return valuesText;
    }

    /**
     * Converts an array of column names and their associated values into a string containing the list of value changes in
     * the format required for an SQL statement, e.g. "vaccineID = 1, dosesNeeded = 3, daysBetweenDoses = 400"
     * @param columnNames column names used in an SQL statement
     * @param values values used in an SQL statement
     * @return the set text which forms part of an SQL statement
     */
    public static String getOnText(String[] columnNames, Object[] values) {
        String setText = columnNames[0] + " = ";
        setText += getValuesText(values[0], "");
        for (int i = 1; i <  columnNames.length; i++) {
            setText += ", " + columnNames[i] + getValuesText(values[i], " = ") + " ";
        }
        return setText;
    }

    /**
     * Converts the given value into a string in the format needed to include it in an SQL statement
     * @param value the value to be included in an SQL statement
     * @param separator the string to be added before the value to separate it from other values
     * @return the given value as a string in the format needed to include it in an SQL statement
     */
    private static String getValuesText(Object value, String separator) {
        String valuesText = "";

        // If value is a number
        try {
            Float.parseFloat(value.toString());
            valuesText = separator + value;
        }

        // If value is null
        catch (NullPointerException e) {
            valuesText = separator + "null";
        }

        // if value is a string or date
        catch (NumberFormatException e) {
            valuesText = separator + "'" + value + "'";
        }
        return valuesText;
    }

    /**
     * Creates the statement text required to execute an SQL select command on the database
     *
     * @param columnNames The column names in the database table to be read
     * @param tableName The table to be read
     * @param innerJoins
     *      Data required to perform SQL inner joins to gather the correct data from the database.
     *      Contains the foreignKey, foreignTableName and localTableName used in
     *      "INNER JOIN foreignTableName ON localTableName.foreignKey = foreignTableName.foreignKey".
     * @param where the where part of the select statement, e.g. (vaccineID = 1)
     * @return
     */
    private static String createSelectStatementText(String[] columnNames, String tableName, HashMap<String, String>[] innerJoins, String where) {
        String statementText = "SELECT " + columnNames[0];

        // Add the columns wanted from the table
        for (int i = 1; i < columnNames.length; i++) {
            statementText += ", " + columnNames[i];
        }

        statementText += " FROM " + tableName;

        // Add inner joins to get data from other tables
        if (innerJoins != null) {

            for (HashMap<String, String> innerJoin : innerJoins) {
                String foreignKey = innerJoin.get("foreignKey");
                String innerJoinWhere = innerJoin.get("localTableName") + "." + foreignKey + " = " + innerJoin.get("foreignTableName") + "." + foreignKey;

                statementText += " INNER JOIN " + innerJoin.get("foreignTableName") + " ON " + innerJoinWhere;
            }
        }

        // Add where statement
        if (where != null) {
            statementText += " WHERE " + where;
        }

        return statementText;
    }

    public void login(String user, String password) {

        this.user = user;
        this.password = password;
        this.pageName = "main";

        data = new Data(this);
        activityLog = new ActivityLog();

        createInterface();
        runAutomation();
    }

    // Get and set methods

    public void setUser(String user) {
        this.user = user;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getURL() {
        return URL;
    }

    public int getUpdateRate() {
        return updateRate;
    }

    public int getSimulationSpeed() {
        return simulationSpeed;
    }

    public Data getData() {
        return data;
    }

    public ActivityLog getActivityLog() {
        return activityLog;
    }

    public void updatePage() {
        cardLayout.show(cards, pageName);
    }

    public void setPageName(String pageName) {
        this.pageName = pageName;
    }
}