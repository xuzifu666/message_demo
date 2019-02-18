package com.netty.demo;

import com.netty.demo.dto.ChatMsg;
import com.netty.demo.dto.FileInfo;
import com.netty.demo.dto.Users;
import com.netty.demo.enums.FriendsState;
import com.netty.demo.mapper.ChatMsgMapper;
import com.netty.demo.mapper.UserCustomMapper;
import com.netty.demo.services.UserService;
import com.netty.demo.utils.FastDFSClient;
import com.netty.demo.utils.FileUtils;
import com.netty.demo.utils.QRCodeUtils;
import com.netty.demo.vo.FriendRequestVo;
import lombok.extern.slf4j.Slf4j;
import org.apache.hadoop.hbase.client.Result;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.hadoop.hbase.HbaseTemplate;
import org.springframework.data.hadoop.hbase.RowMapper;
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

    @Autowired
    private UserCustomMapper userCustomMapper;

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

    @Test
    public void testFriend(){
        Users user = userService.findUserByCondition("username", "姆巴佩");
        log.info(user.toString());
    }

    @Test
    public void testFriendRef(){
        FriendsState state = userService.getFriendRef("190209BSWS7STYNC", "190209BSWS7STYNC");
        log.info(state + "");
    }

    @Test
    public void testAddFriend(){
        Boolean aBoolean = userService.addFriendRequest("123123", "xuzifu");
        log.info("添加的结果是:{}",aBoolean.toString());
    }

    @Test
    public void testFriendReqeusts(){
        List<FriendRequestVo> reqs = userCustomMapper.findFriendRequest("2121");
        log.info("结果是{}",reqs.toString());

    }


    @Autowired
    private HbaseTemplate hbaseTemplate;

    @Test
    public void hbaseTest(){

    }


}

