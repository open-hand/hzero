package org.hzero.iam.domain.service.secgrp.authority.impl;

import io.choerodon.core.exception.CommonException;
import io.choerodon.mybatis.pagehelper.domain.PageRequest;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.hzero.core.convert.CommonConverter;
import org.hzero.core.util.CommonStream;
import org.hzero.iam.api.dto.CompanyOuInvorgDTO;
import org.hzero.iam.api.dto.SecGrpDclDTO;
import org.hzero.iam.api.dto.SecGrpDclQueryDTO;
import org.hzero.iam.domain.entity.*;
import org.hzero.iam.domain.repository.*;
import org.hzero.iam.domain.service.secgrp.authority.AbstractSecGrpAuthorityService;
import org.hzero.iam.domain.service.secgrp.authority.dcl.DclServiceManager;
import org.hzero.iam.domain.service.secgrp.dto.SecGrpDclDimDetailDTO;
import org.hzero.iam.domain.service.secgrp.enums.SecGrpAuthorityType;
import org.hzero.iam.domain.service.secgrp.observer.dim.RoleSecGrpDimObserver;
import org.hzero.iam.domain.service.secgrp.observer.dim.SecGrpDimObserver;
import org.hzero.iam.infra.constant.Constants;
import org.hzero.mybatis.domian.Condition;
import org.hzero.mybatis.util.Sqls;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.*;
import java.util.function.Consumer;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.*;
import static org.hzero.iam.infra.constant.Constants.DIM_UNIQUE_KEY_TEMPLATE;
import static org.hzero.iam.infra.constant.Constants.SecGrpAssignTypeCode.PARENT;
import static org.hzero.iam.infra.constant.Constants.SecGrpAssignTypeCode.SELF_PARENT;

/**
 * 安全组权限服务——数据权限维度权限
 *
 * @author bojiangzhou 2020/02/12
 */
@Component
public class SecGrpDimAuthorityService extends AbstractSecGrpAuthorityService<SecGrpDclDim> {
    /**
     * 日志打印对象
     */
    private final Logger logger = LoggerFactory.getLogger(SecGrpDimAuthorityService.class);

    /**
     * 默认数据源
     */
    private final String DEFAULT_DATA_SOURCE = Constants.SecGrpAssign.DEFAULT_DATA_SOURCE;

    /**
     * 安全组数据源
     */
    private final String SEC_GRP_DATA_SOURCE = Constants.SecGrpAssign.SEC_GRP_DATA_SOURCE;

    /**
     * 默认和安全组数据源
     */
    private final String DEFAULT_SEC_GRP_DATA_SOURCE = Constants.SecGrpAssign.DEFAULT_SEC_GRP_DATA_SOURCE;

    @Autowired
    private SecGrpRepository secGrpRepository;
    @Autowired
    private RoleAuthorityLineRepository roleAuthorityLineRepository;
    @Autowired
    private RoleAuthorityRepository roleAuthorityRepository;
    @Autowired
    private SecGrpDclDimRepository secGrpDclDimRepository;
    @Autowired
    private SecGrpDclDimLineRepository secGrpDclDimLineRepository;

    @Autowired
    private List<SecGrpDimObserver> dimObservers;

    @Autowired
    private RoleSecGrpDimObserver roleSecGrpDimObserver;

    /**
     * 数据权限服务管理器
     */
    @Autowired
    private DclServiceManager dclServiceManager;
    /**
     * 安全组数据权限头参考对象
     */
    @Autowired
    private SecGrpDclRepository secGrpDclRepository;
    /**
     * 安全组数据权限行仓库对象
     */
    @Autowired
    private SecGrpDclLineRepository secGrpDclLineRepository;

    @Override
    public boolean support(@Nonnull SecGrpAuthorityType authorityType) {
        return SecGrpAuthorityType.DCL_DIM.equals(authorityType);
    }

    @Override
    public void initSecGrpAuthority(@Nonnull SecGrp secGrp, @Nonnull Long roleId) {
        List<SecGrpDclDimDetailDTO> dims = this.secGrpDclDimRepository.listRoleSecGrpDim(roleId);
        if (CollectionUtils.isEmpty(dims)) {
            return;
        }
        // 先去重，再按照数据权限维度唯一键分组，最后处理数据
        dims.stream()
                // 根据 authDocTypeId、authScopeCode、authTypeCode 去重
                .filter(CommonStream.distinctByKey(SecGrpDclDimDetailDTO::buildUniqueKey))
                // 分组
                .collect(Collectors.groupingBy(dim ->
                        String.format(DIM_UNIQUE_KEY_TEMPLATE, dim.getAuthDocTypeId(), dim.getAuthScopeCode())))
                // 按每个分组处理数据
                .forEach((dimUniqueKey, lines) -> {
                    if (CollectionUtils.isEmpty(lines)) {
                        return;
                    }
                    // 针对每个权限单据类型（authDocTypeId），他的维度范围 authScopeCode 应该是唯一的
                    // 也就是说按 authDocTypeId 分组得到的 SecGrpDclDimDetailDTO 数组，的 authScopeCode 字段应该是一样的
                    SecGrpDclDimDetailDTO dto = lines.get(0);
                    SecGrpDclDim dim = new SecGrpDclDim(secGrp.getTenantId(), secGrp.getSecGrpId(), dto.getAuthDocTypeId(),
                            dto.getAuthScopeCode(), PARENT);
                    // 插入权限维度
                    this.secGrpDclDimRepository.insertSelective(dim);

                    // 处理行
                    lines.parallelStream()
                            // authScopeCode为空的说明是没有行数据，不需要处理行
                            .filter(line -> StringUtils.isNotBlank(line.getAuthTypeCode()))
                            .forEach(line -> {
                                SecGrpDclDimLine demLine = SecGrpDclDimLine.buildDimLine(dim, line.getAuthTypeCode(), PARENT);
                                this.secGrpDclDimLineRepository.insertSelective(demLine);
                            });
                });
    }

