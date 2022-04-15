/**
 * Manages the data in the system. Data is read from the database, stored in the system as a hashmap of hashmaps in the
 * format HashMap<primaryKeyValue, HashMap<columName, databaseValue>> (representing HashMap<key, value>), accessed and
 * manipulated and then written back to the database. This is repeated regularly (defined by the updateRate in vaccineSystem)
 * to prevent data loss if the system crashes.
 */
package Data;

import Core.ActivityLog;
import Core.VaccineSystem;

import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.HashMap;

import static Data.Read.*;
import static Data.Write.*;
import static Data.Update.*;

public class Data {

    private LocalDate currentDate;
    private LocalTime currentTime;
    private final VaccineSystem vaccineSystem;
    private String actualBookingRate, actualAttendanceRate, predictedVaccinationRate;
    private HashMap<String, HashMap<String, Object>> vaccines, factories, transporterLocations, distributionCentres,
        vaccinationCentres, people, vans, bookings, simulations;


    public Data(VaccineSystem vaccineSystem) {
        this.vaccineSystem = vaccineSystem;

        try {
            ActivityLog activityLog = vaccineSystem.getActivityLog();
            currentDate = LocalDate.now();
            currentTime = LocalTime.now();
            update(activityLog, vaccineSystem, this);
            read();
        }
        catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Reads all relevant data from the database and stores it in hashmaps of hashmaps in the format
     * HashMap<primaryKeyValue, HashMap<columName, databaseValue>> (representing HashMap<key, value>) using the Data.Read class
     * @throws SQLException
     */
    public void read() throws SQLException {
        vaccines = readVaccines(vaccineSystem);
        factories = readFactories(vaccineSystem);
        transporterLocations = readTransporterLocations(vaccineSystem);
        distributionCentres = readDistributionCentres(vaccineSystem);
        vaccinationCentres = readVaccinationCentres(vaccineSystem);
        people = readPeople(vaccineSystem);
        vans = readVans(vaccineSystem);
        bookings = readBookings(vaccineSystem);
        simulations = readSimulations(vaccineSystem);
    }

    /**
     * Writes the hashmaps to the database using the Data.Write class
     * @throws SQLException
     */
    public void write() throws SQLException {

        writeMaps(vaccineSystem, vaccines);
        writeMaps(vaccineSystem, factories);
        writeMaps(vaccineSystem, transporterLocations);
        writeMaps(vaccineSystem, distributionCentres);
        writeMaps(vaccineSystem, vaccinationCentres);
        writeMaps(vaccineSystem, people);
        writeMaps(vaccineSystem, vans);
        writeMaps(vaccineSystem, bookings);
        writeMaps(vaccineSystem, simulations);
    }

    // Getters and setters

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

    public HashMap<String, HashMap<String, Object>> getBookings() {
        return bookings;
    }

    public void setBookings(HashMap<String, HashMap<String, Object>> bookings) {
        this.bookings = bookings;
    }

    public HashMap<String, HashMap<String, Object>> getSimulations() {
        return simulations;
    }

    public String getActualBookingRate() {
        return actualBookingRate;
    }

    public void setActualBookingRate(String actualBookingRate) {
        this.actualBookingRate = actualBookingRate;
    }

    public String getActualAttendanceRate() {
        return actualAttendanceRate;
    }

    public void setActualAttendanceRate(String actualAttendanceRate) {
        this.actualAttendanceRate = actualAttendanceRate;
    }

    public String getPredictedVaccinationRate() {
        return predictedVaccinationRate;
    }

    public void setPredictedVaccinationRate(String predictedVaccinationRate) {
        this.predictedVaccinationRate = predictedVaccinationRate;
    }

    public void setSimulations(HashMap<String, HashMap<String, Object>> simulations) {
        this.simulations = simulations;
    }
}