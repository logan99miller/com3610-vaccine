package Automation;

import Data.Data;

import java.util.HashMap;

public class Factory extends StorageLocation {

    // Increases the stock levels of all factories if they are open
    public static void updateStockLevels(Data data, int updateRate, int simulationSpeed) {
        HashMap<String, HashMap<String, Object>> factories = data.getFactories();

        for (String key : factories.keySet()) {
            HashMap<String, Object> factory = factories.get(key);
            HashMap<String, HashMap<String, String>> openingTimes = (HashMap<String, HashMap<String, String>>) factory.get("openingTimes");
            if (isOpen(data, openingTimes)) {
                factories.put(key, updateStockLevel(data, factory, updateRate, simulationSpeed));
            }
        }
        data.setFactories(factories);
    }

    // Increases the stock levels of the given factory based on its rate of production and the systems updateRate and simulation speed
    private static HashMap<String, Object> updateStockLevel(Data data, HashMap<String, Object> factory, int updateRate, int simulationSpeed) {
        int vaccinesPerMin = Integer.parseInt((String) factory.get("Factory.vaccinesPerMin"));
        HashMap<String, HashMap<String, Object>> stores = (HashMap<String, HashMap<String, Object>>) factory.get("stores");
        int vaccinesToAdd = (vaccinesPerMin * updateRate * simulationSpeed) / 60000;
        String vaccineID = (String) factory.get("Manufacturer.vaccineID");

        stores = addToStores(data, stores, vaccinesToAdd, vaccineID);
        factory.put("stores", stores);
        return factory;
    }
}
