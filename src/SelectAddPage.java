import javax.swing.*;
import java.awt.event.ActionEvent;

public class SelectAddPage extends SelectPage {

    private JButton stockButton;

    public SelectAddPage(VaccineSystem vaccineSystem, MainPage mainPage) {
        super(vaccineSystem, mainPage, "add");
        createAddingPages();
    }

    private void createAddingPages() {


        // MOVE THIS TO ADD OPTION IN VIEW PAGES
        stockButton = new JButton("Stock");
        buttons.add(stockButton);
        addButton(stockButton, mainPanel);

        AddVaccinePage addVaccinePage = new AddVaccinePage(vaccineSystem, mainPage);
        AddPersonPage addPersonPage = new AddPersonPage(vaccineSystem, mainPage);
        AddMedicalConditionPage addMedicalConditionPage = new AddMedicalConditionPage(vaccineSystem, mainPage);
        AddManufacturerPage addManufacturerPage = new AddManufacturerPage(vaccineSystem, mainPage);
        AddFactoryPage addFactoryPage = new AddFactoryPage(vaccineSystem, mainPage);
        AddTransporterPage addTransporterPage = new AddTransporterPage(vaccineSystem, mainPage);
        AddTransporterLocationPage addTransporterLocationPage = new AddTransporterLocationPage(vaccineSystem, mainPage);
        AddDistributionCentrePage addDistributionCentrePage = new AddDistributionCentrePage(vaccineSystem, mainPage);
        AddVaccinationCentrePage addVaccinationCentrePage = new AddVaccinationCentrePage(vaccineSystem, mainPage);
        AddBookingPage addBookingPage = new AddBookingPage(vaccineSystem, mainPage);
        AddVaccinePriorityPage addVaccinePriorityPage = new AddVaccinePriorityPage(vaccineSystem, mainPage);
        AddStockPage addStockPage = new AddStockPage(vaccineSystem, mainPage);

        JPanel addVaccinePanel = addVaccinePage.getPanel();
        JPanel addPersonPanel = addPersonPage.getPanel();
        JPanel addMedicalConditionPanel = addMedicalConditionPage.getPanel();
        JPanel addManufacturerPanel = addManufacturerPage.getPanel();
        JPanel addFactoryPanel = addFactoryPage.getPanel();
        JPanel addTransporterPanel = addTransporterPage.getPanel();
        JPanel addTransportLocationPanel = addTransporterLocationPage.getPanel();
        JPanel addDistributionCentrePanel = addDistributionCentrePage.getPanel();
        JPanel addVaccinationCentrePanel = addVaccinationCentrePage.getPanel();
        JPanel addBookingPanel = addBookingPage.getPanel();
        JPanel addVaccinePriorityPanel = addVaccinePriorityPage.getPanel();
        JPanel addStockPanel = addStockPage.getPanel();

        mainPage.addCard(addVaccinePanel, "add" + getSanitizedButtonText(vaccineButton));
        mainPage.addCard(addPersonPanel, "add" + getSanitizedButtonText(personButton));
        mainPage.addCard(addMedicalConditionPanel, "add" + getSanitizedButtonText(medicalConditionButton));
        mainPage.addCard(addManufacturerPanel, "add" + getSanitizedButtonText(manufacturerButton));
        mainPage.addCard(addFactoryPanel, "add" + getSanitizedButtonText(factoryButton));
        mainPage.addCard(addTransporterPanel, "add" + getSanitizedButtonText(transporterButton));
        mainPage.addCard(addTransportLocationPanel, "add" + getSanitizedButtonText(transportLocationButton));
        mainPage.addCard(addDistributionCentrePanel, "add" + getSanitizedButtonText(distributionCentreButton));
        mainPage.addCard(addVaccinationCentrePanel, "add" + getSanitizedButtonText(vaccinationCentreButton));
        mainPage.addCard(addBookingPanel, "add" + getSanitizedButtonText(bookingButton));
        mainPage.addCard(addVaccinePriorityPanel, "add" + getSanitizedButtonText(vaccinePriorityButton));
        mainPage.addCard(addStockPanel, "add" + getSanitizedButtonText(stockButton));

    }

    // Crude solution to updating selection boxes when items are added to database
    public void actionPerformed(ActionEvent e) {
        for (JButton button : buttons) {
            if (e.getSource() == button) {
                createAddingPages();
                mainPage.setPageName(buttonAction + getSanitizedButtonText(button));
                mainPage.updatePage();
            }
        }
    }
}
