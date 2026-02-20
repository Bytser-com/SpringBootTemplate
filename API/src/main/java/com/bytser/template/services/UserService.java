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
import org.springframework.transaction.annotation.Transactional;

import com.bytser.template.components.DurationFormatter;
import com.bytser.template.dtos.requests.CreateUserRequest;
import com.bytser.template.dtos.requests.UpdateUserRequest;
import com.bytser.template.dtos.responses.SpeciesStatsResponse;
import com.bytser.template.dtos.responses.UserStatsResponse;
import com.bytser.template.exceptions.NotFoundException;
import com.bytser.template.models.Observation;
import com.bytser.template.models.User;
import com.bytser.template.repositories.UserRepository;

@Service
public class UserService {
    
    private static final Logger log = LoggerFactory.getLogger(UserService.class);

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }
    
    @Transactional(readOnly = false)
    public void addUser(CreateUserRequest createUserRequest) {
        if (userRepository.existsByUsername(createUserRequest.getUsername())) {
            throw new NotFoundException("Gebruikersnaam " + createUserRequest.getUsername() + " is al in gebruik");
        }

        User user = new User(
                createUserRequest.getUsername(),
                createUserRequest.getEmail(),
                passwordEncoder.encode(createUserRequest.getPassword())    // Encode/Hash the password 
        );

        userRepository.save(user);

        // !INFO: Log User creation for debugging and auditing
        log.info("User created successfully with id={} and username={}",
            user.getId(), user.getUsername());
    }

    @Transactional(readOnly = false)
    public void updateUser(UUID userId, UpdateUserRequest updateUser) {
        // At least one field must be provided
        if ((updateUser.getUsername() == null || updateUser.getUsername().isBlank()) &&
            (updateUser.getEmail() == null || updateUser.getEmail().isBlank()) &&
            (updateUser.getPassword() == null || updateUser.getPassword().isBlank())) {
            throw new NotFoundException("Ongeldige of ontbrekende input data");
        }

        User user = userRepository.findById(userId)
                .orElseThrow(() ->
                        new NotFoundException("Gebruiker met id " + userId + " niet gevonden"));

        // Update username if present
        if (updateUser.getUsername() != null && !updateUser.getUsername().isBlank()) {
            user.setUsername(updateUser.getUsername());
        }

        // Update email if present
        if (updateUser.getEmail() != null && !updateUser.getEmail().isBlank()) {
            user.setEmail(updateUser.getEmail());
        }

        // Update password if present
        if (updateUser.getPassword() != null && !updateUser.getPassword().isBlank()) {
            user.setPassword(passwordEncoder.encode(updateUser.getPassword()));     // Encode/Hash the password 
        }

        userRepository.save(user);

        // !INFO: Log User updates for debugging and auditing
        log.info("User updated successfully with id={} and (new)username={}",
            user.getId(), user.getUsername());
    }

    @Transactional(readOnly = true)
    public UserStatsResponse getUserStats() {
        User currentUser = getCurrentUser();

        // All observations of this User
        List<Observation> userObservations = currentUser.getObservations();

        int userTotalObservations = userObservations.size();

        // Count Unique species & families of User
        int uniqueSpecies = (int) userObservations.stream()
                .map(Observation::getSpeciesCode)
                .distinct()
                .count();

        int uniqueFamilies = (int) userObservations.stream()
                .map(Observation::getFamilyCode)
                .distinct()
                .count();

        // Calculate average time between observations
        String avgTimeBetweenObservations = calculateAvgTimeBetweenObservations(userObservations);

        // SHow info about the most observed species and family
        SpeciesStatsResponse mostObservedSpecies =
                calculateMostObservedSpecies(userObservations);

        List<SpeciesStatsResponse> mostObservedFamily =
                calculateMostObservedFamily(userObservations);

        // Return a completed response DTO
        return new UserStatsResponse(
                userTotalObservations,
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

    public User getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();

        User currentUser = userRepository.findByUsername(username)
                .orElseThrow(() -> new NotFoundException("Ingelogde gebruiker niet gevonden"));

        return currentUser;
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
