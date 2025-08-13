package org.hzero.iam.infra.repository.impl;

import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Future;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import com.google.common.collect.Lists;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang.time.DateUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.modelmapper.internal.util.Assert;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import io.choerodon.core.domain.Page;
import io.choerodon.core.exception.CommonException;
import io.choerodon.core.oauth.CustomUserDetails;
import io.choerodon.core.oauth.DetailsHelper;
import io.choerodon.mybatis.pagehelper.PageHelper;
import io.choerodon.mybatis.pagehelper.domain.PageRequest;

import org.hzero.boot.message.entity.Receiver;
import org.hzero.boot.oauth.domain.repository.BaseUserRepository;
import org.hzero.boot.platform.common.CommonClient;
import org.hzero.boot.platform.lov.annotation.ProcessLovValue;
import org.hzero.common.HZeroCacheKey;
import org.hzero.common.HZeroConstant;
import org.hzero.common.HZeroService;
import org.hzero.core.base.BaseConstants;
import org.hzero.core.redis.RedisHelper;
import org.hzero.core.redis.safe.SafeRedisHelper;
import org.hzero.core.user.UserType;
import org.hzero.core.util.AsyncTask;
import org.hzero.core.util.CommonExecutor;
import org.hzero.export.vo.ExportParam;
import org.hzero.iam.api.dto.*;
import org.hzero.iam.domain.entity.*;
import org.hzero.iam.domain.repository.RoleRepository;
import org.hzero.iam.domain.repository.UserRepository;
import org.hzero.iam.domain.vo.CompanyVO;
import org.hzero.iam.domain.vo.RoleVO;
import org.hzero.iam.domain.vo.UserCacheVO;
import org.hzero.iam.domain.vo.UserVO;
import org.hzero.iam.infra.common.utils.UserUtils;
import org.hzero.iam.infra.constant.Constants;
import org.hzero.iam.infra.mapper.UserConfigMapper;
import org.hzero.iam.infra.mapper.UserInfoMapper;
import org.hzero.iam.infra.mapper.UserMapper;
import org.hzero.mybatis.base.impl.BaseRepositoryImpl;
import org.hzero.mybatis.common.Criteria;
import org.hzero.mybatis.domian.Condition;
import org.hzero.mybatis.helper.SecurityTokenHelper;
import org.hzero.mybatis.util.Sqls;

/**
 * @author bojiangzhou 优化代码
 * @author allen 2018/6/26
 */
@Component
public class UserRepositoryImpl extends BaseRepositoryImpl<User> implements UserRepository {

    private static final Logger LOGGER = LoggerFactory.getLogger(UserRepositoryImpl.class);


    @Autowired
    private UserMapper userMapper;
    @Autowired
    private UserInfoMapper userInfoMapper;
    @Autowired
    private UserConfigMapper userConfigMapper;
    @Autowired
    private RedisHelper redisHelper;
    @Autowired
    private BaseUserRepository baseUserRepository;
    @Autowired
    private RoleRepository roleRepository;
    @Autowired
    @Qualifier("IamCommonAsyncTaskExecutor")
    private ThreadPoolExecutor commonExecutor;

    @Autowired(required = false)
    private List<UserSelfWrapper> userSelfWrappers;
    @Autowired
    private CommonClient commonClient;

    /**
     * 最近可访问租户列表最大数量
     */
    @Value("${hzero.recent-access-tenant.max-count:20}")
    private int maxRecentAccessTenantCount;
    /**
     * 多少天内的访问租户
     */
    @Value("${hzero.recent-access-tenant.days:7}")
    private int daysRecentAccessTenant;

    @Override
    @ProcessLovValue
    public Page<UserVO> selectSimpleUsers(UserVO params, PageRequest pageRequest) {
        return PageHelper.doPageAndSort(pageRequest, () -> userMapper.selectSimpleUsers(params));
    }

    @Override
    @ProcessLovValue
    public Page<UserVO> selectAllocateUsers(UserVO params, PageRequest pageRequest) {
        CustomUserDetails self = UserUtils.getUserDetails();
        params.setTenantId(self.getTenantId());
        // 为了避免和 CustomUserDetails 中的 roleId 冲突
        params.setAllocateRoleId(params.getRoleId());
        params.setRoleId(null);
        return PageHelper.doPageAndSort(pageRequest, () -> userMapper.selectAllocateUsers(params));
    }

