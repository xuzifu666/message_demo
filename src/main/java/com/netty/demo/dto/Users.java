package com.netty.demo.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Id;
import javax.persistence.Table;

@Data
@Table(name = "users")
@AllArgsConstructor
@NoArgsConstructor
public class Users {
    @Id
    private String id;

    private String username;

    private String password;

    private String faceImage;

    private String faceImageBig;

    private String qrcode;

    private String cid;

    private String nickname;

}
