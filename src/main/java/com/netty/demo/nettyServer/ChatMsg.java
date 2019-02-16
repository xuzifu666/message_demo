package com.netty.demo.nettyServer;

import lombok.Data;

@Data
public class ChatMsg {

    private String from;

    private String to;

    private String msg;

    private String msgId;
}
