import javax.swing.*;

public class SelectAddPage extends SelectTablePage {

    public SelectAddPage(VaccineSystem vaccineSystem, MainPage mainPage) {
        super(vaccineSystem, mainPage, "add");
        createAddingPages();
    }

    private void createAddingPages() {

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

        mainPage.addCard(addVaccinePanel, "add" + getSanatizedtext(vaccineButton));
        mainPage.addCard(addPersonPanel, "add" + getSanatizedtext(personButton));
        mainPage.addCard(addMedicalConditionPanel, "add" + getSanatizedtext(medicalConditionButton));
        mainPage.addCard(addManufacturerPanel, "add" + getSanatizedtext(manufacturerButton));
        mainPage.addCard(addFactoryPanel, "add" + getSanatizedtext(factoryButton));
        mainPage.addCard(addTransporterPanel, "add" + getSanatizedtext(transporterButton));
        mainPage.addCard(addTransportLocationPanel, "add" + getSanatizedtext(transportLocationButton));
        mainPage.addCard(addDistributionCentrePanel, "add" + getSanatizedtext(distributionCentreButton));
        mainPage.addCard(addVaccinationCentrePanel, "add" + getSanatizedtext(vaccinationCentreButton));
        mainPage.addCard(addBookingPanel, "add" + getSanatizedtext(bookingButton));
    }
}