    @Override
    @ProcessLovValue
    public UserVO selectUserDetails(UserVO params) {
        Assert.notNull(params.getId(), "selectUserDetails: user id must not be null.");
        List<UserVO> users = userMapper.selectUserDetails(params);
        return CollectionUtils.isNotEmpty(users) ? users.get(0) : null;
    }

    @Override
    public UserVO selectSelf() {
        CustomUserDetails self = UserUtils.getUserDetails();

        UserVO userVO = selectSelfUser(self);

        // 判断是否提醒修改密码
        boolean changePasswordFlag = false;
        Integer passwordUpdateRate = Optional.ofNullable(userVO.getPasswordUpdateRate()).orElse(0);
        Integer passwordReminderPeriod = Optional.ofNullable(userVO.getPasswordReminderPeriod()).orElse(0);
        if (passwordUpdateRate > 0) {
            // 得到提醒日期
            Date date = DateUtils.addDays(userVO.getLastPasswordUpdatedAt(), passwordUpdateRate - passwordReminderPeriod);
            changePasswordFlag = new Date().after(date);
        }
        // 比较当前时间是否在提醒时间之后
        userVO.setChangePasswordFlag(changePasswordFlag ? BaseConstants.Flag.YES : BaseConstants.Flag.NO);

        // 设置当前切换的租户
        userVO.setTenantId(self.getTenantId());
        // 设置当前切换的角色
        userVO.setCurrentRoleId(self.getRoleId());
        // 合并标识
        userVO.setRoleMergeFlag(self.isRoleMergeFlag() ? BaseConstants.Flag.YES : BaseConstants.Flag.NO);
        // 当前角色名称
        if (self.isRoleMergeFlag()) {
            userVO.setCurrentRoleName(RoleVO.obtainRoleName(userVO.getCurrentRoleLevel(), userVO.getCurrentRoleName(), self.getLanguage()));
        }

        try {
            redisHelper.setCurrentDatabase(HZeroService.Platform.REDIS_DB);

            // 设置 title/logo
            setupTitleLogo(userVO, self);

            // 获取菜单布局样式，优先获取用户级
            setupMenuLayout(userVO, self);

            setupLayoutTheme(userVO, self);

            setupFavicon(userVO, self);

            setupWaterMark(userVO, self);
            userVO.setDataHierarchyFlag(redisHelper.hasKey(HZeroService.Platform.CODE + ":data-hierarchy:" + userVO.getTenantId())
                    ? BaseConstants.Flag.YES : BaseConstants.Flag.NO);
        } finally {
            redisHelper.clearCurrentDatabase();
        }

        userVO.setCurrentRoleLabels(self.getRoleLabels());

        if (CollectionUtils.isNotEmpty(userSelfWrappers)) {
            for (UserSelfWrapper userSelfWrapper : userSelfWrappers) {
                userSelfWrapper.wrap(userVO);
            }
        }

        userVO.setAdditionInfo(self.getAdditionInfo());
        return userVO;
    }

