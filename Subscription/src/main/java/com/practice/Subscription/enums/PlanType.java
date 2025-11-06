package com.practice.Subscription.enums;

public enum PlanType {
    MONTHLY(1, 499.0),
    QUARTERLY(3, 1299.0),
    ANNUAL(12, 4999.0);

    private final int durationInMonths;
    private final double price;

    PlanType(int durationInMonths, double price) {
        this.durationInMonths = durationInMonths;
        this.price = price;
    }

    public int getDurationInMonths() {
        return durationInMonths;
    }

    public double getPrice() {
        return price;
    }
}
