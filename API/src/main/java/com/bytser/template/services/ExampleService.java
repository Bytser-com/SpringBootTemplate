package com.bytser.template.services;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.bytser.template.components.DurationFormatter;
import com.bytser.template.dtos.requests.CreateExampleRequest;
import com.bytser.template.dtos.requests.UpdateExampleRequest;
import com.bytser.template.dtos.responses.ExampleStatsResponse;
import com.bytser.template.dtos.responses.SpeciesStatsResponse;
import com.bytser.template.exceptions.NotFoundException;
import com.bytser.template.models.Example;
import com.bytser.template.models.Observation;
import com.bytser.template.repositories.ExampleRepository;

@Service
public class ExampleService {
    
    private static final Logger log = LoggerFactory.getLogger(ExampleService.class);

    private final ExampleRepository exampleRepository;
    private final PasswordEncoder passwordEncoder;

    public ExampleService(ExampleRepository exampleRepository, PasswordEncoder passwordEncoder) {
        this.exampleRepository = exampleRepository;
        this.passwordEncoder = passwordEncoder;
    }
    
    public void addExample(CreateExampleRequest createExample) {
        if (exampleRepository.existsByUsername(createExample.getUsername())) {
            throw new NotFoundException("Gebruikersnaam " + createExample.getUsername() + " is al in gebruik");
        }

        Example example = new Example(
                createExample.getUsername(),
                createExample.getEmail(),
                passwordEncoder.encode(createExample.getPassword())    // Encode/Hash the password 
        );

        exampleRepository.save(example);

        // !INFO: Log Example creation for debugging and auditing
        log.info("Example created successfully with id={} and username={}",
            example.getId(), example.getUsername());
    }

    public void updateExample(UUID exampleId, UpdateExampleRequest updateExample) {
        // At least one field must be provided
        if ((updateExample.getUsername() == null || updateExample.getUsername().isBlank()) &&
            (updateExample.getEmail() == null || updateExample.getEmail().isBlank()) &&
            (updateExample.getPassword() == null || updateExample.getPassword().isBlank())) {
            throw new NotFoundException("Ongeldige of ontbrekende input data");
        }

        Example example = exampleRepository.findById(exampleId)
                .orElseThrow(() ->
                        new NotFoundException("Gebruiker met id " + exampleId + " niet gevonden"));

        // Update username if present
        if (updateExample.getUsername() != null && !updateExample.getUsername().isBlank()) {
            example.setUsername(updateExample.getUsername());
        }

        // Update email if present
        if (updateExample.getEmail() != null && !updateExample.getEmail().isBlank()) {
            example.setEmail(updateExample.getEmail());
        }

        // Update password if present
        if (updateExample.getPassword() != null && !updateExample.getPassword().isBlank()) {
            example.setPassword(passwordEncoder.encode(updateExample.getPassword()));     // Encode/Hash the password 
        }

        exampleRepository.save(example);

        // !INFO: Log Example updates for debugging and auditing
        log.info("Example updated successfully with id={} and (new)username={}",
            example.getId(), example.getUsername());
    }

    public ExampleStatsResponse getExampleStats() {
        Example currentExample = getCurrentExample();

        // All observations of this Example
        List<Observation> ExampleObservations = currentExample.getObservations();

        int ExampleTotalObservations = ExampleObservations.size();

        // Count Unique species & families of Example
        int uniqueSpecies = (int) ExampleObservations.stream()
                .map(Observation::getSpeciesCode)
                .distinct()
                .count();

        int uniqueFamilies = (int) ExampleObservations.stream()
                .map(Observation::getFamilyCode)
                .distinct()
                .count();

        // Calculate average time between observations
        String avgTimeBetweenObservations = calculateAvgTimeBetweenObservations(ExampleObservations);

        // SHow info about the most observed species and family
        SpeciesStatsResponse mostObservedSpecies =
                calculateMostObservedSpecies(ExampleObservations);

        List<SpeciesStatsResponse> mostObservedFamily =
                calculateMostObservedFamily(ExampleObservations);

        // Return a completed response DTO
        return new ExampleStatsResponse(
                ExampleTotalObservations,
                uniqueSpecies,
                uniqueFamilies,
                avgTimeBetweenObservations,
                mostObservedSpecies,
                mostObservedFamily
        );
    }

    //
    // Helper functions
    //

    public Example getCurrentExample() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String Examplename = authentication.getName();

