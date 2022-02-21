import java.time.LocalDate;
import java.time.LocalTime;
import java.util.HashMap;

public class StockLevel {

    // Applies the Haversine formula to calculate the shortest distance over the earth's surface
    // Avoid use of cosine formula as it is unreliable for small distances (https://www.themathdoctors.org/distances-on-earth-2-the-haversine-formula/
    public static double getDistance(double longitudeA, double latitudeA, double longitudeB, double latitudeB) {
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
    public static double getDistance(HashMap<String, Object> locationA, HashMap<String, Object> locationB) {
//    public static double getDistance() {
        double longitudeA = Double.parseDouble((String) locationA.get("Location.longitude"));
        double latitudeA = Double.parseDouble((String) locationA.get("Location.latitude"));
        double longitudeB = Double.parseDouble((String) locationB.get("Location.longitude"));
        double latitudeB = Double.parseDouble((String) locationB.get("Location.latitude"));
        return getDistance(longitudeA, latitudeA, longitudeB, latitudeB);
    }

    private static double degreesToRadians(double degrees) {
        return degrees * (Math.PI / 180);
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

                HashMap<String, HashMap<String, Object>> stores = (HashMap<String, HashMap<String, Object>>) factory.get("stores");
                for (String keyJ : stores.keySet()) {
                    HashMap<String, Object> store = stores.get(keyJ);

                    int vaccineID = Integer.parseInt((String) factory.get("Manufacturer.vaccineID"));
                    int availableCapacity = availableCapacity(store);

                    if ((availableCapacity > 0) && (totalVaccinesToAdd > 0)) {
                        int vaccinesToAdd = Math.min(availableCapacity, totalVaccinesToAdd);
                        totalVaccinesToAdd -= vaccinesToAdd;
                        store = addToStore(data, vaccinesToAdd, vaccineID, store);
                        stores.put(keyJ, store);
                    }
                }
                factory.put("stores", stores);
                factories.put(keyI, factory);
            }
        }
        data.setFactories(factories);
    }

    private static int availableCapacity(HashMap<String, Object> store) {
        int usedCapacity = 0;
        int totalCapacity = Integer.parseInt((String) store.get("Store.capacity"));
        HashMap<String, HashMap<String, Object>> vaccinesInStorage = (HashMap<String, HashMap<String, Object>>) store.get("vaccineInStorage");
        if (vaccinesInStorage != null) {
            for (String key : vaccinesInStorage.keySet()) {
                usedCapacity += Integer.parseInt((String) vaccinesInStorage.get(key).get("VaccineInStorage.stockLevel"));
            }
        }
        return (totalCapacity - usedCapacity);
    }

    private static HashMap<String, Object> addToStore(Data data, int vaccinesToAdd, int vaccineID, HashMap<String, Object> store) {

        String expirationDate = getExpirationDate(data, vaccineID, Integer.parseInt((String) store.get("Store.temperature")));

        HashMap<String, HashMap<String, String>> vaccinesInStorage = (HashMap<String, HashMap<String, String>>) store.get("vaccineInStorage");
        HashMap<String, String> vaccineInStorage;
        for (String key : vaccinesInStorage.keySet()) {
            vaccineInStorage = vaccinesInStorage.get(key);
            String existingExpirationDate = vaccineInStorage.get("VaccineInStorage.expirationDate").substring(0, 10);
            int existingVaccineID = Integer.parseInt(vaccineInStorage.get("VaccineInStorage.vaccineID"));
            if ((existingExpirationDate.equals(expirationDate)) && (existingVaccineID == vaccineID)) {
                int existingStockLevel = Integer.parseInt(vaccineInStorage.get("VaccineInStorage.stockLevel"));
                vaccineInStorage.put("VaccineInStorage.stockLevel", Integer.toString(existingStockLevel + vaccinesToAdd));
                return addVaccineInStorage(store, key, vaccinesInStorage, vaccineInStorage);
            }
        }
        vaccineInStorage = new HashMap<>();
        vaccineInStorage.put("VaccineInStorage.vaccineID", Integer.toString(vaccineID));
        vaccineInStorage.put("VaccineInStorage.storeID", (String) store.get("Store.storeID"));
        vaccineInStorage.put("VaccineInStorage.stockLevel", Integer.toString(vaccinesToAdd));
        vaccineInStorage.put("VaccineInStorage.expirationDate", expirationDate);
        return addVaccineInStorage(store, "newID", vaccinesInStorage, vaccineInStorage);
    }

    private static HashMap<String, Object> addVaccineInStorage(HashMap<String, Object> store, String key,
     HashMap<String, HashMap<String, String>> vaccinesInStorage, HashMap<String, String> vaccineInStorage) {
        vaccineInStorage.put("VaccineInStorage.change", "change");
        vaccinesInStorage.put(key, vaccineInStorage);
        store.put("vaccineInStorage", vaccinesInStorage);
        return store;
    }

    private static String getExpirationDate(Data data, int vaccineID, int storageTemperature) {
        HashMap<String, Object> vaccine = data.getVaccines().get(Integer.toString(vaccineID));
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
