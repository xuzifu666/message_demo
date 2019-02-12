package com.netty.demo.controller;

import com.netty.demo.dto.UploadMsg;
import com.netty.demo.dto.Users;
import com.netty.demo.enums.FriendsState;
import com.netty.demo.services.UserService;
import com.netty.demo.utils.FastDFSClient;
import com.netty.demo.utils.FileUtils;
import com.netty.demo.utils.IMoocJSONResult;
import com.netty.demo.utils.QRCodeUtils;
import com.netty.demo.vo.FriendRefVo;
import com.netty.demo.vo.FriendRequestVo;
import com.netty.demo.vo.UserVo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.List;


@Slf4j
@RequestMapping("user")
@RestController
public class UserController {

    @Autowired
    private QRCodeUtils qrCodeUtils;

    @Autowired
    private UserService userService;

    @Autowired
    private FastDFSClient fastDFSClient;

    @CrossOrigin(origins = "*",maxAge = 3600)
    @RequestMapping(value = "login",method = RequestMethod.GET)
    public IMoocJSONResult login(UserVo userVo){
        log.info("登录接口入参" + userVo.toString());
        if(StringUtils.isEmpty(userVo.getUsername()) || StringUtils.isEmpty(userVo.getPassword())){
            return IMoocJSONResult.errorMsg("参数不正确");
        }
        Users users = new Users();
        BeanUtils.copyProperties(userVo,users);
        Users loginUser = null;
        try {
            loginUser = userService.userLogin(users);
        } catch (Exception e) {
            IMoocJSONResult.errorException("登录异常");
        }
        if(loginUser != null){
            return IMoocJSONResult.ok(loginUser);
        }
        return IMoocJSONResult.errorMsg("登录失败");
    }

    @RequestMapping(value = "register",method = RequestMethod.POST)
    public IMoocJSONResult register(@RequestBody UserVo userVo) {
        Users users = new Users();
        BeanUtils.copyProperties(userVo,users);
        Users registerUser = null;
        try {
            registerUser = userService.userRegister(users);
        } catch (Exception e) {
            return IMoocJSONResult.errorException("注册异常："+e);
        }
        if(registerUser != null){
            return IMoocJSONResult.ok(registerUser);
        }
        return IMoocJSONResult.errorMsg("注册失败");
    }

    @RequestMapping("upload")
    public IMoocJSONResult uploadImg(@RequestBody UploadMsg uploadMsg) throws Exception {
        log.info("用户 " + uploadMsg.getUserId() + " 上传图片");
        MultipartFile multipartFile = FileUtils.base64ToMultipart(uploadMsg.getUserId(),uploadMsg.getFaceData());
        String url = fastDFSClient.uploadBase64(multipartFile);
        String[] split = url.split("\\.");
        String smallImgUrl = split[0] + "_80x80." + split[1];
        Users users = new Users();
        users.setId(uploadMsg.getUserId());
        users.setFaceImageBig(url);
        users.setFaceImage(smallImgUrl);
        userService.updateUserInfo(users);
        log.info("图片信息已上传" + users.toString());
        return IMoocJSONResult.ok(users);
    }

    @RequestMapping("friendRef")
    public IMoocJSONResult getFriendRef(String userId,String friendName){
        log.info("用户:{} 搜索好友:{}",userId,friendName);
        Users friend = userService.findUserByCondition("username", friendName);
        if(null == friend){
            return IMoocJSONResult.errorMsg(FriendsState.ISNOTEXIST.getMsg());
        }
        FriendsState friendRef = userService.getFriendRef(userId, friend.getId());

        if(friendRef == FriendsState.ISNOTEXIST){
            return IMoocJSONResult.errorMsg(FriendsState.ISNOTEXIST.getMsg());
        }
        if(friendRef == FriendsState.ISFRIEND){
            return IMoocJSONResult.errorMsg(FriendsState.ISFRIEND.getMsg());
        }
        if(friendRef == FriendsState.ISSELEF){
            return IMoocJSONResult.errorMsg(FriendsState.ISSELEF.getMsg());
        }
        FriendRefVo friendRefVo = new FriendRefVo();
        friendRefVo.setFriendName(friend.getUsername());
        friendRefVo.setFriendId(friend.getId());
        friendRefVo.setCanSearch(0);
        friendRefVo.setIconUrl(friend.getFaceImage());
        friendRefVo.setFriendNickname(friend.getNickname());
        return IMoocJSONResult.ok(friendRefVo);
    }

    @RequestMapping(value = "sendAddFriendMsg",method = RequestMethod.POST)
    public IMoocJSONResult sendAddFriendRequest(String userId,String friendName){
        if(StringUtils.isEmpty(userId) || StringUtils.isEmpty(friendName)){
            return IMoocJSONResult.errorMsg("参数异常");
        }
        Boolean isSuccess = userService.addFriendRequest(userId, friendName);
        if(isSuccess){
            return IMoocJSONResult.ok("请求好友发送成功");
        }
        return IMoocJSONResult.errorMsg("请求好友发送失败");
    }

    @RequestMapping(value = "getFriendRequests",method = RequestMethod.GET)
    public IMoocJSONResult getAddFriendRequests(String userId){
        List<FriendRequestVo> friendList = userService.getFriendList(userId);
        if(CollectionUtils.isEmpty(friendList)){
            return IMoocJSONResult.ok(new ArrayList<FriendRequestVo>());
        }
        return IMoocJSONResult.ok(friendList);
    }

}
