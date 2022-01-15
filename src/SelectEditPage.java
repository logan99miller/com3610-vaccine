import javax.swing.*;

public class SelectEditPage extends SelectTablePage {

    public SelectEditPage(VaccineSystem vaccineSystem, MainPage mainPage) {
        super(vaccineSystem, mainPage, "edit");
        createEditingPages();
    }

    private void createEditingPages() {

        Page editVaccinePage = new Page(vaccineSystem);
        Page editPersonPage = new Page(vaccineSystem);
        Page editMedicalConditionPage = new Page(vaccineSystem);
        Page editManufacturerPage = new Page(vaccineSystem);
        Page editFactoryPage = new Page(vaccineSystem);
        Page editTransporterPage = new Page(vaccineSystem);
        Page editTransportLocationPage = new Page(vaccineSystem);
        Page editDistributionCentrePage = new Page(vaccineSystem);
        Page editVaccinationCentrePage = new Page(vaccineSystem);
        Page editBookingPage = new Page(vaccineSystem);

        JPanel editVaccinePanel = editVaccinePage.getPanel();
        JPanel editPersonPanel = editPersonPage.getPanel();
        JPanel editMedicalConditionPanel = editMedicalConditionPage.getPanel();
        JPanel editManufacturerPanel = editManufacturerPage.getPanel();
        JPanel editFactoryPanel = editFactoryPage.getPanel();
        JPanel editTransporterPanel = editTransporterPage.getPanel();
        JPanel editTransportLocationPanel = editTransportLocationPage.getPanel();
        JPanel editDistributionCentrePanel = editDistributionCentrePage.getPanel();
        JPanel editVaccinationCentrePanel = editVaccinationCentrePage.getPanel();
        JPanel editBookingPanel = editBookingPage.getPanel();

        mainPage.addCard(editVaccinePanel, "edit" + getSanitizedButtonText(vaccineButton));
        mainPage.addCard(editPersonPanel, "edit" + getSanitizedButtonText(personButton));
        mainPage.addCard(editMedicalConditionPanel, "edit" + getSanitizedButtonText(medicalConditionButton));
        mainPage.addCard(editManufacturerPanel, "edit" + getSanitizedButtonText(manufacturerButton));
        mainPage.addCard(editFactoryPanel, "edit" + getSanitizedButtonText(factoryButton));
        mainPage.addCard(editTransporterPanel, "edit" + getSanitizedButtonText(transporterButton));
        mainPage.addCard(editTransportLocationPanel, "edit" + getSanitizedButtonText(transportLocationButton));
        mainPage.addCard(editDistributionCentrePanel, "edit" + getSanitizedButtonText(distributionCentreButton));
        mainPage.addCard(editVaccinationCentrePanel, "edit" + getSanitizedButtonText(vaccinationCentreButton));
        mainPage.addCard(editBookingPanel, "edit" + getSanitizedButtonText(bookingButton));
    }
}
