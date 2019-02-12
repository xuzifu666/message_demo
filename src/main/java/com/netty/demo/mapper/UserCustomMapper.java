package com.netty.demo.mapper;

import com.netty.demo.vo.FriendRequestVo;

import java.util.List;

public interface UserCustomMapper {

    List<FriendRequestVo> findFriendRequest(String userId);

}
