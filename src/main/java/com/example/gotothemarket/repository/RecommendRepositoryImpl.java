package com.example.gotothemarket.repository;

import com.example.gotothemarket.dto.TopStoreDto;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.stereotype.Repository;

import java.util.Collections;
import java.util.List;
import java.util.Objects;

@Repository
public class RecommendRepositoryImpl implements RecommendRepository {

    @PersistenceContext
    private EntityManager em;

    @Override
    public TopStoreDto pickTopStoreByLabels(int marketId, int storeType, List<String> labelCodes) {
        if (labelCodes == null || labelCodes.isEmpty()) return null;

        String labelsCsv = String.join(",", labelCodes);

        String sql = """
            WITH kw AS (
              SELECT unnest(string_to_array(:labelsCsv, ',')) AS label_code
            )
            SELECT s.store_id, s.store_name, s.store_type, COALESCE(SUM(svs.ratio), 0) AS score
            FROM store s
            JOIN store_vibe_stat svs ON svs.store_id = s.store_id
            JOIN vibe v              ON v.vibe_id    = svs.vibe_id
            JOIN kw                  ON kw.label_code = v.label_code
            WHERE s.market_id = :marketId
              AND s.store_type = :storeType
            GROUP BY s.store_id, s.store_name, s.store_type
            ORDER BY score DESC, s.store_id ASC
            LIMIT 1
            """;

        @SuppressWarnings("unchecked")
        List<Object[]> rows = em.createNativeQuery(sql)
                .setParameter("labelsCsv", labelsCsv)
                .setParameter("marketId", marketId)
                .setParameter("storeType", storeType)
                .getResultList();

        if (rows.isEmpty()) return null;
        Object[] r = rows.get(0);
        return new TopStoreDto(
                ((Number) r[0]).intValue(),
                (String) r[1],
                ((Number) r[2]).intValue(),
                ((Number) r[3]).doubleValue()
        );
    }
    @Override
    public List<String> findPositiveLabelCodes(Integer storeId, List<String> labelCodes) {
        if (storeId == null || labelCodes == null || labelCodes.isEmpty()) return List.of();

        List<String> labels = labelCodes.stream()
                .filter(Objects::nonNull)
                .map(String::trim)
                .filter(s -> !s.isEmpty())
                .distinct()
                .toList();
        if (labels.isEmpty()) return List.of();

        String sql = """
        SELECT v.label_code
        FROM store_vibe_stat svs
        JOIN vibe v ON v.vibe_id = svs.vibe_id
        WHERE svs.store_id = ?1
          AND v.label_code = ANY (?2)
          AND (svs.hit_count > 0 OR svs.ratio > 0)
        """;
        var q = em.createNativeQuery(sql);
        q.setParameter(1, storeId);
        q.setParameter(2, labels.toArray(new String[0])); // Postgres ANY(array)

        @SuppressWarnings("unchecked")
        List<Object> rows = q.getResultList();
        return rows.stream().map(o -> o != null ? o.toString() : null)
                .filter(Objects::nonNull)
                .toList();
    }

    @Override
    public List<String> findMatchingKeywords(int storeId, List<String> keywords) {
        if (keywords == null || keywords.isEmpty()) {
            return List.of();
        }

        String placeholders = String.join(",", Collections.nCopies(keywords.size(), "?"));
        String sql = String.format("""
       SELECT v.label_code 
       FROM store_vibe_stat svs 
       JOIN vibe v ON svs.vibe_id = v.vibe_id 
       WHERE svs.store_id = ? 
       AND v.label_code IN (%s)
       """, placeholders);

        var query = em.createNativeQuery(sql);
        query.setParameter(1, storeId);

        for (int i = 0; i < keywords.size(); i++) {
            query.setParameter(i + 2, keywords.get(i));
        }

        return query.getResultList();
    }
}