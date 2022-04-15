/**
 * Calculates the availabilities for vaccination appointments for all vaccination centres. Used when booking vaccination appointments
 */
package Automation;

import Data.Data;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;

public class Availability {

    /**
     * Gets the availabilities for vaccination appointments for all vaccination centres. Used when booking vaccination appointments
     * @return the availabilities in the format HashMap<vaccinationCentreID, HashMap<dateAndHour, currentNumberOfAppointments>>
     */
    public static HashMap<String, HashMap<String, Integer>> getAvailabilities(Data data) {
        LocalDate currentDate = data.getCurrentDate();

        HashMap<String, HashMap<String, Object>> vaccinationCentres = data.getVaccinationCentres();
        HashMap<String, HashMap<String, Integer>> availabilities = new HashMap<>();
        HashMap<String, LocalDate> week = getDaysOfWeek(currentDate);

        // Get the available slots for each vaccination centre and add them to the hash map to be returned
        if (vaccinationCentres != null) {
            for (String key : vaccinationCentres.keySet()) {

                HashMap<String, Object> vaccinationCentre = vaccinationCentres.get(key);
                String vaccinationCentreID = (String) vaccinationCentre.get("VaccinationCentre.vaccinationCentreID");

                HashMap<String, Integer> slots = getSlots(vaccinationCentre, week);
                availabilities.put(vaccinationCentreID, slots);
            }
        }

        return availabilities;
    }

    /**
     * Gets the amount of appointments made for each 1 hour slot for the given vaccination centre
     * @param vaccinationCentre in the format HashMap<columnName, databaseValue>
     * @param week the week we are interested in getting appointment details for, in the format HashMap<dayOfWeek, date>
     * @return the amount of appointments already taken, in the format HashMap<dateAndHour, currentNumberOfAppointments>
     */
    private static HashMap<String, Integer> getSlots(HashMap<String, Object> vaccinationCentre, HashMap<String, LocalDate> week) {
        HashMap<String, HashMap<String, String>> bookings = (HashMap<String, HashMap<String, String>>) vaccinationCentre.get("bookings");
        HashMap<String, Integer> slots = generateSlots(vaccinationCentre, week);

        int vaccinesPerHour = Integer.parseInt((String) vaccinationCentre.get("VaccinationCentre.vaccinesPerHour"));

        slots = addCurrentSlots(slots, bookings);
        slots = removeFullSlots(slots, vaccinesPerHour);

        return slots;
    }

    /**
     * Gets the opening times the given vaccination centre is open for and create a hash map with a key for each hour of each
     * day in the given week
     * @param vaccinationCentre in the format HashMap<columnName, databaseValue>
     * @param week the week we are interested in getting appointment details for, in the format HashMap<dayOfWeek, date>
     * @return a hash map in the structure needed to store the amount of appointments currently made, in the format HashMap<dateAndHour, 0>
     */
    private static HashMap<String, Integer> generateSlots(HashMap<String, Object> vaccinationCentre, HashMap<String, LocalDate> week) {
        HashMap<String, Integer> slots = new HashMap<>();
        HashMap<String, HashMap<String, String>> openingTimes = (HashMap<String, HashMap<String, String>>) vaccinationCentre.get("openingTimes");

        // Iterate through each day in the week
        for (String key : openingTimes.keySet()) {

            HashMap<String, String> openingTime = openingTimes.get(key);

            String day = openingTime.get("OpeningTime.day").toLowerCase();
            LocalTime startTime = LocalTime.parse(openingTime.get("OpeningTime.startTime"));
            LocalTime endTime = LocalTime.parse(openingTime.get("OpeningTime.endTime"));

            long hoursOpen = startTime.until(endTime, ChronoUnit.HOURS);

            LocalDate date = week.get(day);

            // Add the slots for each hour in the current day
            for (int i = 0; i < hoursOpen; i++) {
                LocalTime hour = startTime.plusHours(i);
                slots.put(date + " " + hour, 0);
            }
        }
        return slots;
    }

    /**
     * Adds the current bookings to the slots structure. Later used to determine if the slot is full or not
     * @param slots a hash map in the structure needed to store the amount of appointments currently made, in the format HashMap<dateAndHour, 0>
     * @param bookings currently existing bookings, in the format HashMap<columnName, databaseValue>
     * @returnthe the bookings currently made for one vaccination centre, in the format HashMap<dateAndHour, currentNumberOfAppointments>
     */
    private static HashMap<String, Integer> addCurrentSlots(HashMap<String, Integer> slots, HashMap<String, HashMap<String, String>> bookings) {
        for (String key : bookings.keySet()) {
            HashMap<String, String> booking = bookings.get(key);
            String date = booking.get("Booking.date").substring(0, 16);

            int existingBookings = 0;
            try {
                existingBookings = slots.get(date);
            }
            catch (NullPointerException e) {}

            slots.put(date, existingBookings + 1);
        }
        return slots;
    }

    /**
     * Removes any 1 hour slots which have the same amount of bookings as the vaccinations the vaccination centre can perform
     * each hour
     * @param slots the bookings currently made for one vaccination centre, in the format HashMap<dateAndHour, currentNumberOfAppointments>
     * @param vaccinesPerHour the number of vaccinations the vaccination centre can perform each hour
     * @return
     */
    private static HashMap<String, Integer> removeFullSlots(HashMap<String, Integer> slots, int vaccinesPerHour) {

        // Directly modifying slots will cause a ConcurrentModificationException as we will iterate through slots
        HashMap<String, Integer> modifiedSlots = slots;

        for (String key : slots.keySet()) {
            if (slots.get(key) < vaccinesPerHour) {
                modifiedSlots.put(key, slots.get(key));
            }
        }

        return slots;
    }

    /**
     * Gets the day of the week (e.g. monday) of the current day and the next 6 days
     * @param currentDate the current date
     * @return a hashmap containing the days of the next week, in the format HashMap<dayOfWeek, date>
     */
    private static HashMap<String, LocalDate> getDaysOfWeek(LocalDate currentDate) {
        HashMap<String, LocalDate> week = new HashMap<>();

        for (int i = 0; i < 7; i++) {
            LocalDate date = currentDate.plusDays(i);

            String dayOfWeek = String.valueOf(date.getDayOfWeek()).toLowerCase();

            week.put(dayOfWeek, date);
        }
        return week;
    }
}