    @Override
    public void copySecGrpAuthority(@Nonnull List<SecGrp> sourceSecGrps, @Nonnull SecGrp targetSecGrp) {
        List<SecGrpDclDimDetailDTO> dims;

        List<Long> selfSecGrpIds = sourceSecGrps.stream().filter(SecGrp::isSelfBuild).map(SecGrp::getSecGrpId)
                .collect(Collectors.toList());
        if (CollectionUtils.isNotEmpty(selfSecGrpIds)) {
            // 有自建安全组的情况：只需要合并所有自建安全组中限定的数据权限维度，其一定包含了分配安全组中的数据权限维度，且可能包含新增的
            dims = this.secGrpDclDimRepository.listSecGrpAssignableDim(selfSecGrpIds);
        } else {
            // 当全部是分配安全组的情况：这个时候应该取角色被限制的数据权限维度
            dims = this.secGrpDclDimRepository.listRoleSecGrpDim(targetSecGrp.getRoleId());
            if (CollectionUtils.isNotEmpty(dims)) {
                // 因为查出来的都来自父级安全组，对于当前角色而言全部是分配的
                dims.forEach(item -> item.setAssignTypeCode(PARENT));
            }
        }

        if (CollectionUtils.isEmpty(dims)) {
            return;
        }

        // 去重并分组，最终处理数据
        dims.parallelStream()
                // 根据 authDocTypeId、authScopeCode、authTypeCode 去重
                .filter(CommonStream.distinctByKey(SecGrpDclDimDetailDTO::buildUniqueKey))
                // 分组
                .collect(Collectors.groupingBy(dim ->
                        String.format(DIM_UNIQUE_KEY_TEMPLATE, dim.getAuthDocTypeId(), dim.getAuthScopeCode())))
                // 按每个处理数据
                .forEach((dimUniqueKey, lines) -> {
                    if (CollectionUtils.isEmpty(lines)) {
                        return;
                    }

                    SecGrpDclDimDetailDTO secGrpDclDimDetailDTO = lines.get(0);
                    SecGrpDclDim secGrpDclDim = new SecGrpDclDim(targetSecGrp.getTenantId(), targetSecGrp.getSecGrpId(),
                            secGrpDclDimDetailDTO.getAuthDocTypeId(), secGrpDclDimDetailDTO.getAuthScopeCode(), PARENT);

                    this.secGrpDclDimRepository.insertSelective(secGrpDclDim);

                    lines.parallelStream().forEach(dim -> {
                        if (StringUtils.isNotBlank(dim.getAuthTypeCode())) {
                            SecGrpDclDimLine line = SecGrpDclDimLine.buildDimLine(secGrpDclDim, dim.getAuthTypeCode(),
                                    dim.getAssignTypeCode());
                            this.secGrpDclDimLineRepository.insertSelective(line);
                        }
                    });
                });
    }

    @Override
    public void deleteAuthorityBySecGrpId(@Nonnull Long secGrpId) {
        // 删除行
        this.secGrpDclDimLineRepository.delete(new SecGrpDclDimLine(secGrpId));
        // 删除头
        this.secGrpDclDimRepository.delete(new SecGrpDclDim(secGrpId));
    }

    @Override
    public void assignRoleSecGrpAuthority(@Nonnull Role role, @Nonnull SecGrp secGrp) {
        Long secGrpId = secGrp.getSecGrpId();
        //查询本安全组所对应的auth_doc_type_id
        List<SecGrpDclDim> secGrpDclDims = this.secGrpDclDimRepository.selectBySecGrpId(secGrpId);
        // 回收的权限行
        Set<String> authTypeCodes;
        // 权限维度行查询条件对象
        SecGrpDclDimLine condition = new SecGrpDclDimLine();
        for (SecGrpDclDim secGrpDclDim : secGrpDclDims) {
            condition.setSecGrpDclDimId(secGrpDclDim.getSecGrpDclDimId());
            authTypeCodes = this.secGrpDclDimLineRepository.select(condition)
                    .stream().map(SecGrpDclDimLine::getAuthTypeCode)
                    .collect(toSet());

            // 分配角色创建的安全组的权限
            this.assignChildrenDim(secGrp, secGrpDclDim, authTypeCodes,
                    (parentSecGrp, parentDim, notIncludedAuthType, assignRoles) ->
                            this.roleSecGrpDimObserver.assignRolesDim(assignRoles, parentDim, notIncludedAuthType),
                    Collections.singletonList(role));
        }
    }

    @Override
    public void recycleRoleSecGrpAuthority(@Nonnull Role role, @Nonnull SecGrp secGrp) {
        Long secGrpId = secGrp.getSecGrpId();
        //查询本安全组所对应的auth_doc_type_id
        List<SecGrpDclDim> secGrpDclDims = this.secGrpDclDimRepository.selectBySecGrpId(secGrpId);
        // 回收的权限行
        Set<String> authTypeCodes;
        // 权限维度行查询条件对象
        SecGrpDclDimLine condition = new SecGrpDclDimLine();
        for (SecGrpDclDim secGrpDclDim : secGrpDclDims) {
            condition.setSecGrpDclDimId(secGrpDclDim.getSecGrpDclDimId());
            authTypeCodes = this.secGrpDclDimLineRepository.select(condition)
                    .stream().map(SecGrpDclDimLine::getAuthTypeCode)
                    .collect(toSet());

            // 回收角色创建的安全组的权限
            this.recycleChildrenDim(secGrp, secGrpDclDim, authTypeCodes,
                    (parentSecGrp, parentDim, notIncludedAuthType, assignRoles) ->
                            this.roleSecGrpDimObserver.recycleRolesDim(assignRoles, parentDim, notIncludedAuthType),
                    Collections.singletonList(role));
        }
    }

    @Override
    protected void updateSecGrpAuthority(@Nonnull SecGrp secGrp, @Nonnull List<SecGrpDclDim> authorities) {
        // 处理安全组权限，分配
        this.validOperateSecGrpAuthority(secGrp, authorities,
                null, dimLines -> {
                    // 校验维度行数据
                    if (dimLines.stream().anyMatch(dimLine -> !dimLine.operability())) {
                        throw new CommonException("hiam.warn.secGrp.noAuthorityToAutoAssign");
                    }
                }, this::assignDim);
    }

    @Override
    protected void removeSecGrpAuthority(@Nonnull SecGrp secGrp, @Nonnull List<SecGrpDclDim> authorities) {
        // 处理安全组权限，移除
        this.validOperateSecGrpAuthority(secGrp, authorities,
                dims -> {
                    // 校验维度头数据
                    if (dims.stream().anyMatch(dim -> !dim.operability())) {
                        throw new CommonException("hiam.warn.secGrp.noAuthorityToAutoAssign");
                    }
                }, dimLines -> {
                    // 校验维度行数据
                    if (dimLines.stream().anyMatch(dimLine -> !dimLine.operability())) {
                        throw new CommonException("hiam.warn.secGrp.noAuthorityToAutoAssign");
                    }
                }, this::recycleDim);
    }

    @Override
    protected void enableSecGrpAuthority(@Nonnull SecGrp secGrp) {
        Long secGrpId = secGrp.getSecGrpId();
        // 获取数据库数据权限信息
        DbDimData dbDimData = this.selectDbDimDataWithDimUniqueKeys(secGrpId, null, null, null);

        // 处理安全组权限，启用
        this.operateSecGrpAuthority(secGrp, dbDimData::getDbDims,
                () -> dbDimData, (opSecGrp, dbDim, operateDim, dbDimLines, operateDimLines) -> {
                    // 分配的权限码
                    Set<String> assignAuthTypeCodes;
                    if (CollectionUtils.isNotEmpty(dbDimLines)) {
                        assignAuthTypeCodes = dbDimLines.stream().map(SecGrpDclDimLine::getAuthTypeCode).collect(toSet());
                    } else {
                        assignAuthTypeCodes = Collections.emptySet();
                    }

                    // 分配子安全组数据权限维度
                    this.assignChildrenDim(opSecGrp, dbDim, assignAuthTypeCodes,
                            (parentSecGrp, parentDim, notIncludedAuthType, assignRoles) -> {
                                for (SecGrpDimObserver dimObserver : this.dimObservers) {
                                    dimObserver.assignSecGrpDim(parentSecGrp, parentDim, notIncludedAuthType);
                                }
                            }, null);
                });
    }

