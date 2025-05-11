package com.chr.blog.controller.ai;

import com.chr.blog.domain.vo.AnswerVO;
import com.chr.blog.service.BlogQAService;
import com.chr.blog.service.BlogVectorService;
import com.chr.blog.task.BlogTask;
import com.chr.blog.util.Result;
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
    @PostMapping(value = "/ask")
    public Result<AnswerVO> askQuestion(@RequestBody String question) {
        Result<AnswerVO> result = new Result<>();
        result.setData(blogQAService.answerQuestion(question));
        return result;
    }

    /**
     * 立即执行同步到redis
     */
    @GetMapping("/insert")
    public void test() {
        blogTask.blogInsert();
    }

    @GetMapping("/clear")
    public void clear() {
        blogVectorService.clear();
    }
}
