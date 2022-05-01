/**
 * The parent class of all storage locations (factories, distribution centres and vaccination centres). Used to add vaccines
 * to their storage and get the total capacity of the storage location
 */
package Automation;

import Data.Data;
import java.time.LocalDate;
import java.util.HashMap;

public class StorageLocation extends Location{

    /**
     * Adds the given amount of vaccines with the given vaccineID and expirationDate to the given stores. Currently, adds
     * vaccines to the first store with the available capacity, future versions should be more selective in which store to put
     * it in (e.g. one which will give it the best shelf life)
     * @param data Used to get the current date and the vaccine hash map from the vaccineID
     * @param stores the stores to add vaccines to, in the format in the format HashMap<storeID, HashMap<columnName, databaseValue>>
     * @param totalAmount the amount of vaccines to add
     * @param vaccineID the type of vaccine to be stored
     * @param creationDate the date the vaccines were created (assumed to be the current date if not given)
     * @param expirationDate the date the vaccines will expire
     * @return the modified stores hashmap, in the format HashMap<storeID, HashMap<columnName, databaseValue>>
     */
    protected static HashMap<String, HashMap<String, Object>> addToStores(
        Data data, HashMap<String, HashMap<String, Object>> stores, int totalAmount, String vaccineID, String creationDate, String expirationDate
    ) {

        // Go through all stores and add the new vaccines to the store. If the store doesn't fit all the new vaccines,
        // continue the process by adding to the next store
        for (String key : stores.keySet()) {

            if (totalAmount > 0) {
                HashMap<String, Object> store = stores.get(key);

                int availableCapacity = getAvailableCapacity(store);

                // Only add to the store if it has available capacity
                if (availableCapacity > 0) {

                    // Add all new vaccines if possible, if not fill the stores remaining space
                    int amount = Math.min(availableCapacity, totalAmount);

                    totalAmount -= amount;
                    store = addToStore(data, store, amount, vaccineID, creationDate, expirationDate);
                }

                stores.put(key, store);
            }
        }
        return stores;
    }

    // Overloaded addToStores methods for when the creation date or expiration date are unknown

    protected static HashMap<String, HashMap<String, Object>> addToStores(
        Data data, HashMap<String, HashMap<String, Object>> stores, int totalAmount, String vaccineID, String creationDate
    ) {
        return addToStores(data, stores, totalAmount, vaccineID, creationDate, null);
    }

    protected static HashMap<String, HashMap<String, Object>> addToStores(
        Data data, HashMap<String, HashMap<String, Object>> stores, int totalAmount, String vaccineID
    ) {
        return addToStores(data, stores, totalAmount, vaccineID, null, null);
    }

    /**
     * Adds the given amount of vaccines (with the given vaccineID, creationDate and expirationDate) to the given store.
     * If a vaccinesInStorage record with the given details already exists, the vaccines are added by increasing the existing
     * records stock level, otherwise a new vaccinesInStorage record is created
     * @param data Used to get the current date
     * @param store the store being added to, in the format HashMap<columnName, databaseValue>
     * @param amount the amount of vaccines to add
     * @param vaccineID the type of vaccine to be stored
     * @param creationDate the date the vaccines were created, in the format "YYYY-MM-DD"
     * @param expirationDate the date the vaccines will expire, in the format "YYYY-MM-DD"
     * @return the given store hash map, modified to include the added vaccines
     */
    private static HashMap<String, Object> addToStore(
        Data data, HashMap<String, Object> store, int amount, String vaccineID, String creationDate, String expirationDate
    ) {

        HashMap<String, HashMap<String, String>> vaccinesInStorage = (HashMap<String, HashMap<String, String>>) store.get("vaccinesInStorage");

        if (creationDate == null) {
            creationDate = data.getCurrentDate().toString();
        }
        if (expirationDate == null) {
            int storageTemperature = Integer.parseInt((String) store.get("Store.temperature"));
            expirationDate = getExpirationDate(data, vaccineID, storageTemperature);
        }

        if (!matchingVaccineInStorage(vaccinesInStorage, vaccineID, creationDate, expirationDate, amount)) {
            HashMap<String, String> vaccineInStorage = new HashMap();
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

    /**
     * Used by addToStore() to see if there is matching vaccinesInStorage in the store, if there is this method adds the vaccines
     * to the matching store and returns true, otherwise returns false
     * @param vaccinesInStorage in the format HashMap<vaccineInStorageID, HashMap<columnName, databaseValue>>
     * @param vaccineID the type of vaccine to be stored
     * @param creationDate the date the vaccines were created, in the format "YYYY-MM-DD"
     * @param expirationDate the date the vaccines will expire, in the format "YYYY-MM-DD"
     * @param amount the amount of vaccines to add
     * @return
     */
    private static boolean matchingVaccineInStorage(
        HashMap<String, HashMap<String, String>> vaccinesInStorage, String vaccineID, String creationDate, String expirationDate, int amount
    ) {
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
                return true;
            }
        }
        return false;
    }

