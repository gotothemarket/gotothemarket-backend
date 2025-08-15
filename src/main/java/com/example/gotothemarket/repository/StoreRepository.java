package com.example.gotothemarket.repository;

import com.example.gotothemarket.entity.Store;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface StoreRepository extends JpaRepository<Store, Integer> {

}