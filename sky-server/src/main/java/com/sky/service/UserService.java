package com.sky.service;

import com.sky.entity.User;

public interface UserService {

    /**
     * 微信登录
     * @param code 微信登录凭证（code）
     * @return 登录成功后的用户信息
     */
    User wxLogin(String code);
}