    @Override
    protected void disableSecGrpAuthority(@Nonnull SecGrp secGrp) {
        Long secGrpId = secGrp.getSecGrpId();
        // 获取数据库数据权限信息
        DbDimData dbDimData = this.selectDbDimDataWithDimUniqueKeys(secGrpId, null, null, null);

        // 处理安全组权限，禁用
        this.operateSecGrpAuthority(secGrp, dbDimData::getDbDims,
                () -> dbDimData, (opSecGrp, dbDim, operateDim, dbDimLines, operateDimLines) -> {
                    // 回收的权限码
                    Set<String> recycleAuthTypeCodes;
                    if (CollectionUtils.isNotEmpty(dbDimLines)) {
                        recycleAuthTypeCodes = dbDimLines.stream().map(SecGrpDclDimLine::getAuthTypeCode).collect(toSet());
                    } else {
                        recycleAuthTypeCodes = Collections.emptySet();
                    }

                    // 回收子安全组数据权限维度
                    this.recycleChildrenDim(opSecGrp, dbDim, recycleAuthTypeCodes,
                            (parentSecGrp, parentDim, notIncludedAuthType, assignRoles) -> {
                                for (SecGrpDimObserver dimObserver : this.dimObservers) {
                                    dimObserver.recycleSecGrpDim(parentSecGrp, parentDim, notIncludedAuthType);
                                }
                            }, null);
                });
    }

    /**
     * 操作安全组权限
     *
     * @param secGrp         安全组
     * @param getAuthorities 获取权限数据方法
     * @param getDbDimData   获取数据库数据方法
     * @param consumer       权限消费逻辑
     */
    private void operateSecGrpAuthority(@Nonnull SecGrp secGrp,
                                        @Nonnull Supplier<List<SecGrpDclDim>> getAuthorities,
                                        @Nonnull Supplier<DbDimData> getDbDimData,
                                        @Nonnull SecGrpAuthorityConsumer consumer) {
        // 权限对象
        List<SecGrpDclDim> authorities = getAuthorities.get();

        // 数据对象不为空
        if (CollectionUtils.isNotEmpty(authorities)) {
            // 获取数据库数据权限信息
            DbDimData dbDimData = Optional.ofNullable(getDbDimData.get())
                    .orElse(DbDimData.of(null, null));

            // 数据库的数据权限维度头
            SecGrpDclDim dbDim;
            // 数据库的数据权限维度行
            List<SecGrpDclDimLine> dbDimLines;
            for (SecGrpDclDim operateDim : authorities) {
                // 获取数据库数据权限维度头
                dbDim = dbDimData.getDbDim(operateDim.getAuthDocTypeId(), operateDim.getAuthScopeCode());
                if (Objects.nonNull(dbDim)) {
                    // 获取数据库数据权限维度行
                    dbDimLines = dbDimData.getDbDimLine(dbDim.getSecGrpDclDimId());
                } else {
                    dbDimLines = Collections.emptyList();
                }

                // 操作
                consumer.operate(secGrp, dbDim, operateDim, dbDimLines, operateDim.getDimLineList());
            }
        }
    }

    /**
     * 加校验的操作安全组权限
     *
     * @param secGrp       安全组
     * @param authorities  要操作的权限
     * @param validDim     校验数据维度头的校验逻辑
     * @param validDimLine 校验数据维度行的校验逻辑
     * @param consumer     权限消费逻辑
     */
    private void validOperateSecGrpAuthority(@Nonnull SecGrp secGrp, List<SecGrpDclDim> authorities,
                                             @Nullable Consumer<List<SecGrpDclDim>> validDim,
                                             @Nullable Consumer<List<SecGrpDclDimLine>> validDimLine,
                                             @Nonnull SecGrpAuthorityConsumer consumer) {
        // 处理安全组权限，分配
        this.operateSecGrpAuthority(secGrp, () -> authorities,
                () -> this.selectDbDimDataWithDimUniqueKeys(secGrp.getSecGrpId(), authorities.stream()
                        .collect(toMap(dim -> String.format(DIM_UNIQUE_KEY_TEMPLATE, dim.getAuthDocTypeId(), dim.getAuthScopeCode()),
                                SecGrpDclDim::getDimLineList)), validDim, validDimLine), consumer);
    }

    /**
     * 分配数据权限维度
     *
     * @param secGrp          安全组
     * @param dbDim           数据库中的当前数据权限维度头
     * @param operateDim      操作的数据权维度头
     * @param dbDimLines      数据库中的当前数据权限维度行
     * @param operateDimLines 操作的数据权限维度行
     */
    private void assignDim(SecGrp secGrp, SecGrpDclDim dbDim, SecGrpDclDim operateDim,
                           List<SecGrpDclDimLine> dbDimLines, List<SecGrpDclDimLine> operateDimLines) {
        // 判断数据库中的dim是否存在
        if (Objects.isNull(dbDim)) {
            // 不存在就创建并持久化dim
            // 新增维度范围
            operateDim.setAssignTypeCode(Constants.SecGrpAssignTypeCode.SELF);
            this.secGrpDclDimRepository.insertSelective(operateDim);
            dbDim = operateDim;
        }

        // 分配的维度码
        Set<String> assignAuthTypeCodes = new HashSet<>();
        // 回收的维度码
        Set<String> recycleAuthTypeCodes = new HashSet<>();
        if (CollectionUtils.isNotEmpty(operateDimLines)) {
            // 查询维度值，按维度值转成 map
            Map<String, SecGrpDclDimLine> dbDimLineMap = dbDimLines.stream()
                    .collect(toMap(SecGrpDclDimLine::getAuthTypeCode, t -> t));
            // 数据库数据权限维度行
            SecGrpDclDimLine dbDimLine;
            for (SecGrpDclDimLine operateDimLine : operateDimLines) {
                // 获取数据库数据权限维度行
                dbDimLine = dbDimLineMap.get(operateDimLine.getAuthTypeCode());
                if (Objects.isNull(dbDimLine)) {
                    // 数据库不存在维度行
                    if (operateDimLine.isChecked()) {
                        // 如果操作的参数选择了，就需要创建维度行
                        SecGrpDclDimLine newDimLine = SecGrpDclDimLine.buildDimLine(dbDim, operateDimLine.getAuthTypeCode(),
                                Constants.SecGrpAssignTypeCode.SELF);
                        // 插入数据
                        this.secGrpDclDimLineRepository.insertSelective(newDimLine);
                        // 增加已分配的授权码
                        assignAuthTypeCodes.add(operateDimLine.getAuthTypeCode());
                    }
                } else {
                    // 数据库存在维度行
                    if (operateDimLine.isUnchecked()) {
                        // 如果操作的参数取消了，就需要删除维度行
                        this.secGrpDclDimLineRepository.deleteByPrimaryKey(dbDimLine.getSecGrpDclDimLineId());
                        // 增加已删除的授权码
                        recycleAuthTypeCodes.add(operateDimLine.getAuthTypeCode());
                    }
                }
            }
        }

        // 回收维度的数据权限
        this.recycleDcl(secGrp, recycleAuthTypeCodes);


        // 回收子安全组数据权限维度
        this.recycleChildrenDim(secGrp, dbDim, recycleAuthTypeCodes,
                (parentSecGrp, parentDim, notIncludedAuthType, assignRoles) -> {
                    for (SecGrpDimObserver dimObserver : this.dimObservers) {
                        dimObserver.recycleSecGrpDimLine(parentSecGrp, parentDim, notIncludedAuthType);
                    }
                }, null);
        // 分配子安全组数据权限维度
        this.assignChildrenDim(secGrp, dbDim, assignAuthTypeCodes,
                (parentSecGrp, parentDim, notIncludedAuthType, assignRoles) -> {
                    for (SecGrpDimObserver dimObserver : this.dimObservers) {
                        dimObserver.assignSecGrpDim(parentSecGrp, parentDim, notIncludedAuthType);
                    }
                }, null);
    }

