package Core;

import java.sql.SQLException;
import Automation.*;

public class AutomateSystem {

    private ActivityLog activityLog;
    private Data data;
    private int updateRate;
    private int simulationSpeed;

    public void start(ActivityLog activityLog, Data data, int updateRate, int simulationSpeed) {
        this.activityLog = activityLog;
        this.data = data;
        this.updateRate = updateRate;
        this.simulationSpeed = simulationSpeed;
    }

    public void run() {
        Factory.updateStockLevels(data, updateRate, simulationSpeed);
        VaccinationCentre.orderVaccines(activityLog, data);
        DistributionCentre.orderVaccines(activityLog, data);
        Booking.simulateBookings(activityLog, data);
        Delivery.update(activityLog, data, updateRate, simulationSpeed);

        try {
            System.out.println("------------------------------------");
            data.update();
            data.write();
            data.read();
        }
        catch (SQLException e) {
            e.printStackTrace();
        }
    }

}