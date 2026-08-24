package org.hzero.message.infra.util;

/**
 * 清理文本的html标签
 *
 * @author shuangfei.zhu@hand-china.com 2020/09/24 11:12
 */
public class ClearHtmlUtils {

    private ClearHtmlUtils() {
    }

    /**
     * 去除html代码中含有的标签
     *
     * @param html 含标签的文本
     * @return 不含标签的文本
     */
    public static String clearHtml(String html) {
        //定义script的正则表达式，去除js可以防止注入
        String scriptRegex = "<script[^>]*?>[\\s\\S]*?<\\/script>";
        //定义style的正则表达式，去除style样式，防止css代码过多时只截取到css样式代码
        String styleRegex = "<style[^>]*?>[\\s\\S]*?<\\/style>";
        //定义HTML标签的正则表达式，去除标签，只提取文字内容
        String htmlRegex = "<[^>]+>";

        // 过滤script标签
        html = html.replaceAll(scriptRegex, "");
        // 过滤style标签
        html = html.replaceAll(styleRegex, "");
        // 过滤html标签
        html = html.replaceAll(htmlRegex, "");
        return html;
    }
}
