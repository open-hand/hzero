package org.hzero.oauth.domain.repository;

import java.util.List;

import org.hzero.mybatis.base.BaseRepository;
import org.hzero.oauth.domain.entity.UserOpenAccount;

/**
 *
 * @author bojiangzhou 2019/09/01
 */
public interface UserOpenAccountRepository extends BaseRepository<UserOpenAccount> {


    List<String> selectUsernameByOpenId(String openAppCode, String openId);

    List<String> selectUsernameByUnionId(String openAppCode, String unionId);

    List<UserOpenAccount> selectOpenAccountByUsername(String openAppCode, String username);

    UserOpenAccount selectOpenAccount(String openAppCode, String username);
}
