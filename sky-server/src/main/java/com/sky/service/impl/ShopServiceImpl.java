package com.sky.service.impl;

import com.sky.service.ShopService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

@Service
public class ShopServiceImpl implements ShopService {

    @Autowired
    private RedisTemplate redisTemplate;

    static final String SHOP_STATUS_KEY = "shop:status";

    @Override
    public void setStatus(Integer status) {
        // 店铺营业状态 1:营业中 0:打烊中
        redisTemplate.opsForValue().set(SHOP_STATUS_KEY, status);
    }

    @Override
    public Integer getStatus() {
        // 店铺营业状态 1:营业中 0:打烊中
        Integer status = (Integer) redisTemplate.opsForValue().get(SHOP_STATUS_KEY);
        return status == null ? 0 : status;
    }
}