    /**
     * 给子节点分配数据权限维度
     *
     * @param parentSecGrp        父级安全组
     * @param parentDim           父级权限维度头
     * @param assignAuthTypeCodes 分配的权限类型码
     */
    private void assignChildrenDim(@Nonnull SecGrp parentSecGrp,
                                   @Nonnull SecGrpDclDim parentDim,
                                   @Nonnull Set<String> assignAuthTypeCodes,
                                   @Nonnull DimObserverConsumer parentSecGrpConsumer,
                                   @Nullable List<Role> assignedRoles) {
        // 操作子Dim，分配
        this.operateChildrenDim(parentSecGrp, parentDim, assignAuthTypeCodes, parentSecGrpConsumer,
                (childSecGrp, childDim, authTypeCodes) -> {
                    // todo 处理角色分配的安全组中,在提供的维度值中存在的维度值 对应的数据权限处理
                    // 因为父级安全组已经去除了数据权限,所以子安全组对应的可操作的数据权限范围缩小,此处需要回收子安全组中多出来的这部分数据权限

                },
                (childSecGrp, childDim, authTypeCodes) -> {
                    // 当前操作需要分配的权限码
                    Set<String> opAssignAuthTypeCodes = Collections.emptySet();
                    // 当前操作更新的权限码
                    Set<String> opUpdateAuthTypeCodes = Collections.emptySet();

                    // 处理头
                    if (Objects.isNull(childDim)) {
                        // 创建Dim
                        childDim = SecGrpDclDim.buildDim(childSecGrp.getSecGrpId(), parentDim, PARENT);
                        // 插入dim
                        this.secGrpDclDimRepository.insertSelective(childDim);
                    } else {
                        if (Constants.SecGrpAssignTypeCode.SELF.equals(childDim.getAssignTypeCode())) {
                            childDim.setAssignTypeCode(SELF_PARENT);
                            // 更新维度
                            this.secGrpDclDimRepository.updateOptional(childDim, SecGrpDclDim.FIELD_ASSIGN_TYPE_CODE);
                        }
                    }

                    // 处理行
                    if (CollectionUtils.isNotEmpty(authTypeCodes)) {
                        // 查询当前Dim未分配的权限行 key -> value === AuthTypeCode -> DimLine
                        Map<String, SecGrpDclDimLine> includedDimLineMap = this.secGrpDclDimLineRepository.listDimIncludedDimLine(
                                childDim.getSecGrpDclDimId(), authTypeCodes);
                        // 新建集合
                        opAssignAuthTypeCodes = new HashSet<>(authTypeCodes.size());
                        opUpdateAuthTypeCodes = new HashSet<>(authTypeCodes.size());

                        // 子类DimLine
                        SecGrpDclDimLine childDimLine;
                        for (String authTypeCode : authTypeCodes) {
                            // 获取DimLine
                            childDimLine = includedDimLineMap.get(authTypeCode);

                            // 处理行
                            if (Objects.isNull(childDimLine)) {
                                // 创建行
                                childDimLine = SecGrpDclDimLine.buildDimLine(childDim, authTypeCode, PARENT);

                                // 新增数据
                                this.secGrpDclDimLineRepository.insertSelective(childDimLine);
                                // 添加权限码
                                opAssignAuthTypeCodes.add(childDimLine.getAuthTypeCode());
                            } else {
                                if (Constants.SecGrpAssignTypeCode.SELF.equals(childDimLine.getAssignTypeCode())) {
                                    childDimLine.setAssignTypeCode(SELF_PARENT);
                                    // 更新数据
                                    this.secGrpDclDimLineRepository.updateOptional(childDimLine,
                                            SecGrpDclDimLine.FIELD_ASSIGN_TYPE_CODE);
                                    // 添加权限码
                                    opUpdateAuthTypeCodes.add(childDimLine.getAuthTypeCode());
                                }
                            }
                        }
                    }

                    // 回收更新的权限码的数据权限(因为当前添加了权限码,父级对子集有控制,所以需要移除子集已经添加的数据权限)
                    this.recycleDcl(childSecGrp, opUpdateAuthTypeCodes);

                    // 分配子类Dim
                    this.assignChildrenDim(childSecGrp, childDim, opAssignAuthTypeCodes, parentSecGrpConsumer, null);
                }, assignedRoles);
    }

    /**
     * 回收数据权限维度
     *
     * @param secGrp          安全组
     * @param dbDim           数据库中的当前数据权限维度头
     * @param operateDim      操作的数据权维度头
     * @param dbDimLines      数据库中的当前数据权限维度行
     * @param operateDimLines 操作的数据权限维度行
     */
    private void recycleDim(SecGrp secGrp, SecGrpDclDim dbDim, SecGrpDclDim operateDim,
                            List<SecGrpDclDimLine> dbDimLines, List<SecGrpDclDimLine> operateDimLines) {
        if (Objects.nonNull(dbDim)) {
            // 数据库的Dim存在，才进行操作
            // 当前安全组需要回收的权限类型代码
            Set<String> authTypeCodes = Collections.emptySet();
            if (CollectionUtils.isNotEmpty(dbDimLines)) {
                // 解析授权码
                authTypeCodes = dbDimLines.stream().map(SecGrpDclDimLine::getAuthTypeCode).collect(toSet());
                // 删除维度行
                this.secGrpDclDimLineRepository.batchDeleteByPrimaryKey(dbDimLines);
                // 移除维度后,需要移除维度对应的数据权限
                this.recycleDcl(secGrp, authTypeCodes);
            }

            // 回收子类Dim
            this.recycleChildrenDim(secGrp, dbDim, authTypeCodes,
                    (parentSecGrp, parentDim, notIncludedAuthType, assignRoles) -> {
                        for (SecGrpDimObserver dimObserver : this.dimObservers) {
                            dimObserver.recycleSecGrpDim(parentSecGrp, parentDim, notIncludedAuthType);
                        }
                    }, null);

            // 删除维度头
            this.secGrpDclDimRepository.deleteByPrimaryKey(dbDim.getSecGrpDclDimId());
        }
    }

