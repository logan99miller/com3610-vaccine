import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.ArrayList;

public class AddVaccineLifespanPage extends AddPage {

    private AddVaccinePage addVaccinePage;
    private ArrayList<AddVaccineLifespan> addVaccineLifespans;
    private JFrame addVaccineLifespanFrame;
    private int frameWidth;

    public AddVaccineLifespanPage(AddVaccinePage addVaccinePage, JFrame addVaccineLifespanFrame, int frameWidth) {
        this.addVaccinePage = addVaccinePage;
        this.addVaccineLifespanFrame = addVaccineLifespanFrame;
        this.frameWidth = frameWidth;

        mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));

        createPageTitle("Lifespans:");
        createInputFieldsPanel();
        createAddLifespanPanel();
        createSubmitButton();
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
        if (e.getSource() == submitButton) {
            addVaccinePage.setAddLifespans(addVaccineLifespans);
            addVaccineLifespanFrame.setVisible(false);
        }
    }
}
