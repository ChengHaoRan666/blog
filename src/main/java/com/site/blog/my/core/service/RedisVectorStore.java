package com.site.blog.my.core.service;

import com.site.blog.my.core.domain.entity.Blog;
import org.springframework.ai.document.Document;
import org.springframework.ai.embedding.EmbeddingClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.Cursor;
import org.springframework.data.redis.core.ScanOptions;
import org.springframework.stereotype.Service;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Redis 向量检索服务
 */
@Service
public class RedisVectorStore {
    private static final String VECTOR_INDEX = "blog_vector_index";
    private static final String DOC_PREFIX = "doc:";
    private static final int EMBEDDING_DIMENSION = 1536;
    private final RedisConnectionFactory connectionFactory;
    private final EmbeddingClient embeddingClient;
    @Value("${app.url}")
    String url;

    @Autowired
    public RedisVectorStore(RedisConnectionFactory connectionFactory, EmbeddingClient embeddingClient) {
        this.connectionFactory = connectionFactory;
        this.embeddingClient = embeddingClient;
        createIndexIfNotExists();
    }

    /**
     * 在 Redis 中创建向量索引（FT.CREATE），如果索引已存在则跳过
     */
    private void createIndexIfNotExists() {
        try (RedisConnection connection = connectionFactory.getConnection()) {
            try {
                connection.execute("FT.INFO", VECTOR_INDEX.getBytes());
            } catch (Exception ignored) {
            }

            connection.execute("FT.CREATE",
                    VECTOR_INDEX.getBytes(),
                    "ON".getBytes(), "HASH".getBytes(),
                    "PREFIX".getBytes(), "1".getBytes(), DOC_PREFIX.getBytes(),
                    "SCHEMA".getBytes(),
                    "title".getBytes(), "TEXT".getBytes(), "WEIGHT".getBytes(), "5.0".getBytes(),
                    "content".getBytes(), "TEXT".getBytes(),
                    "url".getBytes(), "TEXT".getBytes(),
                    "embedding".getBytes(), "VECTOR".getBytes(), "FLAT".getBytes(), "6".getBytes(),
                    "TYPE".getBytes(), "FLOAT32".getBytes(),
                    "DIM".getBytes(), String.valueOf(EMBEDDING_DIMENSION).getBytes(),
                    "DISTANCE_METRIC".getBytes(), "COSINE".getBytes()
            );
        }
    }

    /**
     * 将单篇博客文章转为向量，并存入 Redis
     *
     * @param blog 单篇文章
     */
    public void indexBlog(Blog blog) {
        String textToEmbed = formatTextForEmbedding(blog);
        List<Double> embedding = embeddingClient.embed(textToEmbed);

        try (RedisConnection connection = connectionFactory.getConnection()) {
            Map<byte[], byte[]> documentData = new HashMap<>();
            documentData.put("id".getBytes(), blog.getBlogId().toString().getBytes());
            documentData.put("title".getBytes(), blog.getBlogTitle().getBytes());
            documentData.put("content".getBytes(), blog.getBlogContent().getBytes()); // Markdown
            documentData.put("url".getBytes(), (url + "/blog/" + blog.getBlogId()).getBytes());
            documentData.put("embedding".getBytes(), convertEmbeddingToBytes(embedding));

            String key = DOC_PREFIX + blog.getBlogId();
            connection.hashCommands().hMSet(key.getBytes(), documentData);
        }
    }

    /**
     * 将多篇博客文章转为向量，并存入 Redis
     *
     * @param blogs 多篇文章
     */
    public void indexBlogs(List<Blog> blogs) {
        try (RedisConnection connection = connectionFactory.getConnection()) {
            connection.openPipeline();
            for (Blog blog : blogs) {
                String textToEmbed = formatTextForEmbedding(blog);
                List<Double> embedding = embeddingClient.embed(textToEmbed);

                Map<byte[], byte[]> documentData = new HashMap<>();
                documentData.put("id".getBytes(), blog.getBlogId().toString().getBytes());
                documentData.put("title".getBytes(), blog.getBlogTitle().getBytes());
                documentData.put("content".getBytes(), blog.getBlogContent().getBytes()); // Markdown
                documentData.put("url".getBytes(), (url + "/blog/" + blog.getBlogId()).getBytes());
                documentData.put("embedding".getBytes(), convertEmbeddingToBytes(embedding));

                String key = DOC_PREFIX + blog.getBlogId();
                connection.hashCommands().hMSet(key.getBytes(), documentData);
            }
            connection.closePipeline();
        }
    }

