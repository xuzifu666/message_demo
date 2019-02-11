package com.netty.demo.services.serviceImpl;

import com.netty.demo.dto.Users;
import com.netty.demo.mapper.UsersMapper;
import com.netty.demo.services.UserService;
import com.netty.demo.utils.FastDFSClient;
import com.netty.demo.utils.FileUtils;
import com.netty.demo.utils.MD5Utils;
import com.netty.demo.utils.QRCodeUtils;
import org.n3r.idworker.Sid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import tk.mybatis.mapper.entity.Example;

import java.util.List;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UsersMapper usersMapper;

    @Autowired
    private Sid sid;

    @Autowired
    private QRCodeUtils qrCodeUtils;

    @Autowired
    private FastDFSClient fastDFSClient;

    @Value("${fdfs.tmp.file.url}")
    private String tmpPath;

    @Override
    public Boolean userNameIsExist(String username) {
        Example example = new Example(Users.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("username",username);
        List<Users> usersList = usersMapper.selectByExample(example);
        if(usersList == null || usersList.size() == 0){
            return false;
        }
        return true;
    }

    @Override
    public Users userLogin(Users users) throws Exception {
        Example example = new Example(Users.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("username",users.getUsername());
        criteria.andEqualTo("password", MD5Utils.getMD5Str(users.getPassword()));
        List<Users> users1 = usersMapper.selectByExample(example);
        return users1.get(0);
    }

    @Override
    public Users updateUserInfo(Users user){
        int count = usersMapper.updateByPrimaryKeySelective(user);
        Users users = usersMapper.selectByPrimaryKey(user.getId());
        return count > 0 ? users : null;
    }

    @Override
    @Transactional
    public Users userRegister(Users users)  throws Exception{

        users.setPassword(MD5Utils.getMD5Str(users.getPassword()));
        users.setId(sid.nextShort());
        String username = users.getUsername();
        String path = tmpPath + "\\" + users.getUsername() + ".png";
        qrCodeUtils.createQRCode(path,username);
        MultipartFile multipartFile = FileUtils.fileToMultipart(path);
        String qrUrl = fastDFSClient.uploadBase64(multipartFile);
        users.setQrcode(qrUrl);
        users.setFaceImage("");
        users.setFaceImageBig("");
        users.setNickname("");
        usersMapper.insertSelective(users);
        return users;
    }
}
