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

        mainPage.addCard(editVaccinePanel, "edit" + getSanatizedtext(vaccineButton));
        mainPage.addCard(editPersonPanel, "edit" + getSanatizedtext(personButton));
        mainPage.addCard(editMedicalConditionPanel, "edit" + getSanatizedtext(medicalConditionButton));
        mainPage.addCard(editManufacturerPanel, "edit" + getSanatizedtext(manufacturerButton));
        mainPage.addCard(editFactoryPanel, "edit" + getSanatizedtext(factoryButton));
        mainPage.addCard(editTransporterPanel, "edit" + getSanatizedtext(transporterButton));
        mainPage.addCard(editTransportLocationPanel, "edit" + getSanatizedtext(transportLocationButton));
        mainPage.addCard(editDistributionCentrePanel, "edit" + getSanatizedtext(distributionCentreButton));
        mainPage.addCard(editVaccinationCentrePanel, "edit" + getSanatizedtext(vaccinationCentreButton));
        mainPage.addCard(editBookingPanel, "edit" + getSanatizedtext(bookingButton));
    }
}
