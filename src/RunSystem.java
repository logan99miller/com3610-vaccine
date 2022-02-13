import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.*;
import java.util.stream.IntStream;

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
            writeChanges();
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

    private void writeChanges() throws SQLException {
        writeMaps(vaccines);
        writeMaps(factories);
        writeMaps(transporterLocations);
        writeMaps(distributionCentres);
        writeMaps(vaccinationCentres);
    }

    private void writeMaps(HashMap<String, HashMap<String, Object>> maps) throws SQLException {
        for (String key : maps.keySet()) {
            writeMap(maps.get(key));
            System.out.println(maps.get(key));
        }
    }

    private void writeMap(HashMap<String, Object> map) throws SQLException {
        HashMap<String, HashMap<String, String>> valuesToInsert = new HashMap<>();

        for (String key : map.keySet()) {
            try {
                String value = (String) map.get(key);
                String[] splitKey = key.split("\\.");
                String secondaryTableName = splitKey[0];
                String fieldName = splitKey[1];

                if (valuesToInsert.get(secondaryTableName) == null) {
                    valuesToInsert.put(secondaryTableName, new HashMap<>());
                }
                valuesToInsert.get(secondaryTableName).put(fieldName, value);
            } catch (ClassCastException e) {
                HashMap<String, Object> value = (HashMap<String, Object>) map.get(key);
                writeMap(value);
            }
        }

        for (String key : valuesToInsert.keySet()) {
            HashMap<String, String> valuesMap = valuesToInsert.get(key);
            String[] columnNames = new String[valuesMap.size()];
            String[] values = new String[valuesMap.size()];
            int i = 0;
            String where = "";
            for (Map.Entry<String, String> set : valuesMap.entrySet()) {
                columnNames[i] = set.getKey();
                values[i] = set.getValue();
                String potentialTableName = columnNames[i].substring(0, columnNames[i].length() - 2).toLowerCase();
                if (potentialTableName.equals(key.toLowerCase())) {
                    where = columnNames[i] + " = " + values[i];
                }
                i++;
            }

            List<String> list1 = new ArrayList<String>();
            Collections.addAll(list1, columnNames);
            List<String> list2 = new ArrayList<String>();
            Collections.addAll(list2, values);

            System.out.println("ColumnNames: " + list1);
            System.out.println("Values: " + list2);
            System.out.println("Key: " + key);

            if (where == "") {
                insert(columnNames, values, key);
            }
            else {
                update(columnNames, values, key, where);
            }

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
                // In future should also add to activity log
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

        for (String keyD : distributionCentres.keySet()) {

            int vaccinesNeeded = 100;
            int minimumOrder = 50;

            if (vaccinesNeeded > minimumOrder) {
                String[] factoryIDs = new String[factories.size()];
                double[] distances = new double[factories.size()];
                int i = 0;
                for (String keyF : factories.keySet()) {
                    String factoryID = (String) factories.get(keyF).get("Factory.factoryID");
                    double distance = getDistance(distributionCentres.get(keyD), factories.get(keyF));
                    factoryIDs[i] = factoryID;
                    distances[i] = distance;
                    i++;
                }
                int[] sortedDistancesIndices = getSortedIndices(distances);

                for (int factoryIndex : sortedDistancesIndices) {
                    String factoryID = factoryIDs[factoryIndex];
                    HashMap<String, Object> factory = factories.get(factoryID);
                    if (isOpen((HashMap<String, HashMap<String, Object>>) factory.get("openingTime"))) {
                        int totalStock = getTotalStock((HashMap<String, Object>) factory.get("stores"));
                        if (totalStock >= vaccinesNeeded) {
////                            private void update(String[] columnNames, Object[] values, String tableName, String where) throws SQLException {
//                            String[] columnNames[]
//
////                                    int availableCapacity, int totalVaccinesToAdd, int vaccineID, HashMap<String, Object> store
//                            addToStore();
                                break;
                        }
                    }
                }
            }

        }
    }

    private int[] getSortedIndices(String[] array) {
        return IntStream.range(0, array.length)
                .boxed().sorted(Comparator.comparing(i -> array[i]))
                .mapToInt(ele -> ele).toArray();
    }

    private int[] getSortedIndices(double[] array) {
        return IntStream.range(0, array.length)
                .boxed().sorted(Comparator.comparing(i -> array[i]))
                .mapToInt(ele -> ele).toArray();
    }

    private int[] getDistances(HashMap<String, Object> facilityA, HashMap<String, HashMap<String, Object>> facilitiesB, String IDFieldName) {
        String[] facilityBIDs = new String[facilitiesB.size()];
        double[] distances = new double[facilitiesB.size()];
        int i = 0;
        for (String keyF : facilitiesB.keySet()) {
            String factoryID = (String) facilitiesB.get(keyF).get(IDFieldName);
            double distance = getDistance(facilityA, facilitiesB.get(keyF));
            facilityBIDs[i] = factoryID;
            distances[i] = distance;
            i++;
        }
        return getSortedIndices(distances);
    }

    private double getDistance(HashMap<String, Object> facilityA, HashMap<String, Object> facilityB) {
        double longitudeA = Double.parseDouble((String) facilityA.get("Location.longitude"));
        double latitudeA = Double.parseDouble((String) facilityA.get("Location.longitude"));
        double longitudeB = Double.parseDouble((String) facilityB.get("Location.longitude"));
        double latitudeB = Double.parseDouble((String) facilityB.get("Location.longitude"));

        double longitudeDistance = longitudeA - longitudeB;
        double latitudeDistance = latitudeA - latitudeB;

       return Math.sqrt(Math.pow(longitudeDistance, 2) + Math.pow(latitudeDistance, 2));
    }

    private int getTotalStock(HashMap<String, Object> stores) {
        int totalStockLevel = 0;
        for (String keyS : stores.keySet()) {
            HashMap<String, Object> store = (HashMap<String, Object>) stores.get(keyS);
            HashMap<String, Object> vaccinesInStorage = (HashMap<String, Object>) store.get("vaccineInStorage");
            for (String keyV : vaccinesInStorage.keySet()) {
                HashMap<String, Object> vaccineInStorage = (HashMap<String, Object>) vaccinesInStorage.get(keyV);
                totalStockLevel += Integer.parseInt((String) vaccineInStorage.get("VaccineInStorage.stockLevel"));
            }
        }
        return totalStockLevel;
    }

    // Should be modified to store vaccine in most suitable fridge by sorting lifespans and picking fridge with longest lifespan
    // Remember to keep original algorithm for comparison
    private void updateFactoryStockLevels() throws SQLException {
        for (String keyI : factories.keySet()) {
            HashMap<String, Object> factory = factories.get(keyI);

            if (isOpen((HashMap<String, HashMap<String, Object>>) factory.get("openingTime"))) {
                int vaccinesPerMin = Integer.parseInt((String) factory.get("Factory.vaccinesPerMin"));

                int updateRate = vaccineSystem.getUpdateRate();
                int simulationSpeed = vaccineSystem.getSimulationSpeed();
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
                addedStocks = true;

                vaccinesInStorage.put(key, vaccineInStorage);
                store.put("vaccineInStorage", vaccinesInStorage);
                return store;
            }
        }
        if (!addedStocks) {
            vaccineInStorage = new HashMap<>();
            vaccineInStorage.put("VaccineInStorage.vaccineID", Integer.toString(vaccineID));
            vaccineInStorage.put("VaccineInStorage.storeID", (String) store.get("Store.storeID"));
            vaccineInStorage.put("VaccineInStorage.stockLevel", Integer.toString(vaccinesToAdd));
            vaccineInStorage.put("VaccineInStorage.expirationDate", expirationDate);
            vaccinesInStorage.put("newID", vaccineInStorage);
        }
        store.put("vaccineInStorage", vaccinesInStorage);
        return store;
    }

    private String getExpirationDate(int vaccineID, int storageTemperature) {
        HashMap<String, Object> vaccine = vaccines.get(Integer.toString(vaccineID));
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

    private void delete(String IDFieldName, String ID, String tableName) throws SQLException {
        System.out.println("DELETE FROM " + tableName + " WHERE " + IDFieldName + " = " + ID);
        vaccineSystem.executeUpdate("DELETE FROM " + tableName + " WHERE " + IDFieldName + " = " + ID);
    }

    private void insert(String[] columnNames, Object[] values, String tableName) throws SQLException {
        String columnNamesText = getColumnNamesText(columnNames);
        String valuesText = getValuesText(values);
        System.out.println("INSERT INTO " + tableName + " (" + columnNamesText + ") VALUES (" + valuesText + ");");
        vaccineSystem.executeUpdate("INSERT INTO " + tableName + " (" + columnNamesText + ") VALUES (" + valuesText + ");");
    }

    private void update(String[] columnNames, Object[] values, String tableName, String where) throws SQLException {
        String statementText = "UPDATE " + tableName + " SET " + getOnText(columnNames, values) + " WHERE " + where;
        System.out.println(statementText);
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
                usedCapacity += Integer.parseInt((String) vaccinesInStorage.get(key).get("VaccineInStorage.stockLevel"));
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
        String[] columnNames = {"VaccineLifespan.vaccineLifespanID", "VaccineLifespan.lifespan", "VaccineLifespan.lowestTemperature", "VaccineLifespan.highestTemperature"};
        return vaccineSystem.executeSelect4(columnNames, "VaccineLifespan", null, "vaccineID = " + vaccineID);
    }

    private HashMap<String, HashMap<String, Object>> getVaccineExemptionFromVaccineID(String vaccineID) throws SQLException {
        String[] columnNames = {"VaccineExemption.medicalConditionID", "MedicalCondition.name", "MedicalCondition.vulnerabilityLevel"};

        medicalConditionMap.put("localTableName", "VaccineExemption");

        HashMap[] innerJoins = new HashMap[] {medicalConditionMap};
        return vaccineSystem.executeSelect4(columnNames, "VaccineExemption", innerJoins, "vaccineID = " + vaccineID);
    }

    private HashMap<String, HashMap<String, Object>> getFactories() throws SQLException {
        String[] columnNames = {"Factory.factoryID", "Factory.manufacturerID", "Manufacturer.manufacturerID", "Manufacturer.name", "Manufacturer.vaccineID", "Factory.vaccinesPerMin"};

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
                HashMap<String, HashMap<String, Object>> vaccinesInStorage = getVaccinesInStorage((String) stores.get(keyJ).get("Store.storeID"));
                stores.get(keyJ).put("vaccineInStorage", vaccinesInStorage);
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
            locations.get(key).put("openingTime", openingTimes);
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
        String[] columnNames = {"Store.storeID", "Store.storageLocationID", "Store.temperature", "Store.capacity"};
        return vaccineSystem.executeSelect4(columnNames, "Store", null, "storageLocationID = " + storageLocationID);
    }

    private HashMap<String, HashMap<String, Object>> getVaccinesInStorage() throws SQLException {
        String[] columnNames = {"VaccineInStorage.vaccineInStorageID", "VaccineInStorage.vaccineID", "VaccineInStorage.stockLevel", "VaccineInStorage.expirationDate"};
        return vaccineSystem.executeSelect4(columnNames, "VaccineInStorage", null, null);
    }

    private HashMap<String, HashMap<String, Object>> getVaccinesInStorage(String storeID) throws SQLException {
        String[] columnNames = {"VaccineInStorage.vaccineInStorageID", "VaccineInStorage.vaccineID", "VaccineInStorage.stockLevel", "VaccineInStorage.expirationDate"};
        return vaccineSystem.executeSelect4(columnNames, "VaccineInStorage", null, "storeID = " + storeID);
    }

    private HashMap<String, HashMap<String, Object>> getOpeningTimes(String locationID) throws SQLException {
        String[] columnNames = {"OpeningTime.openingTimeID", "OpeningTime.day", "OpeningTime.startTime", "OpeningTime.endTime"};
        return vaccineSystem.executeSelect4(columnNames, "OpeningTime", null, "locationID = " + locationID);
    }
}
