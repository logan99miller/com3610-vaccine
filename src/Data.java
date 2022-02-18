import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Data {

    private LocalDate currentDate;
    private LocalTime currentTime;
    private final VaccineSystem vaccineSystem;
    private HashMap<String, String> locationMap, storageLocationMap, medicalConditionMap;
    private HashMap<String, HashMap<String, Object>> vaccines, factories, transporterLocations, distributionCentres,
     vaccinationCentres, vaccinesInTransit, vaccinePriority, people;

    public Data(VaccineSystem vaccineSystem) {
        this.vaccineSystem = vaccineSystem;
        setMaps();
        try {
            read();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void updateDate() {
        currentDate = LocalDate.now();
        currentTime = LocalTime.of(11, 0);
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
        updateDate();
        removeExpiredStock(currentDate);
        removePastBookings(currentDate);

        vaccines = readVaccines();
        factories = readFactories();
        transporterLocations = readTransporterLocations();
        distributionCentres = readDistributionCentres();
        vaccinationCentres = readVaccinationCentres();
        vaccinesInTransit = readVaccinesInTransit();
        vaccinePriority = readVaccinePriorities();
        people = readPeople();
    }

    public void write() throws SQLException {
        writeMaps(vaccines);
        writeMaps(factories);
        writeMaps(transporterLocations);
        writeMaps(distributionCentres);
        writeMaps(vaccinationCentres);
        writeMaps(vaccinesInTransit);
//        writeMaps(bookings);
        writeMaps(people);
    }

    private void writeMaps(HashMap<String, HashMap<String, Object>> maps) throws SQLException {
        for (String key : maps.keySet()) {
            writeMap(maps.get(key));
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

    private void removePastBookings(LocalDate currentDate) throws SQLException {
        HashMap<String, HashMap<String, Object>> bookings = readBookings();

        for (String key : bookings.keySet()) {
            LocalDate bookingDate = getLocalDate((String) bookings.get(key).get("Booking.date"));
            if (bookingDate.isBefore(currentDate)) {
                String ID = (String) bookings.get(key).get("Booking.bookingID");
                delete("bookingID", ID, "Booking");
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
//            HashMap<String, HashMap<String, Object>> priorities = readVaccinePriorities(vaccineID);
            vaccines.get(key).put("lifespans", lifespans);
            vaccines.get(key).put("exemptions", exemptions);
//            vaccines.get(key).put("priorities", priorities);
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

    private HashMap<String, HashMap<String, Object>> readVaccinePriorities() throws SQLException {
        String[] columnNames = {"VaccinePriority.vaccinePriorityID", "VaccinePriority.vaccineID", "VaccinePriority.lowestAge",
         "VaccinePriority.highestAge", "VaccinePriority.doseNumber", "VaccinePriority.positionInQueue", "VaccinePriority.eligible"};
        return vaccineSystem.executeSelect4(columnNames, "VaccinePriority", null, null);
    }

//    private HashMap<String, HashMap<String, Object>> readVaccinePriorities(String vaccineID) throws SQLException {
//        String[] columnNames = {"VaccinePriority.vaccinePriorityID", "VaccinePriority.lowestAge", "VaccinePriority.highestAge",
//         "VaccinePriority.doseNumber", "VaccinePriority.positionInQueue", "VaccinePriority.eligible"};
//        return vaccineSystem.executeSelect4(columnNames, "VaccinePriority", null, "vaccineID = " + vaccineID);
//    }

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
        String[] columnNames = {"VaccinationCentre.vaccinationCentreID", "VaccinationCentre.name", "VaccinationCentre.vaccinesPerHour"};

        storageLocationMap.put("localTableName", "VaccinationCentre");
        HashMap<String, HashMap<String, Object>> vaccinationCentres = readStorageLocations(columnNames, "VaccinationCentre", new HashMap[] {});

        for (String key : vaccinationCentres.keySet()) {
            String vaccinationCentreID = (String) vaccinationCentres.get(key).get("VaccinationCentre.vaccinationCentreID");
            HashMap<String, HashMap<String, Object>> bookings = readBookingsFromVaccinationCentreID(vaccinationCentreID);
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
        String[] columnNames = {"Person.personID", "Person.forename", "Person.surname", "Person.DoB"};

        HashMap<String, HashMap<String, Object>> people = vaccineSystem.executeSelect4(columnNames, "Person", new HashMap[] {}, null);

        for (String key : people.keySet()) {
            String personID = (String) people.get(key).get("Person.personID");
            HashMap<String, HashMap<String, Object>> bookings = readBookingsFromPersonID(personID);
            HashMap<String, HashMap<String, Object>> medicalConditions = readPersonMedicalConditions(personID);
            HashMap<String, HashMap<String, Object>> vaccinesReceived = readVaccinesReceived(personID);
            people.get(key).put("booking", bookings);
            people.get(key).put("medicalCondition", medicalConditions);
            people.get(key).put("vaccineReceived", vaccinesReceived);
        }

        return people;
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

    private HashMap<String, HashMap<String, Object>> readBookingsFromVaccinationCentreID(String vaccinationCentreID) throws SQLException {
        String[] columnNames = {"Booking.bookingID", "Booking.personID", "Booking.vaccinationCentreID", "Booking.date"};
        return vaccineSystem.executeSelect4(columnNames, "Booking", null, "vaccinationCentreID = " + vaccinationCentreID);
    }

    private HashMap<String, HashMap<String, Object>> readBookingsFromPersonID(String personID) throws SQLException {
        String[] columnNames = {"Booking.bookingID", "Booking.personID", "Booking.vaccinationCentreID", "Booking.date"};
        return vaccineSystem.executeSelect4(columnNames, "Booking", null, "personID = " + personID);
    }

    private HashMap<String, HashMap<String, Object>> readBookings() throws SQLException {
        String[] columnNames = {"Booking.bookingID", "Booking.personID", "Booking.vaccinationCentreID", "Booking.date"};
        return vaccineSystem.executeSelect4(columnNames, "Booking", null, null);
    }

    private HashMap<String, HashMap<String, Object>> readPersonMedicalConditions(String personID) throws SQLException {
        String[] columnNames = {"PersonMedicalCondition.personMedicalConditionID", "PersonMedicalCondition.personID", "PersonMedicalCondition.medicalConditionID"};
        return vaccineSystem.executeSelect4(columnNames, "PersonMedicalCondition", null, "personID = " + personID);
    }

    private HashMap<String, HashMap<String, Object>> readVaccinesReceived(String personID) throws SQLException {
        String[] columnNames = {"VaccineReceived.vaccineReceivedID", "VaccineReceived.personID", "VaccineReceived.vaccineID", "VaccineReceived.date"};
        return vaccineSystem.executeSelect4(columnNames, "VaccineReceived", null, "personID = " + personID);
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

    public static ArrayList<Integer> sortMaps(HashMap<String, HashMap<String, Object>> map, String sortKey) {
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

    // Expects dates in the format YYYY:MM:DD, where : is the splitter value given
    public static LocalDate getDateFromString(String string, String splitter) {
        String[] subString = string.split(splitter);
        if (subString.length == 3) {
            int year = Integer.parseInt(subString[0]);
            int hour = Integer.parseInt(subString[1]);
            int minute = Integer.parseInt(subString[2]);
            LocalDate date = LocalDate.of(year, hour, minute);
            return date;
        }
        return null;
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

    public HashMap<String, HashMap<String, Object>> getVaccinePriority() {
        return vaccinePriority;
    }

    public void setVaccinePriority(HashMap<String, HashMap<String, Object>> vaccinePriority) {
        this.vaccinePriority = vaccinePriority;
    }

    public HashMap<String, HashMap<String, Object>> getPeople() {
        return people;
    }

    public void setPeople(HashMap<String, HashMap<String, Object>> people) {
        this.people = people;
    }

    public LocalDate getCurrentDate() {
        return currentDate;
    }

    public void setCurrentDate(LocalDate currentDate) {
        this.currentDate = currentDate;
    }

    public LocalTime getCurrentTime() {
        return currentTime;
    }

    public void setCurrentTime(LocalTime currentTime) {
        this.currentTime = currentTime;
    }
}
