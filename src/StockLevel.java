import java.time.LocalDate;
import java.time.LocalTime;
import java.util.HashMap;

public class StockLevel {

    private static int getTravelTime(double distance) {
        final int SPEED_KMPH = 55;
        return (int) Math.round(distance / SPEED_KMPH);

    }

    private static int getTravelTime(HashMap<String, Object> locationA, HashMap<String, Object> locationB) {
        return getTravelTime(getDistance(locationA, locationB));
    }

    // Applies the Haversine formula to calculate the shortest distance over the earth's surface
    // Avoid use of cosine formula as it is unreliable for small distances (https://www.themathdoctors.org/distances-on-earth-2-the-haversine-formula/
    private static double getDistance(double longitudeA, double latitudeA, double longitudeB, double latitudeB) {
        final double EARTH_RADIUS = 6371; // in km
        double longitudeDifferenceRad = degreesToRadians(longitudeB - longitudeA);
        double latitudeDifferenceRad = degreesToRadians(latitudeB - latitudeA);
        double latitudeARad = degreesToRadians(latitudeA);
        double latitudeBRad = degreesToRadians(latitudeB);

        // a represents the square of half the chord length between the points
        double a =
            0.5 - Math.cos(latitudeDifferenceRad) / 2 +
            Math.cos(latitudeARad) * Math.cos(latitudeBRad) *
            (1 - Math.cos(longitudeDifferenceRad)) / 2;

        double angularDistance = 2 * Math.asin(Math.sqrt(a));

        return EARTH_RADIUS * angularDistance;
    }

    // Applies the Haversine formula to calculate the shortest distance over the earth's surface
    // Avoid use of cosine formula as it is unreliable for small distances (https://www.themathdoctors.org/distances-on-earth-2-the-haversine-formula/)
    private static double getDistance(HashMap<String, Object> locationA, HashMap<String, Object> locationB) {
        double longitudeA = Double.parseDouble((String) locationA.get("Location.longitude"));
        double latitudeA = Double.parseDouble((String) locationA.get("Location.latitude"));
        double longitudeB = Double.parseDouble((String) locationB.get("Location.longitude"));
        double latitudeB = Double.parseDouble((String) locationB.get("Location.latitude"));
        return getDistance(longitudeA, latitudeA, longitudeB, latitudeB);
    }

    private static double degreesToRadians(double degrees) {
        return degrees * (Math.PI / 180);
    }

