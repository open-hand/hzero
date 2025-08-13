package org.hzero.oauth.security.social;

import org.hzero.boot.oauth.domain.entity.BaseOpenApp;
import org.hzero.boot.oauth.domain.repository.BaseOpenAppRepository;
import org.hzero.mybatis.helper.DataSecurityHelper;
import org.hzero.mybatis.security.DataSecurityInterceptor;
import org.hzero.mybatis.service.DataSecurityKeyService;
import org.hzero.starter.social.core.provider.Provider;
import org.hzero.starter.social.core.provider.SocialProviderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.CollectionUtils;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author bojiangzhou 2019/09/01
 */
public class CustomSocialProviderRepository extends SocialProviderRepository {

    @Autowired
    private BaseOpenAppRepository baseOpenAppRepository;

    @Autowired
    public void setDataSecurityKeyService(DataSecurityKeyService dataSecurityKeyService) {
        // 处理在组件加载时，DataSecurityInterceptor的DataSecurityKeyService为空的问题
        DataSecurityInterceptor.setDataSecurityKeyService(dataSecurityKeyService);
    }

    @Override
    public List<Provider> getProvider(String providerId) {
        BaseOpenApp params = new BaseOpenApp();
        params.setAppCode(providerId);
        List<BaseOpenApp> apps = baseOpenAppRepository.select(params);

        if (CollectionUtils.isEmpty(apps)) {
            return Collections.emptyList();
        }

        return apps.stream()
                .map(app -> {
                    Provider provider = new Provider(
                            providerId,
                            app.getChannel(),
                            app.getAppId(),
                            decryptAppKey(app.getAppKey()),
                            app.getSubAppId()
                    );
                    provider.setScope(app.getScope());
                    return provider;
                })
                .collect(Collectors.toList());
    }

    /**
     * 解密授权码参数
     *
     * @param encryptAppKey 加密后的appKey
     * @return 解密后的授权码
     */
    private String decryptAppKey(String encryptAppKey) {
        try {
            // 返回解密后的参数
            return DataSecurityHelper.decrypt(encryptAppKey);
        } catch (Exception e) {
            // 原有的参数就是解密的，返回原有参数即可
            return encryptAppKey;
        }
    }
}
