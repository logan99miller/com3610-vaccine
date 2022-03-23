/** Static class called upon by the Data and Update classes to read data from the database and return it as a hashmap of
 * hashmaps in the format HashMap<primaryKeyValue, HashMap<columName, databaseValue>> representing HashMap<key, value>
 */
package Data;

import Core.VaccineSystem;
import java.sql.SQLException;
import java.util.HashMap;

public class Read {

    // Data required to perform SQL inner joins to gather the correct data from the database.
    // Contains the foreignKey, foreignTableName and localTableName used in
    // "INNER JOIN foreignTableName ON localTableName.foreignKey = foreignTableName.foreignKey".
    private static final HashMap<String, String> locationMap = new HashMap<>();
    private static final HashMap<String, String> storageLocationMap = new HashMap<>();
    private static final HashMap<String, String> medicalConditionMap = new HashMap<>();
    private static final HashMap<String, String> manufacturerMap = new HashMap<>();
    private static final HashMap<String, String> transporterMap = new HashMap<>();

    static {
        // locationMap, storageLocationMap and medicalConditionMap used for several local tables so their localTable is defined
        // in the method using the map, whereas manufacturerMap and transporterMap are only used once so their localTable can
        // be defined here.

        locationMap.put("foreignKey", "locationID");
        locationMap.put("foreignTableName", "Location");

        storageLocationMap.put("foreignKey", "storageLocationID");
        storageLocationMap.put("foreignTableName", "StorageLocation");

        medicalConditionMap.put("foreignKey", "medicalConditionID");
        medicalConditionMap.put("foreignTableName", "MedicalCondition");

        manufacturerMap.put("foreignKey", "manufacturerID");
        manufacturerMap.put("foreignTableName", "Manufacturer");
        manufacturerMap.put("localTableName", "Factory");

        transporterMap.put("foreignKey", "transporterID");
        transporterMap.put("foreignTableName", "Transporter");
        transporterMap.put("localTableName", "TransporterLocation");
    }

    public static HashMap<String, HashMap<String, Object>> readVaccines(VaccineSystem vaccineSystem) throws SQLException {
        String[] columnNames = {
            "Vaccine.vaccineID", "Vaccine.name", "Vaccine.dosesNeeded", "Vaccine.daysBetweenDoses", "Vaccine.minimumAge",
            "Vaccine.maximumAge"};

        HashMap<String, HashMap<String, Object>> vaccines = vaccineSystem.select(columnNames, "vaccine");

        // Add lifespan and exemption data for each vaccine
        if (vaccines != null) {
            for (String key : vaccines.keySet()) {
                String vaccineID = (String) vaccines.get(key).get("Vaccine.vaccineID");

                HashMap<String, HashMap<String, Object>> lifespans = readVaccineLifespans(vaccineSystem, vaccineID);
                HashMap<String, HashMap<String, Object>> exemptions = readVaccineExemptions(vaccineSystem, vaccineID);

                vaccines.get(key).put("lifespans", lifespans);
                vaccines.get(key).put("exemptions", exemptions);
            }
        }

        return vaccines;
    }

    public static HashMap<String, HashMap<String, Object>> readFactories(VaccineSystem vaccineSystem) throws SQLException {
        String[] columnNames = {
            "Factory.factoryID", "Factory.manufacturerID", "Manufacturer.manufacturerID", "Manufacturer.name",
            "Manufacturer.vaccineID", "Factory.vaccinesPerMin"};

        storageLocationMap.put("localTableName", "Factory");

        HashMap<String, String>[] innerJoins = new HashMap[] {manufacturerMap};

        return readStorageLocations(vaccineSystem, columnNames, "Factory", innerJoins);
    }

    public static HashMap<String, HashMap<String, Object>> readTransporterLocations(VaccineSystem vaccineSystem) throws SQLException {
        String[] columnNames = {"TransporterLocation.transporterLocationID", "TransporterLocation.transporterID", "Transporter.name"};

        locationMap.put("localTableName", "TransporterLocation");

        HashMap<String, String>[] innerJoins = new HashMap[] {transporterMap};

        return readLocations(vaccineSystem, columnNames, "TransporterLocation", innerJoins);
    }

