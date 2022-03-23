/**
 * Orders the needed amount of vaccines for all distribution centres
 */
package Automation;

import Core.ActivityLog;
import Core.AutomateSystem;
import Data.Data;

import java.util.HashMap;

import static Automation.Availability.*;
import static Automation.Distance.*;
import static Automation.People.*;

public class DistributionCentre extends DeliveryLocation {

    /**
     * Orders the needed amount of vaccines for all distribution centres
     * @param automateSystem used to access the activity log and data in the data class
     */
    public static void orderVaccines(AutomateSystem automateSystem) {
        Data data = automateSystem.getData();
        ActivityLog activityLog = automateSystem.getActivityLog();

        HashMap<String, HashMap<String, Integer>> availabilities = automateSystem.getAvailabilities();
        HashMap<String, HashMap<String, Object>> bookablePeople = automateSystem.getBookablePeople();

        HashMap<String, HashMap<String, Object>> factories = data.getFactories();
        HashMap<String, HashMap<String, Object>> vaccinationCentres = data.getVaccinationCentres();
        HashMap<String, HashMap<String, Object>> distributionCentres = data.getDistributionCentres();
        HashMap<String, HashMap<String, Object>> vans = data.getVans();

        double totalDistance = getTotalDistance(distributionCentres, vaccinationCentres);
        int totalVaccinesNeeded = getTotalVaccinesNeeded(data);
        int totalVaccinesPerHour = getTotalVaccinesPerHour(vaccinationCentres);
        int totalCapacity = getTotalCapacity(vaccinationCentres);

        if (factories.size() == 0) {
            activityLog.add("No factories so distribution centres cannot order more vaccines", true);
        }
        else if (vans.size() == 0) {
            activityLog.add("No vans so distribution centres cannot order more vaccines", true);
        }
        else {
            for (String key : distributionCentres.keySet()) {
                HashMap<String, Object> distributionCentre = distributionCentres.get(key);

                int vaccinesNeeded = getVaccinesNeeded(
                    data, distributionCentre, availabilities, bookablePeople, totalVaccinesPerHour, totalCapacity, totalDistance, totalVaccinesNeeded
                );

                String vaccineID = "1"; // NEEDS TO BE BASED OF A FUNCTION IN FUTURE
                vans = orderVaccine(activityLog, factories, distributionCentre, vans, vaccinesNeeded, vaccineID);
                data.setVans(vans);
            }
        }
    }

    /**
     * Gets the number of vaccines the given distribution centre needs to fulfill the needs of the nearby vaccination centres
     * for the next week, based off the predicted needs of nearby vaccination centres and how much of their needs need to be
     * met by the given dsitribution centre
     * @param data
     * @param distributionCentre the distribution centre that needs more vaccines
     * @param availabilities how many people have already booked vaccines, in the format
     *                       HashMap<vaccinationCentreID, HashMap<Hour, currentNumberOfAppointments>>
     * @param bookablePeople everyone eligible to be booked, in the format HashMap<personID, HashMap<columnName, databaseValue>>
     * @param totalVaccinesPerHour the number of vaccinations all vaccination centres can perform each hour
     * @param totalCapacity the storage capacity of all vaccination centres
     * @param totalDistance distance between all vaccination centres and all distribution centres. Used to help determine
     *                      the proportion of total vaccination centre demand each distribution centre should be fulfilling
     * @param totalVaccinesNeeded the number of vaccines needed to be ordered for all vaccination centres to fulfill all current and
     *                            predicted bookings for the next week. Used to help determine the demand from each vaccination centre.
     * @return the number of vaccines the given distribution centre needs
     */
    public static int getVaccinesNeeded(
        Data data, HashMap<String, Object> distributionCentre, HashMap<String, HashMap<String, Integer>> availabilities,
        HashMap<String, HashMap<String, Object>> bookablePeople, int totalVaccinesPerHour, int totalCapacity,
        double totalDistance, int totalVaccinesNeeded
    ) {

        HashMap<String, HashMap<String, Object>> vaccinationCentres = data.getVaccinationCentres();

        int demand = 0;
        int capacity = getCapacity(distributionCentre);

        for (String key : vaccinationCentres.keySet()) {
            HashMap<String, Object> vaccinationCentre = vaccinationCentres.get(key);

            getDemandFromVaccinationCentre(
                data, vaccinationCentre, distributionCentre, availabilities, bookablePeople, totalVaccinesPerHour,
                totalCapacity, totalDistance, totalVaccinesNeeded
            );
        }

        if (demand > capacity) {
            return demand - capacity;
        }
        return 0;
    }

