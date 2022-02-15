import java.sql.SQLException;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

public class Data {

    private final VaccineSystem vaccineSystem;
    private HashMap<String, String> locationMap, storageLocationMap, medicalConditionMap;
    private HashMap<String, HashMap<String, Object>> vaccines, factories, transporterLocations, distributionCentres,
     vaccinationCentres, vaccinesInTransit, people;

    public Data(VaccineSystem vaccineSystem) {
        this.vaccineSystem = vaccineSystem;
        setMaps();
        try {
            read();
        } catch (SQLException e) {
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

    public void read() throws SQLException {
        vaccines = readVaccines();
        factories = readFactories();
        transporterLocations = readTransporterLocations();
        distributionCentres = readDistributionCentres();
        vaccinationCentres = readVaccinationCentres();
        vaccinesInTransit = readVaccinesInTransit();
//        bookings = readBookings();
        people = readPeople();
    }

    public void write(LocalDate currentDate) throws SQLException {
        writeMaps(vaccines);
        writeMaps(factories);
        writeMaps(transporterLocations);
        writeMaps(distributionCentres);
        writeMaps(vaccinationCentres);
        writeMaps(vaccinesInTransit);
//        writeMaps(bookings);
        writeMaps(people);
        removeExpiredStock(currentDate);
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

            if (valuesMap.get("change") != null) {
                valuesMap.remove("change");
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

                if (where.equals("")) {
                    insert(columnNames, values, key);
                } else {
                    update(columnNames, values, key, where);
                }
            }
        }
    }

    private void removeExpiredStock(LocalDate currentDate) throws SQLException {
        HashMap<String, HashMap<String, Object>> vaccinesInStorage = readVaccinesInStorage();

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
        return LocalDate.of(
                Integer.parseInt(dateValues[0]),
                Integer.parseInt(dateValues[1]),
                Integer.parseInt(dateValues[2].substring(0, 2)));
    }

    private HashMap<String, HashMap<String, Object>> readVaccines() throws SQLException {
        String[] columnNames = {"Vaccine.vaccineID", "Vaccine.name", "Vaccine.dosesNeeded", "Vaccine.daysBetweenDoses"};

        HashMap<String, HashMap<String, Object>> vaccines = vaccineSystem.executeSelect4(columnNames, "vaccine", null, null);

        for (String key : vaccines.keySet()) {
            String vaccineID = (String) vaccines.get(key).get("Vaccine.vaccineID");
            HashMap<String, HashMap<String, Object>> lifespans = readVaccineLifespans(vaccineID);
            HashMap<String, HashMap<String, Object>> exemptions = readVaccineExemptions(vaccineID);
            HashMap<String, HashMap<String, Object>> priorities = readVaccineExemptions(vaccineID);
            vaccines.get(key).put("lifespans", lifespans);
            vaccines.get(key).put("exemptions", exemptions);
            vaccines.get(key).put("priorities", priorities);
        }

        return vaccines;
    }

    private HashMap<String, HashMap<String, Object>> readVaccineLifespans(String vaccineID) throws SQLException {
        String[] columnNames = {"VaccineLifespan.vaccineLifespanID", "VaccineLifespan.lifespan", "VaccineLifespan.lowestTemperature", "VaccineLifespan.highestTemperature"};
        return vaccineSystem.executeSelect4(columnNames, "VaccineLifespan", null, "vaccineID = " + vaccineID);
    }

    private HashMap<String, HashMap<String, Object>> readVaccineExemptions(String vaccineID) throws SQLException {
        String[] columnNames = {"VaccineExemption.medicalConditionID", "MedicalCondition.name", "MedicalCondition.vulnerabilityLevel"};
        medicalConditionMap.put("localTableName", "VaccineExemption");

        HashMap[] innerJoins = new HashMap[] {medicalConditionMap};
        return vaccineSystem.executeSelect4(columnNames, "VaccineExemption", innerJoins, "vaccineID = " + vaccineID);
    }

    private HashMap<String, HashMap<String, Object>> readVaccinePriorities(String vaccineID) throws SQLException {
        String[] columnNames = {"VaccinePriority.vaccinePriorityID", "VaccinePriority.lowestAge", "VaccinePriority.highestAge",
         "VaccinePriority.doseNumber", "VaccinePriority.positionInQueue", "VaccinePriority.eligible"};
        return vaccineSystem.executeSelect4(columnNames, "VaccinePriority", null, "vaccineID = " + vaccineID);
    }

    private HashMap<String, HashMap<String, Object>> readFactories() throws SQLException {
        String[] columnNames = {"Factory.factoryID", "Factory.manufacturerID", "Manufacturer.manufacturerID", "Manufacturer.name", "Manufacturer.vaccineID", "Factory.vaccinesPerMin"};
        storageLocationMap.put("localTableName", "Factory");

        HashMap[] innerJoins = new HashMap[] {getManufacturerMap()};

        return readStorageLocations(columnNames, "Factory", innerJoins);
    }

    private HashMap<String, String> getManufacturerMap() {
        HashMap<String, String> map = new HashMap<>();
        map.put("foreignKey", "manufacturerID");
        map.put("foreignTableName", "Manufacturer");
        map.put("localTableName", "Factory");
        return map;
    }

    private HashMap<String, HashMap<String, Object>> readTransporterLocations() throws SQLException {
        String[] columnNames = {"TransporterLocation.transporterLocationID", "TransporterLocation.transporterID",
         "TransporterLocation.availableCapacity", "TransporterLocation.totalCapacity", "Transporter.name"};

        locationMap.put("localTableName", "TransporterLocation");

        HashMap[] innerJoins = new HashMap[] {getTransporterMap()};

        return readLocations(columnNames, "TransporterLocation", innerJoins);
    }

    private HashMap<String, String> getTransporterMap() {
        HashMap<String, String> map = new HashMap<>();
        map.put("foreignKey", "transporterID");
        map.put("foreignTableName", "Transporter");
        map.put("localTableName", "TransporterLocation");
        return map;
    }

    private HashMap<String, HashMap<String, Object>> readDistributionCentres() throws SQLException {
        String[] columnNames = {"DistributionCentre.distributionCentreID"};
        storageLocationMap.put("localTableName", "DistributionCentre");
        return readStorageLocations(columnNames, "DistributionCentre", new HashMap[] {});
    }

    private HashMap<String, HashMap<String, Object>> readVaccinationCentres() throws SQLException {
        String[] columnNames = {"VaccinationCentre.vaccinationCentreID", "VaccinationCentre.name"};

        storageLocationMap.put("localTableName", "VaccinationCentre" +
                "");
        HashMap<String, HashMap<String, Object>> vaccinationCentres = readStorageLocations(columnNames, "VaccinationCentre", new HashMap[] {});

        for (String key : vaccinationCentres.keySet()) {
            String vaccinationCentreID = (String) vaccinationCentres.get(key).get("VaccinationCentre.vaccinationCentreID");
            HashMap<String, HashMap<String, Object>> bookings = readBookings(vaccinationCentreID);
            vaccinationCentres.get(key).put("booking", bookings);
        }

        return vaccinationCentres;
    }

    private HashMap<String, HashMap<String, Object>> readStorageLocations(String[] originalColumnNames, String tableName, HashMap[] originalInnerJoins) throws SQLException {
        locationMap.put("localTableName", "StorageLocation");

        String[] additionalColumnNames = {"StorageLocation.storageLocationID", "StorageLocation.locationID"};
        String[] columnNames = mergeColumnNames(originalColumnNames, additionalColumnNames);

        HashMap[] additionalInnerJoins = new HashMap[] {storageLocationMap};
        HashMap[] innerJoins = mergeInnerJoins(additionalInnerJoins, originalInnerJoins);

        HashMap<String, HashMap<String, Object>> facilities = readLocations(columnNames, tableName, innerJoins);

        for (String keyI : facilities.keySet()) {
            HashMap<String, HashMap<String, Object>> stores = readStores((String) facilities.get(keyI).get("StorageLocation.storageLocationID"));
            for (String keyJ : stores.keySet()) {
                HashMap<String, HashMap<String, Object>> vaccinesInStorage = readVaccinesInStorage((String) stores.get(keyJ).get("Store.storeID"));
                stores.get(keyJ).put("vaccineInStorage", vaccinesInStorage);
            }
            facilities.get(keyI).put("stores", stores);
        }

        return facilities;
    }

    private HashMap<String, HashMap<String, Object>> readLocations(String[] originalColumnNames, String tableName, HashMap[] originalInnerJoins) throws SQLException {

        String[] additionalColumnNames = {"Location.locationID", "Location.longitude", "Location.latitude"};
        String[] columnNames = mergeColumnNames(originalColumnNames, additionalColumnNames);

        HashMap[] additionalInnerJoins = new HashMap[] {locationMap};
        HashMap[] innerJoins = mergeInnerJoins(originalInnerJoins, additionalInnerJoins);

        HashMap<String, HashMap<String, Object>> locations = vaccineSystem.executeSelect4(columnNames, tableName, innerJoins, null);

        for (String key : locations.keySet()) {
            HashMap<String, HashMap<String, Object>> openingTimes = readOpeningTimes((String) locations.get(key).get("Location.locationID"));
            locations.get(key).put("openingTime", openingTimes);
        }

        return locations;
    }

    private HashMap<String, HashMap<String, Object>> readVaccinesInTransit() throws SQLException {
        String[] columnNames = {"VaccineInTransit.vaccineInTransitID", "VaccineInTransit.vaccineID", "VaccineInTransit.destinationID",
         "VaccineInTransit.originID", "VaccineInTransit.remainingMins", "VaccineInTransit.expirationDate", "VaccineInTransit.stockLevel"};
        return vaccineSystem.executeSelect4(columnNames, "VaccineInTransit", null, null);
    }

    private HashMap<String, HashMap<String, Object>> readPeople() throws SQLException {
        String[] columnNames = {"Person.personID", "Person.forename", "Person.surname", "Person.DoB",
         "PersonMedicalCondition.personMedicalConditionID", "PersonMedicalCondition.medicalConditionID"};
        HashMap[] innerJoins = new HashMap[] {getPersonMedicalConditionMap()};
        return vaccineSystem.executeSelect4(columnNames, "Person", innerJoins, null);
    }

    private HashMap<String, String> getPersonMedicalConditionMap() {
        HashMap<String, String> map = new HashMap<>();
        map.put("foreignKey", "personID");
        map.put("foreignTableName", "PersonMedicalCondition");
        map.put("localTableName", "Person");
        return map;
    }

    private HashMap<String, HashMap<String, Object>> readStores(String storageLocationID) throws SQLException {
        String[] columnNames = {"Store.storeID", "Store.storageLocationID", "Store.temperature", "Store.capacity"};
        return vaccineSystem.executeSelect4(columnNames, "Store", null, "storageLocationID = " + storageLocationID);
    }

    private HashMap<String, HashMap<String, Object>> readVaccinesInStorage() throws SQLException {
        String[] columnNames = {"VaccineInStorage.vaccineInStorageID", "VaccineInStorage.vaccineID", "VaccineInStorage.stockLevel", "VaccineInStorage.expirationDate"};
        return vaccineSystem.executeSelect4(columnNames, "VaccineInStorage", null, null);
    }

    private HashMap<String, HashMap<String, Object>> readVaccinesInStorage(String storeID) throws SQLException {
        String[] columnNames = {"VaccineInStorage.vaccineInStorageID", "VaccineInStorage.vaccineID", "VaccineInStorage.stockLevel", "VaccineInStorage.expirationDate"};
        return vaccineSystem.executeSelect4(columnNames, "VaccineInStorage", null, "storeID = " + storeID);
    }

    private HashMap<String, HashMap<String, Object>> readOpeningTimes(String locationID) throws SQLException {
        String[] columnNames = {"OpeningTime.openingTimeID", "OpeningTime.day", "OpeningTime.startTime", "OpeningTime.endTime"};
        return vaccineSystem.executeSelect4(columnNames, "OpeningTime", null, "locationID = " + locationID);
    }

    private HashMap<String, HashMap<String, Object>> readBookings(String vaccinationCentreID) throws SQLException {
        String[] columnNames = {"Booking.bookingID", "Booking.personID", "Booking.vaccinationCentreID", "Booking.date"};
        return vaccineSystem.executeSelect4(columnNames, "Booking", null, "vaccinationCentreID = " + vaccinationCentreID);
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

    private void delete(String IDFieldName, String ID, String tableName) throws SQLException {
//        System.out.println("DELETE FROM " + tableName + " WHERE " + IDFieldName + " = " + ID);
        vaccineSystem.executeUpdate("DELETE FROM " + tableName + " WHERE " + IDFieldName + " = " + ID);
    }

    private void insert(String[] columnNames, Object[] values, String tableName) throws SQLException {
        String columnNamesText = getColumnNamesText(columnNames);
        String valuesText = getValuesText(values);
//        System.out.println("INSERT INTO " + tableName + " (" + columnNamesText + ") VALUES (" + valuesText + ");");
        vaccineSystem.executeUpdate("INSERT INTO " + tableName + " (" + columnNamesText + ") VALUES (" + valuesText + ");");
    }

    private void update(String[] columnNames, Object[] values, String tableName, String where) throws SQLException {
        String statementText = "UPDATE " + tableName + " SET " + getOnText(columnNames, values) + " WHERE " + where;
//        System.out.println(statementText);
        vaccineSystem.executeUpdate(statementText);
    }

    private String getValuesText(Object[] values) {
        String valuesText = "";
        valuesText = addToValues(valuesText, values[0], "");
        for (int i = 1; i < values.length; i++) {
            valuesText = addToValues(valuesText, values[i], ", ");
        }
        return valuesText;
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

    private String getColumnNamesText(String[] columnNames) {
        String columnNamesText = columnNames[0];
        for (int i = 1; i < columnNames.length; i++) {
            columnNamesText += ", " + columnNames[i];
        }
        return columnNamesText;
    }

    public HashMap<String, HashMap<String, Object>> getVaccines() {
        return vaccines;
    }

    public void setVaccines(HashMap<String, HashMap<String, Object>> vaccines) {
        this.vaccines = vaccines;
    }

    public HashMap<String, HashMap<String, Object>> getFactories() {
        return factories;
    }

    public void setFactories(HashMap<String, HashMap<String, Object>> factories) {
        this.factories = factories;
    }

    public HashMap<String, HashMap<String, Object>> getTransporterLocations() {
        return transporterLocations;
    }

    public void setTransporterLocations(HashMap<String, HashMap<String, Object>> transporterLocations) {
        this.transporterLocations = transporterLocations;
    }

    public HashMap<String, HashMap<String, Object>> getDistributionCentres() {
        return distributionCentres;
    }

    public void setDistributionCentres(HashMap<String, HashMap<String, Object>> distributionCentres) {
        this.distributionCentres = distributionCentres;
    }

    public HashMap<String, HashMap<String, Object>> getVaccinationCentres() {
        return vaccinationCentres;
    }

    public void setVaccinationCentres(HashMap<String, HashMap<String, Object>> vaccinationCentres) {
        this.vaccinationCentres = vaccinationCentres;
    }

    public HashMap<String, HashMap<String, Object>> getVaccinesInTransit() {
        return vaccinesInTransit;
    }

    public void setVaccinesInTransit(HashMap<String, HashMap<String, Object>> vaccinesInTransit) {
        this.vaccinesInTransit = vaccinesInTransit;
    }
}
