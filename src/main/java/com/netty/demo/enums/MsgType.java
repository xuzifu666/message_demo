package com.netty.demo.enums;

public enum MsgType {

    LOGINMSG(0,"登录消息"),
    CHAT(1,"普通聊天消息"),
    SIGN(2,"签收消息"),
    HEART(3,"心跳消息"),
    PULL_FRIENDS(4,"刷新好友列表消息");

    private Integer type;

    private String desc;

    private MsgType(Integer type,String desc){
        this.type = type;
        this.desc = desc;
    }

}
