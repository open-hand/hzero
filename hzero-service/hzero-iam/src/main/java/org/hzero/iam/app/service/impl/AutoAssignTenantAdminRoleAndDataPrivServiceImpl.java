package org.hzero.iam.app.service.impl;

import java.util.*;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;

import io.choerodon.core.exception.CommonException;

import org.hzero.common.HZeroConstant;
import org.hzero.core.base.BaseConstants;
import org.hzero.iam.api.dto.MemberRoleAssignDTO;
import org.hzero.iam.api.dto.TenantAdminRoleAndDataPrivAutoAssignmentDTO;
import org.hzero.iam.app.service.AutoAssignTenantAdminRoleAndDataPrivService;
import org.hzero.iam.app.service.MemberRoleService;
import org.hzero.iam.app.service.RoleService;
import org.hzero.iam.domain.entity.*;
import org.hzero.iam.domain.repository.*;
import org.hzero.iam.domain.vo.CompanyVO;
import org.hzero.iam.infra.constant.Constants;
import org.hzero.iam.infra.constant.HiamMemberType;
import org.hzero.iam.infra.mapper.UserAuthorityMapper;

/**
 * @author allen 2018/7/11
 */
@Service
public class AutoAssignTenantAdminRoleAndDataPrivServiceImpl
        implements AutoAssignTenantAdminRoleAndDataPrivService {

    private static final Logger LOGGER = LoggerFactory.getLogger(AutoAssignTenantAdminRoleAndDataPrivServiceImpl.class);

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private TenantRepository tenantRepository;
    @Autowired
    private RoleRepository roleRepository;
    @Autowired
    private RoleService roleService;
    @Autowired
    private MemberRoleService memberRoleService;
    @Autowired
    private UserConfigRepository userConfigRepository;
    @Autowired
    private UserAuthorityMapper userAuthorityMapper;
    @Autowired
    private UserAuthorityRepository userAuthorityRepository;
    @Autowired
    private UserAuthorityLineRepository userAuthorityLineRepository;

    /**
     * 自动分配角色及数据权限
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public void autoAssignTenantAdminRoleAndDataPriv(
            TenantAdminRoleAndDataPrivAutoAssignmentDTO tenantAdminRoleAndDataPrivAutoAssignmentDTO) {

        User user = userRepository.selectByPrimaryKey(tenantAdminRoleAndDataPrivAutoAssignmentDTO.getUserId());
        Tenant tenant = tenantRepository.selectByPrimaryKey(tenantAdminRoleAndDataPrivAutoAssignmentDTO.getTenantId());
        if (user == null || tenant == null) {
            throw new CommonException("error.role.tenantAutoAssignment.userOrTenant.invalid");
        }

        if (tenantAdminRoleAndDataPrivAutoAssignmentDTO.isNewTenantFlag() == null) {
            throw new CommonException("error.role.tenantAutoAssignment.newTenantFlag.invalid");
        }

        LOGGER.info("自动分配租户管理员角色及权限信息: 用户={}, 租户={}, 是否新租户={}", user.getLoginName(), tenant.getTenantName(),
                tenantAdminRoleAndDataPrivAutoAssignmentDTO.isNewTenantFlag());

        // 如果为新建租户: 分配管理员角色
        if (tenantAdminRoleAndDataPrivAutoAssignmentDTO.isNewTenantFlag()) {
            this.autoAssignRoleInternal(user, tenant, tenantAdminRoleAndDataPrivAutoAssignmentDTO);
        }

        /*
        分配管理员角色成功后, 取消分配游客角色
        @since 2018/11/16
        @author mingwei.liu@hand-china.com
         */
        this.autoUnassignGuestRoleInternal(user, tenant);

        // 分配数据权限
        this.autoAssignDataPrivInternal(user, tenant, tenantAdminRoleAndDataPrivAutoAssignmentDTO);
    }

    /**
     * 自动分配角色
     */
    private void autoAssignRoleInternal(User user, Tenant tenant,
                                        TenantAdminRoleAndDataPrivAutoAssignmentDTO tenantAdminRoleAndDataPrivAutoAssignmentDTO) {
        Assert.notNull(user, "user cannnot be null");
        Assert.notNull(tenantAdminRoleAndDataPrivAutoAssignmentDTO,
                "tenant admin role and data privilege auto assignment dto cannot be null");

        List<String> roleTplS = tenantAdminRoleAndDataPrivAutoAssignmentDTO.getRoleTemplateCodes();
        if (CollectionUtils.isEmpty(roleTplS)) {
            return;
        }
        LOGGER.info("============>管理员角色模板: 数量={}<============", roleTplS.size());
        roleTplS.forEach(item -> {
            LOGGER.info("模板={}", item);
        });

        //排除租户管理员模板： userId 对于code有影响，所以底层无法保证幂等
        roleTplS = roleTplS.stream()
                .filter(item ->
                        !Objects.equals(item, TenantAdminRoleAndDataPrivAutoAssignmentDTO.DEFAULT_TENANT_ADMIN_BASIC_TPL))
                .collect(Collectors.toList());

        // 获取平台管理员
        User sampleUser = new User();
        sampleUser.setId(BaseConstants.ANONYMOUS_USER_ID);
        User anonymousUser = userRepository.selectByPrimaryKey(sampleUser);
        if (anonymousUser == null) {
            throw new CommonException("hiam.warn.anonymousUserNotFound");
        }

        /**
         * 创建租户管理员角色
         * 1，若角色不存在，则创建角色
         * 2，将租户管理员角色分配给用户
         * 3，设置租户管理员为默认角色
         * @since 20190409
         * @author jianbo.li
         */
        Role userAdminRole = createAdminRoleAndAssignToUser(tenant, user, anonymousUser);

        // 创建角色并分配给租户管理员用户。
        // 创建角色，该角色的父级角色必须被当前账户所持有。
        //      这里所有的角色均继承自租户管理员角色，租户管理员角色必定被当前账户持有
        roleTplS.forEach(item -> {
            roleService.createRoleByRoleTpl(user, tenant, userAdminRole, item);
        });

    }

    private Role createAdminRoleAndAssignToUser(Tenant tenant, User user, User siteAdminUser) {

        //查租户管理员的父级角色
        Role parentRole = new Role();
        parentRole.setCode(HZeroConstant.RoleCode.TENANT);
        parentRole = roleRepository.selectOne(parentRole);

        // 创建租户管理员角色
        Role tenantAdminRole = roleService.createRoleByRoleTpl(siteAdminUser, tenant,
                parentRole,
                TenantAdminRoleAndDataPrivAutoAssignmentDTO.DEFAULT_TENANT_ADMIN_BASIC_TPL);

        //分配给用户
        memberRoleService.batchAssignMemberRoleOnTenant(Collections.singletonList(
                new MemberRoleAssignDTO(user.getId(), user.getLoginName(), tenantAdminRole.getId(), tenantAdminRole.getCode())
        ));

        // 设置默认角色
        Role queryAdminRoleTpl = new Role();
        queryAdminRoleTpl.setCode(
                TenantAdminRoleAndDataPrivAutoAssignmentDTO.DEFAULT_TENANT_ADMIN_BASIC_TPL);
        this.setUserDefaultRoleToTenantAdmin(user, roleRepository.selectOne(queryAdminRoleTpl), tenantAdminRole);
        return tenantAdminRole;
    }

    /**
     * 设置用户默认角色为租户管理员角色
     *
     * @param user            用户
     * @param adminRoleTpl    管理员角色模板
     * @param tenantAdminRole 管理员角色
     */
    private void setUserDefaultRoleToTenantAdmin(User user, Role adminRoleTpl, Role tenantAdminRole) {
        if (!TenantAdminRoleAndDataPrivAutoAssignmentDTO.DEFAULT_TENANT_ADMIN_BASIC_TPL.equals(adminRoleTpl.getCode())) {
            // 如果模板代码非租户管理员模板, 则跳过
            return;
        }

        // 默认角色ID
        Long userId = user.getId();
        Long tenantId = tenantAdminRole.getTenantId();
        Long defaultRoleId = tenantAdminRole.getId();


        /**
         * 此处会发生乐观锁问题, 怀疑其他地方更新了用户信息, 可能的改进方案, 重新查询用户
         *
         * 另外, iam_user中已经不存在defaultRoleID字段, 此处逻辑可删除, 因此, 目前直接删除
         * @since 2018/12/04
         * @author allen.liu
         */
        // 设置用户
        // user.setDefaultRoleId(defaultRoleId);
        // userRepository.updateOptional(user, User.FIELD_DEFAULT_ROLE_ID);

        // 设置用户配置
        UserConfig sampleUserConfig = new UserConfig();
        sampleUserConfig.setUserId(userId);
        sampleUserConfig.setTenantId(tenantId);
        UserConfig userConfig = userConfigRepository.selectOne(sampleUserConfig);

        // 新增或更新
        if (userConfig != null) {
            userConfig.setDefaultRoleId(defaultRoleId);
            userConfigRepository.updateOptional(userConfig, UserConfig.FIELD_DEFAULT_ROLE_ID);
        } else {
            sampleUserConfig.setDefaultRoleId(defaultRoleId);
            userConfigRepository.insertSelective(sampleUserConfig);
        }
    }

    /**
     * 获取租户管理员角色继承模板<br/>
     */
    protected List<Role> getAdminRoleInheritTemplateList(TenantAdminRoleAndDataPrivAutoAssignmentDTO tenantAdminRoleAndDataPrivAutoAssignmentDTO) {
        Assert.notNull(tenantAdminRoleAndDataPrivAutoAssignmentDTO,
                "tenant admin role and data privilege auto assignment dto cannot be null");

        // 自动包含租户管理员模板
        Set<String> adminRoleCodeInheritTemplateList = new HashSet<>();
        adminRoleCodeInheritTemplateList
                .add(TenantAdminRoleAndDataPrivAutoAssignmentDTO.DEFAULT_TENANT_ADMIN_BASIC_TPL);
        if (!CollectionUtils.isEmpty(tenantAdminRoleAndDataPrivAutoAssignmentDTO.getRoleTemplateCodes())) {
            adminRoleCodeInheritTemplateList.addAll(tenantAdminRoleAndDataPrivAutoAssignmentDTO.getRoleTemplateCodes());
        }

        // 获取模板角色实体
        List<Role> adminRoleInheritTemplateList = new ArrayList<>(adminRoleCodeInheritTemplateList.size());
        adminRoleCodeInheritTemplateList.forEach(item -> {
            Role sampleRole = new Role();
            sampleRole.setCode(item);
            Role resultRole = roleRepository.selectOne(sampleRole);
            if (resultRole != null) {
                adminRoleInheritTemplateList.add(resultRole);
            }
        });

        return adminRoleInheritTemplateList;
    }

    /**
     * @param user
     * @param tenant
     * @param tenantAdminRoleAndDataPrivAutoAssignmentDTO
     */
    private void autoAssignDataPrivInternal(User user, Tenant tenant,
                                            TenantAdminRoleAndDataPrivAutoAssignmentDTO tenantAdminRoleAndDataPrivAutoAssignmentDTO) {
        /**
         * 获取公司权限信息
         * 1: 通过companyNum ---> 查找companyId
         */
        if (StringUtils.isEmpty(tenantAdminRoleAndDataPrivAutoAssignmentDTO.getCompanyNum())) {
            return;
        }

        CompanyVO companyVO = userAuthorityMapper.selectCompanyInfo(tenantAdminRoleAndDataPrivAutoAssignmentDTO.getCompanyNum(), tenant.getTenantId());
        Assert.notNull(companyVO, "hiam.error.company_auth_failed");

        /**
         *
         * 分配公司权限信息
         * 往user_auth表里面插数据
         */

        //插入头
        UserAuthority userAuthority = new UserAuthority();
        userAuthority.setUserId(user.getId());
        userAuthority.setTenantId(tenant.getTenantId());
        userAuthority.setAuthorityTypeCode(Constants.AUTHORITY_TYPE_CODE.COMPANY);
        UserAuthority isExistsFlag = userAuthorityRepository.selectOne(userAuthority);
        if (isExistsFlag == null) {
            userAuthorityRepository.insertSelective(userAuthority);
        } else {
            userAuthority.setAuthorityId(isExistsFlag.getAuthorityId());
        }

        //插入行
        UserAuthorityLine userAuthorityLine = new UserAuthorityLine();
        userAuthorityLine.setTenantId(tenant.getTenantId());
        userAuthorityLine.setAuthorityId(userAuthority.getAuthorityId());
        userAuthorityLine.setDataId(companyVO.getCompanyId());
        userAuthorityLine.setDataCode(companyVO.getCompanyNum());
        userAuthorityLine.setDataName(companyVO.getCompanyName());
        userAuthorityLineRepository.insertSelective(userAuthorityLine);
    }

    /**
     * 分配租户角色成功后, 自动取消分配Guest角色, 平台层/租户层Guest角色
     *
     * @param user
     * @param tenant
     */
    private void autoUnassignGuestRoleInternal(User user, Tenant tenant) {
        try {
            List<MemberRole> memberRoleList = new ArrayList<>(10);

            String[] guestRoleCodeArray = new String[]{Constants.SITE_GUEST_ROLE_CODE, Constants.ORGANIZATION_GUEST_ROLE_CODE};
            for (String guestRoleCode : guestRoleCodeArray) {
                Role role = new Role();
                role.setCode(guestRoleCode);
                Role guestRole = roleRepository.selectOne(role);

                if (guestRole != null) {
                    MemberRole guestMemberRole = new MemberRole();
                    guestMemberRole.setMemberId(user.getId());
                    guestMemberRole.setMemberType(HiamMemberType.USER.value());
                    guestMemberRole.setRoleId(guestRole.getId());
                    memberRoleList.add(guestMemberRole);
                }
            }

            memberRoleService.batchDeleteMemberRoleInternal(tenant.getTenantId(), memberRoleList);
        } catch (Exception ex) {
            LOGGER.error("delete guest role with error: ", ex);
        }
    }
}
