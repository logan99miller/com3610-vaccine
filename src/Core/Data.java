package Core;

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
            vaccinationCentres, people, vans;

    public Data(VaccineSystem vaccineSystem) {
        this.vaccineSystem = vaccineSystem;
        setMaps();
        try {
            update();
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

    public void update() throws SQLException {
        updateDate();
        removeInvalidVanReferences();
        removeExpiredStock(currentDate);
        removePastBookings(currentDate);
    }

    public void read() throws SQLException {
        vaccines = readVaccines();
        factories = readFactories();
        transporterLocations = readTransporterLocations();
        distributionCentres = readDistributionCentres();
        vaccinationCentres = readVaccinationCentres();
        people = readPeople();
        vans = readVans();
    }

    public void write() throws SQLException {
        writeMaps(vaccines);
        writeMaps(factories);
        writeMaps(transporterLocations);
        writeMaps(distributionCentres);
        writeMaps(vaccinationCentres);
        writeMaps(people);
        writeMaps(vans);
    }

    private void writeMaps(HashMap<String, HashMap<String, Object>> maps) throws SQLException {
        for (String key : maps.keySet()) {
            writeMap(maps.get(key));
        }
    }

    private void writeMap(HashMap<String, Object> map) throws SQLException {
        HashMap<String, HashMap<String, String>> valuesToWrite = getValuesToWrite(map);

        for (String key : valuesToWrite.keySet()) {
            HashMap<String, String> valuesMap = valuesToWrite.get(key);
            if (valuesMap.get("change") != null) {
                writeValues(valuesMap, key);
            }
        }
    }

    private HashMap<String, HashMap<String, String>> getValuesToWrite(HashMap<String, Object> map) throws SQLException {
        HashMap<String, HashMap<String, String>> valuesToWrite = new HashMap<>();

        for (String key : map.keySet()) {
            try {
                String value = (String) map.get(key);
                String[] splitKey = key.split("\\.");
                String secondaryTableName = splitKey[0];
                String fieldName = splitKey[1];

                if (valuesToWrite.get(secondaryTableName) == null) {
                    valuesToWrite.put(secondaryTableName, new HashMap<>());
                }
                valuesToWrite.get(secondaryTableName).put(fieldName, value);
            } catch (ClassCastException e) {

                HashMap<String, Object> value = (HashMap<String, Object>) map.get(key);
                writeMap(value);
            }
        }
        return valuesToWrite;
    }

    private void writeValues(HashMap<String, String> valuesMap, String key) throws SQLException {
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
            vaccineSystem.insert(columnNames, values, key);
        }
        else {
            vaccineSystem.update(columnNames, values, key, where);
        }
    }

    private void removeExpiredStock(LocalDate currentDate) throws SQLException {
        HashMap<String, HashMap<String, Object>> vaccinesInStorage = readVaccinesInStorage();

        for (String key : vaccinesInStorage.keySet()) {
            LocalDate expirationDate = getLocalDate((String) vaccinesInStorage.get(key).get("VaccineInStorage.expirationDate"));
            if (expirationDate.isBefore(currentDate)) {
                String ID = (String) vaccinesInStorage.get(key).get("VaccineInStorage.vaccineInStorageID");
                vaccineSystem.delete("vaccineInStorageID", ID, "VaccineInStorage");
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
                vaccineSystem.delete("bookingID", ID, "Booking");
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

    private void removeInvalidVanReferences() throws SQLException {
        vans = readVans();

        HashMap<String, HashMap<String, Object>> allStorageFacilities = new HashMap<>();
        allStorageFacilities.putAll(readFactories());
        allStorageFacilities.putAll(readDistributionCentres());
        allStorageFacilities.putAll(readVaccinationCentres());

        boolean originExists = false;
        boolean destinationExists = false;

        for (String keyI : vans.keySet()) {
            HashMap<String, Object> van = vans.get(keyI);
            String originID = (String) van.get("Van.originID");
            String destinationID = (String) van.get("Van.destinationID");
            for (String keyJ : allStorageFacilities.keySet()) {
                HashMap<String, Object> facility = allStorageFacilities.get(keyJ);
                String storageLocationID = (String) facility.get("StorageLocation.storageLocationID");
                if (storageLocationID.equals(originID)) {
                    originExists = true;
                }
                if (storageLocationID.equals(destinationID)) {
                    destinationExists = true;
                }
            }

            if ((!originExists) || (!destinationExists)) {
                van.put("Van.deliveryStage", "waiting");
                van.put("Van.change", "change");
                writeMap(van);
            }
        }
    }

    private HashMap<String, HashMap<String, Object>> readVaccines() throws SQLException {
        String[] columnNames = {"Vaccine.vaccineID", "Vaccine.name", "Vaccine.dosesNeeded", "Vaccine.daysBetweenDoses"};

        HashMap<String, HashMap<String, Object>> vaccines = vaccineSystem.executeSelect(columnNames, "vaccine");

        for (String key : vaccines.keySet()) {
            String vaccineID = (String) vaccines.get(key).get("Vaccine.vaccineID");
            HashMap<String, HashMap<String, Object>> lifespans = readVaccineLifespans(vaccineID);
            HashMap<String, HashMap<String, Object>> exemptions = readVaccineExemptions(vaccineID);
            vaccines.get(key).put("lifespans", lifespans);
            vaccines.get(key).put("exemptions", exemptions);
        }

        return vaccines;
    }

    private HashMap<String, HashMap<String, Object>> readVaccineLifespans(String vaccineID) throws SQLException {
        String[] columnNames = {"VaccineLifespan.vaccineLifespanID", "VaccineLifespan.lifespan", "VaccineLifespan.lowestTemperature", "VaccineLifespan.highestTemperature"};
        return vaccineSystem.executeSelect(columnNames, "VaccineLifespan", "vaccineID = " + vaccineID);
    }

    private HashMap<String, HashMap<String, Object>> readVaccineExemptions(String vaccineID) throws SQLException {
        String[] columnNames = {"VaccineExemption.medicalConditionID", "MedicalCondition.name", "MedicalCondition.vulnerabilityLevel"};
        medicalConditionMap.put("localTableName", "VaccineExemption");

        HashMap[] innerJoins = new HashMap[] {medicalConditionMap};
        return vaccineSystem.executeSelect(columnNames, "VaccineExemption", innerJoins, "vaccineID = " + vaccineID);
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
        String[] columnNames = {"TransporterLocation.transporterLocationID", "TransporterLocation.transporterID", "Transporter.name"};

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
            vaccinationCentres.get(key).put("bookings", bookings);
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
                stores.get(keyJ).put("vaccinesInStorage", vaccinesInStorage);
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

        HashMap<String, HashMap<String, Object>> locations = vaccineSystem.executeSelect(columnNames, tableName, innerJoins, null);

        for (String key : locations.keySet()) {
            HashMap<String, HashMap<String, Object>> openingTimes = readOpeningTimes((String) locations.get(key).get("Location.locationID"));
            locations.get(key).put("openingTimes", openingTimes);
        }

        return locations;
    }

    private HashMap<String, HashMap<String, Object>> readPeople() throws SQLException {
        String[] columnNames = {"Person.personID", "Person.forename", "Person.surname", "Person.DoB"};

        HashMap<String, HashMap<String, Object>> people = vaccineSystem.executeSelect(columnNames, "Person");

        for (String key : people.keySet()) {
            String personID = (String) people.get(key).get("Person.personID");
            HashMap<String, HashMap<String, Object>> bookings = readBookingsFromPersonID(personID);
            HashMap<String, HashMap<String, Object>> medicalConditions = readPersonMedicalConditions(personID);
            HashMap<String, HashMap<String, Object>> vaccinesReceived = readVaccinesReceived(personID);
            people.get(key).put("bookings", bookings);
            people.get(key).put("medicalConditions", medicalConditions);
            people.get(key).put("vaccinesReceived", vaccinesReceived);
        }

        return people;
    }

    private HashMap<String, HashMap<String, Object>> readVans() throws SQLException {
        String[] columnNames = {"Van.vanID", "Van.deliveryStage", "Van.totalTime", "Van.remainingTime", "Van.storageLocationID",
        "Van.originID", "Van.destinationID", "Van.transporterLocationID"};

        storageLocationMap.put("localTableName", "Van");
        HashMap<String, HashMap<String, Object>> vans = readStorageLocations(columnNames, "Van", new HashMap[] {});

        return vans;
    }

    private HashMap<String, HashMap<String, Object>> readStores(String storageLocationID) throws SQLException {
        String[] columnNames = {"Store.storeID", "Store.storageLocationID", "Store.temperature", "Store.capacity"};
        return vaccineSystem.executeSelect(columnNames, "Store", "storageLocationID = " + storageLocationID);
    }

    private HashMap<String, HashMap<String, Object>> readVaccinesInStorage() throws SQLException {
        String[] columnNames = {"VaccineInStorage.vaccineInStorageID", "VaccineInStorage.vaccineID", "VaccineInStorage.stockLevel", "VaccineInStorage.creationDate", "VaccineInStorage.expirationDate"};
        return vaccineSystem.executeSelect(columnNames, "VaccineInStorage");
    }

    private HashMap<String, HashMap<String, Object>> readVaccinesInStorage(String storeID) throws SQLException {
        String[] columnNames = {"VaccineInStorage.vaccineInStorageID", "VaccineInStorage.vaccineID", "VaccineInStorage.stockLevel", "VaccineInStorage.creationDate", "VaccineInStorage.expirationDate"};
        return vaccineSystem.executeSelect(columnNames, "VaccineInStorage", "storeID = " + storeID);
    }

    private HashMap<String, HashMap<String, Object>> readOpeningTimes(String locationID) throws SQLException {
        String[] columnNames = {"OpeningTime.openingTimeID", "OpeningTime.day", "OpeningTime.startTime", "OpeningTime.endTime"};
        return vaccineSystem.executeSelect(columnNames, "OpeningTime", "locationID = " + locationID);
    }

    private HashMap<String, HashMap<String, Object>> readBookingsFromVaccinationCentreID(String vaccinationCentreID) throws SQLException {
        String[] columnNames = {"Booking.bookingID", "Booking.personID", "Booking.vaccinationCentreID", "Booking.date"};
        return vaccineSystem.executeSelect(columnNames, "Booking", "vaccinationCentreID = " + vaccinationCentreID);
    }

    private HashMap<String, HashMap<String, Object>> readBookingsFromPersonID(String personID) throws SQLException {
        String[] columnNames = {"Booking.bookingID", "Booking.personID", "Booking.vaccinationCentreID", "Booking.date"};
        return vaccineSystem.executeSelect(columnNames, "Booking", "personID = " + personID);
    }

    private HashMap<String, HashMap<String, Object>> readBookings() throws SQLException {
        String[] columnNames = {"Booking.bookingID", "Booking.personID", "Booking.vaccinationCentreID", "Booking.date"};
        return vaccineSystem.executeSelect(columnNames, "Booking");
    }

    private HashMap<String, HashMap<String, Object>> readPersonMedicalConditions(String personID) throws SQLException {
        String[] columnNames = {"PersonMedicalCondition.personMedicalConditionID", "PersonMedicalCondition.personID", "PersonMedicalCondition.medicalConditionID"};
        return vaccineSystem.executeSelect(columnNames, "PersonMedicalCondition", "personID = " + personID);
    }

    private HashMap<String, HashMap<String, Object>> readVaccinesReceived(String personID) throws SQLException {
        String[] columnNames = {"VaccineReceived.vaccineReceivedID", "VaccineReceived.personID", "VaccineReceived.vaccineID", "VaccineReceived.date"};
        return vaccineSystem.executeSelect(columnNames, "VaccineReceived", "personID = " + personID);
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

    public static ArrayList<Integer> sortMaps(HashMap<String, HashMap<String, Object>> map, String sortKey) {
        ArrayList<Integer> keys = new ArrayList<>();
        ArrayList<Integer> values = new ArrayList<>();

        for (String key : map.keySet()) {
            keys.add(Integer.parseInt(key));
            values.add(Integer.parseInt((String) map.get(key).get(sortKey)));
        }

        // Replace with better sorting algorithm
        for (int i = 0; i < values.size() - 1; i++) {
            for (int j = 0; j < values.size() - i - 1; j++) {
                if (values.get(j) > values.get(j + 1)) {
                    int tempID = keys.get(j);
                    int tempValue = values.get(j);
                    keys.set(j, keys.get(j + 1));
                    keys.set(j + 1, tempID);
                    values.set(j, values.get(j + 1));
                    values.set(j + 1, tempValue);
                }
            }
        }
        return keys;
    }

    public static HashMap<String, Object> findMap(HashMap<String, HashMap<String, Object>> maps, String fieldName, String fieldValue) {
        for (String key : maps.keySet()) {
            HashMap<String, Object> map = maps.get(key);
            if ((map.get(fieldName)).equals(fieldValue)) {
                return map;
            }
        }
        return null;
    }

    // Method expects dates in the format YYYY:MM:DD, where : is the splitter value given
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

    // Converts the time stored in this class and the database into LocalTime type
    public static LocalTime getLocalTime(String time) {
        String[] stringTimeValues = time.split(":");
        int[] timeValues = new int[stringTimeValues.length];
        for (int i = 0; i < timeValues.length; i++) {
            timeValues[i] = Integer.parseInt(stringTimeValues[i]);
        }
        return LocalTime.of(timeValues[0], timeValues[1], timeValues[2]);
    }

    public static HashMap<String, HashMap<String, Object>> mergeMaps(HashMap<String, HashMap<String, Object>> primaryMap,
    HashMap<String, HashMap<String, Object>> secondaryMap, String keyAddition) {
        for (String key : secondaryMap.keySet()) {
            primaryMap.put(key + keyAddition, secondaryMap.get(key));
        }
        return primaryMap;
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

    public HashMap<String, HashMap<String, Object>> getVans() {
        return vans;
    }

    public void setVans(HashMap<String, HashMap<String, Object>> vans) {
        this.vans = vans;
    }
}