package UserInterface.AddPopupPages;

import UserInterface.AddPages.AddVaccinePage;
import UserInterface.AddPopupPage;
import UserInterface.AddUtils.AddVaccineLifespan;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.ArrayList;

public class AddVaccineLifespanPage extends AddPopupPage {

    private AddVaccinePage addVaccinePage;
    private ArrayList<AddVaccineLifespan> addVaccineLifespans;

    public AddVaccineLifespanPage(AddVaccinePage addVaccinePage, JFrame frame, int frameWidth) {
        super(frame, frameWidth);

        this.addVaccinePage = addVaccinePage;

        createAddLifespanPanel();
    }

    private void createAddLifespanPanel() {
        JComboBox numStoreComboBox = addVaccinePage.getNumLifespanComboBox();
        int numTemperatures = Integer.parseInt((String) numStoreComboBox.getSelectedItem());

        addVaccineLifespans = new ArrayList<>();

        for (int i = 0; i < numTemperatures; i++) {
            addVaccineLifespans.add(new AddVaccineLifespan());
        }

        for (AddVaccineLifespan addVaccineLifespan : addVaccineLifespans) {
            JPanel panel = addVaccineLifespan.getPanel();
            panel.setMaximumSize(new Dimension(frameWidth, panel.getMinimumSize().height));
            inputPanel.add(panel);
        }
    }

    public void actionPerformed(ActionEvent e) {
        super.actionPerformed(e);
        if (e.getSource() == submitButton) {
            addVaccinePage.setAddLifespans(addVaccineLifespans);
            frame.setVisible(false);
        }
    }
}
