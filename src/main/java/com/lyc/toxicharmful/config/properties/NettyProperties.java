package com.lyc.toxicharmful.config.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * 类 NettyProperties
 *
 * @author ChenQi
 * @date 2024/7/29
 */
@Data
@Component
@ConfigurationProperties(prefix = "netty")
public class NettyProperties {

    /**
     * 监听端口
     */
    private Integer port = 6000;
}
