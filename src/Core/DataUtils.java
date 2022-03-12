package Core;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

public class DataUtils {

    public static ArrayList<Integer> sortIntegerKeyInMap(HashMap<String, HashMap<String, Object>> map, String integerKey) {
        ArrayList<Integer> keys = new ArrayList<>();
        ArrayList<Integer> values = new ArrayList<>();

        for (String key : map.keySet()) {
            keys.add(Integer.parseInt(key));
            values.add(Integer.parseInt((String) map.get(key).get(integerKey)));
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

    public static HashMap<String, Object> findMap(HashMap<String, HashMap<String, Object>> maps, String fieldName, String fieldValue) {
        for (String key : maps.keySet()) {
            HashMap<String, Object> map = maps.get(key);
            if ((map.get(fieldName)).equals(fieldValue)) {
                return map;
            }
        }
        return null;
    }

    // Method expects dates in the format YYYY:MM:DD, where : is the splitter value given
    public static LocalDate getDateFromString(String string, String splitter) {
        String[] subString = string.split(splitter);
        if (subString.length == 3) {
            int year = Integer.parseInt(subString[0]);
            int hour = Integer.parseInt(subString[1]);
            int minute = Integer.parseInt(subString[2]);
            LocalDate date = LocalDate.of(year, hour, minute);
            return date;
        }
        return null;
    }

    // Converts the time stored in this class and the database into LocalTime type
    public static LocalTime getLocalTime(String time) {
        String[] stringTimeValues = time.split(":");
        int[] timeValues = new int[stringTimeValues.length];
        for (int i = 0; i < timeValues.length; i++) {
            timeValues[i] = Integer.parseInt(stringTimeValues[i]);
        }
        return LocalTime.of(timeValues[0], timeValues[1], timeValues[2]);
    }

    public static HashMap<String, HashMap<String, Object>> mergeMaps(HashMap<String, HashMap<String, Object>> primaryMap,
                                                                     HashMap<String, HashMap<String, Object>> secondaryMap, String keyAddition) {
        for (String key : secondaryMap.keySet()) {
            primaryMap.put(key + keyAddition, secondaryMap.get(key));
        }
        return primaryMap;
    }

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
            catch (ArrayIndexOutOfBoundsException e1) {}
        }
        return "";
    }

    public static String getIDFieldName(Set<String> keys) {
        List<String> keysList = new ArrayList<>();
        keysList.addAll(keys);
        return getIDFieldName(keysList);
    }

    public static LocalDate getLocalDate(String databaseDate) {
        String[] dateValues = databaseDate.split("-");
        return LocalDate.of(
                Integer.parseInt(dateValues[0]),
                Integer.parseInt(dateValues[1]),
                Integer.parseInt(dateValues[2].substring(0, 2)));
    }
}
