/**
 * Automates factory production by updating the stock level of all factories
 */
package Automation;

import Core.AutomateSystem;
import Data.Data;
import java.util.HashMap;

public class Factory extends StorageLocation {

    /**
     * Increases the stock levels of all factories if they are open
     * @param automateSystem used to get data from the data class as well as the updateRate and simulationSpeed
     */
    public static void updateStockLevels(AutomateSystem automateSystem) {
        Data data = automateSystem.getData();
        int updateRate = automateSystem.getUpdateRate();
        int simulationSpeed = automateSystem.getSimulationSpeed();

        HashMap<String, HashMap<String, Object>> factories = data.getFactories();

        if (factories != null) {
            for (String key : factories.keySet()) {
                HashMap<String, Object> factory = factories.get(key);
                HashMap<String, HashMap<String, String>> openingTimes = (HashMap<String, HashMap<String, String>>) factory.get("openingTimes");

                if (isOpen(data, openingTimes)) {
                    factories.put(key, updateStockLevel(data, factory, updateRate, simulationSpeed));
                }
            }
            data.setFactories(factories);
        }
    }

    /**
     * Increases the stock levels of the given factory based on its rate of production and the systems updateRate and simulation speed
     * @param data used to get the current date so the vaccine's creation date can be recorded
     * @param factory the factory who's stock level is being updated
     * @param updateRate How often the system updates in milliseconds
     * @param simulationSpeed
     *      How much current time is multiplied by to increase speed
     *      E.g. simulationSpeed = 2 means every 1 minute in real life is 2 minutes in the system
     * @return the factory map with it's updated stock level
     */
    private static HashMap<String, Object> updateStockLevel( Data data, HashMap<String, Object> factory, int updateRate, int simulationSpeed) {
        int vaccinesPerMin = Integer.parseInt((String) factory.get("Factory.vaccinesPerMin"));
        HashMap<String, HashMap<String, Object>> stores = (HashMap<String, HashMap<String, Object>>) factory.get("stores");
        int vaccinesToAdd = (vaccinesPerMin * updateRate * simulationSpeed) / 60000;
        String vaccineID = (String) factory.get("Manufacturer.vaccineID");

        stores = addToStores(data, stores, vaccinesToAdd, vaccineID);
        factory.put("stores", stores);
        return factory;
    }
}
