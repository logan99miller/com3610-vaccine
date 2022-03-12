package Automation;

import Core.ActivityLog;
import Core.Data;

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
            System.out.println("No distribution centres so vaccination centre cannot order more vaccines");
        }
        else if (vans.size() == 0) {
            System.out.println("No vans so vaccination centre cannot order more vaccines");
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

    private static int getVaccinesNeeded(
        HashMap<String, Object> vaccinationCentre,
        HashMap<String, HashMap<String, Integer>> availabilities,
        HashMap<String, HashMap<String, Object>> unbookedPeople,
        HashMap<String, HashMap<String, Object>> vans,
        int totalVaccinesPerHour,
        int totalCapacity
    ) {
        final float PERCENTAGE_WHO_WANT_VACCINATION = 0.8f;

        int vaccinesPerHour = Integer.parseInt((String) vaccinationCentre.get("VaccinationCentre.vaccinesPerHour"));
        int capacity = getCapacityIncludingVans(vaccinationCentre, vans);

        float proportionOfCapacity = capacity / totalCapacity;
        float proportionOfVaccinesPerHour = vaccinesPerHour / totalVaccinesPerHour;

        float proportionOfBookings = (proportionOfCapacity + proportionOfVaccinesPerHour) / 2;
        int expectedBookings = Math.round(proportionOfBookings * unbookedPeople.size() * PERCENTAGE_WHO_WANT_VACCINATION);

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

    // Gets the total vaccines per hour across all vaccination centres
    private static int getTotalVaccinesPerHour(HashMap<String, HashMap<String, Object>> vaccinationCentres) {
        int totalVaccinesPerHour = 0;
        for (String key : vaccinationCentres.keySet()) {
            HashMap<String, Object> vaccinationCentre = vaccinationCentres.get(key);
            totalVaccinesPerHour += Integer.parseInt((String) vaccinationCentre.get("VaccinationCentre.vaccinesPerHour"));
        }
        return totalVaccinesPerHour;
    }

    // Gets the total storage capacity across all vaccination centres
    private static int getTotalCapacity(HashMap<String, HashMap<String, Object>> vaccinationCentres, HashMap<String, HashMap<String, Object>> vans) {
        int totalCapacity = 0;
        for (String key : vaccinationCentres.keySet()) {
            HashMap<String, Object> vaccinationCentre = vaccinationCentres.get(key);
            totalCapacity += getCapacityIncludingVans(vaccinationCentre, vans);
        }
        return totalCapacity;
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
}
