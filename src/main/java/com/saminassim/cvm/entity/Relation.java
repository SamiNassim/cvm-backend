package com.saminassim.cvm.entity;

import com.fasterxml.jackson.annotation.JsonProperty;

public enum Relation {
    @JsonProperty("Amitié")
    FRIENDLY,
    @JsonProperty("Relation sérieuse")
    SERIOUS,
    @JsonProperty("Mariage")
    MARRIAGE
}
