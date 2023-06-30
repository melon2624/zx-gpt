package com.zhangxin.gpt.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.zhangxin.gpt.entity.WxUser;
import org.apache.ibatis.annotations.Mapper;


/**
 * @author zhangxin
 * @date 2023-06-02 11:27
 */

@Mapper
public interface WxUserMapper extends BaseMapper<WxUser> {
}