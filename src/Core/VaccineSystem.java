package Core;

import UserInterface.LoginPage;
import UserInterface.MainPage;
import javax.swing.*;
import java.awt.*;
import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;

public class VaccineSystem extends JFrame {

    final private String URL = "jdbc:mysql://127.0.0.1:3306/vaccine_system";
    private String pageName, user, password;
    private CardLayout cardLayout;
    private JPanel cards;
    private int updateRate, simulationSpeed;
    private Data data;

    public static void main(String[] args) {
        new VaccineSystem("Vaccine System");
    }

    public VaccineSystem(String titleBarText) {
        super(titleBarText);

        user = "root";
        password = "artstowerhas20";

        data = new Data(this);

        configureWindow();
        createInterface();

        updateRate = 10000; // in milliseconds
        simulationSpeed = 10;

        AutomateSystem automateSystem = new AutomateSystem();
        automateSystem.start(data, updateRate, simulationSpeed);
        while (true) {
//            automateSystem.run();
            try {
                Thread.sleep(updateRate);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    private void configureWindow() {
        this.setSize(1000, 700);
        this.setLocationRelativeTo(null); // Sets window to centre of screen
        this.setVisible(true);
        this.setResizable(true);
        this.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
    }

    private void createInterface() {
        cardLayout = new CardLayout();
         cards = new JPanel(cardLayout);

        LoginPage loginPage = new LoginPage(this);
        JPanel loginPanel = loginPage.getPanel();
        cards.add(loginPanel, "login");

        MainPage mainPage = new MainPage(this);
        JPanel mainPanel = mainPage.getPanel();
        cards.add(mainPanel, "main");

        cardLayout.last(cards);

        this.add(cards);
    }

    public void delete(String IDFieldName, String ID, String tableName) throws SQLException {
        executeUpdate("DELETE FROM " + tableName + " WHERE " + IDFieldName + " = " + ID);
    }

    public void insert(String[] columnNames, Object[] values, String tableName) throws SQLException {
        String columnNamesText = getColumnNamesText(columnNames);
        String valuesText = getValuesText(values);
        executeUpdate("INSERT INTO " + tableName + " (" + columnNamesText + ") VALUES (" + valuesText + ");");
    }

    public void update(String[] columnNames, Object[] values, String tableName, String where) throws SQLException {
        String statementText = "UPDATE " + tableName + " SET " + getOnText(columnNames, values) + " WHERE " + where;
        executeUpdate(statementText);
    }

    private String getValuesText(Object[] values) {
        String valuesText = "";
        valuesText = addToValues(valuesText, values[0], "");
        for (int i = 1; i < values.length; i++) {
            valuesText = addToValues(valuesText, values[i], ", ");
        }
        return valuesText;
    }

    private String getOnText(String[] columnNames, Object[] values) {
        String setText = columnNames[0] + " = ";
        setText = addToValues(setText, values[0], "");
        for (int i = 1; i <  columnNames.length; i++) {
            setText += addToSetText(columnNames[i], values[i]);
        }
        return setText;
    }

    private String addToSetText(String columnName, Object value) {
        String text = "";
        return ", " + columnName + addToValues(text, value, " = ") + " ";
    }

    private String addToValues(String valueText, Object value, String separator) {
        try {
            Float.parseFloat(value.toString());
            valueText += separator + value;
        }
        catch (NullPointerException e) {
            valueText += separator + "null";
        }
        catch (NumberFormatException e) {
            valueText += separator + "'" + value + "'";
        }
        return valueText;
    }

    private String getColumnNamesText(String[] columnNames) {
        String columnNamesText = columnNames[0];
        for (int i = 1; i < columnNames.length; i++) {
            columnNamesText += ", " + columnNames[i];
        }
        return columnNamesText;
    }

    public void executeUpdate(String statementText) throws SQLException {
        Connection connection;
        Statement statement = null;
        System.out.println(statementText);
        try {
            connection = DriverManager.getConnection(URL, user, password);
            statement = connection.createStatement();
            statement.executeUpdate(statementText);

        } catch (SQLException e) {
            e.printStackTrace();
        }
        finally {
            if (statement != null) {
                statement.close();
            }
        }
    }

    public HashMap<String, HashMap<String, Object>> executeSelect(String[] columnNames, String tableName,
     HashMap<String, String>[] innerJoins, String where) throws SQLException {

        Connection connection = null;
        Statement statement;

        HashMap<String, HashMap<String, Object>> res = new HashMap<>();
        HashMap<String, Object> values = new HashMap<>();

        String statementText = "SELECT " + columnNames[0];
        for (int i = 1; i < columnNames.length; i++) {
            statementText += ", " + columnNames[i];
        }
        statementText += " FROM " + tableName;

        if (innerJoins != null) {
            for (HashMap<String, String> innerJoin : innerJoins) {
                String foreignKey = innerJoin.get("foreignKey");
                String innerJoinWhere = innerJoin.get("localTableName") + "." + foreignKey + " = " + innerJoin.get("foreignTableName") + "." + foreignKey;
                statementText += " INNER JOIN " + innerJoin.get("foreignTableName") + " ON " + innerJoinWhere;
            }
        }

        if (where != null) {
            statementText += " WHERE " + where;
        }
        try {
            connection = DriverManager.getConnection(URL, user, password);
            statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery(statementText);
            while(resultSet.next()) {
                for (String columnName : columnNames) {
                    values.put(columnName, resultSet.getString(columnName));
                }
                res.put((String) values.get(columnNames[0]), values);
                values = new HashMap<>();
            }
            resultSet.close();
            return res;
        }
        catch (SQLException e) {
            e.printStackTrace();
        }
        finally {
            if (connection != null) {
                connection.close();
            }
        }
        return null;
    }

    public HashMap<String, HashMap<String, Object>> executeSelect(String[] columnNames, String tableName) throws SQLException {
        return executeSelect(columnNames, tableName, null, null);
    }

    public HashMap<String, HashMap<String, Object>> executeSelect(String[] columnNames, String tableName, HashMap<String, String>[] innerJoins) throws SQLException {
        return executeSelect(columnNames, tableName, innerJoins, null);
    }

    public HashMap<String, HashMap<String, Object>> executeSelect(String[] columnNames, String tableName, String where) throws SQLException {
        return executeSelect(columnNames, tableName, null, where);
    }

    public ArrayList<HashMap<String, String>> executeSelect2(String[] columnNames, String tableName, String where) throws SQLException {
        Connection connection = null;
        Statement statement;

        ArrayList<HashMap<String, String>> res = new ArrayList<>();
        HashMap<String, String> values = new HashMap<>();

        StringBuilder statementText = new StringBuilder("SELECT ");
        for (String columnLabel : columnNames) {
            statementText.append(columnLabel).append(", ");
        }
        statementText = new StringBuilder(statementText.substring(0, statementText.length() - 2));
        statementText.append(" FROM ").append(tableName);

        if (where != null) {
            statementText.append(" WHERE ").append(where);
        }

        try {
            connection = DriverManager.getConnection(URL, user, password);
            statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery(statementText.toString());
            while(resultSet.next()) {
                for (String columnName : columnNames) {
                    values.put(columnName, resultSet.getString(columnName));
                }
                res.add(values);
                values = new HashMap<>();
            }
            resultSet.close();
            return res;
        }
        catch (SQLException e) {
            e.printStackTrace();
        }
        finally {
            if (connection != null) {
                connection.close();
            }
        }
        return null;
    }

    public ArrayList<HashMap<String, String>> executeSelect2(String[] columnNames, String tableName) throws SQLException {
        return executeSelect2(columnNames, tableName, null);
    }
    
    public ArrayList<ArrayList<String>> executeSelect1(String[] columnNames, String tableName, String where) throws SQLException {
        Connection connection = null;
        Statement statement;

        ArrayList<ArrayList<String>> res = new ArrayList<>();
        ArrayList<String> values = new ArrayList<>();

        StringBuilder statementText = new StringBuilder("SELECT ");
        for (String columnLabel : columnNames) {
            statementText.append(columnLabel).append(", ");
        }
        statementText = new StringBuilder(statementText.substring(0, statementText.length() - 2));
        statementText.append(" FROM ").append(tableName);

        if (where != null) {
            statementText.append(" WHERE ").append(where);
        }

        try {
            connection = DriverManager.getConnection(URL, user, password);
            statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery(statementText.toString());
            while(resultSet.next()) {
                for (String columnLabel : columnNames) {
                    values.add(resultSet.getString(columnLabel));
                }
                res.add(values);
                values = new ArrayList<>();
            }
            resultSet.close();
            return res;
        }
        catch (SQLException e) {
            e.printStackTrace();
        }
        finally {
            if (connection != null) {
                connection.close();
            }
        }
        return null;
    }

    public ArrayList<ArrayList<String>> executeSelect1(String[] columnNames, String tableName) throws SQLException {
        return executeSelect1(columnNames, tableName, null);
    }

    public void setUser(String user) {
        this.user = user;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setPageName(String pageName) {
        this.pageName = pageName;
    }

    public void updatePage() {
        cardLayout.show(cards, pageName);
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
}
