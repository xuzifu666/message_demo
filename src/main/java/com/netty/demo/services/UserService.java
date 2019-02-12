package com.netty.demo.services;

import com.netty.demo.dto.Users;
import com.netty.demo.enums.FriendsState;

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

    /**
     * 根据条件查询users信息
     * @param property
     * @param value
     * @return
     */
    public Users findUserByCondition(String property,Object value);

    /**
     * 返回用户自己和其他用户的好友关系
     * @param userId
     * @param friendId
     * @return
     */
    public FriendsState getFriendRef(String userId,String friendId);



}
