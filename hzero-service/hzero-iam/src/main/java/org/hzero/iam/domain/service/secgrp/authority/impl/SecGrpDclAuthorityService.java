package org.hzero.iam.domain.service.secgrp.authority.impl;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.hzero.core.base.BaseConstants;
import org.hzero.core.util.CommonStream;
import org.hzero.iam.domain.entity.*;
import org.hzero.iam.domain.repository.SecGrpDclLineRepository;
import org.hzero.iam.domain.repository.SecGrpDclRepository;
import org.hzero.iam.domain.service.secgrp.authority.AbstractSecGrpAuthorityService;
import org.hzero.iam.domain.service.secgrp.authority.dcl.DclServiceManager;
import org.hzero.iam.domain.service.secgrp.enums.SecGrpAuthorityType;
import org.hzero.iam.domain.service.secgrp.observer.dcl.RoleSecGrpDclObserver;
import org.hzero.iam.domain.service.secgrp.observer.dcl.UserSecGrpDclObserver;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.Nonnull;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.toSet;

/**
 * 安全组权限服务——数据权限
 *
 * @author bojiangzhou 2020/02/12
 */
@Component
public class SecGrpDclAuthorityService extends AbstractSecGrpAuthorityService<SecGrpDcl> {

    @Autowired
    private SecGrpDclLineRepository secGrpDclLineRepository;
    @Autowired
    private SecGrpDclRepository secGrpDclRepository;
    /**
     * 数据权限服务管理器
     */
    @Autowired
    private DclServiceManager dclServiceManager;
    @Autowired
    private RoleSecGrpDclObserver roleSecGrpDclObserver;
    @Autowired
    private UserSecGrpDclObserver userSecGrpDclObserver;

    @Override
    public boolean support(@Nonnull SecGrpAuthorityType authorityType) {
        return SecGrpAuthorityType.DCL.equals(authorityType);
    }

    @Override
    public void copySecGrpAuthority(@Nonnull List<SecGrp> sourceSecGrps, @Nonnull SecGrp targetSecGrp) {
        List<Long> secGrpIds = sourceSecGrps.stream().map(SecGrp::getSecGrpId).collect(Collectors.toList());

        List<SecGrpDclLine> dcls = secGrpDclLineRepository.listRoleSecGrpDcl(targetSecGrp.getRoleId(), secGrpIds);

        if (CollectionUtils.isEmpty(dcls)) {
            return;
        }

        Map<String, List<SecGrpDclLine>> map = dcls.parallelStream()
                // 根据 authorityTypeCode、dataId 去重
                .filter(CommonStream.distinctByKey(SecGrpDclLine::buildUniqueKey))
                // 根据 authorityTypeCode 分组
                .collect(groupingBy(SecGrpDclLine::getAuthorityTypeCode));

        map.forEach((headerCode, lines) -> {
            // 插入数据权限头
            SecGrpDcl secGrpDcl = new SecGrpDcl()
                    .setSecGrpId(targetSecGrp.getSecGrpId())
                    .setTenantId(targetSecGrp.getTenantId())
                    .setAuthorityTypeCode(headerCode);

            // 插入头
            secGrpDclRepository.insertSelective(secGrpDcl);

            lines.parallelStream().forEach(line -> {
                line
                        .setSecGrpId(targetSecGrp.getSecGrpId())
                        .setSecGrpDclId(secGrpDcl.getSecGrpDclId())
                        .setSecGrpDclLineId(null);

                secGrpDclLineRepository.insertSelective(line);
            });
        });
    }

    @Override
    public void deleteAuthorityBySecGrpId(@Nonnull Long secGrpId) {
        // 删除行
        secGrpDclLineRepository.delete(new SecGrpDclLine(secGrpId));
        // 删除头
        secGrpDclRepository.delete(new SecGrpDcl(secGrpId));
    }

    @Override
    public void assignUserSecGrpAuthority(@Nonnull User user, @Nonnull SecGrp secGrp) {
        // 处理的用户对象
        List<SecGrpAssign> secGrpAssigns = Collections.singletonList(this.buildSecGrpAssign(user));

        // 分配用户安全组权限
        this.operateSecGrpAuthority(secGrp, (secGrpId, authorityTypeCode, dataIds) ->
                        this.secGrpDclLineRepository.listUserNotIncludeSecGrpDclLine(secGrpId, user.getId(), authorityTypeCode, dataIds),
                (secGrpDcl, secGrpDclLines) -> this.userSecGrpDclObserver.assignUsersDcl(secGrpAssigns, secGrpDcl, secGrpDclLines));
    }