        Example currentExample = exampleRepository.findByUsername(Examplename)
                .orElseThrow(() -> new NotFoundException("Ingelogde gebruiker niet gevonden"));

        return currentExample;
    }

    private String calculateAvgTimeBetweenObservations(List<Observation> observations) {
        if (observations.size() <= 1) {
            return DurationFormatter.format(Duration.ZERO);
        }

        // Sort observations by dateTime
        List<Observation> obsChronological = observations.stream()
                .sorted(Comparator.comparing(Observation::getDateTime))
                .toList();

        Duration total = Duration.ZERO;

        for (int i = 1; i < obsChronological.size(); i++) {
            LocalDateTime prev = obsChronological.get(i - 1).getDateTime();
            LocalDateTime curr = obsChronological.get(i).getDateTime();
            total = total.plus(Duration.between(prev, curr));
        }

        Duration average = total.dividedBy(obsChronological.size() - 1);
        return DurationFormatter.format(average);
    }

    private SpeciesStatsResponse calculateMostObservedSpecies(List<Observation> observations) {
        // Helper class to aggregate data per species
        class SpeciesCountData {
            String familyCode;
            int count;
            LocalDateTime latest;
        }

        Map<String, SpeciesCountData> perSpecies = new HashMap<>();

        // Fill in SpeciesCountData map
        for (Observation observation : observations) {
            String species = observation.getSpeciesCode();
            SpeciesCountData specData = perSpecies.computeIfAbsent(species, s -> {
                SpeciesCountData specDataMap = new SpeciesCountData();
                specDataMap.familyCode = observation.getFamilyCode();
                return specDataMap;
            });
            specData.count++;
            if (specData.latest == null || observation.getDateTime().isAfter(specData.latest)) {
                specData.latest = observation.getDateTime();
            }
        }

        // Find species with max count
        Map.Entry<String, SpeciesCountData> bestEntry = perSpecies.entrySet().stream()
                .max(Comparator.comparing(entry -> entry.getValue().count))
                .orElse(null);

        String bestSpecies = bestEntry != null ? bestEntry.getKey() : null;
        SpeciesCountData bestSpeciesData = bestEntry != null ? bestEntry.getValue() : null;

        if (bestSpeciesData == null) {
            return null;
        }

        return new SpeciesStatsResponse(
                bestSpecies,
                bestSpeciesData.familyCode,
                bestSpeciesData.count,
                bestSpeciesData.latest
        );
    }

    private List<SpeciesStatsResponse> calculateMostObservedFamily(List<Observation> observations) {
        // Count observations per family
        Map<String, Integer> familyCounts = new HashMap<>();
        String bestFamily = null;
        int bestFamilyCount = 0;

        for (Observation observation : observations) {
            String family = observation.getFamilyCode();
            int familyCount = familyCounts.getOrDefault(family, 0) + 1;
            familyCounts.put(family, familyCount);

            if (familyCount > bestFamilyCount) {
                bestFamilyCount = familyCount;
                bestFamily = family;
            }
        }

        if (bestFamily == null) {
            return Collections.emptyList();
        }

        // Aggregate per species, but only inside bestFamily
        class SpeciesCountData {
            int count;
            LocalDateTime latest;
        }

        Map<String, SpeciesCountData> speciesInFamily = new HashMap<>();

        for (Observation observation : observations) {
            if (!observation.getFamilyCode().equals(bestFamily)) {
                continue;
            }
            String species = observation.getSpeciesCode();
            SpeciesCountData speciesCountData = speciesInFamily.computeIfAbsent(species, s -> new SpeciesCountData());
            speciesCountData.count++;
            if (speciesCountData.latest == null || observation.getDateTime().isAfter(speciesCountData.latest)) {
                speciesCountData.latest = observation.getDateTime();
            }
        }

        // Map to ObservationStatsResponse DTOs
        List<SpeciesStatsResponse> result = new ArrayList<>();

        for (Map.Entry<String, SpeciesCountData> entry : speciesInFamily.entrySet()) {
            String speciesCode = entry.getKey();
            SpeciesCountData speciesCountData = entry.getValue();

            result.add(new SpeciesStatsResponse(
                    speciesCode,
                    bestFamily,
                    speciesCountData.count,
                    speciesCountData.latest
            ));
        }

        // Sort: most observations first, then speciesCode
        result.sort(Comparator.comparing(SpeciesStatsResponse::getLatestObservation).reversed());

        return result;
    }

}
