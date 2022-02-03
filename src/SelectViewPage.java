import javax.swing.*;
import java.util.HashMap;

public class SelectViewPage extends SelectPage {

    private HashMap<String, Object> vaccineMap, personMap, medicalConditionMap, manufacturerMap, transporterMap, vaccinationCentreMap,
        bookingMap, openingTimeMap, factoryMap, transportLocationMap, distributionCentreMap, vaccinePriorityMap;

    private ViewPage viewVaccinePage, viewPersonPage, viewMedicalConditionPage, viewManufacturerPage, viewFactoryPage, viewTransporterPage,
        viewTransportLocationPage, viewDistributionCentrePage, viewVaccinationCentrePage, viewBookingPage, viewVaccinePriorityPage;

    public SelectViewPage(VaccineSystem vaccineSystem, MainPage mainPage) {
        super(vaccineSystem, mainPage, "view");
        createMaps();
        createViewPages();
        createPanels();
    }

    private void createMaps() {

        openingTimeMap = new HashMap<>();
        openingTimeMap.put("heading", "Opening Times");
        openingTimeMap.put("idFieldName", "locationID");
        openingTimeMap.put("title", "Opening Times");
        openingTimeMap.put("headings", new String[] {"Day", "Start Time", "End Time"});
        openingTimeMap.put("columnNames", new String[] {"day", "startTime", "endTime"});
        openingTimeMap.put("tableName", "openingTime");
        openingTimeMap.put("buttonText", "View");

        HashMap<String, Object> minimalMedicalConditionMap = new HashMap<>();
        minimalMedicalConditionMap.put("heading", "Medical Condition");
        minimalMedicalConditionMap.put("idFieldName", "medicalConditionID");
        minimalMedicalConditionMap.put("title", "Medical Condition");
        minimalMedicalConditionMap.put("headings", new String[] {"ID", "Name", "Vulnerability Level"});
        minimalMedicalConditionMap.put("columnNames", new String[] {"medicalConditionID", "name", "vulnerabilityLevel"});
        minimalMedicalConditionMap.put("tableName", "medicalCondition");

        HashMap<String, Object> vaccineExemptionMap = new HashMap<>();
        vaccineExemptionMap.put("heading", "Exemptions");
        vaccineExemptionMap.put("idFieldName", "vaccineID");
        vaccineExemptionMap.put("title", "Exemption(s)");
        vaccineExemptionMap.put("headings", new String[] {"Medical Condition"});
        vaccineExemptionMap.put("columnNames", new String[] {"medicalConditionID"});
        vaccineExemptionMap.put("references", new Object[] {minimalMedicalConditionMap});
        vaccineExemptionMap.put("tableName", "vaccineExemption");

        HashMap<String, Object> vaccineLifespanMap = new HashMap<>();
        vaccineLifespanMap.put("heading", "Lifespan");
        vaccineLifespanMap.put("idFieldName", "vaccineID");
        vaccineLifespanMap.put("title", "Lifespan(s)");
        vaccineLifespanMap.put("headings", new String[] {"Lifespan", "Lowest Temperature", "Highest Temperature"});
        vaccineLifespanMap.put("columnNames", new String[] {"lifespan", "lowestTemperature", "highestTemperature"});
        vaccineLifespanMap.put("tableName", "vaccineLifespan");
        vaccineLifespanMap.put("buttonText", "View");

        HashMap<String, Object> minimalVaccineMap = new HashMap<>();
        minimalVaccineMap.put("heading", "Vaccine");
        minimalVaccineMap.put("idFieldName", "vaccineID");
        minimalVaccineMap.put("title", "Vaccine");
        minimalVaccineMap.put("headings", new String[] {"ID", "Name", "Doses Needed", "Lifespan"});
        minimalVaccineMap.put("columnNames", new String[] {"vaccineID", "name", "dosesNeeded", "vaccineID"});
        minimalVaccineMap.put("references", new Object[] {vaccineLifespanMap});
        minimalVaccineMap.put("tableName", "vaccine");

        HashMap<String, Object> medicalExemptionMap = new HashMap<>();
        medicalExemptionMap.put("heading", "Exemptions");
        medicalExemptionMap.put("idFieldName", "medicalConditionID");
        medicalExemptionMap.put("title", "Exemption(s)");
        medicalExemptionMap.put("headings", new String[] {"Vaccine"});
        medicalExemptionMap.put("columnNames", new String[] {"vaccineID"});
        medicalExemptionMap.put("references", new Object[] {minimalVaccineMap});
        medicalExemptionMap.put("tableName", "vaccineExemption");

        vaccineMap = new HashMap<>();
        vaccineMap.put("heading", "Vaccine");
        vaccineMap.put("idFieldName", "vaccineID");
        vaccineMap.put("title", "Vaccine");
        vaccineMap.put("headings", new String[] {"ID", "Name", "Doses Needed", "Lifespan", "Exemptions"});
        vaccineMap.put("columnNames", new String[] {"vaccineID", "name", "dosesNeeded", "vaccineID", "vaccineID"});
        vaccineMap.put("references", new Object[] {vaccineLifespanMap, vaccineExemptionMap});
        vaccineMap.put("tableName", "vaccine");

        medicalConditionMap = new HashMap<>();
        medicalConditionMap.put("heading", "Medical Conditions");
        medicalConditionMap.put("idFieldName", "medicalConditionID");
        medicalConditionMap.put("title", "Medical Conditions");
        medicalConditionMap.put("headings", new String[] {"ID", "Name", "Vulnerability Level", "Exemptions"});
        medicalConditionMap.put("columnNames", new String[] {"medicalConditionID", "name", "vulnerabilityLevel", "medicalConditionID"});
        medicalConditionMap.put("references", new Object[] {medicalExemptionMap});
        medicalConditionMap.put("tableName", "medicalCondition");

        HashMap<String, Object> personVaccineMap = new HashMap<>();
        personVaccineMap.put("heading", "Vaccines");
        personVaccineMap.put("idFieldName", "personID");
        personVaccineMap.put("title", "Vaccines Received");
        personVaccineMap.put("headings", new String[] {"Vaccine", "Doses Received"});
        personVaccineMap.put("columnNames", new String[] {"vaccineID", "dosesRecieved"});
        personVaccineMap.put("references", new Object[] {vaccineMap});
        personVaccineMap.put("tableName", "personVaccine");
        personVaccineMap.put("buttonText", "View");

        HashMap<String, Object> personBookingMap = new HashMap<>();
        personBookingMap.put("heading", "Bookings");
        personBookingMap.put("idFieldName", "personID");
        personBookingMap.put("title", "Vaccination Centre");
        personBookingMap.put("headings", new String[] {"ID", "Vaccination Centre", "Date"});
        personBookingMap.put("columnNames", new String[] {"bookingID", "vaccinationCentreID", "date"});
        personBookingMap.put("references", new Object[] {vaccinationCentreMap});
        personBookingMap.put("tableName", "booking");

        personMap = new HashMap<>();
        personMap.put("heading", "Person");
        personMap.put("idFieldName", "personID");
        personMap.put("title", "Person");
        personMap.put("headings", new String[] {"ID", "Forename", "Surname", "DoB", "Medical Conditions", "Bookings", "Vaccines"});
        personMap.put("columnNames", new String[]  {"personID", "forename", "surname", "dob", "personID", "personID", "personID"});
        personMap.put("references", new Object[] {medicalConditionMap, personBookingMap, personVaccineMap});
        personMap.put("tableName", "person");

        HashMap<String, Object> vaccineInStorageMap = new HashMap<>();
        vaccineInStorageMap.put("heading", "Used Storage");
        vaccineInStorageMap.put("idFieldName", "storeID");
        vaccineInStorageMap.put("title", "Vaccines In Storage:");
        vaccineInStorageMap.put("headings", new String[] {"Vaccine", "Stock Level", "Expiration Date"});
        vaccineInStorageMap.put("columnNames", new String[] {"vaccineID", "stockLevel", "expirationDate"});
        vaccineInStorageMap.put("references", new Object[] {vaccineMap});
        vaccineInStorageMap.put("tableName", "vaccineInStorage");
        vaccineInStorageMap.put("buttonText", "View");

        HashMap<String, Object> storeMap = new HashMap<>();
        storeMap.put("heading", "Storage");
        storeMap.put("idFieldName", "storageLocationID");
        storeMap.put("title", "Storage Details:");
        storeMap.put("headings", new String[] {"Temperature", "Capacity", "Used Storage"});
        storeMap.put("columnNames", new String[] {"temperature", "capacity", "storeID"});
        storeMap.put("references", new Object[] {vaccineInStorageMap});
        storeMap.put("tableName", "store");
        storeMap.put("buttonText", "Details");

        HashMap<String, Object> linkedLocationMap = new HashMap<>();
        linkedLocationMap.put("heading", "Location");
        linkedLocationMap.put("linkerTableName", "storageLocation");
        linkedLocationMap.put("linkerIdFieldName", "locationID");
        linkedLocationMap.put("idFieldName", "storageLocationID");
        linkedLocationMap.put("title", "Location");
        linkedLocationMap.put("headings", new String[] {"ID", "Longitude", "Latitude", "Opening Times"});
        linkedLocationMap.put("columnNames", new String[] {"locationID", "longitude", "latitude", "locationID"});
        linkedLocationMap.put("references", new Object[] {openingTimeMap});
        linkedLocationMap.put("tableName", "location");
        linkedLocationMap.put("buttonText", "Details");

        vaccinationCentreMap = new HashMap<>();
        vaccinationCentreMap.put("heading", "Vaccination Centre");
        vaccinationCentreMap.put("idFieldName", "vaccinationCentreID");
        vaccinationCentreMap.put("title", "Vaccination Centre");
        vaccinationCentreMap.put("headings", new String[] {"ID", "Name", "Location", "Storage"});
        vaccinationCentreMap.put("columnNames", new String[] {"vaccinationCentreID", "name", "storageLocationID", "storageLocationID"});
        vaccinationCentreMap.put("references", new Object[] {linkedLocationMap, storeMap});
        vaccinationCentreMap.put("tableName", "vaccinationCentre");

        bookingMap = new HashMap<>();
        bookingMap.put("heading", "Bookings");
        bookingMap.put("idFieldName", "personID");
        bookingMap.put("title", "Vaccination Centre");
        bookingMap.put("headings", new String[] {"ID", "Person", "Vaccination Centre", "Date"});
        bookingMap.put("columnNames", new String[] {"bookingID", "personID", "vaccinationCentreID", "date"});
        bookingMap.put("references", new Object[] {personMap, vaccinationCentreMap});
        bookingMap.put("tableName", "booking");

        HashMap<String, Object> personMedicalConditionMap = new HashMap<>();
        personMedicalConditionMap.put("heading", "People");
        personMedicalConditionMap.put("idFieldName", "personID");
        personMedicalConditionMap.put("title", "Medical Conditions");
        personMedicalConditionMap.put("headings", new String[] {"Medical Condition"});
        personMedicalConditionMap.put("columnNames", new String[] {"medicalConditionID"});
        personMedicalConditionMap.put("references", new Object[] {medicalConditionMap});
        personMedicalConditionMap.put("tableName", "personMedicalCondition");
        personMedicalConditionMap.put("buttonText", "View");

        manufacturerMap = new HashMap<>();
        manufacturerMap.put("heading", "Manufacturer");
        manufacturerMap.put("idFieldName", "manufacturerID");
        manufacturerMap.put("title", "Manufacturers");
        manufacturerMap.put("headings", new String[] {"ID", "Name"});
        manufacturerMap.put("columnNames", new String[] {"manufacturerID", "name"});
        manufacturerMap.put("tableName", "manufacturer");

        transporterMap = new HashMap<>();
        transporterMap.put("heading", "Transporter");
        transporterMap.put("idFieldName", "transporterID");
        transporterMap.put("title", "Transporter");
        transporterMap.put("headings", new String[] {"ID", "Name"});
        transporterMap.put("columnNames", new String[] {"transporterID", "name"});
        transporterMap.put("tableName", "transporter");

        HashMap<String, Object> locationMap = new HashMap<>();
        locationMap.put("heading", "Location");
        locationMap.put("idFieldName", "locationID");
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

        transportLocationMap = new HashMap<>();
        transportLocationMap.put("headings", new String[] {"ID", "Location", "Transporter", "Available Capacity", "Total Capacity"});
        transportLocationMap.put("columnNames", new String[] {"transporterLocationID", "locationID", "transporterID", "availableCapacity", "totalCapacity"});
        transportLocationMap.put("references", new Object[] {locationMap, transporterMap});
        transportLocationMap.put("tableName", "transporterLocation");

        distributionCentreMap = new HashMap<>();
        distributionCentreMap.put("headings", new String[] {"ID", "Location", "Storage"});
        distributionCentreMap.put("columnNames", new String[] {"distributionCentreID", "storageLocationID", "storageLocationID"});
        distributionCentreMap.put("references", new Object[] {linkedLocationMap, storeMap});
        distributionCentreMap.put("tableName", "distributionCentre");

        vaccinePriorityMap = new HashMap<>();
        vaccinePriorityMap.put("headings", new String[] {"Vaccine", "Lowest Age", "Highest Age", "Dose Number", "Position in Queue", "Eligible"});
        vaccinePriorityMap.put("columnNames", new String[] {"vaccineID", "lowestAge", "highestAge", "doseNumber", "positionInQueue", "eligible"});
        vaccinePriorityMap.put("references", new Object[] {vaccineMap});
        vaccinePriorityMap.put("tableName", "vaccinePriority");
    }

