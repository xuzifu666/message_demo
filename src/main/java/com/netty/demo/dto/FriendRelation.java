package com.netty.demo.dto;

import lombok.Data;

import javax.persistence.Id;
import javax.persistence.Table;

@Data
@Table(name = "my_friends")
public class FriendRelation {
    @Id
    private String id;

    private String myUserId;

    private String myFriendUserId;

}
