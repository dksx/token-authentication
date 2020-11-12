package com.server.application.configuration;

import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import org.springframework.boot.web.embedded.netty.NettyReactiveWebServerFactory;
import org.springframework.boot.web.embedded.netty.NettyServerCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import reactor.netty.http.server.HttpServer;

@Configuration
public class NettyWebServerFactory {

    @Bean
    public NettyReactiveWebServerFactory nettyReactiveWebServerFactory() {
        NettyReactiveWebServerFactory webServerFactory = new NettyReactiveWebServerFactory();
        webServerFactory.addServerCustomizers(new EventLoopNettyCustomizer());
        return webServerFactory;
    }

    private static class EventLoopNettyCustomizer implements NettyServerCustomizer {
        @Override
        public HttpServer apply(HttpServer httpServer) {
            EventLoopGroup upperGroup = new NioEventLoopGroup();
            EventLoopGroup lowerGroup = new NioEventLoopGroup();
            return httpServer.tcpConfiguration(tcpServer ->
                            tcpServer.bootstrap(serverBootstrap ->
                                    serverBootstrap.group(upperGroup, lowerGroup).channel(NioServerSocketChannel.class)
                    ));
        }
    }
}
