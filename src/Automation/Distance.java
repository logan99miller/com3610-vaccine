package Automation;

import java.util.HashMap;

public class Distance {
    public static int getTravelTime(double distance) {
        final float SPEED_KMPH = 55;
        final float SPEED_KMPS = SPEED_KMPH / (60 * 60);
        return (int) Math.round(distance / SPEED_KMPS);
    }

    public static int getTravelTime(HashMap<String, Object> locationA, HashMap<String, Object> locationB) {
        return getTravelTime(getDistance(locationA, locationB));
    }

    // Applies the Haversine formula to calculate the shortest distance over the earth's surface
    // Avoid use of cosine formula as it is unreliable for small distances (https://www.themathdoctors.org/distances-on-earth-2-the-haversine-formula/
    public static double getDistance(double longitudeA, double latitudeA, double longitudeB, double latitudeB) {
        final double EARTH_RADIUS = 6371; // in km
        double longitudeDifferenceRad = degreesToRadians(longitudeB - longitudeA);
        double latitudeDifferenceRad = degreesToRadians(latitudeB - latitudeA);
        double latitudeARad = degreesToRadians(latitudeA);
        double latitudeBRad = degreesToRadians(latitudeB);

        // a represents the square of half the chord length between the points
        double a =
                0.5 - Math.cos(latitudeDifferenceRad) / 2 +
                        Math.cos(latitudeARad) * Math.cos(latitudeBRad) *
                                (1 - Math.cos(longitudeDifferenceRad)) / 2;

        double angularDistance = 2 * Math.asin(Math.sqrt(a));

        return EARTH_RADIUS * angularDistance;
    }

    // Applies the Haversine formula to calculate the shortest distance over the earth's surface
    // Avoid use of cosine formula as it is unreliable for small distances (https://www.themathdoctors.org/distances-on-earth-2-the-haversine-formula/)
    public static double getDistance(HashMap<String, Object> locationA, HashMap<String, Object> locationB) {
        double longitudeA = Double.parseDouble((String) locationA.get("Location.longitude"));
        double latitudeA = Double.parseDouble((String) locationA.get("Location.latitude"));
        double longitudeB = Double.parseDouble((String) locationB.get("Location.longitude"));
        double latitudeB = Double.parseDouble((String) locationB.get("Location.latitude"));
        return getDistance(longitudeA, latitudeA, longitudeB, latitudeB);
    }

    private static double degreesToRadians(double degrees) {
        return degrees * (Math.PI / 180);
    }
}
