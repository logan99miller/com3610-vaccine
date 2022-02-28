package UserInterface.SelectPages;

import Core.VaccineSystem;
import UserInterface.MainPage;
import UserInterface.ViewPage;

import javax.swing.*;
import java.util.HashMap;
import java.util.Map;

public class SelectViewPage extends SelectPage {

    private HashMap<String, Object> vaccineMap, personMap, medicalConditionMap, manufacturerMap, transporterMap, vaccinationCentreMap,
        bookingMap, factoryMap, transportLocationMap, distributionCentreMap, vaccinePriorityMap;

    public SelectViewPage(VaccineSystem vaccineSystem, MainPage mainPage) {
        super(vaccineSystem, mainPage, "view");
        createMaps();
        addButtons(createPages());
    }

    private ViewPage createViewPage(HashMap<String, Object> map, String title) {
        ViewPage viewPage;

        if (!map.containsKey("references")) {
            map.put("references", null);
        }
        if (!map.containsKey("deleteOption")) {
            map.put("deleteOption", false);
        }

        viewPage = new ViewPage(
                vaccineSystem, mainPage, title,
                (String[]) map.get("headings"),
                (String[]) map.get("columnNames"),
                (Object[]) map.get("references"),
                (String) map.get("tableName"),
                (boolean) map.get("deleteOption"));

        return viewPage;
    }

    private HashMap<String, HashMap<String, Object>> createPages() {
        HashMap<String, HashMap<String, Object>> pages = new HashMap<>();
        pages.put("Vaccines", vaccineMap);
        pages.put("People", personMap);
        pages.put("Medical Conditions", medicalConditionMap);
        pages.put("Manufacturers", manufacturerMap);
        pages.put("Factories", factoryMap);
        pages.put("Transporters", transporterMap);
        pages.put("Transporter Locations", transportLocationMap);
        pages.put("Distribution Centres", distributionCentreMap);
        pages.put("Vaccination Centres", vaccinationCentreMap);
        pages.put("Bookings", bookingMap);
        pages.put("Vaccine Priorities", vaccinePriorityMap);
        return pages;
    }

    private void addButtons(HashMap<String, HashMap<String, Object>> pages) {
        for (Map.Entry<String, HashMap<String, Object>> set : pages.entrySet()) {
            JButton button = new JButton(set.getKey());
            button.addActionListener(e -> {
                ViewPage viewPage = createViewPage(set.getValue(), button.getText());
                mainPage.updatePageToComponent(viewPage.getPanel());
            });
            mainPanel.add(button);
        }
    }

