package com.zhangxin.gpt.entity;

import com.thoughtworks.xstream.annotations.XStreamAlias;

import java.util.Map;

/**
 * @author zhangxin
 * @date 2023-05-31 18:04
 */
@XStreamAlias("xml")
public class TextMsg  extends BaseMsg {


    @XStreamAlias("Content")
    private String content;//回复的文本内容

    public TextMsg(Map<String,String> requestMap, String content) {
        super(requestMap);
        this.setMsgType("text");
        this.content = content;
    }

}