    private UserVO selectSelfUser(CustomUserDetails self) {
        UserVO params = new UserVO();
        params.setId(self.getUserId());
        params.setCurrentRoleId(self.getRoleId());
        params.setTenantId(self.getTenantId());
        params.setTimeRecentAccessTenant(LocalDateTime.now().plusDays(-1 * daysRecentAccessTenant));
        params.setRecentAccessTenantList(maxSize(params.getRecentAccessTenantList(), maxRecentAccessTenantCount));

        // 查询当前用户
        CompletableFuture<UserVO> f1 = CompletableFuture.supplyAsync(() -> {
            SecurityTokenHelper.close();
            DetailsHelper.setCustomUserDetails(self);
            UserVO userVO = userMapper.selectSelf(params);
            SecurityTokenHelper.clear();
            return userVO;
        }, commonExecutor);

        // 查询用户最近访问的租户
        CompletableFuture<List<Tenant>> f2 = CompletableFuture.supplyAsync(() -> {
            SecurityTokenHelper.close();
            DetailsHelper.setCustomUserDetails(self);
            List<Tenant> recentTenants = userMapper.selectTenantAccess(params);
            SecurityTokenHelper.clear();
            return recentTenants;
        }, commonExecutor);


        CompletableFuture<UserVO> cf = f1
                .thenCombine(f2, (userVO, tenants) -> {
                    if (userVO != null) {
                        userVO.setRecentAccessTenantList(tenants);
                    }
                    return userVO;
                })
                .exceptionally((e) -> {
                    LOGGER.warn("select self user error", e);
                    return null;
                });

        UserVO userVO = cf.join();

        if (userVO == null) {
            LOGGER.warn("User self not found. params is {}", params);
            // 无权访问系统，可能是用户不存在、被禁用、角色被禁用等
            throw new CommonException("hiam.warn.user.selfError");
        }
        // 设置用户语言信息
        if (StringUtils.isBlank(userVO.getLanguage())) {
            // 用户级语言信息不存在，查询租户级语言信息设置进去
            String defaultLanguage = getTenantDefaultLang(userVO.getOrganizationId());
            defaultLanguage = StringUtils.isBlank(defaultLanguage) ? self.getLanguage() : defaultLanguage;
            UserVO nameVO = userMapper.getLanguageNameByLanguage(defaultLanguage, userVO.getId());
            userVO.setLanguage(defaultLanguage);
            userVO.setLanguageName(nameVO.getLanguageName());
            userVO.setCountryName(nameVO.getCountryName());
            userVO.setRegionName(nameVO.getRegionName());
        }
        return userVO;
    }

    private List<Tenant> maxSize(List<Tenant> recentAccessTenantList, int maxRecentAccessTenantCount) {
        if (!CollectionUtils.isEmpty(recentAccessTenantList) && recentAccessTenantList.size() > maxRecentAccessTenantCount) {
            return recentAccessTenantList.subList(0, maxRecentAccessTenantCount);
        }
        return recentAccessTenantList;
    }

    private void setupTitleLogo(UserVO userVO, CustomUserDetails self) {
        String title = commonClient.getSystemConfigByConfigCode(HZeroConstant.Config.CONFIG_CODE_TITLE, self.getTenantId());
        userVO.setTitle(title);
        String logo = commonClient.getSystemConfigByConfigCode(HZeroConstant.Config.CONFIG_CODE_LOGO, self.getTenantId());
        userVO.setLogo(logo);
    }

    private void setupMenuLayout(UserVO userVO, CustomUserDetails self) {
        UserConfig userConfig = new UserConfig();
        userConfig.setUserId(self.getUserId());
        userConfig.setTenantId(self.getTenantId());
        UserConfig dbUserConfig = userConfigMapper.selectOne(userConfig);
        if (dbUserConfig != null && dbUserConfig.getMenuLayout() != null) {
            userVO.setMenuLayout(dbUserConfig.getMenuLayout());
        } else {
            String layout = commonClient.getSystemConfigByConfigCode(Constants.Config.CONFIG_CODE_MENU_LAYOUT, self.getTenantId());
            userVO.setMenuLayout(layout);
        }
    }

    private void setupLayoutTheme(UserVO userVO, CustomUserDetails self) {
        String layoutTheme = commonClient.getSystemConfigByConfigCode(Constants.Config.CONFIG_CODE_MENU_LAYOUT_THEME, self.getTenantId());
        userVO.setMenuLayoutTheme(layoutTheme);
    }

    private void setupFavicon(UserVO userVO, CustomUserDetails self) {
        String favicon = commonClient.getSystemConfigByConfigCode(Constants.Config.CONFIG_CODE_FAVICON, self.getTenantId());
        userVO.setFavicon(favicon);
    }

    private void setupWaterMark(UserVO userVO, CustomUserDetails self) {
        String waterMark = commonClient.getSystemConfigByConfigCode(Constants.Config.CONFIG_CODE_WATERMARK, self.getTenantId());
        Integer waterMarkFlag;
        try {
            waterMarkFlag = StringUtils.isBlank(waterMark) ? BaseConstants.Flag.NO : Integer.parseInt(waterMark);
        } catch (Exception e) {
            LOGGER.error("Failed to obtain watermark, waterMark is : {}, exception is : {}", waterMark, e.getMessage());
            // 获取水印失败时设置水印标识为0，表示不开启水印
            waterMarkFlag = BaseConstants.Flag.NO;
        }
        userVO.setWaterMarkFlag(waterMarkFlag);
    }

