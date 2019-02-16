package com.netty.demo.nettyServer;

import io.netty.channel.Channel;

import java.util.concurrent.ConcurrentHashMap;

public class ChannelUtils {

    public static ConcurrentHashMap<String, Channel> channels = new ConcurrentHashMap<>();

    public static Channel getChannelById(String userId){
       return channels.get(userId);
    }

    public static void pushChannel(String userId,Channel channel){
        channels.put(userId,channel);
    }

}
