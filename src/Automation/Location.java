/**
 * Parent class for all locations (factories, transporter locations, vans, distribution centres & vaccination centres)
 * containing classes they will call whilst automating the system.
 */
package Automation;

import Data.Data;
import Data.Utils;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.HashMap;

public class Location {

    /**
     * Returns true if the location is open based on the given openingTimes and the systems date and time, false otherwise
     * @param data used to get the current date and time
     * @param openingTimes the location's opening times
     * @return if the location is open
     */
    protected static boolean isOpen(Data data, HashMap<String, HashMap<String, String>> openingTimes) {
        HashMap<String, String> openingTime = getOpeningTime(data, openingTimes);

        LocalTime startTime = Utils.getLocalTime(openingTime.get("OpeningTime.startTime"));
        LocalTime endTime = Utils.getLocalTime(openingTime.get("OpeningTime.endTime"));

        LocalTime currentTime = data.getCurrentTime();
        return ((currentTime.isAfter(startTime)) && (currentTime.isBefore(endTime)));
    }

    /**
     * Returns the opening times from the given opening times for the current day
     * @param data used to get the current day (as stored in the system)
     * @param openingTimes the location's opening times for the whole week
     * @return the opening times for the current day
     */
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

    /**
     * Gets the type of location that the given location is, used for generating the text in the activity log
     * @param location
     * @return
     */
    protected static String getLocationType(HashMap<String, Object> location) {
        String factoryID = (String) location.get("Factory.factoryID");
        String transporterLocationID = (String) location.get("TransporterLocation.transporterLocationID");
        String distributionCentreID = (String) location.get("DistributionCentre.distributionCentreID");
        String vaccinationCentreID = (String) location.get("VaccinationCentre.vaccinationCentreID");

        if (!(factoryID == null)) {
            return "factory";
        }
        else if (!(transporterLocationID == null)) {
            return "transporter location";
        }
        else if (!(distributionCentreID == null)) {
            return "distribution centre";
        }
        else if (!(vaccinationCentreID == null)) {
            return "vaccination centre";
        }
        return null;
    }

    /**
     * Gets the ID of the given location by iterating through potential ID field names that the location could have. Cannot use
     * Data.Utils.getIDFieldName() as location will contain other ID field names matching the pattern (e.g. "Location.locationID")
     * the Data.Utils method is looking for. Used to generate an activity log.
     * @param location
     * @return
     */
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
