package com.example.gotothemarket.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Embeddable
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class StoreVibeKey implements Serializable {
    @Column(name="store_id") private Integer storeId;
    @Column(name="vibe_id")  private Integer vibeId;
}
