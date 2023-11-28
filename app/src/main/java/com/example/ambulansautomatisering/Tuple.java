package com.example.ambulansautomatisering;

public class Tuple {
    private final Object a;

    private final Object b;

    public Tuple(Object latitude, Object longitude) {
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
