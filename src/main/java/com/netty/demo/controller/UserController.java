package com.netty.demo.controller;

import com.netty.demo.dto.UploadMsg;
import com.netty.demo.dto.Users;
import com.netty.demo.services.UserService;
import com.netty.demo.utils.FastDFSClient;
import com.netty.demo.utils.FileUtils;
import com.netty.demo.utils.IMoocJSONResult;
import com.netty.demo.utils.QRCodeUtils;
import com.netty.demo.vo.UserVo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;


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


}