    @Override
    public UserVO selectSelfDetails() {
        CustomUserDetails self = UserUtils.getUserDetails();
        UserVO param = new UserVO();
        param.setId(self.getUserId());
        // 限制所属租户
        param.setOrganizationId(self.getOrganizationId());
        // 查询当前租户
        param.setTenantId(self.getTenantId());

        return userMapper.selectSelfDetails(param);
    }

    @Override
    public User selectSimpleUserById(Long userId) {
        return selectSimpleUserByIdAndTenantId(userId, null);
    }

    @Override
    public User selectUserPassword(Long userId) {
        User params = new User();
        params.setId(userId);

        return selectOneOptional(params, new Criteria()
                .select(
                        User.FIELD_ID,
                        User.FIELD_PASSWORD,
                        User.FIELD_OBJECT_VERSION_NUMBER
                )
                .where(User.FIELD_ID)
        );
    }

    @Override
    public User selectSimpleUserByIdAndTenantId(Long userId, Long tenantId) {
        User params = new User();
        params.setId(userId);
        params.setOrganizationId(tenantId);

        Object[] whereField = tenantId != null ?
                ArrayUtils.toArray(User.FIELD_ID, User.FIELD_ORGANIZATION_ID) : ArrayUtils.toArray(User.FIELD_ID);

        return selectOneOptional(params, new Criteria()
                .select(
                        User.FIELD_ID,
                        User.FIELD_LOGIN_NAME,
                        User.FIELD_REAL_NAME,
                        User.FIELD_ORGANIZATION_ID,
                        User.FIELD_PHONE,
                        User.FIELD_EMAIL,
                        User.FIELD_ADMIN,
                        User.FIELD_USER_TYPE,
                        User.FIELD_LANGUAGE,
                        User.FIELD_OBJECT_VERSION_NUMBER
                )
                .where(whereField)
        );
    }

    @Override
    @ProcessLovValue
    public Page<UserVO> selectRoleUsers(Long roleId, MemberRoleSearchDTO memberRoleSearchDTO, PageRequest pageRequest) {
        CustomUserDetails self = DetailsHelper.getUserDetails();

        memberRoleSearchDTO.setRoleId(roleId);
        Page<UserVO> page = PageHelper.doPage(pageRequest, () -> userMapper.selectRoleUsers(memberRoleSearchDTO));
        List<Long> userIds = page.stream().map(UserVO::getId).collect(Collectors.toList());
        // 可管理的角色ID
        Future<List<Long>> f1 = commonExecutor.submit(() -> roleRepository.selectAllManageableRoleIds(null, self));
        // 查询已分配的角色
        Future<List<MemberRole>> f2 = commonExecutor.submit(() -> roleRepository.selectCantRemoveAssignedRoles(userIds, self));

        List<Long> allManageRoleIds;
        List<MemberRole> cantRemoveRoles;
        try {
            allManageRoleIds = f1.get();
            cantRemoveRoles = f2.get();
        } catch (Exception e) {
            LOGGER.warn("Query executor error", e);
            throw new CommonException(BaseConstants.ErrorCode.ERROR);
        }

        for (UserVO userVO : page) {
            RoleVO role = new RoleVO().setId(roleId);
            Role.setupRoleControlFlag(Lists.newArrayList(role), allManageRoleIds, cantRemoveRoles, userVO.getId(), self);
            userVO.setRemovableFlag(role.getRemovableFlag());
            userVO.setTipMessage(role.getTipMessage());
        }

        return page;
    }

    @Override
    public int updateUserInfoByPrimaryKey(UserInfo userInfo) {
        return userInfoMapper.updateByPrimaryKey(userInfo);
    }

    @Override
    public int updateUserConfigByPrimaryKey(UserConfig userConfig) {
        return userConfigMapper.updateByPrimaryKey(userConfig);
    }

    @Override
    public UserInfo selectUserInfoByPrimaryKey(Long userId) {
        return userInfoMapper.selectByPrimaryKey(userId);
    }

