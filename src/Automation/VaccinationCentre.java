package Automation;

import Core.ActivityLog;
import Core.Data;

import java.util.HashMap;

public class VaccinationCentre extends DeliveryLocation {

    public static void orderVaccines(ActivityLog activityLog, Data data) {
        HashMap<String, HashMap<String, Object>> distributionCentres = data.getDistributionCentres();
        HashMap<String, HashMap<String, Object>> vaccinationCentres = data.getVaccinationCentres();
        HashMap<String, HashMap<String, Object>> vans = data.getVans();

        if (distributionCentres.size() == 0) {
            System.out.println("No distribution centres so vaccination centre cannot order more vaccines");
        }
        else if (vans.size() == 0) {
            System.out.println("No vans so vaccination centre cannot order more vaccines");
        }
        else {
            for (String key : vaccinationCentres.keySet()) {
                HashMap<String, Object> vaccinationCentre = vaccinationCentres.get(key);

                int vaccinesNeeded = 5; // NEEDS TO BE BASED OF A FUNCTION IN FUTURE
                String vaccineID = "1"; // NEEDS TO BE BASED OF A FUNCTION IN FUTURE
                vans = orderVaccine(activityLog, distributionCentres, vaccinationCentre, vans, vaccinesNeeded, vaccineID);
                data.setVans(vans);
            }
        }
    }
}
