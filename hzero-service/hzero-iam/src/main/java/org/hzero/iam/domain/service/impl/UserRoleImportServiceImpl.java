package org.hzero.iam.domain.service.impl;

import org.apache.commons.lang3.StringUtils;
import org.hzero.boot.platform.event.helper.impl.RequestTokenInterceptor;
import org.hzero.boot.platform.lov.adapter.LovAdapter;
import org.hzero.boot.platform.lov.constant.LovConstants;
import org.hzero.boot.platform.lov.dto.LovDTO;
import org.hzero.boot.platform.lov.dto.LovViewDTO;
import org.hzero.common.HZeroService;
import org.hzero.core.base.BaseConstants;
import org.hzero.core.redis.RedisHelper;
import org.hzero.iam.domain.entity.*;
import org.hzero.iam.domain.repository.MemberRoleRepository;
import org.hzero.iam.domain.repository.RoleRepository;
import org.hzero.iam.domain.repository.TenantRepository;
import org.hzero.iam.domain.repository.UserRepository;
import org.hzero.iam.domain.service.UserRoleImportService;
import org.hzero.iam.infra.common.utils.AssertUtils;
import org.hzero.iam.infra.constant.Constants;
import org.hzero.iam.infra.constant.HiamResourceLevel;
import org.hzero.iam.infra.mapper.UserAuthorityMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

import io.choerodon.core.exception.CommonException;

/**
 * 默认实现
 *
 * @author bojiangzhou 2019/05/13
 */
public class UserRoleImportServiceImpl implements UserRoleImportService {

    public static final String HTTP_PREFIX = "http://";

    @Autowired
    private RoleRepository roleRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private TenantRepository tenantRepository;
    @Autowired
    private MemberRoleRepository memberRoleRepository;
    @Autowired
    private UserAuthorityMapper userAuthorityMapper;
    @Autowired
    private LovAdapter lovAdapter;
    @Autowired
    private RestTemplate restTemplate;
    @Autowired
    private RedisHelper redisHelper;
    @Autowired
    private RequestTokenInterceptor requestTokenInterceptor;

    /**
     * 转义一些字段 1，用户名转memeberId 2，memberType固定为"user" 3, 角色路径转 roleId
     */
    @Override
    public void checkAndInitMemberRole(MemberRole memberRole) {
        // 成员类型
        memberRole.setMemberType("user");

        // 绑定用户id
        User queryUser = new User();
        queryUser.setLoginName(memberRole.getLoginName());
        queryUser.setRealName(memberRole.getRealName());
        User user = userRepository.selectOne(queryUser);
        if (user == null) {
            throw new CommonException("user.import.user_login_name_not_exsit");
        }
        memberRole.setMemberId(user.getId());

        // 绑定角色
        Role queryRole = new Role();
        queryRole.setLevelPath(memberRole.getLevelPath());
        /**
         * 兼容之前的版本
         */
        queryRole.setCode(memberRole.getRoleCode());
        Role role = roleRepository.selectOne(queryRole);
        if (role == null) {
            throw new CommonException("hiam.warn.user.import.roleCodeOrNameNotExist");
        }
        memberRole.setRoleId(role.getId());
        memberRole.setAssignLevelValue(role.getTenantId());
    }

    @Override
    public void assignRole(User user) {
        if (StringUtils.isBlank(user.getRoleLevelPath()) && StringUtils.isEmpty(user.getRoleCode())) {
            return;
        }

        List<MemberRole> memberRoles = new ArrayList<>(8);
        /**
         * 保留code 兼容以前的版本
         */
        boolean findRoleByCodeFlag = StringUtils.isEmpty(user.getRoleLevelPath());
        String whereIsRole = findRoleByCodeFlag ? user.getRoleCode() : user.getRoleLevelPath();
        Stream.of(StringUtils.split(whereIsRole, BaseConstants.Symbol.COMMA))
                .map(String::trim).forEach(where -> {
            Role queryRole = new Role();
            if (findRoleByCodeFlag) {
                queryRole.setCode(where);
            } else {
                queryRole.setLevelPath(where);
            }
            memberRoles.add(buildMemberRole(roleRepository.selectOne(queryRole)));
        });

        user.setMemberRoleList(memberRoles);
    }

