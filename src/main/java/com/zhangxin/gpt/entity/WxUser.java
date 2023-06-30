package com.zhangxin.gpt.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import java.util.Date;

import lombok.Data;


/**
 * @author zhangxin
 * @date 2023-06-02 11:27
 */

@Data
@TableName(value = "wx_user")
public class WxUser {
    /**
     * ID
     */
    @TableId(value = "id", type = IdType.INPUT)
    private Integer id;

    @TableField(value = "open_id")
    private String openId;

    /**
     * 首次订阅时间
     */
    @TableField(value = "created_time")
    private Date createdTime;

    /**
     * 1 订阅, 0 取消订阅
     */
    @TableField(value = "is_subscribe")
    private Integer isSubscribe;

    /**
     * 更新时间
     */
    @TableField(value = "update_time")
    private Date updateTime;

    /**
     * 积分数
     */
    @TableField(value = "integral")
    private Integer integral;

    /**
     * 已经邀请的人数
     */
    @TableField(value = "invite_num")
    private Integer inviteNum;

    /**
     * 1 是,0不是
     */
    @TableField(value = "is_vip")
    private Integer isVip;

    /**
     * 推广图片id
     */
    @TableField(value = "media_id")
    private String mediaId;

    @TableField(value = "ticket")
    private String ticket;

    /**
     * 累计使用次数
     */
    @TableField(value = "use_num")
    private  Integer  useNum;


}