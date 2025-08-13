package org.hzero.boot.platform.common.autoconfigure;

import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

/**
 * <p>
 * 模板配置自动注册类
 * </p>
 *
 * @author xiaoyu.zhao@hand-china.com 2019/07/11 10:13
 */
@Configuration
@ComponentScan("org.hzero.boot.platform.common")
@EnableFeignClients("org.hzero.boot.platform.common.infra.feign")
public class CommonAutoConfigure {

}