    public static HashMap<String, HashMap<String, Object>> readDistributionCentres(VaccineSystem vaccineSystem) throws SQLException {
        String[] columnNames = {"DistributionCentre.distributionCentreID"};

        storageLocationMap.put("localTableName", "DistributionCentre");

        return readStorageLocations(vaccineSystem, columnNames, "DistributionCentre", new HashMap[] {});
    }

    public static HashMap<String, HashMap<String, Object>> readVaccinationCentres(VaccineSystem vaccineSystem) throws SQLException {
        String[] columnNames = {"VaccinationCentre.vaccinationCentreID", "VaccinationCentre.name", "VaccinationCentre.vaccinesPerHour"};

        storageLocationMap.put("localTableName", "VaccinationCentre");

        HashMap<String, HashMap<String, Object>> vaccinationCentres = readStorageLocations(vaccineSystem, columnNames, "VaccinationCentre", new HashMap[] {});

        // Add booking data for each vaccination centre
        if (vaccinationCentres != null) {
            for (String key : vaccinationCentres.keySet()) {
                String vaccinationCentreID = (String) vaccinationCentres.get(key).get("VaccinationCentre.vaccinationCentreID");

                HashMap<String, HashMap<String, Object>> bookings = readBookingsFromVaccinationCentreID(vaccineSystem, vaccinationCentreID);

                vaccinationCentres.get(key).put("bookings", bookings);
            }
        }

        return vaccinationCentres;
    }

    public static HashMap<String, HashMap<String, Object>> readPeople(VaccineSystem vaccineSystem) throws SQLException {
        String[] columnNames = {"Person.personID", "Person.forename", "Person.surname", "Person.DoB"};

        HashMap<String, HashMap<String, Object>> people = vaccineSystem.select(columnNames, "Person");

        // Add booking, medical condition and vaccines received data for each person
        if (people != null) {
            for (String key : people.keySet()) {
                String personID = (String) people.get(key).get("Person.personID");

                HashMap<String, HashMap<String, Object>> bookings = readBookingsFromPersonID(vaccineSystem, personID);
                HashMap<String, HashMap<String, Object>> medicalConditions = readPersonMedicalConditions(vaccineSystem, personID);
                HashMap<String, HashMap<String, Object>> vaccinesReceived = readVaccinesReceived(vaccineSystem, personID);

                people.get(key).put("bookings", bookings);
                people.get(key).put("medicalConditions", medicalConditions);
                people.get(key).put("vaccinesReceived", vaccinesReceived);
            }
        }
        return people;
    }

    public static HashMap<String, HashMap<String, Object>> readVans(VaccineSystem vaccineSystem) throws SQLException {
        String[] columnNames = {
            "Van.vanID", "Van.deliveryStage", "Van.totalTime", "Van.remainingTime", "Van.storageLocationID", "Van.originID",
            "Van.destinationID", "Van.transporterLocationID"};

        storageLocationMap.put("localTableName", "Van");

        return readStorageLocations(vaccineSystem, columnNames, "Van", new HashMap[] {});
    }

    public static HashMap<String, HashMap<String, Object>> readBookings(VaccineSystem vaccineSystem) throws SQLException {
        String[] columnNames = {"Booking.bookingID", "Booking.personID", "Booking.vaccinationCentreID", "Booking.date"};

        return vaccineSystem.select(columnNames, "Booking");
    }

    public static HashMap<String, HashMap<String, Object>> readSimulations(VaccineSystem vaccineSystem) throws SQLException {
        String[] columnNames = {"Simulation.simulationID", "Simulation.actualBookingRate", "Simulation.actualAttendanceRate", "Simulation.predictedVaccinationRate"};
        return vaccineSystem.select(columnNames, "Simulation");
    }

    public static HashMap<String, HashMap<String, Object>> readVaccinesInStorage(VaccineSystem vaccineSystem) throws SQLException {
        String[] columnNames = {
            "VaccineInStorage.vaccineInStorageID", "VaccineInStorage.vaccineID", "VaccineInStorage.stockLevel",
            "VaccineInStorage.creationDate", "VaccineInStorage.expirationDate"};

        return vaccineSystem.select(columnNames, "VaccineInStorage");
    }

