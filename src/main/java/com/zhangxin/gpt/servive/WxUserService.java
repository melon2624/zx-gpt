package com.zhangxin.gpt.servive;

import com.zhangxin.gpt.entity.WxUser;
import com.baomidou.mybatisplus.extension.service.IService;
import org.springframework.boot.configurationprocessor.json.JSONException;

/**
 * @author zhangxin
 * @date 2023-06-02 11:19
 */

public interface WxUserService extends IService<WxUser> {


    public void createUserAndIntegral(String fromUserName, String msg);

    public  void unsubscribe(String fromUserName);

    Boolean isSubscribe(String fromUserName);

    String getWxUserMediaId(String fromUserName);

    WxUser getWxUserByOpenId(String fromUserName);
}


