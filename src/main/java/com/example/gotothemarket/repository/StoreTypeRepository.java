package com.example.gotothemarket.repository;

import com.example.gotothemarket.entity.StoreType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface StoreTypeRepository extends JpaRepository<StoreType, Integer> {
    Optional<StoreType> findByTypeName(String typeName);
}