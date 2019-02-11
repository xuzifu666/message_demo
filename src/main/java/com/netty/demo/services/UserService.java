package com.netty.demo.services;

import com.netty.demo.dto.Users;

public interface UserService {

    /**
     * 用户是否存在
     * @param username
     * @return
     */
    public Boolean userNameIsExist(String username);

    /**
     * 用户登录
     * @param users
     * @return
     */
    public Users userLogin(Users users) throws Exception;

    /**
     * 用户注册
     * @param users
     * @return
     */
    public Users userRegister(Users users) throws Exception;

    /**
     * 更新用户信息
     * @param users
     * @return
     */
    public Users updateUserInfo(Users users);



}
