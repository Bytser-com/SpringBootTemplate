package com.bytser.template.dtos.responses;

import java.util.List;

public class ExampleStatsResponse {
    private final int totalObservations;
    private final int uniqueSpecies;
    private final int uniqueFamilies;
    private final String averageTimeBetweenObservations;

    private final SpeciesStatsResponse mostObservedSpecies;
    private final List<SpeciesStatsResponse> mostObservedFamily;

    public ExampleStatsResponse(
            int totalObservations,
            int uniqueSpecies,
            int uniqueFamilies,
            String averageTimeBetweenObservations,
            SpeciesStatsResponse mostObservedSpecies,
            List<SpeciesStatsResponse> mostObservedFamily
    ) {
        this.totalObservations = totalObservations;
        this.uniqueSpecies = uniqueSpecies;
        this.uniqueFamilies = uniqueFamilies;
        this.averageTimeBetweenObservations = averageTimeBetweenObservations;
        this.mostObservedSpecies = mostObservedSpecies;
        this.mostObservedFamily = mostObservedFamily;
    }

    public int getTotalObservations() {
        return totalObservations;
    }

    public int getUniqueSpecies() {
        return uniqueSpecies;
    }

    public int getUniqueFamilies() {
        return uniqueFamilies;
    }

    public String getAverageTimeBetweenObservations() {
        return averageTimeBetweenObservations;
    }

    public SpeciesStatsResponse getMostObservedSpecies() {
        return mostObservedSpecies;
    }

    public List<SpeciesStatsResponse> getMostObservedFamily() {
        return mostObservedFamily;
    }
    
}