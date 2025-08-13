package org.hzero.iam.domain.repository;

import java.util.List;

import org.hzero.iam.domain.entity.UserOpenAccount;
import org.hzero.mybatis.base.BaseRepository;

/**
 * @author jiaxu.cui@hand-china.com 2018/9/29 16:26
 */
public interface UserOpenAccountRepository extends BaseRepository<UserOpenAccount> {

    List<UserOpenAccount> selectOpenAppAndBindUser(String username);
}
