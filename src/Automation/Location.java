package Automation;

import Data.Data;
import Data.Utils;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.HashMap;

public class Location {

    // Returns true if the location is open based on the given openingTimes and the systems date and time, false otherwise
    protected static boolean isOpen(Data data, HashMap<String, HashMap<String, String>> openingTimes) {
        HashMap<String, String> openingTime = getOpeningTime(data, openingTimes);
        LocalTime startTime = Utils.getLocalTime(openingTime.get("OpeningTime.startTime"));
        LocalTime endTime = Utils.getLocalTime(openingTime.get("OpeningTime.endTime"));

        LocalTime currentTime = data.getCurrentTime();
        return ((currentTime.isAfter(startTime)) && (currentTime.isBefore(endTime)));
    }

    // Returns the relevant opening time information on the systems date
    private static HashMap<String, String> getOpeningTime(Data data, HashMap<String, HashMap<String, String>> openingTimes) {
        for (String key : openingTimes.keySet()) {
            LocalDate currentDate = data.getCurrentDate();
            String currentDay = currentDate.getDayOfWeek().toString().toLowerCase();
            String openingTimeDay = (openingTimes.get(key).get("OpeningTime.day")).toLowerCase();
            if (currentDay.equals(openingTimeDay)) {
                return openingTimes.get(key);
            }
        }
        return null;
    }

    protected static String getLocationType(HashMap<String, Object> location) {
        if (location.get("Factory.factoryID") != null) {
            return "factory";
        }
        if (location.get("TransporterLocation.transporterLocationID") != null) {
            return "transporter location";
        }
        if (location.get("DistributionCentre.distributionCentreID") != null) {
            return "distribution centre";
        }
        if (location.get("VaccinationCentre.vaccinationCentreID") != null) {
            return "vaccination centre";
        }
        return null;
    }

    protected static String getID(HashMap<String, Object> location) {
        String[] potentialFieldNames = new String[] {
            "Factory.factoryID",
            "TransporterLocation.transporterLocationID",
            "DistributionCentre.distributionCentreID",
                "VaccinationCentre.vaccinationCentreID"
        };

        for (String potentialFieldName : potentialFieldNames) {
            if (location.get(potentialFieldName) != null) {
                return (String) location.get(potentialFieldName);
            }
        }
        return null;
    }
}
