package com.zhangxin.gpt.entity;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import lombok.Data;

import java.util.Map;

/**
 * @author zhangxin
 * @date 2023-05-31 18:45
 */
@Data
public class BaseMsg {

    /*
   @XStreamAlias使用了xstream的依赖，用来生成xml格式
    */
    @XStreamAlias("ToUserName")
    private String toUserName;//接收方的账号(收到的openid)
    @XStreamAlias("FromUserName")
    private String fromUserName;//开发者的微信号
    @XStreamAlias("CreateTime")
    private String createTime;//消息创建时间
    @XStreamAlias("MsgType")
    private String msgType;//消息类型

    //可直接设置所有消息的发送、接收账号以及创建时间（这些属性所有类型消息都一样，不一样的属性会单独设置）
    public BaseMsg(Map<String,String> requestMap) {
        this.toUserName = requestMap.get("FromUserName");
        this.fromUserName = requestMap.get("ToUserName");
        this.createTime = requestMap.get("CreateTime");
    }


}
