package com.netty.demo.handler;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;


public class HttpServerHandler extends SimpleChannelInboundHandler<TextWebSocketFrame> {

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, TextWebSocketFrame msg) throws Exception {
        String content = msg.text();
        ctx.channel().writeAndFlush(new TextWebSocketFrame("服务端已接受消息:" + content));
        System.out.println("服务端接收到的消息:" + content);
    }
}
