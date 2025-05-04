package com.chr.blog.task;

import com.chr.blog.domain.entity.Blog;
import com.chr.blog.mapper.BlogMapper;
import com.chr.blog.service.BlogVectorService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * 定时让文章存入向量数据库中
 * 每三天凌晨 1 点执行一次
 *
 * @author 程浩然
 * @since 2025-04-22
 */
@Component
public class BlogTask {
    private static final Logger log = LoggerFactory.getLogger(BlogTask.class);

    @Autowired
    private BlogMapper blogMapper;

    @Autowired
    private BlogVectorService blogVectorService;

    /**
     * 定时任务：每三天凌晨 1 点执行一次，插入博客向量到 Redis 向量数据库
     */
    @Scheduled(cron = "0 0 1 */3 * ?")
    public void blogInsert() {
        log.info("==== 开始执行向量同步任务 ====");
        // 清空向量数据库中的数据
        blogVectorService.clear();

        // 获取全部的博客列表
        List<Blog> blogs = blogMapper.getAll();

        // 将每篇博客转为向量并存入 Redis 向量数据库
        for (Blog blog : blogs) {
            blogVectorService.indexBlog(blog);
        }
        log.info("==== 向量同步完成，共处理： {} 篇 ====", blogs.size());
        System.out.println();
    }
}
