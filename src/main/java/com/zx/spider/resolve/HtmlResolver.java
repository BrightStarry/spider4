package com.zx.spider.resolve;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

/**
 * html解析器
 * 使用jsoup
 */
public class HtmlResolver {

    /**
     *抽取html中单一的某个元素
     */
    public static Element getElement(String html,String selector){
        Document doc = Jsoup.parse(html);
        Element element = doc.select(selector).first();
        return element;
    }


}
