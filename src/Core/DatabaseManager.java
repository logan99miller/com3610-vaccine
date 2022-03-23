/**
 * The only class which directly access the database. Database accessed through executeUpdate and executeSelect commands, which
 * are given the database URL, user and password details.
 */
package Core;

import java.sql.*;
import java.util.HashMap;

public class DatabaseManager {

    /**
     * Executes an update command on the database.
     *
     * @param statementText The SQL statement text to be executed
     * @param URL The database URL
     * @param user The database username
     * @param password The database password
     */
    public static void executeUpdate(String statementText, String URL, String user, String password) throws SQLException {
        Connection connection;
        Statement statement = null;

        System.out.println(statementText);

        try {
            connection = DriverManager.getConnection(URL, user, password);
            statement = connection.createStatement();
            statement.executeUpdate(statementText);

        }
        catch (SQLException e) {
            e.printStackTrace();
        }
        finally {
            if (statement != null) {
                statement.close();
            }
        }
    }

    /**
     * Execute a select command on the database and format the output into a hashmap in the format
     * HashMap<primaryKeyValue, HashMap<columName, databaseValue>> (representing HashMap<key, value>).
     *
     * @param statementText The SQL statement text to be executed
     * @param columnNames The column names in the database table to be read
     * @param URL The database URL
     * @param user The database username
     * @param password The database password
     * @return The data gathered from the SQL select command
     */
    public static HashMap<String, HashMap<String, Object>> executeSelect(String statementText, String[] columnNames, String URL, String user, String password) throws SQLException {

        Connection connection = null;
        Statement statement;

        HashMap<String, HashMap<String, Object>> res = null;

        try {
            connection = DriverManager.getConnection(URL, user, password);
            statement = connection.createStatement();

            ResultSet resultSet = statement.executeQuery(statementText);
            res = createSelectResults(resultSet, columnNames);
        }
        catch (SQLException e) {}
        finally {
            if (connection != null) {
                connection.close();
            }
        }
        return res;
    }

    /**
     * Reformats the ResultSet returned when querying the database into a more useful hashmap of hashmaps in the format
     * HashMap<primaryKeyValue, HashMap<columName, databaseValue>> (representing HashMap<key, value>).
     * @param resultSet The set returned when querying the database
     * @param columnNames The column names read from the table
     * @return A hashmap of hashmaps representing data in the database used throughout the system
     */
    private static HashMap<String, HashMap<String, Object>> createSelectResults(ResultSet resultSet, String[] columnNames) throws SQLException {
        HashMap<String, HashMap<String, Object>> res = new HashMap<>();

        while(resultSet.next()) {
            HashMap<String, Object> values = new HashMap<>();

            for (String columnName : columnNames) {
                values.put(columnName, resultSet.getString(columnName));
            }

            res.put((String) values.get(columnNames[0]), values);
        }

        resultSet.close();
        return res;
    }
}
