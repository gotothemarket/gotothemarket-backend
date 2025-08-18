package com.example.gotothemarket.repository;

import com.example.gotothemarket.entity.VibeType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Collection;
import java.util.List;

public interface VibeTypeRepository extends JpaRepository<VibeType, Integer> {
    List<VibeType> findByVibeTypeIdInOrderByVibeTypeIdAsc(Collection<Integer> ids);
}