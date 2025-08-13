package org.hzero.oauth.domain.service.impl;

import java.io.IOException;
import java.util.Date;
import javax.servlet.http.HttpServletRequest;

import cz.mallat.uasparser.OnlineUpdater;
import cz.mallat.uasparser.UASparser;
import cz.mallat.uasparser.UserAgentInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.scheduling.annotation.Async;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.common.exceptions.InvalidTokenException;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import io.choerodon.core.exception.CommonException;
import io.choerodon.core.oauth.CustomUserDetails;

import org.hzero.core.base.BaseConstants;
import org.hzero.oauth.domain.entity.AuditLogin;
import org.hzero.oauth.domain.entity.User;
import org.hzero.oauth.domain.repository.AuditLoginRepository;
import org.hzero.oauth.domain.repository.UserRepository;
import org.hzero.oauth.domain.service.AuditLoginService;
import org.hzero.oauth.domain.utils.IpAddressUtils;
import org.hzero.oauth.infra.constant.Constants;

/**
 * 登录审计服务实现类
 *
 * @author shuangfei.zhu@hand-china.com 2019/10/11 9:47
 */
@Component
public class AuditLoginServiceImpl implements AuditLoginService {

    @Autowired
    private AuditLoginRepository auditLoginRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private TokenStore tokenStore;

    private static final Logger LOGGER = LoggerFactory.getLogger(AuditLoginServiceImpl.class);
    /**
     * Http bearer token key
     */
    private static final String HTTP_HEADER_BEARER_TOKEN_PREFIX = "bearer";

    /**
     * 记录登陆信息
     */
    @Async("commonAsyncTaskExecutor")
    @Override
    public void addLoginRecord(HttpServletRequest request, String accessToken, String clientId, CustomUserDetails user) {
        login(request, accessToken, clientId, user, null);
    }

    /**
     * 记录登出信息
     */
    @Async("commonAsyncTaskExecutor")
    @Override
    public void addLogOutRecord(HttpServletRequest request, User user, String clientId) {
        logout(request, getAccessTokenRequest(request), clientId, user, null);
    }

    /**
     * 记录登录失败信息
     */
    @Async("commonAsyncTaskExecutor")
    @Override
    public void addLogFailureRecord(HttpServletRequest request, User user, String message) {
        loginFailed(request, user, message);
    }

    /**
     * 记录登录
     *
     * @param request 请求对象
     */
    private void login(HttpServletRequest request, String accessToken, String clientId, CustomUserDetails user, String loginMessage) {
        AuditLogin loginAudit = this.buildLoginAuditByRequest(request);
        if (StringUtils.isEmpty(loginAudit.getLoginClient())) {
            loginAudit.setLoginClient(clientId);
        }
        loginAudit.setLoginStatus(BaseConstants.Flag.YES)
                .setAuditType(Constants.AuditType.LOGIN)
                .setLoginMessage(loginMessage);
        if (user != null) {
            // TODO 待优化
            User userDetail = userRepository.selectByPrimaryKey(user.getUserId());
            loginAudit.setPhone(userDetail.getPhone())
                    .setEmail(userDetail.getEmail())
                    .setLoginName(userDetail.getLoginName())
                    .setTenantId(userDetail.getOrganizationId())
                    .setUserId(user.getUserId())
                    .setLoginDate(new Date())
                    .setAccessToken(accessToken);
        }
        this.checkLoginAudit(loginAudit);
        auditLoginRepository.insertSelective(loginAudit);
    }

    /**
     * 记录登出
     *
     * @param request     请求对象
     * @param accessToken token
     * @param clientId    客户端
     * @param user        用户信息
     */
    private void logout(HttpServletRequest request, String accessToken, String clientId, User user, String loginMessage) {
        AuditLogin loginAudit = this.buildLoginAuditByRequest(request);
        loginAudit.setLoginClient(clientId);
        loginAudit.setLoginStatus(BaseConstants.Flag.YES)
                .setAuditType(Constants.AuditType.LOGOUT)
                .setLoginMessage(loginMessage);
        if (user != null) {
            loginAudit.setPhone(user.getPhone())
                    .setEmail(user.getEmail())
                    .setLoginName(user.getLoginName())
                    .setTenantId(user.getOrganizationId())
                    .setUserId(user.getId())
                    .setLoginDate(new Date())
                    .setAccessToken(accessToken);
        }
        this.checkLoginAudit(loginAudit);
        auditLoginRepository.insertSelective(loginAudit);
    }

