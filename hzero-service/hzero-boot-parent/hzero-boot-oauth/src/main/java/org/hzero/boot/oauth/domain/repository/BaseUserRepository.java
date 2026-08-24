package org.hzero.boot.oauth.domain.repository;

import org.hzero.boot.oauth.domain.entity.BaseUser;
import org.hzero.core.user.UserType;
import org.hzero.mybatis.base.BaseRepository;

/**
 *
 * @author bojiangzhou 2019/08/06
 */
public interface BaseUserRepository extends BaseRepository<BaseUser> {

    /**
     * 查询账号是否存在
     */
    boolean existsByLoginName(String loginName);

    /**
     * 查询手机是否存在
     */
    boolean existsByPhone(String phone, UserType userType);

    /**
     * 查询邮箱是否存在
     */
    boolean existsByEmail(String email, UserType userType);

    /**
     * 判断账号，或手机，或邮箱是否存
     */
    boolean existsUser(String loginName, String phone, String email, UserType userType);

}