    @Override
    public int insertUserInfoSelective(UserInfo userInfo) {
        return userInfoMapper.insertSelective(userInfo);
    }

    @Override
    public int insertUserConfigSelective(UserConfig userConfig) {
        return userConfigMapper.insertSelective(userConfig);
    }

    @Override
    public boolean existsUser(String loginName, String phone, String email, String userType) {
        return existsByLoginName(loginName) || existsByPhone(phone, userType) || existsByEmail(email, userType);
    }

    @Override
    public boolean existsByLoginName(String loginName) {
        return baseUserRepository.existsByLoginName(loginName);
    }

    @Override
    public boolean existsByPhone(String phone, String userType) {
        return baseUserRepository.existsByPhone(phone, UserType.ofDefault(userType));
    }

    @Override
    public boolean existsByEmail(String email, String userType) {
        return baseUserRepository.existsByEmail(email, UserType.ofDefault(userType));
    }


    @Override
    @ProcessLovValue
    public UserVO selectByLoginNameOrEmailOrPhone(UserVO params) {
        params.setUserType(UserType.ofDefault(params.getUserType()).value());
        return userMapper.selectByLoginNameOrEmailOrPhone(params);
    }

    @Override
    public List<UserVO> selectByRealNameOrEmail(UserVO params) {
        params.setUserType(UserType.ofDefault(params.getUserType()).value());
        return userMapper.selectByRealName(params);
    }

    @Override
    @ProcessLovValue(targetField = {"", UserExportDTO.FIELD_USER_AUTHORITY_LIST})
    public List<UserExportDTO> exportUserInfo(UserVO params, String authorityTypeQueryParams, PageRequest pageRequest, ExportParam exportParam) {
        List<UserExportDTO> results;

        if (exportParam.getSelection().contains(UserExportDTO.FIELD_ROLE_LIST)) {
            params.setSelectRole(true);
        }
        if (StringUtils.isNotBlank(authorityTypeQueryParams)) {
            params.setAuthorityTypeQueryParams(Arrays.asList(authorityTypeQueryParams.split(",")));
            params.setSelectAuthority(true);
        }

        if (pageRequest == null) {
            results = userMapper.selectExportUsers(params);
        } else {
            results = PageHelper.doPage(pageRequest, () -> userMapper.selectExportUsers(params));
        }

        return results;
    }

    @Override
    public CompanyVO countCompanyByName(String companyName) {
        return userMapper.countCompanyByName(companyName);
    }

    @Override
    public Page<UserVO> selectMultiTenantUsers(UserVO params, PageRequest pageRequest) {
        return PageHelper.doPage(pageRequest, () -> userMapper.selectMultiTenantUsers(params));
    }

    @Override
    public UserVO selectCompanyName(Long userId) {
        return userMapper.selectCompanyName(userId);
    }

    @Override
    public List<User> listRecentUser(Long tenantId, Date after) {
        return userMapper.selectByCondition(Condition.builder(User.class)
                .notSelect(User.FIELD_PASSWORD, User.FIELD_OBJECT_VERSION_NUMBER)
                .andWhere(Sqls.custom().andEqualTo(User.FIELD_ORGANIZATION_ID, tenantId, true)
                        .andGreaterThan(User.FIELD_LAST_UPDATE_DATE, after))
                .build());
    }

    @Override
    public User selectSimpleUserWithTenant(Long id) {
        User params = new User();
        params.setId(id);
        return userMapper.selectUserTenant(params).stream().findFirst().orElse(null);
    }

    @Override
    public List<User> selectSimpleUsersWithTenant(User params) {
        return userMapper.selectUserTenant(params);
    }

    @Override
    public int countTenantUser(Long organizationId) {
        return selectCountByCondition(Condition.builder(User.class)
                .andWhere(
                        Sqls.custom().andEqualTo(User.FIELD_ORGANIZATION_ID, organizationId)
                ).build());
    }

