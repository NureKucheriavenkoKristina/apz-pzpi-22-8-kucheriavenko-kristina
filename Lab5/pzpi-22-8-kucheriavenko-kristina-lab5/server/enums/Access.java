package com.BiologicalMaterialsSystem.enums;

import lombok.Getter;

@Getter
public enum Access {
    FULL("Повний доступ"),
    READ_ALL("Лише для читання"),
    READ_ONLY("Лише для читання з обмеженнями");

    private final String name;

    Access(String name) {
        this.name = name;
    }

}
