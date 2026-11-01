package com.sky.service;

public interface ShopService {
    /**
     * 设置店铺营业状态
     * @param status 店铺营业状态 1:营业中 0:打烊中
     */
    void setStatus(Integer status);

    /**
     * 获取店铺营业状态
     * @return 店铺营业状态 1:营业中 0:打烊中
     */
    Integer getStatus();
}
