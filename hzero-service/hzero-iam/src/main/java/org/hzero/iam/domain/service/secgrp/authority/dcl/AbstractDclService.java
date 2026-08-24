package org.hzero.iam.domain.service.secgrp.authority.dcl;

import io.choerodon.core.domain.Page;
import io.choerodon.core.exception.CommonException;
import io.choerodon.mybatis.pagehelper.PageHelper;
import io.choerodon.mybatis.pagehelper.domain.PageRequest;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.hzero.common.HZeroConstant;
import org.hzero.core.base.BaseConstants;
import org.hzero.iam.api.dto.SecGrpDclDTO;
import org.hzero.iam.api.dto.SecGrpDclQueryDTO;
import org.hzero.iam.domain.entity.*;
import org.hzero.iam.domain.repository.*;
import org.hzero.iam.domain.service.secgrp.authority.impl.SecGrpDclAuthorityService;
import org.hzero.iam.domain.service.secgrp.enums.SecGrpAuthorityRevokeType;
import org.hzero.iam.domain.service.secgrp.enums.SecGrpAuthorityType;
import org.hzero.iam.domain.service.secgrp.observer.dcl.SecGrpDclObserver;
import org.hzero.iam.infra.common.utils.AssertUtils;
import org.hzero.iam.infra.mapper.SecGrpDclLineMapper;
import org.modelmapper.internal.util.Assert;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.*;
import java.util.function.BiConsumer;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.groupingBy;

/**
 * 安全组数据权限服务抽象实现
 *
 * @author bojiangzhou 2020/03/04
 */
public abstract class AbstractDclService implements DclService {
    /**
     * 日志打印对象
     */
    private static Logger logger = LoggerFactory.getLogger(AbstractDclService.class);

    @Autowired
    protected SecGrpRepository secGrpRepository;
    @Autowired
    protected SecGrpDclRepository secGrpDclRepository;
    @Autowired
    protected SecGrpDclLineRepository secGrpDclLineRepository;
    @Autowired
    protected SecGrpRevokeRepository revokeRepository;
    @Autowired
    protected List<SecGrpDclObserver> dclObservers;
    @Autowired
    protected RoleRepository roleRepository;
    @Autowired
    protected SecGrpDclLineMapper secGrpDclLineMapper;
    @Autowired
    protected SecGrpDclDimRepository secGrpDclDimRepository;
    @Autowired
    private SecGrpDclAuthorityService secGrpDclAuthorityService;

    @Override
    public boolean support(@Nonnull String authorityTypeCode, @Nullable DocTypeDimension docTypeDimension) {
        if (Objects.isNull(docTypeDimension)) {
            throw new CommonException("Doc Type Dimension Not Exists, Dimension Code Is [{0}]", authorityTypeCode);
        }

        // 值来源类型
        String valueSourceType = this.getValueSourceType();
        // 值类型
        String selfAuthorityTypeCode = this.getAuthorityTypeCode();

        // 返回判断结果
        return Objects.nonNull(valueSourceType) && valueSourceType.equals(docTypeDimension.getValueSourceType())
                && Objects.nonNull(selfAuthorityTypeCode) && selfAuthorityTypeCode.equals(authorityTypeCode);
    }

    @Override
    public SecGrpDclDTO querySecGrpDclAuthority(@Nonnull SecGrpDclQueryDTO queryDTO, @Nonnull PageRequest pageRequest) {
        Page<SecGrpDclLine> dclLines = secGrpDclLineRepository.listSecGrpDclLine(queryDTO, pageRequest);
        return new SecGrpDclDTO().setSecGrpDclLineList(dclLines);
    }

    @Override
    public SecGrpDclDTO querySecGrpDclAssignableAuthority(@Nonnull SecGrpDclQueryDTO queryDTO, @Nonnull PageRequest pageRequest) {
        SecGrpDclDTO result = new SecGrpDclDTO();
        Page<SecGrpDclLine> page = PageHelper.doPageAndSort(pageRequest, () -> secGrpDclLineRepository.listSecGrpAssignableDclLine(queryDTO, pageRequest));
        result.setSecGrpDclLineList(page);
        return result;
    }

