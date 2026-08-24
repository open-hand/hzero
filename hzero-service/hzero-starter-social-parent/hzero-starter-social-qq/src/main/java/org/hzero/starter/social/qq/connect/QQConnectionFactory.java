package org.hzero.starter.social.qq.connect;

import org.hzero.starter.social.core.common.connect.SocialApiAdapter;
import org.hzero.starter.social.core.common.connect.SocialConnectionFactory;
import org.hzero.starter.social.core.common.connect.SocialServiceProvider;
import org.hzero.starter.social.core.provider.Provider;

/**
 * QQ Connection 工厂
 *
 * @author bojiangzhou
 */
public class QQConnectionFactory extends SocialConnectionFactory {

    public QQConnectionFactory(Provider provider, SocialServiceProvider serviceProvider, SocialApiAdapter apiAdapter) {
        super(provider, serviceProvider, apiAdapter);
    }
}
