package com.saminassim.cvm.entity;

import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.Getter;


@Getter
public enum Gender {
    @Enumerated(EnumType.STRING)
    MALE("Homme"),
    @Enumerated(EnumType.STRING)
    FEMALE("Femme");

    private final String displayName;

    Gender(String displayName) {
        this.displayName = displayName;
    }

    public static Gender fromDisplayName(String displayName) {
        for (Gender gender : Gender.values()) {
            if (gender.displayName.equalsIgnoreCase(displayName)) {
                return gender;
            }
        }
        throw new IllegalArgumentException("Invalid display name: " + displayName);
}}
