package Automation;

import Core.Data;

import java.util.HashMap;

public class DistributionCentre extends DeliveryLocation {

    public static void orderVaccines(Data data) {
        HashMap<String, HashMap<String, Object>> factories = data.getFactories();
        HashMap<String, HashMap<String, Object>> distributionCentres = data.getDistributionCentres();
        HashMap<String, HashMap<String, Object>> vans = data.getVans();

        if (factories.size() == 0) {
            System.out.println("No factories so distribution centre cannot order more vaccines");
        }
        else if (vans.size() == 0) {
            System.out.println("No vans so distribution centre cannot order more vaccines");
        }
        else {
            for (String key : distributionCentres.keySet()) {
                HashMap<String, Object> distributionCentre = distributionCentres.get(key);

                int vaccinesNeeded = 5; // NEEDS TO BE BASED OF A FUNCTION IN FUTURE
                String vaccineID = "1"; // NEEDS TO BE BASED OF A FUNCTION IN FUTURE
                vans = orderVaccine(factories, distributionCentre, vans, vaccinesNeeded, vaccineID);
                data.setVans(vans);
            }
        }
    }
}
