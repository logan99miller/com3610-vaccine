/**
 * Displays the system's activity log. Automatically refreshed by LoggedInPage's autoRefresh() function.
 */
package UserInterface;

import Core.ActivityLog;
import Core.VaccineSystem;
import java.awt.Color;

import javax.swing.*;
import java.util.Iterator;
import java.util.LinkedList;

public class ActivityLogPage extends Page {

    private ActivityLog activityLog;
    private String warningPrefix;
    private JScrollPane logScrollPane;

    public ActivityLogPage(VaccineSystem vaccineSystem) {
        super(vaccineSystem);

        mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));

        activityLog = vaccineSystem.getActivityLog();
        warningPrefix = activityLog.getWARNING_PREFIX();

        logScrollPane = createLogScrollPane();

        mainPanel.add(logScrollPane);
    }

    private JScrollPane createLogScrollPane() {
        JPanel logPanel = createLogPanel();
        setMaxWidthMinHeight(logPanel);

        JScrollPane logScrollPane = new JScrollPane(logPanel);
        logScrollPane.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));

        return logScrollPane;
    }

    /**
     * Creates a panel containing a list of all logs found in the activity log by iterating through them in reverse order
     * @return
     */
    private JPanel createLogPanel() {
        LinkedList<String> log = activityLog.getLog();

        JPanel logPanel = new JPanel();
        logPanel.setLayout(new BoxLayout(logPanel, BoxLayout.Y_AXIS));

        if (log.size() == 0) {
            logPanel.add(new JLabel("No data"));
        }
        else {

            // Iterate in reverse order so more recent logs are at the top
            Iterator<String> iterator = log.descendingIterator();
            while (iterator.hasNext()) {

                String logString = iterator.next();
                JLabel logLabel = new JLabel(" - " + logString);

                // If log is a warning, highlight it to user by changing it's colour
                if (logString.contains(warningPrefix)) {
                    logLabel.setForeground(Color.RED);
                }

                logPanel.add(logLabel);
            }
        }
        return logPanel;
    }

    /**
     * Automatically called every time the system updates (defined by the updateRate)
     */
    public void refreshPage() {
        mainPanel.remove(logScrollPane);
        logScrollPane = createLogScrollPane();
        mainPanel.add(logScrollPane);

        vaccineSystem.invalidate();
        vaccineSystem.validate();
        vaccineSystem.repaint();
    }
}
