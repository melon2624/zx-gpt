package com.zhangxin.gpt.servive;

import com.thoughtworks.xstream.XStream;
import com.zhangxin.gpt.entity.*;
import me.chanjar.weixin.common.exception.WxErrorException;
import me.chanjar.weixin.mp.api.WxMpService;
import me.chanjar.weixin.mp.bean.kefu.WxMpKefuMessage;
import org.apache.commons.lang3.StringUtils;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.io.InputStream;
import java.util.*;

/**
 * @author zhangxin
 * @date 2023-05-31 18:03
 */
@Service
public class WxService {

    @Resource
    WxUserService wxUserService;

    @Autowired
    private WxMpService wxMpService;


    public static Map<String, String> parseRequest(InputStream is) {
        Map<String, String> map = new HashMap<String, String>();
        //1.通过io流得到文档对象
        SAXReader saxReader = new SAXReader();
        Document document = null;
        try {
            document = saxReader.read(is);
        } catch (DocumentException e) {
            e.printStackTrace();
        }
        //2.通过文档对象得到根节点对象
        Element root = document.getRootElement();
        //3.通过根节点对象获取所有子节点对象
        List<Element> elements = root.elements();
        //4.将所有节点放入map
        for (Element element : elements) {
            map.put(element.getName(), element.getStringValue());
        }
        return map;
    }


    /**
     * 事件消息回复
     */
    public String getRespose(Map<String, String> requestMap) {
        BaseMsg msg = null;
        // 根据用户发送消息的类型,做不同的处理
        String msgType = requestMap.get("MsgType");
        switch (msgType) {
            case "text":
                msg = dealTextMsg(requestMap);
                break;
            case "event":
                msg = dealEventMsg(requestMap);
            case "news":
                break;
            default:
                break;
        }
        // 将处理结果转化成xml的字符串返回
        if (null != msg) {
            return beanToXml(msg);
        }
        return "";
    }

