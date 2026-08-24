package org.hzero.file;

import org.hzero.autoconfigure.file.EnableHZeroFile;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

/**
 * HZERO 文件服务
 *
 * @author xianzhi.chen@hand-china.com 2018年6月25日下午4:20:25
 */
@EnableHZeroFile
@EnableDiscoveryClient
@SpringBootApplication
public class FileApplication {

    public static void main(String[] args) {
        SpringApplication.run(FileApplication.class, args);
    }

}
