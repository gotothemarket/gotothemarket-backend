package com.example.gotothemarket.repository;

import com.example.gotothemarket.entity.Vibe;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface VibeRepository extends JpaRepository<Vibe, Integer> {
    Optional<Vibe> findByLabelCode(String labelCode);
    Optional<Vibe> findByVibeName(String vibeName);
    // 패키지/인터페이스 그대로 유지하고, 아래 메서드만 추가
    List<Vibe>
    findByVibeType_VibeTypeIdInOrderByCodeAsc(java.util.Collection<Integer> vibeTypeIds);
}