package org.example.server.proxy;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.example.server.proxy.handler.ProxyHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class ProxyServer {
    private EventLoopGroup bossGroup = new NioEventLoopGroup();
    private EventLoopGroup workerGroup = new NioEventLoopGroup();
    private final int port = 8866;

    @Autowired
    private ProxyHandler handler;

    @PostConstruct
    public void start() {
        new Thread(() -> {
            try {
                ServerBootstrap b = new ServerBootstrap();
                b.group(bossGroup, workerGroup)
                        .channel(NioServerSocketChannel.class)
                        .handler(new LoggingHandler(LogLevel.INFO))
                        .childHandler(new ChannelInitializer<SocketChannel>() {
                            @Override
                            public void initChannel(SocketChannel ch) throws Exception {
                                ChannelPipeline pipeline = ch.pipeline();
                                // 添加HTTP编解码器
                                pipeline.addLast("httpCodec", new HttpServerCodec());
                                // 添加HTTP对象聚合器
                                pipeline.addLast("httpAggregator", new HttpObjectAggregator(65536));
                                // 添加代理处理器
                                pipeline.addLast("proxyHandler", handler);
                            }
                        });

                ChannelFuture f = b.bind(port).sync();
                log.info("SSL Proxy Server started on port {} in mode", port);

                // 等待服务器套接字关闭
                f.channel().closeFuture().sync();
            } catch (Exception e) {
                log.error("Error starting SSL proxy server", e);
            } finally {
                bossGroup.shutdownGracefully();
                workerGroup.shutdownGracefully();
            }
        }
        ).start();
    }

    public void stop() {
        if (bossGroup != null) {
            bossGroup.shutdownGracefully();
        }
        if (workerGroup != null) {
            workerGroup.shutdownGracefully();
        }
        log.info("SSL Proxy Server stopped");
    }
}