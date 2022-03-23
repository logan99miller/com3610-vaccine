/**
 * Called by Core.AutomateSystem each time the system updates and when Data.Data is first initialized. Updates the data
 * in the database by removing data that should no longer be there before reading from the database and updates the systems
 * date and time.
 */
package Data;

import Core.ActivityLog;
import Core.VaccineSystem;

import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.HashMap;

import static Data.Read.*;
import static Data.Read.readVaccinationCentres;
import static Data.Utils.*;
import static Data.Write.writeMap;

public class Update {

    /**
     * Updates the data in the database by removing data that should no longer be there before reading from the database
     * and updates the systems date and time
     * @param activityLog needed to add to the activity log that vaccines have expired
     * @param vaccineSystem needed to modify the database
     * @param data needed to get the current date
     */
    public static void update(ActivityLog activityLog, VaccineSystem vaccineSystem, Data data) throws SQLException {
        updateDateAndTime(data);
        updateRates(data);
        removeInvalidVanReferences(vaccineSystem);
        removeEmptyVaccinesInStorage(vaccineSystem);
        removeExpiredStock(activityLog, vaccineSystem, data);
        processBookings(vaccineSystem, data);
    }

    /**
     * Updates the current date and time
     */
    private static void updateDateAndTime(Data data) {
        data.setCurrentDate(LocalDate.now());
        data.setCurrentTime(LocalTime.now());
    }

