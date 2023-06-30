package com.zhangxin.gpt.entity;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import lombok.Data;

/**
 * @author zhangxin
 * @date 2023-05-31 18:04
 */
@Data
public class Article {

    @XStreamAlias("Title")
    private String title;//图文消息标题
    @XStreamAlias("Description")
    private String description;//图文消息描述
    @XStreamAlias("PicUrl")
    private String picUrl;//图片链接
    @XStreamAlias("Url")
    private String url;//点击图文消息跳转链接

    public Article() {
    }

    public Article(String title ){
        this.title=title;
    }


    public Article(String title, String description, String picUrl, String url) {
        this.title = title;
        this.description = description;
        this.picUrl = picUrl;
        this.url = url;
    }





}
