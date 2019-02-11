package com.netty.demo.nettyServer;

import com.netty.demo.handler.HttpServerHandler;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.codec.http.websocketx.WebSocketServerProtocolHandler;
import io.netty.handler.stream.ChunkedWriteHandler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Component;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Component
@Slf4j
public class WebSocketServer implements InitializingBean {

    private ServerBootstrap serverBootstrap;
    private NioEventLoopGroup worker;
    private NioEventLoopGroup boss;
    private ChannelFuture future;

    @Override
    public void afterPropertiesSet() {
        startNettyServer();
    }

    /**
     * 初始化
     */
    public void init(){
        boss = new NioEventLoopGroup();
        worker = new NioEventLoopGroup();
        serverBootstrap = new ServerBootstrap().group(boss, worker)
                .channel(NioServerSocketChannel.class)
                .childHandler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel ch) throws Exception {
                        //HttpServerCodec: 针对http协议进行编解码
                        ch.pipeline().addLast("httpServerCodec", new HttpServerCodec());
                        //ChunkedWriteHandler分块写处理，文件过大会将内存撑爆
                        ch.pipeline().addLast("chunkedWriteHandler", new ChunkedWriteHandler());
                        ch.pipeline().addLast("httpObjectAggregator", new HttpObjectAggregator(8192));
                        //用于处理websocket, /ws为访问websocket时的uri
                        ch.pipeline().addLast("webSocketServerProtocolHandler", new WebSocketServerProtocolHandler("/ws"));
                        ch.pipeline().addLast("http-handler", new HttpServerHandler());
                    }
                });
    }

    /**
     * 开启netty服务器
     * @return
     */
    public Boolean startNettyServer(){
        ExecutorService threadpool = Executors.newSingleThreadExecutor();
        threadpool.submit(new Runnable() {
            @Override
            public void run() {
                try {
                    init();
                    future = serverBootstrap.bind(8081);
                    log.info("netty服务器已经开启");
                    future.channel().closeFuture().sync();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } finally {
                    boss.shutdownGracefully();
                    worker.shutdownGracefully();
                    log.info("netty 服务器已关闭");
                }
            }
        });
        threadpool.shutdown();
        return true;
    }

    /**
     * 关闭netty服务器
     * @return
     */
    public Boolean close(){
        if(future != null) {
            future.channel().close();
            future = null;
            return true;
        }
        return false;
    }



}
