package UserInterface;

import Core.VaccineSystem;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;

public class MapPage extends Page {

    private JButton refreshButton;
    private MapPanel mapPanel;
    private int panelWidth, panelHeight;

    public MapPage(VaccineSystem vaccineSystem) {
        super(vaccineSystem);

        mainPanel = new JPanel();

        refreshButton = new JButton("Refresh");
        refreshButton.addActionListener(this);
        mainPanel.add(refreshButton);

        final int BORDER = 10;
        final int HEADER_HEIGHT = 100;

        panelWidth = vaccineSystem.getWidth() - (BORDER * 2);
        panelHeight = vaccineSystem.getHeight() - HEADER_HEIGHT - (BORDER * 2);

        createMapPanel();
    }

    private void createMapPanel() {
        mapPanel = new MapPanel(vaccineSystem, panelWidth, panelHeight);
        mainPanel.add(mapPanel, BorderLayout.CENTER);
    }

    private void refreshPage() {
        mainPanel.remove(mapPanel);
        createMapPanel();

        vaccineSystem.invalidate();
        vaccineSystem.validate();
        vaccineSystem.repaint();
    }

    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == refreshButton) {
            refreshPage();
        }
    }

}
