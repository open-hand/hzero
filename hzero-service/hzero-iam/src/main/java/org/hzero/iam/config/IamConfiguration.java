package org.hzero.iam.config;

import org.springframework.beans.factory.SmartInitializingSingleton;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import org.hzero.core.message.MessageAccessor;
import org.hzero.iam.app.service.UserSelfService;
import org.hzero.iam.app.service.UserService;
import org.hzero.iam.app.service.impl.UserSelfServiceImpl;
import org.hzero.iam.app.service.impl.UserServiceImpl;
import org.hzero.iam.domain.service.UserRoleImportService;
import org.hzero.iam.domain.service.impl.UserRoleImportServiceImpl;
import org.hzero.iam.domain.service.role.*;
import org.hzero.iam.domain.service.user.*;
import org.hzero.iam.domain.service.user.impl.DefaultClientDetailsService;
import org.hzero.iam.domain.service.user.impl.DefaultUserDetailsService;

/**
 * @author bojiangzhou 2019/03/29
 */
@EnableConfigurationProperties(IamProperties.class)
@Configuration
public class IamConfiguration {

    @Bean
    public SmartInitializingSingleton iamSmartInitializingSingleton() {
        return () -> {
            // 加入消息文件
            MessageAccessor.addBasenames("classpath:messages/messages_hiam");
        };
    }

    @Bean
    @ConditionalOnMissingBean(UserSelfService.class)
    public UserSelfService userSelfService() {
        return new UserSelfServiceImpl();
    }

    @Bean
    @ConditionalOnMissingBean(UserService.class)
    public UserService userService() {
        return new UserServiceImpl();
    }

    @Bean
    @ConditionalOnMissingBean(UserCheckService.class)
    public UserCheckService userCheckService() {
        return new UserCheckService();
    }

    @Bean
    @ConditionalOnMissingBean(UserCaptchaService.class)
    public UserCaptchaService userCaptchaService() {
        return new UserCaptchaService();
    }

    @Bean
    @ConditionalOnMissingBean(UserDetailsService.class)
    public UserDetailsService userDetailsService() {
        return new DefaultUserDetailsService();
    }

    @Bean
    @ConditionalOnMissingBean(ClientDetailsService.class)
    public ClientDetailsService clientDetailsService() {
        return new DefaultClientDetailsService();
    }

    @Bean
    @ConditionalOnMissingBean(UserCreateService.class)
    public UserCreateService userCreateService() {
        return new UserCreateService();
    }

    @Bean
    @ConditionalOnMissingBean(UserCreateInternalService.class)
    public UserCreateInternalService userCreateInternalService() {
        return new UserCreateInternalService();
    }

    @Bean
    @ConditionalOnMissingBean(UserUpdateService.class)
    public UserUpdateService userUpdateService() {
        return new UserUpdateService();
    }

    @Bean
    @ConditionalOnMissingBean(UserRegisterService.class)
    public UserRegisterService userRegisterService() {
        return new UserRegisterService();
    }

    @Bean
    @ConditionalOnMissingBean(RoleCreateService.class)
    public RoleCreateService roleCreateService() {
        return new RoleCreateService();
    }

    @Bean
    @ConditionalOnMissingBean(RoleUpdateService.class)
    public RoleUpdateService roleUpdateService() {
        return new RoleUpdateService();
    }

    @Bean
    @ConditionalOnMissingBean(RolePermissionSetAssignService.class)
    public RolePermissionSetAssignService rolePermissionSetAssignService() {
        return new RolePermissionSetAssignService();
    }

    @Bean
    @ConditionalOnMissingBean(RolePermissionSetRecycleService.class)
    public RolePermissionSetRecycleService rolePermissionSetRecycleService() {
        return new RolePermissionSetRecycleService();
    }

    @Bean
    @ConditionalOnMissingBean(MemberRoleAssignService.class)
    public MemberRoleAssignService memberRoleAssignService() {
        return new MemberRoleAssignService();
    }

    @Bean
    @ConditionalOnMissingBean(UserRoleImportService.class)
    public UserRoleImportService userRoleImportService() {
        return new UserRoleImportServiceImpl();
    }

    @Bean
    @ConditionalOnMissingBean(TemplateRoleCreateService.class)
    public TemplateRoleCreateService templateRoleCreateService() {
        return new TemplateRoleCreateService();
    }

}
