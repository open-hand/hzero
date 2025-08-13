package org.hzero.imported;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

import org.hzero.autoconfigure.imported.EnableHZeroImport;

/**
 * HZERO 导入服务
 *
 * @author xiaowei.zhang@hand-china.com
 * @date 2018/5/10
 */
@EnableHZeroImport
@EnableDiscoveryClient
@SpringBootApplication
public class ImportApplication {

    public static void main(String[] args) {
        SpringApplication.run(ImportApplication.class, args);
    }

}
