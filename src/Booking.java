import java.time.LocalDate;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

public class Booking {
    public static void simulateBookings(Data data) {
        LocalDate currentDate = data.getCurrentDate();
        HashMap<String, HashMap<String, Integer>> availability = getAvailability(data);

        HashMap<String, HashMap<String, Object>> vaccinePriorities = data.getVaccinePriority();
        ArrayList<Integer> sortedIndices = Data.sortMaps(vaccinePriorities, "VaccinePriority.positionInQueue");

        HashMap<String, HashMap<String, Object>> unbookedPeople = getUnbookedPeople(data);

        for (int index : sortedIndices) {
            HashMap<String, Object> vaccinePriority = vaccinePriorities.get(String.valueOf(index));
            for (String key : unbookedPeople.keySet()) {
                HashMap<String, Object> person = unbookedPeople.get(key);
                LocalDate DoB = Data.getDateFromString((String) person.get("Person.DoB"), "-");
                long age = ChronoUnit.YEARS.between(DoB, currentDate);
                int lowestAge = Integer.parseInt((String) vaccinePriority.get("VaccinePriority.lowestAge"));
                int highestAge = Integer.parseInt((String) vaccinePriority.get("VaccinePriority.highestAge"));
                int doseNumber = Integer.parseInt((String) vaccinePriority.get("VaccinePriority.doseNumber"));
                int dosesReceived = ((HashMap<String, Object>) person.get("vaccinesReceived")).size();

                if ((age >= lowestAge) && (age < highestAge) && (doseNumber - 1 == dosesReceived)) {
                    String personID = (String) person.get("Person.personID");
                    simulateBooking(personID, availability, data);
                }
            }
        }

    }

    private static HashMap<String, HashMap<String, Object>> getUnbookedPeople(Data data) {
        HashMap<String, HashMap<String, Object>> people = data.getPeople();
        HashMap<String, HashMap<String, Object>> unbookedPeople = new HashMap<>();

        for (String key : people.keySet()) {
            HashMap<String, Object> person = people.get(key);
            HashMap<String, Object> bookings = (HashMap<String, Object>) person.get("bookings");
            if (bookings.size() == 0) {
                unbookedPeople.put(key, person);
            }
        }
        return unbookedPeople;
    }

    private static HashMap<String, HashMap<String, Integer>> simulateBooking(String personID, HashMap<String, HashMap<String, Integer>> availability, Data data) {
        String vaccinationCentreID = selectVaccinationCentre(availability);
        HashMap<String, Integer> slots = availability.get(vaccinationCentreID);
        HashMap<String, Integer> modifiedSlots = slots;
        HashMap<String, HashMap<String, Object>> vaccinationCentres = data.getVaccinationCentres();
        HashMap<String, Object> vaccinationCentre = vaccinationCentres.get(vaccinationCentreID);
        HashMap<String, HashMap<String, String>> bookings = (HashMap<String, HashMap<String, String>>) vaccinationCentre.get("bookings");
        int vaccinesPerHour = Integer.parseInt((String) vaccinationCentre.get("VaccinationCentre.vaccinesPerHour"));

        for (String date : slots.keySet()) {
            int value = slots.get(date);
            if (value < vaccinesPerHour) {
                slots.put(date, value + 1);
//                if (value != (vaccinesPerHour - 1)) {
////                    modifiedSlots.remove(date);
//                }
//                else {
//                    modifiedSlots.put(date, value + 1);
//                }
                HashMap<String, String> booking = new HashMap<String, String>();
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
        availability.put(vaccinationCentreID, slots);
        return availability;
    }

    private static String selectVaccinationCentre(HashMap<String, HashMap<String, Integer>> availability) {
        Object[] keySet = availability.keySet().toArray();
        String vaccinationCentreID = (String) keySet[new Random().nextInt(keySet.length)]; // Random should be more shared
        return vaccinationCentreID;
    }

    private static HashMap<String, HashMap<String, Integer>> getAvailability(Data data) {
        LocalDate currentDate = data.getCurrentDate();
        HashMap<String, HashMap<String, Object>> vaccinationCentres = data.getVaccinationCentres();
        HashMap<String, HashMap<String, Integer>> availability = new HashMap<>();
        HashMap<String, LocalDate> week = getDatesOfWeek(currentDate);
        for (String key : vaccinationCentres.keySet()) {
            HashMap<String, Object> vaccinationCentre = vaccinationCentres.get(key);
            String vaccinationCentreID = (String) vaccinationCentre.get("VaccinationCentre.vaccinationCentreID");
            HashMap<String, Integer> slots = getSlots(vaccinationCentre, week);
            availability.put(vaccinationCentreID, slots);
        }
        return availability;
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