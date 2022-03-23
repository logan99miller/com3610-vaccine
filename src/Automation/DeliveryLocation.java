/**
 * Parent class to all delivery locations (vaccination centres and distribution centres). Contains methods to order vaccines.
 */
package Automation;

import Core.ActivityLog;

import java.util.HashMap;
import java.util.Optional;

import static Data.Utils.getAllVaccinesInStorage;

public class DeliveryLocation extends StorageLocation {

    /**
     * Orders a vaccine from the most suitable origin using the most suitable van to the given destination
     * @param origins all potential origins to supply the vaccines, in the format  HashMap<primaryKeyValue, HashMap<columName, databaseValue>>
     * @param destination where the vaccines are going to be delivered to, in the format HashMap<columName, databaseValue>
     * @param vans all potential vans to deliver the vaccines, in the format HashMap<primaryKeyValue, HashMap<columName, databaseValue>>
     * @param amount the amount of vaccines being ordered
     * @param vaccineID the type of vaccines being ordered
     * @return the vans hashmap with the order added to one of the vans, in the format HashMap<primaryKeyValue, HashMap<columName, databaseValue>>
     */
    protected static HashMap<String, HashMap<String, Object>> orderVaccine(
        ActivityLog activityLog, HashMap<String, HashMap<String, Object>> origins, HashMap<String, Object> destination,
        HashMap<String, HashMap<String, Object>> vans, int amount, String vaccineID
    ) {
        HashMap<String, Object> res = getOriginAndVanWithShortestDistance(activityLog, origins, destination, vans, amount, vaccineID);

        if (res != null) {
            double distance = (double) res.get("distance");

            HashMap<String, Object> origin = (HashMap<String, Object>) res.get("origin");
            HashMap<String, Object> van = (HashMap<String, Object>) res.get("van");

            HashMap<String, HashMap<String, Object>> vanStores = (HashMap<String, HashMap<String, Object>>) van.get("stores");

            String vanStoreKey = vanStores.keySet().iterator().next();
            HashMap<String, Object> vanStore = vanStores.get(vanStoreKey);
            String vanStoreID = (String) vanStore.get("Store.storeID");

            HashMap<String, HashMap<String, Object>> vaccinesInStorage = getVaccinesInStorage(origin, amount, vanStoreID);

            vanStore.put("vaccinesInStorage", vaccinesInStorage);
            vanStores.put(vanStoreKey, vanStore);

            addOrderToActivityLog(activityLog, van, origin, destination);
            van = addOrderToVan(van, origin, destination, distance, vanStores);

            vans.put((String) van.get("Van.vanID"), van);
        }
        return vans;
    }

    /**
     * Adds the vaccines ordered to the van's stores and changes the vans values to reflect the delivery it is making
     * @return the van with the order details added
     */
    private static HashMap<String, Object> addOrderToVan(
        HashMap<String, Object> van, HashMap<String, Object> origin, HashMap<String, Object> destination, double distance,
        HashMap<String, HashMap<String, Object>> vanStores
    ) {
        String travelTime = String.valueOf(Distance.getTravelTime(distance));

        van.put("stores", vanStores);
        van.put("Van.deliveryStage", "toOrigin");
        van.put("Van.originID", origin.get("Location.locationID"));
        van.put("Van.destinationID", destination.get("Location.locationID"));
        van.put("Van.totalTime", travelTime);
        van.put("Van.remainingTime", travelTime);
        van.put("Van.change", "change");

        return van;
    }

    /**
     * Finds the origin and van with the combined shortest distance to the given destination. Returns the results as a
     * hashmap containing the origin, van and distance.
     * @param activityLog used to add activity logs if there are no origins or vans available to complete a delivery
     * @param origins all potential origins that can supply the destination
     * @param destination the destination which needs to be supplied with vaccines
     * @param vans all potential vans that can deliver the vaccines
     * @param amount the amount of vaccines which need to be supplied
     * @param vaccineID the type of vaccine which needs to be supplied
     * @return the origin and van with the shortest distance to the destination
     */
    private static HashMap<String, Object> getOriginAndVanWithShortestDistance(
        ActivityLog activityLog, HashMap<String, HashMap<String, Object>> origins, HashMap<String, Object> destination,
        HashMap<String, HashMap<String, Object>> vans, int amount, String vaccineID
    ) {

        HashMap<String, HashMap<String, Object>> availableOrigins = getAvailableOrigins(origins, vans, amount, vaccineID);
        HashMap<String, HashMap<String, Object>> availableVans = getAvailableVans(vans); // SHOULD ALSO CONSIDER VAN CAPACITY

        if (availableOrigins.size() == 0) {
            addNoOriginsToActivityLog(activityLog, origins, destination);
            return null;
        }
        else if (availableVans.size() == 0) {
            addNoVansToActivityLog(activityLog, destination);
            return null;
        }

        // An arbitrarily high distance to ensure it is not shorter than other real distances
        double shortestDistance = 1000000;

        HashMap<String, Object> originWithShortestDistance = null;
        HashMap<String, Object> vanWithShortestDistance = null;

        for (String key : origins.keySet()) {

            HashMap<String, Object> origin = origins.get(key);

            HashMap<String, Object> res = getVanWithShortestDistance(origin, availableVans);

            double distance = (double) res.get("distance");

            // Combined distance of origin and van
            distance += Distance.getDistance(origin, destination);

            if (distance < shortestDistance) {
                shortestDistance = distance;
                vanWithShortestDistance = (HashMap<String, Object>) res.get("van");
                originWithShortestDistance = origin;
            }
        }

        // Generate hash map to return
        HashMap<String, Object> res = new HashMap();
        res.put("distance", shortestDistance);
        res.put("origin", originWithShortestDistance);
        res.put("van", vanWithShortestDistance);

        return res;
    }

