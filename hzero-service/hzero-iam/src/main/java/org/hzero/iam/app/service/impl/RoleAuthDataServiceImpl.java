package org.hzero.iam.app.service.impl;

import java.util.*;
import java.util.stream.Collectors;

import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.hzero.core.algorithm.tree.TreeBuilder;
import org.hzero.core.base.BaseConstants;
import org.hzero.iam.api.dto.CompanyOuInvorgDTO;
import org.hzero.iam.api.dto.CompanyOuInvorgNodeDTO;
import org.hzero.iam.api.dto.ResponseCompanyOuInvorgDTO;
import org.hzero.iam.api.dto.RoleAuthDataDTO;
import org.hzero.iam.app.service.RoleAuthDataLineService;
import org.hzero.iam.app.service.RoleAuthDataService;
import org.hzero.iam.domain.entity.RoleAuthData;
import org.hzero.iam.domain.entity.RoleAuthDataLine;
import org.hzero.iam.domain.repository.RoleAuthDataLineRepository;
import org.hzero.iam.domain.repository.RoleAuthDataRepository;
import org.hzero.iam.domain.repository.RoleAuthorityRepository;
import org.hzero.iam.domain.service.AuthorityDomainService;
import org.hzero.iam.domain.service.role.AbstractAuthorityCommonService;
import org.hzero.iam.infra.constant.Constants;
import org.hzero.iam.infra.constant.HiamError;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;

import io.choerodon.core.exception.CommonException;
import io.choerodon.mybatis.pagehelper.domain.PageRequest;

/**
 * 角色单据权限管理应用服务默认实现
 *
 * @author qingsheng.chen@hand-china.com 2019-06-14 11:49:29
 */
@Service
public class RoleAuthDataServiceImpl extends AbstractAuthorityCommonService implements RoleAuthDataService {
    public static final Logger logger = LoggerFactory.getLogger(RoleAuthDataServiceImpl.class);
    private static final String ROOT_ID = "-1";
    private RoleAuthDataRepository roleAuthDataRepository;
    private RoleAuthDataLineService roleAuthDataLineService;
    private RoleAuthDataLineRepository dataLineRepository;
    private AuthorityDomainService authorityDomainService;
    private RoleAuthorityRepository roleAuthorityRepository;

