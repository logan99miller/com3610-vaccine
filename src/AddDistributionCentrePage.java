import java.awt.event.ActionEvent;
import java.util.ArrayList;

public class AddDistributionCentrePage extends AddStorageLocationPage {

    public AddDistributionCentrePage(VaccineSystem vaccineSystem, MainPage mainPage) {
        super(vaccineSystem, mainPage, "Add Distribution Centre:");
        setMaxWidthMinHeight(inputPanel);
    }

    protected void createStatements() {
        statements = new ArrayList<>();
        statements.add("INSERT INTO DistributionCentre (storageLocationID) VALUES (" + storageLocationID + ");");
    }

    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == submitButton) {
            createStatements();
        }
        super.actionPerformed(e);
    }
}
