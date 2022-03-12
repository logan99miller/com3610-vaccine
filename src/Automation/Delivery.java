package Automation;

import Core.ActivityLog;
import Core.Data;
import Core.DataUtils;

import java.util.HashMap;

public class Delivery {

    public static void update(ActivityLog activityLog, Data data, int updateRate, int simulationSpeed) {
        HashMap<String, HashMap<String, Object>> vans = data.getVans();

        vans = updateRemainingTime(vans, updateRate, simulationSpeed);

        for (String key : vans.keySet()) {
            HashMap<String, Object> van = vans.get(key);
            van = updateStockAndDeliveryStage(activityLog, data, updateRate, van);
            vans.put(key, van);
        }
        data.setVans(vans);
    }

    private static HashMap<String, Object> updateStockAndDeliveryStage(ActivityLog activityLog, Data data, int updateRate, HashMap<String, Object> van) {
        int remainingTime = Integer.parseInt((String) van.get("Van.remainingTime"));

        HashMap<String, Object> destination = getDestination(data, van);

        if (remainingTime < (updateRate / 1000)) {
            String deliveryStage = (String) van.get("Van.deliveryStage");

            if (deliveryStage.equals("toOrigin")) {
                System.out.println("Van before reached origin: " + van); // Has 2 vaccinesInStorage, should only have 1
                HashMap<String, Object> origin = getOrigin(data, van);
                origin = removeVaccinesFromOrigin(origin, van);
                van = vanReachedOrigin(activityLog, van, origin, destination);
                System.out.println("Van once reached origin: " + van); // Has 2 vaccinesInStorage, should only have 1
            }
            else if (deliveryStage.equals("toDestination")) {
                destination = addVaccinesToDestination(data, destination, van);
                van = vanReachedDestination(activityLog, van, destination);

            }
            van.put("Van.change", "change");
        }
        return van;
    }

    private static HashMap<String, Object> vanReachedOrigin(ActivityLog activityLog, HashMap<String, Object> van, HashMap<String, Object> origin,
    HashMap<String, Object> destination) {

        String vanID = (String) van.get("Van.ID");
        activityLog.add(vanID + " reached it's origin");

        String longitude = (String) origin.get("Location.longitude");
        String latitude = (String) origin.get("Location.latitude");
        int remainingTime = Distance.getTravelTime(origin, destination);
        van.put("Van.deliveryStage", "toDestination");
        van.put("Location.longitude", longitude);
        van.put("Location.latitude", latitude);
        van.put("Van.remainingTime", String.valueOf(remainingTime));
        return van;
    }

    private static HashMap<String, Object> vanReachedDestination(ActivityLog activityLog, HashMap<String, Object> van, HashMap<String, Object> destination) {

        String vanID = (String) van.get("Van.ID");
        activityLog.add(vanID + " reached it's destination");

        HashMap<String, HashMap<String, Object>> stores = (HashMap<String, HashMap<String, Object>>) van.get("stores");
        for (String keyI : stores.keySet()) {
            HashMap<String, Object> store = stores.get(keyI);
            HashMap<String, HashMap<String, Object>> vaccinesInStorage = (HashMap<String, HashMap<String, Object>>) store.get("vaccinesInStorage");
            for (String keyJ : vaccinesInStorage.keySet()) {
                HashMap<String, Object> vaccineInStorage = vaccinesInStorage.get(keyJ);
                vaccineInStorage.put("VaccineInStorage.delete", "delete");
                vaccinesInStorage.put(keyJ, vaccineInStorage);
            }
            store.put("vaccinesInStorage", vaccinesInStorage);
            stores.put(keyI, store);
        }
        van.put("stores", stores);

        String longitude = (String) destination.get("Location.longitude");
        String latitude = (String) destination.get("Location.latitude");
        van.put("Van.deliveryStage", "waiting");
        van.put("Location.longitude", longitude);
        van.put("Location.latitude", latitude);
        return van;
    }

    private static HashMap<String, Object> getOrigin(Data data, HashMap<String, Object> van) {
        String originID = (String) van.get("Van.originID");
        HashMap<String, HashMap<String, Object>> origins = new HashMap<>();
        origins = DataUtils.mergeMaps(origins, data.getFactories(), "f");
        origins = DataUtils.mergeMaps(origins, data.getDistributionCentres(), "d");
        HashMap<String, Object> origin = DataUtils.findMap(origins, "Location.locationID", originID);
        return origin;
    }

    private static HashMap<String, Object> getDestination(Data data, HashMap<String, Object> van) {
        String originID = (String) van.get("Van.destinationID");
        HashMap<String, HashMap<String, Object>> destinations = new HashMap<>();
        destinations = DataUtils.mergeMaps(destinations, data.getVaccinationCentres(), "v");
        destinations = DataUtils.mergeMaps(destinations, data.getDistributionCentres(), "d");
        HashMap<String, Object> destination = DataUtils.findMap(destinations, "Location.locationID", originID);
        return destination;
    }

