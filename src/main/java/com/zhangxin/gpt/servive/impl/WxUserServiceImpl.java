package com.zhangxin.gpt.servive.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.google.gson.JsonObject;
import com.zhangxin.gpt.entity.WxUser;
import com.zhangxin.gpt.mapper.WxUserMapper;
import com.zhangxin.gpt.servive.WxUserService;
import com.zhangxin.gpt.util.AccessTokenUtil;
import com.zhangxin.gpt.util.WxUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import com.alibaba.fastjson.JSONObject;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.Date;

/**
 * @author zhangxin
 * @date 2023-06-02 11:19
 */

@Service
public class WxUserServiceImpl extends ServiceImpl<WxUserMapper, WxUser> implements WxUserService {

    @Value("${picture.imagePathWin}")
    String imagePathWin;

    @Value("${picture.imagePathLinux}")
    String imagePathLinux;

    String imagePath;

    @Resource
    WxUserMapper userMapper;

    @Resource
    AccessTokenUtil accessTokenUtil;

    /**
     * @param fromUserName  订阅者openId
     * @param inventeOpenId 推广者inventeOpenId
     */
    @Transactional
    public void createUserAndIntegral(String fromUserName, String inventeOpenId) {
        LambdaQueryWrapper<WxUser> selectWrapper = new LambdaQueryWrapper<>();
        selectWrapper.eq(WxUser::getOpenId, fromUserName);

        WxUser wxUser1 = userMapper.selectOne(selectWrapper);

        if (wxUser1 == null) {
            //判断操作系统
            if (System.getProperty("os.name").toLowerCase().startsWith("win")) {
                imagePath = imagePathWin;
            } else {
                imagePath = imagePathLinux;
            }
            WxUser user = new WxUser();
            user.setOpenId(fromUserName);
            user.setCreatedTime(new Date());
            user.setIntegral(50);
            user.setIsSubscribe(1);
            user.setInviteNum(0);
            user.setUseNum(0);
            String accessToken = accessTokenUtil.getAccessToken();
            //生成分享二维码
            JSONObject jsonResponse = WxUtil.materialUploader(accessToken, "image", imagePath, fromUserName);
            // 提取需要的信息
            String mediaId = jsonResponse.getString("media_id");
            user.setMediaId(mediaId);
            userMapper.insert(user);

            if (!StringUtils.isEmpty(inventeOpenId)) {
                LambdaQueryWrapper<WxUser> wrapper = new LambdaQueryWrapper<>();
                wrapper.eq(WxUser::getOpenId, inventeOpenId);
                WxUser wxUser = userMapper.selectOne(wrapper);
                wxUser.setUpdateTime(new Date());
                wxUser.setInviteNum(wxUser.getInviteNum() + 1);
                wxUser.setIntegral(wxUser.getIntegral() + 10);
                if (wxUser.getInviteNum() == 5) {
                    wxUser.setIntegral(wxUser.getIntegral() + 50);
                } else if (wxUser.getInviteNum() == 10) {
                    wxUser.setIntegral(wxUser.getIntegral() + 100);
                } else if (wxUser.getInviteNum() == 20) {
                    wxUser.setIntegral(wxUser.getIntegral() + 200);
                } else if (wxUser.getInviteNum() == 50) {
                    wxUser.setIntegral(wxUser.getIntegral() + 500);
                } else if (wxUser.getInviteNum() == 50) {
                    wxUser.setIntegral(wxUser.getIntegral() + 1000);
                }
                userMapper.updateById(wxUser);
            }
        } else {
            wxUser1.setUpdateTime(new Date());
            wxUser1.setIsSubscribe(1);
            userMapper.updateById(wxUser1);
        }
    }

    @Override
    public void unsubscribe(String fromUserName) {

        LambdaQueryWrapper<WxUser> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(WxUser::getOpenId, fromUserName);
        WxUser wxUser = userMapper.selectOne(wrapper);
        if (wxUser == null) {
            return;
        }
        wxUser.setUpdateTime(new Date());
        wxUser.setIsSubscribe(0);
        userMapper.updateById(wxUser);
    }

    @Override
    public Boolean isSubscribe(String fromUserName) {
        LambdaQueryWrapper<WxUser> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(WxUser::getOpenId, fromUserName);
        WxUser wxUser = userMapper.selectOne(wrapper);
        if (wxUser != null) {
            wxUser.setUpdateTime(new Date());
            wxUser.setIsSubscribe(1);
            userMapper.updateById(wxUser);
            return true;
        }
        return false;
    }

    @Override
    public String getWxUserMediaId(String fromUserName) {
        LambdaQueryWrapper<WxUser> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(WxUser::getOpenId, fromUserName);
        WxUser wxUser = userMapper.selectOne(wrapper);
        return wxUser.getMediaId();
    }

    @Override
    public WxUser getWxUserByOpenId(String fromUserName) {
        LambdaQueryWrapper<WxUser> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(WxUser::getOpenId, fromUserName);
        WxUser wxUser = userMapper.selectOne(wrapper);
        return wxUser;
    }

}


