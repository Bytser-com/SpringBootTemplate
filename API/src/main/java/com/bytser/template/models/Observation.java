package com.bytser.template.models;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;

@Entity
public class Observation {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long observationID;

    @Column(nullable = false, unique=false, updatable = true)
    private String speciesCode;

    @Column(nullable = false, unique=false, updatable = true)
    private String familyCode;

    @Column(nullable = false, unique=false, updatable = true)
    private String location;

    @Column(nullable = false, unique=false, updatable = true)
    private LocalDateTime dateTime;

    @Column(nullable = false, unique=false, updatable = true)
    private String notes;

    @ManyToOne(optional=false, fetch = FetchType.LAZY)
    @JoinColumn(name = "example_id")
    private Example owner;

    protected Observation() {
        // JPA requirement
    }

    public Observation(String speciesCode, String familyCode, String location, LocalDateTime dateTime, String notes, Example owner) {
        this.speciesCode = speciesCode;
        this.familyCode = familyCode;
        this.location = location;
        this.dateTime = dateTime;
        this.notes = notes;
        this.owner = owner;
    }

    public Long getObservationID() {
        return observationID;
    }

    public String getSpeciesCode() {
        return speciesCode;
    }
    public void setSpeciesCode(String speciesCode) {
        this.speciesCode = speciesCode;
    }

    public String getFamilyCode() {
        return familyCode;
    }
    public void setFamilyCode(String familyCode) {
        this.familyCode = familyCode;
    }

    public String getLocation() {
        return location;
    }
    public void setLocation(String location) {
        this.location = location;
    }

    public LocalDateTime getDateTime() {
        return dateTime;
    }
    public void setDateTime(LocalDateTime dateTime) {
        this.dateTime = dateTime;
    }

    public String getNotes() {
        return notes;
    }
    public void setNotes(String notes) {
        this.notes = notes;
    }

    public Example getOwner() {
        return owner;
    }
    public void setOwner(Example owner) {
        this.owner = owner;
    }

}