    /**
     * Calculates when the given vaccine type will expire based on the storage temperature, current date and the vaccine's
     * lifespan depending on different temperatures
     * @param data used to get the vaccines hash map (in the format Hashmap<vaccineID, HashMap<columnName, databaseValue> and
     *             current date
     * @param vaccineID the type of vaccine
     * @param storageTemperature the temperature the vaccine will be stored at
     * @return the expiration date, in the format "YYYY-MM-DD"
     */
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

    /**
     * Gets how many more vaccines can be stored in the given store by subtracting the used capacity from the total capacity
     * @param store the store to get the available capacity from
     * @return the available capacity
     */
    protected static int getAvailableCapacity(HashMap<String, Object> store) {
        int totalCapacity = Integer.parseInt((String) store.get("Store.capacity"));
        int usedCapacity = getTotalStock(store);

        return (totalCapacity - usedCapacity);
    }

    /**
     * Gets the amount of vaccines in a given storage location by iterating through every store in the storage location and
     * then removing any stocks currently being delivered to another storage location
     * @param storageLocation in the format HashMap<columName, databaseValue>
     * @param vans in the format HashMap<primaryKeyValue, HashMap<columName, databaseValue>>, used to get stock being delivered
     *             to another storage location
     * @param vaccineID the type of vaccine which we are counting the stock of
     * @return the total stock in the given storage location
     */
    protected static int getTotalStockInStorageLocation(
            HashMap<String, Object> storageLocation, HashMap<String, HashMap<String, Object>> vans, String vaccineID
    ) {
        int stock = 0;
        HashMap<String, HashMap<String, Object>> stores = (HashMap<String, HashMap<String, Object>>) storageLocation.get("stores");

        // Get the total stock currently in the storage location
        for (String key : stores.keySet()) {
            HashMap<String, Object> store = stores.get(key);
            stock += getTotalStockInStore(store, vaccineID);
        }

        String storageLocationID = (String) storageLocation.get("StorageLocation.storageLocationID");

        // Remove any stock which will shortly be delivered to another storage location
        for (String key : vans.keySet()) {

            HashMap<String, Object> van = vans.get(key);
            String originID = (String) van.get("Van.originID");

            if (originID != null) {
                String deliveryStage = (String) van.get("Van.deliveryStage");

                if (originID.equals(storageLocationID) && deliveryStage.equals("toOrigin")) {
                    stock -= getTotalStockInStore(van, vaccineID);
                }
            }
        }
        return stock;
    }

