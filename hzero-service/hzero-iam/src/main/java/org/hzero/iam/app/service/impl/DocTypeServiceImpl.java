package org.hzero.iam.app.service.impl;

import java.util.*;
import java.util.stream.Collectors;

import org.apache.commons.lang.ObjectUtils;
import org.hzero.core.base.BaseConstants;
import org.hzero.core.message.MessageAccessor;
import org.hzero.core.util.Pair;
import org.hzero.core.util.ResponseUtils;
import org.hzero.iam.app.service.DocTypeAssignService;
import org.hzero.iam.app.service.DocTypeService;
import org.hzero.iam.config.IamProperties;
import org.hzero.iam.domain.entity.*;
import org.hzero.iam.domain.repository.*;
import org.hzero.iam.domain.service.AuthorityDomainService;
import org.hzero.iam.domain.vo.DataPermissionRangeVO;
import org.hzero.iam.domain.vo.DataPermissionRuleVO;
import org.hzero.iam.infra.constant.Constants;
import org.hzero.iam.infra.constant.DocTypeConstants;
import org.hzero.iam.infra.constant.DocTypeScript;
import org.hzero.iam.infra.feign.PermissionRuleFeignClient;
import org.hzero.mybatis.domian.Condition;
import org.hzero.mybatis.util.Sqls;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import com.fasterxml.jackson.core.type.TypeReference;

import io.choerodon.core.domain.Page;
import io.choerodon.core.exception.CommonException;
import io.choerodon.mybatis.domain.AuditDomain;
import io.choerodon.mybatis.pagehelper.PageHelper;
import io.choerodon.mybatis.pagehelper.domain.PageRequest;
import io.choerodon.mybatis.pagehelper.domain.Sort;

/**
 * 单据类型定义应用服务默认实现
 *
 * @author min.wang01@hand-china.com 2018-08-08 16:32:49
 */
@Service
public class DocTypeServiceImpl implements DocTypeService {
    private static final Logger logger = LoggerFactory.getLogger(DocTypeServiceImpl.class);
    private static final String DOC_TYPE_PERMISSION_MESSAGE = "doc.type.permission.description";
    private static final String DOC_TYPE_PERMISSION_CODE_RULE_PREFIX = "DT";
    private static final String DOC_TYPE_PERMISSION_REPLACE = "${authorityTypeCode}";
    private static final String DOC_TYPE_FIELD_REPLACE = "${fieldName}";
    private static final String DOC_TYPE_ID_REPLACE = "${docTypeId}";

    private final RoleRepository roleRepository;
    private final RoleAuthorityRepository roleAuthorityRepository;

    private final DocTypeRepository docTypeRepository;
    private final DocTypePermissionRepository docTypePermissionRepository;
    private final DocTypeSqlidRepository docTypeSqlidRepository;
    private final DocTypeAssignService docTypeAssignService;
    private final PermissionRuleFeignClient permissionRuleFeignClient;
    private final DocTypeDimensionRepository docTypeDimensionRepository;
    private final AuthorityDomainService authorityDomainService;
    private final IamProperties iamProperties;

    @Autowired
    public DocTypeServiceImpl(DocTypeRepository docTypeRepository, DocTypePermissionRepository docTypePermissionRepository,
            DocTypeSqlidRepository docTypeSqlidRepository, DocTypeAssignService docTypeAssignService,
            PermissionRuleFeignClient permissionRuleFeignClient, DocTypeDimensionRepository docTypeDimensionRepository,
            RoleRepository roleRepository, RoleAuthorityRepository roleAuthorityRepository, IamProperties iamProperties,
            AuthorityDomainService authorityDomainService) {
        this.docTypeRepository = docTypeRepository;
        this.docTypePermissionRepository = docTypePermissionRepository;
        this.docTypeSqlidRepository = docTypeSqlidRepository;
        this.docTypeAssignService = docTypeAssignService;
        this.permissionRuleFeignClient = permissionRuleFeignClient;
        this.docTypeDimensionRepository = docTypeDimensionRepository;
        this.roleRepository = roleRepository;
        this.roleAuthorityRepository = roleAuthorityRepository;
        this.iamProperties = iamProperties;
        this.authorityDomainService = authorityDomainService;
    }

