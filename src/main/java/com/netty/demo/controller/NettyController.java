package com.netty.demo.controller;

import com.netty.demo.nettyServer.WebSocketServer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("config")
public class NettyController {

    @Autowired
    private WebSocketServer webSocketServer;

    @RequestMapping("start")
    public String startNetty(){
        webSocketServer.startNettyServer();
        return "start success";
    }

    @RequestMapping("close")
    public String closeNettyServer(){
        webSocketServer.close();
        return "stop success";
    }


}
