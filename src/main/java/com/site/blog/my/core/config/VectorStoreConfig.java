package com.site.blog.my.core.config;


import com.site.blog.my.core.service.MyBailianEmbeddingClient;
import org.springframework.ai.vectorstore.RedisVectorStore;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 向量数据库配置
 *
 * @author 程浩然
 * @since 2025-04-24
 */
@Configuration
public class VectorStoreConfig {
    /**
     * 向量数据库url
     */
    @Value("${vectorstore.redis.url}")
    private String url;

    /**
     * 索引名称
     */
    @Value("${vectorstore.redis.prefix}")
    private String prefix;

    /**
     * 键前缀
     */
    @Value("${vectorstore.redis.index}")
    private String indexName;


    @Autowired
    private MyBailianEmbeddingClient myBailianEmbeddingClient;

    @Bean
    public RedisVectorStore redisVectorStore() {
        // 配置向量数据库的信息（url，前缀，索引名称）
        RedisVectorStore.RedisVectorStoreConfig redisVectorStoreConfig = RedisVectorStore.RedisVectorStoreConfig.builder()
                .withURI(url)
                .withPrefix(prefix)
                .withIndexName(indexName)
                .build();
        // 将配置的向量数据库和自己实现的EmbeddingClient接口类配置进去
        return new RedisVectorStore(redisVectorStoreConfig, myBailianEmbeddingClient);
    }
}
