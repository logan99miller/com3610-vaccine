package UserInterface.AddUtils;

import javax.swing.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;

public class CheckInputs {

    public static boolean checkRequiredInput(String label, String text) {
        if (label.startsWith("*") || (label.startsWith("-*"))) {
            return !text.equals("");
        }
        return true;
    }

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
}