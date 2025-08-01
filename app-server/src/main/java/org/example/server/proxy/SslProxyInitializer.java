package org.example.server.proxy;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;

public class SslProxyInitializer extends ChannelInitializer<SocketChannel> {
    private final ProxyMode mode;

    public SslProxyInitializer(ProxyMode mode) {
        this.mode = mode;
    }

    @Override
    protected void initChannel(SocketChannel ch) throws Exception {
        ChannelPipeline pipeline = ch.pipeline();

        // 添加HTTP编解码器
        pipeline.addLast(new HttpServerCodec());
        // 添加HTTP对象聚合器
        pipeline.addLast(new HttpObjectAggregator(65536));
        // 添加代理处理器
        pipeline.addLast(new SslProxyHandler(mode));
    }
}