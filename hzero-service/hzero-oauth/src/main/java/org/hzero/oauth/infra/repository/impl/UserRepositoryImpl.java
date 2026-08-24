package org.hzero.oauth.infra.repository.impl;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import io.choerodon.core.oauth.CustomUserDetails;

import org.hzero.common.HZeroConstant;
import org.hzero.common.HZeroService;
import org.hzero.core.redis.RedisHelper;
import org.hzero.core.redis.safe.SafeRedisHelper;
import org.hzero.core.user.UserType;
import org.hzero.core.util.Regexs;
import org.hzero.mybatis.base.impl.BaseRepositoryImpl;
import org.hzero.oauth.domain.entity.User;
import org.hzero.oauth.domain.repository.UserRepository;
import org.hzero.oauth.domain.vo.UserRoleDetails;
import org.hzero.oauth.domain.vo.UserVO;
import org.hzero.oauth.infra.mapper.UserPlusMapper;

/**
 * 用户资源库实现
 *
 * @author bojiangzhou 2018/12/04
 */
@Component
public class UserRepositoryImpl extends BaseRepositoryImpl<User> implements UserRepository {

    private static final String CONFIG_CODE_MENU_LAYOUT = "MENU_LAYOUT";
    private static final String CONFIG_CODE_MENU_LAYOUT_THEME = "MENU_LAYOUT_THEME";
    private static final String CONFIG_CODE_FAVICON = "FAVICON";

    @Autowired
    private UserPlusMapper userPlusMapper;
    @Autowired
    private RedisHelper redisHelper;

    @Override
    public List<UserRoleDetails> selectRoleDetails(Long userId) {
        return userPlusMapper.selectRoleDetails(userId);
    }

    @Override
    public List<UserRoleDetails> selectRootUserRoleDetails(Long userId, Long tenantId) {
        return userPlusMapper.selectRootUserRoleDetails(userId, tenantId);
    }

    @Override
    public User selectLoginUserByLoginName(String loginName) {
        User param = new User();
        param.setLoginName(loginName);

        return userPlusMapper.selectLoginUser(param);
    }

    @Override
    public User selectLoginUserByLoginName(String loginName, UserType userType) {
        User param = new User();
        param.setLoginName(loginName);
        param.setUserType(Optional.ofNullable(userType).map(UserType::value).orElse(null));
        return userPlusMapper.selectLoginUser(param);
    }

    @Override
    public User selectLoginUserByPhone(String phone, UserType userType) {
        User param = new User();
        param.setPhone(phone);
        param.setUserType(Optional.ofNullable(userType).map(UserType::value).orElse(null));

        return userPlusMapper.selectLoginUser(param);
    }

    @Nullable
    @Override
    public User selectLoginUserByPhone(@Nonnull String internationalTelCode, @Nonnull String phone, @Nonnull UserType userType) {
        User param = new User();
        param.setInternationalTelCode(internationalTelCode);
        param.setPhone(phone);
        param.setUserType(Optional.ofNullable(userType).map(UserType::value).orElse(null));

        return userPlusMapper.selectLoginUser(param);
    }

    @Override
    public User selectLoginUserByEmail(String email, UserType userType) {
        User param = new User();
        param.setEmail(email);
        param.setUserType(userType.value());

        return userPlusMapper.selectLoginUser(param);
    }

    @Override
    public User selectUserByPhoneOrEmail(String account, UserType userType) {
        if (StringUtils.isBlank(account)) {
            return null;
        }
        userType = Optional.ofNullable(userType).orElse(UserType.ofDefault());
        User user = null;
        if (Regexs.isMobile(account)) {
            user = selectLoginUserByPhone(account, userType);
        }
        else if (Regexs.isEmail(account)) {
            user = selectLoginUserByEmail(account, userType);
        }

        return user;
    }

    @Override
    public int countUserMemberRole(Long userId) {
        return userPlusMapper.countUserMemberRole(userId);
    }

    /**
     * 查询当前登录用户
     *
     * @return 返回当前登录用户
     */
    @Override
    public UserVO selectSelf(CustomUserDetails self) {

        UserVO params = new UserVO();
        params.setId(self.getUserId());
        params.setCurrentRoleId(self.getRoleId());
        params.setTenantId(self.getTenantId());

        UserVO userVO = userPlusMapper.selectSelf(params);

        // 设置当前切换的租户
        userVO.setTenantId(self.getTenantId());
        // 设置当前切换的角色
        userVO.setCurrentRoleId(self.getRoleId());

        // 设置 title/logo/layout/layoutTheme
        SafeRedisHelper.execute(HZeroService.Platform.REDIS_DB, () -> {
            userVO.setTitle(redisHelper.strGet(UserVO.generateCacheKey(HZeroConstant.Config.CONFIG_CODE_TITLE, self.getOrganizationId())));
            userVO.setLogo(redisHelper.strGet(UserVO.generateCacheKey(HZeroConstant.Config.CONFIG_CODE_LOGO, self.getOrganizationId())));
            userVO.setMenuLayout(redisHelper.strGet(UserVO.generateCacheKey(CONFIG_CODE_MENU_LAYOUT, self.getOrganizationId())));
            userVO.setMenuLayoutTheme(redisHelper.strGet(UserVO.generateCacheKey(CONFIG_CODE_MENU_LAYOUT_THEME, self.getOrganizationId())));
            userVO.setFavicon(redisHelper.strGet(UserVO.generateCacheKey(CONFIG_CODE_FAVICON, self.getOrganizationId())));
        });

        return userVO;
    }

    @Override
    public List<Long> findUserLegalOrganization(Long userId) {
        return userPlusMapper.findUserLegalOrganization(userId);
    }

    @Override
    public List<Long> selectUserRole(Long userId) {
        return userPlusMapper.selectUserRole(userId);
    }

    @Override
    public Set<String> selectRoleLabels(List<Long> roleIds) {
        if (CollectionUtils.isEmpty(roleIds)) {
            return Collections.emptySet();
        }
        return userPlusMapper.selectRoleLabels(roleIds);
    }
}