    /**
     * 根据问题查找最相似的前几条向量数据
     *
     * @param query 问题
     * @param topK  选择条数
     * @return 查找的结果
     */
    public List<Document> similaritySearch(String query, int topK) {
        List<Double> queryEmbedding = embeddingClient.embed(query);

        try (RedisConnection connection = connectionFactory.getConnection()) {
            byte[] queryVectorBytes = convertEmbeddingToBytes(queryEmbedding);

            Object[] args = new Object[]{
                    VECTOR_INDEX.getBytes(),
                    ("*=>[KNN " + topK + " @embedding $vector]").getBytes(),
                    "PARAMS".getBytes(), "2".getBytes(),
                    "vector".getBytes(), queryVectorBytes,
                    "DIALECT".getBytes(), "2".getBytes()
            };

//            List<Object> rawResults = connection.execute("FT.SEARCH", args);
            List<Object> rawResults = null;
            return parseSearchResults(rawResults);
        }
    }

    /**
     * 删除指定 id 的 Redis 向量文档（key 为 doc:{id}）
     *
     * @param id key
     */
    public void deleteDocument(String id) {
        try (RedisConnection connection = connectionFactory.getConnection()) {
            connection.keyCommands().del((DOC_PREFIX + id).getBytes());
        }
    }


    /**
     * 获取 Redis 向量库中的文档数量
     *
     * @return 返回文档数量
     */
    public long getDocumentCount() {
        try (RedisConnection connection = connectionFactory.getConnection()) {
            Object[] args = new Object[]{
                    VECTOR_INDEX.getBytes(),
                    "*".getBytes(),
                    "LIMIT".getBytes(),
                    "0".getBytes(),
                    "0".getBytes()
            };

//            List<Object> result = connection.execute("FT.SEARCH", args);
            List<Object> result = null;
//            return (long) result.get(0);
            return 1L;
        }
    }


    /**
     * 把 FT.SEARCH 返回的 Redis 原始结果解析为 Document 列表
     *
     * @param rawResults redis查出的原始内容
     * @return 解析后的数据
     */
    private List<Document> parseSearchResults(List<Object> rawResults) {
        List<Document> documents = new ArrayList<>();
        if (rawResults.size() <= 1) return documents;

        for (int i = 1; i < rawResults.size(); i += 2) {
            String docId = new String((byte[]) rawResults.get(i));
            List<byte[]> fields = (List<byte[]>) rawResults.get(i + 1);

            Map<String, String> fieldMap = new HashMap<>();
            for (int j = 0; j < fields.size(); j += 2) {
                String fieldName = new String(fields.get(j));
                String fieldValue = new String(fields.get(j + 1));
                fieldMap.put(fieldName, fieldValue);
            }

            Document doc = new Document(
                    fieldMap.get("content"), // Markdown
                    Map.of(
                            "id", fieldMap.get("id"),
                            "title", fieldMap.get("title"),
                            "url", fieldMap.get("url")
                    )
            );
            documents.add(doc);
        }

        return documents;
    }

    /**
     * 将一篇文章格式化成统一字符串，便于送入向量模型生成 embedding
     *
     * @param blog 文章
     * @return 字符串
     */
    private String formatTextForEmbedding(Blog blog) {
        return String.format("Title: %s\nContent: %s\nURL: %s",
                blog.getBlogTitle(),
                blog.getBlogContent(),
                url + "/blog/" + blog.getBlogId());
    }


    /**
     * 把 List<Double> 的向量转为 byte[]，存入 Redis 时使用
     *
     * @param embedding
     * @return
     */
    private byte[] convertEmbeddingToBytes(List<Double> embedding) {
        ByteBuffer buffer = ByteBuffer.allocate(embedding.size() * Float.BYTES);
        embedding.forEach(value -> buffer.putFloat(value.floatValue()));
        return buffer.array();
    }

    /**
     * 清除所有 Redis 中以 doc: 开头的文档，同时删除并重建索引
     */
    public void clearAll() {
        try (RedisConnection connection = connectionFactory.getConnection()) {
            ScanOptions options = ScanOptions.scanOptions()
                    .match(DOC_PREFIX + "*")
                    .count(100)
                    .build();

            Cursor<byte[]> cursor = connection.scan(options);
            while (cursor.hasNext()) {
                connection.keyCommands().del(cursor.next());
            }

            try {
                connection.execute("FT.DROPINDEX", VECTOR_INDEX.getBytes());
            } catch (Exception ignored) {
            }

            createIndexIfNotExists();
        }
    }
}

