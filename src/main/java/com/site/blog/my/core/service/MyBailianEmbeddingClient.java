package com.site.blog.my.core.service;

import com.alibaba.dashscope.utils.JsonUtils;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.ai.document.Document;
import org.springframework.ai.embedding.Embedding;
import org.springframework.ai.embedding.EmbeddingClient;
import org.springframework.ai.embedding.EmbeddingResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class MyBailianEmbeddingClient implements EmbeddingClient {
    /**
     * 百炼 API Key
     */
    @Value("${bailian.api-key}")
    private String apiKey;

    /**
     * 百炼 Base URL
     */
    @Value("${bailian.baseUrl}")
    private String baiLianUrl;

    /**
     * 向量模型
     */
    @Value("${bailian.vector-model}")
    private String model;

    /**
     * 向量维度
     */
    @Value("${bailian.dimensions}")
    private int dimensions;

    /**
     * 将字符串转为向量
     *
     * @param content 文本内容
     * @return 浮点向量列表
     */
    @Override
    public List<Double> embed(String content) {
        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("model", model);
        requestBody.put("input", content);
        requestBody.put("dimensions", dimensions);
        requestBody.put("encoding_format", "float");

        try {
            HttpClient client = HttpClient.newHttpClient();
            String requestBodyString = JsonUtils.toJson(requestBody);

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(baiLianUrl))
                    .header("Content-Type", "application/json")
                    .header("Authorization", "Bearer " + apiKey)
                    .POST(HttpRequest.BodyPublishers.ofString(requestBodyString))
                    .build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            // 验证HTTP状态码
            if (response.statusCode() != 200) {
                System.err.println("API错误响应：" + response.body());
                throw new RuntimeException("API请求失败，状态码：" + response.statusCode());
            }

            ObjectMapper mapper = new ObjectMapper();
            JsonNode root = mapper.readTree(response.body());

            // 结构化校验
            if (!root.has("data")) {
                throw new RuntimeException("响应缺少data字段");
            }

            JsonNode dataArray = root.get("data");
            if (!dataArray.isArray() || dataArray.isEmpty()) {
                throw new RuntimeException("data应为非空数组");
            }

            JsonNode firstItem = dataArray.get(0);
            if (firstItem == null || !firstItem.has("embedding")) {
                throw new RuntimeException("data首元素缺少embedding字段");
            }

            JsonNode embeddingNode = firstItem.get("embedding");
            if (!embeddingNode.isArray()) {
                throw new RuntimeException("embedding应为数组类型");
            }

            return mapper.convertValue(embeddingNode, new TypeReference<List<Double>>() {
            });
        } catch (Exception e) {
            System.err.println("Error: " + e.getMessage());
        }
        return new ArrayList<>();
    }

    @Override
    public List<Double> embed(Document document) {
        return embed(document.getContent());
    }

    @Override
    public List<List<Double>> embed(List<String> texts) {
        return texts.stream().map(this::embed).toList();
    }

    @Override
    public EmbeddingResponse embedForResponse(List<String> texts) {
        List<List<Double>> embed = this.embed(texts);
        List<Embedding> embeddings = new ArrayList<>();
        for (int i = 0; i < embed.size(); i++) {
            Embedding embedding = new Embedding(embed.get(i), i);
            embeddings.add(embedding);
        }
        return new EmbeddingResponse(embeddings, null);
    }
}




