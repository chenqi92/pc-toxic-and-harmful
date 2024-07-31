package com.lyc.toxicharmful;

import cn.allbs.influx.annotation.EnableAllbsInflux;
import com.lyc.toxicharmful.netty.NettyServer;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

import javax.annotation.Resource;

@EnableAsync
@EnableAllbsInflux
@SpringBootApplication
public class PcToxicAndHarmfulApplication implements CommandLineRunner {

    @Resource
    private NettyServer nettyServer;

    public static void main(String[] args) {
        SpringApplication.run(PcToxicAndHarmfulApplication.class, args);
    }

    @Override
    public void run(String... args) throws Exception {
        nettyServer.start();
    }

}
