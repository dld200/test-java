package org.example.server.proxy;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.http.*;
import io.netty.handler.ssl.SslContext;
import io.netty.handler.ssl.SslContextBuilder;
import io.netty.handler.ssl.util.InsecureTrustManagerFactory;
import io.netty.util.CharsetUtil;
import lombok.extern.slf4j.Slf4j;
import org.example.common.domain.Transaction;
import org.example.server.service.TransactionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Component
public class SslProxyHandler extends SimpleChannelInboundHandler<FullHttpRequest> {

    @Autowired
    private TransactionService transactionService;

    private final ProxyMode mode;

    public SslProxyHandler(ProxyMode mode) {
        this.mode = mode;
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, FullHttpRequest request) throws Exception {
        String url = request.uri();
        String method = request.method().name();
        String requestBody = request.content().toString(io.netty.util.CharsetUtil.UTF_8);

        // 获取请求头
        Map<String, String> headers = new HashMap<>();
        for (Map.Entry<String, String> header : request.headers()) {
            headers.put(header.getKey(), header.getValue());
        }

        log.info("Received request: {} {}", method, url);

        if (mode == ProxyMode.RECORD) {
            // 录制模式 - 转发请求并记录响应
            recordRequest(ctx, request, url, method, headers, requestBody);
        } else if (mode == ProxyMode.REPLAY) {
            // 回放模式 - 从记录中查找匹配的响应
            replayRequest(ctx, url, requestBody);
        }
    }

    private void recordRequest(ChannelHandlerContext ctx, FullHttpRequest request,
                               String url, String method, Map<String, String> headers, String requestBody) {
        // 解析目标URL
        URI uri;
        try {
            uri = new URI(url);
        } catch (URISyntaxException e) {
            log.error("Invalid URL: {}", url, e);
            sendErrorResponse(ctx, HttpResponseStatus.BAD_REQUEST);
            return;
        }

        // 构建目标请求
        FullHttpRequest proxyRequest = buildProxyRequest(request, uri);
        if (proxyRequest == null) {
            sendErrorResponse(ctx, HttpResponseStatus.BAD_REQUEST);
            return;
        }

        String scheme = uri.getScheme() == null ? "http" : uri.getScheme();
        String host = uri.getHost() == null ? "127.0.0.1" : uri.getHost();
        int port = uri.getPort();
        if (port == -1) {
            if ("http".equalsIgnoreCase(scheme)) {
                port = 80;
            } else if ("https".equalsIgnoreCase(scheme)) {
                port = 443;
            }
        }

        final String finalHost = host;
        final int finalPort = port;

        // 创建事件循环组
        EventLoopGroup workerGroup = new NioEventLoopGroup();

        try {
            // 创建SSL上下文（如果需要）
            SslContext sslContext = createSslContext(scheme);
            final SslContext finalSslContext = sslContext;

            // 创建Bootstrap
            Bootstrap bootstrap = new Bootstrap();
            bootstrap.group(workerGroup)
                    .channel(NioSocketChannel.class)
                    .option(ChannelOption.SO_KEEPALIVE, true)
                    .handler(new ChannelInitializer<Channel>() {
                        @Override
                        protected void initChannel(Channel ch) throws Exception {
                            if (finalSslContext != null) {
                                ch.pipeline().addLast(finalSslContext.newHandler(ch.alloc(), finalHost, finalPort));
                            }
                            ch.pipeline().addLast(new HttpClientCodec());
                            ch.pipeline().addLast(new HttpObjectAggregator(1024 * 1024));
                        }
                    });

            // 连接到目标服务器并发送请求
            ChannelFuture future = bootstrap.connect(finalHost, finalPort);
            future.addListener((ChannelFutureListener) channelFuture -> {
                if (channelFuture.isSuccess()) {
                    // 连接成功，发送请求
                    channelFuture.channel().writeAndFlush(proxyRequest);
                } else {
                    // 连接失败，返回错误响应
                    log.error("Failed to connect to target server: {}:{}", finalHost, finalPort, channelFuture.cause());
                    sendErrorResponse(ctx, HttpResponseStatus.BAD_GATEWAY);
                    workerGroup.shutdownGracefully();
                }
            });

            // 添加响应处理器
            future.channel().pipeline().addLast(new SimpleChannelInboundHandler<FullHttpResponse>() {
                @Override
                protected void channelRead0(ChannelHandlerContext channelCtx, FullHttpResponse response) throws Exception {
                    handleProxyResponse(channelCtx, ctx, response, url, method, headers, requestBody, workerGroup);
                }

                @Override
                public void exceptionCaught(ChannelHandlerContext channelCtx, Throwable cause) throws Exception {
                    log.error("Exception in org.example.server.proxy response handler", cause);
                    sendErrorResponse(ctx, HttpResponseStatus.BAD_GATEWAY);
                    workerGroup.shutdownGracefully();
                }
            });
        } catch (Exception e) {
            log.error("Error in recordRequest", e);
            sendErrorResponse(ctx, HttpResponseStatus.INTERNAL_SERVER_ERROR);
            workerGroup.shutdownGracefully();
        }
    }

