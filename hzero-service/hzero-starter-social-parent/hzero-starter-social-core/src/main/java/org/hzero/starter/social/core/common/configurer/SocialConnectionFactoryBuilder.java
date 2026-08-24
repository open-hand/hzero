package org.hzero.starter.social.core.common.configurer;

import org.hzero.starter.social.core.common.connect.SocialConnectionFactory;
import org.hzero.starter.social.core.provider.Provider;


public interface SocialConnectionFactoryBuilder {

    /**
     * @return 应用编码
     */
    String getProviderId();

    /**
     * 配置 SocialConnectionFactory
     * 
     * @return SocialConnectionFactory
     */
    SocialConnectionFactory buildConnectionFactory(Provider provider);

}
