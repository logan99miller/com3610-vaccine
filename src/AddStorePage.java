import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.ArrayList;

public class AddStorePage extends AddPage {

    private AddStorageLocationPage addStorageLocationPage;
    private ArrayList<AddStore> addStores;
    private JFrame addStoreFrame;
    private int frameWidth;

    public AddStorePage(AddStorageLocationPage addStorageLocationPage, JFrame addStoreFrame, int frameWidth) {
        this.addStorageLocationPage = addStorageLocationPage;
        this.addStoreFrame = addStoreFrame;
        this.frameWidth = frameWidth;

        mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));

        createPageTitle("Storage Capacities:");
        createInputFieldsPanel();
        createAddStorePanel();
        createSubmitButton();
    }

    private void createAddStorePanel() {
        JComboBox numStoreComboBox = addStorageLocationPage.getNumStoreComboBox();
        int numTemperatures = Integer.parseInt((String) numStoreComboBox.getSelectedItem());

        addStores = new ArrayList<>();

        for (int i = 0; i < numTemperatures; i++) {
            addStores.add(new AddStore());
        }

        for (AddStore addStore : addStores) {
            JPanel panel = addStore.getPanel();
            panel.setMaximumSize(new Dimension(frameWidth, panel.getMinimumSize().height));
            inputPanel.add(panel);
        }
    }

    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == submitButton) {
            addStorageLocationPage.setAddStores(addStores);
            addStoreFrame.setVisible(false);
        }
    }
}
