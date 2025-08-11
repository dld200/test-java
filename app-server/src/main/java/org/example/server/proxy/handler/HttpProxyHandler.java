package org.example.server.proxy.handler;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.util.CharsetUtil;
import org.springframework.stereotype.Component;

@Component
@ChannelHandler.Sharable
public class HttpProxyHandler extends SimpleChannelInboundHandler<FullHttpRequest> {
    private Channel outboundChannel;

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, FullHttpRequest request) {
        logRequest(request);

        String remoteHost = request.uri().split(":")[0];
        int remotePort = Integer.parseInt(request.uri().split(":")[1]);

        // 发起与目标服务器的连接
        Bootstrap b = new Bootstrap();
        b.connect(remoteHost, remotePort).addListener((ChannelFuture future) -> {
            if (future.isSuccess()) {
                outboundChannel = future.channel();

                // 将请求写到远程服务器
                outboundChannel.writeAndFlush(request).addListener((ChannelFuture f) -> {
                    // 注册响应处理器
                    outboundChannel.pipeline().addLast(new ChannelInboundHandlerAdapter() {
                        @Override
                        public void channelRead(ChannelHandlerContext serverCtx, Object msg) {
                            logResponse(msg);
                            // 转发给客户端
                            ctx.channel().writeAndFlush(msg);
                        }
                    });
                });

            } else {
                ctx.close();
            }
        });
    }

    private void logRequest(FullHttpRequest req) {
        System.out.println("=== 请求 ===");
        System.out.println(req.method() + " " + req.uri());
        System.out.println(req.headers());
        System.out.println(req.content().toString(CharsetUtil.UTF_8));
    }


    private void logResponse(Object msg) {
        if (msg instanceof FullHttpResponse) {
            FullHttpResponse res = (FullHttpResponse) msg;
            System.out.println("=== 响应 ===");
            System.out.println(res.status());
            System.out.println(res.headers());
            System.out.println(res.content().toString(CharsetUtil.UTF_8));
        }
    }
}