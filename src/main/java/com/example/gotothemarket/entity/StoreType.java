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

    // 비즈니스 메서드
    public void updateTypeName(String newTypeName) {
        this.typeName = newTypeName;
    }

    // 상점 추가
    public void addStore(Store store) {
        this.stores.add(store);
    }

    // 해당 타입의 상점 수 조회
    public int getStoreCount() {
        return stores.size();
    }
}