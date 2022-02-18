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
        StockLevel.updateFactoryStockLevels(data, updateRate, simulationSpeed);
        Booking.simulateBookings(data);

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