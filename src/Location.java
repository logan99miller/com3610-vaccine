import java.util.ArrayList;

public class Location {

    protected String addressLineOne;
    protected String addressLineTwo;
    protected String postcode;
    protected ArrayList<OpeningTime> openingTimes;

    public Location(String addressLineOne, String addressLineTwo, String postcode, ArrayList<OpeningTime> openingTimes) {
        this.addressLineOne = addressLineOne;
        this.addressLineTwo = addressLineTwo;
        this.postcode = postcode;
        this.openingTimes = openingTimes;
    }
}
