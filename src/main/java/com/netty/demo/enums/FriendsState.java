package com.netty.demo.enums;

public enum FriendsState {

    CANSEARCH(0,"可以查找"),
    ISSELEF(1,"该用户是自己"),
    ISFRIEND(2,"该用户已是好友"),
    ISNOTEXIST(3,"该用户不存在");

    private Integer state;
    private String msg;

    private FriendsState(Integer state,String msg){
        this.state = state;
        this.msg = msg;
    }

    public Integer getState() {
        return state;
    }

    public String getMsg() {
        return msg;
    }
}
