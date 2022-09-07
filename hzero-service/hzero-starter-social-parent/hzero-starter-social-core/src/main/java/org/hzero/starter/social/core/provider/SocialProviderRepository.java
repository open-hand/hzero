package org.hzero.starter.social.core.provider;

import java.util.List;

/**
 * APP 资源库
 *
 * @author bojiangzhou 2019/08/29
 */
public abstract class SocialProviderRepository {

    /**
     * 获取三方应用信息
     *
     * @param providerId 三方应用编码
     * @return Provider 一个三方应用可能有多个渠道的信息，因此返回 List
     */
    public abstract List<Provider> getProvider(String providerId);

}
