package com.chr.blog.util;

import org.commonmark.Extension;
import org.commonmark.ext.gfm.tables.TablesExtension;
import org.commonmark.node.Node;
import org.commonmark.parser.Parser;
import org.commonmark.renderer.html.HtmlRenderer;
import org.springframework.util.StringUtils;

import java.util.List;

public class MarkDownUtil {
    /**
     * 转换md格式为html
     *
     * @param markdownString markdown 格式字符串
     * @return html 格式字符串
     */
    public static String mdToHtml(String markdownString) {
        if (!StringUtils.hasText(markdownString)) {
            return "";
        }
        List<Extension> extensions = List.of(TablesExtension.create());
        Parser parser = Parser.builder().extensions(extensions).build();
        Node document = parser.parse(markdownString);
        HtmlRenderer renderer = HtmlRenderer.builder().extensions(extensions).build();
        return renderer.render(document);
    }
}
