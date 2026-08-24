package org.hzero.boot.platform.profile.autoconfigure;

import org.hzero.boot.platform.profile.feign.ProfileRemoteService;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

/**
 * <p>
 * 配置维护自动注册类
 * </p>
 *
 * @author xiaoyu.zhao@hand-china.com 2019/07/10 10:13
 */
@Configuration
@ComponentScan(basePackages = "org.hzero.boot.platform.profile")
@EnableFeignClients(basePackageClasses = ProfileRemoteService.class)
public class ProfileAutoConfigure {

}
