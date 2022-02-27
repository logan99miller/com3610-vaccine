import java.util.HashMap;

public class DeliveryLocation extends StorageLocation {

    protected static HashMap<String, HashMap<String, Object>> orderVaccine(HashMap<String, HashMap<String, Object>> origins,
     HashMap<String, Object> destination, HashMap<String, HashMap<String, Object>> vans, int amount, String vaccineID) {
        HashMap<String, Object> res = getOriginAndVanWithShortestDistance(origins, destination, vans, amount, vaccineID);
        if (res != null) {
            double distance = (double) res.get("distance");
            HashMap<String, Object> origin = (HashMap<String, Object>) res.get("origin");
            HashMap<String, Object> van = (HashMap<String, Object>) res.get("van");

            HashMap<String, HashMap<String, Object>> originStores = (HashMap<String, HashMap<String, Object>>) origin.get("stores");

            HashMap<String, HashMap<String, Object>> vanStores = (HashMap<String, HashMap<String, Object>>) van.get("stores");
            String vanStoreKey = vanStores.keySet().iterator().next();
            HashMap<String, Object> vanStore = vanStores.get(vanStoreKey);
            String vanStoreID = (String) vanStore.get("Store.storeID");
            HashMap<String, HashMap<String, Object>> vaccinesInStorage = getVaccinesInStorage(originStores, amount, vanStoreID);
            vanStore.put("vaccinesInStorage", vaccinesInStorage);
            vanStores.put(vanStoreKey, vanStore);
            van.put("stores", vanStores);
            van.put("Van.deliveryStage", "toOrigin");
            van.put("Van.originID", origin.get("Location.locationID"));
            van.put("Van.destinationID", destination.get("Location.locationID"));
            van.put("Van.remainingTime", String.valueOf(Distance.getTravelTime(distance)));
            van.put("Van.change", "change");
            vans.put((String) van.get("Van.vanID"), van);
        }
        return vans;
    }

    private static HashMap<String, Object> getOriginAndVanWithShortestDistance(HashMap<String, HashMap<String, Object>> origins,
     HashMap<String, Object> destination, HashMap<String, HashMap<String, Object>> vans, int amount, String vaccineID) {
        HashMap<String, HashMap<String, Object>> availableOrigins = getAvailableOrigins(origins, vans, amount, vaccineID);
        HashMap<String, HashMap<String, Object>> availableVans = getAvailableVans(vans); // SHOULD ALSO CONSIDER VAN CAPACITY
        if (availableOrigins.size() == 0) {
            System.out.println("Not enough origins");
            return null;
        }
        else if (availableVans.size() == 0) {
            System.out.println("Not enough vans");
            return null;
        }
        double shortestDistance = 1000000;
        HashMap<String, Object> originWithShortestDistance = null;
        HashMap<String, Object> vanWithShortestDistance = null;

        for (String key : origins.keySet()) {
            HashMap<String, Object> origin = origins.get(key);
            HashMap<String, Object> res = getVanWithShortestDistance(origin, availableVans);
            double distance = (double) res.get("distance");
            distance += Distance.getDistance(origin, destination);
            if (distance < shortestDistance) {
                shortestDistance = distance;
                vanWithShortestDistance = (HashMap<String, Object>) res.get("van");
                originWithShortestDistance = origin;
            }
        }

        HashMap<String, Object> res = new HashMap();
        res.put("distance", shortestDistance);
        res.put("origin", originWithShortestDistance);
        res.put("van", vanWithShortestDistance);

        return res;
    }

    private static HashMap<String, Object> getVanWithShortestDistance(HashMap<String, Object> origin, HashMap<String, HashMap<String, Object>> vans) {
        double shortestDistance = 1000000;
        HashMap<String, Object> vanWithShortestDistance = null;

        for (String key : vans.keySet()) {
            HashMap<String, Object> van =  vans.get(key);
            double distance = Distance.getDistance(van, origin);
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

    private static HashMap<String, HashMap<String, Object>> getAvailableOrigins(HashMap<String, HashMap<String, Object>> origins,
    HashMap<String, HashMap<String, Object>> vans, int amount, String vaccineID) {
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

    private static int getTotalStockInStorageLocation(HashMap<String, Object> storageLocation, HashMap<String, HashMap<String, Object>> vans,
                                                      String vaccineID) {
        int stock = 0;
        HashMap<String, HashMap<String, Object>> stores = (HashMap<String, HashMap<String, Object>>) storageLocation.get("stores");
        for (String key : stores.keySet()) {
            HashMap<String, Object> store = stores.get(key);
            stock += getTotalStockInStore(store, vaccineID);
        }
        String storageLocationID = (String) storageLocation.get("StorageLocation.storageLocationID");
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

    // Method Currently only gets 1st suitable stock, but in future should get stock that will expire first
    private static HashMap<String, HashMap<String, Object>> getVaccinesInStorage(HashMap<String, HashMap<String, Object>> stores, int vaccinesNeeded, String vanStoreID) {
        System.out.println("getVaccinesInStorage(): ");
        HashMap<String, HashMap<String, Object>> newVaccinesInStorage = new HashMap<>();
        for (String keyI : stores.keySet()) {
            HashMap<String, Object> store = stores.get(keyI);
            HashMap<String, HashMap<String, Object>> storeVaccinesInStorage = (HashMap<String, HashMap<String, Object>>) store.get("vaccinesInStorage");
            for (String keyJ : storeVaccinesInStorage.keySet()) {
                HashMap<String, Object> vaccineInStorage = storeVaccinesInStorage.get(keyJ);
                int stockLevel = Integer.parseInt((String) vaccineInStorage.get("VaccineInStorage.stockLevel"));
                if (stockLevel >= vaccinesNeeded) {
                    vaccineInStorage.put("VaccineInStorage.stockLevel", String.valueOf(vaccinesNeeded));
                    vaccineInStorage.put("VaccineInStorage.storeID", vanStoreID);
                    vaccineInStorage.remove("VaccineInStorage.vaccineInStorageID");
                    vaccineInStorage.put("VaccineInStorage.change", "change");
                    newVaccinesInStorage.put(keyJ, vaccineInStorage);
                    return newVaccinesInStorage;
                }
                else {
                    newVaccinesInStorage.put(keyJ, vaccineInStorage);
                    vaccinesNeeded -= stockLevel;
                }
            }
        }
        return newVaccinesInStorage;
    }
}
