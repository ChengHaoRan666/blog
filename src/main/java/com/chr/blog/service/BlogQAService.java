package com.chr.blog.service;

import com.chr.blog.domain.bo.Reference;
import com.chr.blog.domain.vo.AnswerVO;
import org.springframework.ai.document.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 博客问答服务
 *
 * @author 程浩然
 * @since 2025-04-14
 */
@Service
public class BlogQAService {
    @Autowired
    private BlogVectorService vectorService;
    @Autowired
    private DeepSeekService deepSeekService;

    /**
     * 传入问题，进行分析，返回回答
     *
     * @param question 问题
     * @return 回答
     */
    public AnswerVO answerQuestion(String question) {
        // 1. 检索相关文档
        List<Document> relevantPosts = vectorService.similaritySearch(question, 3);

        // 2. 构建上下文（Markdown）
        String context = buildContext(relevantPosts);

        // 3. 将问题和上下文传入，调用DeepSeek生成回答
        String answer = deepSeekService.generateResponse(question, context);

        // 4. 构建响应
        return new AnswerVO(
                answer,
                extractReferences(relevantPosts),
                System.currentTimeMillis()
        );
    }

    /**
     * 将redis中存储的信息转为string返回
     *
     * @param posts 向量集合
     * @return string
     */
    private String buildContext(List<Document> posts) {
        return posts.stream()
                .map(post -> String.format("""
                                ### 相关文章标题：%s
                                链接：%s
                                内容摘要：
                                %s
                                """,
                        post.getMetadata().get("title"),
                        post.getMetadata().get("url"),
                        post.getContent()))
                .collect(Collectors.joining("\n\n"));
    }


    /**
     * 响应体中将问题进行处理返回
     *
     * @param posts
     * @return
     */
    private List<Reference> extractReferences(List<Document> posts) {
        return posts.stream()
                .map(post -> {
                    String content = post.getContent();
                    String title = extractTitleFromContent(content);
                    String url = extractUrlFromContent(content);
                    return new Reference(title, url);
                })
                .collect(Collectors.toList());
    }

    private String extractTitleFromContent(String content) {
        // 假设 title 格式为 "Title: xxx"
        for (String line : content.split("\n")) {
            if (line.startsWith("Title:")) {
                return line.substring("Title:".length()).trim();  // 提取 "Title: " 后面的部分
            }
        }
        return "未命名标题";  // 如果没有找到 title，则返回默认值
    }

    private String extractUrlFromContent(String content) {
        // 假设 URL 格式为 "URL: http://xxx"
        for (String line : content.split("\n")) {
            if (line.startsWith("URL:")) {
                return line.substring("URL:".length()).trim();  // 提取 "URL: " 后面的部分
            }
        }
        return null;  // 如果没有找到 URL，则返回 null
    }

}
