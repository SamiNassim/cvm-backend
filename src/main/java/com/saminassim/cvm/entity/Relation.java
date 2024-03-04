package com.saminassim.cvm.entity;

import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.Getter;

@Getter
public enum Relation {
    @Enumerated(EnumType.STRING)
    FRIENDLY("Amitié"),
    @Enumerated(EnumType.STRING)
    SERIOUS("Relation sérieuse"),
    @Enumerated(EnumType.STRING)
    MARRIAGE("Mariage");

    private final String displayName;

    Relation(String displayName) {
        this.displayName = displayName;
    }

    public static Relation fromDisplayName(String displayName) {
        for (Relation relation : Relation.values()) {
            if (relation.displayName.equalsIgnoreCase(displayName)) {
                return relation;
            }
        }
        throw new IllegalArgumentException("Invalid display name: " + displayName);
}}
