import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.*;

public class RunSystem extends Thread {

    private VaccineSystem vaccineSystem;
    private HashMap<String, String> locationMap, storageLocationMap, medicalConditionMap;
    private HashMap<String, HashMap<String, Object>> vaccines, factories, transporterLocations, distributionCentres, vaccinationCentres;
    private LocalDate currentDate;
    private LocalTime currentTime;

    public void start(VaccineSystem vaccineSystem) {
        this.vaccineSystem = vaccineSystem;
        updateDate();
        setMaps();
    }

    public void run() {
        try {
            // Do these values also need to update HashMaps? - Probably not
            updateDate();
            removeExpiredStock();
            updateData();
            updateFactoryStockLevels();
            orderDistributionCentreStock();
            // Functions to add:
            // - Remove expired stocks
            // - orderVaccinationCentreStock()
            //      - best transport location option
            // - create bookings
            // - fufill bookings (assume average no show rate
            //
        }
        catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void setMaps() {
        locationMap = new HashMap<>();
        locationMap.put("foreignKey", "locationID");
        locationMap.put("foreignTableName", "Location");

        storageLocationMap = new HashMap<>();
        storageLocationMap.put("foreignKey", "storageLocationID");
        storageLocationMap.put("foreignTableName", "StorageLocation");

        medicalConditionMap = new HashMap<>();
        medicalConditionMap.put("foreignKey", "medicalConditionID");
        medicalConditionMap.put("foreignTableName", "MedicalCondition");
    }

    private void updateData() throws SQLException {
        vaccines = getVaccines();
        factories = getFactories();
        transporterLocations = getTransporterLocations();
        distributionCentres = getDistributionCentres();
        vaccinationCentres = getVaccinationCentres();
    }

    private void updateDate() {
        currentDate = LocalDate.now();
        currentTime = LocalTime.of(11, 0);
    }

    private void removeExpiredStock() throws SQLException {
        HashMap<String, HashMap<String, Object>> vaccinesInStorage = getVaccinesInStorage();

        for (String key : vaccinesInStorage.keySet()) {
            LocalDate expirationDate = getLocalDate((String) vaccinesInStorage.get(key).get("VaccineInStorage.expirationDate"));
            if (expirationDate.isBefore(currentDate)) {
                String ID = (String) vaccinesInStorage.get(key).get("VaccineInStorage.vaccineInStorageID");
                delete("vaccineInStorageID", ID, "VaccineInStorage");
            }
        }
    }

    private LocalDate getLocalDate(String databaseDate) {
        String[] dateValues = databaseDate.split("-");
        LocalDate localDate = LocalDate.of(
                Integer.parseInt(dateValues[0]),
                Integer.parseInt(dateValues[1]),
                Integer.parseInt(dateValues[2].substring(0, 2)));
        return localDate;
    }

    // Should be modified as a modified trend projection algorithm rather than threshold algorithm
    // Remember to keep original algorithm for comparison
    private void orderDistributionCentreStock() throws SQLException {
        System.out.println(distributionCentres);
        for (String keyI : distributionCentres.keySet()) {
            HashMap<String, Object> distributionCentre = distributionCentres.get(keyI);

            if (isOpen((HashMap<String, HashMap<String, Object>>) distributionCentre.get("openingTimes"))) {
                int stock = getTotalStock((HashMap<String, Object>) distributionCentre.get("stores"));
            }
        }
    }

    private int getTotalStock(HashMap<String, Object> stores) {
        return 0;
    }

    // Should be modified to store vaccine in most suitable fridge by sorting lifespans and picking fridge with longest lifespan
    // Remember to keep original algorithm for comparison
    private void updateFactoryStockLevels() throws SQLException {
        for (String keyI : factories.keySet()) {
            HashMap<String, Object> factory = factories.get(keyI);

            if (isOpen((HashMap<String, HashMap<String, Object>>) factory.get("openingTimes"))) {
                int vaccinesPerMin = Integer.parseInt((String) factory.get("Factory.vaccinesPerMin"));

                int updateRate = vaccineSystem.getUpdateRate();
                int simulationSpeed = vaccineSystem.getSimulationSpeed();
                int totalVaccinesToAdd = (vaccinesPerMin * updateRate * simulationSpeed) / 60000;

                HashMap<String, HashMap<String, Object>> stores = (HashMap<String, HashMap<String, Object>>) factory.get("stores");
                for (String keyJ : stores.keySet()) {

                    int vaccineID = Integer.parseInt((String) factory.get("Manufacturer.vaccineID"));
                    int availableCapacity = availableCapacity(stores.get(keyJ));

                    if ((availableCapacity > 0) && (totalVaccinesToAdd > 0)) {
                        totalVaccinesToAdd = addToStore(availableCapacity, totalVaccinesToAdd, vaccineID, stores.get(keyJ));
                    }
                }
            }
        }
    }
    
    private int addToStore(int availableCapacity, int totalVaccinesToAdd, int vaccineID, HashMap<String, Object> store) throws SQLException {
        int vaccinesToAdd = Math.min(availableCapacity, totalVaccinesToAdd);

        int storeID = Integer.parseInt((String) store.get("Store.storeID"));
        String expirationDate = getExpirationDate(vaccineID, Integer.parseInt((String) store.get("Store.temperature")));

        String[] columnNames = {"vaccineID", "storeID", "stockLevel", "expirationDate"};
        String tableName = "VaccineInStorage";
        String where = "(vaccineID = " + vaccineID + ") AND (storeID = " + storeID + ") AND (expirationDate = '" + expirationDate + "')";

        HashMap<String, HashMap<String, Object>> matchingExistingStock = vaccineSystem.executeSelect4(columnNames, tableName, null, where);

        if (matchingExistingStock.size() > 0) {
            String existingStockKey = matchingExistingStock.keySet().iterator().next();
            vaccinesToAdd += Integer.parseInt((String) matchingExistingStock.get(existingStockKey).get("stockLevel"));

        }
        Object[] values = {vaccineID, store.get("Store.storeID"), vaccinesToAdd, expirationDate};
        
        if (matchingExistingStock.size() > 0) {
            update(columnNames, values, tableName, where);
        }
        else {
            insert(columnNames, values, tableName);
        }
        
        return (totalVaccinesToAdd - vaccinesToAdd);
    }

    private String getExpirationDate(int vaccineID, int storageTemperature) {
        HashMap<String, Object> vaccine = vaccines.get(Integer.toString(vaccineID));
        HashMap<String, HashMap<String, Object>> lifespans = (HashMap<String, HashMap<String, Object>>) vaccine.get("lifespans");
        int lifespanValue = 0;
        for (String key : lifespans.keySet()) {
            HashMap<String, Object> lifespan = lifespans.get(key);
            int lowestTemperature = Integer.parseInt((String) lifespan.get("lowestTemperature"));
            int highestTemperature = Integer.parseInt((String) lifespan.get("highestTemperature"));
            if ((lowestTemperature < storageTemperature) && (highestTemperature > storageTemperature)) {
                lifespanValue = Integer.parseInt((String) lifespan.get("lifespan"));
                break;
            }
        }
        return LocalDate.from(currentDate.plusDays(lifespanValue)).toString();
    }

    private void delete(String IDFieldName, String ID, String tableName) throws SQLException {
        System.out.println("DELETE FROM " + tableName + " WHERE " + IDFieldName + " = " + ID);
        vaccineSystem.executeUpdate("DELETE FROM " + tableName + " WHERE " + IDFieldName + " = " + ID);
    }

    private void insert(String[] columnNames, Object[] values, String tableName) throws SQLException {
        String columnNamesText = getColumnNamesText(columnNames);
        String valuesText = getValuesText(values);
        vaccineSystem.executeUpdate("INSERT INTO " + tableName + " (" + columnNamesText + ") VALUES (" + valuesText + ");");
    }

    private void update(String[] columnNames, Object[] values, String tableName, String where) throws SQLException {
        String statementText = "UPDATE " + tableName + " SET " + getOnText(columnNames, values) + " WHERE " + where;
        vaccineSystem.executeUpdate(statementText);
    }

    private String getOnText(String[] columnNames, Object[] values) {
        String setText = columnNames[0] + " = ";
        setText = addToValues(setText, values[0], "");
        for (int i = 1; i <  columnNames.length; i++) {
            setText += addToSetText(columnNames[i], values[i]);
        }
        return setText;
    }

    private String addToSetText(String columnName, Object value) {
        String text = "";
        return ", " + columnName + addToValues(text, value, " = ") + " ";
    }

    private String getColumnNamesText(String[] columnNames) {
        String columnNamesText = columnNames[0];
        for (int i = 1; i < columnNames.length; i++) {
            columnNamesText += ", " + columnNames[i];
        }
        return columnNamesText;
    }

    private String getValuesText(Object[] values) {
        String valuesText = "";
        valuesText = addToValues(valuesText, values[0], "");
        for (int i = 1; i < values.length; i++) {
            valuesText = addToValues(valuesText, values[i], ", ");
        }
        return valuesText;
    }

    private String addToValues(String valueText, Object value, String separator) {
        try {
            Float.parseFloat(value.toString());
            valueText += separator + value;
        }
        catch (NumberFormatException e) {
            valueText += separator + "'" + value + "'";
        }
        return valueText;
    }

    // Can be used for which person should be booked first and sorting lifespans for best fridge to put vaccines in
    private ArrayList<Integer> sortMaps(HashMap<String, HashMap<String, Object>> map, String sortKey) {
        ArrayList<Integer> IDs = new ArrayList<>();
        ArrayList<Integer> values = new ArrayList<>();

        for (String key : map.keySet()) {
            IDs.add(Integer.parseInt(key));
            values.add(Integer.parseInt((String) map.get(key).get(sortKey)));
        }

        // Replace with better sorting algorithm
        for (int i = 0; i < values.size() - 1; i++) {
            for (int j = 0; j < values.size() - i - 1; j++) {
                if (values.get(j) > values.get(j + 1)) {
                    int tempID = IDs.get(j);
                    int tempValue = values.get(j);
                    IDs.set(j, IDs.get(j + 1));
                    IDs.set(j + 1, tempID);
                    values.set(j, values.get(j + 1));
                    values.set(j + 1, tempValue);
                }
            }
        }
        return IDs;
    }

    private int availableCapacity(HashMap<String, Object> store) {
        int usedCapacity = 0;
        int totalCapacity = Integer.parseInt((String) store.get("Store.capacity"));
        HashMap<String, HashMap<String, Object>> vaccinesInStorage = (HashMap<String, HashMap<String, Object>>) store.get("vaccineInStorage");
        if (vaccinesInStorage != null) {
            for (String key : vaccinesInStorage.keySet()) {
                usedCapacity += Integer.parseInt((String) vaccinesInStorage.get(key).get("Store.stockLevel"));
            }
        }
        return (totalCapacity - usedCapacity);
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

    private LocalTime getLocalTime(String time) {
        String[] stringTimeValues = time.split(":");
        int[] timeValues = new int[stringTimeValues.length];
        for (int i = 0; i < timeValues.length; i++) {
            timeValues[i] = Integer.parseInt(stringTimeValues[i]);
        }
        return LocalTime.of(timeValues[0], timeValues[1], timeValues[2]);
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

    private HashMap<String, HashMap<String, Object>> getVaccines() throws SQLException {
        String[] columnNames = {"Vaccine.vaccineID", "Vaccine.name", "Vaccine.dosesNeeded", "Vaccine.daysBetweenDoses"};

        HashMap<String, HashMap<String, Object>> vaccines = vaccineSystem.executeSelect4(columnNames, "vaccine", null, null);

        for (String key : vaccines.keySet()) {
            HashMap<String, HashMap<String, Object>> lifespans = getVaccineLifespans((String) vaccines.get(key).get("Vaccine.vaccineID"));
            HashMap<String, HashMap<String, Object>> exemptions = getVaccineExemptionFromVaccineID((String) vaccines.get(key).get("Vaccine.vaccineID"));
            vaccines.get(key).put("lifespans", lifespans);
            vaccines.get(key).put("exemptions", exemptions);
        }

        return vaccines;
    }

    private HashMap<String, HashMap<String, Object>> getVaccineLifespans(String vaccineID) throws SQLException {
        String[] columnNames = {"lifespan", "lowestTemperature", "highestTemperature"};
        return vaccineSystem.executeSelect4(columnNames, "VaccineLifespan", null, "vaccineID = " + vaccineID);
    }

    private HashMap<String, HashMap<String, Object>> getVaccineExemptionFromVaccineID(String vaccineID) throws SQLException {
        String[] columnNames = {"VaccineExemption.medicalConditionID", "MedicalCondition.name", "MedicalCondition.vulnerabilityLevel"};

        medicalConditionMap.put("localTableName", "VaccineExemption");

        HashMap[] innerJoins = new HashMap[] {medicalConditionMap};
        return vaccineSystem.executeSelect4(columnNames, "VaccineExemption", innerJoins, "vaccineID = " + vaccineID);
    }

    private HashMap<String, HashMap<String, Object>> getFactories() throws SQLException {
        String[] columnNames = {"Factory.factoryID", "Factory.manufacturerID", "Manufacturer.name", "Manufacturer.vaccineID", "Factory.vaccinesPerMin"};

        HashMap<Object, Object> manufacturerMap = new HashMap<>();
        manufacturerMap.put("foreignKey", "manufacturerID");
        manufacturerMap.put("foreignTableName", "Manufacturer");
        manufacturerMap.put("localTableName", "Factory");

        storageLocationMap.put("localTableName", "Factory");

        HashMap[] innerJoins = new HashMap[] {manufacturerMap};

        return getStorageLocations(columnNames, "Factory", innerJoins);
    }

    private HashMap<String, HashMap<String, Object>> getTransporterLocations() throws SQLException {
        String[] columnNames = {"TransporterLocation.transporterLocationID", "TransporterLocation.transporterID",
         "TransporterLocation.availableCapacity", "TransporterLocation.totalCapacity", "Transporter.name"};

        HashMap<String, String> transporterMap = new HashMap<>();
        transporterMap.put("foreignKey", "transporterID");
        transporterMap.put("foreignTableName", "Transporter");
        transporterMap.put("localTableName", "Transporter");

        locationMap.put("localTableName", "TransporterLocation");

        HashMap[] innerJoins = new HashMap[] {transporterMap};

        return getLocations(columnNames, "TransporterLocation", innerJoins);
    }

    private HashMap<String, HashMap<String, Object>> getDistributionCentres() throws SQLException {
        String[] columnNames = {"DistributionCentre.distributionCentreID"};
        storageLocationMap.put("localTableName", "DistributionCentre");
        return getStorageLocations(columnNames, "DistributionCentre", new HashMap[] {});
    }

    private HashMap<String, HashMap<String, Object>> getVaccinationCentres() throws SQLException {
        String[] columnNames = {"VaccinationCentre.vaccinationCentreID", "VaccinationCentre.name"};
        storageLocationMap.put("localTableName", "VaccinationCentre");
        return getStorageLocations(columnNames, "VaccinationCentre", new HashMap[] {});
    }

    private HashMap<String, HashMap<String, Object>> getStorageLocations(String[] originalColumnNames, String tableName, HashMap[] originalInnerJoins) throws SQLException {
        locationMap.put("localTableName", "StorageLocation");

        String[] additionalColumnNames = {"StorageLocation.storageLocationID", "StorageLocation.locationID"};
        String[] columnNames = mergeColumnNames(originalColumnNames, additionalColumnNames);

        HashMap[] additionalInnerJoins = new HashMap[] {storageLocationMap};
        HashMap[] innerJoins = mergeInnerJoins(additionalInnerJoins, originalInnerJoins);

        HashMap<String, HashMap<String, Object>> facilities = getLocations(columnNames, tableName, innerJoins);

        for (String keyI : facilities.keySet()) {
            HashMap<String, HashMap<String, Object>> stores = getStores((String) facilities.get(keyI).get("StorageLocation.storageLocationID"));
            for (String keyJ : stores.keySet()) {
                HashMap<String, HashMap<String, Object>> vaccinesInStorage = getVaccinesInStorage((String) stores.get(keyJ).get("storeID"));
                stores.get(keyJ).put("vaccinesInStorage", vaccinesInStorage);
            }
            facilities.get(keyI).put("stores", stores);
        }

        return facilities;
    }

    private HashMap<String, HashMap<String, Object>> getLocations(String[] originalColumnNames, String tableName, HashMap[] originalInnerJoins) throws SQLException {

        String[] additionalColumnNames = {"Location.locationID", "Location.longitude", "Location.latitude"};
        String[] columnNames = mergeColumnNames(originalColumnNames, additionalColumnNames);

        HashMap[] additionalInnerJoins = new HashMap[] {locationMap};
        HashMap[] innerJoins = mergeInnerJoins(originalInnerJoins, additionalInnerJoins);

        HashMap<String, HashMap<String, Object>> locations = vaccineSystem.executeSelect4(columnNames, tableName, innerJoins, null);

        for (String key : locations.keySet()) {
            HashMap<String, HashMap<String, Object>> openingTimes = getOpeningTimes((String) locations.get(key).get("Location.locationID"));
            locations.get(key).put("openingTimes", openingTimes);
        }

        return locations;
    }

    private String[] mergeColumnNames(String[] arrayA, String[] arrayB) {
        String[] arrayC = new String[arrayA.length + arrayB.length];
        System.arraycopy(arrayA, 0, arrayC, 0, arrayA.length);
        System.arraycopy(arrayB, 0, arrayC, arrayA.length, arrayB.length);
        return arrayC;
    }

    // Must be separate to mergeColumnNames as casting doesn't work for arrays
    private HashMap[] mergeInnerJoins(HashMap[] arrayA, HashMap[] arrayB) {
        HashMap[] arrayC = new HashMap[arrayA.length + arrayB.length];
        System.arraycopy(arrayA, 0, arrayC, 0, arrayA.length);
        System.arraycopy(arrayB, 0, arrayC, arrayA.length, arrayB.length);
        return arrayC;
    }

    private HashMap<String, HashMap<String, Object>> getStores(String storageLocationID) throws SQLException {
        String[] columnNames = {"Store.temperature", "Store.capacity", "Store.storeID"};
        return vaccineSystem.executeSelect4(columnNames, "Store", null, "storageLocationID = " + storageLocationID);
    }

    private HashMap<String, HashMap<String, Object>> getVaccinesInStorage() throws SQLException {
        String[] columnNames = {"VaccineInStorage.vaccineInStorageID", "VaccineInStorage.vaccineID", "VaccineInStorage.stockLevel", "VaccineInStorage.expirationDate"};
        return vaccineSystem.executeSelect4(columnNames, "VaccineInStorage", null, null);
    }

    private HashMap<String, HashMap<String, Object>> getVaccinesInStorage(String storeID) throws SQLException {
        String[] columnNames = {"VaccineInStorage.vaccineID", "VaccineInStorage.stockLevel", "VaccineInStorage.expirationDate"};
        return vaccineSystem.executeSelect4(columnNames, "VaccineInStorage", null, "storeID = " + storeID);
    }

    private HashMap<String, HashMap<String, Object>> getOpeningTimes(String locationID) throws SQLException {
        String[] columnNames = {"OpeningTime.day", "OpeningTime.startTime", "OpeningTime.endTime"};
        return vaccineSystem.executeSelect4(columnNames, "OpeningTime", null, "locationID = " + locationID);
    }
}
