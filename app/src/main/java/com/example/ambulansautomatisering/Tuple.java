package com.example.ambulansautomatisering;

public class Tuple<A, B> {
    private final double latitude;
    private final double longitude;

    public Tuple(double latitude, double longitude) {
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public double getLatitude() {
        return latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    @Override
    public String toString() {
        return "(" + latitude + ", " + longitude + ")";
    }
}