    @Override
    public SecGrpDclDTO querySecGrpDclAssignedAuthority(@Nonnull SecGrpDclQueryDTO queryDTO, @Nonnull PageRequest pageRequest) {
        Page<SecGrpDclLine> dclLines = secGrpDclLineRepository.listSecGrpDclLine(queryDTO, pageRequest);
        return new SecGrpDclDTO().setSecGrpDclLineList(dclLines);
    }

    @Override
    public SecGrpDclDTO queryRoleSecGrpDclAuthority(@Nonnull Long roleId, @Nonnull SecGrpDclQueryDTO queryDTO, @Nonnull PageRequest pageRequest) {
        SecGrpDclDTO dto = new SecGrpDclDTO();
        Page<SecGrpDclLine> dclLines = secGrpDclLineRepository.listRoleSecGrpDcl(roleId, queryDTO.getSecGrpId(), queryDTO, pageRequest);
        return dto.setSecGrpDclLineList(dclLines);
    }

    @Override
    public void addSecGrpDclAuthority(@Nonnull SecGrp secGrp, @Nonnull SecGrpDcl dcl, @Nonnull List<SecGrpDclLine> dclLines) {
        // 数据权限行唯一性约束
        SecGrpDclLine unique = new SecGrpDclLine();
        unique.setSecGrpDclId(dcl.getSecGrpDclId());

        // 遍历行数据
        dclLines.forEach(item -> {
            // 设置数据ID
            unique.setDataId(item.getDataId());
            // 如果数据不存在就插入数据
            if (this.secGrpDclLineRepository.selectCount(unique) == 0) {
                // 设置参数
                item.setSecGrpDclLineId(null)
                        .setTenantId(secGrp.getTenantId())
                        .setSecGrpId(secGrp.getSecGrpId())
                        .setSecGrpDclId(dcl.getSecGrpDclId())
                        .setAuthorityTypeCode(this.getAuthorityTypeCode());

                this.secGrpDclLineRepository.insertSelective(item);
            }
        });

        for (SecGrpDclObserver dclObserver : dclObservers) {
            dclObserver.assignSecGrpDcl(secGrp, dcl, dclLines);
        }

        cancelRevokeChildSecGrpDcl(secGrp, dcl, dclLines);
    }

    @Override
    public void removeSecGrpDclAuthority(@Nonnull SecGrp secGrp, @Nonnull SecGrpDcl dcl, @Nonnull List<SecGrpDclLine> dclLines) {
        // 移除数据权限
        Set<Long> dataIds = dclLines.stream().map(SecGrpDclLine::getDataId).collect(Collectors.toSet());
        // 移除数据权限
        this.secGrpDclLineRepository.batchRemove(secGrp.getSecGrpId(), this.getAuthorityTypeCode(), dataIds);

        for (SecGrpDclObserver dclObserver : this.dclObservers) {
            dclObserver.recycleSecGrpDcl(secGrp, dcl, dclLines);
        }

        this.revokeChildSecGrpDcl(secGrp, dcl, dclLines);
    }

    /**
     * 启用安全组数据权限
     *
     * @param secGrp 安全组
     * @param dcl    数据权限
     */
    @Override
    public void enableSecGrpDclAuthority(@Nonnull SecGrp secGrp, @Nonnull SecGrpDcl dcl) {
        // 查询数据权限行
        List<SecGrpDclLine> secGrpDclLines = this.secGrpDclLineRepository.selectDclLineByDclId(dcl.getSecGrpDclId());

        // 安全组启用时，向分配了此安全组的对象分配数据权限
        for (SecGrpDclObserver dclObserver : this.dclObservers) {
            dclObserver.assignSecGrpDcl(secGrp, dcl, secGrpDclLines);
        }

        // 取消回收子安全组的数据权限
        this.cancelRevokeChildSecGrpDcl(secGrp, dcl, secGrpDclLines);
    }

