package org.hzero.boot.oauth.infra.repository.impl;

import org.springframework.stereotype.Component;

import org.hzero.boot.oauth.domain.entity.BaseUserInfo;
import org.hzero.boot.oauth.domain.repository.BaseUserInfoRepository;
import org.hzero.mybatis.base.impl.BaseRepositoryImpl;

/**
 *
 * @author bojiangzhou 2019/08/06
 */
@Component
public class BaseUserInfoRepositoryImpl extends BaseRepositoryImpl<BaseUserInfo> implements BaseUserInfoRepository {

}
