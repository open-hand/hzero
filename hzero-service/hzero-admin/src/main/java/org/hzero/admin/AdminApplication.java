package org.hzero.admin;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

import org.hzero.autoconfigure.admin.EnableHZeroAdmin;

@EnableHZeroAdmin
@EnableDiscoveryClient
@SpringBootApplication
public class AdminApplication {

    public static void main(String[] args) {
        try {
            SpringApplication.run(AdminApplication.class, args);
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }
}