    /**
     * 登录失败
     *
     * @param user    用户信息
     * @param message 失败消息
     */
    private void loginFailed(HttpServletRequest request, User user, String message) {
        AuditLogin auditLogin = buildLoginAuditByRequest(request);
        String clientId = getClientId(request);
        if (auditLogin.getLoginClient() == null) {
            auditLogin.setLoginClient(clientId);
        }
        if (user != null) {
            auditLogin.setPhone(user.getPhone())
                    .setEmail(user.getEmail())
                    .setLoginName(user.getLoginName())
                    .setTenantId(user.getOrganizationId())
                    .setUserId(user.getId())
                    .setLoginDate(new Date());
        }
        // 设置状态参数
        auditLogin.setLoginStatus(BaseConstants.Flag.NO)
                .setAuditType(Constants.AuditType.LOGIN_FAILURE)
                .setLoginMessage(message);
        this.checkLoginAudit(auditLogin);
        auditLoginRepository.insertSelective(auditLogin);
    }

    private AuditLogin buildLoginAuditByRequest(HttpServletRequest request) {
        AuditLogin loginAudit = new AuditLogin();
        if (request != null) {
            //登录IP
            String ipAddress = IpAddressUtils.getIpAddress(request);
            //尝试获取clientId
            String clientId = request.getParameter("client_id");
            loginAudit.setLoginClient(clientId);

            loginAudit.setLoginIp(ipAddress);
            String userAgent = request.getHeader(HttpHeaders.USER_AGENT);
            try {
                UASparser uasParser = new UASparser(OnlineUpdater.getVendoredInputStream());
                UserAgentInfo userAgentInfo = uasParser.parse(userAgent);
                loginAudit.setLoginBrowser(userAgentInfo.getUaName()).setLoginOs(userAgentInfo.getOsName());
            } catch (IOException e) {
                throw new CommonException(e);
            }
        }
        return loginAudit;
    }

    /**
     * 防止由于非空字段为空导致的插入失败
     *
     * @param loginAudit 对象信息
     */
    private void checkLoginAudit(AuditLogin loginAudit) {
        if (loginAudit.getAuditType() == null) {
            loginAudit.setAuditType("N/A");
        }
        if (loginAudit.getLoginIp() == null) {
            loginAudit.setLoginIp("N/A");
        }
        if (loginAudit.getUserId() == null) {
            loginAudit.setUserId(-1L);
        }
        if (loginAudit.getLoginName() == null) {
            loginAudit.setLoginName("N/A");
        }
        if (loginAudit.getTenantId() == null) {
            loginAudit.setTenantId(-1L);
        }
        if (loginAudit.getLoginDate() == null) {
            loginAudit.setLoginDate(new Date());
        }
    }

    @Override
    public String getClientId(HttpServletRequest request) {
        //尝试获取clientId
        String clientId = request.getParameter("client_id");
        if (StringUtils.isEmpty(clientId)) {
            //如果无法从request中获取，尝试从redis中获取
            clientId = getClientIdFromRedisTokenStore(request);
        }
        return clientId;
    }

    /**
     * 通过Redis存储的Token信息获取认证信息进而获取ClientID信息
     *
     * @param request request
     * @return clientId
     */
    private String getClientIdFromRedisTokenStore(HttpServletRequest request) {
        String clientId;

        if (this.tokenStore == null) {
            LOGGER.warn("==========>redis token store is null<==========");
            return null;
        }
        String accessTokenValue = this.getAccessTokenRequest(request);
        if (StringUtils.isEmpty(accessTokenValue)) {
            return null;
        }
        OAuth2Authentication oAuth2Authentication = this.loadAuthentication(accessTokenValue);
        //获取ClientID信息
        clientId = oAuth2Authentication.getOAuth2Request().getClientId();
        if (LOGGER.isInfoEnabled()) {
            LOGGER.info("==========>after access token: {}, clientId={}<==========", accessTokenValue, clientId);
        }
        return clientId;
    }

    /**
     * 从请求头中获取access token
     *
     * @param request 请求头
     * @return access_token
     */
    private String getAccessTokenRequest(HttpServletRequest request) {
        String authentication = request.getHeader(HttpHeaders.AUTHORIZATION);
        if (authentication != null) {
            //不清楚大小写，直接截取
            return authentication.substring(HTTP_HEADER_BEARER_TOKEN_PREFIX.length()).trim();
        }
        return request.getParameter("access_token");
    }

    private OAuth2Authentication loadAuthentication(String accessTokenValue) {
        OAuth2AccessToken accessToken = tokenStore.readAccessToken(accessTokenValue);
        if (accessToken == null) {
            throw new InvalidTokenException("Invalid access token: " + accessTokenValue);
        }
        if (accessToken.isExpired()) {
            tokenStore.removeAccessToken(accessToken);
            throw new InvalidTokenException("Access token expired: " + accessTokenValue);
        }
        OAuth2Authentication result = tokenStore.readAuthentication(accessToken);
        if (result == null) {
            // in case of race condition
            throw new InvalidTokenException("Invalid access token: " + accessTokenValue);
        }
        return result;
    }

}
