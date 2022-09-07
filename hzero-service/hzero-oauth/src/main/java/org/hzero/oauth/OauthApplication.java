package org.hzero.oauth;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

import org.hzero.autoconfigure.oauth.EnableHZeroOauth;

/**
 * HZERO 认证服务
 */
@EnableHZeroOauth
@EnableDiscoveryClient
@SpringBootApplication
public class OauthApplication {

    public static void main(String[] args) {
        try {
            SpringApplication.run(OauthApplication.class, args);
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }

}

