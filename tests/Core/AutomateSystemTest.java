package Core;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class AutomateSystemTest {

    @Test
    void start() {
        VaccineSystem vaccineSystem = new VaccineSystem("Title Text", false);
        ActivityLog activityLog = new ActivityLog();
        AutomateSystem automateSystem = new AutomateSystem();

        automateSystem.start(activityLog, vaccineSystem);
    }

    @Test
    void run() {
        VaccineSystem vaccineSystem = new VaccineSystem("Title Text", false);
        ActivityLog activityLog = new ActivityLog();
        AutomateSystem automateSystem = new AutomateSystem();

        automateSystem.start(activityLog, vaccineSystem);
        automateSystem.run();
    }
}