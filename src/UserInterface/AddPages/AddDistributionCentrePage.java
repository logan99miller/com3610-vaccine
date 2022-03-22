/**
 * Page used to insert a distribution centre into the system's database
 */
package UserInterface.AddPages;

import Core.VaccineSystem;
import UserInterface.AddUtils.Insert;
import UserInterface.LoggedInPage;
import java.awt.event.ActionEvent;

public class AddDistributionCentrePage extends AddStorageLocationPage {

    public AddDistributionCentrePage(VaccineSystem vaccineSystem, LoggedInPage loggedInPage) {
        super(vaccineSystem, loggedInPage, "Add Distribution Centre:");
        setMaxWidthMinHeight(inputPanel);
    }

    /**
     * Creates the SQL statements required and adds them to the inserts list
     */
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