    /**
     * 回收子节点的数据维度
     *
     * @param parentSecGrp         父安全组
     * @param parentDim            父安全组的权限维度
     * @param recycleAuthTypeCodes 需要处理的授权类型码
     * @param parentSecGrpConsumer 角色分配的其他安全组中没有包含的维度的父安全组处理逻辑
     */
    private void recycleChildrenDim(@Nonnull SecGrp parentSecGrp,
                                    @Nonnull SecGrpDclDim parentDim,
                                    @Nonnull Set<String> recycleAuthTypeCodes,
                                    @Nonnull DimObserverConsumer parentSecGrpConsumer,
                                    @Nullable List<Role> assignedRoles) {
        // 操作子Dim，回收
        this.operateChildrenDim(parentSecGrp, parentDim, recycleAuthTypeCodes, parentSecGrpConsumer,
                (childSecGrp, childDim, authTypeCodes) -> {
                    // todo 处理角色分配的安全组中,在提供的维度值中存在的维度值 对应的数据权限处理
                    // 因为父级安全组已经去除了数据权限,所以子安全组对应的可操作的数据权限范围缩小,此处需要回收子安全组中多出来的这部分数据权限

                },
                (childSecGrp, childDim, authTypeCodes) -> {
                    // 当前操作需要更新的权限码
                    Set<String> opUpdateAuthTypeCodes = Collections.emptySet();
                    // 当前操作需要回收的权限码
                    Set<String> opRecycleAuthTypeCodes = Collections.emptySet();
                    // 处理头
                    if (Objects.nonNull(childDim)) {
                        // 处理行
                        if (CollectionUtils.isNotEmpty(authTypeCodes)) {
                            // 查询当前Dim未分配的权限行 key -> value === AuthTypeCode -> DimLine
                            Map<String, SecGrpDclDimLine> includedDimLineMap = this.secGrpDclDimLineRepository
                                    .listDimIncludedDimLine(childDim.getSecGrpDclDimId(), authTypeCodes);
                            // 子类DimLine
                            SecGrpDclDimLine childDimLine;

                            // 创建集合
                            opUpdateAuthTypeCodes = new HashSet<>(authTypeCodes.size());
                            opRecycleAuthTypeCodes = new HashSet<>(authTypeCodes.size());
                            for (String authTypeCode : authTypeCodes) {
                                // 获取DimLine
                                childDimLine = includedDimLineMap.get(authTypeCode);

                                if (Objects.nonNull(childDimLine)) {
                                    if (SELF_PARENT.equals(childDimLine.getAssignTypeCode())) {
                                        childDimLine.setAssignTypeCode(Constants.SecGrpAssignTypeCode.SELF);
                                        // 更新数据
                                        this.secGrpDclDimLineRepository.updateOptional(childDimLine,
                                                SecGrpDclDimLine.FIELD_ASSIGN_TYPE_CODE);
                                        // 添加需要更新的权限码
                                        opUpdateAuthTypeCodes.add(childDimLine.getAuthTypeCode());
                                    } else if (PARENT.equals(childDimLine.getAssignTypeCode())) {
                                        // 删除数据
                                        this.secGrpDclDimLineRepository.deleteByPrimaryKey(childDimLine);
                                        // 添加需要回收的权限码
                                        opRecycleAuthTypeCodes.add(childDimLine.getAuthTypeCode());
                                    }
                                }
                            }
                        }

                        // 存在维度头
                        if (SELF_PARENT.equals(childDim.getAssignTypeCode())) {
                            // 判断维度行是否还存在 SELF_PARENT 数据
                            if (this.secGrpDclDimLineRepository
                                    .countDimLineByDimIdAndAssignTypeCode(childDim.getSecGrpDclDimId(), SELF_PARENT) == 0) {
                                childDim.setAssignTypeCode(Constants.SecGrpAssignTypeCode.SELF);
                                // 不存在，就更新维度到 SELF
                                this.secGrpDclDimRepository.updateOptional(childDim, SecGrpDclDim.FIELD_ASSIGN_TYPE_CODE);
                            }
                        } else if (PARENT.equals(childDim.getAssignTypeCode())) {
                            // 判断维度行是否还存在 PARENT 数据
                            if (this.secGrpDclDimLineRepository
                                    .countDimLineByDimIdAndAssignTypeCode(childDim.getSecGrpDclDimId(), PARENT) == 0) {
                                // 不存在，就删除维度
                                this.secGrpDclDimRepository.deleteByPrimaryKey(childDim);
                            }
                        }

                        // 分配维度的数据权限
                        this.assignDcl(childSecGrp, opUpdateAuthTypeCodes);
                        // 回收维度的数据权限
                        this.recycleDcl(childSecGrp, opRecycleAuthTypeCodes);

                        // 回收子类Dim
                        this.recycleChildrenDim(childSecGrp, childDim, opRecycleAuthTypeCodes, parentSecGrpConsumer, null);
                    }
                }, assignedRoles);
    }

    /**
     * 分配数据权限
     *
     * @param secGrp             安全组
     * @param authorityTypeCodes 权限类型码
     */
    private void assignDcl(@Nonnull SecGrp secGrp, @Nonnull Set<String> authorityTypeCodes) {
        // 分配数据权限
        this.operateDcl(secGrp, authorityTypeCodes, (authorityTypeCode, innerSecGrp, dcl, dclLines) -> {
            // 删除原有的数据,重新分配权限(其实在父级已经回收数据,只是在这里去掉回收表的数据和安全组的数据,接下来再进行数据分配,减少业务逻辑复杂度)
            Set<Long> secGrpDclLineIds = dclLines.stream()
                    // 设置行数据为选择
                    .peek(dclLine -> dclLine.setCheckedFlag(1))
                    .map(SecGrpDclLine::getSecGrpDclLineId).collect(Collectors.toSet());
            // 移除数据权限
            this.secGrpDclLineRepository.batchDeleteBySql(secGrp.getSecGrpId(), secGrpDclLineIds);

            // 分配数据权限
            this.dclServiceManager.addSecGrpDclAuthority(authorityTypeCode, innerSecGrp, dcl, dclLines);
        });
    }

    /**
     * 回收数据权限
     *
     * @param secGrp             安全组
     * @param authorityTypeCodes 权限类型码
     */
    private void recycleDcl(@Nonnull SecGrp secGrp, @Nonnull Set<String> authorityTypeCodes) {
        // 回收数据权限
        this.operateDcl(secGrp, authorityTypeCodes, (authorityTypeCode, innerSecGrp, dcl, dclLines) -> {
            // 设置行数据为未选择
            dclLines.forEach(secGrpDclLine -> secGrpDclLine.setCheckedFlag(0));

            // 移除数据权限
            this.dclServiceManager.removeSecGrpDclAuthority(authorityTypeCode, innerSecGrp, dcl, dclLines);
        });
    }

