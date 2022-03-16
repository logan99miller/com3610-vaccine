/**
 * Used by other parts of the system to manipulate the data hashmaps. The data hashmaps are in the format
 * HashMap<primaryKeyValue, HashMap<columName, databaseValue>> (representing HashMap<key, value>) and associated with the
 * Data class.
 */
package Data;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

public class Utils {

    /**
     * Sorts the given maps from low to high
     *
     * @param maps The maps to sort
     * @param integerKey The key for the value that will be give the date
     * @return sorted list of keys for the given map
     */
    public static ArrayList<Integer> sortIntegerKeyInMap(HashMap<String, HashMap<String, Object>> maps, String integerKey) {
        ArrayList<Integer> keys = new ArrayList<>();
        ArrayList<Integer> values = new ArrayList<>();

        for (String key : maps.keySet()) {
            keys.add(Integer.parseInt(key));
            values.add(Integer.parseInt((String) maps.get(key).get(integerKey)));
        }

        // Replace with better sorting algorithm
        for (int i = 0; i < values.size() - 1; i++) {
            for (int j = 0; j < values.size() - i - 1; j++) {
                if (values.get(j) > values.get(j + 1)) {
                    int tempID = keys.get(j);
                    int tempValue = values.get(j);
                    keys.set(j, keys.get(j + 1));
                    keys.set(j + 1, tempID);
                    values.set(j, values.get(j + 1));
                    values.set(j + 1, tempValue);
                }
            }
        }
        return keys;
    }

    /**
     * Sorts the map by date, from the oldest date to the newest date.
     *
     * @param maps The maps to sort
     * @param dateKey The key for the value that will be give the date
     * @return A sorted list of keys for the given map
     */
    public static ArrayList<Integer> sortDateKeyInMap(HashMap<String, HashMap<String, Object>> maps, String dateKey) {

        // Convert dates to integers
        for (String iterationKey : maps.keySet()) {
            HashMap<String, Object> map = maps.get(iterationKey);
            String date = (String) map.get(dateKey);

            map.put(dateKey, date.replace("-", ""));
            maps.put(iterationKey, map);
        }

        return sortIntegerKeyInMap(maps, dateKey);
    }

    /**
     * Finds the map from the given maps which has the wantedValue at the wantedKey
     *
     * @param maps The maps to search through
     * @param wantedKey The key for the value that will be compared against the wanted value
     * @param wantedValue the value we are searching for
     * @return map with the wantedValue at the wantedKey
     */
    public static HashMap<String, Object> findMap(HashMap<String, HashMap<String, Object>> maps, String wantedKey, String wantedValue) {
        for (String key : maps.keySet()) {
            HashMap<String, Object> map = maps.get(key);
            if ((map.get(wantedKey)).equals(wantedValue)) {
                return map;
            }
        }
        return null;
    }

    /**
     * Merges two maps by adding the secondaryMap to the primaryMap. To avoid issues where 2 map values have the same key,
     * the secondaryMap has an additional value appended to its key.
     *
     * @param primaryMap the first map, it's value's keys are not changed
     * @param secondaryMap the second map, it's value's keys are changed
     * @param keyAddition the value to add to the second map's keys
     * @return The merged maps
     */
    public static HashMap<String, HashMap<String, Object>> mergeMaps(
        HashMap<String, HashMap<String, Object>> primaryMap,
        HashMap<String, HashMap<String, Object>> secondaryMap,
        String keyAddition
    ) {
        for (String key : secondaryMap.keySet()) {
            primaryMap.put(key + keyAddition, secondaryMap.get(key));
        }
        return primaryMap;
    }

    /**
     * Finds the ID field name from a list of keys. Keys are in format "tableName.fieldName" and the ID field name is in
     * the format "tableNameID", so by iterating through all keys and finding one where the table name equals the field
     * name - "ID", we find our ID field name.
     * Keys are the keys for a hashmap found in Data.Data.
     *
     * @param keys list of map keys
     * @return the IDFieldName, e.g. vaccineID
     */
    public static String getIDFieldName(List<String> keys) {
        for (String key : keys) {
            String[] splitKey = key.split("\\.");
            try {
                String tableName = splitKey[0];
                String fieldName = splitKey[1];
                if (tableName.equalsIgnoreCase(fieldName.substring(0, fieldName.length() - 2))) {
                    return fieldName;
                }
            }
            catch (ArrayIndexOutOfBoundsException ignored) {}
        }
        return "";
    }

    public static String getIDFieldName(Set<String> keys) {
        List<String> keysList = new ArrayList<>(keys);
        return getIDFieldName(keysList);
    }

    /**
     * Given the date as a string and returns a date with the LocalDate data type
     *
     * @param date in the format YYYY-MM-DD
     * @return date in LocalDate format
     */
    public static LocalDate getLocalDate(String date, String splitter) {
        String[] dateValues = date.split(splitter);
        return LocalDate.of(
                Integer.parseInt(dateValues[0]),
                Integer.parseInt(dateValues[1]),
                Integer.parseInt(dateValues[2].substring(0, 2)));
    }

    public static LocalDate getLocalDate(String date) {
        return getLocalDate(date, "-");
    }

    /**
     * Given the time as a string and returns the time in the LocalTime data type
     *
     * @param time in the format SS:MM:HH
     * @return time in LocalTime format
     */
    public static LocalTime getLocalTime(String time) {
        String[] stringTimeValues = time.split(":");
        int[] timeValues = new int[stringTimeValues.length];
        for (int i = 0; i < timeValues.length; i++) {
            timeValues[i] = Integer.parseInt(stringTimeValues[i]);
        }
        return LocalTime.of(timeValues[0], timeValues[1], timeValues[2]);
    }
}
