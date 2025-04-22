package com.site.blog.my.core.service;

import org.springframework.ai.client.AiClient;
import org.springframework.ai.prompt.PromptTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

/**
 * 大模型服务类
 *
 * @author 程浩然
 * @since 2025-04-14
 */
@Service
public class DeepSeekService {
    @Autowired
    private AiClient aiClient;

    public String generateResponse(String question, String context) {
        // 可以添加DeepSeek特定的参数
        PromptTemplate promptTemplate = new PromptTemplate("""
                你是一个博客网站专业助手，基于提供的上下文回答问题。
                以下是相关的博客内容（已按 Markdown 格式提供）：
                {context}
                                
                用户问题：{question}
                                
                要求：
                1. 使用中文回答
                2. 保持专业且友好的语气
                3. 引用上下文中的具体内容
                4. 如果无法从给定内容中找到答案，请如实告知
                """);
        Map<String, Object> model = new HashMap<>();
        model.put("context", context);
        model.put("question", question);
        String problem = promptTemplate.render(model);
        System.out.println("提问问题：\n" + problem);
        return aiClient.generate(problem);
    }
}