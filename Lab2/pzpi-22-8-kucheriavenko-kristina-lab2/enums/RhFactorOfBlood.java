package com.BiologicalMaterialsSystem.enums;

public enum RhFactorOfBlood {

    A_POS( "A Позитивний"),
    A_NEG("A Негативний"),
    B_POS( "B Позитивний"),
    B_NEG( "B Негативний"),
    AB_POS( "AB Позитивний"),
    AB_NEG( "AB Негативний"),
    O_POS("O Позитивний"),
    O_NEG("O Негативний");

    private final String name;

    RhFactorOfBlood(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
