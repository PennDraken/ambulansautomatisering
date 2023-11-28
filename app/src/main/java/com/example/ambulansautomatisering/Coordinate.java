package com.example.ambulansautomatisering;

public class Coordinate {
    private final Object a;

    private final Object b;

    public Coordinate(Object latitude, Object longitude) {
        this.a = latitude;
        this.b = longitude;
    }

    public Object getA() {
        return a;
    }

    public Object getB() {
        return b;
    }

    @Override
    public String toString() {
        return "(" + a + ", " + b + ")";
    }
}