    @Override
    public void recycleUserSecGrpAuthority(@Nonnull User user, @Nonnull SecGrp secGrp) {
        // 处理的用户对象
        List<SecGrpAssign> secGrpAssigns = Collections.singletonList(this.buildSecGrpAssign(user));

        // 回收用户安全组权限
        this.operateSecGrpAuthority(secGrp, (secGrpId, authorityTypeCode, dataIds) ->
                        this.secGrpDclLineRepository.listUserNotIncludeSecGrpDclLine(secGrpId, user.getId(), authorityTypeCode, dataIds),
                (secGrpDcl, secGrpDclLines) -> this.userSecGrpDclObserver.recycleUsersDcl(secGrpAssigns, secGrpDcl, secGrpDclLines));
    }

    @Override
    public void assignRoleSecGrpAuthority(@Nonnull Role role, @Nonnull SecGrp secGrp) {
        // 处理的角色对象
        List<Role> roles = Collections.singletonList(role);

        // 分配角色安全组权限
        this.operateSecGrpAuthority(secGrp, (secGrpId, authorityTypeCode, dataIds) ->
                        this.secGrpDclLineRepository.listUserNotIncludeSecGrpDclLine(secGrpId, role.getId(), authorityTypeCode, dataIds),
                (secGrpDcl, secGrpDclLines) -> this.roleSecGrpDclObserver.assignRolesDcl(roles, secGrpDcl, secGrpDclLines));
    }

    @Override
    public void recycleRoleSecGrpAuthority(@Nonnull Role role, @Nonnull SecGrp secGrp) {
        // 处理的角色对象
        List<Role> roles = Collections.singletonList(role);

        // 分配角色安全组权限
        this.operateSecGrpAuthority(secGrp, (secGrpId, authorityTypeCode, dataIds) ->
                        this.secGrpDclLineRepository.listUserNotIncludeSecGrpDclLine(secGrpId, role.getId(), authorityTypeCode, dataIds),
                (secGrpDcl, secGrpDclLines) -> this.roleSecGrpDclObserver.recycleRolesDcl(roles, secGrpDcl, secGrpDclLines));
    }

    @Override
    protected void addSecGrpAuthority(@Nonnull SecGrp secGrp, @Nonnull List<SecGrpDcl> authorities) {
        for (SecGrpDcl dcl : authorities) {
            // 添加安全组数据权限
            this.dclServiceManager.addSecGrpDclAuthority(dcl.getAuthorityTypeCode(), secGrp, dcl, dcl.getDclLineList());
        }
    }

    @Override
    protected void removeSecGrpAuthority(@Nonnull SecGrp secGrp, @Nonnull List<SecGrpDcl> authorities) {
        for (SecGrpDcl dcl : authorities) {
            // 移除安全组数据权限
            this.dclServiceManager.removeSecGrpDclAuthority(dcl.getAuthorityTypeCode(), secGrp, dcl, dcl.getDclLineList());
        }
    }

    @Override
    protected void enableSecGrpAuthority(@Nonnull SecGrp secGrp) {
        Long secGrpId = secGrp.getSecGrpId();
        // 查询数据权限
        List<SecGrpDcl> secGrpDcls = this.secGrpDclRepository.selectBySecGrpId(secGrpId);
        if (CollectionUtils.isNotEmpty(secGrpDcls)) {
            // 启用数据权限
            for (SecGrpDcl dcl : secGrpDcls) {
                // 启用数据权限
                this.dclServiceManager.enableSecGrpDclAuthority(dcl.getAuthorityTypeCode(), secGrp, dcl);
            }
        }
    }

    @Override
    protected void disableSecGrpAuthority(@Nonnull SecGrp secGrp) {
        Long secGrpId = secGrp.getSecGrpId();
        // 查询数据权限
        List<SecGrpDcl> secGrpDcls = this.secGrpDclRepository.selectBySecGrpId(secGrpId);
        if (CollectionUtils.isNotEmpty(secGrpDcls)) {
            // 禁用数据权限
            for (SecGrpDcl dcl : secGrpDcls) {
                // 禁用数据权限
                this.dclServiceManager.disabledSecGrpDclAuthority(dcl.getAuthorityTypeCode(), secGrp, dcl);
            }
        }
    }

