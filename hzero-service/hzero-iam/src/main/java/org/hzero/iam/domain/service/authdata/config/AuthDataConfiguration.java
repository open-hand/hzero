package org.hzero.iam.domain.service.authdata.config;

import org.hzero.iam.domain.service.authdata.AuthDataManager;
import org.hzero.iam.domain.service.authdata.AuthDataProvider;
import org.hzero.iam.domain.service.authdata.impl.DefaultAuthDataManager;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

/**
 * 权限数据配置对象
 *
 * @author bo.he02@hand-china.com 2020/05/26 10:46
 */
@Configuration
public class AuthDataConfiguration {
    /**
     * 创建权限数据管理器
     *
     * @return 权限数据管理器
     */
    @Bean
    @ConditionalOnMissingBean(AuthDataManager.class)
    public AuthDataManager authDataManager(List<AuthDataProvider> authDataProviders) {
        // 创建默认的权限数据管理器
        DefaultAuthDataManager defaultAuthDataManager = new DefaultAuthDataManager();
        // 初始化权限数据管理器
        defaultAuthDataManager.init(authDataProviders);
        // 返回结果
        return defaultAuthDataManager;
    }
}