    private ViewPage createViewPage(HashMap<String, Object> map, String title) {
        ViewPage viewPage;

        try {
            viewPage = new ViewPage(vaccineSystem, mainPage, title, (String[]) map.get("headings"),
                (String[]) map.get("columnNames"), (Object[]) map.get("references"), (String) map.get("tableName"));
        } catch (Exception e) {
            viewPage = new ViewPage(vaccineSystem, mainPage, title, (String[]) map.get("headings"),
                (String[]) map.get("columnNames"), (String) map.get("tableName"));
        }

        return viewPage;
    }

    private void createViewPages() {
        viewVaccinePage = createViewPage(vaccineMap, "Vaccines");
        viewPersonPage = createViewPage(personMap, "People");
        viewMedicalConditionPage = createViewPage(medicalConditionMap, "Medical Conditions");
        viewManufacturerPage = createViewPage(manufacturerMap, "Manufacturers");
        viewFactoryPage = createViewPage(factoryMap, "Factories");
        viewTransporterPage = createViewPage(transporterMap, "Transporters");
        viewTransportLocationPage = createViewPage(transportLocationMap, "Transport Locations");
        viewDistributionCentrePage = createViewPage(distributionCentreMap, "Distribution Centres");
        viewVaccinationCentrePage = createViewPage(vaccinationCentreMap, "Vaccination Centres");
        viewBookingPage = createViewPage(bookingMap, "Bookings");
        viewVaccinePriorityPage = createViewPage(vaccinePriorityMap, "Vaccine Priority");
    }