    /**
     * Uses the amount of vaccines the given vaccination centre will demand from the given distribution centre based off
     * the number of vaccines the vaccination centre will need to order to fulfill current and predicted bookings for the next
     * week and what proportion of these vaccines the given distribution centre needs to fulfill (based off its distance from
     * the vaccination centre, and it's total capacity (assuming larger distribution centres should be used more than smaller ones)
     * @param data used to get the user provided predicted vaccination rate
     * @param vaccinationCentre the vaccination centre that is demanding vaccines
     * @param distributionCentre the distribution centre who is being demanded from
     * @param availabilities how many people have already booked vaccines, in the format
     *                       HashMap<vaccinationCentreID, HashMap<Hour, currentNumberOfAppointments>>
     * @param bookablePeople everyone eligible to be booked, in the format HashMap<personID, HashMap<columnName, databaseValue>>
     * @param totalVaccinesPerHour the number of vaccinations all vaccination centres can perform each hour
     * @param totalCapacity the storage capacity of all vaccination centres
     * @param totalDistance distance between all vaccination centres and all distribution centres. Used to help determine
     *                      the proportion of total vaccination centre demand each distribution centre should be fulfilling
     * @param totalVaccinesNeeded the number of vaccines needed to be ordered for all vaccination centres to fulfill all current and
     *                            predicted bookings for the next week. Used to help determine the demand from each vaccination centre.
     * @return the amount of vaccines the given vaccination centre will demand from the given distribution centre
     */
    private static int getDemandFromVaccinationCentre(
        Data data, HashMap<String, Object> vaccinationCentre, HashMap<String, Object> distributionCentre,
        HashMap<String, HashMap<String, Integer>> availabilities, HashMap<String, HashMap<String, Object>> bookablePeople,
        int totalVaccinesPerHour, int totalCapacity, double totalDistance, int totalVaccinesNeeded
    ) {

        // How many vaccines we need beyond the current vaccination centres needs
        // Useful to prevent interruptions in the flow of supplys
        final float DEMAND_MULTIPLIER = 1.5f;

        float predictedVaccinationRate = Float.parseFloat(data.getPredictedVaccinationRate());

        double distance = getDistance(vaccinationCentre, distributionCentre);

        int vaccinesNeeded = VaccinationCentre.getVaccinesNeeded(
            vaccinationCentre, availabilities, bookablePeople, totalVaccinesPerHour, totalCapacity, predictedVaccinationRate
        );

        double proportionOfDistance = distance / totalDistance;

        double proportionOfVaccinesNeeded;
        if (totalVaccinesNeeded == 0) {
            proportionOfVaccinesNeeded = 0;
        }
        else {
            proportionOfVaccinesNeeded = vaccinesNeeded / totalVaccinesNeeded;
        }

        // The proportion of vaccines the given vaccination centre needs that the given distribution centre should provide
        double proportionOfVaccines = (proportionOfDistance + proportionOfVaccinesNeeded) / 2;

        int demand = (int) Math.round(proportionOfVaccines * totalVaccinesNeeded * DEMAND_MULTIPLIER);

        return demand;
    }

    /**
     * The total distance between all vaccination centres and all distribution centres. Used to help determine the proportion of
     * total vaccination centre demand each distribution centre should be fulfilling
     * @param vaccinationCentres all vaccination centres, in the format HashMap<vaccinationCentreID, HashMap<columnName, databaseValue>
     * @param distributionCentres all distribution centres, in the format HashMap<distributionCentreID, HashMap<columnName, databaseValue>
     * @return the total distance
     */
    private static double getTotalDistance(HashMap<String, HashMap<String, Object>> vaccinationCentres, HashMap<String, HashMap<String, Object>> distributionCentres) {
        double totalDistance = 0;
        for (String keyI : vaccinationCentres.keySet()) {
            HashMap<String, Object> vaccinationCentre = vaccinationCentres.get(keyI);
            for (String keyJ : distributionCentres.keySet()) {
                HashMap<String, Object> distributionCentre = distributionCentres.get(keyJ);
                totalDistance += getDistance(vaccinationCentre, distributionCentre);
            }
        }
        return totalDistance;
    }

    /**
     * Gets the total number of vaccines needed to be ordered for all vaccination centres to fulfill all current and predicted
     * bookings for the next week. Used to help determine the demand from each vaccination centre.
     * @param data used to calculate each vaccination centre's availability and the number of people eligible to book
     * @return the total number of vaccines needed
     */
    private static int getTotalVaccinesNeeded(Data data) {
        HashMap<String, HashMap<String, Object>> vaccinationCentres = data.getVaccinationCentres();

        HashMap<String, HashMap<String, Integer>> availabilities = getAvailabilities(data);
        HashMap<String, HashMap<String, Object>> bookablePeople = getBookablePeople(data);

        int totalVaccinesPerHour = getTotalVaccinesPerHour(vaccinationCentres);
        int totalCapacity = getTotalCapacity(vaccinationCentres);

        float predictedVaccinationRate = Float.parseFloat(data.getPredictedVaccinationRate());

        int totalVaccinesNeeded = 0;

        for (String keyI : vaccinationCentres.keySet()) {
            HashMap<String, Object> vaccinationCentre = vaccinationCentres.get(keyI);

            totalVaccinesNeeded += VaccinationCentre.getVaccinesNeeded(
                vaccinationCentre, availabilities, bookablePeople, totalVaccinesPerHour, totalCapacity, predictedVaccinationRate
            );
        }

        return totalVaccinesNeeded;
    }
}