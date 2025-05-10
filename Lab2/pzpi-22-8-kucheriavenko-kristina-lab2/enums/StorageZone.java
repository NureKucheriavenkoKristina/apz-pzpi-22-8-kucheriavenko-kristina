package com.BiologicalMaterialsSystem.enums;

import lombok.Getter;

@Getter
public enum StorageZone {
    GREEN("Standard storage conditions"),
    YELLOW("Minor deviation"),
    RED("Critical conditions");

    private final String name;

    StorageZone(String name) {
        this.name = name;
    }

}
