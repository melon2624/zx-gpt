package com.zhangxin.gpt.controller;

import com.zhangxin.gpt.servive.WxService;
import com.zhangxin.gpt.util.WxUtil;
import lombok.extern.slf4j.Slf4j;
import me.chanjar.weixin.common.api.WxConsts;
import me.chanjar.weixin.common.exception.WxErrorException;
import me.chanjar.weixin.mp.api.WxMpService;
import me.chanjar.weixin.mp.bean.result.WxMpOAuth2AccessToken;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URLEncoder;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.Map;

/**
 * @author zhangxin
 * @date 2023-05-31 11:11
 */
@RestController
@Slf4j
public class WeChatController {

    private Logger logger = LoggerFactory.getLogger(WeChatController.class);
    private static final String TOKEN = "zhangxin";

    @Resource
    WxService wxService;

    /**
     * 微信验证token
     * @param signature
     * @param timestamp
     * @param nonce
     * @param echostr
     * @return
     */
    @GetMapping(value = "/sendMessage",produces = "text/html;charset=utf-8")
    public String checkToken(@RequestParam("signature") String signature, @RequestParam("timestamp") String timestamp,
                             @RequestParam("nonce") String nonce, @RequestParam("echostr") String echostr) {
        logger.info("微信验证token-----------------------");
        //排序
        String[] arr = {TOKEN, timestamp, nonce};
        Arrays.sort(arr);

        StringBuilder content = new StringBuilder();
        for (int i = 0; i < arr.length; i++) {
            content.append(arr[i]);
        }
        //sha1Hex 加密
        MessageDigest md = null;
        String temp = null;
        try {
            md = MessageDigest.getInstance("SHA-1");
            byte[] digest = md.digest(content.toString().getBytes());
            temp = WxUtil.byteToStr(digest);
            logger.info("加密后的token:"+temp);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        if ((temp.toLowerCase()).equals(signature)){
            return echostr;
        }
        return null;
    }

    // post方法用于接收微信服务端消息
    @RequestMapping(value =  "sendMessage" , method = RequestMethod.POST)
    public  void  sendMessage(HttpServletRequest req, HttpServletResponse resp) throws IOException {

        req.setCharacterEncoding("UTF-8");
        resp.setCharacterEncoding("UTF-8");
        //将请求中的xml参数转成map
        Map<String,String> map = WxService.parseRequest(req.getInputStream());
        System.out.println(map);
        //转换为xml格式回复消息
        String textMsg = wxService.getRespose(map);
        logger.info("回复微信服务端消息: "+textMsg);
        resp.getWriter().print(textMsg);
    }

    @Autowired
    private WxMpService wxMpService;

    @GetMapping("/authorize")
    public String authorize(@RequestParam("returnUrl") String returnUrl) {
        //1. 配置
        //2. 调用方法
        String url = "http://czxts4.natappfree.cc/wechat/userInfo";
        String redirectUrl = wxMpService.oauth2buildAuthorizationUrl(url, WxConsts.OAUTH2_SCOPE_USER_INFO, URLEncoder.encode(returnUrl));
       // log.info("[微信网页授权]获取code,result={}", redirectUrl);
        return "redirect:" + redirectUrl;
    }

    @GetMapping("/userInfo")
    public String userInfo(@RequestParam("code") String code, @RequestParam("state") String returnUrl){
        WxMpOAuth2AccessToken wxMpOAuth2AccessToken = new WxMpOAuth2AccessToken();
        try {
            wxMpOAuth2AccessToken = wxMpService.oauth2getAccessToken(code);
        } catch (WxErrorException e) {
           e.printStackTrace();
            // log.error("【微信网页授权】{}", e);
           // throw new SellException(ResultEnum.WECHAT_MP_ERROR.getCode(), e.getError().getErrorMsg());
        }
        String openId=wxMpOAuth2AccessToken.getOpenId();
        System.out.println(openId);
        //  return "redirect:" + returnUrl + "?openid=" + openId;
            return null;
    }

    @RequestMapping("/auth")
    public  void  auth(@RequestParam("code") String code){
        log.info("进入auth方法。。。");

        String url="https://api.weixin.qq.com/sns/oauth2/access_token?appid=wx44138a3cfca464fc&secret=118c2d7d7980b4ec8fded95525449dab&code="+code+"&grant_type=authorization_code";
        RestTemplate restTemplate=new RestTemplate();

        String response=restTemplate.getForObject(url,String.class);
        log.info("response={}",response);

    }







}