    /**
     * 禁用安全组数据权限
     *
     * @param secGrp 安全组
     * @param dcl    数据权限
     */
    @Override
    public void disabledSecGrpDclAuthority(@Nonnull SecGrp secGrp, @Nonnull SecGrpDcl dcl) {
        // 查询数据权限行
        List<SecGrpDclLine> secGrpDclLines = this.secGrpDclLineRepository.selectDclLineByDclId(dcl.getSecGrpDclId());

        // 安全组禁用时，向分配了此安全组的对象回收数据权限
        for (SecGrpDclObserver dclObserver : this.dclObservers) {
            dclObserver.recycleSecGrpDcl(secGrp, dcl, secGrpDclLines);
        }

        // 回收子安全组的数据权限
        this.revokeChildSecGrpDcl(secGrp, dcl, secGrpDclLines);
    }

    @Override
    public void saveSecGrpDclAuthority(@Nonnull Long secGrpId, @Nonnull List<SecGrpDclLine> dclLines) {
        if (CollectionUtils.isEmpty(dclLines)) {
            return;
        }
        SecGrp secGrp = this.secGrpRepository.querySecGrp(secGrpId);

        SecGrpDcl dcl = this.secGrpDclRepository.queryOne(secGrpId, this.getAuthorityTypeCode());
        if (Objects.isNull(dcl)) {
            dcl = SecGrpDcl.initFrom(secGrp, this.getAuthorityTypeCode());
            this.secGrpDclRepository.insertSelective(dcl);
        }

        dcl.setDclLineList(dclLines);

        this.secGrpDclAuthorityService.addSecGrpAuthority(secGrpId, Collections.singletonList(dcl));
    }

    @Override
    public void deleteSecGrpDclAuthority(@Nonnull Long secGrpId, @Nonnull List<SecGrpDclLine> dclLines) {
        if (CollectionUtils.isEmpty(dclLines)) {
            return;
        }

        SecGrpDcl dcl = secGrpDclRepository.queryOne(secGrpId, this.getAuthorityTypeCode());
        Assert.notNull(dcl, BaseConstants.ErrorCode.DATA_NOT_EXISTS);

        dcl.setDclLineList(dclLines);

        this.secGrpDclAuthorityService.removeSecGrpAuthority(secGrpId, Collections.singletonList(dcl));
    }

    @Override
    public void clear() {
        // 如果程序中存在需要额外清理的资源，比如线程变量，请重写该方法。
        logger.debug("If There Are Resources In The Program That Need Extra Cleanup, Such As Thread Variables, Please Override This Method.");
    }

    /**
     * 获取值来源类型，值集：HIAM.DOC_DIMENSION.SOURCE_TYPE
     *
     * @return 值来源类型
     */
    protected abstract String getValueSourceType();

    /**
     * 获取数据权限类型Code
     *
     * @return 数据权限类型Code
     */
    protected abstract String getAuthorityTypeCode();

    /**
     * 回收子安全组的数据权限
     *
     * @param secGrp   安全组
     * @param dcl      安全组数据权限对象
     * @param dclLines 安全组数据权限行对象
     */
    protected void revokeChildSecGrpDcl(@Nonnull SecGrp secGrp, @Nonnull SecGrpDcl dcl, @Nonnull List<SecGrpDclLine> dclLines) {
        // 递归处理回收的子安全组数据权限
        this.processChildSecGrpDcl(secGrp, dcl, dclLines, (childSecGrp, childSecGrpDclLine) -> {
            // 添加到权限屏蔽(回收)
            this.revokeRepository.batchAdd(childSecGrpDclLine.parallelStream().map(item ->
                    SecGrpRevoke.build(childSecGrp.getSecGrpId(), item.getTenantId(),
                            item.getSecGrpDclLineId(), SecGrpAuthorityRevokeType.REVOKE, SecGrpAuthorityType.DCL)
            ).collect(Collectors.toList()));

            // 通知其它变更
            for (SecGrpDclObserver dclObserver : this.dclObservers) {
                dclObserver.recycleSecGrpDcl(childSecGrp, dcl, childSecGrpDclLine);
            }

            this.revokeChildSecGrpDcl(childSecGrp, dcl, childSecGrpDclLine);
        });
    }

