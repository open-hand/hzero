package org.hzero.iam.domain.service.secgrp.authority.dcl;

import org.hzero.iam.infra.constant.Constants;

/**
 * 安全组数据权限服务值集抽象实现
 *
 * @author bergturing 2020/04/09 11:15
 */
public abstract class AbstractLovDclService extends AbstractDclService {
    @Override
    protected String getValueSourceType() {
        // 值集
        return Constants.DocValueSourceType.LOV;
    }
}
