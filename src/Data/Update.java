/**
 * Called by Core.AutomateSystem each time the system updates and when Data.Data is first initialized. Updates the data
 * in the database by removing data that should no longer be there before reading from the database and updates the systems
 * date and time.
 */
package Data;

import Core.VaccineSystem;

import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.HashMap;

import static Data.Read.*;
import static Data.Read.readVaccinationCentres;
import static Data.Utils.getLocalDate;
import static Data.Write.writeMap;

public class Update {

    /**
     * Updates the data in the database by removing data that should no longer be there before reading from the database
     * and updates the systems date and time
     */
    public static void update(VaccineSystem vaccineSystem, Data data) throws SQLException {
        HashMap<String, HashMap<String, Object>> vans = data.getVans();

        updateDateAndTime(data);
        removeInvalidVanReferences(vaccineSystem);
//        removeEmptyVaccinesInStorage(vaccineSystem, vans);
        removeExpiredStock(vaccineSystem, data);
        removePastBookings(vaccineSystem, data);
    }

    /**
     * Updates the current date and time
     */
    private static void updateDateAndTime(Data data) {
        data.setCurrentDate(LocalDate.now());
        data.setCurrentTime(LocalTime.of(11, 0));
    }

    /**
     * Goes through all vans and sets delivery stage to "waiting" if the originID or destinationID is not valid (occurs
     * if user deletes location during van delivery)
     */
    private static void removeInvalidVanReferences(VaccineSystem vaccineSystem) throws SQLException {
        HashMap<String, HashMap<String, Object>> vans = readVans(vaccineSystem);

        // Allows us to iterate through all storage locations
        HashMap<String, HashMap<String, Object>> allStorageLocations = new HashMap<>();
        allStorageLocations.putAll(readFactories(vaccineSystem));
        allStorageLocations.putAll(readDistributionCentres(vaccineSystem));
        allStorageLocations.putAll(readVaccinationCentres(vaccineSystem));

        boolean originExists = false;
        boolean destinationExists = false;

        for (String keyI : vans.keySet()) {
            HashMap<String, Object> van = vans.get(keyI);

            String originID = (String) van.get("Van.originID");
            String destinationID = (String) van.get("Van.destinationID");

            for (String keyJ : allStorageLocations.keySet()) {

                HashMap<String, Object> storageLocation = allStorageLocations.get(keyJ);
                String locationID = (String) storageLocation.get("Location.locationID");

                if (locationID.equals(originID)) {
                    originExists = true;
                }
                if (locationID.equals(destinationID)) {
                    destinationExists = true;
                }
            }

            // If either origin or destination does not exist, change van's delivery stage
            if ((!originExists) || (!destinationExists)) {
                van.put("Van.deliveryStage", "waiting");
                van.put("Van.change", "change");
                writeMap(vaccineSystem, van);
            }
        }
    }

    // METHOD NOT TESTED
//    private static void removeEmptyVaccinesInStorage(VaccineSystem vaccineSystem, HashMap<String, HashMap<String, Object>> storageLocations) throws SQLException {
//        System.out.println("removeEmptyVaccinesInStorage():");
//        for (String keyI : storageLocations.keySet()) {
//            HashMap<String, Object> storageLocation = storageLocations.get(keyI);
//            HashMap<String, HashMap<String, Object>> stores = (HashMap<String, HashMap<String, Object>>) storageLocation.get("stores");
//            for (String keyJ : stores.keySet()) {
//                HashMap<String, Object> store = stores.get(keyJ);
//                HashMap<String, HashMap<String, Object>> vaccinesInStorage = (HashMap<String, HashMap<String, Object>>) store.get("vaccinesInStorage");
//                System.out.println("VaccinesInStorage: " + vaccinesInStorage);
//                if (vaccinesInStorage == null) {
//                    vaccineSystem.delete("vaccineInStorageID", keyJ, "VaccineInStorage");
//                }
//            }
//        }
//    }

    /**
     * Deletes all stock from the database that have an expiration date before the current date
     */
    private static void removeExpiredStock(VaccineSystem vaccineSystem, Data data) throws SQLException {
        LocalDate currentDate = data.getCurrentDate();

        HashMap<String, HashMap<String, Object>> vaccinesInStorage = readVaccinesInStorage(vaccineSystem);

        for (String key : vaccinesInStorage.keySet()) {
            LocalDate expirationDate = getLocalDate((String) vaccinesInStorage.get(key).get("VaccineInStorage.expirationDate"));

            if (expirationDate.isBefore(currentDate)) {
                String ID = (String) vaccinesInStorage.get(key).get("VaccineInStorage.vaccineInStorageID");
                vaccineSystem.delete("vaccineInStorageID", ID, "VaccineInStorage");
                // In future should also add to activity log
            }
        }
    }

    /**
     * Deletes all bookings from the database that have a date before the current date
     */
    private static void removePastBookings(VaccineSystem vaccineSystem, Data data) throws SQLException {
        LocalDate currentDate = data.getCurrentDate();

        HashMap<String, HashMap<String, Object>> bookings = readBookings(vaccineSystem);

        for (String key : bookings.keySet()) {
            LocalDate bookingDate = getLocalDate((String) bookings.get(key).get("Booking.date"));

            if (bookingDate.isBefore(currentDate)) {
                String ID = (String) bookings.get(key).get("Booking.bookingID");
                vaccineSystem.delete("bookingID", ID, "Booking");
                // In future should also add to activity log
            }
        }
    }
}