    /**
     * Adds a message to the activity log saying an order for vaccines has been issued
     */
    private static void addOrderToActivityLog(ActivityLog activityLog, HashMap<String, Object> van, HashMap<String, Object> origin, HashMap<String, Object> destination) {
        String vanID = (String) van.get("Van.vanID");
        String originType = getLocationType(origin);
        String destinationType = getLocationType(destination);
        String originID = getID(origin);
        String destinationID = getID(destination);

        activityLog.add("Van " + vanID + " began delivering vaccines from " + originType + " " + originID + " to " + destinationType + " " + destinationID);
    }

    /**
     * Adds a message to the activity log saying there is no origins that can supply the needed amount of vaccines to the
     * destination
     * @param origins all origins, used to get the type of origin location
     * @param destination the destination which needed to be supplied with vaccines, used to get the type of destination and
     *                    the destination ID
     */
    private static void addNoOriginsToActivityLog(ActivityLog activityLog, HashMap<String, HashMap<String, Object>> origins, HashMap<String, Object> destination) {
        Optional<String> firstOriginKey = origins.keySet().stream().findFirst();
        String originType = getLocationType(origins.get(firstOriginKey));

        String destinationType = getLocationType(destination);
        String destinationID = getID(destination);

        activityLog.add("No " + originType + " available for " + destinationType + " " + destinationID + " to order from", true);
    }

    /**
     * Adds a message to the activity log saying there is no vans available for delivery
     * @param destination the destination that needed a van to deliver to
     */
    private static void addNoVansToActivityLog(ActivityLog activityLog, HashMap<String, Object> destination) {
        String destinationType = getLocationType(destination);
        String destinationID = getID(destination);

        activityLog.add("No vans available for " + destinationType + " " + destinationID + " to make and order", true);
    }

    /**
     * Gets the van closest to the given location, used to determine which van should be used for delivery
     * @param location the location to measure the vans distance to, in the format HashMap<columName, databaseValue>
     * @param vans all available vans, in the format HashMap<primaryKeyValue, HashMap<columName, databaseValue>>
     * @return the van with the shortest distance, in the format HashMap<columName, databaseValue>
     */
    private static HashMap<String, Object> getVanWithShortestDistance(HashMap<String, Object> location, HashMap<String, HashMap<String, Object>> vans) {

        // An arbitrarily high distance to ensure it is not shorter than other real distances
        double shortestDistance = 1000000;

        HashMap<String, Object> vanWithShortestDistance = null;

        for (String key : vans.keySet()) {
            HashMap<String, Object> van =  vans.get(key);
            double distance = Distance.getDistance(van, location);

            if (distance < shortestDistance) {
                shortestDistance = distance;
                vanWithShortestDistance = van;
            }
        }

        HashMap<String, Object> res = new HashMap();
        res.put("distance", shortestDistance);
        res.put("van", vanWithShortestDistance);

        return res;
    }

    /**
     * Returns a hashmap containing all origins available to supply the given amount of vaccines of the given vaccineID type
     * @param vans a hashmap of all origins, in the format HashMap<primaryKeyValue, HashMap<columName, databaseValue>>
     * @param vans a hashmap of all vans, in the format HashMap<primaryKeyValue, HashMap<columName, databaseValue>>
     * @param amount the amount of vaccines needed from the origin
     * @param vaccineID the type of vaccine needed from the origin
     * @return origins available to supply vaccines, in the format HashMap<primaryKeyValue, HashMap<columName, databaseValue>>
     */
    private static HashMap<String, HashMap<String, Object>> getAvailableOrigins(
        HashMap<String, HashMap<String, Object>> origins, HashMap<String, HashMap<String, Object>> vans, int amount, String vaccineID
    ) {
        HashMap<String, HashMap<String, Object>> availableOrigins = new HashMap<>();

        for (String key : origins.keySet()) {
            HashMap<String, Object>  origin = origins.get(key);
            int totalStock = getTotalStockInStorageLocation(origin, vans, vaccineID);

            if (totalStock >= amount) {
                availableOrigins.put(key, origin);
            }
        }
        return availableOrigins;
    }

