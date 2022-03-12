package Core;

import java.util.LinkedList;

public class ActivityLog {

    private final int LOG_SIZE = 20;
    private LinkedList<String> log;

    public ActivityLog() {
        log = new LinkedList<>();
    }

    public void add(String string) {
        if (log.size() > LOG_SIZE) {
            log.removeFirst();
        }
        log.add(string);
    }

    public LinkedList<String> getLog() {
        return log;
    }
}
