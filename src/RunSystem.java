import java.sql.SQLException;
public class RunSystem {

    private Data data;
    private int updateRate;
    private int simulationSpeed;

    public void start(Data data, int updateRate, int simulationSpeed) {
        this.data = data;
        this.updateRate = updateRate;
        this.simulationSpeed = simulationSpeed;
    }

    public void run() {
        Factory.updateStockLevels(data, updateRate, simulationSpeed);
        VaccinationCentre.orderVaccines(data);
        DistributionCentre.orderVaccines(data);
        Booking.simulateBookings(data);
        Delivery.update(data, updateRate, simulationSpeed);

        try {
            data.update();
            data.write();
            data.read();
        }
        catch (SQLException e) {
            e.printStackTrace();
        }
    }

}