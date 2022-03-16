package Automation;

import Data.Data;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;

public class Availability {

    public static HashMap<String, HashMap<String, Integer>> getAvailabilities(Data data) {
        LocalDate currentDate = data.getCurrentDate();
        HashMap<String, HashMap<String, Object>> vaccinationCentres = data.getVaccinationCentres();
        HashMap<String, HashMap<String, Integer>> availabilities = new HashMap<>();
        HashMap<String, LocalDate> week = getDatesOfWeek(currentDate);
        for (String key : vaccinationCentres.keySet()) {
            HashMap<String, Object> vaccinationCentre = vaccinationCentres.get(key);
            String vaccinationCentreID = (String) vaccinationCentre.get("VaccinationCentre.vaccinationCentreID");
            HashMap<String, Integer> slots = getSlots(vaccinationCentre, week);
            availabilities.put(vaccinationCentreID, slots);
        }
        return availabilities;
    }

    private static HashMap<String, Integer> getSlots(HashMap<String, Object> vaccinationCentre, HashMap<String, LocalDate> week) {
        HashMap<String, HashMap<String, String>> bookings = (HashMap<String, HashMap<String, String>>) vaccinationCentre.get("bookings");
        HashMap<String, Integer> slots = generateSlots(vaccinationCentre, week);
        int vaccinesPerHour = Integer.parseInt((String) vaccinationCentre.get("VaccinationCentre.vaccinesPerHour"));
        slots = addCurrentSlots(slots, bookings);
        slots = removeFullSlots(slots, vaccinesPerHour);
        return slots;
    }

    private static HashMap<String, Integer> generateSlots(HashMap<String, Object> vaccinationCentre, HashMap<String, LocalDate> week) {
        HashMap<String, Integer> slots = new HashMap<>();
        HashMap<String, HashMap<String, String>> openingTimes = (HashMap<String, HashMap<String, String>>) vaccinationCentre.get("openingTimes");
        for (String key : openingTimes.keySet()) {
            HashMap<String, String> openingTime = openingTimes.get(key);
            String day = openingTime.get("OpeningTime.day").toLowerCase();
            LocalTime startTime = LocalTime.parse(openingTime.get("OpeningTime.startTime"));
            LocalTime endTime = LocalTime.parse(openingTime.get("OpeningTime.endTime"));
            long hoursOpen = startTime.until(endTime, ChronoUnit.HOURS);
            for (int i = 0; i < hoursOpen; i++) {
                LocalDate date = week.get(day);
                LocalTime hour = startTime.plusHours(i);
                slots.put(date + " " + hour, 0);
            }
        }
        return slots;
    }

    private static HashMap<String, Integer> addCurrentSlots(HashMap<String, Integer> slots, HashMap<String, HashMap<String, String>> bookings) {
        for (String key : bookings.keySet()) {
            HashMap<String, String> booking = bookings.get(key);
            String date = booking.get("Booking.date").substring(0, 16);
            int existingBookings = slots.get(date);
            slots.put(date, existingBookings + 1);
        }
        return slots;
    }

    private static HashMap<String, Integer> removeFullSlots(HashMap<String, Integer> slots, int vaccinesPerHour) {
        HashMap<String, Integer> modifiedSlots = slots; // Directly modifying slots will cause a ConcurrentModificationException as we will iterate through slots
        for (String key : slots.keySet()) {
            if (slots.get(key) < vaccinesPerHour) {
                modifiedSlots.put(key, slots.get(key));
            }
        }
        return slots;
    }

    private static HashMap<String, LocalDate> getDatesOfWeek(LocalDate currentDate) {
        HashMap<String, LocalDate> week = new HashMap<>();
        for (int i = 0; i < 7; i++) {
            LocalDate date = currentDate.plusDays(i);
            String dayOfWeek = String.valueOf(date.getDayOfWeek()).toLowerCase();
            week.put(dayOfWeek, date);
        }
        return week;
    }

}