    @Override
    protected List<SecGrpDcl> getShieldRoleAuthority(Set<Long> authorityIds) {
        // 查询字段权限数据
        List<SecGrpDclLine> secGrpDclLines = this.secGrpDclLineRepository
                .selectByIds(StringUtils.join(authorityIds, BaseConstants.Symbol.COMMA));
        if (CollectionUtils.isEmpty(secGrpDclLines)) {
            return null;
        }

        // 按照数据权限头ID进行分组
        Map<Long, List<SecGrpDclLine>> secGrpDclLineMap = secGrpDclLines.stream().collect(groupingBy(SecGrpDclLine::getSecGrpDclId));
        // 查询所有数据权限头
        List<SecGrpDcl> secGrpDcls = this.secGrpDclRepository
                .selectByIds(StringUtils.join(secGrpDclLineMap.keySet(), BaseConstants.Symbol.COMMA));
        if (CollectionUtils.isEmpty(secGrpDcls)) {
            return null;
        }

        // 设置行数据
        for (SecGrpDcl secGrpDcl : secGrpDcls) {
            secGrpDcl.setDclLineList(secGrpDclLineMap.get(secGrpDcl.getSecGrpDclId()));
        }

        // 返回结果
        return secGrpDcls;
    }

    /**
     * 操作安全组权限，主要用于给(角色/用户)(分配/回收)指定安全组的数据权限
     *
     * @param secGrp   权限所在的安全组
     * @param supplier 获取当前实体分配的安全组中，除当前安全组，其他安全组未包含的数据权限行 逻辑
     * @param consumer 权限数据处理逻辑
     */
    private void operateSecGrpAuthority(@Nonnull SecGrp secGrp, @Nonnull NotIncludedDclLineSupplier supplier,
                                        @Nonnull BiConsumer<SecGrpDcl, List<SecGrpDclLine>> consumer) {
        Long secGrpId = secGrp.getSecGrpId();
        // 安全组数据权限行map  key -> value === secGrpDclId -> secGrpDclLines
        Map<Long, List<SecGrpDclLine>> secGrpDclLineMap = Collections.emptyMap();

        // 查询安全组所有数据权限行
        List<SecGrpDclLine> secGrpDclLines = this.secGrpDclLineRepository.select(SecGrpDclLine.FIELD_SEC_GRP_ID, secGrpId);
        if (CollectionUtils.isNotEmpty(secGrpDclLines)) {
            // 按照数据权限头Id进行分组
            secGrpDclLineMap = secGrpDclLines.stream().collect(groupingBy(SecGrpDclLine::getSecGrpDclId));
        }

        // 查询安全组所有数据权限行
        List<SecGrpDcl> secGrpDcls = this.secGrpDclRepository.select(SecGrpDcl.FIELD_SEC_GRP_ID, secGrpId);
        if (CollectionUtils.isNotEmpty(secGrpDcls)) {
            List<SecGrpDclLine> allDclLines;
            List<SecGrpDclLine> notIncludeSecGrpDclLine;
            for (SecGrpDcl secGrpDcl : secGrpDcls) {
                allDclLines = secGrpDclLineMap.get(secGrpDcl.getSecGrpDclId());
                if (CollectionUtils.isNotEmpty(allDclLines)) {
                    // 获取其他安全未包含的权限行
                    notIncludeSecGrpDclLine = supplier.get(secGrpId, secGrpDcl.getAuthorityTypeCode(),
                            allDclLines.stream().map(SecGrpDclLine::getDataId).collect(toSet()));
                } else {
                    notIncludeSecGrpDclLine = Collections.emptyList();
                }

                // 处理权限数据
                consumer.accept(secGrpDcl, notIncludeSecGrpDclLine);
            }
        }
    }

    /**
     * 当前实体分配的安全组中，除当前安全组，其他安全组未包含的数据权限行获取函数式接口
     *
     * @author bo.he02@hand-china.com 2020/04/22
     */
    @FunctionalInterface
    private interface NotIncludedDclLineSupplier {
        /**
         * 获取除当前安全组其他安全组未包含的数据权限行
         *
         * @param secGrpId          安全组ID
         * @param authorityTypeCode 数据权限类型
         * @param dataIds           数据IDs
         * @return 满足条件的数据
         */
        List<SecGrpDclLine> get(Long secGrpId, String authorityTypeCode, Set<Long> dataIds);
    }
}
