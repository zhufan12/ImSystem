package com.mogen.im.service.seq;

import lombok.extern.java.Log;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

@Service
public class RedisSeq {

    @Autowired
    private StringRedisTemplate  stringRedisTemplate;
    public long doGetSeq(String key){
        return stringRedisTemplate.opsForValue().increment(key);
    }


}
