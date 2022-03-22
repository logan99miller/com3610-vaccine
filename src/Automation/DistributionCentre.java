package Automation;

import Core.ActivityLog;
import Core.AutomateSystem;
import Data.Data;

import java.util.HashMap;

import static Automation.Availability.*;
import static Automation.Distance.*;
import static Automation.People.*;

public class DistributionCentre extends DeliveryLocation {

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
        int totalCapacity = getTotalCapacity(vaccinationCentres, vans);

//        HashMap<String, HashMap<String, Integer>> availabilities = getAvailabilities(data);
//        HashMap<String, HashMap<String, Object>> bookablePeople = getBookablePeople(data);

        if (factories.size() == 0) {
            activityLog.add("No factories so distribution centres cannot order more vaccines", true);
        }
        else if (vans.size() == 0) {
            activityLog.add("No vans so distribution centres cannot order more vaccines", true);
        }
        else {
            for (String key : distributionCentres.keySet()) {
                HashMap<String, Object> distributionCentre = distributionCentres.get(key);

                int vaccinesNeeded = getVaccinesNeeded(data, distributionCentre, availabilities, bookablePeople, vans, totalVaccinesPerHour, totalCapacity, totalDistance, totalVaccinesNeeded);
                String vaccineID = "1"; // NEEDS TO BE BASED OF A FUNCTION IN FUTURE
                vans = orderVaccine(activityLog, factories, distributionCentre, vans, vaccinesNeeded, vaccineID);
                data.setVans(vans);
            }
        }
    }

    public static int getVaccinesNeeded(
            Data data,
            HashMap<String, Object> distributionCentre,
            HashMap<String, HashMap<String, Integer>> availabilities,
            HashMap<String, HashMap<String, Object>> bookablePeople,
            HashMap<String, HashMap<String, Object>> vans,
            int totalVaccinesPerHour,
            int totalCapacity,
            double totalDistance,
            int totalVaccinesNeeded
    ) {
        final float DEMAND_MULTIPLIER = 1.5f; // How many more vaccines we want over VC demand

        float predictedVaccinationRate = Float.parseFloat(data.getPredictedVaccinationRate());

        HashMap<String, HashMap<String, Object>> vaccinationCentres = data.getVaccinationCentres();

        int demand = 0;
        int capacity = getCapacityIncludingVans(distributionCentre, vans);

        for (String key : vaccinationCentres.keySet()) {
            HashMap<String, Object> vaccinationCentre = vaccinationCentres.get(key);

            double distance = getDistance(vaccinationCentre, distributionCentre);
            int vaccinesNeeded = VaccinationCentre.getVaccinesNeeded(vaccinationCentre, availabilities, bookablePeople, vans, totalVaccinesPerHour, totalCapacity, predictedVaccinationRate);

            double proportionOfDistance = distance / totalDistance;

            double proportionOfVaccinesNeeded;
            if (totalVaccinesNeeded == 0) {
                proportionOfVaccinesNeeded = 0;
            }
            else {
                proportionOfVaccinesNeeded = vaccinesNeeded / totalVaccinesNeeded;
            }

            double proportionOfVaccines = (proportionOfDistance + proportionOfVaccinesNeeded) / 2;

            demand += Math.round(proportionOfVaccines * totalVaccinesNeeded * DEMAND_MULTIPLIER);

        }

        if (demand > capacity) {
            return demand - capacity;
        }
        return 0;
    }

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

    private static int getTotalVaccinesNeeded(Data data) {
        HashMap<String, HashMap<String, Object>> vaccinationCentres = data.getVaccinationCentres();
        HashMap<String, HashMap<String, Object>> vans = data.getVans();

        HashMap<String, HashMap<String, Integer>> availabilities = getAvailabilities(data);
        HashMap<String, HashMap<String, Object>> bookablePeople = getBookablePeople(data);

        int totalVaccinesPerHour = getTotalVaccinesPerHour(vaccinationCentres);
        int totalCapacity = getTotalCapacity(vaccinationCentres, vans);

        float predictedVaccinationRate = Float.parseFloat(data.getPredictedVaccinationRate());

        int totalVaccinesNeeded = 0;
        for (String keyI : vaccinationCentres.keySet()) {
            HashMap<String, Object> vaccinationCentre = vaccinationCentres.get(keyI);
            totalVaccinesNeeded += VaccinationCentre.getVaccinesNeeded(vaccinationCentre, availabilities, bookablePeople, vans, totalVaccinesPerHour, totalCapacity, predictedVaccinationRate);

        }
        return totalVaccinesNeeded;
    }
}
