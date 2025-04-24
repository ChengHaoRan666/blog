package com.site.blog.my.core.controller.ai;

import com.site.blog.my.core.domain.vo.AnswerVO;
import com.site.blog.my.core.service.BlogQAService;
import com.site.blog.my.core.service.BlogVectorService;
import com.site.blog.my.core.task.BlogTask;
import com.site.blog.my.core.util.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * AI控制层
 *
 * @author 程浩然
 * @since 2025-04-13
 */
@RestController
@RequestMapping("/ai")
public class AiController {
    @Autowired
    private BlogQAService blogQAService;
    @Autowired
    private BlogTask blogTask;
    @Autowired
    private BlogVectorService blogVectorService;

    /**
     * 输入问题返回结果
     *
     * @param question 问题
     * @return 结果
     */
    @PostMapping("/ask")
    public Result<AnswerVO> askQuestion(@RequestBody String question) {
        Result<AnswerVO> result = new Result<>();
        result.setData(blogQAService.answerQuestion(question));
        return result;
    }

    @GetMapping("/test")
    public void test() {
        blogTask.blogInsert();
    }

    @GetMapping("/clear")
    public void clear() {
        blogVectorService.clear();
    }
}