    private void createPanels() {

        JPanel viewVaccinePanel = viewVaccinePage.getPanel();
        JPanel viewPersonPanel = viewPersonPage.getPanel();
        JPanel viewMedicalConditionPanel = viewMedicalConditionPage.getPanel();
        JPanel viewManufacturerPanel = viewManufacturerPage.getPanel();
        JPanel viewFactoryPanel = viewFactoryPage.getPanel();
        JPanel viewTransporterPanel = viewTransporterPage.getPanel();
        JPanel viewTransportLocationPanel = viewTransportLocationPage.getPanel();
        JPanel viewDistributionCentrePanel = viewDistributionCentrePage.getPanel();
        JPanel viewVaccinationCentrePanel = viewVaccinationCentrePage.getPanel();
        JPanel viewBookingPanel = viewBookingPage.getPanel();
        JPanel viewVaccinePriorityPanel = viewVaccinePriorityPage.getPanel();

        mainPage.addCard(viewVaccinePanel, "view" + getSanitizedButtonText(vaccineButton));
        mainPage.addCard(viewPersonPanel, "view" + getSanitizedButtonText(personButton));
        mainPage.addCard(viewMedicalConditionPanel, "view" + getSanitizedButtonText(medicalConditionButton));
        mainPage.addCard(viewManufacturerPanel, "view" + getSanitizedButtonText(manufacturerButton));
        mainPage.addCard(viewFactoryPanel, "view" + getSanitizedButtonText(factoryButton));
        mainPage.addCard(viewTransporterPanel, "view" + getSanitizedButtonText(transporterButton));
        mainPage.addCard(viewTransportLocationPanel, "view" + getSanitizedButtonText(transportLocationButton));
        mainPage.addCard(viewDistributionCentrePanel, "view" + getSanitizedButtonText(distributionCentreButton));
        mainPage.addCard(viewVaccinationCentrePanel, "view" + getSanitizedButtonText(vaccinationCentreButton));
        mainPage.addCard(viewBookingPanel, "view" + getSanitizedButtonText(bookingButton));
        mainPage.addCard(viewVaccinePriorityPanel, "view" + getSanitizedButtonText(vaccinePriorityButton));
    }
}
