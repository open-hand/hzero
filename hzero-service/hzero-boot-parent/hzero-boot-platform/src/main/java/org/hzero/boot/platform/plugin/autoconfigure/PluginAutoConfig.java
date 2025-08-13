package org.hzero.boot.platform.plugin.autoconfigure;

import org.hzero.boot.platform.plugin.hr.feign.EmployeeRemoteService;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

/**
 * @author qingsheng.chen@hand-china.com 2019-05-21 19:45
 */
@Configuration
@ComponentScan(basePackages = {"org.hzero.boot.platform.plugin.hr", "org.hzero.boot.platform.plugin.search"})
@EnableFeignClients(basePackageClasses = EmployeeRemoteService.class)
public class PluginAutoConfig {
}