    /**
     * Returns a hashmap containing all vans available for deliveries (i.e. ones currently not already delivering vaccines)
     * @param vans a hashmap of all vans, in the format HashMap<primaryKeyValue, HashMap<columName, databaseValue>>
     * @return vans available for delivery, in the format HashMap<primaryKeyValue, HashMap<columName, databaseValue>>
     */
    private static HashMap<String, HashMap<String, Object>> getAvailableVans(HashMap<String, HashMap<String, Object>> vans) {
        HashMap<String, HashMap<String, Object>> availableVans = new HashMap<>();

        for (String key : vans.keySet()) {
            HashMap<String, Object> van = vans.get(key);
            String deliveryStage = (String) van.get("Van.deliveryStage");

            if ((deliveryStage.equals("waiting")) || (deliveryStage.equals("toTransportLocation"))) {
                availableVans.put(key, van);
            }
        }
        return availableVans;
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
    private static int getTotalStockInStorageLocation(
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
    private static int getTotalStockInStore(HashMap<String, Object> store, String neededVaccineID) {
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
            }
        }
        return stock;
    }

    /**
     * Returns a hashmap of vaccines in storage found in the storage location with the required number of vaccines.
     * Currently, gets the 1st suitable stock it finds. Future developments could get the stock that will expire first.
     * The method will add the vaccines to a van but not remove it from the storage location. Removal is performed when
     * the van has completed its delivery
     * @param storageLocation the storage location to get the stock from, in the format HashMap<columName, databaseValue>
     * @param vaccinesNeeded the number of vaccines needed from the storage location
     * @param vanStoreID the ID of the store to add the vaccines to
     * @return the vaccines found in the storage location, in the format HashMap<primaryKeyValue, HashMap<columName, databaseValue>>
     */
    private static HashMap<String, HashMap<String, Object>> getVaccinesInStorage(HashMap<String, Object> storageLocation, int vaccinesNeeded, String vanStoreID) {
        HashMap<String, HashMap<String, Object>> newVaccinesInStorage = new HashMap<>();
        HashMap<String, HashMap<String, Object>> allVaccinesInStorage = getAllVaccinesInStorage(storageLocation);

        for (String key : allVaccinesInStorage.keySet()) {

            HashMap<String, Object> vaccineInStorage = allVaccinesInStorage.get(key);

            int stockLevel = Integer.parseInt((String) vaccineInStorage.get("VaccineInStorage.stockLevel"));

            if (stockLevel >= vaccinesNeeded) {

                vaccineInStorage.put("VaccineInStorage.stockLevel", String.valueOf(vaccinesNeeded));
                vaccineInStorage.put("VaccineInStorage.storeID", vanStoreID);
                vaccineInStorage.remove("VaccineInStorage.vaccineInStorageID");
                vaccineInStorage.put("VaccineInStorage.change", "change");

                newVaccinesInStorage.put(key, vaccineInStorage);

                return newVaccinesInStorage;
            }
            else {
                newVaccinesInStorage.put(key, vaccineInStorage);
                vaccinesNeeded -= stockLevel;
            }
        }

        return newVaccinesInStorage;
    }

    /**
     * Gets the total vaccinations performed per hour across all vaccination centres
     * @param vaccinationCentres all vaccination centres, in the format HashMap<primaryKeyValue, HashMap<columName, databaseValue>>
     * @return the total vaccines per hour
     */
    protected static int getTotalVaccinesPerHour(HashMap<String, HashMap<String, Object>> vaccinationCentres) {
        int totalVaccinesPerHour = 0;

        for (String key : vaccinationCentres.keySet()) {
            HashMap<String, Object> vaccinationCentre = vaccinationCentres.get(key);
            totalVaccinesPerHour += Integer.parseInt((String) vaccinationCentre.get("VaccinationCentre.vaccinesPerHour"));
        }

        return totalVaccinesPerHour;
    }

    /**
     * Gets the total storage capacity across all vaccination centres
     * @param vaccinationCentres all vaccination centres, in the format HashMap<primaryKeyValue, HashMap<columName, databaseValue>>
     * @return the total capacity
     */
    protected static int getTotalCapacity(HashMap<String, HashMap<String, Object>> vaccinationCentres) {
        int totalCapacity = 0;

        for (String key : vaccinationCentres.keySet()) {
            HashMap<String, Object> vaccinationCentre = vaccinationCentres.get(key);
            totalCapacity += getCapacity(vaccinationCentre);
        }

        return totalCapacity;
    }
}