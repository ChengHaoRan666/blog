package com.chr.blog.domain.bo;

/**
 * 参考文件的链接
 *
 * @author 程浩然
 * @since 2025-04-22
 */
public class Reference {
    /**
     * 标题
     */
    private String title;

    /**
     * url
     */
    private String url;

    public Reference() {
    }

    public Reference(String title, String url) {
        this.title = title;
        this.url = url;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}
