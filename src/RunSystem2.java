import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.HashMap;

public class RunSystem2 {

    private Data data;
    private LocalDate currentDate;
    private LocalTime currentTime;
    private int updateRate;
    private int simulationSpeed;

    public void start(Data data, int updateRate, int simulationSpeed) {
        this.data = data;
        this.updateRate = updateRate;
        this.simulationSpeed = simulationSpeed;

        try {
            data.read();
        }
        catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void run() {
        updateDate();
        updateFactoryStockLevels();

        System.out.println(data.getVaccines());
        try {
            data.write(currentDate);
        }
        catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void updateDate() {
        currentDate = LocalDate.now();
        currentTime = LocalTime.of(11, 0);
    }

    // Should be modified to store vaccine in most suitable fridge by sorting lifespans and picking fridge with longest lifespan
    // Remember to keep original algorithm for comparison
    private void updateFactoryStockLevels() {
        HashMap<String, HashMap<String, Object>> factories = data.getFactories();
        for (String keyI : factories.keySet()) {
            HashMap<String, Object> factory = factories.get(keyI);

            if (isOpen((HashMap<String, HashMap<String, Object>>) factory.get("openingTime"))) {
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
                        store = addToStore(vaccinesToAdd, vaccineID, store);
                        stores.put(keyJ, store);
                    }
                }
                factory.put("stores", stores);
                factories.put(keyI, factory);
            }
        }
        data.setFactories(factories);
    }

    private int availableCapacity(HashMap<String, Object> store) {
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

    private HashMap<String, Object> addToStore(int vaccinesToAdd, int vaccineID, HashMap<String, Object> store) {

        String expirationDate = getExpirationDate(vaccineID, Integer.parseInt((String) store.get("Store.temperature")));

        HashMap<String, HashMap<String, String>> vaccinesInStorage = (HashMap<String, HashMap<String, String>>) store.get("vaccineInStorage");
        HashMap<String, String> vaccineInStorage;
        boolean addedStocks = false;
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

    private HashMap<String, Object> addVaccineInStorage(HashMap<String, Object> store, String key,
     HashMap<String, HashMap<String, String>> vaccinesInStorage, HashMap<String, String> vaccineInStorage) {
        vaccineInStorage.put("VaccineInStorage.change", "change");
        vaccinesInStorage.put(key, vaccineInStorage);
        store.put("vaccineInStorage", vaccinesInStorage);
        return store;
    }

    private String getExpirationDate(int vaccineID, int storageTemperature) {
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
        return LocalDate.from(currentDate.plusDays(lifespanValue)).toString();
    }

    private boolean isOpen(HashMap<String, HashMap<String, Object>> openingTimes) {
        HashMap<String, Object> openingTime = getOpeningTime(openingTimes);
        LocalTime startTime = getLocalTime((String) openingTime.get("OpeningTime.startTime"));
        LocalTime endTime = getLocalTime((String) openingTime.get("OpeningTime.endTime"));

        if ((currentTime.isAfter(startTime)) && (currentTime.isBefore(endTime))) {
            return true;
        }
        else {
            return false;
        }
    }

    private HashMap<String, Object> getOpeningTime(HashMap<String, HashMap<String, Object>> openingTimes) {
        for (String key : openingTimes.keySet()) {
            String currentDay = currentDate.getDayOfWeek().toString().toLowerCase();
            String openingTimeDay = ((String) openingTimes.get(key).get("OpeningTime.day")).toLowerCase();
            if (currentDay.equals(openingTimeDay)) {
                return openingTimes.get(key);
            }
        }
        return null;
    }

    private LocalTime getLocalTime(String time) {
        String[] stringTimeValues = time.split(":");
        int[] timeValues = new int[stringTimeValues.length];
        for (int i = 0; i < timeValues.length; i++) {
            timeValues[i] = Integer.parseInt(stringTimeValues[i]);
        }
        return LocalTime.of(timeValues[0], timeValues[1], timeValues[2]);
    }
}