    /**
     * 数据权限操作逻辑
     *
     * @param secGrp             安全组
     * @param authorityTypeCodes 权限类型码
     * @param dclOperate         数据权限操作具体逻辑
     */
    private void operateDcl(@Nonnull SecGrp secGrp, @Nonnull Set<String> authorityTypeCodes, @Nonnull DclOperate dclOperate) {
        if (CollectionUtils.isNotEmpty(authorityTypeCodes)) {
            // 根据维度码和安全组查询数据权限头
            List<SecGrpDcl> secGrpDcls = this.secGrpDclRepository.selectByCondition(Condition.builder(SecGrpDcl.class)
                    .where(Sqls.custom()
                            .andEqualTo(SecGrpDcl.FIELD_SEC_GRP_ID, secGrp.getSecGrpId())
                            .andIn(SecGrpDcl.FIELD_AUTHORITY_TYPE_CODE, authorityTypeCodes)
                    ).build());
            if (CollectionUtils.isNotEmpty(secGrpDcls)) {
                // 处理数据权限数据
                secGrpDcls.forEach((secGrpDcl) -> {
                    // 数据权限行；默认为空集合
                    List<SecGrpDclLine> dclLines = Collections.emptyList();
                    // 本地编码：公司
                    if (Constants.DocLocalAuthorityTypeCode.COMPANY.equals(secGrpDcl.getAuthorityTypeCode())) {
                        // 查询条件对象
                        SecGrpDclQueryDTO queryDTO = new SecGrpDclQueryDTO();
                        // 查询全量数据
                        PageRequest pageRequest = new PageRequest(0, Integer.MAX_VALUE);
                        // 设置安全组ID
                        queryDTO.setSecGrpId(secGrp.getSecGrpId());
                        // 设置权限类型码
                        queryDTO.setAuthorityTypeCode(secGrpDcl.getAuthorityTypeCode());
                        // 查询行数据
                        SecGrpDclDTO secGrpDclDTO = this.dclServiceManager.querySecGrpDclAssignedAuthority(
                                secGrpDcl.getAuthorityTypeCode(), queryDTO, pageRequest);
                        if (Objects.nonNull(secGrpDclDTO)) {
                            // 原始List数据
                            List<CompanyOuInvorgDTO> originList = secGrpDclDTO.getOriginList();
                            if (CollectionUtils.isNotEmpty(originList)) {
                                // 获取结果并转换数据
                                dclLines = CommonConverter.listConverter(SecGrpDclLine.class, originList);
                            }
                        }
                    } else {
                        // 根据维度ID和安全组查询数据权限行
                        dclLines = this.secGrpDclLineRepository.selectByCondition(Condition.builder(SecGrpDclLine.class)
                                .where(Sqls.custom()
                                        .andEqualTo(SecGrpDclLine.FIELD_SEC_GRP_ID, secGrp.getSecGrpId())
                                        .andEqualTo(SecGrpDclLine.FIELD_SEC_GRP_DCL_ID, secGrpDcl.getSecGrpDclId())
                                ).build());
                    }

                    if (CollectionUtils.isNotEmpty(dclLines)) {
                        // 处理数据
                        dclOperate.operate(secGrpDcl.getAuthorityTypeCode(), secGrp, secGrpDcl, dclLines);
                    }
                });
            }
        }
    }


    /**
     * 操作子Dim
     *
     * @param parentSecGrp                               父安全组
     * @param parentDim                                  父安全组Dim
     * @param operateAuthTypeCodes                       处理的权限码
     * @param roleNotIncludedAuthTypeDimObserverConsumer 角色分配的其他安全组中没有包含的维度的父安全组处理逻辑
     * @param roleIncludedAuthTypeChildConsumer          角色分配的其他安全组中包含的维度的子安全组处理逻辑(比如: 父安全组已回收而其他角色分配的安全组中没有的数据权限回收)
     * @param roleNotIncludedAuthTypeChildConsumer       角色分配的其他安全组中没有包含的维度的子安全组处理逻辑(比如：权限回收、处理自己的子安全组数据)
     * @param assignedRoles                              分配的角色
     */
    private void operateChildrenDim(@Nonnull SecGrp parentSecGrp,
                                    @Nonnull SecGrpDclDim parentDim,
                                    @Nonnull Set<String> operateAuthTypeCodes,
                                    @Nonnull DimObserverConsumer roleNotIncludedAuthTypeDimObserverConsumer,
                                    @Nonnull DimConsumer roleIncludedAuthTypeChildConsumer,
                                    @Nonnull DimConsumer roleNotIncludedAuthTypeChildConsumer,
                                    @Nullable List<Role> assignedRoles) {
        // 父安全组ID
        Long parentSecGrpId = parentSecGrp.getSecGrpId();
        // 传入的安全组分配的角色为空，就查询安全组分配的角色
        if (Objects.isNull(assignedRoles)) {
            // 查询安全组分配的角色
            assignedRoles = this.secGrpRepository.listSecGrpAssignedRole(parentSecGrpId);
        }
        if (CollectionUtils.isNotEmpty(assignedRoles)) {
            // 查询角色创建的安全组
            Map<Long, List<SecGrp>> roleCreatedSecGrpMap = this.secGrpRepository.queryRoleCreatedSecGrp(assignedRoles);
            if (MapUtils.isNotEmpty(roleCreatedSecGrpMap)) {
                // 查询角色分配的安全组中,在提供的维度值中存在的维度值
                Map<Long, Set<String>> roleIncludedAuthTypeMap = this.secGrpDclDimLineRepository
                        .queryRoleIncludedAuthType(assignedRoles, parentSecGrpId, parentDim.getAuthDocTypeId(),
                                parentDim.getAuthScopeCode(), operateAuthTypeCodes);
                // 筛选出角色分配的安全中，在提供的维度值中没有的维度值
                Map<Long, Set<String>> roleNotIncludedAuthTypeMap = this.getRoleNotIncludedAuthType(assignedRoles,
                        operateAuthTypeCodes, roleIncludedAuthTypeMap);

                // 角色ID
                Long roleId;
                // 角色创建的安全组
                List<SecGrp> roleCreatedSecGrps;
                // 角色安全组中包含的权限码
                Set<String> includedAuthTypes;
                // 角色安全组中不包含的权限码
                Set<String> notIncludedAuthTypes;
                // 角色安全组中包含的指定dim key -> value === SecGrpId -> SecGrpDclDim
                Map<Long, SecGrpDclDim> roleSecGrpDimMap;
                // 子安全组ID
                Long childSecGrpId;
                // 子类Dim
                SecGrpDclDim childDim;
                for (Role assignedRole : assignedRoles) {
                    // 获取角色ID
                    roleId = assignedRole.getId();

                    // 获取角色分配的安全组中,在提供的维度值中存在的维度值,这部分维度值有额外的处理
                    includedAuthTypes = roleIncludedAuthTypeMap.get(roleId);
                    // 筛选出角色分配的安全组中，其它安全组没有的维度值，这部分维度值需要回收
                    notIncludedAuthTypes = roleNotIncludedAuthTypeMap.get(roleId);

                    // 父安全组处理角色分配的安全组中，在提供的维度值中没有的纬度值
                    roleNotIncludedAuthTypeDimObserverConsumer.operate(parentSecGrp, parentDim, notIncludedAuthTypes,
                            Collections.singletonList(assignedRole));

                    // 获取角色创建的安全组
                    roleCreatedSecGrps = roleCreatedSecGrpMap.get(roleId);
                    if (CollectionUtils.isNotEmpty(roleCreatedSecGrps)) {
                        // 查询角色安全组的dim
                        roleSecGrpDimMap = this.secGrpDclDimRepository.listRoleSecGrpIncludedDim(roleCreatedSecGrps,
                                parentDim.getAuthDocTypeId(), parentDim.getAuthScopeCode());
                        // 处理角色创建的安全组
                        for (SecGrp childSecGrp : roleCreatedSecGrps) {
                            // 获取子安全组ID
                            childSecGrpId = childSecGrp.getSecGrpId();
                            // 获取子安全组Dim
                            childDim = roleSecGrpDimMap.get(childSecGrpId);

                            // 子安全组处理角色分配的安全组中,在提供的维度值中存在的维度值
                            roleIncludedAuthTypeChildConsumer.operate(childSecGrp, childDim, includedAuthTypes);
                            // 子安全组处理角色分配的安全中，在提供的维度值中没有的维度值
                            roleNotIncludedAuthTypeChildConsumer.operate(childSecGrp, childDim, notIncludedAuthTypes);
                        }
                    }
                }
            }
        }
    }

