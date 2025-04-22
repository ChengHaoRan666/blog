package com.site.blog.my.core.task;

import com.site.blog.my.core.domain.entity.Blog;
import com.site.blog.my.core.mapper.BlogMapper;
import com.site.blog.my.core.service.RedisVectorStore;
import com.site.blog.my.core.util.PageQueryUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * 定时让文章存入向量数据库中
 * 每三天凌晨 1 点执行一次 *
 *
 * @author 程浩然
 * @since 2025-04-22
 */
@Component
public class BlogTask {

    @Autowired
    private BlogMapper blogMapper;

    @Autowired
    private RedisVectorStore redisVectorStore;

    @Scheduled(cron = "0 0 1 */3 * ?")
    public void blogInsert() {
        System.out.println("==== 开始执行向量同步任务 ====");

        // TODO 获取全部的笔记
        List<Blog> blogs = blogMapper.findBlogList(new PageQueryUtil(null));
        redisVectorStore.indexBlogs(blogs);

        System.out.println("==== 向量同步完成，共处理：" + blogs.size() + " 篇 ====");
    }
}
