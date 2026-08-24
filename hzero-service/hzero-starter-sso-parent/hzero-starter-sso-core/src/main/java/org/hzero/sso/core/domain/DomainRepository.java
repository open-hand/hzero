package org.hzero.sso.core.domain;

import javax.annotation.Nullable;

/**
 * 域名资源库
 *
 * @author bojiangzhou 2020/08/15
 */
public interface DomainRepository {

    /**
     * 根据域名查询 Domain
     *
     * @param host 域名
     */
    @Nullable
    Domain selectByHost(String host);
}
