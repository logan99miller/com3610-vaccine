package UserInterface;

import Core.ActivityLog;
import Core.VaccineSystem;

import javax.swing.*;
import java.util.LinkedList;

public class ActivityLogPage extends Page {

    public ActivityLogPage(VaccineSystem vaccineSystem) {
        super(vaccineSystem);

        mainPanel = new JPanel();

        JPanel logPanel = new JPanel();

        ActivityLog activityLog = vaccineSystem.getActivityLog();
        LinkedList<String> log = activityLog.getLog();
        for (String string : log) {
            logPanel.add(new JLabel(string));
        }
        logPanel.add(new JLabel("Hello world"));

        setMaxWidthMinHeight(logPanel);

        JScrollPane logScrollPane = new JScrollPane(logPanel);
        logScrollPane.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));
        mainPanel.add(logScrollPane);
    }

}
