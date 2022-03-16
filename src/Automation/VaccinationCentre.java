package Automation;

import Core.ActivityLog;
import Data.Data;

import java.util.HashMap;

import static Automation.Availability.getAvailabilities;
import static Automation.People.getUnbookedPeople;

public class VaccinationCentre extends DeliveryLocation {

    public static void orderVaccines(ActivityLog activityLog, Data data) {
        HashMap<String, HashMap<String, Object>> distributionCentres = data.getDistributionCentres();
        HashMap<String, HashMap<String, Object>> vaccinationCentres = data.getVaccinationCentres();
        HashMap<String, HashMap<String, Object>> vans = data.getVans();

        HashMap<String, HashMap<String, Integer>> availabilities = getAvailabilities(data);
        HashMap<String, HashMap<String, Object>> unbookedPeople = getUnbookedPeople(data);

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

                int vaccinesNeeded = getVaccinesNeeded(vaccinationCentre, availabilities, unbookedPeople, vans, totalVaccinesPerHour, totalCapacity);
                String vaccineID = "1"; // NEEDS TO BE BASED OF A FUNCTION IN FUTURE
                vans = orderVaccine(activityLog, distributionCentres, vaccinationCentre, vans, vaccinesNeeded, vaccineID);
                data.setVans(vans);
            }
        }
    }

    public static int getVaccinesNeeded(
        HashMap<String, Object> vaccinationCentre,
        HashMap<String, HashMap<String, Integer>> availabilities,
        HashMap<String, HashMap<String, Object>> unbookedPeople,
        HashMap<String, HashMap<String, Object>> vans,
        int totalVaccinesPerHour,
        int totalCapacity
    ) {

        int vaccinesPerHour = Integer.parseInt((String) vaccinationCentre.get("VaccinationCentre.vaccinesPerHour"));
        int capacity = getCapacityIncludingVans(vaccinationCentre, vans);

        int expectedBookings = getExpectedBookings(vaccinesPerHour, capacity, unbookedPeople.size(), totalVaccinesPerHour, totalCapacity);

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
    private static int getExpectedBookings(int vaccinesPerHour, int capacity, int numberOfUnbookedPeople, int totalVaccinesPerHour, int totalCapacity) {
        final float PERCENTAGE_WHO_WANT_VACCINATION = 0.8f;

        float proportionOfCapacity = capacity / totalCapacity;
        float proportionOfVaccinesPerHour = vaccinesPerHour / totalVaccinesPerHour;

        float proportionOfBookings = (proportionOfCapacity + proportionOfVaccinesPerHour) / 2;
        return Math.round(proportionOfBookings * numberOfUnbookedPeople * PERCENTAGE_WHO_WANT_VACCINATION);
    }
}