    @Autowired
    public RoleAuthDataServiceImpl(RoleAuthDataRepository roleAuthDataRepository,
                                   RoleAuthDataLineService roleAuthDataLineService, AuthorityDomainService authorityDomainService,
                                   RoleAuthorityRepository roleAuthorityRepository, RoleAuthDataLineRepository dataLineRepository) {
        this.roleAuthDataRepository = roleAuthDataRepository;
        this.roleAuthDataLineService = roleAuthDataLineService;
        this.authorityDomainService = authorityDomainService;
        this.roleAuthorityRepository = roleAuthorityRepository;
        this.dataLineRepository = dataLineRepository;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public RoleAuthDataDTO pageRoleAuthDataLine(Long tenantId, Long roleId, String authorityTypeCode, String dataCode, String dataName, PageRequest pageRequest) {
        RoleAuthData roleAuthData = queryRoleAuthData(tenantId, roleId, authorityTypeCode);
        return new RoleAuthDataDTO()
                .setRoleAuthData(roleAuthData)
                .setRoleAuthDataLineList(roleAuthDataLineService.pageRoleAuthDataLine(roleAuthData, dataCode, dataName, pageRequest));
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public RoleAuthDataDTO createRoleAuthDataLine(RoleAuthDataDTO roleAuthData) {
        if (roleAuthData.getRoleAuthData().getAuthDataId() != null) {
            roleAuthDataRepository.updateOptional(roleAuthData.getRoleAuthData(), RoleAuthData.FIELD_INCLUDE_ALL_FLAG);
        } else {
//            roleAuthDataRepository.insertSelective(roleAuthData.getRoleAuthData());
            saveDefaultRoleAuthData(roleAuthData.getRoleAuthData());
        }
        RoleAuthDataDTO roleAuthDataLine = roleAuthDataLineService.createRoleAuthDataLine(roleAuthData);
        return roleAuthDataLine;
    }

    @Override
    public ResponseCompanyOuInvorgDTO treeRoleAuthority(Long tenantId, Long roleId, String dataCode, String dataName) {
        //查询所有的公司、业务实体、库存组织
        List<CompanyOuInvorgNodeDTO> originDataList = roleAuthDataRepository.listCompanyUoInvorg(tenantId, roleId, dataCode, dataName);
        // 装载数据集合，用于构建树
        List<CompanyOuInvorgDTO> treeDataList = authorityDomainService.generateTreeDataList(originDataList);
        List<CompanyOuInvorgDTO> treeList = TreeBuilder.buildTree(treeDataList, ROOT_ID, new ArrayList<>(256), CompanyOuInvorgDTO.NODE);
        return new ResponseCompanyOuInvorgDTO(treeDataList, treeList);
    }

    @Override
    public List<CompanyOuInvorgDTO> createRoleAuthority(Long tenantId, Long roleId, List<CompanyOuInvorgDTO> companyOuInvorgDTOList) {
        if (companyOuInvorgDTOList == null) {
            companyOuInvorgDTOList = Collections.emptyList();
        }
        // 新增集
        List<RoleAuthDataLine> roleAuthDataLineList = new ArrayList<>(256);
        // 删除集
        List<RoleAuthDataLine> delRoleAuthDataLineList = new ArrayList<>();
        companyOuInvorgDTOList
                .stream()
                .collect(Collectors.groupingBy(CompanyOuInvorgDTO::getTypeCode))
                .forEach((typeCode, list) -> {
                    if (CollectionUtils.isEmpty(list)) {
                        return;
                    }
                    RoleAuthData roleAuthData = roleAuthDataRepository.selectOne(new RoleAuthData().setAuthorityTypeCode(typeCode).setRoleId(roleId).setTenantId(tenantId));
                    if (roleAuthData == null) {
                        logger.debug("Error data : type={},role={},tenant={}", typeCode, roleId, tenantId);
                        roleAuthData = new RoleAuthData().setAuthorityTypeCode(typeCode).setRoleId(roleId).setTenantId(tenantId).setIncludeAllFlag(BaseConstants.Flag.NO);
                        saveDefaultRoleAuthData(roleAuthData);
                    } else if (!roleAuthData.getDataSource().contains(Constants.SecGrpAssign.DEFAULT_DATA_SOURCE)){
                        // 此时查询出的为安全组的roleAuthData，将默认数据来源更新进去
                        roleAuthData.setDataSource(StringUtils.join(Constants.SecGrpAssign.DEFAULT_DATA_SOURCE, ",", roleAuthData.getDataSource()));
                        // 更新为DEFAULT,SEC_GRP
                        roleAuthDataRepository.updateOptional(roleAuthData, RoleAuthData.FIELD_DATASOURCE);
                    }
                    processRoleAuthDataLine(roleAuthData, tenantId, list, roleAuthDataLineList, delRoleAuthDataLineList);
                    //roleAuthDataLineList.addAll(processRoleAuthDataLine(roleAuthData.getAuthDataId(), tenantId, list));
                });
        batchSaveDefaultRoleAuthDataLine(roleAuthDataLineList);
        // 删除角色数据权限行
        this.delRoleAuthDataLine(delRoleAuthDataLineList);
        return companyOuInvorgDTOList;
    }

    @Override
    public List<CompanyOuInvorgDTO> createAuthorityForRoles(Long tenantId, List<CompanyOuInvorgDTO> companyOuInvorgDTOList) {
        // 按角色分组
        companyOuInvorgDTOList.stream()
                .collect(Collectors.groupingBy(CompanyOuInvorgDTO::getRoleId))
                .forEach((roleId, list) -> {
                            createRoleAuthority(tenantId, roleId, list);
                        }
                );
        return companyOuInvorgDTOList;
    }

    @Override
    public void copyRoleAuthority(Long organizationId, Long roleId, List<Long> copyRoleIdList) {
        // 获取原有角色的单据维度
        if (CollectionUtils.isEmpty(copyRoleIdList)) {
            throw new CommonException(HiamError.ErrorCode.COPY_ROLE_LIST_NOT_NULL);
        }
        for (Long copyRoleId : copyRoleIdList) {
            // 获取与源单据维度匹配的目标维度
            List<String> compDocTypes = roleAuthorityRepository.selectCompareDimensions(roleId, copyRoleId);
            // 判断被复制的角色的单据维度下是否存在
            if (CollectionUtils.isEmpty(compDocTypes)) {
                throw new CommonException(HiamError.ErrorCode.DOC_TYPE_DIMENSIONS_EMPTY);
            }
            // 存在匹配的权限维度，将匹配维度的权限进行复制, 先判断权限维度头，再复制权限维度行
            for (String compDocType : compDocTypes) {
                // 查询权限维度头信息
                processCopyRoleAuthData(organizationId, roleId, copyRoleId, compDocType);
                if (compDocType.equals(Constants.AUTHORITY_TYPE_CODE.COMPANY)) {
                    // 公司维度需额外处理业务实体，库存组织维度的数据
                    processCopyRoleAuthData(organizationId, roleId, copyRoleId, Constants.AUTHORITY_TYPE_CODE.OU);
                    processCopyRoleAuthData(organizationId, roleId, copyRoleId, Constants.AUTHORITY_TYPE_CODE.INVORG);
                }
            }
        }
    }

    /**
     * 处理角色权限数据行
     *
     * @param roleAuthData              角色权限头
     * @param tenantId                  租户Id
     * @param companyOuInvorgList       数据集合，判断checkedFlag 为0添加进删除集 为1添加进新增集
     * @param roleAuthDataLineList      新增集
     * @param delRoleAuthDataLineList   删除集
     */
    private void processRoleAuthDataLine(RoleAuthData roleAuthData, Long tenantId, List<CompanyOuInvorgDTO> companyOuInvorgList,
            List<RoleAuthDataLine> roleAuthDataLineList, List<RoleAuthDataLine> delRoleAuthDataLineList) {
        companyOuInvorgList.forEach(item ->{
            // 保证数据Id不为空，防止误删除数据问题
            Assert.notNull(item.getDataId(), BaseConstants.ErrorCode.DATA_INVALID);
            if (BaseConstants.Flag.YES.equals(item.getCheckedFlag())) {
                // 新增集
                roleAuthDataLineList.add(new RoleAuthDataLine()
                        .setAuthDataId(roleAuthData.getAuthDataId())
                        .setTenantId(tenantId)
                        .setDataId(item.getDataId())
                        .setDataCode(item.getDataCode())
                        .setDataName(item.getDataName()));
            } else {
                // 删除集，需判断是否为公司/业务实体层级，若为这两个层级则需删除下面的子孙级数据
                Map<String, List<RoleAuthData>> roleAuthDataMap = roleAuthDataRepository
                        .select(new RoleAuthData().setTenantId(tenantId).setRoleId(roleAuthData.getRoleId())).stream()
                        .collect(Collectors.groupingBy(RoleAuthData::getAuthorityTypeCode));
                if (MapUtils.isEmpty(roleAuthDataMap)) {
                    return;
                }
                if (roleAuthData.getAuthorityTypeCode().equals(Constants.AUTHORITY_TYPE_CODE.COMPANY)
                        && roleAuthDataMap.containsKey(Constants.AUTHORITY_TYPE_CODE.OU)) {
                    // 设置ou删除集，并查找需删除的库存组织信息
                    RoleAuthData ouRoleAuthData = roleAuthDataMap.get(Constants.AUTHORITY_TYPE_CODE.OU).get(0);
                    List<Long> ouIds = this.processOuDelList(ouRoleAuthData, item.getDataId(), delRoleAuthDataLineList);
                    if (roleAuthDataMap.containsKey(Constants.AUTHORITY_TYPE_CODE.INVORG) && !CollectionUtils.isEmpty(ouIds)) {
                        // 设置库存组织删除集
                        RoleAuthData invRoleAuthData = roleAuthDataMap.get(Constants.AUTHORITY_TYPE_CODE.INVORG).get(0);
                        this.processInvDelList(invRoleAuthData, ouIds, delRoleAuthDataLineList);
                    }
                } else if (roleAuthData.getAuthorityTypeCode().equals(Constants.AUTHORITY_TYPE_CODE.OU)
                        && roleAuthDataMap.containsKey(Constants.AUTHORITY_TYPE_CODE.INVORG)) {
                    // 设置库存组织删除集
                    RoleAuthData invRoleAuthData = roleAuthDataMap.get(Constants.AUTHORITY_TYPE_CODE.INVORG).get(0);
                    List<Long> ouIds = new ArrayList<>();
                    ouIds.add(item.getDataId());
                    this.processInvDelList(invRoleAuthData, ouIds, delRoleAuthDataLineList);
                }
                delRoleAuthDataLineList.add(new RoleAuthDataLine().setAuthDataId(roleAuthData.getAuthDataId()).setDataId(item.getDataId()));
            }
        });
    }

    private RoleAuthData queryRoleAuthData(Long tenantId, Long roleId, String authorityTypeCode) {
        RoleAuthData roleAuthData = new RoleAuthData().setTenantId(tenantId).setRoleId(roleId).setAuthorityTypeCode(authorityTypeCode);
        return Optional.ofNullable(roleAuthDataRepository.select(tenantId, roleId, authorityTypeCode))
                .orElse(roleAuthData.setIncludeAllFlag(BaseConstants.Flag.NO));
    }

    private List<Long> processOuDelList(RoleAuthData ouRoleAuthData, Long companyId, List<RoleAuthDataLine> delList) {
        List<Long> ouIds = roleAuthDataLineRepository.selectCompanyAssignOu(ouRoleAuthData.getRoleId(), ouRoleAuthData.getTenantId(), companyId);
        if (!CollectionUtils.isEmpty(ouIds)) {
            ouIds.forEach(ouId -> delList.add(new RoleAuthDataLine().setAuthDataId(ouRoleAuthData.getAuthDataId()).setDataId(ouId)));
        }
        return ouIds;
    }

    private void processInvDelList(RoleAuthData invRoleAuthData, List<Long> ouIds, List<RoleAuthDataLine> delList) {
        List<Long> invIds = roleAuthDataLineRepository.selectOuAssignInvOrg(invRoleAuthData.getRoleId(), invRoleAuthData.getTenantId(), ouIds);
        if (!CollectionUtils.isEmpty(invIds)) {
            invIds.forEach(invId -> delList.add(new RoleAuthDataLine().setAuthDataId(invRoleAuthData.getAuthDataId()).setDataId(invId)));
        }
    }

    private void delRoleAuthDataLine(List<RoleAuthDataLine> delRoleAuthDataLineList) {
        if (CollectionUtils.isEmpty(delRoleAuthDataLineList)) {
            return;
        }
        roleAuthDataLineService.deleteRoleAuthDataLine(delRoleAuthDataLineList);
    }

    private void processCopyRoleAuthData(Long tenantId, Long roleId, Long copyRoleId, String authTypeCode) {
        Long authDataId =
                roleAuthDataRepository.selectRoleAuthDataId(tenantId, roleId, copyRoleId, authTypeCode);
        if (authDataId != null) {
            // 获取源角色权限数据:排除目标角色已经存在的权限数据
            List<RoleAuthDataLine> roleAuthDataLines =
                    dataLineRepository.selectCompliantRoleAuthDatas(tenantId, roleId, copyRoleId, authTypeCode);
            if (!CollectionUtils.isEmpty(roleAuthDataLines)) {
                // 替换authDataId
                roleAuthDataLines.forEach(roleAuthDataLine -> roleAuthDataLine.setAuthDataId(authDataId));
                //                        dataLineRepository.batchInsertSelective(roleAuthDataLines);
                batchSaveDefaultRoleAuthDataLine(roleAuthDataLines);
            }
        }
    }
}
