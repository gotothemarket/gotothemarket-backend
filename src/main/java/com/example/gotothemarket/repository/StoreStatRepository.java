package com.example.gotothemarket.repository;

import com.example.gotothemarket.entity.StoreStat;
import com.example.gotothemarket.entity.StoreVibeKey;
import com.example.gotothemarket.entity.StoreVibeStat;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface StoreStatRepository extends JpaRepository<StoreStat, Integer> {}

