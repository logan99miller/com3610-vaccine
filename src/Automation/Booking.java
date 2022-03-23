package Automation;

import Core.ActivityLog;
import Core.AutomateSystem;
import Data.Data;
import Data.Utils;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Random;

public class Booking {
    public static void simulateBookings(AutomateSystem automateSystem) {
        Data data = automateSystem.getData();
        ActivityLog activityLog = automateSystem.getActivityLog();

        HashMap<String, HashMap<String, Integer>> availabilities = automateSystem.getAvailabilities();
        HashMap<String, HashMap<String, Object>> bookablePeople = automateSystem.getBookablePeople();

        LocalDate currentDate = data.getCurrentDate();

        HashMap<String, HashMap<String, Object>> vaccines = data.getVaccines();

        for (String vaccineKey : vaccines.keySet()) {
            HashMap<String, Object> vaccine = vaccines.get(vaccineKey);
            int dosesNeeded = Integer.parseInt((String) vaccine.get("Vaccine.dosesNeeded"));
            int minimumAge = Integer.parseInt((String) vaccine.get("Vaccine.minimumAge"));
            int maximumAge = Integer.parseInt((String) vaccine.get("Vaccine.maximumAge"));

            for (int doseNumber = 1; doseNumber < dosesNeeded + 1; doseNumber++) {
                HashMap<String, HashMap<String, Object>> filteredPeople = filterPeopleByVaccineReceived(bookablePeople, doseNumber - 1);

                ArrayList<String> sortedKeys = Utils.sortDateKeyInMap(filteredPeople, "Person.DoB");
                Collections.reverse(sortedKeys);

                for (String peopleKey : sortedKeys) {

                    HashMap<String, Object> person = filteredPeople.get(peopleKey);
                    LocalDate DoB = Utils.getLocalDate((String) person.get("Person.DoB"), "-");
                    long age = ChronoUnit.YEARS.between(DoB, currentDate);
                    if ((minimumAge <= age) && (maximumAge >= age)) {
                        String personID = (String) person.get("Person.personID");
                        availabilities = simulateBooking(activityLog, data, personID, availabilities);
                    }
                }
            }
        }
        automateSystem.setAvailabilities(availabilities);
    }

    private static HashMap<String, HashMap<String, Object>> filterPeopleByVaccineReceived(
        HashMap<String, HashMap<String, Object>> people,
        int numVaccinesReceived
    ) {

        HashMap<String, HashMap<String, Object>> filteredPeople = new HashMap<>();

        for (String key : people.keySet()) {
            HashMap<String, Object> person = people.get(key);
            HashMap<String, Object> vaccinesReceived = (HashMap<String, Object>) person.get("vaccinesReceived");

            if (vaccinesReceived.size() == numVaccinesReceived) {
                filteredPeople.put(key, person);
            }
        }
        return filteredPeople;
    }

    private static HashMap<String, HashMap<String, Integer>> simulateBooking(ActivityLog activityLog, Data data, String personID, HashMap<String, HashMap<String, Integer>> availabilities) {
        String vaccinationCentreID = selectVaccinationCentre(availabilities);
        HashMap<String, Integer> slots = availabilities.get(vaccinationCentreID);
        HashMap<String, HashMap<String, Object>> vaccinationCentres = data.getVaccinationCentres();
        HashMap<String, Object> vaccinationCentre = vaccinationCentres.get(vaccinationCentreID);
        HashMap<String, HashMap<String, String>> bookings = (HashMap<String, HashMap<String, String>>) vaccinationCentre.get("bookings");
        int vaccinesPerHour = Integer.parseInt((String) vaccinationCentre.get("VaccinationCentre.vaccinesPerHour"));

        for (String date : slots.keySet()) {
            int value = slots.get(date);
            if (value < vaccinesPerHour) {
                slots.put(date, value + 1);

                activityLog.add("Person " + personID + " booked in at " + vaccinationCentreID + " vaccination centre for " + date);
                HashMap<String, String> booking = new HashMap<>();
                booking.put("Booking.personID", personID);
                booking.put("Booking.vaccinationCentreID", vaccinationCentreID);
                booking.put("Booking.date", date);
                booking.put("Booking.change", "change");
                bookings.put("newID" + personID, booking);
            }
        }
        vaccinationCentre.put("bookings", bookings);
        vaccinationCentres.put(vaccinationCentreID, vaccinationCentre);
        data.setVaccinationCentres(vaccinationCentres);
        availabilities.put(vaccinationCentreID, slots);
        return availabilities;
    }

    private static String selectVaccinationCentre(HashMap<String, HashMap<String, Integer>> availabilities) {
        Object[] keySet = availabilities.keySet().toArray();
        String vaccinationCentreID = (String) keySet[new Random().nextInt(keySet.length)];
        return vaccinationCentreID;
    }
}