    @Override
    public Page<DocType> pageDocType(Long tenantId, String docTypeCode, String docTypeName, PageRequest pageRequest) {
        Page<DocType> docTypeList = docTypeRepository.pageDocType(tenantId, docTypeCode, docTypeName, pageRequest);
        if (CollectionUtils.isEmpty(docTypeList)) {
            return docTypeList;
        }
        docTypeList.forEach(docType -> {
                    docType.setDocTypeAssigns(docTypeAssignService.listAssign(tenantId, docType.getDocTypeId()))
                            .setDocTypePermissions(docTypePermissionRepository.listPermission(tenantId, docType.getDocTypeId()));
                    // 查寻已分配的维度，供数据权限维度使用
                    docType.setDocTypeDimensionList(docTypeDimensionRepository.listDocTypeDimensionByDocTypeId(docType.getDocTypeId()));
                }
        );
        return docTypeList;
    }

    @Override
    public DocType queryDocType(Long tenantId, Long docTypeId, boolean includeAssign) {
        DocType docType = docTypeRepository.queryDocType(tenantId, docTypeId, includeAssign);
        if (docType != null && !CollectionUtils.isEmpty(docType.getDocTypePermissions())) {
            docType.setDocTypePermissions(docType.getDocTypePermissions().stream().distinct().collect(Collectors.toList()));
        }
        return moveSqlid(docType);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public DocType createDocType(DocType docType) {
        // 设置单据权限控制类型为所有人（ALL）类型
        moveSqlid(docType);
        docTypeRepository.insertSelective(docType);
        // 插入单据类型分配
        docTypeAssignService.createAssign(docType.getDocTypeId(), docType.getTenantId(), docType.getDocTypeAssigns());
        if (!CollectionUtils.isEmpty(docType.getDocTypeSqlidList())) {
            docType.getDocTypeSqlidList().forEach(docTypeSqlid ->
                    docTypeSqlidRepository.insertSelective(docTypeSqlid.setDocTypeId(docType.getDocTypeId()))
            );
        }
        generateShieldRule(docType.getTenantId(), Collections.singletonList(docType.getDocTypeId()));
        return docType;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public DocType updateDocType(DocType docType) {
        moveSqlid(docType);
        docTypeRepository.updateOptional(docType, DocType.getUpdatableField());
        // 若当前层级为平台级，删除租户分配信息
        if (ObjectUtils.equals(docType.getLevelCode(), DocTypeConstants.DocTypeLevelCode.GLOBAL)) {
            docType.setDocTypeAssigns(Collections.emptyList());
        }
        docTypeAssignService.updateAssign(docType.getDocTypeId(), docType.getTenantId(), docType.getDocTypeAssigns());
        if (!CollectionUtils.isEmpty(docType.getDocTypeSqlidList())) {
            docType.getDocTypeSqlidList().forEach(docTypeSqlid -> {
                if (docTypeSqlid.getDocTypeSqlidId() == null || AuditDomain.RecordStatus.create.equals(docTypeSqlid.get_status())) {
                    docTypeSqlidRepository.insertSelective(docTypeSqlid.setDocTypeId(docType.getDocTypeId()));
                } else if (AuditDomain.RecordStatus.update.equals(docTypeSqlid.get_status())) {
                    docTypeSqlidRepository.updateOptional(docTypeSqlid.setDocTypeId(docType.getDocTypeId()), DocTypeSqlid.FIELD_SQLID);
                } else if (AuditDomain.RecordStatus.delete.equals(docTypeSqlid.get_status())) {
                    docTypeSqlidRepository.deleteByPrimaryKey(docTypeSqlid);
                }
            });
        }
        generateShieldRule(docType.getTenantId(), Collections.singletonList(docType.getDocTypeId()));
        return docType;
    }

    private DocType moveSqlid(DocType docType) {
        if (docType != null && !StringUtils.isEmpty(docType.getSourceDataEntity())) {
            DocTypeSqlid docTypeSqlid = new DocTypeSqlid()
                    .setDocTypeId(docType.getDocTypeId())
                    .setSqlid(docType.getSourceDataEntity());
            docType.setSourceDataEntity(null);
            if (CollectionUtils.isEmpty(docType.getDocTypeSqlidList())) {
                docType.setDocTypeSqlidList(Collections.singletonList(docTypeSqlid));
            } else {
                docType.getDocTypeSqlidList().add(docTypeSqlid);
            }
        }
        return docType;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void generateShieldRule(Long tenantId, List<Long> docTypeIds) {
        if (CollectionUtils.isEmpty(docTypeIds)) {
            return;
        }
        List<Pair<DataPermissionRangeVO, DataPermissionRuleVO>> dataPermissionList = new ArrayList<>();
        Map<String, Long> authDimPermission = new HashMap<>(docTypeIds.size());
        // 获取 单据权限：租户分配/数据权限关联/单据维度
        List<DocType> docTypeList = docTypeRepository.queryDocTypeWithDimension(tenantId, docTypeIds);
        docTypeList.forEach(docType -> {
            moveSqlid(docType);
            // 生成权限规则和权限范围
            if (CollectionUtils.isEmpty(docType.getDocTypeAuthDims())) {
                return;
            }
            Set<Long> tenantIds = null;
            // 获取分配租户
            if (Constants.DocType.DOC_SITE_LEVEL.equals(docType.getLevelCode())) {
                tenantIds = Collections.singleton(BaseConstants.DEFAULT_TENANT_ID);
            } else if (!CollectionUtils.isEmpty(docType.getDocTypeAssigns())) {
                tenantIds = docType.getDocTypeAssigns().stream().map(DocTypeAssign::getAssignValueId).collect(Collectors.toSet());
            }
            if (CollectionUtils.isEmpty(tenantIds)) {
                logger.error("Error generate shield rule, because not assign to any tenant![docTypeId = {}]", docType.getDocTypeId());
                return;
            }
            List<DocTypeSqlid> docTypeSqlids = Optional.ofNullable(CollectionUtils.isEmpty(docType.getDocTypeSqlidList()) ? null : docType.getDocTypeSqlidList())
                    .orElseGet(() -> Collections.singletonList(new DocTypeSqlid()));
            // 遍历单据分配维度
            for (DocTypeAuthDim docTypeAuthDim : docType.getDocTypeAuthDims()) {
                // 单据权限头被禁用 或者 行数据为空（旧数据产生的数据） 禁用关联关系
                if (BaseConstants.Flag.NO.equals(docType.getEnabledFlag())
                        || StringUtils.isEmpty(docTypeAuthDim.getSourceMatchTable())
                        || StringUtils.isEmpty(docTypeAuthDim.getSourceMatchField())) {
                    continue;
                }
                String desc = MessageAccessor.getMessage(DOC_TYPE_PERMISSION_MESSAGE, new Object[]{docType.getDocTypeCode(), docTypeAuthDim.getAuthTypeCode()}).desc();
                // 需要新增或更新的单据权限
                tenantIds.forEach(itemTenant -> {
                    docTypeSqlids.forEach(docTypeSqlid -> {
                        String codeRuleKey = getCodeRuleKey(docType.getDocTypeId(), docTypeAuthDim.getAuthDimId(), itemTenant);
                        // 关联单据权限编码 + 单据分配维度ID
                        authDimPermission.put(codeRuleKey, docTypeAuthDim.getAuthDimId());
                        dataPermissionList.add(Pair.of(
                                new DataPermissionRangeVO()
                                        .setTableName(docTypeAuthDim.getSourceMatchTable())
                                        .setEnabledFlag(BaseConstants.Flag.YES)
                                        .setTenantId(itemTenant)
                                        .setSqlId(docTypeSqlid.getSqlid())
                                        .setDescription(desc)
                                        .setServiceName(docType.getSourceServiceName())
                                        .setCustomRuleFlag(BaseConstants.Flag.NO)
                                        .setEditableFlag(BaseConstants.Flag.NO),
                                new DataPermissionRuleVO()
                                        .setRuleCode(codeRuleKey)
                                        .setRuleName(docType.getDocTypeName())
                                        .setDescription(desc)
                                        .setSqlValue(getScript(getDocTypeDimension(tenantId, docTypeAuthDim.getAuthTypeCode()), docType.getAuthControlType(), docTypeAuthDim.getRuleType(), docTypeAuthDim.getSourceMatchField(), docTypeAuthDim.getAuthTypeCode(), docType.getDocTypeId(), iamProperties.getDocTenantType()))
                                        .setTenantId(itemTenant)
                                        .setEnabledFlag(BaseConstants.Flag.YES)
                                        .setEditableFlag(BaseConstants.Flag.NO)));
                    });
                });
            }
        });
        List<Pair<DataPermissionRangeVO, DataPermissionRuleVO>> response = Collections.emptyList();
        if (!CollectionUtils.isEmpty(dataPermissionList)) {
            // 新增或更新数据权限
            response = ResponseUtils.getResponse(permissionRuleFeignClient.save(tenantId, dataPermissionList), new TypeReference<List<Pair<DataPermissionRangeVO, DataPermissionRuleVO>>>() {
            });
        }
        // 将返回的结果构建成 Set<[authDimId, rangeId, ruleId]>
        Set<List<Long>> resultSet = response.stream().map(dataPermission -> Arrays.asList(
                authDimPermission.get(dataPermission.getSecond().getRuleCode()),
                dataPermission.getFirst().getRangeId(),
                dataPermission.getSecond().getRuleId()
        )).collect(Collectors.toSet());
        Set<List<Long>> presenceSet = new HashSet<>();
        // 删除原来存在，但是最新生成的数据权限中不存在的
        List<Pair<Long, Long>> disablePermissionRuleList = new ArrayList<>();
        for (DocType docType : docTypeList) {
            if (CollectionUtils.isEmpty(docType.getDocTypePermissions())) {
                continue;
            }
            for (DocTypePermission docTypePermission : docType.getDocTypePermissions()) {
                List<Long> presence = Arrays.asList(docTypePermission.getAuthDimId(), docTypePermission.getRangeId(), docTypePermission.getRuleId());
                if (!resultSet.contains(presence)) {
                    disablePermissionRuleList.add(Pair.of(presence.get(1), presence.get(2)));
                    docTypePermissionRepository.deleteByPrimaryKey(docTypePermission);
                }
                presenceSet.add(presence);
            }
        }
        // 单据权限维度被分配后会产生关联不到单据的权限，修复数据
        docTypePermissionRepository.listPermissionNotAssociated().forEach(docTypePermission -> {
            docTypePermissionRepository.deleteByPrimaryKey(docTypePermission);
            disablePermissionRuleList.add(Pair.of(docTypePermission.getRangeId(), docTypePermission.getRuleId()));
        });

        if (!CollectionUtils.isEmpty(disablePermissionRuleList)) {
            permissionRuleFeignClient.disableRel(tenantId, disablePermissionRuleList);
        }
        // 获取维度禁用但单据仍为启用的数据权限Id信息，该种数据权限需被禁用掉
        List<Long> permissionRuleIds =
                docTypeDimensionRepository.selectDisabledPermissionRuleId();
        if (!CollectionUtils.isEmpty(permissionRuleIds)) {
            // 禁用无用的数据权限
            permissionRuleFeignClient.disablePermission(tenantId, permissionRuleIds);
        }
        // 生成原来Set中不存在的关系
        resultSet.forEach(result -> {
            if (!presenceSet.contains(result)) {
                docTypePermissionRepository.insertSelective(new DocTypePermission()
                        .setRangeId(result.get(1))
                        .setTenantId(tenantId)
                        .setRuleId(result.get(2))
                        .setAuthDimId(result.get(0)));
            }
        });

    }

    @Override
    @Async("IamCommonAsyncTaskExecutor")
    public void fixDocTypeData() {
        logger.info("============================ Start repairing document authority data... ============================");
        // 判断是否是1.4.0升级到1.4.1版本
        int count = docTypeRepository.selectCountByCondition(Condition.builder(DocType.class)
                .andWhere(Sqls.custom()
                        .andEqualTo(DocType.FIELD_AUTH_CONTROL_TYPE, Constants.DocType.NEW_AUTH_TYPE_ALL))
                .build());
        // 处理启用单据的数据权限SQL
        List<DocType> enabledDocTypes = docTypeRepository.selectByCondition(Condition.builder(DocType.class)
                .andWhere(Sqls.custom()
                        .andEqualTo(DocType.FIELD_ENABLED_FLAG, BaseConstants.Flag.YES)
                )
                .build());
        List<Long> enabledDocTypeIds = enabledDocTypes.stream().map(DocType::getDocTypeId).collect(Collectors.toList());
        if (count != 0) {
            // 1.4.0-1.4.1，该种类型直接更新数据权限SQL即可
            // finally 批量修复数据权限SQL
            this.generateShieldRule(BaseConstants.DEFAULT_TENANT_ID, enabledDocTypeIds);
            // 处理缓存标识
            authorityDomainService.initDocAuthCache();
            logger.info("============================ Finishing repairing 1.4.0 document authority data... ============================");
            return ;
        }
        // 1.查询所有ALL类型的单据权限，用于处理角色头数据
        List<DocType> allControlList = docTypeRepository.selectByCondition(Condition.builder(DocType.class)
                .andWhere(Sqls.custom()
                    .andEqualTo(DocType.FIELD_AUTH_CONTROL_TYPE, Constants.DocType.AUTH_TYPE_ALL)
                ).build());
        if (!CollectionUtils.isEmpty(allControlList)) {
            // 处理所有人类型的单据权限数据
            this.processAllControlDoc(allControlList);
        }
        // finally 批量修复数据权限SQL
        this.generateShieldRule(BaseConstants.DEFAULT_TENANT_ID, enabledDocTypeIds);
        // 处理缓存标识
        authorityDomainService.initDocAuthCache();
        logger.info("============================ Finishing repairing document authority data... ============================");
    }

    /**
     * 处理所有人类型单据权限数据
     *
     * @param allControlList 所有人类型的单据权限数据
     */
    private void processAllControlDoc(List<DocType> allControlList) {
        if (CollectionUtils.isEmpty(allControlList)) {
            return ;
        }

        List<Long> docIds = allControlList.stream().map(DocType::getDocTypeId).collect(Collectors.toList());
        if (CollectionUtils.isEmpty(docIds)) {
            return ;
        }
        // 2.处理角色单据权限
        // 获取所有人类型中，仅存在头信息，不存在行信息的数据，该种数据直接删除头数据即可
        logger.info("=====================>>start fix role authority headers<<=====================");
        int total = 0;
        Page<RoleAuthority> roleAuthorities;
        PageRequest pageRequest = new PageRequest(0, 1000, new Sort(Sort.Direction.ASC, RoleAuthority.FIELD_ROLE_AUTH_ID));
        do {
            roleAuthorities = PageHelper.doPageAndSort(pageRequest, () -> roleAuthorityRepository.selectByDocIds(docIds));
            if (!CollectionUtils.isEmpty(roleAuthorities)) {
                List<Long> roleAuthIds =
                        roleAuthorities.parallelStream().map(RoleAuthority::getRoleAuthId).collect(Collectors.toList());
                roleAuthorityRepository.batchDeleteByRoleAuthorityId(roleAuthIds);
            }
            total += roleAuthorities.size();
            pageRequest.setPage(pageRequest.getPage() + 1);
        } while(!roleAuthorities.isEmpty());
        logger.info("=====================>>success fix role authority headers, total is : {}<<=====================", total);
        // 更新单据权限类型
        allControlList.forEach(docType -> docType.setAuthControlType(Constants.DocType.NEW_AUTH_TYPE_ALL));
        docTypeRepository.batchUpdateOptional(allControlList, DocType.FIELD_AUTH_CONTROL_TYPE);
    }

    private String getDocTypeDimension(Long tenantId, String authTypeCode) {
        if (tenantId == null) {
            tenantId = BaseConstants.DEFAULT_TENANT_ID;
        }
        DocTypeDimension docTypeDimension = docTypeDimensionRepository.selectOne(new DocTypeDimension()
                .setTenantId(tenantId)
                .setDimensionCode(authTypeCode));
        if (docTypeDimension != null) {
            return docTypeDimension.getDimensionType();
        }
        if (!BaseConstants.DEFAULT_TENANT_ID.equals(tenantId)) {
            docTypeDimension = docTypeDimensionRepository.selectOne(new DocTypeDimension()
                    .setTenantId(BaseConstants.DEFAULT_TENANT_ID)
                    .setDimensionCode(authTypeCode));
            if (docTypeDimension != null) {
                return docTypeDimension.getDimensionType();
            }
        }
        logger.error("get docTypeDimension failed, the docTypeDimension is null");
        throw new CommonException(BaseConstants.ErrorCode.DATA_INVALID);
    }

    private String getScript(String dimension, String authorizeControl, String ruleType, String field, String authType, Long docTypeId, String docTenantType) {
        return DocTypeScript.getScript(dimension, authorizeControl, ruleType, docTenantType)
                .replace(DOC_TYPE_FIELD_REPLACE, field)
                .replace(DOC_TYPE_PERMISSION_REPLACE, authType)
                .replace(DOC_TYPE_ID_REPLACE, String.valueOf(docTypeId));

    }

    private String getCodeRuleKey(long docTypeId, long authDimId, long tenantId) {
        return String.format("%s%04d%04d%04d", DOC_TYPE_PERMISSION_CODE_RULE_PREFIX, docTypeId, authDimId, tenantId);
    }
}