    private static HashMap<String, Object> shortestDistance(HashMap<String, Object> origin, HashMap<String, Object> destination,
     HashMap<String, HashMap<String, Object>> availableVans) {
        double shortestDistance = 1000000;
        String bestVanID = "";

        // Get distance between origin and closest van
        for (String key : availableVans.keySet()) {
            HashMap<String, Object> van = availableVans.get(key);
            double distance = getDistance(van, origin);
            if (distance < shortestDistance) {
                shortestDistance = distance;
                bestVanID = (String) van.get("Van.vanID");
            }
        }

        shortestDistance += getDistance(origin, destination);

        HashMap<String, Object> res = new HashMap<>();
        res.put("vanID", bestVanID);
        res.put("distance", shortestDistance);

        return res;
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

    public static void updateDeliveries(Data data, int updateRate, int simulationSpeed) {
        HashMap<String, HashMap<String, Object>> vaccinationCentres = data.getVaccinationCentres();
        HashMap<String, HashMap<String, Object>> distributionCentres = data.getDistributionCentres();
        HashMap<String, HashMap<String, Object>> factories = data.getFactories();
        HashMap<String, HashMap<String, Object>> transporterLocations = data.getTransporterLocations();
        HashMap<String, HashMap<String, Object>> vans = data.getVans();
        for (String key : vans.keySet()) {
            HashMap<String, Object> van = vans.get(key);

            int remainingTime = Integer.parseInt((String) van.get("Van.remainingTime"));
            remainingTime -= ((updateRate / 100) * simulationSpeed);
            van.put("Van.remainingTime", String.valueOf(remainingTime));

            String deliveryStage = (String) van.get("Van.deliveryStage");
            String destinationID = (String) van.get("Van.destinationID");
            if ((remainingTime < updateRate) && (destinationID != null)) {
                HashMap[] destinationLocations = new HashMap[]{vaccinationCentres, distributionCentres};
                HashMap<String, Object> destination = getMapFromFieldValue(destinationLocations, "StorageLocation.storageLocationID", destinationID);;
                if (deliveryStage.equals("toOrigin")) {
                    van.put("Van.deliveryStage", "toDestination");
                    String originID = (String) van.get("Van.originID");
                    HashMap[] originLocations = new HashMap[] {factories, distributionCentres};
                    HashMap<String, Object> origin = getMapFromFieldValue(originLocations, "StorageLocation.storageLocationID", originID);
                    origin = removeVaccinesFromOrigin(origin, van);
                    van.put("Van.remainingTime", String.valueOf(getTravelTime(origin, destination)));
                }
                else if (deliveryStage.equals("toDestination")) {
                    van.put("Van.deliveryStage", "toTransporterLocation");
                    HashMap<String, Object> transporterLocation = transporterLocations.get(van.get("Van.transporterLocationID"));
                    destination = addVaccineToDestination(destination, van);
                    van.put("Van.remainingTime", String.valueOf(getTravelTime(destination, transporterLocation)));
                }
                else if (deliveryStage.equals("toTransporterLocation")) {
                    van.put("Van.deliveryStage", "waiting");
                }
                van.put("Van.change", "change");
            }
            vans.put(key, van);
        }
    }

    // REQUIRES HEAVY REFACTORING (MAY BE LINKED W/ ADD VACCINE TO DESTINATION FUNCTION
    private static HashMap<String, Object> removeVaccinesFromOrigin(HashMap<String, Object> origin, HashMap<String, Object> van) {
        HashMap<String,  HashMap<String, Object>> originStores = (HashMap<String,  HashMap<String, Object>>) origin.get("stores");
        HashMap<String,  HashMap<String, Object>> vanStores = (HashMap<String,  HashMap<String, Object>>) van.get("stores");
        for (String originStoreKey : originStores.keySet()) {
            HashMap<String, Object> originStore = originStores.get(originStoreKey);
            HashMap<String,  HashMap<String, String>> originVaccinesInStorage = (HashMap<String,  HashMap<String, String>>) originStore.get("vaccinesInStorage");
            for (String originVaccineInStorageKey : originVaccinesInStorage.keySet()) {
                HashMap<String, String> originVaccineInStorage = originVaccinesInStorage.get(originVaccineInStorageKey);
                String originVaccineID = originVaccineInStorage.get("VaccineInStorage.vaccineID");
                String originExpirationDate = originVaccineInStorage.get("VaccineInStorage.expirationDate");
                int originStockLevel = Integer.parseInt( originVaccineInStorage.get("VaccineInStorage.stockLevel"));
                for (String vanStoreKey : vanStores.keySet()) {
                    HashMap<String, Object> vanStore = vanStores.get(vanStoreKey);
                    HashMap<String,  HashMap<String, String>> vanVaccinesInStorage = (HashMap<String,  HashMap<String, String>>) vanStore.get("vaccinesInStorage");
                    for (String vanVaccineInStorageKey : vanVaccinesInStorage.keySet()) {
                        HashMap<String, String> vanVaccineInStorage = vanVaccinesInStorage.get(vanVaccineInStorageKey);
                        String vanVaccineID = vanVaccineInStorage.get("VaccineInStorage.vaccineID");
                        String vanExpirationDate = vanVaccineInStorage.get("VaccineInStorage.expirationDate");
                        int vanStockLevel = Integer.parseInt(vanVaccineInStorage.get("VaccineInStorage.stockLevel"));
                        if ((originVaccineID.equals(vanVaccineID)) && (originExpirationDate.equals(vanExpirationDate))) {
                            if (originStockLevel == vanStockLevel) {
                                originVaccinesInStorage.remove(originVaccineInStorageKey);
                            }
                            else {
                                int newStockLevel = originStockLevel - vanStockLevel;
                                originVaccineInStorage.put("VaccineInStorage.stockLevel", String.valueOf(newStockLevel));
                                originStore.put("Store.change", "change");
                            }
                        }
                    }
                }
            originVaccinesInStorage.put(originVaccineInStorageKey, originVaccineInStorage);
            }
            originStores.put(originStoreKey, originStore);
        }
        origin.put("stores", originStores);
//        HashMap<String,  HashMap<String, Object>> originVaccinesInStorage = (HashMap<String,  HashMap<String, Object>>) origin.get("vaccinesInStorage");
//        HashMap<String,  HashMap<String, Object>> vanVaccinesInStorage = (HashMap<String,  HashMap<String, Object>>) van.get("vaccinesInStorage");
//        System.out.println("originVaccinesInStorage: " + originVaccinesInStorage);
//        for (String keyO : originVaccinesInStorage.keySet()) {
//            HashMap<String, Object> originVaccineInStorage = originVaccinesInStorage.get(keyO);
//            String originVaccineID = (String) originVaccineInStorage.get("VaccineInStorage.vaccineID");
//            String originExpirationDate = (String) originVaccineInStorage.get("VaccineInStorage.expirationDate");
//            int originStockLevel = Integer.parseInt((String) originVaccineInStorage.get("VaccineInStorage.stockLevel"));
//            for (String keyV : vanVaccinesInStorage.keySet()) {
//                HashMap<String, Object> vanVaccineInStorage = vanVaccinesInStorage.get(keyV);
//                String vanVaccineID = (String) vanVaccineInStorage.get("VaccineInStorage.vaccineID");
//                String vanExpirationDate = (String) vanVaccineInStorage.get("VaccineInStorage.expirationDate");
//                int vanStockLevel = Integer.parseInt((String) vanVaccineInStorage.get("VaccineInStorage.stockLevel"));
//                if ((originVaccineID.equals(vanVaccineID)) && (originExpirationDate.equals(vanExpirationDate))) {
//                    if (originStockLevel == vanStockLevel) {
//                        originVaccineInStorage.remove(keyO);
//                    }
//                    else {
//                        int newStockLevel = originStockLevel - vanStockLevel;
//                        originVaccineInStorage.put("VaccineInStorage.stockLevel", String.valueOf(newStockLevel));
//                    }
//                }
//            }
//            originVaccinesInStorage.put(keyO, originVaccineInStorage);
//        }
//        origin.put("vaccinesInStorage", originVaccinesInStorage);
//        origin.put("VaccinesInStorage.change", "change");
        return origin;
    }

    private static HashMap<String, Object> addVaccineToDestination(HashMap<String, Object> destination, HashMap<String, Object> van) {
        HashMap<String, HashMap<String, Object>> vanStores = (HashMap<String, HashMap<String, Object>>) van.get("stores");
        HashMap<String, HashMap<String, Object>> destinationStores = (HashMap<String, HashMap<String, Object>>) destination.get("stores");
        for (String vanStoreKey : vanStores.keySet()) {
            HashMap<String, Object> vanStore = vanStores.get(vanStoreKey);
            HashMap<String, HashMap<String, String>> vaccinesInStorage = (HashMap<String, HashMap<String, String>>) vanStore.get("vaccinesInStorage");
            for (String vanVaccineInStorageKey : vaccinesInStorage.keySet()) {
                HashMap<String, String> vaccineInStorage = vaccinesInStorage.get(vanVaccineInStorageKey);
                int stockLevel = Integer.parseInt(vaccineInStorage.get("VaccineInStorage.stockLevel"));
                String vaccineID = vaccineInStorage.get("VaccineInStorage.vaccineID");
                String expirationDate = vaccineInStorage.get("VaccineInStorage.expirationDate");
                destinationStores = addToStores(expirationDate, destinationStores, stockLevel, vaccineID);
            }
        }
        destination.put("stores", destinationStores);
        return destination;
    }
//        HashMap<String,  HashMap<String, String>> vaccinesInStorage = (HashMap<String,  HashMap<String, String>>) store.get("vaccinesInStorage");
//        HashMap<String, HashMap<String, Object>> stores = (HashMap<String, HashMap<String, Object>>) destination.get("stores");
//        for (String key : vaccinesInStorage.keySet()) {
//            HashMap<String, String> vaccineInStorage = vaccinesInStorage.get(key);
//            int stockLevel = Integer.parseInt(vaccineInStorage.get("VaccineInStorage.stockLevel"));
//            String vaccineID = vaccineInStorage.get("VaccineInStorage.vaccineID");
//            String expirationDate = vaccineInStorage.get("VaccineInStorage.expirationDate");
//            stores = addToStores(expirationDate, stores, stockLevel, vaccineID);
//        }
//        destination.put("stores", stores);
//        destination.put("Store.change", "change");
//        return destination;
//    }

    // Need function to search through all DCs & VCs to see if fieldValue matches
    private static HashMap<String, Object> getMapFromFieldValue(HashMap<String, HashMap<String, Object>>[] maps, String fieldName, String fieldValue) {
        System.out.println("getMapFromFieldValue():");
        System.out.println("    maps: " + maps);
        System.out.println("    fieldName: " + fieldName);
        System.out.println("    fieldValue: " + fieldValue);
        for (HashMap<String, HashMap<String, Object>> subMaps : maps) {
            for (String key : subMaps.keySet()) {
                HashMap<String, Object> map = subMaps.get(key);
                if ((map.get(fieldName)).equals(fieldValue)) {
                    System.out.println("    map: " + map);
                    return map;
                }
            }
        }
        System.out.println("    returned null");
        return null;
    }

    // Should be modified to store vaccine in most suitable fridge by sorting lifespans and picking fridge with longest lifespan
    // Remember to keep original algorithm for comparison
    public static void updateFactoryStockLevels(Data data, int updateRate, int simulationSpeed) {
        HashMap<String, HashMap<String, Object>> factories = data.getFactories();
        for (String keyI : factories.keySet()) {
            HashMap<String, Object> factory = factories.get(keyI);

            if (isOpen(data, (HashMap<String, HashMap<String, Object>>) factory.get("openingTimes"))) {
                int vaccinesPerMin = Integer.parseInt((String) factory.get("Factory.vaccinesPerMin"));

                int totalVaccinesToAdd = (vaccinesPerMin * updateRate * simulationSpeed) / 60000;

//                HashMap<String, HashMap<String, Object>> stores = (HashMap<String, HashMap<String, Object>>) factory.get("stores");
//                for (String keyJ : stores.keySet()) {
//                    HashMap<String, Object> store = stores.get(keyJ);
//
//                    String vaccineID = (String) factory.get("Manufacturer.vaccineID");
//                    int availableCapacity = getAvailableCapacity(store);
//
//                    if ((availableCapacity > 0) && (totalVaccinesToAdd > 0)) {
//                        int vaccinesToAdd = Math.min(availableCapacity, totalVaccinesToAdd);
//                        totalVaccinesToAdd -= vaccinesToAdd;
//                        store = addToStore(data, vaccinesToAdd, vaccineID, store);
//                        stores.put(keyJ, store);
//                    }
//                }
                HashMap<String, HashMap<String, Object>> stores = (HashMap<String, HashMap<String, Object>>) factory.get("stores");
                String vaccineID = (String) factory.get("Manufacturer.vaccineID");
                stores = addToStores(data, stores, totalVaccinesToAdd, vaccineID);
                factory.put("stores", stores);
                factories.put(keyI, factory);
            }
        }
        data.setFactories(factories);
    }

    private static HashMap<String, HashMap<String, Object>> addToStores(Object expirationDate, HashMap<String,
     HashMap<String, Object>> stores,
     int totalVaccinesToAdd, String vaccineID) {
        for (String key : stores.keySet()) {
            HashMap<String, Object> store = stores.get(key);
            int availableCapacity = getAvailableCapacity(store);

            if ((availableCapacity > 0) && (totalVaccinesToAdd > 0)) {
                int vaccinesToAdd = Math.min(availableCapacity, totalVaccinesToAdd);
                totalVaccinesToAdd -= vaccinesToAdd;
                try {
                    String expirationDateString = (String) expirationDate;
                    store = addToStore(expirationDateString, vaccinesToAdd, vaccineID, store);
                }
                catch (Exception e) {
                    Data data = (Data) expirationDate;
                    store = addToStore(data, vaccinesToAdd, vaccineID, store);
                }
                stores.put(key, store);
            }
        }
        return stores;
    }

    public static void orderVaccines(Data data) {
        System.out.println("orderVaccines(): ");
        HashMap<String, HashMap<String, Object>> vaccinationCentres = data.getVaccinationCentres();
        HashMap<String, HashMap<String, Object>> distributionCentres = data.getDistributionCentres();
        HashMap<String, HashMap<String, Object>> vans = data.getVans();
        HashMap<String, HashMap<String, Object>> factories = data.getFactories();
        for (String key : vaccinationCentres.keySet()) {
            HashMap<String, Object> vaccinationCentre = vaccinationCentres.get(key);
            String neededVaccineID = getNeededVaccineID();
            int vaccinesNeeded = getVaccinationCentreOrder();

            HashMap<String, Object> orderMap = getBestVanAndOrigin(distributionCentres, vaccinationCentre, vans, vaccinesNeeded, neededVaccineID);
            if (orderMap != null) {
                System.out.println("    orderMap: " + orderMap);
                double distance = (double) orderMap.get("distance");

                HashMap<String, HashMap<String, Object>>[] locations = new HashMap[]{distributionCentres};
                HashMap<String, Object> distributionCentre = getMapFromFieldValue(locations, "StorageLocation.storageLocationID", (String) orderMap.get("originID"));
                HashMap<String, Object> van = vans.get((String) orderMap.get("vanID"));
                makeOrder(distributionCentre, vaccinationCentre, van, distance, vaccinesNeeded, neededVaccineID);
            }
            else {
                // Cannot make delivery
                System.out.println("cannot make delivery");
            }
        }
        for (String key : distributionCentres.keySet()) {
            HashMap<String, Object> distributionCentre = distributionCentres.get(key);
            int vaccinesNeeded = getVaccinationCentreOrder();
            String neededVaccineID = getNeededVaccineID();

            HashMap<String, Object> orderMap = getBestVanAndOrigin(factories, distributionCentre, vans, vaccinesNeeded, neededVaccineID);
            if (orderMap != null) {
                double distance = (double) orderMap.get("distance");
                HashMap<String, HashMap<String, Object>>[] locations = new HashMap[]{factories};
                HashMap<String, Object> factory = getMapFromFieldValue(locations, "StorageLocation.storageLocationID", (String) orderMap.get("originID"));
                HashMap<String, Object> van = vans.get((String) orderMap.get("vanID"));

                makeOrder(factory, distributionCentre, van, distance, vaccinesNeeded, neededVaccineID);
            }
            else {
                // Cannot make delivery
                System.out.println("cannot make delivery");
            }
        }
    }

    // YET TO BE IMPLEMENTED, does it need to be separate for vaccination centres and distribution centres?
    private static String getNeededVaccineID() {
        return "1";
    }

    // YET TO BE IMPLEMENTED
    private static int getVaccinationCentreOrder() {
        return 10;
    }

    private static  HashMap<String, Object> makeOrder(HashMap<String, Object> origin, HashMap<String, Object> destination, HashMap<String, Object> van,
     double distance, int vaccinesNeeded, String vaccineID) {
        HashMap<String, HashMap<String, Object>> stores = (HashMap<String, HashMap<String, Object>>) origin.get("stores");
        HashMap<String, HashMap<String, Object>> vaccinesInStorage = getStock(stores, vaccinesNeeded);
        van.put("vaccinesInStorage", vaccinesInStorage);
        van.put("Van.deliveryStage", "toOrigin");
        van.put("Van.originID", origin.get("StorageLocation.storageLocationID"));
        van.put("Van.destinationID", destination.get("StorageLocation.storageLocationID"));
        van.put("Van.remainingTime", String.valueOf(getTravelTime(distance)));
        van.put("Van.change", "change");
        return van;
    }

    // Currently only gets 1st suitable stock, but in future should get stock that will expire first
    private static HashMap<String, HashMap<String, Object>> getStock(HashMap<String, HashMap<String, Object>> stores, int vaccinesNeeded) {
        HashMap<String, HashMap<String, Object>> newVaccinesInStorage = new HashMap<>();
        for (String keyI : stores.keySet()) {
            HashMap<String, Object> store = stores.get(keyI);
            HashMap<String, HashMap<String, Object>> storeVaccinesInStorage = (HashMap<String, HashMap<String, Object>>) store.get("vaccinesInStorage");
            for (String keyJ : storeVaccinesInStorage.keySet()) {
                HashMap<String, Object> vaccineInStorage = storeVaccinesInStorage.get(keyJ);
                int stockLevel = Integer.parseInt((String) vaccineInStorage.get("VaccineInStorage.stockLevel"));
                if (stockLevel >= vaccinesNeeded) {
                    vaccineInStorage.put("VaccineInStorage.stockLevel", String.valueOf(vaccinesNeeded));
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

    private static HashMap<String, Object> getBestVanAndOrigin(HashMap<String, HashMap<String, Object>> origins, HashMap<String, Object> destination,
     HashMap<String, HashMap<String, Object>> vans, int vaccinesNeeded, String vaccineID) {
        System.out.println("getBestVanAndOrigin():");
        System.out.println("    origins: " + origins);
        System.out.println("    destination: " + destination);
        System.out.println("    vans: " + vans);
        System.out.println("    vaccinesNeeded: " + vaccinesNeeded);
        System.out.println("    vaccineID: " + vaccineID);
        HashMap<String, HashMap<String, Object>> availableOrigins = getAvailableOrigins(origins, vans, vaccinesNeeded, vaccineID);
        HashMap<String, HashMap<String, Object>> availableVans = getAvailableVans(vans);
        System.out.println("    availableOrigins: " + availableOrigins);
        System.out.println("    availableVans: " + availableVans);
        if ((availableOrigins.size() > 0) && (availableVans.size() > 0)) {
            double shortestDistance = 1000000;
            String bestVanID = "";
            String bestOriginID = "";
            for (String key : availableOrigins.keySet()) {
                HashMap<String, Object> origin = availableOrigins.get(key);
                HashMap map = shortestDistance(origin, destination, availableVans);
                double distance = (double) map.get("distance");
                System.out.println("    sistance: " + distance);
                if (distance < shortestDistance) {
                    shortestDistance = distance;
                    bestVanID = (String) map.get("vanID");
                    bestOriginID = (String) origin.get("StorageLocation.storageLocationID");
                }
            }
            HashMap map = new HashMap<>();
            map.put("distance", shortestDistance);
            map.put("vanID", bestVanID);
            map.put("originID", bestOriginID);
            System.out.println("    map: " + map);
            return map;
        }
        else {
            // Delivery cannot be made, so do something
            System.out.println("Not enough vaccines to perform delivery");
            return null;
        }
    }

    private static HashMap<String, HashMap<String, Object>> getAvailableOrigins(HashMap<String, HashMap<String, Object>> origins,
     HashMap<String, HashMap<String, Object>> vans, int vaccinesNeeded, String vaccineID) {
        HashMap<String, HashMap<String, Object>> availableOrigins = new HashMap<>();
        for (String key : origins.keySet()) {
            HashMap<String, Object>  origin = origins.get(key);
            int totalStock = getTotalStockInStorageLocation(origin, vans, vaccineID);
            if (totalStock >= vaccinesNeeded) {
                availableOrigins.put(key, origin);
            }
        }
        return availableOrigins;
    }

    private static int getAvailableCapacity(HashMap<String, Object> store) {
        int totalCapacity = Integer.parseInt((String) store.get("Store.capacity"));
        int usedCapacity = getTotalStockInStore(store);
        return (totalCapacity - usedCapacity);
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

    private static int getTotalStockInStorageLocation(HashMap<String, Object> storageLocation, HashMap<String, HashMap<String, Object>> vans) {
        return getTotalStockInStorageLocation(storageLocation, vans, null);
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

    private static int getTotalStockInStore(HashMap<String, Object> store) {
        return getTotalStockInStore(store, null);
    }

    private static HashMap<String, Object> addToStore(String expirationDate, int vaccinesToAdd, String vaccineID, HashMap<String, Object> store) {
        HashMap<String, HashMap<String, String>> vaccinesInStorage = (HashMap<String, HashMap<String, String>>) store.get("vaccinesInStorage");
        HashMap<String, String> vaccineInStorage;
        for (String key : vaccinesInStorage.keySet()) {
            vaccineInStorage = vaccinesInStorage.get(key);
            String existingExpirationDate = vaccineInStorage.get("VaccineInStorage.expirationDate").substring(0, 10);
            String existingVaccineID = vaccineInStorage.get("VaccineInStorage.vaccineID");
            if ((existingExpirationDate.equals(expirationDate)) && (existingVaccineID.equals(vaccineID))) {
                int existingStockLevel = Integer.parseInt(vaccineInStorage.get("VaccineInStorage.stockLevel"));
                vaccineInStorage.put("VaccineInStorage.stockLevel", Integer.toString(existingStockLevel + vaccinesToAdd));
                return addVaccineInStorage(store, key, vaccinesInStorage, vaccineInStorage);
            }
        }
        vaccineInStorage = new HashMap<>();
        vaccineInStorage.put("VaccineInStorage.vaccineID", vaccineID);
        vaccineInStorage.put("VaccineInStorage.storeID", (String) store.get("Store.storeID"));
        vaccineInStorage.put("VaccineInStorage.stockLevel", Integer.toString(vaccinesToAdd));
        vaccineInStorage.put("VaccineInStorage.expirationDate", expirationDate);
        return addVaccineInStorage(store, "newID", vaccinesInStorage, vaccineInStorage);
    }

    private static HashMap<String, Object> addToStore(Data data, int vaccinesToAdd, String vaccineID, HashMap<String, Object> store) {
        String expirationDate = getExpirationDate(data, vaccineID, Integer.parseInt((String) store.get("Store.temperature")));
        return addToStore(expirationDate, vaccinesToAdd, vaccineID, store);
    }

    private static HashMap<String, Object> addVaccineInStorage(HashMap<String, Object> store, String key,
     HashMap<String, HashMap<String, String>> vaccinesInStorage, HashMap<String, String> vaccineInStorage) {
        vaccineInStorage.put("VaccineInStorage.change", "change");
        vaccinesInStorage.put(key, vaccineInStorage);
        store.put("vaccinesInStorage", vaccinesInStorage);
        return store;
    }

    private static String getExpirationDate(Data data, String vaccineID, int storageTemperature) {
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

    private static boolean isOpen(Data data, HashMap<String, HashMap<String, Object>> openingTimes) {
        HashMap<String, Object> openingTime = getOpeningTime(data, openingTimes);
        LocalTime startTime = getLocalTime((String) openingTime.get("OpeningTime.startTime"));
        LocalTime endTime = getLocalTime((String) openingTime.get("OpeningTime.endTime"));

        LocalTime currentTime = data.getCurrentTime();
        if ((currentTime.isAfter(startTime)) && (currentTime.isBefore(endTime))) {
            return true;
        }
        else {
            return false;
        }
    }

    private static HashMap<String, Object> getOpeningTime(Data data, HashMap<String, HashMap<String, Object>> openingTimes) {
        for (String key : openingTimes.keySet()) {
            LocalDate currentDate = data.getCurrentDate();
            String currentDay = currentDate.getDayOfWeek().toString().toLowerCase();
            String openingTimeDay = ((String) openingTimes.get(key).get("OpeningTime.day")).toLowerCase();
            if (currentDay.equals(openingTimeDay)) {
                return openingTimes.get(key);
            }
        }
        return null;
    }

    private static LocalTime getLocalTime(String time) {
        String[] stringTimeValues = time.split(":");
        int[] timeValues = new int[stringTimeValues.length];
        for (int i = 0; i < timeValues.length; i++) {
            timeValues[i] = Integer.parseInt(stringTimeValues[i]);
        }
        return LocalTime.of(timeValues[0], timeValues[1], timeValues[2]);
    }
}