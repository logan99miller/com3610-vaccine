package UserInterface;

import Core.Data;
import Core.VaccineSystem;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

public class MapPage extends Page {

    public MapPage(VaccineSystem vaccineSystem, MainPage mainPage) {
        super(vaccineSystem);
        mainPanel = new JPanel();

        final int BORDER = 10;
        final int NAV_PANEL_HEIGHT = mainPage.getNavPanel().getMinimumSize().height; // Nav height not accurate so just multiplied by 2 in PANEL_HEIGHT
        final int PANEL_WIDTH = vaccineSystem.getWidth() - (BORDER * 2);
        final int PANEL_HEIGHT = vaccineSystem.getHeight() - (NAV_PANEL_HEIGHT * 2) - (BORDER * 2);

        System.out.println("PANEL DIMENSIONS: " + PANEL_WIDTH + ", " + PANEL_HEIGHT);
        MapPanel mapPanel = new MapPanel(vaccineSystem, PANEL_WIDTH, PANEL_HEIGHT);
        mainPanel.add(mapPanel, BorderLayout.CENTER);

        mapPanel.setBorder(BorderFactory.createLineBorder(Color.black));
    }

}
