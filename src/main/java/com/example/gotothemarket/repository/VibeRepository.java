package com.example.gotothemarket.repository;

import com.example.gotothemarket.entity.Vibe;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface VibeRepository extends JpaRepository<Vibe, Integer> {
    Optional<Vibe> findByLabelCode(String labelCode);
    Optional<Vibe> findByVibeName(String vibeName);
}