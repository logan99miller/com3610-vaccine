/**
 * Displays the location of each factory, distribution centre, vaccination centre and transporter location and a line
 * indicating any van moving between them.
 */
package UserInterface.Map;

import Core.VaccineSystem;
import UserInterface.Page;

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

    /**
     * Not included in the constructor as it is called when the map panel is refreshed
     */
    private void createMapPanel() {
        mapPanel = new MapPanel(vaccineSystem, panelWidth, panelHeight);
        mainPanel.add(mapPanel, BorderLayout.CENTER);
    }

    /**
     * Automatically called every time the system updates (defined by the updateRate)
     */
    public void refreshPage() {
        mainPanel.remove(mapPanel);
        createMapPanel();

        vaccineSystem.invalidate();
        vaccineSystem.validate();
        vaccineSystem.repaint();
    }

    /**
     * Creates a popup frame explaining each symbol if the key button is pressed
     */
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
