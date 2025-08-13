package org.hzero.iam.infra.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import io.choerodon.mybatis.common.BaseMapper;

import org.hzero.iam.domain.entity.UserOpenAccount;

/**
 * 用户第三方账号Mapper
 *
 * @author jiaxu.cui@hand-china.com 2018/9/29 16:28
 */
public interface UserOpenAccountMapper extends BaseMapper<UserOpenAccount> {

    List<UserOpenAccount> selectOpenAppAndBindUser(@Param("username") String username);
}
