package com.netty.demo;

import com.netty.demo.dto.ChatMsg;
import com.netty.demo.dto.Users;
import com.netty.demo.mapper.ChatMsgMapper;
import com.netty.demo.services.UserService;
import com.netty.demo.utils.FastDFSClient;
import com.netty.demo.utils.FileUtils;
import com.netty.demo.utils.QRCodeUtils;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RunWith(SpringRunner.class)
@SpringBootTest
@Slf4j
public class DemoApplicationTests {

    @Autowired
    private ChatMsgMapper chatMsgMapper;

    @Autowired
    private UserService userService;

    @Autowired
    private QRCodeUtils qrCodeUtils;

    @Autowired
    private FastDFSClient fastDFSClient;

    @Test
    public void contextLoads() {
        List<ChatMsg> chatMsgs = chatMsgMapper.selectAll();
        log.info("消息数量为：" + chatMsgs.size());
    }


    @Test
    public void register() throws Exception {
        Users users = new Users();
        users.setQrcode("asdwdasdw");
        users.setPassword("12dswqddw");
        users.setId("123123");
        users.setFaceImageBig("http://123");
        users.setFaceImage("http://small");
        users.setCid("qe12ese");
        users.setUsername("xuzifu");
        users.setNickname("xiaoxu");
        userService.userRegister(users);
        log.info("注册成功");
    }

    @Test
    public void qrCodeTest() throws IOException {
        byte[] res = qrCodeUtils.createQrCode("xuzifu");
        MultipartFile multipartFile = FileUtils.bytesToMultiPart("123", res);
        String s = fastDFSClient.uploadNormalFile(multipartFile);
        log.info(s);
    }

}

