package com.chr.blog.domain.vo;

import com.chr.blog.domain.bo.Reference;

import java.util.List;

/**
 * 用户询问ai时的返回响应
 *
 * @author 程浩然
 * @since 2025-04-22
 */
public class AnswerVO {
    /**
     * 问题答案
     */
    String answer;

    /**
     * 参考链接列表
     */
    List<Reference> references;

    /**
     * 时间戳
     */
    long timestamp;

    public AnswerVO() {
    }

    public AnswerVO(String answer, List<Reference> references, long timestamp) {
        this.answer = answer;
        this.references = references;
        this.timestamp = timestamp;
    }

    public String getAnswer() {
        return answer;
    }

    public void setAnswer(String answer) {
        this.answer = answer;
    }

    public List<Reference> getReferences() {
        return references;
    }

    public void setReferences(List<Reference> references) {
        this.references = references;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }
}
