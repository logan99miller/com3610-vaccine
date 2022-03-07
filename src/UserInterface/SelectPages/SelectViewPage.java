package UserInterface.SelectPages;

import Core.Data;
import Core.VaccineSystem;
import UserInterface.MainPage;
import UserInterface.ViewPage;

import javax.swing.*;
import java.util.*;

public class SelectViewPage extends SelectPage {

    public SelectViewPage(VaccineSystem vaccineSystem, MainPage mainPage) {
        super(vaccineSystem, mainPage, "view");

        Data data = vaccineSystem.getData();

        HashMap<String, List<String>> keys = new HashMap<>();
        keys.put("Factories", Arrays.asList("Factory.factoryID", "Location.longitude", "Location.latitude", "Manufacturer.name", "Manufacturer.vaccineID", "Factory.vaccinesPerMin", "openingTimes", "stores"));
        keys.put("Transporter Locations", Arrays.asList("TransporterLocation.transporterLocationID", "Location.longitude", "Location.latitude", "Transporter.name"));
        keys.put("Distribution Centres", Arrays.asList("DistributionCentre.distributionCentreID", "Location.longitude", "Location.latitude", "openingTimes", "stores"));
        keys.put("Vaccination Centres", Arrays.asList("VaccinationCentre.vaccinationCentreID", "VaccinationCentre.name", "Location.longitude", "Location.latitude", "VaccinationCentre.vaccinesPerHour", "bookings", "openingTimes", "stores"));
        keys.put("Vaccines", Arrays.asList("Vaccine.vaccineID", "Vaccine.name", "Vaccine.dosesNeeded", "Vaccine.daysBetweenDoses", "lifespans", "exemptions"));
        keys.put("People", Arrays.asList("Person.personID", "Person.forename", "Person.surname", "Person.DoB", "bookings", "medicalConditions", "vaccinesReceived"));
        keys.put("Vans", Arrays.asList("Van.vanID", "Van.deliveryStage", "Van.totalTime", "Van.remainingTime", "Van.originID", "Van.destinationID", "Van.transporterLocationID"));

        HashMap<String, List<String>> headings = new HashMap<>();
        headings.put("Factories", Arrays.asList("ID", "Longitude", "Latitude", "Manufacturer", "Vaccine ID", "Vaccine Per Min", "Opening Times", "Stores"));
        headings.put("Transporter Locations", Arrays.asList("ID", "Longitude", "Latitude", "Transporter"));
        headings.put("Distribution Centres", Arrays.asList("ID", "Longitude", "Latitude", "Opening Times", "Stores"));
        headings.put("Vaccination Centres", Arrays.asList("ID", "Name", "Longitude", "Latitude", "Vaccines Per Hour", "Bookings", "Opening Times", "Stores"));
        headings.put("Vaccines", Arrays.asList("ID", "Name", "Doses Needed", "Days Between Doses", "Lifespans", "Exemptions"));
        headings.put("People", Arrays.asList("ID", "Forename", "Surname", "DoB", "Bookings", "Medical Conditions", "Vaccines Received"));
        headings.put("Vans", Arrays.asList("ID", "Delivery Stage", "Total Time", "Remaining Time", "Origin ID", "Destination ID", "Transporter Location"));

        for (String mapKey : keys.keySet()) {
            JButton button = new JButton(mapKey);
            button.addActionListener(e -> {
                ViewPage viewPage = new ViewPage(vaccineSystem, mainPage, mapKey, keys.get(mapKey), headings.get(mapKey));
                mainPage.updatePageToComponent(viewPage.getPanel());
            });
            mainPanel.add(button);
        }
    }
}
