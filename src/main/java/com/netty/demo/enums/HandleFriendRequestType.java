package com.netty.demo.enums;

public enum HandleFriendRequestType {

    ACCEPT(1,"接受好友请求"),
    REJIECT(0,"拒绝好友请求");

    private String msg;

    private Integer type;

    private HandleFriendRequestType(Integer type, String msg){
        this.msg = msg;
        this.type = type;
    }

}
