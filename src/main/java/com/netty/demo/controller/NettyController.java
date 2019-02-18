package com.netty.demo.controller;

import com.netty.demo.dto.FileInfo;
import com.netty.demo.nettyServer.WebSocketServer;
import lombok.extern.slf4j.Slf4j;
import org.apache.hadoop.hbase.client.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.hadoop.hbase.HbaseTemplate;
import org.springframework.data.hadoop.hbase.RowMapper;
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

    @Autowired
    private HbaseTemplate hbaseTemplate;

    @RequestMapping("hbase")
    public FileInfo hbaseTest(){
        FileInfo fileInfo = hbaseTemplate.get("file_tb", "123", new RowMapper<FileInfo>() {
            @Override
            public FileInfo mapRow(Result result, int i) throws Exception {
                FileInfo t = new FileInfo();
                t.setName(new String(result.getValue("fileinfo".getBytes(), "name".getBytes()), "utf-8"));
                return t;
            }
        });
        log.info(fileInfo.toString());
        return fileInfo;
    }


}
