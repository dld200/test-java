package org.example.server.proxy.handler;

import io.netty.buffer.ByteBufAllocator;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.*;
import io.netty.handler.ssl.SslContext;
import io.netty.handler.ssl.SslContextBuilder;
import io.netty.handler.ssl.SslHandler;
import lombok.extern.slf4j.Slf4j;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.example.server.proxy.cert.CertFileUtils;
import org.example.server.proxy.cert.CertificateGenerator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.net.ssl.SSLEngine;
import java.security.PrivateKey;
import java.security.Security;
import java.security.cert.X509Certificate;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Component
@ChannelHandler.Sharable
public class ProxyHandler extends SimpleChannelInboundHandler<FullHttpRequest> {

    private static final Map<String, CertificateGenerator.CertAndKey> CERT_MAP = new HashMap<>();
    @Autowired
    private HttpProxyHandler httpProxyHandler;

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, FullHttpRequest req) {
        if (req.method() == HttpMethod.CONNECT) {
            handleConnect(ctx, req);
        } else {
            forwardHttpRequest(ctx, req);
        }
    }

    private void handleConnect(ChannelHandlerContext ctx, FullHttpRequest req) {
        String remoteHost = req.uri().split(":")[0];
        int remotePort = Integer.parseInt(req.uri().split(":")[1]);

        // 回复客户端：连接建立成功
        DefaultFullHttpResponse response = new DefaultFullHttpResponse(
                HttpVersion.HTTP_1_1, new HttpResponseStatus(200, "Connection Established"));
        ctx.writeAndFlush(response).addListener(future -> {
            if (future.isSuccess()) {
                // 移除HTTP处理器
                ctx.pipeline().remove("httpCodec");
                ctx.pipeline().remove("httpAggregator");

                // 与客户端建立伪SSL连接（MITM）
                SSLEngine clientSsl = createFakeSSLEngineForClient(remoteHost);
                ctx.pipeline().addFirst("clientSSL", new SslHandler(clientSsl));

                // 握手完成后，初始化后端连接
                ctx.pipeline().addLast(new SslProxyHandler(remoteHost, remotePort));
            }
        });
    }

    private void forwardHttpRequest(ChannelHandlerContext ctx, FullHttpRequest req) {
        // 普通 HTTP 代理请求处理
        // 转发到目标主机
//        ctx.pipeline().addLast(httpProxyHandler);
    }

    public SSLEngine createFakeSSLEngineForClient(String host) {
        try {
            X509Certificate caCert = CertFileUtils.loadCert("ca.pem");
            PrivateKey caKey = CertFileUtils.loadPrivateKey("ca-key.pem");
            // 检查缓存：有没有为该host生成过伪证书
            // 如果没有，为host生成证书（用你的CA签名）

            CertificateGenerator.CertAndKey cert = CERT_MAP.get(host);
            if (cert == null) {
                cert = CertificateGenerator.generateCertForHost(host, caCert, caKey);
                CERT_MAP.put(host, cert);
                CertFileUtils.save(cert, host);
            }

            SslContext ctx = SslContextBuilder.forServer(cert.key, cert.cert).build();
            SSLEngine engine = ctx.newEngine(ByteBufAllocator.DEFAULT);
            engine.setUseClientMode(false);
            engine.setEnabledProtocols(new String[]{"TLSv1", "TLSv1.1", "TLSv1.2", "TLSv1.3"});
//            Security.addProvider(new BouncyCastleProvider());
//            log.info("Supported protocols: {}", Arrays.toString(engine.getSupportedProtocols()));
//            log.info("Enabled protocols: {}", Arrays.toString(engine.getEnabledProtocols()));
//            log.info("jdk.tls.disabledAlgorithms: {}", Security.getProperty("jdk.tls.disabledAlgorithms"));
            return engine;
        } catch (Exception e) {
            log.error("generate ssl context error", e);
            throw new RuntimeException(e);
        }
    }
}