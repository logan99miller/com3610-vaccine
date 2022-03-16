package Core;

import java.sql.SQLException;
import Automation.*;
import Data.Data;

import static Data.Update.*;

public class AutomateSystem {

    private ActivityLog activityLog;
    private VaccineSystem vaccineSystem;
    private Data data;
    private int updateRate;
    private int simulationSpeed;

    public void start(ActivityLog activityLog, VaccineSystem vaccineSystem) {
        this.activityLog = activityLog;
        this.vaccineSystem = vaccineSystem;
        this.data = vaccineSystem.getData();
        this.updateRate = vaccineSystem.getUpdateRate();
        this.simulationSpeed = vaccineSystem.getSimulationSpeed();
    }

    public void run() {
        Factory.updateStockLevels(data, updateRate, simulationSpeed);
        VaccinationCentre.orderVaccines(activityLog, data);
        DistributionCentre.orderVaccines(activityLog, data);
        Booking.simulateBookings(activityLog, data);
        Delivery.update(activityLog, data, updateRate, simulationSpeed);

        try {
            System.out.println("------------------------------------");
            update(vaccineSystem, data);
            data.write();
            data.read();
        }
        catch (SQLException e) {
            e.printStackTrace();
        }
    }

}