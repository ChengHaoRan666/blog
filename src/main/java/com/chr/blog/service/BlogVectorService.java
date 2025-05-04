package com.chr.blog.service;

import com.chr.blog.domain.entity.Blog;
import org.springframework.ai.document.Document;
import org.springframework.ai.vectorstore.RedisVectorStore;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * 博客向量服务类，封装了将博客文章转为向量并存入 Redis 向量数据库的相关逻辑。
 */
@Service
public class BlogVectorService {
    // Spring AI 提供的 Redis 向量存储组件，用于增删查向量文档
    @Autowired
    private RedisVectorStore vectorStore;

    @Autowired
    private MyBailianEmbeddingClient embeddingClient;

    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    // 博客链接前缀
    @Value("${app.baseUrl}")
    private String baseUrl;

    /**
     * 工具方法：用于将字符串切割
     *
     * @param text      字符串
     * @param maxLength 最大长度
     * @return 切割结果
     */
    public static List<String> splitText(String text, int maxLength) {
        List<String> result = new ArrayList<>();
        if (text == null || text.isBlank()) return result;

        String[] paragraphs = text.split("\n");
        StringBuilder currentChunk = new StringBuilder();

        for (String paragraph : paragraphs) {
            if (currentChunk.length() + paragraph.length() + 1 > maxLength) {
                if (!currentChunk.isEmpty()) {
                    result.add(currentChunk.toString());
                    currentChunk.setLength(0);
                }
            }

            if (paragraph.length() > maxLength) {
                for (int i = 0; i < paragraph.length(); i += maxLength) {
                    result.add(paragraph.substring(i, Math.min(i + maxLength, paragraph.length())));
                }
            } else {
                currentChunk.append(paragraph).append("\n");
            }
        }

        if (!currentChunk.isEmpty()) {
            result.add(currentChunk.toString());
        }

        return result;
    }

    /**
     * 将一篇博客转为向量文档并存入 Redis 向量数据库
     *
     * @param blog 博客实体
     */
    public void indexBlog(Blog blog) {
        // 1. 构造用于生成向量的文本
        List<String> contentList = formatTextForEmbedding(blog);
        for (String content : contentList) {
            // 2. 使用嵌入模型生成向量
//            List<Double> embedding = embeddingClient.embed(content);
            List<Double> embedding = embeddingClient.embed(content);

            // 3. 构造 Document，附带元数据
            Document document = new Document(
                    content,
                    Map.of(
                            "id", blog.getBlogId().toString(),
                            "title", blog.getBlogTitle(),
                            "url", baseUrl + "/blog/" + blog.getBlogId()
                    )
            );
            // 4. 设置向量
            document.setEmbedding(embedding);

            // 5. 存入 Redis 向量数据库
            vectorStore.add(List.of(document));

            // 插入一个 ID
            redisTemplate.opsForSet().add("vector:ids", document.getId());
        }
    }

    /**
     * 根据用户查询语句，从 Redis 中检索最相似的 topK 篇博客文档
     *
     * @param query 查询语句（用户问题）
     * @param topK  返回前 topK 个最相似的
     * @return 向量检索结果（Document 列表）
     */
    public List<Document> similaritySearch(String query, int topK) {
        // 构造 SearchRequest 对象
        SearchRequest request = SearchRequest.query(query).withTopK(topK);

        // 使用 RedisVectorStore 查询
        return vectorStore.similaritySearch(request);
    }

    /**
     * 清空redis向量数据库中数据
     */
    public void clear() {
        // 获取全部 ID
        Set<String> idSet = redisTemplate.opsForSet().members("vector:ids");

        // 删除整个集合
        redisTemplate.delete("vector:ids");

        vectorStore.delete(new ArrayList<>(idSet));
    }

    /**
     * 将博客对象转为统一的字符串格式（多段），用于生成向量，每段长度不超过限制
     *
     * @param blog 博客实体
     * @return 格式化后的多段字符串列表
     */
    private List<String> formatTextForEmbedding(Blog blog) {
        String title = blog.getBlogTitle();
        String content = blog.getBlogContent();
        String url = baseUrl + "/blog/" + blog.getBlogId();

        // 设置最大长度
        int maxLength = 7500;

        List<String> chunks = splitText(content, maxLength);
        List<String> formattedChunks = new ArrayList<>();

        for (String chunk : chunks) {
            String formatted = String.format("Title: %s\nContent: %s\nURL: %s", title, chunk, url);
            formattedChunks.add(formatted);
        }

        return formattedChunks;
    }

}