    /**
     * 取消回收子安全组数据权限
     *
     * @param secGrp   安全组
     * @param dcl      安全组数据权限对象
     * @param dclLines 安全组数据权限行对象
     */
    protected void cancelRevokeChildSecGrpDcl(@Nonnull SecGrp secGrp, @Nonnull SecGrpDcl dcl, @Nonnull List<SecGrpDclLine> dclLines) {
        // 递归处理取消回收的子安全组数据权限
        this.processChildSecGrpDcl(secGrp, dcl, dclLines, (childSecGrp, childSclLines) -> {
            // 移除被回收的权限
            this.revokeRepository.batchRemove(null, childSecGrp.getSecGrpId(),
                    childSclLines.stream().map(SecGrpDclLine::getSecGrpDclLineId).collect(Collectors.toSet()),
                    SecGrpAuthorityRevokeType.REVOKE, SecGrpAuthorityType.DCL);

            // 通知其它更改
            for (SecGrpDclObserver dclObserver : this.dclObservers) {
                dclObserver.assignSecGrpDcl(childSecGrp, dcl, childSclLines);
            }

            // 向下继续处理
            this.cancelRevokeChildSecGrpDcl(childSecGrp, dcl, childSclLines);
        });
    }

    /**
     * 处理子安全组数据权限
     *
     * @param secGrp   安全组
     * @param dcl      安全组数据权限对象
     * @param dclLines 安全组数据权限行对象
     */
    protected void processChildSecGrpDcl(@Nonnull SecGrp secGrp,
                                         @Nonnull SecGrpDcl dcl,
                                         @Nonnull List<SecGrpDclLine> dclLines,
                                         @Nonnull BiConsumer<SecGrp, List<SecGrpDclLine>> childDclConsumer) {
        if (CollectionUtils.isEmpty(dclLines)) {
            return;
        }

        Long secGrpId = secGrp.getSecGrpId();
        // 查询当前安全组分配的角色
        List<Role> assignedRoles = this.secGrpRepository.listSecGrpAssignedRole(secGrpId);
        if (CollectionUtils.isEmpty(assignedRoles)) {
            return;
        }
        // 查询角色创建的安全组
        Map<Long, List<SecGrp>> roleCreatedSecGrpMap = this.secGrpRepository.queryRoleCreatedSecGrp(assignedRoles);
        if (MapUtils.isEmpty(roleCreatedSecGrpMap)) {
            return;
        }

        // 获取待处理的数据权限
        Set<Long> dataIds = dclLines.stream().map(SecGrpDclLine::getDataId).collect(Collectors.toSet());

        // 角色创建的安全组
        List<SecGrp> roleCreatedSecGrp;
        // 遍历当前安全组分配的角色
        for (Role assignedRole : assignedRoles) {
            // 角色id
            Long roleId = assignedRole.getId();
            // 获取角色创建的安全组
            roleCreatedSecGrp = roleCreatedSecGrpMap.get(roleId);
            if (CollectionUtils.isEmpty(roleCreatedSecGrp)) {
                continue;
            }

            // 查询角色其它父安全组不包含的权限，这部分权限需要分配
            List<SecGrpDclLine> secGrpDclLines = this.secGrpDclLineRepository
                    .listRoleNotIncludeSecGrpDclLine(secGrpId, roleId, dcl.getAuthorityTypeCode(), dataIds);
            if (CollectionUtils.isEmpty(secGrpDclLines)) {
                continue;
            }

            // 按照安全组ID分组
            Map<Long, List<SecGrpDclLine>> secGrpDclLineMap = secGrpDclLines.stream().collect(groupingBy(SecGrpDclLine::getSecGrpId));
            // 遍历处理子安全组
            for (SecGrp childSecGrp : roleCreatedSecGrp) {
                // 处理子安全组权限
                childDclConsumer.accept(childSecGrp, secGrpDclLineMap.get(childSecGrp.getSecGrpId()));
            }
        }
    }

    /**
     * 判断角色是否为超级管理员
     *
     * @param roleId 角色ID
     * @return 判断结果 true 是超级管理员 false 不是超级管理员
     */
    protected boolean isSuperAdmin(@Nonnull Long roleId) {
        // 根据角色ID查询角色
        Role role = roleRepository.selectRoleSimpleById(roleId);
        // 判断查询结果不能为空
        AssertUtils.notNull(role, "hiam.warn.role.notFound");
        // 超级管理员查询可分配的数据权限
        return Objects.equals(HZeroConstant.RoleCode.TENANT, role.getCode()) || Objects.equals(HZeroConstant.RoleCode.SITE, role.getCode());
    }
}
