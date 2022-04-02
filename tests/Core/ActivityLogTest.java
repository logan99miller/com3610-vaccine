package Core;

import java.util.LinkedList;

import static org.junit.Assert.*;

class ActivityLogTest {

    @org.junit.jupiter.api.Test
    void add() {
        ActivityLog activityLog = new ActivityLog();

        activityLog.add("Test");
        activityLog.add("Testing", true);
        activityLog.add("Testing", false);
    }

    @org.junit.jupiter.api.Test
    void getLog() {
        ActivityLog activityLog = new ActivityLog();

        activityLog.add("Test 1");
        activityLog.add("Test 2");

        LinkedList<String> linkedList = new LinkedList<>();
        linkedList.add("Test 1");
        linkedList.add("Test 2");

        assertEquals(linkedList, activityLog.getLog());
    }

    @org.junit.jupiter.api.Test
    void getWARNING_PREFIX() {
        ActivityLog activityLog = new ActivityLog();

        assertEquals("WARNING: ", activityLog.getWARNING_PREFIX());
    }
}