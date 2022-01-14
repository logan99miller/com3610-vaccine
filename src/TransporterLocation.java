import java.util.ArrayList;

public class TransporterLocation extends Location {

    private int availableCapacity;
    private int totalCapacity;
    private int transporterID;

    public TransporterLocation(
            String addressLineOne,
            String addressLineTwo,
            String postcode,
            ArrayList<OpeningTime> openingTimes,
            int availableCapacity,
            int totalCapacity,
            int transporterID) {
        super(addressLineOne, addressLineTwo, postcode, openingTimes);
        this.availableCapacity = availableCapacity;
        this.totalCapacity = totalCapacity;
    }
}