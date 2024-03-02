package com.saminassim.cvm.entity;

import com.fasterxml.jackson.annotation.JsonProperty;

public enum Gender {
    @JsonProperty("Homme")
    MALE,
    @JsonProperty("Femme")
    FEMALE
}
