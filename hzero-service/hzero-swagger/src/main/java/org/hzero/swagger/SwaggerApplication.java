package org.hzero.swagger;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

import org.hzero.autoconfigure.swagger.EnableHZeroSwagger;

/**
 * HZERO Swagger 文档服务 <br/>
 * 注：Swagger 服务中，service_route、service，默认不与产品、环境做关联，都设置默认ID=0
 */
@EnableHZeroSwagger
@EnableDiscoveryClient
@SpringBootApplication
public class SwaggerApplication {

    public static void main(String[] args) {
        SpringApplication.run(SwaggerApplication.class, args);
    }

}
