package UserInterface.AddPopupPages;

import UserInterface.AddPages.AddStorageLocationPage;
import UserInterface.AddUtils.AddStore;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.ArrayList;

public class AddStorePage extends AddPopupPage {

    private AddStorageLocationPage addStorageLocationPage;
    private ArrayList<AddStore> addStores;

    public AddStorePage(AddStorageLocationPage addStorageLocationPage, JFrame frame, int frameWidth) {
        super(frame, frameWidth);

        this.addStorageLocationPage = addStorageLocationPage;

        createAddStorePanel();
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
        super.actionPerformed(e);
        if (e.getSource() == submitButton) {
            addStorageLocationPage.setAddStores(addStores);
            frame.setVisible(false);
        }
    }
}
