package org.hzero.boot.platform.ds.autoconfigure;

import org.hzero.boot.platform.ds.feign.DsRemoteService;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

/**
 * 数据源自动化配置类
 *
 * @author xianzhi.chen@hand-china.com 2019年1月21日下午8:16:20
 */
@Configuration
@ComponentScan(basePackages = "org.hzero.boot.platform.ds")
@EnableFeignClients(basePackageClasses = DsRemoteService.class)
public class DsAutoConfigure {

}
