package Automation;

import Core.ActivityLog;
import Core.AutomateSystem;
import Data.Data;

import java.util.HashMap;

import static Automation.Availability.getAvailabilities;
import static Automation.People.getBookablePeople;

public class VaccinationCentre extends DeliveryLocation {

//    public static void orderVaccines(ActivityLog activityLog, Data data) {
    public static void orderVaccines(AutomateSystem automateSystem) {
        Data data = automateSystem.getData();
        ActivityLog activityLog = automateSystem.getActivityLog();

        HashMap<String, HashMap<String, Integer>> availabilities = automateSystem.getAvailabilities();
        HashMap<String, HashMap<String, Object>> bookablePeople = automateSystem.getBookablePeople();

        HashMap<String, HashMap<String, Object>> distributionCentres = data.getDistributionCentres();
        HashMap<String, HashMap<String, Object>> vaccinationCentres = data.getVaccinationCentres();
        HashMap<String, HashMap<String, Object>> vans = data.getVans();

        float predictedVaccinationRate = Float.parseFloat(data.getPredictedVaccinationRate());

//        HashMap<String, HashMap<String, Integer>> availabilities = getAvailabilities(data);
//        HashMap<String, HashMap<String, Object>> bookablePeople = getBookablePeople(data);

        int totalVaccinesPerHour = getTotalVaccinesPerHour(vaccinationCentres);
        int totalCapacity = getTotalCapacity(vaccinationCentres, vans);

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
                String vaccineID = "1"; // NEEDS TO BE BASED OF A FUNCTION IN FUTURE
                vans = orderVaccine(activityLog, distributionCentres, vaccinationCentre, vans, vaccinesNeeded, vaccineID);
                data.setVans(vans);
            }
        }
    }

    public static int getVaccinesNeeded(
        HashMap<String, Object> vaccinationCentre,
        HashMap<String, HashMap<String, Integer>> availabilities,
        HashMap<String, HashMap<String, Object>> bookablePeople,
        HashMap<String, HashMap<String, Object>> vans,
        int totalVaccinesPerHour,
        int totalCapacity,
        float predictedVaccinationRate
    ) {

        int vaccinesPerHour = Integer.parseInt((String) vaccinationCentre.get("VaccinationCentre.vaccinesPerHour"));
        int capacity = getCapacityIncludingVans(vaccinationCentre, vans);

        int expectedBookings = getExpectedBookings(vaccinesPerHour, capacity, bookablePeople.size(), totalVaccinesPerHour, totalCapacity, predictedVaccinationRate);

        String vaccinationCentreID = (String) vaccinationCentre.get("VaccinationCentre.vaccinationCentreID");
        HashMap<String, Integer> availability = availabilities.get(vaccinationCentreID);

        int numberOfBookings = getNumberOfBookings(availability);
        int numberOfUnbookedPlaces = getNumberOfUnbookedPlaces(availability, vaccinesPerHour, numberOfBookings);

        if (expectedBookings > numberOfUnbookedPlaces) {
            expectedBookings = numberOfUnbookedPlaces;
        }

        int demand = expectedBookings + numberOfBookings;

        if (demand > capacity) {
            return demand - capacity;
        }
        return 0;
    }

    private static int getNumberOfBookings(HashMap<String, Integer> availability) {
        int numberOfBookings = 0;
        for (String key : availability.keySet()) {
            numberOfBookings += availability.get(key);
        }
        return numberOfBookings;
    }

    private static int getNumberOfUnbookedPlaces(HashMap<String, Integer> availability, int vaccinesPerHour, int numberOfBookings) {
        int numberOfPlaces = 0;
        for (String ignored : availability.keySet()) {
            numberOfPlaces += vaccinesPerHour;
        }
        return (numberOfPlaces - numberOfBookings);
    }

    // For a given vaccination centre
    private static int getExpectedBookings(int vaccinesPerHour, int capacity, int numberOfBookablePeople, int totalVaccinesPerHour, int totalCapacity, float predictedVaccinationRate) {
        float proportionOfCapacity = capacity / totalCapacity;
        float proportionOfVaccinesPerHour = vaccinesPerHour / totalVaccinesPerHour;

        float proportionOfBookings = (proportionOfCapacity + proportionOfVaccinesPerHour) / 2;
        return Math.round(proportionOfBookings * numberOfBookablePeople * predictedVaccinationRate);
    }
}
