package com.example.gotothemarket.converter;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.net.URI;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.net.http.HttpClient;

// CohereClassifier.java (일부만 발췌)
@Component
public class CohereClassifier {

    private static final String API_URL = "https://api.cohere.ai/v1/classify";

    private final HttpClient http;

    @Value("${cohere.api-key:}")
    private String apiKey;

    @Value("${cohere.model-id:}")
    private String modelId;

    public CohereClassifier(@Value("${cohere.api-key:}") String apiKey,
                            @Value("${cohere.model-id:}") String modelId) {
        this.apiKey = (apiKey == null || apiKey.isBlank()) ? System.getenv("COHERE_API_KEY") : apiKey;
        this.modelId = (modelId == null || modelId.isBlank()) ? System.getenv("COHERE_CLASSIFY_MODEL_ID") : modelId;

        if (this.apiKey == null || this.apiKey.isBlank()) {
            throw new IllegalStateException("Missing Cohere API key. Set spring property 'cohere.api-key' or env COHERE_API_KEY.");
        }
        if (this.modelId == null || this.modelId.isBlank()) {
            throw new IllegalStateException("Missing Cohere model id. Set 'cohere.model-id' or env COHERE_CLASSIFY_MODEL_ID (fine-tuned Classify model id).");
        }

        this.http = HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(30))
                .build();
    }

    // ... 기존 필드/생성자 그대로

    public static class LabelScore {
        public final String label;
        public final double confidence;
        public LabelScore(String label, double confidence) {
            this.label = label; this.confidence = confidence;
        }
        @Override public String toString() { return label + " (" + String.format("%.3f", confidence) + ")"; }
    }

    /** 단문 1개 예측 (멀티라벨 임계값 방식) */
    public List<LabelScore> predict(String text, double threshold) throws Exception {
        return predictBatch(List.of(text), threshold).get(0);
    }

    /** 배치 예측: inputs 최대 96개/배치 권장 */
    public List<List<LabelScore>> predictBatch(List<String> texts, double threshold) throws Exception {
        ObjectMapper mapper = new ObjectMapper();

        Map<String, Object> payload = new HashMap<>();
        payload.put("model", modelId);        // ★ 파인튜닝 모델 ID
        payload.put("inputs", texts);
        payload.put("examples", null);        // ★ 파인튜닝은 예시 불필요

        String body = mapper.writeValueAsString(payload);

        HttpRequest req = HttpRequest.newBuilder()
                .uri(URI.create(API_URL))
                .timeout(Duration.ofSeconds(60))
                .header("Authorization", "Bearer " + apiKey)
                .header("Content-Type", "application/json")
                .header("User-Agent", "Market-Classifier/1.0 (+Spring)")
                .POST(HttpRequest.BodyPublishers.ofString(body))
                .build();

        HttpResponse<String> res = http.send(req, HttpResponse.BodyHandlers.ofString());
        if (res.statusCode() / 100 != 2) {
            throw new RuntimeException("Cohere API error: " + res.statusCode() + " - " + res.body());
        }

        JsonNode root = mapper.readTree(res.body());
        ArrayNode arr = (ArrayNode) root.path("classifications");

        List<List<LabelScore>> allResults = new ArrayList<>();
        for (JsonNode cls : arr) {
            // 파인튜닝 classify 응답 예: prediction(최상위), labels{label:{confidence}}
            JsonNode labelsNode = cls.path("labels");
            List<LabelScore> scores = new ArrayList<>();
            labelsNode.fields().forEachRemaining(e -> {
                String lab = e.getKey();
                double conf = e.getValue().path("confidence").asDouble();
                scores.add(new LabelScore(lab, conf));
            });
            // 정렬
            scores.sort((a,b)->Double.compare(b.confidence, a.confidence));

            // 임계값 이상 멀티라벨 선택, 없으면 Top1 보장
            List<LabelScore> picked = scores.stream()
                    .filter(s -> s.confidence >= threshold)
                    .toList();
            if (picked.isEmpty() && !scores.isEmpty()) {
                picked = List.of(scores.get(0));
            }
            allResults.add(picked);
        }
        return allResults;
    }
}