    /**
     * The amount of vaccines of the given vaccineID in the given store.
     * @param store the store to get the total stock from, in the format HashMap<columName, databaseValue>
     * @param neededVaccineID the type of vaccine we are interested in
     * @return the amount of vaccines
     */
    public static int getTotalStockInStore(HashMap<String, Object> store, String neededVaccineID) {
        int stock = 0;
        HashMap<String, HashMap<String, Object>> vaccinesInStorage = (HashMap<String, HashMap<String, Object>>) store.get("vaccinesInStorage");

        if (vaccinesInStorage != null) {
            for (String key : vaccinesInStorage.keySet()) {
                HashMap<String, Object> vaccineInStorage = vaccinesInStorage.get(key);

                if (neededVaccineID != null) {
                    String storedVaccineID = (String) vaccineInStorage.get("VaccineInStorage.vaccineID");

                    if (storedVaccineID.equals(neededVaccineID)) {
                        stock += Integer.parseInt((String) vaccineInStorage.get("VaccineInStorage.stockLevel"));
                    }
                }
                stock += Integer.parseInt((String) vaccineInStorage.get("VaccineInStorage.stockLevel"));
            }
        }
        return stock;
    }

    /**
     * The total number of vaccines in the given store which are the given type of vaccine
     * @param store the store to get the total stock from
     * @param vaccineID the type of vaccine
     * @return the
     */
    protected static int getTotalStock(HashMap<String, Object> store, String vaccineID) {
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

    /**
     * Overloaded method for when the stock of all vaccine types is wanted
     */
    protected static int getTotalStock(HashMap<String, Object> store) {
        return getTotalStock(store, null);
    }

    /**
     * The total storage capacity for the given storage location
     */
    protected static int getCapacity(HashMap<String, Object> storageLocation) {
        HashMap<String, HashMap<String, Object>> stores = (HashMap<String, HashMap<String, Object>>) storageLocation.get("stores");
        int totalCapacity = 0;

        for (String key : stores.keySet()) {
            HashMap<String, Object> store = stores.get(key);
            totalCapacity += Integer.parseInt((String) store.get("Store.capacity"));
        }

        return totalCapacity;
    }

    protected static HashMap<String, HashMap<String, Object>> simulateVaccineWastage(HashMap<String, HashMap<String, Object>> storageLocations) {
//        for (String storageLocationKey : storageLocations.keySet()) {
//            HashMap<String, Object> storageLocation = storageLocations.get(storageLocationKey);
//
//            // Used in simulations when some vaccine wastage occurs
//            double random = Math.random();
//            if (random < 0.02) {
//                int wastage = 0;
//                HashMap<String, HashMap<String, Object>> stores = (HashMap<String, HashMap<String, Object>>) storageLocation.get("stores");
//                for (String storeKey : stores.keySet()) {
//                    HashMap<String, Object> store = stores.get(storeKey);
//                    HashMap<String, HashMap<String, Object>> vaccinesInStorage = (HashMap<String, HashMap<String, Object>>) store.get("vaccinesInStorage");
//                    for (String vaccineInStorageKey : vaccinesInStorage.keySet()) {
//                        HashMap<String, Object> vaccineInStorage = vaccinesInStorage.get(vaccineInStorageKey);
//                        int stockLevel = Integer.parseInt((String) vaccineInStorage.get("VaccineInStorage.stockLevel"));
//                        int newStockLevel = stockLevel - Math.round(stockLevel / 4);
//                        wastage += newStockLevel - stockLevel;
//                        vaccineInStorage.put("VaccineInStorage.stockLevel", String.valueOf(newStockLevel));
//                        vaccinesInStorage.put(vaccineInStorageKey, vaccineInStorage);
//                    }
//                    store.put("vaccinesInStorage", vaccinesInStorage);
//                    stores.put(storeKey, store);
//                }
//                storageLocation.put("stores", stores);
//                String storageLocationID = (String) storageLocation.get("Location.locationID");
//                System.out.println("StorageLocation " + storageLocationID + " wasted " + wastage + " vaccines");
//            }
//            storageLocations.put(storageLocationKey, storageLocation);
//        }
        return storageLocations;
    }
}