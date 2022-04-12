package UserInterface.SelectPages;

import UserInterface.AddPages.AddPage;
import UserInterface.AddPages.*;
import Core.VaccineSystem;
import UserInterface.AddPopupPages.AddStockPage;
import UserInterface.LoggedInPage;

import javax.swing.*;
import java.util.HashMap;
import java.util.Map;

public class SelectAddPage extends SelectPage {

    public SelectAddPage(VaccineSystem vaccineSystem, LoggedInPage loggedInPage) {
        super(vaccineSystem, loggedInPage, " Add");
        addButtons(createPages());
    }

    private HashMap<String, Class> createPages() {
        HashMap<String, Class> pages = new HashMap<>();
        pages.put("Vaccines", AddVaccinePage.class);
        pages.put("People", AddPersonPage.class);
        pages.put("Medical Conditions", AddMedicalConditionPage.class);
        pages.put("Manufacturers", AddManufacturerPage.class);
        pages.put("Factories", AddFactoryPage.class);
        pages.put("Transporters", AddTransporterPage.class);
        pages.put("Transport Locations", AddTransporterLocationPage.class);
        pages.put("Distribution Centres", AddDistributionCentrePage.class);
        pages.put("Vaccination Centres", AddVaccinationCentrePage.class);
        pages.put("Bookings", AddBookingPage.class);
        pages.put("Stocks", AddStockPage.class);
        pages.put("Vaccination", AddVaccinationPage.class);
        return pages;
    }

    private void addButtons(HashMap<String, Class> pages) {
        for (Map.Entry<String, Class> set : pages.entrySet()) {
            JButton button = new JButton(set.getKey());
            button.addActionListener(e -> {
                try {
                    AddPage addPage = (AddPage) set.getValue().asSubclass(AddPage.class)
                        .getConstructor(VaccineSystem.class, LoggedInPage.class)
                        .newInstance(vaccineSystem, loggedInPage);
                    loggedInPage.updatePageToComponent(addPage.getPanel());
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            });
            mainPanel.add(button);
        }
    }
}
