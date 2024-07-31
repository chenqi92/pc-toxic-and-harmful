package com.lyc.toxicharmful.netty;

import cn.allbs.hj212.enums.GBT16706;
import cn.allbs.hj212.format.T212Generator;
import cn.allbs.hj212.format.T212Mapper;
import cn.allbs.hj212.model.HjData;
import com.lyc.toxicharmful.service.MessageFactory;
import com.lyc.toxicharmful.utils.MsgHandleUtils;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.socket.SocketChannel;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.io.StringWriter;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 类 NettyServerHandler
 *
 * @author ChenQi
 * @date 2024/7/29
 */
@Slf4j
@ChannelHandler.Sharable
@Component
@AllArgsConstructor
public class NettyServerHandler extends ChannelInboundHandlerAdapter {

    private final MessageFactory messageFactory;

    private static final Map<String, SocketChannel> channelMap = new ConcurrentHashMap<>();

    /**
     * 连接后回调
     */
    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        super.channelActive(ctx);
        log.info("client:{} is connected", MsgHandleUtils.getIPPortString(ctx));
    }

    /**
     * 接收到报文回调
     */
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) {
        try {
            // 获取企业mn号与排口code对应编号
            String msgStr = new String(msg.toString().getBytes(StandardCharsets.UTF_8), StandardCharsets.UTF_8);
            log.info("{}接收到三废排口报文：{}", LocalDateTime.now().format(DateTimeFormatter.BASIC_ISO_DATE), msgStr);
            T212Mapper mapper = new T212Mapper().enableDefaultParserFeatures().enableDefaultVerifyFeatures();
            HjData data = mapper.readData(msgStr);
            if (data == null || data.getCp() == null || !StringUtils.hasText(data.getMn())) {
                log.error("缺少必要信息或格式不正确");
                return;
            }
            if (Optional.of(data).map(HjData::getMn).isPresent()) {
                channelMap.put(data.getMn(), (SocketChannel) ctx.channel());
            } else {
                log.info("传的报文中mn为空，关闭该通道");
                ctx.channel().close();
            }
            if (GBT16706._91.value().equals(data.getSt())) {
                log.info("数据{}", data);
                messageFactory.systemAction(data);
            } else {
                log.info("数据储存{}", data);
                messageFactory.hj212DataSave(data);
            }
        } catch (Exception e) {
            log.error("处理报文发生异常{}", e.getLocalizedMessage());
        }
    }

    /**
     * 方法功能: 发送系统报文信息
     *
     * @param mn mn号
     */
    public void sengMess(String mn, String ms) {
        SocketChannel sc = channelMap.get(mn);
        StringWriter writer = new StringWriter();
        T212Generator generator = new T212Generator(writer);
        try {
            generator.writeHeader();
            generator.writeDataAndLenAndCrc(ms.toCharArray());
            generator.writeFooter();
            // 查询redis中是否有需要发送的数据
            ByteBuf encoded = sc.alloc().buffer(writer.toString().length());
            encoded.writeBytes(writer.toString().getBytes("GBK"));
            sc.writeAndFlush(encoded).addListener((ChannelFutureListener) future -> log.info("回复:{}", writer));
        } catch (Exception e) {
            log.error("数据发送失败");
        } finally {
            generator.close();
        }

    }

    /**
     * 数据处理完成回调
     *
     * @param ctx
     */
    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) {
        log.info("channelReadComplete" + LocalDateTime.now());
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        //cause.printStackTrace();// 捕捉异常信息
        // 出现异常时关闭channel
        ctx.close();
    }

    /**
     * 客户端断开连接回调
     */
    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        // channel失效处理,客户端下线或者强制退出等任何情况都触发这个方法
        log.info("{} 断开连接", MsgHandleUtils.getIPPortString(ctx));
        ctx.channel().close();
        super.channelInactive(ctx);
    }
}
