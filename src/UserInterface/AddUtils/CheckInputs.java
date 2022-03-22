/**
 * Static class called upon to check inputs of add pages and the simulation page
 */
package UserInterface.AddUtils;

import javax.swing.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;

public class CheckInputs {

    /**
     *  a * in the 1st or 2nd character of the input's label indicates the input is required
     * @param label the input's label
     * @param text the text entered by the user
     * @return true if the input passes the check, false otherwise
     */
    public static boolean checkRequiredInput(String label, String text) {
        if (label.startsWith("*") || (label.startsWith("-*")) || (label.startsWith("#*"))) {
            return !text.equals("");
        }
        return true;
    }

    /**
     *  * - at the start of the input's label indicates a numeric input is required
     * @param label the input's label
     * @param text the text entered by the user
     * @return true if the input passes the check, false otherwise
     */
    public static boolean checkNumericInput(String label, String text) {
        if (label.startsWith("-")) {
            try {
                Float.parseFloat(text);
            } catch (NumberFormatException e) {
                return false;
            }
        }
        return true;
    }

    /**
     *  # at the start of the input's label indicates an integer input is required
     * @param label the input's label
     * @param text the text entered by the user
     * @return true if the input passes the check, false otherwise
     */
    public static boolean checkIntegerInput(String label, String text) {
        if (label.startsWith("#")) {
            try {
                Integer.parseInt(text);
            }
            catch (NumberFormatException e) {
                return false;
            }
        }
        return true;
    }

    /**
     * yyyy-mm-dd anywhere in the input's label indicates a date in the format YYYY-MM-DD is required.
     * Checked by trying to create a date with the input and returning false if an error occurs.
     * @param label the input's label
     * @param text the text entered by the user
     * @return true if the input passes the check, false otherwise
     */
    public static boolean checkDateInput(String label, String text) {
        if (label.contains("yyyy-mm-dd")) {
            ArrayList<Integer> dateComponents = new ArrayList<>();
            try {
                for (String dateComponent : text.split("-")) {
                    dateComponents.add(Integer.parseInt(dateComponent));
                }

                LocalDate.of(dateComponents.get(0), dateComponents.get(1), dateComponents.get(2));
            }
            catch (Exception ex) {
                return false;
            }
        }
        return true;
    }

    /**
     *  * "longitude" or "latitude" anywhere in the input's label indicates a coordinate in the range -90 to 90 is required
     * @param label the input's label
     * @param text the text entered by the user
     * @return true if the input passes the check, false otherwise
     */
    public static boolean checkCoordinates(String label, String text) {
        if (label.contains("longitude") || label.contains("latitude")) {
            try {
                float coordinate = Float.parseFloat(text);
                if ((coordinate > 90) || (coordinate < -90)) {
                    return false;
                }
            } catch (NumberFormatException e) {
                return false;
            }
        }
        return true;
    }

    /**
     * Checks if there is at least one stock levels value given in the add stocks page and that the stock levels values
     * are greater than 1.
     * @param stockLevels an array of hashmaps containing the storeID and associated user's input
     * @return true if the input passes the check, false otherwise
     */
    public static boolean checkStockLevelsConditions(ArrayList<HashMap<String, Object>> stockLevels) {
        try {
            if (stockLevels.size() == 0) {
                return false;
            }

            for (HashMap<String, Object> stockLevelMap : stockLevels) {
                int stockLevel = Integer.parseInt(((JTextField) stockLevelMap.get("textField")).getText());
                if (stockLevel < 1) {
                    return false;
                }
            }
        } catch (NumberFormatException e) {
            return false;
        }
        return true;
    }

    /**
     * Called upon when adding storage locations to check the stores added in the stores pop up have been given an
     * integer capacity greater than 0
     * @param addStores a list of AddStore objects that hold the capacity inputted by the user
     * @return true if the input passes the check, false otherwise
     */
    public static boolean checkCapacitiesCondition(ArrayList<AddStore> addStores) {
        try {
            if (addStores.size() == 0) {
                return false;
            }

            for (AddStore addStore : addStores) {
                int capacity = Integer.parseInt(addStore.getCapacity());
                if (capacity < 1) {
                    return false;
                }
            }
        } catch (NumberFormatException e) {
            return false;
        }
        return true;
    }

    /**
     * Called upon when adding vaccines to check the vaccine lifespans (in days) are integers greater than 1
     * @param addLifespans a list of AddVaccineLifespan objects that hold the lifespans of the vaccine at different temperature
     *                     ranges
     * @return true if the input passes the check, false otherwise
     */
    public static boolean checkLifespanConditions(ArrayList<AddVaccineLifespan> addLifespans) {
        try {
            if (addLifespans.size() == 0) {
                return false;
            }

            for (AddVaccineLifespan addLifespan : addLifespans) {
                int lifespan = Integer.parseInt(addLifespan.getLifespan());
                if (lifespan < 1) {
                    return false;
                }
            }
        } catch (NumberFormatException e) {
            return false;
        }
        return true;
    }

    /**
     * Called upon when adding vaccines, checks the minimum temperature is less than the maximum temperature for the vaccine
     * lifespans temperature range
     * @param addLifespans a list of AddVaccineLifespan objects that hold the lifespans of the vaccine at different temperature
     *                     ranges
     * @return true if the input passes the check, false otherwise
     */
    public static boolean checkTemperatureConditions(ArrayList<AddVaccineLifespan> addLifespans) {
        for (AddVaccineLifespan addLifespan : addLifespans) {
            int minimumTemperature = (int) addLifespan.getMinimumTemperature();
            int maximumTemperature = (int) addLifespan.getMaximumTemperature();
            if (minimumTemperature > maximumTemperature) {
                return false;
            }
        }
        return true;
    }

    /**
     * Called upon when adding vaccines, checks the minimum age is less than the maximum age
     * @return true if the input passes the check, false otherwise
     */
    public static boolean checkAgeConditions(JTextField minimumAgeTextField, JTextField maximumAgeTextField) {
        int minimumAge = Integer.parseInt(minimumAgeTextField.getText());
        int maximumAge = Integer.parseInt(maximumAgeTextField.getText());
        if (maximumAge - minimumAge >= 0) {
            return true;
        }
        return false;
    }
}