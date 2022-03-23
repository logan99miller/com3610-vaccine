/**
 * Methods used to filter the hashmap of people found in the data class by those who are unbooked, and it has been long
 * enough since their previous vaccine for them to have another dose.
 */
package Automation;

import Data.Data;

import java.time.LocalDate;
import java.util.HashMap;

import static Data.Utils.getLocalDate;

public class People {

    /**
     * Returns all people eligible to be booked (as they do not currently have a booking, and it has been long enough since
     * their previous vaccination).
     * @return a hashmap of hashmaps in the format HashMap<personID, person>, where person is in the format HashMap<columnName, databaseValue>
     */
    public static HashMap<String, HashMap<String, Object>> getBookablePeople(Data data) {

        HashMap<String, HashMap<String, Object>> people = data.getPeople();
        HashMap<String, HashMap<String, Object>> bookablePeople = new HashMap<>();

        if (people != null) {
            for (String keyI : people.keySet()) {

                HashMap<String, Object> person = people.get(keyI);
                HashMap<String, Object> bookings = (HashMap<String, Object>) person.get("bookings");

                if (bookings.size() == 0) {

                    // Don't include the person in the unbooked hashmap if person is not eligible for another vaccine yet
                    if (longEnoughSincePreviousVaccines(data, person)) {
                        bookablePeople.put(keyI, person);
                    }
                }
            }
        }
        return bookablePeople;
    }

    /**
     * Iterates through all vaccines the given person has received and determines if enough time has elapsed for them to
     * receive another dose.
     * @param data used to get the current date and list of all vaccines
     * @param person represents the person table in the database. In the format in the format HashMap<columnName, databaseValue>
     * @return true if the person is eligible for another dose, false if not
     */
    private static boolean longEnoughSincePreviousVaccines(Data data, HashMap<String, Object> person) {

        LocalDate currentDate = data.getCurrentDate();
        HashMap<String, HashMap<String, Object>> vaccines = data.getVaccines();
        HashMap<String, HashMap<String, Object>> vaccinesReceived = (HashMap<String, HashMap<String, Object>>) person.get("vaccinesReceived");

        for (String keyJ : vaccinesReceived.keySet()) {

            String vaccineID = (String) vaccinesReceived.get(keyJ).get("VaccineReceived.vaccineID");
            LocalDate dateReceived = getLocalDate((String) vaccinesReceived.get(keyJ).get("VaccineReceived.date"));

            HashMap<String, Object> vaccine = vaccines.get(vaccineID);
            int daysBetweenDoses = Integer.parseInt((String) vaccine.get("Vaccine.daysBetweenDoses"));

            // When person will be eligible for their next vaccine
            LocalDate dateForNextVaccine = dateReceived.plusDays(daysBetweenDoses);

            if (dateForNextVaccine.isAfter(currentDate)) {
                return false;
            }
        }
        return true;
    }

}
