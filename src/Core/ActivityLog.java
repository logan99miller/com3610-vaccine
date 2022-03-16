package Core;

import java.util.LinkedList;

public class ActivityLog {

    private final int LOG_SIZE = 100;
    private final String WARNING_PREFIX = "WARNING: ";
    private LinkedList<String> log;

    public ActivityLog() {
        log = new LinkedList<>();
    }

    public void add(String string, boolean warning) {
        if (log.size() > LOG_SIZE) {
            log.removeFirst();
        }

        if (warning) {
            string = WARNING_PREFIX + string;
        }

        log.add(string);
    }

    public void add(String string) {
        add(string, false);
    }

    public LinkedList<String> getLog() {
        return log;
    }

    public String getWARNING_PREFIX() {
        return WARNING_PREFIX;
    }
}