    private static HashMap<String, HashMap<String, Object>> updateRemainingTime(HashMap<String, HashMap<String, Object>> vans,
    int updateRate, int simulationSpeed) {
        for (String key : vans.keySet()) {
            HashMap<String, Object> van = vans.get(key);

            int remainingTime = Integer.parseInt((String) van.get("Van.remainingTime"));
            String deliveryStage = (String) van.get("Van.deliveryStage");
            if ((remainingTime > 0) && ((deliveryStage.equals("toOrigin")) || (deliveryStage.equals("toDestination")))) {
                remainingTime -= ((updateRate / 1000) * simulationSpeed);
                van.put("Van.remainingTime", String.valueOf(remainingTime));
                van.put("Van.change", "change");
                vans.put(key, van);
            }
        }
        return vans;
    }

    private static HashMap<String, Object> addVaccinesToDestination(Data data, HashMap<String, Object> destination, HashMap<String, Object> van) {
        HashMap<String, HashMap<String, Object>> vanStores = (HashMap<String, HashMap<String, Object>>) van.get("stores");
        HashMap<String, HashMap<String, Object>> destinationStores = (HashMap<String, HashMap<String, Object>>) destination.get("stores");
        for (String keyI : vanStores.keySet()) {
            HashMap<String, Object> vanStore = vanStores.get(keyI);
            HashMap<String, HashMap<String, Object>> vaccinesInStorage = (HashMap<String, HashMap<String, Object>>) vanStore.get("vaccinesInStorage");
            for (String keyJ : vaccinesInStorage.keySet()) {
                HashMap<String, Object> vaccineInStorage = vaccinesInStorage.get(keyJ);
                int amount = Integer.parseInt((String) vaccineInStorage.get("VaccineInStorage.stockLevel"));
                String vaccineID = (String) vaccineInStorage.get("VaccineInStorage.vaccineID");
                String creationDate = (String) vaccineInStorage.get("VaccineInStorage.creationDate");
                destinationStores = StorageLocation.addToStores(data, destinationStores, amount, vaccineID, creationDate);
            }
        }
        destination.put("stores", destinationStores);
        return destination;
    }

    private static HashMap<String, Object> removeVaccinesFromOrigin(HashMap<String, Object> origin, HashMap<String, Object> van) {
        HashMap<String,  HashMap<String, Object>> originStores = (HashMap<String,  HashMap<String, Object>>) origin.get("stores");
        HashMap<String,  HashMap<String, Object>> vanStores = (HashMap<String,  HashMap<String, Object>>) van.get("stores");
        for (String keyI : originStores.keySet()) {
            HashMap<String, Object> originStore = originStores.get(keyI);
            HashMap<String, Object> originVaccinesInStorage = (HashMap<String, Object>) originStore.get("vaccinesInStorage");
            for (String keyJ : vanStores.keySet()) {
                HashMap<String, HashMap<String, String>> vanVaccinesInStorage = (HashMap<String, HashMap<String, String>>) vanStores.get(keyJ).get("vaccinesInStorage");
                for (String keyK : originVaccinesInStorage.keySet()) {
                    HashMap<String, String> originVaccineInStorage = (HashMap<String, String>) originVaccinesInStorage.get(keyK);
                    for (String keyL : vanVaccinesInStorage.keySet()) {
                        HashMap<String, String> vanVaccineInStorage = vanVaccinesInStorage.get(keyL);
                        originVaccineInStorage = removeVaccineInStorage(originVaccineInStorage, vanVaccineInStorage);
                        originVaccinesInStorage.put(keyK, originVaccineInStorage);
                    }
                }
                originStore.put("vaccinesInStorage", originVaccinesInStorage);
            }
            originStores.put(keyI, originStore);
        }
        origin.put("stores", originStores);
        return origin;
    }

    private static HashMap<String, String> removeVaccineInStorage(HashMap<String, String> vaccineInStorageA, HashMap<String, String> vaccineInStorageB) {
        String vaccineIDA = vaccineInStorageA.get("VaccineInStorage.vaccineID");
        String vaccineExpirationDateA = vaccineInStorageA.get("VaccineInStorage.expirationDate");

        String vaccineIDB = vaccineInStorageB.get("VaccineInStorage.vaccineID");
        String vaccineExpirationDateB= vaccineInStorageB.get("VaccineInStorage.expirationDate");

        if ((vaccineIDA.equals(vaccineIDB)) && (vaccineExpirationDateA.equals(vaccineExpirationDateB))) {
            int stockLevelA = Integer.parseInt(vaccineInStorageA.get("VaccineInStorage.stockLevel"));
            int stockLevelB = Integer.parseInt(vaccineInStorageB.get("VaccineInStorage.stockLevel"));
            int stockLevel = stockLevelA + stockLevelB;

            vaccineInStorageA.put("VaccineInStorage.stockLevel", String.valueOf(stockLevel));
            vaccineInStorageA.put("VaccineInStorage.change", "change");
        }
        return vaccineInStorageA;
    }
}
