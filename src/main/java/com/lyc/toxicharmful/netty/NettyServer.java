package com.lyc.toxicharmful.netty;

import com.lyc.toxicharmful.config.properties.NettyProperties;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.DelimiterBasedFrameDecoder;
import io.netty.handler.codec.string.StringDecoder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.net.InetSocketAddress;

/**
 * 类 NettyServer
 *
 * @author ChenQi
 * @date 2024/7/29
 */
@Slf4j
@Component
public class NettyServer {

    @Resource
    private NettyServerHandler nettyServerHandler;

    @Resource
    private NettyProperties nettyProperties;

    public void start() throws InterruptedException {
        // 引导辅助程序
        ServerBootstrap b = new ServerBootstrap();
        // 通过nio方式来接收连接和处理连接
        EventLoopGroup group = new NioEventLoopGroup();
        try {
            b.group(group);
            // 设置nio类型的channel
            b.channel(NioServerSocketChannel.class);
            // 设置监听端口
            b.localAddress(new InetSocketAddress(nettyProperties.getPort()));
            // 有连接到达时会创建一个channel
            b.childHandler(new ChannelInitializer<SocketChannel>() {
                @Override
                protected void initChannel(SocketChannel ch) throws Exception {
                    //解决连包问题
                    ByteBuf delimiter = Unpooled.copiedBuffer("\r\n".getBytes());
                    ch.pipeline().addLast(new DelimiterBasedFrameDecoder(16 * 1024, delimiter));
                    ch.pipeline().addLast(new StringDecoder());
                    // pipeline管理channel中的Handler，在channel队列中添加一个handler来处理业务
                    ch.pipeline().addLast("myHandler", nettyServerHandler);
                }
            });
            // 配置完成，开始绑定server，通过调用sync同步方法阻塞直到绑定成功
            ChannelFuture f = b.bind().sync();
            log.info("{} started and listen on {}", NettyServer.class.getName(), f.channel().localAddress());
            f.channel().closeFuture().sync();// 应用程序会一直等待，直到channel关闭
        } catch (Exception e) {
            log.error("NettyServer start error", e);
        } finally {
            // 关闭EventLoopGroup，释放掉所有资源包括创建的线程
            group.shutdownGracefully().sync();
        }
    }
}
