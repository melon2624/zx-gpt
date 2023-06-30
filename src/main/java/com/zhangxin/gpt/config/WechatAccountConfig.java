package com.zhangxin.gpt.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * @author zhangxin
 * @date 2021/8/1 19:48
 */
@Data
@Component
@ConfigurationProperties(prefix = "wechat")
public class WechatAccountConfig {

    /**
     *公众号平台id
     */
    private String mpAppId;

    /**
     * 公众平台密钥
     */
    private String mpAppSecret;

    /**
     * 商户号
     */
    private String mchId;

    /**
     * 商户密钥
     */
    private String mchKey;
/*
    *//**
     * 商户证书路径
     *//*
    private String keyPath="";*/

    /**
     * 微信支付异步通知地址
     */
    private String notifyUrl="";


}