    private static HashMap<String, HashMap<String, Object>> readVaccineLifespans(VaccineSystem vaccineSystem, String vaccineID) throws SQLException {
        String[] columnNames = {
            "VaccineLifespan.vaccineLifespanID", "VaccineLifespan.lifespan", "VaccineLifespan.lowestTemperature",
            "VaccineLifespan.highestTemperature"};

        return vaccineSystem.select(columnNames, "VaccineLifespan", "vaccineID = " + vaccineID);
    }

    private static HashMap<String, HashMap<String, Object>> readVaccineExemptions(VaccineSystem vaccineSystem, String vaccineID) throws SQLException {
        String[] columnNames = {"VaccineExemption.medicalConditionID", "MedicalCondition.name", "MedicalCondition.vulnerabilityLevel"};

        medicalConditionMap.put("localTableName", "VaccineExemption");

        HashMap<String, String>[] innerJoins = new HashMap[] {medicalConditionMap};

        return vaccineSystem.select(columnNames, "VaccineExemption", innerJoins, "vaccineID = " + vaccineID);
    }

    private static HashMap<String, HashMap<String, Object>> readStorageLocations(
        VaccineSystem vaccineSystem,
        String[] givenColumnNames,
        String tableName,
        HashMap[] givenInnerJoins
    ) throws SQLException {

        // Merge the given columnNames with additional ones required for the StorageLocation table
        String[] additionalColumnNames = {"StorageLocation.storageLocationID", "StorageLocation.locationID"};
        String[] columnNames = mergeColumnNames(givenColumnNames, additionalColumnNames);

        // Merge the given innerJoin maps with additional ones required for the StorageLocation table
        HashMap<String, String>[] additionalInnerJoins = new HashMap[] {storageLocationMap};
        HashMap<String, String>[] innerJoins = mergeInnerJoins(additionalInnerJoins, givenInnerJoins);

        locationMap.put("localTableName", "StorageLocation");

        HashMap<String, HashMap<String, Object>> storageLocations = readLocations(vaccineSystem, columnNames, tableName, innerJoins);

        // For all storageLocations, add all store data, and for each store add all vaccineInStorage data
        if (storageLocations != null) {
            for (String keyI : storageLocations.keySet()) {
                HashMap<String, HashMap<String, Object>> stores = readStores(vaccineSystem, (String) storageLocations.get(keyI).get("StorageLocation.storageLocationID"));

                for (String keyJ : stores.keySet()) {
                    String storeID = (String) stores.get(keyJ).get("Store.storeID");
                    HashMap<String, HashMap<String, Object>> vaccinesInStorage = readVaccinesInStorage(vaccineSystem, storeID);
                    stores.get(keyJ).put("vaccinesInStorage", vaccinesInStorage);
                }

                storageLocations.get(keyI).put("stores", stores);
            }
        }

        return storageLocations;
    }

    private static HashMap<String, HashMap<String, Object>> readLocations(
        VaccineSystem vaccineSystem,
        String[] originalColumnNames,
        String tableName,
        HashMap[] originalInnerJoins
    ) throws SQLException {

        // Merge the given columnNames with additional ones required for the StorageLocation table
        String[] additionalColumnNames = {"Location.locationID", "Location.longitude", "Location.latitude"};
        String[] columnNames = mergeColumnNames(originalColumnNames, additionalColumnNames);

        // Merge the given inner join maps with additional ones required for the StorageLocation table
        HashMap<String, String>[] additionalInnerJoins = new HashMap[] {locationMap};
        HashMap<String, String>[] innerJoins = mergeInnerJoins(originalInnerJoins, additionalInnerJoins);

        HashMap<String, HashMap<String, Object>> locations = vaccineSystem.select(columnNames, tableName, innerJoins);

        // Add opening times data for each location
        if (locations != null) {
            for (String key : locations.keySet()) {
                HashMap<String, HashMap<String, Object>> openingTimes = readOpeningTimes(vaccineSystem, (String) locations.get(key).get("Location.locationID"));
                locations.get(key).put("openingTimes", openingTimes);
            }
        }

        return locations;
    }