    /**
     * 构建代理请求
     */
    private FullHttpRequest buildProxyRequest(FullHttpRequest originalRequest, URI uri) {
        try {
            String path = uri.getRawPath() + (uri.getRawQuery() == null ? "" : "?" + uri.getRawQuery());
            FullHttpRequest proxyRequest = new DefaultFullHttpRequest(
                    HttpVersion.HTTP_1_1,
                    originalRequest.method(),
                    path,
                    originalRequest.content().retainedDuplicate());

            // 复制请求头
            proxyRequest.headers().set(originalRequest.headers());
            proxyRequest.headers().set(HttpHeaderNames.HOST, uri.getHost() != null ? uri.getHost() : "127.0.0.1");

            return proxyRequest;
        } catch (Exception e) {
            log.error("Failed to build org.example.server.proxy request for URI: {}", uri, e);
            return null;
        }
    }

    /**
     * 创建SSL上下文
     */
    private SslContext createSslContext(String scheme) {
        try {
            if ("https".equalsIgnoreCase(scheme)) {
                return SslContextBuilder.forClient()
                        .trustManager(InsecureTrustManagerFactory.INSTANCE)
                        .build();
            }
            return null;
        } catch (Exception e) {
            log.error("Failed to create SSL context for scheme: {}", scheme, e);
            return null;
        }
    }

    /**
     * 处理代理响应
     */
    private void handleProxyResponse(ChannelHandlerContext proxyCtx, ChannelHandlerContext clientCtx, 
                                     FullHttpResponse response, String url, String method, 
                                     Map<String, String> headers, String requestBody, 
                                     EventLoopGroup workerGroup) {
        long startTime = System.currentTimeMillis();

        try {
            // 读取响应内容
            String responseBody = response.content().toString(CharsetUtil.UTF_8);
            long duration = System.currentTimeMillis() - startTime;

            // 创建Transaction记录
            Transaction transaction = new Transaction();
            transaction.setUrl(url);
            transaction.setMethod(method);
            transaction.setHeaders(headers.toString());
            transaction.setRequest(requestBody);
            transaction.setResponse(responseBody);
            transaction.setDuration(duration);

            // 保存记录
            transactionService.save(transaction);

            log.info("Recorded transaction for URL: {}, Status: {}, Duration: {}ms", url, response.status(), duration);

            // 将响应发送回客户端
            FullHttpResponse proxyResponse = new DefaultFullHttpResponse(
                    response.protocolVersion(),
                    response.status(),
                    response.content().retainedDuplicate());
            proxyResponse.headers().set(response.headers());

            clientCtx.writeAndFlush(proxyResponse).addListener(ChannelFutureListener.CLOSE);
        } catch (Exception e) {
            log.error("Error handling org.example.server.proxy response for URL: {}", url, e);
            sendErrorResponse(clientCtx, HttpResponseStatus.INTERNAL_SERVER_ERROR);
        } finally {
            workerGroup.shutdownGracefully();
        }
    }

    private void replayRequest(ChannelHandlerContext ctx, String url, String requestBody) {
        // 查找匹配的记录
        Transaction transaction = transactionService.find(url, requestBody);

        if (transaction != null) {
            // 找到匹配的记录，返回记录的响应
            log.info("Found recorded response for URL: {}", url);

            FullHttpResponse response = new DefaultFullHttpResponse(
                    HttpVersion.HTTP_1_1,
                    HttpResponseStatus.OK);
            response.headers().set(HttpHeaderNames.CONTENT_TYPE, "application/json");
            response.headers().set(HttpHeaderNames.CONTENT_LENGTH, transaction.getResponse().length());
            response.content().writeBytes(transaction.getResponse().getBytes());

            ctx.writeAndFlush(response);
        } else {
            // 没有找到匹配的记录
            log.warn("No recorded response found for URL: {}", url);

            FullHttpResponse response = new DefaultFullHttpResponse(
                    HttpVersion.HTTP_1_1,
                    HttpResponseStatus.NOT_FOUND);
            String errorMsg = "{\n  \"error\": \"No recorded response found for this request\"\n}";
            response.headers().set(HttpHeaderNames.CONTENT_TYPE, "application/json");
            response.headers().set(HttpHeaderNames.CONTENT_LENGTH, errorMsg.length());
            response.content().writeBytes(errorMsg.getBytes());

            ctx.writeAndFlush(response);
        }
    }

    private void sendErrorResponse(ChannelHandlerContext ctx, HttpResponseStatus status) {
        FullHttpResponse response = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, status);
        ctx.writeAndFlush(response).addListener(ChannelFutureListener.CLOSE);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        log.error("Exception in SSL org.example.server.proxy handler", cause);
        ctx.close();
    }
}