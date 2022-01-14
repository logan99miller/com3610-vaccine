import java.awt.event.ActionEvent;

public class AddDistributionCentrePage extends AddStorageLocationPage {

    public AddDistributionCentrePage(VaccineSystem vaccineSystem, MainPage mainPage) {
        super(vaccineSystem, mainPage, "Add Distribution Centre:");
        fitPanelToMainPanel(inputFieldsPanel);
    }

    protected void createStatements() {
        statements.add("INSERT INTO DistributionCentre (storageLocationID) VALUES (" + storageLocationID + ");");
    }

    public void actionPerformed(ActionEvent e) {
        if ((e.getSource() == submitButton) && (checkCoordinates()) && (fieldConditionsMet())) {
            super.createStatements();
            createStatements();
            super.actionPerformed(e);
        }
        else {
            super.actionPerformed(e);
        }
    }
}
