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

        mapPanel = new MapPanel(vaccineSystem, panelWidth, panelHeight);
        mainPanel.add(mapPanel, BorderLayout.CENTER);
    }

    /**
     * Automatically called every time the system updates (defined by the updateRate)
     */
    public void refreshPage() {
        mapPanel.refresh(vaccineSystem);
        mapPanel.repaint();

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
            framePanel.setAlignmentX(Component.CENTER_ALIGNMENT);

            JPanel gridPanel = new JPanel();
            gridPanel.setLayout(new GridLayout(0, 2));

            gridPanel.add(new JLabel(" Factory:"));
            gridPanel.add(new JLabel(" Building with chimney"));
            gridPanel.add(new JLabel(" Transporter Location:"));
            gridPanel.add(new JLabel(" Van"));
            gridPanel.add(new JLabel(" Distribution Centre:"));
            gridPanel.add(new JLabel(" Square building"));
            gridPanel.add(new JLabel(" Vaccination Centre:"));
            gridPanel.add(new JLabel(" Vaccine"));
            setMaxWidthMinHeight(gridPanel);

            framePanel.add(gridPanel);

            createPopupFrame(frame, framePanel, 400, 100);
        }
    }
}
