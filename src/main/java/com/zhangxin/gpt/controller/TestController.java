package com.zhangxin.gpt.controller;


import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.zhangxin.gpt.entity.WxUser;
import com.zhangxin.gpt.mapper.WxUserMapper;
import com.zhangxin.gpt.servive.WxUserService;
import me.chanjar.weixin.mp.api.WxMpService;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


import javax.annotation.Resource;

/**
 * @author zhangxin
 * @date 2023-06-02 11:50
 */
@RestController
public class TestController {

    @Resource
    WxUserMapper userMapper;

    @Resource
    WxMpService wxMpService;


    @Resource
    WxUserService wxUserService;

   // @RequestMapping("/test")
    public void createUserAndIntegral() {

        String fromUserName = "ojGES6In5vw1g-SILj-QniYxT4rg";
//
//        WxUser user = new WxUser();
//        user.setOpenId(fromUserName);
//        user.setCreatedTime(new Date());
//        user.setIntegral(50);
//        user.setIsSubscribe(1);
//        user.setInviteNum(0);
//        userMapper.insert(user);
        LambdaQueryWrapper<WxUser> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(WxUser::getOpenId, fromUserName);
       /* Date startTime = DateUtils.string2Date2(xunNiaoDto.getStartTime());
        Date endTme = DateUtils.string2Date2(xunNiaoDto.getEndTime());*/
//        wrapper.eq(XunNiaoInfo::getCallType,xunNiaoDto.getQueryType());
//        wrapper.between(XunNiaoInfo::getBeginTime, xunNiaoDto.getStartTime(), xunNiaoDto.getEndTime());
//        wrapper.orderByDesc(XunNiaoInfo::getBeginTime);
        WxUser wxUser = userMapper.selectOne(wrapper);
    }

/*

    @RequestMapping("/test")
    public void test() {
        //String accessToken = "YOUR_ACCESS_TOKEN";

        String accessToken = "69_-OlhG2mWjILUPuurPVqA4OtIy-Usj7weeCElukUNZEBHbBLAUMsTeFc0Ffe0FPNGO5eaA3umNTIrLyc4l_NG61FQ03NUD8hr0679xaEcdRSPP7DuTMiQe_GMNZ4DEQfAIAJKW";

        String type = "image";

        String filePath = "C:\\\\Users\\\\zhangxin\\\\Desktop\\\\0327\\\\微信图片_20230428101439.jpg";

        // String accessToken = "YOUR_ACCESS_TOKEN"; // 替换为你的访问令牌

        String url = "https://api.weixin.qq.com/cgi-bin/qrcode/create?access_token=" + accessToken;

        CloseableHttpClient httpClient = null;

        try {
            httpClient = HttpClients.createDefault();
            HttpPost httpPost = new HttpPost(url);

            // 构建请求参数
            JSONObject params = new JSONObject();
            params.put("action_name", "QR_LIMIT_STR_SCENE"); // 永久二维码场景
            JSONObject scene = new JSONObject();
            scene.put("scene_str", "1415458002"); // 替换为你的场景值
            JSONObject actionInfo = new JSONObject();
            actionInfo.put("scene", scene);
            params.put("action_info", actionInfo);

            // 设置请求体
            StringEntity requestEntity = new StringEntity(params.toString(), "UTF-8");
            httpPost.setEntity(requestEntity);
            httpPost.setHeader("Content-type", "application/json");

            // 发送请求并获取响应
            HttpResponse response = httpClient.execute(httpPost);
            HttpEntity entity = response.getEntity();
            if (entity != null) {
                String responseString = EntityUtils.toString(entity);
                JSONObject jsonResponse = new JSONObject(responseString);

                // 解析响应数据，获取永久二维码的ticket
                String ticket = jsonResponse.getString("ticket");
                String qrcodeUrl = jsonResponse.getString("url");

                System.out.println("永久二维码ticket: " + ticket);
                System.out.println("永久二维码URL: " + qrcodeUrl);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (httpClient != null) {
                try {
                    httpClient.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }


    }
*/


    @RequestMapping("/test2")
    public  void  test2(){
      //  wxUserService.createUserAndIntegral(null,null);
    }

}