    private BaseMsg dealEventMsg(Map<String, String> requestMap) {

        BaseMsg baseMsg = null;
        String event = requestMap.get("Event");
        //订阅者的openId
        String FromUserName = requestMap.get("FromUserName");
        //如果是subscribe ,表示用户是通过扫码关注的形式,给推广人增加积分
        if ("subscribe".equals(event)) {

            Boolean subscribe = wxUserService.isSubscribe(FromUserName);
            if (!subscribe) {
                String eventKey = requestMap.get("EventKey");
                String inviteOpenId = null;
                //如果不为空就是扫码来的,为空就是转发推荐关注,扫码推广有积分
                if (!StringUtils.isEmpty(eventKey)) {
                    inviteOpenId = eventKey.split("_")[1];
                }
                //创建用户和给推荐人添加积分
                wxUserService.createUserAndIntegral(FromUserName, inviteOpenId);
                baseMsg = new TextMsg(requestMap, new Date(System.currentTimeMillis()).toLocaleString() + "你好");
            }
        } else if ("unsubscribe".equals(event)) {
            wxUserService.unsubscribe(FromUserName);
        } else if (event.equals("CLICK")) {
            String eventKey = requestMap.get("EventKey");
            if (eventKey.equals("邀请码")||eventKey.equals("领取专属邀请码")) {
                String mediaId = wxUserService.getWxUserMediaId(FromUserName);
                baseMsg = new ImageMsg(requestMap, mediaId);
                // baseMsg = new TextMsg(requestMap, new Date(System.currentTimeMillis()).toLocaleString() + "你好");
                // 构建回复消息
                WxMpKefuMessage message = WxMpKefuMessage
                        .TEXT()
                        .toUser(FromUserName)
                        .content("下面是【您的专属邀请码】，分享海报邀请朋友一起来玩！\n" +
                                "\n" +
                                "每成功邀请1人+10积分\n" +
                                "✅累计5人额外送50积分\n" +
                                "✅累计10人额外送100积分\n" +
                                "✅累计20人额外送200积分\n" +
                                "✅累计50人额外送500积分\n" +
                                "✅累计100人额外送1000积分")
                        .build();
                try {
                    Boolean result = wxMpService.getKefuService().sendKefuMessage(message);
                } catch (WxErrorException e) {
                    e.printStackTrace();
                }
            } else if (eventKey.equals("帮助文档")) {
                String content = "\u270C\ufe0f" + "用户每日3次免费使用次数\n" +
                        "\u270C\ufe0f" + "免费次数用完自动消耗积分(5积分1次)\n" +
                        "\u26A0\ufe0f" + "体验ChatGPT，对话框直接提问(支持上下文)\n" +
                        "\u26A0\ufe0f" + "/img 图片描述，GPT会帮你生成图片\n" +
                        "\uD83D\uDC49" + "<a href=\"weixin://bizmsgmenu?msgmenucontent=获取邀请码&msgmenuid=1\">获取邀请码</a>\n" +
                        "\uD83D\uDC49" + "<a href=\"weixin://bizmsgmenu?msgmenucontent=个人信息查询&msgmenuid=1\">个人信息查询</a>\n" +
                        "\uD83D\uDC49" + "<a href=\"weixin://bizmsgmenu?msgmenucontent=如何获取积分&msgmenuid=1\">如何获取积分</a>\n" +
                        " ——\n" +
                        " 1️⃣不定期发放兑换码\n" +
                        " 2️⃣查看兑换码使用指南\n" +
                        " 3️⃣获取ChatGPT使用、注册教程";

                baseMsg = new TextMsg(requestMap, content);
            } else if (eventKey.equals("使用次数")) {
                WxUser user = wxUserService.getWxUserByOpenId(FromUserName);

                String content = "今天已使用次数:" + 0 + "\n" +
                        "累计使用次数:" + user.getUseNum() + "\n" +
                        "\uD83D\uDC49" + "<a href=\"weixin://bizmsgmenu?msgmenucontent=帮助文档&msgmenuid=1\">查看帮助文档</a>\n";
                baseMsg = new TextMsg(requestMap, content);
            } else if (eventKey.equals("积分查询")) {
                WxUser user = wxUserService.getWxUserByOpenId(FromUserName);

                String content = "当前积分:" + user.getIntegral() + "\n" +
                        "\uD83D\uDC49" + "<a href=\"weixin://bizmsgmenu?msgmenucontent=帮助文档&msgmenuid=1\">查看帮助文档</a>\n";
                baseMsg = new TextMsg(requestMap, content);
            } else if (eventKey.equals("邀请人数")) {
                WxUser user = wxUserService.getWxUserByOpenId(FromUserName);

                String content = "已邀请用户数量:" + user.getInviteNum() + "\n" +
                        "\uD83D\uDC49" + "<a href=\"weixin://bizmsgmenu?msgmenucontent=帮助文档&msgmenuid=1\">查看帮助文档</a>\n";
                baseMsg = new TextMsg(requestMap, content);
            }
        } else if ("SCAN".equals(event)) {

            Boolean subscribe = wxUserService.isSubscribe(FromUserName);
            if (!subscribe) {
                String eventKey = requestMap.get("EventKey");
                String inviteOpenId = null;
                //如果不为空就是扫码来的,为空就是转发推荐关注,扫码推广有积分
                if (!StringUtils.isEmpty(eventKey)) {
                    inviteOpenId = eventKey.split("_")[1];
                }
                //创建用户和给推荐人添加积分
                wxUserService.createUserAndIntegral(FromUserName, inviteOpenId);

            } else {
                baseMsg = new TextMsg(requestMap, "欢迎光临");
            }
        }
  /*      // 如果是图文回复一个图文消息
        if (msg.equals("图文")) {
            List<Article> articles = new ArrayList<Article>();
            articles.add(new Article("自定义"));
            return new NewsMsg(requestMap, articles);
        }*/
        //否则回复一个文本消息,文本内容为'当前时间+你好'
        //当然这个内容可以自定义,在这里也可以接入自动回复机器人
        //   TextMsg textMsg = new TextMsg(requestMap, new Date(System.currentTimeMillis()).toLocaleString() + "你好");
        return baseMsg;
    }

    /**
     * 将回复的消息类转成xml字符串
     *
     * @param msg
     * @return
     */
    public static String beanToXml(BaseMsg msg) {
        XStream stream = new XStream();
        stream.processAnnotations(TextMsg.class);
        stream.processAnnotations(NewsMsg.class);
        stream.processAnnotations(ImageMsg.class);
        String xml = stream.toXML(msg);
        return xml;
    }

