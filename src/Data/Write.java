/** Static class called upon by the Data class which gives data as a hashmap of hashmaps in the format
 * HashMap<primaryKeyValue, HashMap<columName, databaseValue>> (representing HashMap<key, value>) to be written to the database.
 */
package Data;

import Core.VaccineSystem;

import java.sql.SQLException;
import java.util.HashMap;

import static Data.Utils.getIDFieldName;

public class Write {

    /**
     *  Iterates through the hashmap of hashmaps given by the data class and if a change has been flagged will write the changed
     *  data to the database
     *
     * @param maps hash maps in the format HashMap<primaryKeyValue, HashMap<columName, databaseValue>> (representing HashMap<key, value>)
     */
    public static void writeMaps(VaccineSystem vaccineSystem, HashMap<String, HashMap<String, Object>> maps) throws SQLException {
        for (String key : maps.keySet()) {
            writeMap(vaccineSystem, maps.get(key));
        }
    }

    /**
     *  Recursive function which writes the given map to the database. Recursion occurs when getValuesToWrite is called,
     *  which will call writeMaps if while getting the values to write finds a map inside the given map.
     *  This occurs if the given map contains data from multiple tables, for example the vaccine map also contains a map
     *  of vaccine exemptions which are stored in a separate table.
     *  The base case is reached when there are no more "sub" maps found.
     *
     * @param map The map to be written, in the format HashMap<columName, databaseValue>
     */
    public static void writeMap(VaccineSystem vaccineSystem, HashMap<String, Object> map) throws SQLException {

        // A hashmap of hashmaps in the format HashMap<tableName, HashMap<fieldName, databaseValue>> (representing HashMap<key, value>)
        HashMap<String, HashMap<String, String>> valuesToWrite = getValuesToWrite(vaccineSystem, map);

        for (String key : valuesToWrite.keySet()) {

            HashMap<String, String> valuesMap = valuesToWrite.get(key);

            // Only write if a change has been flagged as mySQL limits the number of read/writes in a given time
            if (valuesMap.get("change") != null) {
                writeValues(vaccineSystem, valuesMap, key);
            }

            if (valuesMap.get("delete") != null) {
                String IDFieldName = getIDFieldName(map.keySet());
                String ID = valuesMap.get(IDFieldName);
                vaccineSystem.delete(IDFieldName, ID, key);
            }
        }
    }

    /**
     * Given a hashmap of <string, object> hashmaps used to access data.
     * Returns a hashmap of <string, string> hashmaps to be written to the database.
     *
     * Achieves this by calling writeMap when the object is a hashmap (as it is a separate map
     * representing a separate table in the database) and adding the object to valuesToWrite when the object is a string
     * (as it is a value in the table being written to)
     *
     * @param map A hashmap of <string, object> hashmaps used to access data
     * @return A hashmap of <string, string> hashmaps to be written to the database
     */
    private static HashMap<String, HashMap<String, String>> getValuesToWrite(VaccineSystem vaccineSystem, HashMap<String, Object> map) throws SQLException {
        HashMap<String, HashMap<String, String>> valuesToWrite = new HashMap<>();

        for (String key : map.keySet()) {

            // Try treating the object as a string (i.e. value in the table)
            try {
                String value = (String) map.get(key);
                String[] splitKey = key.split("\\.");
                String secondaryTableName = splitKey[0];
                String fieldName = splitKey[1];

                if (valuesToWrite.get(secondaryTableName) == null) {
                    valuesToWrite.put(secondaryTableName, new HashMap<>());
                }

                valuesToWrite.get(secondaryTableName).put(fieldName, value);

            }
            // Otherwise, object is a hashmap which is storing data of another (but linked) table which should be written
            // to separately
            catch (ClassCastException e) {
                HashMap<String, Object> value = (HashMap<String, Object>) map.get(key);
                writeMap(vaccineSystem, value);
            }
        }

        return valuesToWrite;
    }

    /**
     * Writes the given values to the table with the given table name by either inserting a new record or updating an existing one
     *
     * @param valuesMap the column names and values ot be written to the table
     * @param tableName the table being written to
     */
    private static void writeValues(VaccineSystem vaccineSystem, HashMap<String, String> valuesMap, String tableName) throws SQLException {

        // Remove the change flag before writing to the database
        valuesMap.remove("change");

        String[] columnNames = valuesMap.keySet().toArray(new String[0]);
        String[] values = valuesMap.values().toArray(new String[0]);

        String where = getWhereClause(columnNames, values, tableName);

        if (where.equals("")) {
            vaccineSystem.insert(columnNames, values, tableName);
        }
        else {
            vaccineSystem.update(columnNames, values, tableName, where);
        }
    }

    /**
     * Creates the where clause required in the SQL statement to write the given valuesMap to the database.
     *
     * @param columnNames column names being written to
     * @param values values being written
     * @param tableName the table being written to
     * @return a where clause, blank if no there should be no where clause
     */
    private static String getWhereClause(String[] columnNames, String[] values, String tableName) {
        String where = "";

        tableName = tableName.toLowerCase();

        // Iterates through each columnName, removes the last 2 characters from the columnName and compares it against the
        // tableName, if they are the same then the current column is the primary key, which the where clause should use
        // as it is a unique identifier.
        // e.g. the primary key field name for vaccine is vaccineID
        for (int i = 0; i < columnNames.length; i++) {

            String potentialTableName = columnNames[i].substring(0, columnNames[i].length() - 2);
            potentialTableName = potentialTableName.toLowerCase();

            if (potentialTableName.equals(tableName)) {
                where = columnNames[i] + " = " + values[i];
            }
        }
        return where;
    }
}
