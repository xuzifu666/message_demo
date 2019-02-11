package com.netty.demo.vo;

import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
public class UserVo {

    private String id;
    @NotNull
    private String username;
    @NotNull
    private String password;

    private String faceImage;

    private String faceImageBig;

    private String nickname;

    private String qrcode;

    private String cid;

}
