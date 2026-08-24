package org.hzero.iam.app.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.choerodon.core.exception.CommonException;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.ListUtils;
import org.hzero.boot.imported.app.service.IDoImportService;
import org.hzero.boot.imported.infra.validator.annotation.ImportService;
import org.hzero.core.base.BaseConstants;
import org.hzero.iam.domain.entity.*;
import org.hzero.iam.domain.repository.TenantRepository;
import org.hzero.iam.domain.repository.UserAuthorityLineRepository;
import org.hzero.iam.domain.repository.UserAuthorityRepository;
import org.hzero.iam.domain.repository.UserRepository;
import org.hzero.iam.domain.service.authdata.AuthDataManager;
import org.hzero.iam.domain.service.authdata.AuthDataVoConverter;
import org.hzero.iam.domain.service.authdata.condition.AuthDataCondition;
import org.hzero.iam.domain.service.authdata.vo.AuthDataVo;
import org.hzero.iam.infra.constant.Constants;
import org.hzero.mybatis.domian.Condition;
import org.hzero.mybatis.util.Sqls;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;


/**
 * @author jianbo.li@hand-china.com 2018/10/16
 */
@ImportService(templateCode = Constants.ImportTemplateCode.AUTH_TEMP)
public class AuthImportServiceImpl implements IDoImportService {
    /**
     * 日志打印对象
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(AuthImportServiceImpl.class);

    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private UserAuthorityRepository userAuthorityRepository;
    @Autowired
    private UserAuthorityLineRepository userAuthorityLineRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private TenantRepository tenantRepository;
    /**
     * 权限数据管理器对象
     */
    @Autowired
    private AuthDataManager authDataManager;
    @Autowired
    private UserAuthImportAuthDataVoConverter userAuthImportAuthDataVoConverter;

    @Override
    public Boolean doImport(String data) {
        //转实体
        UserAuthImport userAuthImport;
        try {
            // 读取数据并转换成实体对象
            userAuthImport = this.objectMapper.readValue(data, UserAuthImport.class);
        } catch (IOException e) {
            throw new CommonException(BaseConstants.ErrorCode.DATA_INVALID);
        }

        //参数校验并获取权限数据
        List<AuthDataVo> authDataVos = this.validAndGetAuthData(userAuthImport);
        if (CollectionUtils.isEmpty(authDataVos)) {
            LOGGER.warn("Auth Data Is Empty: {} - {} - {} - {} - {}", userAuthImport.getAuthorityTypeCode(),
                    userAuthImport.getTenantNum(), userAuthImport.getUserName(), userAuthImport.getDataCode(),
                    userAuthImport.getDataName());
            return Boolean.TRUE;
        }

        // 处理权限数据
        authDataVos.stream().collect(Collectors.groupingBy(AuthDataVo::getAuthorityTypeCode))
                .forEach((authorityTypeCode, innerAuthDataVos) -> {
                    //授权头插入
                    UserAuthority userAuthority = this.processUserAuthority(authorityTypeCode, userAuthImport);
                    //授权行插入
                    this.processUserAuthorityLine(userAuthority, innerAuthDataVos);
                    // 处理权限缓存
                    this.userAuthorityRepository.processUserAuthorityCache(userAuthImport.getTenantId(),
                            userAuthImport.getUserId(), authorityTypeCode);
                });

        return Boolean.TRUE;
    }

    /**
     * 校验参数并获取权限数据
     *
     * @param userAuthImport 用户权限导入对象
     * @return 权限数据
     */
    public List<AuthDataVo> validAndGetAuthData(UserAuthImport userAuthImport) {
        // 校验用户信息是否存在
        User queryUser = new User();
        queryUser.setLoginName(userAuthImport.getUserName());
        queryUser.setRealName(userAuthImport.getRealName());
        queryUser = this.userRepository.selectOne(queryUser);
        if (Objects.isNull(queryUser)) {
            throw new CommonException("user.import.user_login_name_not_exsit");
        }
        // 设置UserId
        userAuthImport.setUserId(queryUser.getId());

        // 校验租户信息是否存在
        Tenant tenant = new Tenant();
        tenant.setTenantNum(userAuthImport.getTenantNum());
        tenant = this.tenantRepository.selectOne(tenant);
        if (Objects.isNull(tenant)) {
            throw new CommonException("user.import.tenant_num_not_exsit");
        }
        // 设置租户ID
        userAuthImport.setTenantId(tenant.getTenantId());

        // 获取权限数据
        return Optional.ofNullable(this.authDataManager.findAuthData(userAuthImport.getAuthorityTypeCode(),
                this.userAuthImportAuthDataVoConverter.convert(userAuthImport)))
                .orElse(Collections.emptyList()).stream()
                // 过滤为空的数据
                .filter(Objects::nonNull)
                // 返回的参数校验
                .peek(AuthDataVo::validate)
                .collect(Collectors.toList());
    }

