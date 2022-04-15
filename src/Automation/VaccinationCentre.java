/**
 * Orders the needed amount of vaccines for all vaccination centres
 */
package Automation;

import Core.ActivityLog;
import Core.AutomateSystem;
import Data.Data;

import java.util.HashMap;

import static Automation.Availability.getAvailabilities;
import static Automation.People.getBookablePeople;

public class VaccinationCentre extends DeliveryLocation {

    /**
     * Orders the needed amount of vaccines for all vaccination centres
     * @param automateSystem used to access the activity log and data in the data class
     */
    public static void orderVaccines(AutomateSystem automateSystem) {
        Data data = automateSystem.getData();
        ActivityLog activityLog = automateSystem.getActivityLog();

        HashMap<String, HashMap<String, Integer>> availabilities = automateSystem.getAvailabilities();
        HashMap<String, HashMap<String, Object>> bookablePeople = automateSystem.getBookablePeople();

        HashMap<String, HashMap<String, Object>> distributionCentres = data.getDistributionCentres();
        HashMap<String, HashMap<String, Object>> vaccinationCentres = data.getVaccinationCentres();
        HashMap<String, HashMap<String, Object>> vans = data.getVans();

        float predictedVaccinationRate = Float.parseFloat(data.getPredictedVaccinationRate());

        int totalVaccinesPerHour = getTotalVaccinesPerHour(vaccinationCentres);
        int totalCapacity = getTotalCapacity(vaccinationCentres);

        if (distributionCentres.size() == 0) {
            activityLog.add("No distribution centres so vaccination centre cannot order more vaccines", true);
        }
        else if (vans.size() == 0) {
            activityLog.add("No vans so vaccination centre cannot order more vaccines", true);
        }

        else {
            for (String key : vaccinationCentres.keySet()) {
                HashMap<String, Object> vaccinationCentre = vaccinationCentres.get(key);

                int vaccinesNeeded = getVaccinesNeeded(vaccinationCentre, availabilities, bookablePeople, vans, totalVaccinesPerHour, totalCapacity, predictedVaccinationRate);
                String vaccineID = "1";
                vans = orderVaccine(activityLog, distributionCentres, vaccinationCentre, vans, vaccinesNeeded, vaccineID);
                data.setVans(vans);
            }
        }
    }

    /**
     * Gets the number of vaccines the given vaccination centre needs to fulfill the needs of the vaccination centre for the
     * next week, based of a predicted vaccination rate and the vaccination centre's current number of vaccines
     * @param vaccinationCentre the vaccination centre which may need more vaccines
     * @param availabilities how many people have already booked vaccines, in the format
     *                       HashMap<vaccinationCentreID, HashMap<Hour, currentNumberOfAppointments>>
     * @param bookablePeople everyone eligible to be booked, in the format HashMap<personID, HashMap<columnName, databaseValue>>
     * @param totalVaccinesPerHour the number of vaccinations all vaccination centres can perform each hour
     * @param totalCapacity the storage capacity of all vaccination centres
     * @param predictedVaccinationRate the predicted percent of people who will get vaccinated from 0 to 1
     * @return the number of vaccines the given vaccination centre needs to order
     */
    public static int getVaccinesNeeded(
        HashMap<String, Object> vaccinationCentre, HashMap<String, HashMap<String, Integer>> availabilities,
        HashMap<String, HashMap<String, Object>> bookablePeople, HashMap<String, HashMap<String, Object>> vans,
        int totalVaccinesPerHour, int totalCapacity, float predictedVaccinationRate
    ) {

        int vaccinesPerHour = Integer.parseInt((String) vaccinationCentre.get("VaccinationCentre.vaccinesPerHour"));
        int totalStock = getTotalStockInStorageLocation(vaccinationCentre, vans, null);

        int expectedBookings = getExpectedBookings(vaccinesPerHour, totalStock, bookablePeople.size(), totalVaccinesPerHour, totalCapacity, predictedVaccinationRate);

        String vaccinationCentreID = (String) vaccinationCentre.get("VaccinationCentre.vaccinationCentreID");
        HashMap<String, Integer> availability = availabilities.get(vaccinationCentreID);

        int numberOfBookings = getNumberOfBookings(availability);
        int numberOfUnbookedPlaces = getNumberOfUnbookedPlaces(availability, vaccinesPerHour, numberOfBookings);

        if (expectedBookings > numberOfUnbookedPlaces) {
            expectedBookings = numberOfUnbookedPlaces;
        }

        // How many vaccines will be needed this week
        int demand = expectedBookings + numberOfBookings;
        System.out.println("VC demand & totalStock: " + demand + ", " + totalStock);

        if (demand > totalStock) {
            return demand - totalStock;
        }
        return 0;
    }

    /**
     * Gets the number of people who have already booked a slot at this vaccination centre this week
     * @param availability how many people have already booked vaccines, in the format HashMap<Hour, currentNumberOfAppointments>
     * @return the number of bookings
     */
    private static int getNumberOfBookings(HashMap<String, Integer> availability) {
        int numberOfBookings = 0;

        for (String key : availability.keySet()) {
            numberOfBookings += availability.get(key);
        }
        return numberOfBookings;
    }

    /**
     * The number of available appointments for a given vaccination centre this week
     * @param availability how many people have already booked vaccines, in the format HashMap<Hour, currentNumberOfAppointments>
     *                     Used to get the number of hours the vaccination centre is open for
     * @param vaccinesPerHour the number of vaccinations the vaccination centre can perform each hour
     * @param numberOfBookings the number of people who have already booked a slot at this vaccination centre this week
     * @return the number of available appointments
     */
    private static int getNumberOfUnbookedPlaces(HashMap<String, Integer> availability, int vaccinesPerHour, int numberOfBookings) {
        int numberOfPlaces = 0;
        for (String ignored : availability.keySet()) {
            numberOfPlaces += vaccinesPerHour;
        }
        return (numberOfPlaces - numberOfBookings);
    }

    /**
     * Gets the number of expected bookings for a given vaccination centre based off the given variables
     * @param vaccinesPerHour the number of vaccinations the given vaccination centre can perform each hour
     * @param capacity the storage capacity of the given vaccination centre
     * @param numberOfBookablePeople the total number of people who are eligible to book
     * @param totalVaccinesPerHour the number of vaccinations all vaccination centres can perform each hour
     * @param totalCapacity the storage capacity of all vaccination centres
     * @param predictedVaccinationRate the predicted percent of people who will get vaccinated from 0 to 1
     * @return the expected bookings for a given vaccination centre
     */
    private static int getExpectedBookings(int vaccinesPerHour, int capacity, int numberOfBookablePeople, int totalVaccinesPerHour, int totalCapacity, float predictedVaccinationRate) {
        float proportionOfCapacity = capacity / totalCapacity;
        float proportionOfVaccinesPerHour = vaccinesPerHour / totalVaccinesPerHour;

        float proportionOfBookings = (proportionOfCapacity + proportionOfVaccinesPerHour) / 2;
        return Math.round(proportionOfBookings * numberOfBookablePeople * predictedVaccinationRate);
    }
}