    /**
     * 筛选出角色分配的安全中，在提供的维度值中没有的维度值
     *
     * @param roles                   角色s
     * @param assignAuthTypeCodes     分配的权限类型码
     * @param roleIncludedAuthTypeMap 角色分配的安全组中,在提供的维度值中存在的维度值
     * @return 该角色需回收的维度行 key -> value === roleId -> authTypeCodes
     */
    private Map<Long, Set<String>> getRoleNotIncludedAuthType(@Nonnull List<Role> roles,
                                                              @Nonnull Set<String> assignAuthTypeCodes,
                                                              @Nonnull Map<Long, Set<String>> roleIncludedAuthTypeMap) {
        if (CollectionUtils.isNotEmpty(roles) && CollectionUtils.isNotEmpty(assignAuthTypeCodes)) {
            // 角色未包含的权限码Map
            Map<Long, Set<String>> roleNotIncludedAuthTypeMap = new HashMap<>(roles.size());
            // 角色未包含的权限码
            Set<String> roleNotIncludedAuthType;
            // 处理分组，并返回结果
            for (Role role : roles) {
                // 获取角色包含的权限码
                if (roleIncludedAuthTypeMap.containsKey(role.getId())) {
                    // 筛选角色未包含的权限码
                    roleNotIncludedAuthType = assignAuthTypeCodes.stream().filter(item ->
                            !roleIncludedAuthTypeMap.get(role.getId()).contains(item)).collect(toSet());
                } else {
                    // 角色未包含的权限码为所有
                    roleNotIncludedAuthType = assignAuthTypeCodes;
                }
                // 增加数据
                roleNotIncludedAuthTypeMap.put(role.getId(), roleNotIncludedAuthType);
            }

            // 返回结果
            return roleNotIncludedAuthTypeMap;
        }

        // 返回空Map
        return Collections.emptyMap();
    }

    /**
     * 通过数据权限维度唯一键查询数据库中的数据权限维度信息
     * 如果传入的权限维度唯一键为空，就查询所有
     *
     * @param secGrpId     安全组
     * @param dimUniqueMap 数据权限维度头唯一键
     * @param validDim     校验数据维度头的校验逻辑
     * @param validDimLine 校验数据维度行的校验逻辑
     * @return 数据库数据权限信息
     */
    private DbDimData selectDbDimDataWithDimUniqueKeys(@Nonnull Long secGrpId,
                                                       @Nullable Map<String, List<SecGrpDclDimLine>> dimUniqueMap,
                                                       @Nullable Consumer<List<SecGrpDclDim>> validDim,
                                                       @Nullable Consumer<List<SecGrpDclDimLine>> validDimLine) {
        // 已分配的数据权限维度头
        List<SecGrpDclDim> dbDims;
        // 已分配的数据权限维度行
        List<SecGrpDclDimLine> dbDimLineGroups = null;

        if (MapUtils.isNotEmpty(dimUniqueMap)) {
            // 根据数据权限唯一键查询已分配的数据权限维度头
            dbDims = this.secGrpDclDimRepository.listSecGrpDimByUniqueKeys(secGrpId, dimUniqueMap.keySet());
        } else {
            // 查询所有已分配的数据权限维度头
            dbDims = this.secGrpDclDimRepository.selectBySecGrpId(secGrpId);
        }

        if (CollectionUtils.isNotEmpty(dbDims)) {
            // 校验数据
            if (Objects.nonNull(validDim)) {
                validDim.accept(dbDims);
            }

            // 新建行查询构造器
            Condition.Builder conditionBuilder = Condition.builder(SecGrpDclDimLine.class);
            if (MapUtils.isNotEmpty(dimUniqueMap)) {
                List<SecGrpDclDimLine> secGrpDclDimLines;
                // 生成的sql对象
                Sqls sqls;
                for (SecGrpDclDim dbDim : dbDims) {
                    secGrpDclDimLines = dimUniqueMap.get(
                            String.format(DIM_UNIQUE_KEY_TEMPLATE, dbDim.getAuthDocTypeId(), dbDim.getAuthScopeCode()));
                    // 通用条件sql
                    sqls = Sqls.custom()
                            .andEqualTo(SecGrpDclDimLine.FIELD_SEC_GRP_ID, secGrpId)
                            .andEqualTo(SecGrpDclDimLine.FIELD_SEC_GRP_DCL_DIM_ID, dbDim.getSecGrpDclDimId());
                    if (CollectionUtils.isNotEmpty(secGrpDclDimLines)) {
                        // 限制行
                        sqls.andIn(SecGrpDclDimLine.FIELD_AUTH_TYPE_CODE, secGrpDclDimLines.stream()
                                .map(SecGrpDclDimLine::getAuthTypeCode).collect(toSet()));
                    }

                    // 条件
                    conditionBuilder.orWhere(sqls);
                }
            } else {
                // 条件构建器
                conditionBuilder = Condition.builder(SecGrpDclDimLine.class)
                        .where(
                                Sqls.custom()
                                        .andEqualTo(SecGrpDclDimLine.FIELD_SEC_GRP_ID, secGrpId)
                                        .andIn(SecGrpDclDimLine.FIELD_SEC_GRP_DCL_DIM_ID, dbDims.stream()
                                                .map(SecGrpDclDim::getSecGrpDclDimId).collect(toSet()))
                        );
            }

            // 查询已分配的维度行
            dbDimLineGroups = this.secGrpDclDimLineRepository.selectByCondition(conditionBuilder.build());
            if (Objects.nonNull(validDimLine) && CollectionUtils.isNotEmpty(dbDimLineGroups)) {
                // 校验数据
                validDimLine.accept(dbDimLineGroups);
            }
        }

        // 返回数据对象
        return DbDimData.of(dbDims, dbDimLineGroups);
    }

