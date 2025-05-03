package com.BiologicalMaterialsSystem.enums;

public enum Gender {
    FEMALE("Жінка"),
    MALE("Чоловік");

    private final String gender;

    Gender(String gender) {
        this.gender = gender;
    }

    public String getName() {
        return gender;
    }
}
