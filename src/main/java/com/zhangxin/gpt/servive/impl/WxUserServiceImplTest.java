package com.zhangxin.gpt.servive.impl;

import com.zhangxin.gpt.entity.WxUser;
import com.zhangxin.gpt.mapper.WxUserMapper;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;
import java.util.Date;


/**
 * @author zhangxin
 * @date 2023-06-02 11:43
 */
@RunWith(SpringRunner.class)
@SpringBootTest
class WxUserServiceImplTest {

    @Resource
    WxUserMapper userMapper;

    @Test
    public void createUserAndIntegral() {

        String fromUserName = "ojGES6In5vw1g-SILj-QniYxT4rg";

        WxUser user = new WxUser();
        user.setOpenId(fromUserName);
        user.setCreatedTime(new Date());
        user.setIntegral(50);
        user.setIsSubscribe(1);
        user.setInviteNum(0);
        userMapper.insert(user);

    }
}