    /**
     * <p>
     * 安全组权限消费接口
     * </p>
     *
     * @author bo.he02@hand-china.com 2020/03/31 10:05
     */
    @FunctionalInterface
    private interface SecGrpAuthorityConsumer {
        /**
         * 数据权限维度具体操作
         *
         * @param secGrp          安全组
         * @param dbDim           数据库中的当前数据权限维度头
         * @param operateDim      操作的数据权维度头
         * @param dbDimLines      数据库中的当前数据权限维度行
         * @param operateDimLines 操作的数据权限维度行
         */
        void operate(SecGrp secGrp, SecGrpDclDim dbDim, SecGrpDclDim operateDim,
                     List<SecGrpDclDimLine> dbDimLines, List<SecGrpDclDimLine> operateDimLines);
    }

    /**
     * <p>
     * 安全组Dim消费接口
     * </p>
     *
     * @author bo.he02@hand-china.com 2020/03/31 11:03
     */
    @FunctionalInterface
    private interface DimConsumer {
        /**
         * 子安全组Dim操作
         *
         * @param secGrp               安全组
         * @param secGrpDclDim         数据维度头
         * @param notIncludedAuthTypes 需要处理的权限码
         */
        void operate(SecGrp secGrp, SecGrpDclDim secGrpDclDim, Set<String> notIncludedAuthTypes);
    }

    /**
     * <p>
     * Dim的observer操作消费接口
     * </p>
     *
     * @author bo.he02@hand-china.com 2020/04/20 16:35
     */
    @FunctionalInterface
    private interface DimObserverConsumer {
        /**
         * Dim的observer操作
         *
         * @param secGrp               安全组
         * @param secGrpDclDim         数据维度头
         * @param notIncludedAuthTypes 需要处理的权限码
         * @param roles                处理的角色
         */
        void operate(SecGrp secGrp, SecGrpDclDim secGrpDclDim, Set<String> notIncludedAuthTypes, List<Role> roles);
    }

    /**
     * <p>
     * 数据权限处理接口
     * </p>
     *
     * @author bo.he02@hand-china.com 2020/04/13 15:21
     */
    @FunctionalInterface
    private interface DclOperate {
        /**
         * 数据权限处理逻辑
         *
         * @param authorityTypeCode 权限类型码
         * @param secGrp            安全组
         * @param dcl               数据权限头对象
         * @param dclLines          数据权限行对象
         */
        void operate(@Nonnull String authorityTypeCode,
                     @Nonnull SecGrp secGrp,
                     @Nonnull SecGrpDcl dcl,
                     @Nonnull List<SecGrpDclLine> dclLines);
    }

    /**
     * <p>
     * 数据库中数据权限维度数据
     * </p>
     *
     * @author bo.he02@hand-china.com 2020/03/30 16:39
     */
    private static class DbDimData {
        /**
         * 已分配的数据权限维度头
         */
        private final List<SecGrpDclDim> dbDims;
        /**
         * 已分配的数据权限维度头map：key -> value === dimUniqueKey -> dim
         */
        private final Map<String, SecGrpDclDim> dbDimMap;
        /**
         * 已分配的数据权限维度行map：key -> value === dimId -> dimLines
         */
        private final Map<Long, List<SecGrpDclDimLine>> dbDimLineMap;

        /**
         * 构造函数
         *
         * @param dbDims       已分配的数据权限维度头
         * @param dbDimMap     已分配的数据权限维度头map：key -> value === dimUniqueKey -> dim
         * @param dbDimLineMap 已分配的数据权限维度行map：key -> value === dimId -> dimLines
         */
        public DbDimData(List<SecGrpDclDim> dbDims,
                         Map<String, SecGrpDclDim> dbDimMap,
                         Map<Long, List<SecGrpDclDimLine>> dbDimLineMap) {
            this.dbDims = dbDims;
            this.dbDimMap = dbDimMap;
            this.dbDimLineMap = dbDimLineMap;
        }

        /**
         * 静态工厂方法
         *
         * @param dbDims          已分配的数据权限维度头
         * @param dbDimLineGroups 已分配的数据权限维度行
         * @return 数据库中数据权限维度数据
         */
        public static DbDimData of(List<SecGrpDclDim> dbDims, List<SecGrpDclDimLine> dbDimLineGroups) {
            // 已分配的数据权限维度头map：key -> value === dimUniqueKey -> dim
            Map<String, SecGrpDclDim> dbDimMap;
            // 已分配的数据权限维度行map：key -> value === dimId -> dimLines
            Map<Long, List<SecGrpDclDimLine>> dbDimLineMap;

            // 处理数据权限头
            if (CollectionUtils.isEmpty(dbDims)) {
                dbDims = Collections.emptyList();
                dbDimMap = Collections.emptyMap();
            } else {
                // 按维度转成 map
                dbDimMap = dbDims.stream()
                        .collect(toMap(dim -> String.format(DIM_UNIQUE_KEY_TEMPLATE,
                                dim.getAuthDocTypeId(), dim.getAuthScopeCode()), t -> t));
            }

            // 处理数据权限行
            if (Objects.isNull(dbDimLineGroups)) {
                dbDimLineMap = Collections.emptyMap();
            } else {
                // 按照dimId分组
                dbDimLineMap = dbDimLineGroups.stream().collect(groupingBy(SecGrpDclDimLine::getSecGrpDclDimId));
            }

            // 返回数据对象
            return new DbDimData(dbDims, dbDimMap, dbDimLineMap);
        }

        /**
         * 获取所有已分配的数据权限维度头
         *
         * @return 所有已分配的数据权限维度头
         */
        public List<SecGrpDclDim> getDbDims() {
            // 返回数据
            return Collections.unmodifiableList(this.dbDims);
        }

        /**
         * 根据条件获取已分配的数据权限维度头
         *
         * @param authDocTypeId 单据维度
         * @param authScopeCode 维度范围
         * @return 已分配的数据权限维度头
         */
        public SecGrpDclDim getDbDim(Long authDocTypeId, String authScopeCode) {
            // 处理并返回结果
            return this.dbDimMap.get(String.format(DIM_UNIQUE_KEY_TEMPLATE, authDocTypeId, authScopeCode));
        }

        /**
         * 获取已分配的数据权限维度行
         *
         * @param dimId 数据权限维度头ID
         * @return 已分配的数据权限维度行
         */
        public List<SecGrpDclDimLine> getDbDimLine(Long dimId) {
            // 获取数据
            List<SecGrpDclDimLine> secGrpDclDimLines = this.dbDimLineMap.get(dimId);

            // 处理数据并返回结果
            return CollectionUtils.isEmpty(secGrpDclDimLines) ? Collections.emptyList() :
                    Collections.unmodifiableList(secGrpDclDimLines);
        }
    }
}
