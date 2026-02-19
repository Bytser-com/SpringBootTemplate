package com.bytser.template.repositories;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.bytser.template.models.Example;

public interface ExampleRepository extends JpaRepository<Example, UUID> {

    boolean existsByEmail(String email);

    boolean existsByUsername(String username);

    Optional<Example> findByEmail(String email);

    Optional<Example> findByUsername(String username);

}