package org.hzero.iam.domain.service.secgrp.authority.dcl;

import org.hzero.iam.infra.constant.Constants;

/**
 * 安全组数据权限服务本地编码抽象实现
 *
 * @author bergturing 2020/04/09 11:15
 */
public abstract class AbstractLocalDclService extends AbstractDclService {
    @Override
    protected String getValueSourceType() {
        // 本地编码
        return Constants.DocValueSourceType.LOCAL;
    }
}
