package Automation;

import Core.Data;

import java.time.LocalDate;
import java.util.HashMap;

import static Core.DataUtils.getLocalDate;

public class People {

    public static HashMap<String, HashMap<String, Object>> getUnbookedPeople(Data data) {
        HashMap<String, HashMap<String, Object>> people = data.getPeople();
        HashMap<String, HashMap<String, Object>> unbookedPeople = new HashMap<>();

        for (String keyI : people.keySet()) {
            HashMap<String, Object> person = people.get(keyI);
            HashMap<String, Object> bookings = (HashMap<String, Object>) person.get("bookings");
            if (bookings.size() == 0) {

                // Don't include if person is not eligible for another vaccine yet
                if (longEnoughSincePreviousVaccines(data, person)) {
                    unbookedPeople.put(keyI, person);
                }
            }
        }
        return unbookedPeople;
    }

    // METHOD NOT TESTED
    private static boolean longEnoughSincePreviousVaccines(Data data, HashMap<String, Object> person) {
        LocalDate currentDate = data.getCurrentDate();
        HashMap<String, HashMap<String, Object>> vaccines = data.getVaccines();

        HashMap<String, HashMap<String, Object>> vaccinesReceived = (HashMap<String, HashMap<String, Object>>) person.get("vaccinesReceived");
        for (String keyJ : vaccinesReceived.keySet()) {
            String vaccineID = (String) vaccinesReceived.get(keyJ).get("VaccineReceived.vaccineID");
            LocalDate dateReceived = getLocalDate((String) vaccinesReceived.get(keyJ).get("VaccineReceived.date"));

            HashMap<String, Object> vaccine = vaccines.get(vaccineID);
            int daysBetweenDoses = Integer.parseInt((String) vaccine.get("Vaccine.daysBetweenDoses"));

            LocalDate dateForNextVaccine = dateReceived.plusDays(daysBetweenDoses);

            if (dateForNextVaccine.isAfter(currentDate)) {
                return false;
            }
        }
        return true;
    }

}