    /**
     * 处理用户权限头
     *
     * @param authorityTypeCode 权限类型码
     * @param userAuthImport    用户权限导入数据对象
     * @return 数据权限头对象
     */
    private UserAuthority processUserAuthority(String authorityTypeCode, UserAuthImport userAuthImport) {
        //授权头
        UserAuthority userAuthority = new UserAuthority();

        //检查头是否存在，存在直接插入到行里面去
        userAuthority.setAuthorityTypeCode(authorityTypeCode);
        userAuthority.setTenantId(userAuthImport.getTenantId());
        userAuthority.setUserId(userAuthImport.getUserId());
        userAuthority = this.userAuthorityRepository.selectOne(userAuthority);
        if (Objects.isNull(userAuthority)) {
            userAuthority = new UserAuthority();
            BeanUtils.copyProperties(userAuthImport, userAuthority);
            userAuthority.setAuthorityTypeCode(authorityTypeCode);

            this.userAuthorityRepository.insertSelective(userAuthority);
        } else {
            // 是否需要执行更新
            boolean needUpdate = false;

            // 处理是否包括全部的逻辑
            Integer includeAllFlag = userAuthImport.getIncludeAllFlag();
            includeAllFlag = Objects.isNull(includeAllFlag) ? BaseConstants.Flag.NO : includeAllFlag;
            if (!Objects.equals(userAuthority.getIncludeAllFlag(), includeAllFlag)) {
                userAuthority.setIncludeAllFlag(includeAllFlag);
                needUpdate = true;
            }

            // 处理数据来源
            if (userAuthority.equalSecGrpDataSource()) {
                userAuthority.setDataSource(Constants.SecGrpAssign.DEFAULT_SEC_GRP_DATA_SOURCE);
                needUpdate = true;
            }

            // 判断是否更新
            if (needUpdate) {
                this.userAuthorityRepository.updateByPrimaryKeySelective(userAuthority);
            }
        }

        return userAuthority;
    }

    /**
     * 处理用户数据权限行
     *
     * @param userAuthority 用户数据权限头
     * @param authDataVos   权限数据
     */
    private void processUserAuthorityLine(UserAuthority userAuthority, List<AuthDataVo> authDataVos) {
        UserAuthorityLine originUserAuthorityLine = new UserAuthorityLine();
        originUserAuthorityLine.setTenantId(userAuthority.getTenantId());
        originUserAuthorityLine.setAuthorityId(userAuthority.getAuthorityId());

        // 处理数据
        ListUtils.partition(authDataVos, 1000).forEach(subDataVos -> {
            // 数据ID
            List<Long> dataIds = subDataVos.stream().map(AuthDataVo::getDataId).collect(Collectors.toList());
            // 查询数据 key ---> value === dataId ---> UserAuthorityLine
            Map<Long, UserAuthorityLine> userAuthorityLineMap = Optional.ofNullable(this.userAuthorityLineRepository
                    .selectByCondition(Condition.builder(UserAuthorityLine.class)
                            .where(Sqls.custom()
                                    .andEqualTo(UserAuthorityLine.FIELD_AUTHORITY_ID, userAuthority.getAuthorityId())
                                    .andIn(UserAuthorityLine.FIELD_DATA_ID, dataIds)
                            ).build())).orElse(Collections.emptyList())
                    .stream().collect(Collectors.toMap(UserAuthorityLine::getDataId, t -> t));

            // 处理权限数据
            subDataVos.forEach(authDataVo -> {
                // 数据库中的数据
                UserAuthorityLine dbUserAuthorityLine = userAuthorityLineMap.get(authDataVo.getDataId());
                if (Objects.isNull(dbUserAuthorityLine)) {
                    // 插入新数据
                    UserAuthorityLine userAuthorityLine = originUserAuthorityLine.clone();
                    BeanUtils.copyProperties(authDataVo, userAuthorityLine);
                    this.userAuthorityLineRepository.insertSelective(userAuthorityLine);
                } else if (dbUserAuthorityLine.equalSecGrpDataSource()) {
                    // 数据来源为安全组，就更新为  默认安全组
                    dbUserAuthorityLine.setDataSource(Constants.SecGrpAssign.DEFAULT_SEC_GRP_DATA_SOURCE);
                    this.userAuthorityLineRepository.updateByPrimaryKeySelective(dbUserAuthorityLine);
                } else {
                    LOGGER.warn("User Authority Line Is Exists: {}", dbUserAuthorityLine);
                }
            });
        });
    }

    /**
     * 用户权限导入数据转权限数据工厂接口
     */
    public interface UserAuthImportAuthDataVoConverter extends AuthDataVoConverter<UserAuthImport> {
    }

    @Configuration
    public static class UserAuthImportConfiguration {
        @Bean
        @ConditionalOnMissingBean(UserAuthImportAuthDataVoConverter.class)
        public UserAuthImportAuthDataVoConverter userAuthImportAuthDataVoConverter() {
            return new DefaultUserAuthImportAuthDataVoConverter();
        }
    }

    /**
     * 用户权限导入数据转为权限数据参数对象默认实现
     */
    public static class DefaultUserAuthImportAuthDataVoConverter implements UserAuthImportAuthDataVoConverter {
        @Override
        public AuthDataCondition convert(UserAuthImport paramObject) {
            return AuthDataCondition.of(paramObject.getTenantId(), paramObject.getDataCode(),
                    paramObject.getDataName(), paramObject.getExtendsParam(), paramObject);
        }
    }
}
