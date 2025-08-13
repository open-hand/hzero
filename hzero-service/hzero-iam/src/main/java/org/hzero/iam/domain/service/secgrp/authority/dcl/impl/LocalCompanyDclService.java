package org.hzero.iam.domain.service.secgrp.authority.dcl.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import io.choerodon.mybatis.pagehelper.domain.PageRequest;

import org.hzero.core.algorithm.tree.TreeBuilder;
import org.hzero.core.base.BaseConstants;
import org.hzero.iam.api.dto.CompanyOuInvorgDTO;
import org.hzero.iam.api.dto.CompanyOuInvorgNodeDTO;
import org.hzero.iam.api.dto.SecGrpDclDTO;
import org.hzero.iam.api.dto.SecGrpDclQueryDTO;
import org.hzero.iam.domain.entity.DocTypeDimension;
import org.hzero.iam.domain.entity.SecGrp;
import org.hzero.iam.domain.entity.SecGrpDcl;
import org.hzero.iam.domain.entity.SecGrpDclLine;
import org.hzero.iam.domain.repository.SecGrpDclRepository;
import org.hzero.iam.domain.service.AuthorityDomainService;
import org.hzero.iam.domain.service.secgrp.authority.dcl.AbstractLocalDclService;
import org.hzero.iam.domain.service.secgrp.observer.dcl.SecGrpDclObserver;
import org.hzero.iam.infra.constant.Constants;

/**
 * 安全组数据权限服务 —— 本地编码：公司
 *
 * @author bojiangzhou 2020/03/04
 */
@Component
public class LocalCompanyDclService extends AbstractLocalDclService {
    @Autowired
    private SecGrpDclRepository secGrpDclRepository;
    @Autowired
    private AuthorityDomainService authorityDomainService;

    @Override
    public boolean support(@Nonnull String authorityTypeCode, @Nullable DocTypeDimension docTypeDimension) {
        // 判断类型
        return Constants.DocLocalAuthorityTypeCode.INVORG.equals(authorityTypeCode)
                || Constants.DocLocalAuthorityTypeCode.OU.equals(authorityTypeCode)
                || super.support(authorityTypeCode, docTypeDimension);
    }

    @Override
    protected String getAuthorityTypeCode() {
        return Constants.DocLocalAuthorityTypeCode.COMPANY;
    }

    /**
     * 查询公司数据权限 包含所有可勾选的
     */
    @Override
    public SecGrpDclDTO querySecGrpDclAuthority(@Nonnull SecGrpDclQueryDTO queryDTO, @Nonnull PageRequest pageRequest) {
        Integer selfDim = secGrpDclDimRepository.isSelfManagementDim(queryDTO.getSecGrpId(),
                queryDTO.getRoleId(), Constants.DocLocalAuthorityTypeCode.COMPANY) ? 1 : 0;
        // 查询所有的公司、业务实体、库存组织
        List<CompanyOuInvorgNodeDTO> originDataList = secGrpDclRepository.listSecGrpCompanyDcl(queryDTO.getSecGrpId(), selfDim, queryDTO);
        // 装载数据集合，用于构建树
        List<CompanyOuInvorgDTO> treeDataList = authorityDomainService.generateTreeDataList(originDataList);
        List<CompanyOuInvorgDTO> treeList = TreeBuilder.buildTree(treeDataList, CompanyOuInvorgDTO.ROOT_ID, new ArrayList<>(), CompanyOuInvorgDTO.NODE);
        return new SecGrpDclDTO().setOriginList(treeDataList).setTreeList(treeList);
    }

    @Override
    public SecGrpDclDTO querySecGrpDclAssignableAuthority(@Nonnull SecGrpDclQueryDTO queryDTO, @Nonnull PageRequest pageRequest) {
        return new SecGrpDclDTO();
    }

    /**
     * 查询已分配的 需排除回收的
     */
    @Override
    public SecGrpDclDTO querySecGrpDclAssignedAuthority(@Nonnull SecGrpDclQueryDTO queryDTO, @Nonnull PageRequest pageRequest) {
        List<CompanyOuInvorgNodeDTO> companyOuInvorgNodeDtos = secGrpDclRepository.listSecGrpAssignedCompanyDcl(queryDTO.getSecGrpId(), queryDTO);
        List<CompanyOuInvorgDTO> treeDataList = authorityDomainService.buildTreeDataListForSecGrp(companyOuInvorgNodeDtos);
        List<CompanyOuInvorgDTO> treeList = TreeBuilder.buildTree(treeDataList, SecGrpDcl.ROOT_ID, new ArrayList<>(), CompanyOuInvorgDTO.NODE);
        return new SecGrpDclDTO().setOriginList(treeDataList).setTreeList(treeList);
    }

