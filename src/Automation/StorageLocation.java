package Automation;

import Data.Data;
import java.time.LocalDate;
import java.util.HashMap;

public class StorageLocation extends Location{

    // Adds the given amount of vaccines with the given vaccineID and expirationDate to the given. Method currently will
    // add vaccines to the first store with capacity, but future versions will be more selective in which store to put it
    // in (e.g. one which will give it the best shelf life)
    protected static HashMap<String, HashMap<String, Object>> addToStores(
        Data data, HashMap<String, HashMap<String, Object>> stores, int totalAmount,
        String vaccineID, String creationDate, String expirationDate
    ) {
        for (String key : stores.keySet()) {
            if (totalAmount > 0) {
                HashMap<String, Object> store = stores.get(key);
                int availableCapacity = getAvailableCapacity(store);
                if (availableCapacity > 0) {
                    int amount = Math.min(availableCapacity, totalAmount);
                    totalAmount -= amount;
                    store = addToStore(data, store, amount, vaccineID, creationDate, expirationDate);
                }
                stores.put(key, store);
            }
        }
        return stores;
    }

    protected static HashMap<String, HashMap<String, Object>> addToStores(
        Data data, HashMap<String, HashMap<String, Object>> stores,
        int totalAmount, String vaccineID, String creationDate
    ) {
        return addToStores(data, stores, totalAmount, vaccineID, creationDate, null);
    }
    protected static HashMap<String, HashMap<String, Object>> addToStores(
        Data data, HashMap<String, HashMap<String, Object>> stores,
        int totalAmount, String vaccineID
    ) {
        return addToStores(data, stores, totalAmount, vaccineID, null, null);
    }

    private static HashMap<String, Object> addToStore(Data data, HashMap<String, Object> store, int amount, String vaccineID, String creationDate, String expirationDate) {

        HashMap<String, HashMap<String, String>> vaccinesInStorage = (HashMap<String, HashMap<String, String>>) store.get("vaccinesInStorage");

        boolean foundMatchingVaccineInStorage = false;
        if (creationDate == null) {
            creationDate = data.getCurrentDate().toString();
        }
        if (expirationDate == null) {
            int storageTemperature = Integer.parseInt((String) store.get("Store.temperature"));
            expirationDate = getExpirationDate(data, vaccineID, storageTemperature);
        }

        for (String key : vaccinesInStorage.keySet()) {
            HashMap<String, String> vaccineInStorage = vaccinesInStorage.get(key);
            String existingVaccineID = vaccineInStorage.get("VaccineInStorage.vaccineID");

            // Substring removes time value (e.g. 09:00 that is extracted form database but not used)
            String existingCreationDate = vaccineInStorage.get("VaccineInStorage.creationDate").substring(0, 10);
            String existingExpirationDate = vaccineInStorage.get("VaccineInStorage.expirationDate").substring(0, 10);
            creationDate = creationDate.substring(0, 10);

            if (existingVaccineID.equals(vaccineID) && (existingExpirationDate.equals(expirationDate)) && (existingCreationDate.equals(creationDate))) {
                int stockLevel = Integer.parseInt(vaccineInStorage.get("VaccineInStorage.stockLevel"));
                vaccineInStorage.put("VaccineInStorage.stockLevel", Integer.toString(stockLevel + amount));
                vaccineInStorage.put("VaccineInStorage.change", "change");
                vaccinesInStorage.put(key, vaccineInStorage);
                foundMatchingVaccineInStorage = true;
            }
        }

        if (!foundMatchingVaccineInStorage) {
            HashMap<String, String> vaccineInStorage = new HashMap<String, String>();
            vaccineInStorage.put("VaccineInStorage.stockLevel", String.valueOf(amount));
            vaccineInStorage.put("VaccineInStorage.vaccineID", vaccineID);
            vaccineInStorage.put("VaccineInStorage.storeID", (String) store.get("Store.storeID"));
            vaccineInStorage.put("VaccineInStorage.creationDate", creationDate);
            vaccineInStorage.put("VaccineInStorage.expirationDate", expirationDate);
            vaccineInStorage.put("VaccineInStorage.change", "change");
            vaccinesInStorage.put("newID", vaccineInStorage);
        }

        store.put("vaccinesInStorage", vaccinesInStorage);
        return store;
    }

    public static String getExpirationDate(Data data, String vaccineID, int storageTemperature) {
        HashMap<String, Object> vaccine = data.getVaccines().get(vaccineID);
        HashMap<String, HashMap<String, Object>> lifespans = (HashMap<String, HashMap<String, Object>>) vaccine.get("lifespans");
        int lifespanValue = 0;
        for (String key : lifespans.keySet()) {
            HashMap<String, Object> lifespan = lifespans.get(key);
            int lowestTemperature = Integer.parseInt((String) lifespan.get("VaccineLifespan.lowestTemperature"));
            int highestTemperature = Integer.parseInt((String) lifespan.get("VaccineLifespan.highestTemperature"));
            if ((lowestTemperature < storageTemperature) && (highestTemperature > storageTemperature)) {
                lifespanValue = Integer.parseInt((String) lifespan.get("VaccineLifespan.lifespan"));
                break;
            }
        }
        LocalDate currentDate = data.getCurrentDate();
        return LocalDate.from(currentDate.plusDays(lifespanValue)).toString();
    }

    protected static int getAvailableCapacity(HashMap<String, Object> store) {
        int totalCapacity = Integer.parseInt((String) store.get("Store.capacity"));
        int usedCapacity = getTotalStock(store);
        return (totalCapacity - usedCapacity);
    }

    private static int getTotalStock(HashMap<String, Object> store, String vaccineID) {
        int stock = 0;
        HashMap<String, HashMap<String, Object>> vaccinesInStorage = (HashMap<String, HashMap<String, Object>>) store.get("vaccinesInStorage");
        if (vaccinesInStorage != null) {
            for (String key : vaccinesInStorage.keySet()) {
                HashMap<String, Object> vaccineInStorage = vaccinesInStorage.get(key);
                if (vaccineID != null) {
                    String storedVaccineID = (String) vaccineInStorage.get("VaccineInStorage.vaccineID");
                    if (storedVaccineID.equals(vaccineID)) {
                        stock += Integer.parseInt((String) vaccineInStorage.get("VaccineInStorage.stockLevel"));
                    }
                }
                else {
                    stock += Integer.parseInt((String) vaccineInStorage.get("VaccineInStorage.stockLevel"));
                }
            }
        }
        return stock;
    }

    private static int getTotalStock(HashMap<String, Object> store) {
        return getTotalStock(store, null);
    }

    protected static int getCapacity(HashMap<String, Object> storageLocation) {
        HashMap<String, HashMap<String, Object>> stores = (HashMap<String, HashMap<String, Object>>) storageLocation.get("stores");
        int totalCapacity = 0;
        for (String key : stores.keySet()) {
            HashMap<String, Object> store = stores.get(key);
            totalCapacity += Integer.parseInt((String) store.get("Store.capacity"));
        }
        return totalCapacity;
    }
}
