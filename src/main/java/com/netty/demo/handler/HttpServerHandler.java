package com.netty.demo.handler;

import com.netty.demo.enums.MsgType;
import com.netty.demo.nettyServer.ChannelUtils;
import com.netty.demo.nettyServer.ContentBody;
import com.netty.demo.utils.JsonUtils;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import io.netty.util.ReferenceCountUtil;
import io.netty.util.ReferenceCounted;
import io.netty.util.concurrent.GlobalEventExecutor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class HttpServerHandler extends SimpleChannelInboundHandler<TextWebSocketFrame> {

    // 用于记录和管理所有客户端的channel
    public static ChannelGroup users =
            new DefaultChannelGroup(GlobalEventExecutor.INSTANCE);

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, TextWebSocketFrame msg) throws Exception {
        String content = msg.text();
//        ctx.channel().writeAndFlush(new TextWebSocketFrame("服务端已接受消息:" + content));
        System.out.println("服务端接收到的消息:" + content);
        ContentBody contentMsg = JsonUtils.jsonToPojo(content, ContentBody.class);
        if(contentMsg.getAction() == MsgType.LOGINMSG){
            //登录消息
            String from = contentMsg.getChatMsg().getFrom();
            ChannelUtils.pushChannel(from,ctx.channel());
        }else if(contentMsg.getAction() == MsgType.CHAT){
            //普通聊天消息
            String to = contentMsg.getChatMsg().getTo();
            if(ChannelUtils.getChannelById(to) != null && users.contains(ChannelUtils.getChannelById(to))){
                ChannelUtils.getChannelById(to).writeAndFlush(new TextWebSocketFrame(msg.text()));
            }
            log.info("channel数：{}",ChannelUtils.channels.size());
            //记录消息到数据库

        }else if(contentMsg.getAction() == MsgType.SIGN){
            //签收消息
        }else if(contentMsg.getAction() == MsgType.HEART){
            //心跳消息
        }else if(contentMsg.getAction() == MsgType.PULL_FRIENDS){
            //拉取好友列表消息
        }
    }


    @Override
    public void handlerAdded(ChannelHandlerContext ctx) throws Exception {
        users.add(ctx.channel());
        log.info("添加新连接");
    }

    @Override
    public void handlerRemoved(ChannelHandlerContext ctx) throws Exception {
        users.remove(ctx.channel());
        log.info("移除新连接");
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        log.info("发生异常",cause);
        users.remove(ctx.channel());
    }
}
