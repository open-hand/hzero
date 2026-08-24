package org.hzero.feign.autoconfigure;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

/**
 * 包扫描
 *
 * @author shuangfei.zhu@hand-china.com 2019/05/06 14:16
 */
@Configuration
@ComponentScan(basePackages = "org.hzero.feign")
public class FeignAutoConfiguration {

}
