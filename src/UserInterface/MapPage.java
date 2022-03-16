package UserInterface;

import Core.VaccineSystem;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;

public class MapPage extends Page {

    private JButton keyButton;
    private MapPanel mapPanel;
    private int panelWidth, panelHeight;

    public MapPage(VaccineSystem vaccineSystem) {
        super(vaccineSystem);

        mainPanel = new JPanel();

        keyButton = new JButton("Key");
        addButton(keyButton, mainPanel);

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

    public void refreshPage() {
        mainPanel.remove(mapPanel);
        createMapPanel();

        vaccineSystem.invalidate();
        vaccineSystem.validate();
        vaccineSystem.repaint();
    }

    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == keyButton) {
            JFrame frame = new JFrame();
            frame.setResizable(false);

            JPanel framePanel = new JPanel();
            framePanel.setLayout(new BoxLayout(framePanel, BoxLayout.Y_AXIS));
            framePanel.add(new JLabel("Factory: circle"));
            framePanel.add(new JLabel("Transporter Location: square"));
            framePanel.add(new JLabel("Distribution Centre: triangle"));
            framePanel.add(new JLabel("Vaccination Centre: diamond"));

            createPopupFrame(frame, framePanel, 400, 400);
        }
    }
}
