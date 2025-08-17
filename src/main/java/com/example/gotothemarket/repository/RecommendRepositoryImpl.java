package com.example.gotothemarket.repository;

import com.example.gotothemarket.dto.TopStoreDto;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.stereotype.Repository;

import java.util.List;

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
}