    /**
     * Goes through all vans and sets delivery stage to "waiting" if the originID or destinationID is not valid (occurs
     * if user deletes location during van delivery)
     * @param vaccineSystem needed to modify the database
     */
    private static void removeInvalidVanReferences(VaccineSystem vaccineSystem) throws SQLException {
        HashMap<String, HashMap<String, Object>> vans = readVans(vaccineSystem);

        // Allows us to iterate through all storage locations
        HashMap<String, HashMap<String, Object>> allStorageLocations = new HashMap<>();

        allStorageLocations = addStorageLocationToAllLocations(allStorageLocations, readFactories(vaccineSystem));
        allStorageLocations = addStorageLocationToAllLocations(allStorageLocations, readDistributionCentres(vaccineSystem));
        allStorageLocations = addStorageLocationToAllLocations(allStorageLocations, readVaccinationCentres(vaccineSystem));

        boolean originExists = false;
        boolean destinationExists = false;

        if (vans != null) {
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
    }

    /**
     * Checks the given storage locations are not null befor adding them to all storage locations
     */
    private static HashMap<String, HashMap<String, Object>> addStorageLocationToAllLocations(
        HashMap<String, HashMap<String, Object>> allStorageLocations, HashMap<String, HashMap<String, Object>> storageLocations
    ) {
        if (storageLocations != null) {
            allStorageLocations.putAll(storageLocations);
        }
        return allStorageLocations;
    }

    /**
     * Deletes vaccinesInStorage records in the database if they have a stock level of 0 or less. This occurs when a van
     * @param vaccineSystem needed to modify the database
     */
    private static void removeEmptyVaccinesInStorage(VaccineSystem vaccineSystem) throws SQLException {

        HashMap<String, HashMap<String, Object>> vaccinesInStorage = readVaccinesInStorage(vaccineSystem);

        if (vaccinesInStorage != null) {
            for (String key : vaccinesInStorage.keySet()) {
                HashMap<String, Object> vaccineInStorage = vaccinesInStorage.get(key);

                int stockLevel = Integer.parseInt((String) vaccineInStorage.get("VaccineInStorage.stockLevel"));

                if (stockLevel < 1) {
                    vaccineSystem.delete("vaccineInStorageID", key, "VaccineInStorage");
                }
            }
        }
    }

    /**
     * Deletes all stock from the database that have an expiration date before the current date
     * @param activityLog needed to add to the activity log that vaccines have expired
     * @param vaccineSystem needed to modify the database
     * @param data needed to get the current date
     */
    private static void removeExpiredStock(ActivityLog activityLog, VaccineSystem vaccineSystem, Data data) throws SQLException {
        LocalDate currentDate = data.getCurrentDate();

        HashMap<String, HashMap<String, Object>> vaccinesInStorage = readVaccinesInStorage(vaccineSystem);

        if (vaccinesInStorage != null) {
            for (String key : vaccinesInStorage.keySet()) {
                HashMap<String, Object> vaccineInStorage = vaccinesInStorage.get(key);

                LocalDate expirationDate = getLocalDate((String) vaccineInStorage.get("VaccineInStorage.expirationDate"));

                if (expirationDate.isBefore(currentDate)) {

                    String ID = (String) vaccineInStorage.get("VaccineInStorage.vaccineInStorageID");
                    String stockLevel = (String) vaccineInStorage.get("VaccineInStorage.stockLevel");

                    vaccineSystem.delete("vaccineInStorageID", ID, "VaccineInStorage");

                    activityLog.add(stockLevel + " vaccine(s) have expired and thrown away");
                }
            }
        }
    }

    /**
     * Iterates through all bookings and completes the appointment if the appointment is now in the past
     * @param vaccineSystem needed to modify the database
     * @param data needed to access the current time and date
     */
    private static void processBookings(VaccineSystem vaccineSystem, Data data) throws SQLException {

        LocalDate currentDate = data.getCurrentDate();
        LocalTime currentTime = data.getCurrentTime();

        HashMap<String, HashMap<String, Object>> vaccinationCentres = readVaccinationCentres(vaccineSystem);
        HashMap<String, HashMap<String, Object>> bookings = readBookings(vaccineSystem);

        if (bookings != null) {
            for (String key : bookings.keySet()) {
                HashMap<String, Object> booking = bookings.get(key);

                // In the format YYYY-MM-DD HH:MM:SS
                String dateTime = (String) booking.get("Booking.date");

                // In the format HH:MM:SS
                String time = dateTime.substring(dateTime.length() - 8);

                LocalDate bookingDate = getLocalDate(dateTime);
                LocalTime bookingTime = getLocalTime(time);

                if (hasAppointmentHappened(data, bookingDate, bookingTime, currentDate, currentTime)) {

                    String vaccinationCentreID = (String) booking.get("Booking.vaccinationCentreID");
                    HashMap<String, Object> vaccinationCentre = vaccinationCentres.get(vaccinationCentreID);

                    completeAppointment(vaccineSystem, booking, vaccinationCentre);
                }
            }
        }
    }

    /**
     * Determines if the appointment has happened or not be seeing if its date and time is before the current date and time and
     * if the appointment was attended based off the user defined attendance rate
     * @return true if the appointment has occurred and was attended, false otherwise
     */
    private static boolean hasAppointmentHappened(Data data, LocalDate bookingDate, LocalTime bookingTime, LocalDate currentDate, LocalTime currentTime) {
        boolean appointmentTimePassed = false;

        if (bookingDate.equals(currentDate)) {;
            if (bookingTime.isBefore(currentTime)) {
                appointmentTimePassed = true;
            }
        }
        else if (bookingDate.isBefore(currentDate)) {
            appointmentTimePassed = true;
        }

        // Return true if the appointment time has passed and the appointment was attended (based on a random number between
        // 0 and 1 and the attendance rate)
        if (appointmentTimePassed) {
            float attendanceRate = Float.parseFloat(data.getActualAttendanceRate());
            return (Math.random() < attendanceRate);
        }
        return false;
    }

    /**
     * If an appointment has happened this method is called to modify the database to reflect the completed appointment. This
     * involves deleting the booking, reducing the vaccination centre's stock levels and adding a record to note that the
     * person has received a vaccine.
     * @param vaccineSystem needed to modify the database
     * @param booking the booking which has been completed, in the format HashMap<columnName, databaseValue>
     * @param vaccinationCentre the vaccination centre the appointment occured at, in the format HashMap<columnName, databaseValue>
     */
    private static void completeAppointment(VaccineSystem vaccineSystem, HashMap<String, Object> booking, HashMap<String, Object> vaccinationCentre) throws SQLException {
        String bookingID = (String) booking.get("Booking.bookingID");

        HashMap<String, Object> bestVaccineInStorage = getBestVaccinesInStorage(vaccinationCentre);

        vaccineSystem.delete("bookingID", bookingID, "Booking");

        reduceStockLevels(vaccineSystem, bestVaccineInStorage);
        addVaccineReceived(vaccineSystem, booking, bestVaccineInStorage);
    }

    /**
     * Reduces the stock levels of the given vaccineInStorage by 1 to represent that an inoculation has occured
     * @param vaccineSystem needed to modify the database
     * @param vaccineInStorage the vaccineInStorage used to perform the inoculation, in the format HashMap<columnName, databaseValue>
     */
    private static void reduceStockLevels(VaccineSystem vaccineSystem, HashMap<String, Object> vaccineInStorage) throws SQLException {

        int stockLevel = Integer.parseInt((String) vaccineInStorage.get("VaccineInStorage.stockLevel"));

        String[] columnNames = {"stockLevel"};
        Object[] values = new Object[] {stockLevel - 1};

        String vaccineInStorageID = (String) vaccineInStorage.get("VaccineInStorage.vaccineInStorageID");
        String where = "vaccineInStorageID = " + vaccineInStorageID;

        vaccineSystem.update(columnNames, values, "VaccineInStorage", where);
    }

    /**
     * Adds to the VaccineReceived table to store the details of the vaccination
     * @param vaccineSystem needed to modify the database
     * @param booking the booking which has been completed, in the format HashMap<columnName, databaseValue>
     * @param vaccineInStorage the vaccineInStorage used to perform the inoculation, in the format HashMap<columnName, databaseValue>
     */
    private static void addVaccineReceived(VaccineSystem vaccineSystem, HashMap<String, Object> booking, HashMap<String, Object> vaccineInStorage) throws SQLException {
        String personID = (String) booking.get("Booking.personID");
        String vaccineID = (String) vaccineInStorage.get("VaccineInStorage.vaccineID");
        String date = (String) booking.get("Booking.date");

        String[] columnNames = {"personID", "vaccineID", "date"};
        Object[] values = {personID, vaccineID, date};
        vaccineSystem.insert(columnNames, values, "VaccineReceived");
    }

    /**
     * Finds the vaccines in the storage location that will expire soonest, and return the vaccineStorage hashmap
     * representing them.
     * @param storageLocation the storage location to find the best vaccines in
     * @return the vaccines will expire the soonest, in the format HashMap<columnName, databaseValue>
     */
    private static HashMap<String, Object> getBestVaccinesInStorage(HashMap<String, Object> storageLocation) {
        HashMap<String, HashMap<String, Object>> allVaccinesInStorage = getAllVaccinesInStorage(storageLocation);

        HashMap<String, Object> bestVaccineInStorage = new HashMap<>();

        // Set an arbitrarily far away expiration date so the 1st expiration date is marked as the soonest
        LocalDate earliestExpirationDate = LocalDate.of(4000, 01, 01);

        for (String key : allVaccinesInStorage.keySet()) {

            HashMap<String, Object> vaccineInStorage = allVaccinesInStorage.get(key);
            LocalDate expirationDate = getLocalDate((String) vaccineInStorage.get("VaccineInStorage.expirationDate"));

            if (expirationDate.isBefore(earliestExpirationDate)) {
                bestVaccineInStorage = vaccineInStorage;
                earliestExpirationDate = expirationDate;
            }
        }

        return bestVaccineInStorage;
    }

    /**
     * Updates the simulation rates in the system by reading from the simulation table that they are stored in so that
     * they are stored even if the program crashes
     */
    public static void updateRates(Data data) {
        final String DEFAULT_RATE = "0.5";

        try {
            HashMap<String, HashMap<String, Object>> simulations = data.getSimulations();
            String simulationKey = simulations.keySet().iterator().next();
            HashMap<String, Object> simulation = simulations.get(simulationKey);

            data.setActualBookingRate((String) simulation.get("Simulation.actualBookingRate"));
            data.setActualAttendanceRate((String) simulation.get("Simulation.actualAttendanceRate"));
            data.setPredictedVaccinationRate((String) simulation.get("Simulation.predictedVaccinationRate"));
        }
        catch (Exception e) {
            data.setActualBookingRate(DEFAULT_RATE);
            data.setActualAttendanceRate(DEFAULT_RATE);
            data.setPredictedVaccinationRate(DEFAULT_RATE);
        }
    }
}