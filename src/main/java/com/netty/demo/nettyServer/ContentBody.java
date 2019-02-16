package com.netty.demo.nettyServer;

import com.netty.demo.enums.MsgType;
import lombok.Data;

@Data
public class ContentBody {

    private MsgType action;

    private ChatMsg chatMsg;

    private String extras;

}
