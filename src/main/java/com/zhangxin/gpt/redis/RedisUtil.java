package com.zhangxin.gpt.redis;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;

import java.util.UUID;
import java.util.concurrent.TimeUnit;

/**
 * @author zhangxin
 * @date 2023-06-05 18:22
 */
@Slf4j
@Component
public class RedisUtil {

    private static final String NX = "NX";
    private static final String EX = "EX";

    private static final String LOCK_OK = "OK";

    private static final Long UNLOCK_OK = 1L;

    private static final String UNLOCK_SCRIPT = "if redis.call('get', KEYS[1]) == ARGV[1] then return redis.call('del', KEYS[1]) else return 0 end";

    private static ThreadLocal<String> LOCK_VALUE = new ThreadLocal<String>() {
        @Override
        protected String initialValue() {
            return UUID.randomUUID().toString();
        }
    };

    @Autowired
    public RedisTemplate<String, Object> redisTemplate;

    public void setRedisTemplate(RedisTemplate<String, Object> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }


    // ===============统一操作============

    /**
     * @param key
     * @param time
     * @return boolean
     * @throws
     * @description 指定缓存失效时间
     * @date 2019/4/15
     */
    public  boolean expire(String key,long time) {
        try {
            if (time > 0) {
                redisTemplate.expire(key, time, TimeUnit.SECONDS);
            }
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }

    }

    public boolean expire(String key, long time, TimeUnit timeUnit) {
        try {
            if (time > 0) {
                redisTemplate.expire(key, time, timeUnit);
            }
            return true;
        } catch (Exception e) {
            log.error("redisUtils expire e:", e);
            return false;
        }

    }

    /**
     * @param key
     * @return boolean
     * @throws
     * @description 判断key是否存在

     */
    public  boolean hasKey( String key) {
        try {
            return redisTemplate.hasKey(key);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    // ==================String================

    /**
     * @param key
     * @return java.lang.String
     * @throws
     * @description 获取值

     */
    public  String getStr( final String key) {
        if (key==null){
            return  "";
        }
        if (ObjectUtils.isEmpty(redisTemplate.opsForValue().get(key))){
            return  "";
        }
        String value=String.valueOf(redisTemplate.opsForValue().get(key));
        return value;
    }

    /**
     * @param key
     * @param value
     * @return boolean
     * @throws
     * @description 设置缓存
     * @author 梁XL
     * @date 2019/4/10
     */
    public  boolean setStr( final String key, final String value) {
        boolean result = false;
        try {
            redisTemplate.opsForValue().set(key, value);
            result = true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    /**
     * @param key
     * @param value
     * @param timeOut
     * @return boolean
     * @throws
     * @description 写入缓存带失效时间
     * @author 梁XL
     * @date 2019/4/10
     */
    public  boolean setStrWithTime( final String key,final String value,final long timeOut) {
        boolean result = false;
        try {
            redisTemplate.opsForValue().set(key, value, timeOut, TimeUnit.SECONDS);
            result = true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }


    /**
     * 更新缓存
     */
    public  boolean getAndSetStr( final String key, final String value) {
        boolean result = false;
        try {
            redisTemplate.opsForValue().getAndSet(key, value);
            result = true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    /**
     * 设置缓存
     * @param key
     * @param value
     * @return
     */
    public  boolean setObject( final String key, final Object value) {
        boolean result = false;
        try {
            redisTemplate.opsForValue().set(key, value);
            result = true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    /**
     * @param key
     * @return java.lang.String
     * @throws
     * @description 获取值

     */
    public  Object getObject( final String key) {
        if (key==null){
            return  null;
        }
        Object value=redisTemplate.opsForValue().get(key);
        return value;
    }

    public Long incr(String key) {
        return redisTemplate.opsForValue().increment(key, 1);
    }


}
