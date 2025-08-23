package com.example.gotothemarket.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "store_type")
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StoreType {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "store_type")
    private Integer storeTypeId;

    @Column(name = "type_name", length = 50, nullable = false)
    private String typeName;

    // Store와의 1:N 관계 (한 타입에 여러 상점)
    @OneToMany(mappedBy = "storeType", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @Builder.Default
    private List<Store> stores = new ArrayList<>();

    public Integer getStoreType() {
        return this.storeTypeId;
    }
}