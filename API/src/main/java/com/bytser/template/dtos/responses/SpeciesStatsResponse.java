package com.bytser.template.dtos.responses;

import java.time.LocalDateTime;

public class SpeciesStatsResponse {

    private final String speciesCode;
    private final String familyCode;
    private final int numberObservations;
    private final LocalDateTime latestObservation;

    public SpeciesStatsResponse(
            String speciesCode,
            String familyCode,
            int numberObservations,
            LocalDateTime latestObservation
    ) {
        this.speciesCode = speciesCode;
        this.familyCode = familyCode;
        this.numberObservations = numberObservations;
        this.latestObservation = latestObservation;
    }

    public String getSpeciesCode() {
        return speciesCode;
    }

    public String getFamilyCode() {
        return familyCode;
    }

    public int getNumberObservations() {
        return numberObservations;
    }

    public LocalDateTime getLatestObservation() {
        return latestObservation;
    }
}
