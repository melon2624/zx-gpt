package com.zhangxin.gpt.entity;

import com.thoughtworks.xstream.annotations.XStreamAlias;

import java.util.List;
import java.util.Map;

/**
 * @author zhangxin
 * @date 2023-05-31 18:48
 */
@XStreamAlias("xml")
public class NewsMsg  extends BaseMsg {

    @XStreamAlias("ArticleCount")
    private String articleCount;//图文消息个数



    @XStreamAlias("Articles")
    private List<Article> articles;
    public NewsMsg(Map<String, String> requestMap, List<Article> articles) {
        super(requestMap);
        this.setMsgType("news");
        this.articles = articles;
        this.setArticleCount(this.articles.size()+"");
    }


    public String getArticleCount() {
        return articleCount;
    }

    public void setArticleCount(String articleCount) {
        this.articleCount = articleCount;
    }

    public List<Article> getArticles() {
        return articles;
    }

    public void setArticles(List<Article> articles) {
        this.articles = articles;
    }



}