    @Override
    public void initUsers() {
        PageRequest pageRequest = new PageRequest(0, 600);
        Page<User> pageData = pageSimple(pageRequest);
        batchCacheUser(pageData.getContent());

        pageData.getContent().clear();

        List<AsyncTask<Integer>> tasks = IntStream.rangeClosed(1, pageData.getTotalPages()).mapToObj(page -> (AsyncTask<Integer>) () -> {
            PageRequest pr = new PageRequest(page, 600);
            Page<User> data = pageSimple(pr);
            batchCacheUser(data.getContent());

            data.getContent().clear();
            return data.getNumberOfElements();
        }).collect(Collectors.toList());

        CommonExecutor.batchExecuteAsync(tasks, commonExecutor, "BatchCacheUser");

        LOGGER.info("Finish cache user: cache size: [{}]", pageData.getTotalElements());
    }

    public Page<User> pageSimple(PageRequest pageRequest) {
        return PageHelper.doPage(pageRequest, () -> userMapper.selectCacheUseInfo(new User()));
    }

    @Override
    public Set<String> matchLoginName(Set<String> nameSet) {
        return userMapper.matchLoginName(nameSet);
    }

    @Override
    public Set<String> matchEmail(Set<String> emailSet, String userType) {
        return userMapper.matchEmail(emailSet, userType);
    }

    @Override
    public Set<String> matchPhone(Set<String> phoneSet, String userType) {
        return userMapper.matchPhone(phoneSet, userType);
    }

    @Override
    public User selectByLoginName(String loginName) {
        User param = new User();
        param.setLoginName(loginName);
        return selectOne(param);
    }

    @Override
    public Set<Long> getIdsByMatchLoginName(Set<String> nameSet) {
        return userMapper.getIdsByMatchLoginName(nameSet);
    }

    @Override
    public void disableByIdList(Set<Long> ids) {
        userMapper.disableByIdList(ids);
    }

    @Override
    public void batchCacheUser(List<User> users) {
        if (CollectionUtils.isEmpty(users)) {
            return;
        }

        Map<String, String> map = users.stream()
                .peek(user -> {
                    if (StringUtils.isBlank(user.getLanguage())) {
                        // 设置租户级默认语言信息
                        user.setLanguage(getTenantDefaultLang(user.getOrganizationId()));
                    }
                })
                .map(UserCacheVO::new)
                .collect(Collectors.toMap(u -> u.getId().toString(), u -> redisHelper.toJson(u)));
        SafeRedisHelper.execute(HZeroService.Iam.REDIS_DB, () -> {
            redisHelper.hshPutAll(HZeroCacheKey.USER, map);
        });
    }

    @Override
    public void cacheUser(Long userId) {
        User user = new User();
        user.setId(userId);
        user = userMapper.selectCacheUseInfo(user).stream().findFirst().orElse(null);

        if (user == null) {
            LOGGER.warn("Cache user not found. userId:{}", userId);
            return;
        }

        UserCacheVO cacheVO = new UserCacheVO(user);
        SafeRedisHelper.execute(HZeroService.Iam.REDIS_DB, () -> {
            redisHelper.hshPut(HZeroCacheKey.USER, userId.toString(), redisHelper.toJson(cacheVO));
        });
    }

    @Override
    public Page<UserEmployeeAssignDTO> pageUserEmployeeAssign(PageRequest pageRequest,
                                                              UserEmployeeAssignDTO userEmployeeAssignDTO) {
        return PageHelper.doPageAndSort(pageRequest,
                () -> userMapper.selectUserEmployeeAssignList(userEmployeeAssignDTO));
    }

    @Override
    public List<Receiver> listReceiverByUserIds(List<Long> userIds) {
        return userMapper.listReceiverByUserIds(userIds);
    }

    @Override
    public List<Receiver> listOpenReceiverByUserIds(List<Long> userIds, String thirdPlatformType) {
        return userMapper.listOpenReceiverByUserIds(userIds, thirdPlatformType);
    }

    @Override
    public List<UserSecCheckDTO> listSecondaryCheck(UserSecCheckSearchDTO searchDTO) {
        return this.userMapper.listSecondaryCheck(searchDTO);
    }

    /**
     * 获取租户级默认语言
     *
     * @param tenantId 租户Id
     * @return 租户级默认语言
     */
    private String getTenantDefaultLang(Long tenantId) {
        return commonClient.getSystemConfigByConfigCode(Constants.Config.TENANT_DEFAULT_LANGUAGE, tenantId);
    }
}
