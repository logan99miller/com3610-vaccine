package UserInterface.AddPages;

import Core.VaccineSystem;
import UserInterface.MainPage;
import java.awt.event.ActionEvent;

public class AddDistributionCentrePage extends AddStorageLocationPage {

    public AddDistributionCentrePage(VaccineSystem vaccineSystem, MainPage mainPage) {
        super(vaccineSystem, mainPage, "Add Distribution Centre:");
        setMaxWidthMinHeight(inputPanel);
    }

    protected void createStatements() {
        super.createStatements();
        String[] columnNames = new String[] {"storageLocationID"};
        Object[] values = new Object[] {storageLocationID};
        inserts.add(new Insert(columnNames, values, "DistributionCentre"));
    }

    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == submitButton) {
            createStatements();
        }
        super.actionPerformed(e);
    }
}
