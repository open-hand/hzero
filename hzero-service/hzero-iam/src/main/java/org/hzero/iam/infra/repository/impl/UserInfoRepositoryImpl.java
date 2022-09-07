package org.hzero.iam.infra.repository.impl;

import org.springframework.stereotype.Component;

import org.hzero.iam.domain.entity.UserInfo;
import org.hzero.iam.domain.repository.UserInfoRepository;
import org.hzero.mybatis.base.impl.BaseRepositoryImpl;

/**
 * 用户信息仓库实现
 *
 * @author bergturing 2020/08/25 15:06
 */
@Component
public class UserInfoRepositoryImpl extends BaseRepositoryImpl<UserInfo> implements UserInfoRepository {

}
