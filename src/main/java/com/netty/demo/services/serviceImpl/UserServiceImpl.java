package com.netty.demo.services.serviceImpl;

import com.netty.demo.dto.FriendRelation;
import com.netty.demo.dto.FriendsRequest;
import com.netty.demo.dto.Users;
import com.netty.demo.enums.FriendsState;
import com.netty.demo.mapper.FriendMapper;
import com.netty.demo.mapper.FriendsRequestMapper;
import com.netty.demo.mapper.UserCustomMapper;
import com.netty.demo.mapper.UsersMapper;
import com.netty.demo.services.UserService;
import com.netty.demo.utils.FastDFSClient;
import com.netty.demo.utils.FileUtils;
import com.netty.demo.utils.MD5Utils;
import com.netty.demo.utils.QRCodeUtils;
import com.netty.demo.vo.FriendRequestVo;
import org.n3r.idworker.Sid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;
import tk.mybatis.mapper.entity.Example;

import java.util.Date;
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

    @Autowired
    private FriendMapper friendMapper;

    @Autowired
    private UserCustomMapper userCustomMapper;

    @Autowired
    private FriendsRequestMapper friendsRequestMapper;

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
    @Transactional
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

    @Override
    public Users findUserByCondition(String property, Object value) {
        Example example = new Example(Users.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo(property,value);
        List<Users> users = usersMapper.selectByExample(example);
        if(!CollectionUtils.isEmpty(users)){
            return users.get(0);
        }
        return null;
    }

    @Override
    public FriendsState getFriendRef(String userId, String friendId) {
        if(StringUtils.isEmpty(friendId)){
            return FriendsState.ISNOTEXIST;
        }
        if(userId.equals(friendId)){
            return FriendsState.ISSELEF;
        }
        Users users = new Users();
        users.setId(friendId);
        Users users1 = usersMapper.selectByPrimaryKey(users);
        if(users1 == null){
            return FriendsState.ISNOTEXIST;
        }
        Example example = new Example(FriendRelation.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("myUserId",userId).andEqualTo("myFriendUserId",friendId);
        List<FriendRelation> friendRelations = friendMapper.selectByExample(example);
        if(CollectionUtils.isEmpty(friendRelations)){
            return FriendsState.CANSEARCH;
        }
        return FriendsState.ISFRIEND;
    }

    @Transactional
    @Override
    public Boolean addFriendRequest(String userId, String friendName) {
        Users friend = findUserByCondition("username", friendName);
        if(null == friend){
            return false;
        }
        if(friend.getId().equals(userId)){
            return false;
        }
        Example example = new Example(FriendsRequest.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("sendUserId",userId).andEqualTo("acceptUserId",friend.getId());
        List<FriendsRequest> friendsRequests = friendsRequestMapper.selectByExample(example);
        if(!CollectionUtils.isEmpty(friendsRequests)){
            return false;
        }
        FriendsRequest friendsRequest = new FriendsRequest();
        friendsRequest.setId(sid.nextShort());
        friendsRequest.setRequestDateTime(new Date());
        friendsRequest.setSendUserId(userId);
        friendsRequest.setAcceptUserId(friend.getId());
        int count = friendsRequestMapper.insertSelective(friendsRequest);
        return count > 0 ? true : false;
    }

    @Override
    public List<FriendRequestVo> getFriendList(String userId) {
        if(StringUtils.isEmpty(userId)){
            return null;
        }
        List<FriendRequestVo> friendRequests = userCustomMapper.findFriendRequest(userId);
        return friendRequests;
    }
}