    @Override
    public SecGrpDclDTO queryRoleSecGrpDclAuthority(@Nonnull Long roleId, @Nonnull SecGrpDclQueryDTO queryDTO, @Nonnull PageRequest pageRequest) {
        List<CompanyOuInvorgNodeDTO> companyOuInvorgNodeDtos = secGrpDclRepository.listRoleSecGrpCompanyDcl(roleId, queryDTO.getSecGrpId(), queryDTO);
        List<CompanyOuInvorgDTO> treeDataList = authorityDomainService.buildTreeDataListForSecGrp(companyOuInvorgNodeDtos);
        List<CompanyOuInvorgDTO> treeList = TreeBuilder.buildTree(treeDataList, SecGrpDcl.ROOT_ID, new ArrayList<>(), CompanyOuInvorgDTO.NODE);
        return new SecGrpDclDTO().setOriginList(treeDataList).setTreeList(treeList);
    }

    @Override
    public void addSecGrpDclAuthority(@Nonnull SecGrp secGrp, @Nonnull SecGrpDcl dcl, @Nonnull List<SecGrpDclLine> dclLines) {
        // 公司数据权限 又按照 typeCode 分为三种数据权限 COMPANY、OU、INV_ORGANIZATION
        if (CollectionUtils.isNotEmpty(dclLines)) {
            Long secGrpId = secGrp.getSecGrpId();
            Long tenantId = secGrp.getTenantId();
            Map<Long, SecGrpDcl> dclMap = new HashMap<>(16);
            dclLines.stream()
                    .collect(Collectors.groupingBy(SecGrpDclLine::getTypeCode))
                    .forEach((typeCode, list) -> {
                        if (CollectionUtils.isEmpty(list)) {
                            return;
                        }
                        SecGrpDcl secGrpDcl = secGrpDclRepository.queryOne(secGrpId, typeCode);
                        if (secGrpDcl == null) {
                            secGrpDcl = SecGrpDcl.initFrom(secGrp, typeCode);
                            secGrpDclRepository.insertSelective(secGrpDcl);
                        }
                        dclMap.put(secGrpDcl.getSecGrpDclId(), secGrpDcl);

                        for (SecGrpDclLine item : list) {
                            item.setSecGrpDclId(secGrpDcl.getSecGrpDclId());
                            item.setSecGrpId(secGrpId);
                            item.setTenantId(tenantId);
                        }
                    });

            Map<Integer, List<SecGrpDclLine>> map = dclLines.stream().collect(Collectors.groupingBy(SecGrpDclLine::getCheckedFlag));
            List<SecGrpDclLine> addList = map.get(BaseConstants.Flag.YES);
            List<SecGrpDclLine> removeList = map.get(BaseConstants.Flag.NO);

            if (CollectionUtils.isNotEmpty(addList)) {
                // 插入数据
                secGrpDclLineRepository.batchAdd(addList);

                // 按Dcl头分组
                Map<Long, List<SecGrpDclLine>> dclLineMap = addList.stream().collect(Collectors.groupingBy(SecGrpDclLine::getSecGrpDclId));
                dclLineMap.forEach((dclId, list) -> {
                    SecGrpDcl header = dclMap.get(dclId);

                    // 同步更改
                    for (SecGrpDclObserver dclObserver : dclObservers) {
                        dclObserver.assignSecGrpDcl(secGrp, header, list);
                    }

                    // 取消回收子安全组
                    cancelRevokeChildSecGrpDcl(secGrp, header, list);
                });
            }

            if (CollectionUtils.isNotEmpty(removeList)) {
                // 移除数据权限
                removeList.stream()
                        .collect(Collectors.groupingBy(SecGrpDclLine::getTypeCode))
                        .forEach((typeCode, list) -> {
                            secGrpDclLineRepository.batchRemove(secGrpId, typeCode, list.stream().map(SecGrpDclLine::getDataId).collect(Collectors.toSet()));
                        });

                // 按Dcl头分组
                Map<Long, List<SecGrpDclLine>> dclLineMap = removeList.stream().collect(Collectors.groupingBy(SecGrpDclLine::getSecGrpDclId));
                dclLineMap.forEach((dclId, list) -> {
                    SecGrpDcl header = dclMap.get(dclId);

                    // 同步更改
                    for (SecGrpDclObserver dclObserver : dclObservers) {
                        dclObserver.recycleSecGrpDcl(secGrp, header, list);
                    }

                    // 取消回收子安全组
                    revokeChildSecGrpDcl(secGrp, header, list);
                });
            }
        }
    }

    @Override
    public void removeSecGrpDclAuthority(@Nonnull SecGrp secGrp, @Nonnull SecGrpDcl dcl, @Nonnull List<SecGrpDclLine> dclLines) {
        // 添加和移除的逻辑通过 checkFlag 标识
        this.addSecGrpDclAuthority(secGrp, dcl, dclLines);
    }
}