    private static HashMap<String, HashMap<String, Object>> readStores(VaccineSystem vaccineSystem, String storageLocationID) throws SQLException {
        String[] columnNames = {"Store.storeID", "Store.storageLocationID", "Store.temperature", "Store.capacity"};
        return vaccineSystem.select(columnNames, "Store", "storageLocationID = " + storageLocationID);
    }

    private static HashMap<String, HashMap<String, Object>> readVaccinesInStorage(VaccineSystem vaccineSystem, String storeID) throws SQLException {
        String[] columnNames = {
            "VaccineInStorage.vaccineInStorageID", "VaccineInStorage.vaccineID", "VaccineInStorage.stockLevel",
            "VaccineInStorage.creationDate", "VaccineInStorage.expirationDate"};

        return vaccineSystem.select(columnNames, "VaccineInStorage", "storeID = " + storeID);
    }

    private static HashMap<String, HashMap<String, Object>> readOpeningTimes(VaccineSystem vaccineSystem, String locationID) throws SQLException {
        String[] columnNames = {"OpeningTime.openingTimeID", "OpeningTime.day", "OpeningTime.startTime", "OpeningTime.endTime"};
        return vaccineSystem.select(columnNames, "OpeningTime", "locationID = " + locationID);
    }

    private static HashMap<String, HashMap<String, Object>> readBookingsFromVaccinationCentreID(VaccineSystem vaccineSystem, String vaccinationCentreID) throws SQLException {
        String[] columnNames = {"Booking.bookingID", "Booking.personID", "Booking.vaccinationCentreID", "Booking.date"};
        return vaccineSystem.select(columnNames, "Booking", "vaccinationCentreID = " + vaccinationCentreID);
    }

    private static HashMap<String, HashMap<String, Object>> readBookingsFromPersonID(VaccineSystem vaccineSystem, String personID) throws SQLException {
        String[] columnNames = {"Booking.bookingID", "Booking.personID", "Booking.vaccinationCentreID", "Booking.date"};
        return vaccineSystem.select(columnNames, "Booking", "personID = " + personID);
    }

    private static HashMap<String, HashMap<String, Object>> readPersonMedicalConditions(VaccineSystem vaccineSystem, String personID) throws SQLException {
        String[] columnNames = {"PersonMedicalCondition.personMedicalConditionID", "PersonMedicalCondition.personID", "PersonMedicalCondition.medicalConditionID"};
        return vaccineSystem.select(columnNames, "PersonMedicalCondition", "personID = " + personID);
    }

    private static HashMap<String, HashMap<String, Object>> readVaccinesReceived(VaccineSystem vaccineSystem, String personID) throws SQLException {
        String[] columnNames = {"VaccineReceived.vaccineReceivedID", "VaccineReceived.personID", "VaccineReceived.vaccineID", "VaccineReceived.date"};
        return vaccineSystem.select(columnNames, "VaccineReceived", "personID = " + personID);
    }

    // Merges two columnNames arrays into one array, separate from mergeColumnNames due to String and HashMap type differences

    /**
     * Merges two columnNames arrays into one array, separate from mergeColumnNames due to String and HashMap type differences
     *
     * @param arrayA array to be merged
     * @param arrayB array to be merged
     * @return merged arrays
     */
    private static String[] mergeColumnNames(String[] arrayA, String[] arrayB) {
        String[] arrayC = new String[arrayA.length + arrayB.length];
        System.arraycopy(arrayA, 0, arrayC, 0, arrayA.length);
        System.arraycopy(arrayB, 0, arrayC, arrayA.length, arrayB.length);
        return arrayC;
    }

    /**
     * Merges two inner join map arrays into one array, separate from mergeColumnNames due to String and HashMap type differences
     *
     * @param arrayA array to be merged
     * @param arrayB array to be merged
     * @return merged arrays
     */
    private static HashMap<String, String>[] mergeInnerJoins(HashMap<String, String>[] arrayA, HashMap<String, String>[] arrayB) {
        HashMap<String, String>[] arrayC = new HashMap[arrayA.length + arrayB.length];
        System.arraycopy(arrayA, 0, arrayC, 0, arrayA.length);
        System.arraycopy(arrayB, 0, arrayC, arrayA.length, arrayB.length);
        return arrayC;
    }
}
