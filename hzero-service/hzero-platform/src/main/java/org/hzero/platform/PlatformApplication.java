package org.hzero.platform;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

import org.hzero.autoconfigure.platform.EnableHZeroPlatform;

/**
 * HZero平台服务启动类
 * 主要包含平台通用功能
 * 
 * @author xianzhi.chen@hand-china.com 2018年6月7日下午6:09:44
 */
@EnableHZeroPlatform
@EnableDiscoveryClient
@SpringBootApplication
public class PlatformApplication {

    public static void main(String[] args) {
        try {
            SpringApplication.run(PlatformApplication.class, args);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
