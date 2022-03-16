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
}