    /**
     * 当用户发送是文本消息的处理逻辑
     *
     * @param requestMap
     * @return
     */
    public BaseMsg dealTextMsg(Map<String, String> requestMap) {

        BaseMsg textMsg = null;
        // 获取用户发送的消息内容
        String msg = requestMap.get("Content");

        String ticket = requestMap.get("Ticket");

        String event = requestMap.get("Event");

        //如果是subscribe ,表示用户是通过扫码关注的形式,给推广人增加积分
        if ("subscribe".equals(event)) {
            //订阅者的openId
            String FromUserName = requestMap.get("FromUserName");
            //创建用户和给推荐人添加积分
            wxUserService.createUserAndIntegral(FromUserName, ticket);
        }
        // 如果是图文回复一个图文消息
        if (msg.equals("获取邀请码") || msg.equals("个人信息查询") || msg.equals("如何获取积分")||msg.equals("获取专属邀请码")) {
           /* List<Article> articles = new ArrayList<Article>();
            articles.add(new Article("自定义"));*/
            //return new NewsMsg(requestMap, articles);
            if (msg.equals("个人信息查询")) {
                WxUser user = wxUserService.getWxUserByOpenId(requestMap.get("FromUserName"));

                String content = "今天已使用次数:" + 0 + "\n" +
                        "累计使用次数:" + user.getUseNum() + "\n" +
                        "当前积分:" + user.getIntegral() + "\n" +
                        "已邀请用户数量" + user.getInviteNum();
                textMsg = new TextMsg(requestMap, content);
            } else if (msg.equals("获取邀请码")||msg.equals("获取专属邀请码")) {
                String mediaId = wxUserService.getWxUserMediaId(requestMap.get("FromUserName"));
                textMsg = new ImageMsg(requestMap, mediaId);
                // baseMsg = new TextMsg(requestMap, new Date(System.currentTimeMillis()).toLocaleString() + "你好");
                // 构建回复消息
                WxMpKefuMessage message = WxMpKefuMessage
                        .TEXT()
                        .toUser(requestMap.get("FromUserName"))
                        .content("下面是【您的专属邀请码】，分享海报邀请朋友一起来玩！\n" +
                                "\n" +
                                "每成功邀请1人+10积分\n" +
                                "✅累计5人额外送50积分\n" +
                                "✅累计10人额外送100积分\n" +
                                "✅累计20人额外送200积分\n" +
                                "✅累计50人额外送500积分\n" +
                                "✅累计100人额外送1000积分")
                        .build();
                try {
                    Boolean result = wxMpService.getKefuService().sendKefuMessage(message);
                } catch (WxErrorException e) {
                    e.printStackTrace();
                }
            } else if (msg.equals("如何获取积分")) {

         /*       String content1 = "\u270C\ufe0f" + "用户每日3次免费使用次数\n" +
                        "\u270C\ufe0f" + "免费次数用完自动消耗积分(5积分1次)\n" +
                        "\u26A0\ufe0f" + "体验ChatGPT，对话框直接提问(支持上下文)\n" +
                        "\u26A0\ufe0f" + "/img 图片描述，GPT会帮你生成图片\n" +
                        "\uD83D\uDC49" + "<a href=\"weixin://bizmsgmenu?msgmenucontent=获取邀请码&msgmenuid=1\">获取邀请码</a>\n" +
                        "\uD83D\uDC49" + "<a href=\"weixin://bizmsgmenu?msgmenucontent=个人信息查询&msgmenuid=1\">个人信息查询</a>\n" +
                        "\uD83D\uDC49" + "<a href=\"weixin://bizmsgmenu?msgmenucontent=如何获取积分&msgmenuid=1\">如何获取积分</a>\n" +
                        " ——\n" +
                        " 1️⃣不定期发放兑换码\n" +
                        " 2️⃣查看兑换码使用指南\n" +
                        " 3️⃣获取ChatGPT使用、注册教程";*/
                String content = "方式一\n" +
                        "\uD83D\uDC49" +"<a href=\"weixin://bizmsgmenu?msgmenucontent=购买积分&msgmenuid=1\">购买积分</a>\n" +
                        "方式二\n" +
                        "\uD83D\uDC49"+"<a href=\"weixin://bizmsgmenu?msgmenucontent=获取专属邀请码&msgmenuid=1\">获取专属邀请码</a>\n"+
                        "每成功邀请1人+10积分\n" +
                        "✅累计5人额外送50积分\n" +
                        "✅累计10人额外送100积分\n" +
                        "✅累计20人额外送200积分\n" +
                        "✅累计50人额外送500积分\n" +
                        "✅累计100人额外送1000积分\n" +
                        "方式三\n" +
                        "请点击右下角获取积分";
                       // "请戳#公众号：程序dunk\n" +
                        //"回复【兑换码】获取本期兑换码";
                textMsg = new TextMsg(requestMap, content);
            }


        }
        //否则回复一个文本消息,文本内容为'当前时间+你好'
        //当然这个内容可以自定义,在这里也可以接入自动回复机器人
        // new TextMsg(requestMap, new Date(System.currentTimeMillis()).toLocaleString() + "你好");
        return textMsg;
    }

    /**
     *
     * @param fromUserName 订阅者openID
     * @param msg 推荐者 openId
     */


}