    private MemberRole buildMemberRole(Role assignRole) {
        AssertUtils.notNull(assignRole, "hiam.warn.user.import.roleCodeOrNameNotExist");
        MemberRole memberRole = new MemberRole();
        memberRole.setRoleId(assignRole.getId());
        memberRole.setRoleCode(assignRole.getCode());
        memberRole.setRoleName(assignRole.getName());
        memberRole.setAssignLevel(HiamResourceLevel.ORGANIZATION.value());
        memberRole.setAssignLevelValue(assignRole.getTenantId());
        return memberRole;
    }

    /**
     * 检验唯一索引
     */
    @Override
    public boolean memberRoleNotExists(MemberRole memberRole) {
        MemberRole params = new MemberRole();
        params.setRoleId(memberRole.getRoleId());
        params.setMemberId(memberRole.getMemberId());
        params.setMemberType(memberRole.getMemberType());
        params.setSourceId(memberRole.getSourceId());
        params.setSourceType(memberRole.getSourceType());
        return memberRoleRepository.selectCount(params) == 0;
    }

    /**
     * 检验以及翻译一些参数
     * 1,userName转userId
     * 2,tenantNum转tenantId
     * 3,权限类型代码 + dataCode + dataName -> 查dataId
     */
    @Override
    public void checkAndInitUserAuthority(UserAuthImport userAuthImport) {
        //绑定用户
        User queryUser = new User();
        queryUser.setLoginName(userAuthImport.getUserName());
        queryUser.setRealName(userAuthImport.getRealName());
        queryUser = userRepository.selectOne(queryUser);
        if (queryUser == null) {
            throw new CommonException("user.import.user_login_name_not_exsit");
        }
        userAuthImport.setUserId(queryUser.getId());

        //绑定租户
        Tenant tenant = new Tenant();
        tenant.setTenantNum(userAuthImport.getTenantNum());
        tenant = tenantRepository.selectOne(tenant);
        if (tenant == null) {
            throw new CommonException("user.import.tenant_num_not_exsit");
        }
        userAuthImport.setTenantId(tenant.getTenantId());

        //授权类型
        switch (userAuthImport.getAuthorityTypeCode()) {
            case Constants.AUTHORITY_TYPE_CODE.COMPANY:
                userAuthImport.setDataId(userAuthorityMapper.queryComDataSourceInfo(userAuthImport.getTenantId(),
                        userAuthImport.getDataCode(),
                        userAuthImport.getDataName()));
                break;
            case Constants.AUTHORITY_TYPE_CODE.OU:
                userAuthImport.setDataId(userAuthorityMapper.queryOUDataSourceInfo(userAuthImport.getTenantId(),
                        userAuthImport.getDataCode(),
                        userAuthImport.getDataName()));
                break;
            case Constants.AUTHORITY_TYPE_CODE.INVORG:
                userAuthImport.setDataId(userAuthorityMapper.queryInvDataSourceInfo(userAuthImport.getTenantId(),
                        userAuthImport.getDataCode(),
                        userAuthImport.getDataName()));
                break;
            case Constants.AUTHORITY_TYPE_CODE.PURORG:
                userAuthImport.setDataId(userAuthorityMapper.queryPOrgDataSourceInfo(userAuthImport.getTenantId(),
                        userAuthImport.getDataCode(),
                        userAuthImport.getDataName()));
                break;
            case Constants.AUTHORITY_TYPE_CODE.PURAGENT:
                userAuthImport.setDataId(userAuthorityMapper.queryPAgentDataSourceInfo(userAuthImport.getTenantId(),
                        userAuthImport.getDataCode(),
                        userAuthImport.getDataName()));
                break;
            case Constants.AUTHORITY_TYPE_CODE.PURCAT:
                userAuthImport.setDataId(userAuthorityMapper.queryPCatDataSourceInfo(userAuthImport.getTenantId(),
                        userAuthImport.getDataCode(),
                        userAuthImport.getDataName()));
                break;
            default:
                // 通用值集类型导入
                restTemplate.getInterceptors().add(requestTokenInterceptor);

                // step1 : 找到code对应的值集视图Code
                String lovViewCode = userAuthorityMapper.selectDimensionLovCode(userAuthImport.getAuthorityTypeCode());
                Assert.notNull(lovViewCode, "hiam.error.auth_type_code_not_support");
                // step2: lovAdapter获取值集调用信息
                LovViewDTO lovViewDTO = lovAdapter.queryLovViewInfo(lovViewCode, null);
                List<LovViewDTO.QueryField> queryFiledList = lovViewDTO.getQueryFields();
                LovDTO lovDTO = lovAdapter.queryLovInfo(lovViewDTO.getLovCode(), null);
                // step3: restTemplate调用值集
                Map<String, String> queryParamMap = handleQueryParam(queryFiledList, userAuthImport.getQueryParamList());
                try {
                    redisHelper.setCurrentDatabase(HZeroService.Admin.REDIS_DB);
                    String serviceName = redisHelper.hshGet(Constants.ADMIN_ROUTE_INFO_CACHE_PREFIX, lovDTO.getRouteName());
                    List<Map> content = null;

                    // url 类型值集数据获取
                    if (LovConstants.LovTypes.URL.equals(lovDTO.getLovTypeCode())) {
                        String url = HTTP_PREFIX + serviceName + lovDTO.getCustomUrl();
                        String urlParam = convertMapToUrlParam(queryParamMap);
                        if (StringUtils.isNotEmpty(urlParam)) {
                            url = url + BaseConstants.Symbol.QUESTION + urlParam;
                        }
                        Map lovValueInfo = restTemplate.getForObject(url, Map.class);
                        content = (List<Map>) lovValueInfo.get("content");

                    }
                    // SQL 值集数据获取
                    else if (LovConstants.LovTypes.SQL.equals(lovDTO.getLovTypeCode())) {
                        queryParamMap.put("lovCode", lovDTO.getLovCode());
                        String suffixUrl = lovViewDTO.getQueryUrl().replaceFirst(BaseConstants.Symbol.SLASH + lovDTO.getRouteName(), StringUtils.EMPTY);
                        String url = HTTP_PREFIX + serviceName + suffixUrl;
                        String urlParam = convertMapToUrlParam(queryParamMap);
                        if (StringUtils.isNotEmpty(urlParam)) {
                            url = url + BaseConstants.Symbol.QUESTION + urlParam;
                        }
                        Map lovValueInfo = restTemplate.getForObject(url, Map.class);
                        content = (List<Map>) lovValueInfo.get("content");
                    }
                    // IDP 类型值集
                    else {
                        // 暂时不予支持
                        throw new CommonException("hiam.error.auth_type_code_not_support");
                    }

                    Assert.isTrue(!CollectionUtils.isEmpty(content), "hiam.error.not_found_data_id");
                    Map findRow = content.get(0);
                    Long dataId = Long.parseLong(findRow.get(lovViewDTO.getValueField()).toString());
                    String dataName = findRow.get(lovViewDTO.getDisplayField()).toString();
                    userAuthImport.setDataId(dataId);
                    userAuthImport.setDataName(dataName);
                    userAuthImport.setDataCode(null);

                } finally {
                    redisHelper.clearCurrentDatabase();
                }
        }
    }

    /**
     * 处理查询参数
     *
     * @param queryFiledList
     * @param queryParamList
     * @return
     */
    private Map<String, String> handleQueryParam(List<LovViewDTO.QueryField> queryFiledList, String queryParamList) {
        Map<String, String> paramMap = new HashMap<>(8);
        String[] params = queryParamList.split(BaseConstants.Symbol.SEMICOLON);
        for (String param : params) {
            String[] kv = param.split(BaseConstants.Symbol.COLON);
            if (kv.length > 1) {
                queryFiledList.stream()
                        .filter(item -> kv[0].equals(item.getLabel()))
                        .findFirst().ifPresent(queryField -> paramMap.put(queryField.getField(), kv[1]));
            }
        }

        return paramMap;

    }

    /**
     * map转url参数
     *
     * @param map
     * @return
     */
    private String convertMapToUrlParam(Map<String, String> map) {
        StringBuilder stringBuilder = new StringBuilder();
        for (Map.Entry<String, String> entry : map.entrySet()) {
            stringBuilder.append(entry.getKey()).append(BaseConstants.Symbol.EQUAL).append(entry.getValue())
                    .append(BaseConstants.Symbol.AND);
        }
        return stringBuilder.toString();
    }


}
