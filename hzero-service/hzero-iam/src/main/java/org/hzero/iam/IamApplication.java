package org.hzero.iam;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;


import org.hzero.autoconfigure.iam.EnableHZeroIam;


@EnableHZeroIam
@EnableDiscoveryClient
@SpringBootApplication
public class IamApplication {

    public static void main(String[] args) {
        try {
            SpringApplication.run(IamApplication.class, args);
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }

}

