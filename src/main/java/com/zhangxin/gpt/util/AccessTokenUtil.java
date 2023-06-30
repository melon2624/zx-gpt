package com.zhangxin.gpt.util;

import com.alibaba.fastjson.JSONObject;
import com.zhangxin.gpt.redis.RedisUtil;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.cookie.CookiePolicy;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.params.DefaultHttpParams;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.UnsupportedEncodingException;

/**
 * @author zhangxin
 * @date 2023-06-05 18:15
 */
@Component
public class AccessTokenUtil {

    private static final String ACCESS_TOKEN_KEY = "access_token";

    private static final long EXPIRATION_TIME_SECONDS = 7000; // AccessToken的有效期（单位：秒）


    private static final String WECHAT_API_URL = "https://api.weixin.qq.com/cgi-bin/token";


    private final static String ACCESS_TOKEN_URL_FULL = "https://api.weixin.qq.com/cgi-bin/token?grant_type=client_credential&appid=@appid&secret=@secret";


    @Value("${wechat.mpAppId}")
    String APP_ID;

    @Value("${wechat.mpAppSecret}")
    String APP_SECRET;


    @Autowired
    public RedisUtil redisUtil;

    public String getAccessToken() {
        String accessToken = redisUtil.getStr(ACCESS_TOKEN_KEY);

        // 如果Redis中不存在AccessToken，则从微信获取并存储到Redis中
        if (StringUtils.isEmpty(accessToken)) {
            accessToken = fetchAccessTokenFromWeChat();
            redisUtil.setStrWithTime(ACCESS_TOKEN_KEY, accessToken, EXPIRATION_TIME_SECONDS);
        }

        return accessToken;
    }

    private String fetchAccessTokenFromWeChat() {
        // 调用微信API获取AccessToken的逻辑，此处省略实现
        // 返回从微信获取的AccessToken
/*        String url = WECHAT_API_URL + "?grant_type=client_credential&appid=" + APP_ID + "&secret=" + APP_SECRET;

        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<AccessTokenResponse> responseEntity = restTemplate.exchange(url, HttpMethod.GET, null, AccessTokenResponse.class);

        if (responseEntity.getStatusCode().is2xxSuccessful()) {
            AccessTokenResponse accessTokenResponse = responseEntity.getBody();
            if (accessTokenResponse != null) {
                return accessTokenResponse.getAccessToken();
            }
        }*/

        String result = "";
        try {
            HttpClient client = new HttpClient();//服务
            String tokenURL = ACCESS_TOKEN_URL_FULL.replace("@appid", APP_ID).replace("@secret", APP_SECRET);//ACCESS_TOKEN_URL_FULL 地址
            GetMethod getMethod = new GetMethod(tokenURL);//GET

            DefaultHttpParams.getDefaultParams().setParameter("http.protocol.cookie-policy", CookiePolicy.BROWSER_COMPATIBILITY);//cookie
            client.executeMethod(getMethod);//执行
            result = new String(getMethod.getResponseBodyAsString().getBytes("gbk"));//转码得到数据

            // 将数据转换成json
            JSONObject jasonObject = JSONObject.parseObject(result);
            result = (String) jasonObject.get("access_token");
            // System.out.println(result);
            getMethod.releaseConnection();
            return result;
        } catch (IOException e) {
            e.printStackTrace();
        }

        throw new RuntimeException("Failed to fetch access token from WeChat API");
    }

    private static class AccessTokenResponse {
        private String accessToken;

        public String getAccessToken() {
            return accessToken;
        }

        public void setAccessToken(String accessToken) {
            this.accessToken = accessToken;
        }
    }


}