    private void createMaps() {
        HashMap<String, Object> openingTimeMap = new HashMap<>();
        openingTimeMap.put("heading", "Opening Times");
        openingTimeMap.put("IDFieldName", "locationID");
        openingTimeMap.put("title", "Opening Times");
        openingTimeMap.put("headings", new String[] {"Day", "Start Time", "End Time"});
        openingTimeMap.put("columnNames", new String[] {"day", "startTime", "endTime"});
        openingTimeMap.put("tableName", "openingTime");
        openingTimeMap.put("buttonText", "View");

        HashMap<String, Object> minimalMedicalConditionMap = new HashMap<>();
        minimalMedicalConditionMap.put("heading", "Medical Condition");
        minimalMedicalConditionMap.put("IDFieldName", "medicalConditionID");
        minimalMedicalConditionMap.put("title", "Medical Condition");
        minimalMedicalConditionMap.put("headings", new String[] {"ID", "Name", "Vulnerability Level"});
        minimalMedicalConditionMap.put("columnNames", new String[] {"medicalConditionID", "name", "vulnerabilityLevel"});
        minimalMedicalConditionMap.put("tableName", "medicalCondition");

        HashMap<String, Object> vaccineExemptionMap = new HashMap<>();
        vaccineExemptionMap.put("heading", "Exemptions");
        vaccineExemptionMap.put("IDFieldName", "vaccineID");
        vaccineExemptionMap.put("title", "Exemption(s)");
        vaccineExemptionMap.put("headings", new String[] {"Medical Condition"});
        vaccineExemptionMap.put("columnNames", new String[] {"medicalConditionID"});
        vaccineExemptionMap.put("references", new Object[] {minimalMedicalConditionMap});
        vaccineExemptionMap.put("tableName", "vaccineExemption");

        HashMap<String, Object> vaccineLifespanMap = new HashMap<>();
        vaccineLifespanMap.put("heading", "Lifespan");
        vaccineLifespanMap.put("IDFieldName", "vaccineID");
        vaccineLifespanMap.put("title", "Lifespan(s)");
        vaccineLifespanMap.put("headings", new String[] {"Lifespan", "Lowest Temperature", "Highest Temperature"});
        vaccineLifespanMap.put("columnNames", new String[] {"lifespan", "lowestTemperature", "highestTemperature"});
        vaccineLifespanMap.put("tableName", "vaccineLifespan");
        vaccineLifespanMap.put("buttonText", "View");

        HashMap<String, Object> minimalVaccineMap = new HashMap<>();
        minimalVaccineMap.put("heading", "Vaccine");
        minimalVaccineMap.put("IDFieldName", "vaccineID");
        minimalVaccineMap.put("title", "Vaccine");
        minimalVaccineMap.put("headings", new String[] {"ID", "Name", "Doses Needed", "Days Between Doses", "Lifespan"});
        minimalVaccineMap.put("columnNames", new String[] {"vaccineID", "name", "dosesNeeded", "daysBetweenDoses", "vaccineID"});
        minimalVaccineMap.put("references", new Object[] {vaccineLifespanMap});
        minimalVaccineMap.put("tableName", "vaccine");

        HashMap<String, Object> medicalExemptionMap = new HashMap<>();
        medicalExemptionMap.put("heading", "Exemptions");
        medicalExemptionMap.put("IDFieldName", "medicalConditionID");
        medicalExemptionMap.put("title", "Exemption(s)");
        medicalExemptionMap.put("headings", new String[] {"Vaccine"});
        medicalExemptionMap.put("columnNames", new String[] {"vaccineID"});
        medicalExemptionMap.put("references", new Object[] {minimalVaccineMap});
        medicalExemptionMap.put("tableName", "vaccineExemption");

        vaccineMap = new HashMap<>();
        vaccineMap.put("heading", "Vaccine");
        vaccineMap.put("IDFieldName", "vaccineID");
        vaccineMap.put("title", "Vaccine");
        vaccineMap.put("headings", new String[] {"ID", "Name", "Doses Needed", "Days Between Doses", "Lifespan", "Exemptions"});
        vaccineMap.put("columnNames", new String[] {"vaccineID", "name", "dosesNeeded", "daysBetweenDoses", "vaccineID", "vaccineID"});
        vaccineMap.put("references", new Object[] {vaccineLifespanMap, vaccineExemptionMap});
        vaccineMap.put("tableName", "vaccine");
        vaccineMap.put("deleteOption", true);

        medicalConditionMap = new HashMap<>();
        medicalConditionMap.put("heading", "Medical Conditions");
        medicalConditionMap.put("IDFieldName", "medicalConditionID");
        medicalConditionMap.put("title", "Medical Conditions");
        medicalConditionMap.put("headings", new String[] {"ID", "Name", "Vulnerability Level", "Exemptions"});
        medicalConditionMap.put("columnNames", new String[] {"medicalConditionID", "name", "vulnerabilityLevel", "medicalConditionID"});
        medicalConditionMap.put("references", new Object[] {medicalExemptionMap});
        medicalConditionMap.put("tableName", "medicalCondition");
        medicalConditionMap.put("deleteOption", true);

        HashMap<String, Object> personVaccineMap = new HashMap<>();
        personVaccineMap.put("heading", "Vaccines");
        personVaccineMap.put("IDFieldName", "personID");
        personVaccineMap.put("title", "Vaccines Received");
        personVaccineMap.put("headings", new String[] {"Date", "Vaccine"});
        personVaccineMap.put("columnNames", new String[] {"date", "vaccineID"});
        personVaccineMap.put("references", new Object[] {vaccineMap});
        personVaccineMap.put("tableName", "vaccineReceived");
        personVaccineMap.put("buttonText", "View");

        HashMap<String, Object> vaccineInStorageMap = new HashMap<>();
        vaccineInStorageMap.put("heading", "Used Storage");
        vaccineInStorageMap.put("IDFieldName", "storeID");
        vaccineInStorageMap.put("title", "Vaccines In Storage:");
        vaccineInStorageMap.put("headings", new String[] {"Vaccine", "Stock Level", "Expiration Date"});
        vaccineInStorageMap.put("columnNames", new String[] {"vaccineID", "stockLevel", "expirationDate"});
        vaccineInStorageMap.put("references", new Object[] {vaccineMap});
        vaccineInStorageMap.put("tableName", "vaccineInStorage");
        vaccineInStorageMap.put("buttonText", "View");

        HashMap<String, Object> storeMap = new HashMap<>();
        storeMap.put("heading", "Storage");
        storeMap.put("IDFieldName", "storageLocationID");
        storeMap.put("title", "Storage Details:");
        storeMap.put("headings", new String[] {"Temperature", "Capacity", "Used Storage"});
        storeMap.put("columnNames", new String[] {"temperature", "capacity", "storeID"});
        storeMap.put("references", new Object[] {vaccineInStorageMap});
        storeMap.put("tableName", "store");
        storeMap.put("buttonText", "Details");

        HashMap<String, Object> linkedLocationMap = new HashMap<>();
        linkedLocationMap.put("heading", "Location");
        linkedLocationMap.put("linkerTableName", "storageLocation");
        linkedLocationMap.put("linkerIDFieldName", "locationID");
        linkedLocationMap.put("IDFieldName", "storageLocationID");
        linkedLocationMap.put("title", "Location");
        linkedLocationMap.put("headings", new String[] {"ID", "Longitude", "Latitude", "Opening Times"});
        linkedLocationMap.put("columnNames", new String[] {"locationID", "longitude", "latitude", "locationID"});
        linkedLocationMap.put("references", new Object[] {openingTimeMap});
        linkedLocationMap.put("tableName", "location");
        linkedLocationMap.put("buttonText", "Details");

        vaccinationCentreMap = new HashMap<>();
        vaccinationCentreMap.put("heading", "Vaccination Centre");
        vaccinationCentreMap.put("IDFieldName", "vaccinationCentreID");
        vaccinationCentreMap.put("title", "Vaccination Centre");
        vaccinationCentreMap.put("headings", new String[] {"ID", "Name", "Location", "Storage"});
        vaccinationCentreMap.put("columnNames", new String[] {"vaccinationCentreID", "name", "storageLocationID", "storageLocationID"});
        vaccinationCentreMap.put("references", new Object[] {linkedLocationMap, storeMap});
        vaccinationCentreMap.put("tableName", "vaccinationCentre");
        vaccinationCentreMap.put("deleteOption", true);

        HashMap<String, Object> personBookingMap = new HashMap<>();
        personBookingMap.put("heading", "Bookings");
        personBookingMap.put("IDFieldName", "personID");
        personBookingMap.put("title", "Vaccination Centre");
        personBookingMap.put("headings", new String[] {"ID", "Vaccination Centre", "Date"});
        personBookingMap.put("columnNames", new String[] {"bookingID", "vaccinationCentreID", "date"});
        personBookingMap.put("references", new Object[] {vaccinationCentreMap});
        personBookingMap.put("tableName", "booking");

        personMap = new HashMap<>();
        personMap.put("heading", "Person");
        personMap.put("IDFieldName", "personID");
        personMap.put("title", "Person");
        personMap.put("headings", new String[] {"ID", "Forename", "Surname", "DoB", "Medical Conditions", "Bookings", "Vaccines"});
        personMap.put("columnNames", new String[]  {"personID", "forename", "surname", "dob", "personID", "personID", "personID"});
        personMap.put("references", new Object[] {medicalConditionMap, personBookingMap, personVaccineMap});
        personMap.put("tableName", "person");
        personMap.put("deleteOption", true);

        bookingMap = new HashMap<>();
        bookingMap.put("heading", "Bookings");
        bookingMap.put("IDFieldName", "personID");
        bookingMap.put("title", "Vaccination Centre");
        bookingMap.put("headings", new String[] {"ID", "Person", "Vaccination Centre", "Date"});
        bookingMap.put("columnNames", new String[] {"bookingID", "personID", "vaccinationCentreID", "date"});
        bookingMap.put("references", new Object[] {personMap, vaccinationCentreMap});
        bookingMap.put("tableName", "booking");
        bookingMap.put("deleteOption", true);

        manufacturerMap = new HashMap<>();
        manufacturerMap.put("heading", "Manufacturer");
        manufacturerMap.put("IDFieldName", "manufacturerID");
        manufacturerMap.put("title", "Manufacturers");
        manufacturerMap.put("headings", new String[] {"ID", "Name"});
        manufacturerMap.put("columnNames", new String[] {"manufacturerID", "name"});
        manufacturerMap.put("tableName", "manufacturer");
        manufacturerMap.put("deleteOption", true);

        transporterMap = new HashMap<>();
        transporterMap.put("heading", "Transporter");
        transporterMap.put("IDFieldName", "transporterID");
        transporterMap.put("title", "Transporter");
        transporterMap.put("headings", new String[] {"ID", "Name"});
        transporterMap.put("columnNames", new String[] {"transporterID", "name"});
        transporterMap.put("tableName", "transporter");
        transporterMap.put("deleteOption", true);

        HashMap<String, Object> locationMap = new HashMap<>();
        locationMap.put("heading", "Location");
        locationMap.put("IDFieldName", "locationID");
        locationMap.put("title", "Location");
        locationMap.put("headings", new String[] {"ID", "Longitude", "Latitude", "Opening Times"});
        locationMap.put("columnNames", new String[] {"locationID", "longitude", "latitude", "locationID"});
        locationMap.put("references", new Object[] {openingTimeMap});
        locationMap.put("tableName", "location");

        factoryMap = new HashMap<>();
        factoryMap.put("headings", new String[] {"ID", "Location", "Storage", "Manufacturer", "Vaccines Per Min"});
        factoryMap.put("columnNames", new String[] {"factoryID", "storageLocationID", "storageLocationID", "manufacturerID", "vaccinesPerMin"});
        factoryMap.put("references", new Object[] {storeMap, linkedLocationMap, manufacturerMap});
        factoryMap.put("tableName", "factory");
        factoryMap.put("deleteOption", true);

        transportLocationMap = new HashMap<>();
        transportLocationMap.put("headings", new String[] {"ID", "Location", "Transporter", "Available Capacity", "Total Capacity"});
        transportLocationMap.put("columnNames", new String[] {"transporterLocationID", "locationID", "transporterID", "availableCapacity", "totalCapacity"});
        transportLocationMap.put("references", new Object[] {locationMap, transporterMap});
        transportLocationMap.put("tableName", "transporterLocation");
        transportLocationMap.put("deleteOption", true);

        distributionCentreMap = new HashMap<>();
        distributionCentreMap.put("headings", new String[] {"ID", "Location", "Storage"});
        distributionCentreMap.put("columnNames", new String[] {"distributionCentreID", "storageLocationID", "storageLocationID"});
        distributionCentreMap.put("references", new Object[] {linkedLocationMap, storeMap});
        distributionCentreMap.put("tableName", "distributionCentre");
        distributionCentreMap.put("deleteOption", true);

        vaccinePriorityMap = new HashMap<>();
        vaccinePriorityMap.put("headings", new String[] {"Vaccine", "Lowest Age", "Highest Age", "Dose Number", "Position in Queue", "Eligible"});
        vaccinePriorityMap.put("columnNames", new String[] {"vaccineID", "lowestAge", "highestAge", "doseNumber", "positionInQueue", "eligible"});
        vaccinePriorityMap.put("references", new Object[] {vaccineMap});
        vaccinePriorityMap.put("tableName", "vaccinePriority");
        vaccinePriorityMap.put("deleteOption", true);
    }
}