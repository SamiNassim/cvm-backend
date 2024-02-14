package com.saminassim.cvm.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.UuidGenerator;

@Entity
@Table(name = "profile")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Profile {
    @Id
    @UuidGenerator
    private String id;
    @JsonIgnore
    @OneToOne
    private User user;
    private Gender gender;
    private String country;
}
