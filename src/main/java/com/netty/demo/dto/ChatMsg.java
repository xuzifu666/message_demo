package com.netty.demo.dto;

import lombok.Data;

import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import java.util.Date;

@Data
@Table(name = "chat_msg")
public class ChatMsg {

    @Id
    @NotNull
    private String id;

    @NotNull
    private String sendUserId;

    @NotNull
    private String acceptUserId;

    @NotNull
    private String msg;

    @NotNull
    private String signFlag;

    @NotNull
    private Date createTime;

}
