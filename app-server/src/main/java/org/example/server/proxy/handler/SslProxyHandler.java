package org.example.server.proxy.handler;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.handler.ssl.SslHandler;
import io.netty.handler.ssl.SslHandshakeCompletionEvent;
import io.netty.util.ReferenceCountUtil;
import lombok.extern.slf4j.Slf4j;

import javax.net.ssl.SSLEngine;
import java.net.InetSocketAddress;
import java.util.Arrays;

@Slf4j
public class SslProxyHandler extends ChannelInboundHandlerAdapter {
    private final String realHost;
    private final int realPort;

    public SslProxyHandler(String realHost, int realPort) {
        this.realHost = realHost;
        this.realPort = realPort;
    }

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        // 当客户端 SSL 握手完成
        if (evt instanceof SslHandshakeCompletionEvent handshakeEvent) {
            if (!handshakeEvent.isSuccess()) {
                ctx.close();
                return;
            }

            log.info("[+] SSL 握手完成，建立目标连接到 " + realHost + ":" + realPort);

            // 开始连接目标服务器
            Bootstrap bootstrap = new Bootstrap();
            bootstrap.group(ctx.channel().eventLoop())
                    .channel(ctx.channel().getClass())
                    .handler(new ChannelInitializer<Channel>() {
                        @Override
                        protected void initChannel(Channel ch) {
                            try {
                                SSLEngine engine = createSSLEngineForTarget(ch);
                                engine.setUseClientMode(true);
                                engine.setEnabledProtocols(new String[] {"TLSv1", "TLSv1.1", "TLSv1.2", "TLSv1.3"});
                                ch.pipeline().addFirst("ssl", new SslHandler(engine));
                            } catch (Exception e) {
                                log.error("init channel error", e);
                                ch.close();
                            }
                        }
                    });

            Channel inboundChannel = ctx.channel();

            bootstrap.connect(new InetSocketAddress(realHost, realPort)).addListener((ChannelFuture future) -> {
                if (future.isSuccess()) {
                    Channel outboundChannel = future.channel();
                    log.info("[+] 成功连接目标服务器 SSL");

                    // Relay 数据
                    setupRelay(inboundChannel, outboundChannel);
                } else {
                    log.warn("[-] 无法连接目标服务器: " + future.cause());
                    inboundChannel.close();
                }
            });
        } else {
            super.userEventTriggered(ctx, evt);
        }
    }

    /**
     * 创建一个用于连接目标服务器的 SSLEngine
     */
    private SSLEngine createSSLEngineForTarget(Channel channel) throws Exception {
        javax.net.ssl.SSLContext sslCtx = javax.net.ssl.SSLContext.getInstance("TLS");
        sslCtx.init(null, null, null);
        SSLEngine engine = sslCtx.createSSLEngine(realHost, realPort);
        engine.setEnabledProtocols(new String[] {"TLSv1", "TLSv1.1", "TLSv1.2", "TLSv1.3"});
        log.info("Supported protocols: {}", Arrays.toString(engine.getSupportedProtocols()));
        log.info("Enabled protocols: {}", Arrays.toString(engine.getEnabledProtocols()));
        return engine;
    }

    /**
     * 设置双向 relay，数据互通
     */
    private void setupRelay(Channel clientChannel, Channel targetChannel) {
        clientChannel.pipeline().addLast(new RelayHandler(targetChannel));
        targetChannel.pipeline().addLast(new RelayHandler(clientChannel));
    }

    /**
     * 简单的 relay handler，把一端的数据写入另一端
     */
    static class RelayHandler extends ChannelInboundHandlerAdapter {
        private final Channel peer;

        RelayHandler(Channel peer) {
            this.peer = peer;
        }

        @Override
        public void channelRead(ChannelHandlerContext ctx, Object msg) {
            if (peer.isActive()) {
                peer.writeAndFlush(msg);
            } else {
                ReferenceCountUtil.release(msg);
            }
        }

        @Override
        public void channelInactive(ChannelHandlerContext ctx) {
            if (peer.isActive()) {
                peer.close();
            }
        }

        @Override
        public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
            log.error("Exception in proxy handler", cause);
            ctx.close();
        }
    }
}