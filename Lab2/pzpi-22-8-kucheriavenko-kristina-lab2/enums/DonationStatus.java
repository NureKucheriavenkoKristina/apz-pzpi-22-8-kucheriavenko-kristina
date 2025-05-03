package com.BiologicalMaterialsSystem.enums;

public enum DonationStatus {
    AVAILABLE("Доступно"),
    COLLECTED("Зібрано"),
    DONATED("Використано"),
    PENDING("Очікується"),
    DISPOSED("Утилізовано");

    private final String name;

    DonationStatus(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
