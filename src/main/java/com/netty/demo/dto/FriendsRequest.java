package com.netty.demo.dto;

import lombok.Data;

import javax.persistence.Id;
import javax.persistence.Table;
import java.util.Date;

@Data
@Table(name = "friends_request")
public class FriendsRequest {
    @Id
    private String id;

    private String sendUserId;

    private String acceptUserId;

    private Date requestDateTime;

}
