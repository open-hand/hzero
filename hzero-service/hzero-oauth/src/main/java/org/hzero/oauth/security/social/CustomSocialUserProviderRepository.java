package org.hzero.oauth.security.social;

import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.collections4.CollectionUtils;
import org.hzero.oauth.domain.entity.User;
import org.hzero.oauth.domain.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;

import org.hzero.oauth.domain.entity.UserOpenAccount;
import org.hzero.oauth.domain.repository.UserOpenAccountRepository;
import org.hzero.starter.social.core.common.connect.SocialUserData;
import org.hzero.starter.social.core.exception.SocialErrorCode;
import org.hzero.starter.social.core.exception.UserBindException;
import org.hzero.starter.social.core.provider.SocialUserProviderRepository;

/**
 *
 * @author bojiangzhou 2019/09/01
 */
public class CustomSocialUserProviderRepository implements SocialUserProviderRepository {

    @Autowired
    private UserOpenAccountRepository userOpenAccountRepository;
    @Autowired
    private UserRepository userRepository;

    @Override
    public String findUsernameByProviderId(String providerId, String providerUserId) {
        List<String> usernames = userOpenAccountRepository.selectUsernameByOpenId(providerId, providerUserId);
        return CollectionUtils.isNotEmpty(usernames) ? usernames.get(0) : null;
    }

    @Override
    public List<String> findUsernameByUnionId(String providerId, String providerUnionId) {
        return userOpenAccountRepository.selectUsernameByUnionId(providerId, providerUnionId);
    }

    @Override
    public List<SocialUserData> findProviderUser(String providerId, String username) {
        List<UserOpenAccount> accounts = userOpenAccountRepository.selectOpenAccountByUsername(providerId, username);
        return accounts.stream().map(a -> {
            SocialUserData su = new SocialUserData();
            su.setProviderId(a.getOpenId());
            su.setProviderUnionId(a.getUnionId());
            su.setDisplayName(a.getOpenName());
            su.setImageUrl(a.getImageUrl());
            return su;
        }).collect(Collectors.toList());
    }

    @Override
    public void updateUserBind(String username, String providerId, String providerUserId, SocialUserData socialUser) {
        UserOpenAccount account = userOpenAccountRepository.selectOpenAccount(providerId, username);
        account.setOpenId(providerUserId);
        account.setUnionId(socialUser.getProviderUnionId());
        account.setOpenName(socialUser.getDisplayName());
        account.setImageUrl(socialUser.getImageUrl());
        userOpenAccountRepository.updateByPrimaryKeySelective(account);
    }

    @Override
    public void createUserBind(String username, String providerId, String providerUserId, SocialUserData socialUser) {
        User user = userRepository.selectLoginUserByLoginName(username);
        UserOpenAccount params;

        params= new UserOpenAccount();
        params.setUsername(username);
        params.setOpenAppCode(providerId);
        params.setOpenId(providerUserId);
        params.setUnionId(socialUser.getProviderUnionId());

        if (CollectionUtils.isNotEmpty(userOpenAccountRepository.select(params))) {
            throw new UserBindException(SocialErrorCode.USER_ALREADY_BIND);
        }

        params = new UserOpenAccount();
        params.setOpenAppCode(providerId);
        params.setOpenId(providerUserId);
        params.setUnionId(socialUser.getProviderUnionId());

        if (CollectionUtils.isNotEmpty(userOpenAccountRepository.select(params))) {
            throw new UserBindException(SocialErrorCode.OPEN_ID_ALREADY_BIND_OTHER_USER);
        }

        UserOpenAccount account = new UserOpenAccount();
        account.setOpenAppCode(providerId);
        account.setOpenId(providerUserId);
        account.setUnionId(socialUser.getProviderUnionId());
        account.setUsername(username);
        account.setOpenName(socialUser.getDisplayName());
        account.setImageUrl(socialUser.getImageUrl());
        account.setTenantId(user.getTenantId());
        userOpenAccountRepository.insertSelective(account);
    }
}
