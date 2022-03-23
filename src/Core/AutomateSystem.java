package Core;

import java.sql.SQLException;
import java.util.HashMap;

import Automation.*;
import Data.Data;
import static Data.Update.*;

public class AutomateSystem {

    private ActivityLog activityLog;
    private VaccineSystem vaccineSystem;
    private Data data;
    private int updateRate;
    private int simulationSpeed;
    private HashMap<String, HashMap<String, Integer>> availabilities;
    private HashMap<String, HashMap<String, Object>> bookablePeople;

    public void start(ActivityLog activityLog, VaccineSystem vaccineSystem) {

        this.activityLog = activityLog;
        this.vaccineSystem = vaccineSystem;
        this.data = vaccineSystem.getData();
        this.updateRate = vaccineSystem.getUpdateRate();
        this.simulationSpeed = vaccineSystem.getSimulationSpeed();
    }

    public void run() {

        availabilities = Availability.getAvailabilities(data);
        bookablePeople = People.getBookablePeople(data);

        Factory.updateStockLevels(this);
        VaccinationCentre.orderVaccines(this);
        DistributionCentre.orderVaccines(this);
        Booking.simulateBookings(this);
        Delivery.update(this);

        try {
            System.out.println("------------------------------------");
            update(activityLog, vaccineSystem, data);
            data.write();
            data.read();
        }
        catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public ActivityLog getActivityLog() {
        return activityLog;
    }

    public Data getData() {
        return data;
    }

    public int getUpdateRate() {
        return updateRate;
    }

    public int getSimulationSpeed() {
        return simulationSpeed;
    }

    public HashMap<String, HashMap<String, Integer>> getAvailabilities() {
        return availabilities;
    }

    public void setAvailabilities(HashMap<String, HashMap<String, Integer>> availabilities) {
        this.availabilities = availabilities;
    }

    public HashMap<String, HashMap<String, Object>> getBookablePeople() {
        return bookablePeople;
    }
}