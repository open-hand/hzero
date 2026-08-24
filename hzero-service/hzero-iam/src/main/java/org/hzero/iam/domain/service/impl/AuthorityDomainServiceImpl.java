package org.hzero.iam.domain.service.impl;

import java.util.*;
import java.util.stream.Collectors;

import org.hzero.boot.platform.data.permission.util.DocRedisUtils;
import org.hzero.core.util.CommonStream;
import org.hzero.iam.api.dto.CompanyOuInvorgDTO;
import org.hzero.iam.api.dto.CompanyOuInvorgNodeDTO;
import org.hzero.iam.domain.entity.RoleAuthority;
import org.hzero.iam.domain.entity.RoleAuthorityLine;
import org.hzero.iam.domain.entity.UserAuthority;
import org.hzero.iam.domain.repository.RoleAuthorityLineRepository;
import org.hzero.iam.domain.repository.RoleAuthorityRepository;
import org.hzero.iam.domain.repository.UserAuthorityRepository;
import org.hzero.iam.domain.service.AuthorityDomainService;
import org.hzero.iam.infra.constant.Constants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import io.choerodon.core.domain.Page;
import io.choerodon.mybatis.pagehelper.PageHelper;
import io.choerodon.mybatis.pagehelper.domain.PageRequest;
import io.choerodon.mybatis.pagehelper.domain.Sort;

/**
 * 用户角色单据权限通用服务实现类
 *
 * @author xiaoyu.zhao@hand-china.com 2019/07/17 20:04
 */
@Component
public class AuthorityDomainServiceImpl implements AuthorityDomainService {
    private static final Logger LOGGER = LoggerFactory.getLogger(AuthorityDomainServiceImpl.class);
    @Autowired
    private RoleAuthorityRepository roleAuthRepository;
    @Autowired
    private RoleAuthorityLineRepository roleAuthLineRepository;
    @Autowired
    private UserAuthorityRepository userAuthorityRepository;


    @Override
    public List<CompanyOuInvorgDTO> generateTreeDataList(List<CompanyOuInvorgNodeDTO> originList) {
        List<CompanyOuInvorgDTO> treeDataList = new ArrayList<>();
        //将传入的元数据拆分成单个的公司、业务实体、库存组织
        for (CompanyOuInvorgNodeDTO com : originList) {
            if (com.getCompanyId() != null) {
                treeDataList.add(new CompanyOuInvorgDTO(Constants.AUTHORITY_TYPE_CODE.COMPANY + com.getCompanyId(),
                                Constants.AUTHORITY_TYPE_CODE.COMPANY, com.getCompanyId(), com.getCompanyNum(),
                                com.getCompanyName(), null, com.getComCheckedFlag()));
                if (com.getOuId() != null) {
                    treeDataList.add(new CompanyOuInvorgDTO(Constants.AUTHORITY_TYPE_CODE.OU + com.getOuId(),
                                    Constants.AUTHORITY_TYPE_CODE.OU, com.getOuId(), com.getOuCode(), com.getOuName(),
                                    com.getCompanyId(), com.getOuCheckedFlag()));
                    if (com.getOrganizationId() != null) {
                        treeDataList.add(new CompanyOuInvorgDTO(
                                        Constants.AUTHORITY_TYPE_CODE.INVORG + com.getOrganizationId(),
                                        Constants.AUTHORITY_TYPE_CODE.INVORG, com.getOrganizationId(),
                                        com.getOrganizationCode(), com.getOrganizationName(), com.getOuId(),
                                        com.getOrgCheckedFlag()));
                    }
                }
            }
        }
        return treeDataList.stream().filter(CommonStream.distinctByKey(CompanyOuInvorgDTO::getId))
                        .sorted(Comparator.comparing(CompanyOuInvorgDTO::getDataId)).collect(Collectors.toList());
    }

    @Override
    public List<CompanyOuInvorgDTO> buildTreeDataListForSecGrp(List<CompanyOuInvorgNodeDTO> originList) {
        Map<String,CompanyOuInvorgDTO> companyOuInvorgMap = new HashMap<>(8);
        //按照类型进行分组
        Map<String, List<CompanyOuInvorgNodeDTO>> listMap = originList.stream().collect(Collectors.groupingBy(item -> item.getTypeCode()));
        //解析公司
        if(listMap.get(CompanyOuInvorgNodeDTO.COMPANY)!=null){
            for(CompanyOuInvorgNodeDTO node:listMap.get(CompanyOuInvorgNodeDTO.COMPANY)){
                CompanyOuInvorgDTO company = new CompanyOuInvorgDTO(Constants.AUTHORITY_TYPE_CODE.COMPANY + node.getCompanyId(),
                        Constants.AUTHORITY_TYPE_CODE.COMPANY, node.getCompanyId(), node.getCompanyNum(),
                        node.getCompanyName(), null, node.getShieldFlag(),node.getSecGrpDclLineId());
                if(!companyOuInvorgMap.containsKey(company.getId())){
                    companyOuInvorgMap.put(company.getId(),company);
                }
            }
        }
        //解析实体
        if(listMap.get(CompanyOuInvorgNodeDTO.OU)!=null){
            for(CompanyOuInvorgNodeDTO node:listMap.get(CompanyOuInvorgNodeDTO.OU)){
                CompanyOuInvorgDTO company = new CompanyOuInvorgDTO(Constants.AUTHORITY_TYPE_CODE.COMPANY + node.getCompanyId(),
                        Constants.AUTHORITY_TYPE_CODE.COMPANY, node.getCompanyId(), node.getCompanyNum(),
                        node.getCompanyName(), null, null,null);
                if(!companyOuInvorgMap.containsKey(company.getId())){
                    companyOuInvorgMap.put(company.getId(),company);
                }

                CompanyOuInvorgDTO ou = new CompanyOuInvorgDTO(Constants.AUTHORITY_TYPE_CODE.OU + node.getOuId(),
                        Constants.AUTHORITY_TYPE_CODE.OU, node.getOuId(), node.getOuCode(), node.getOuName(),
                        node.getCompanyId(), node.getShieldFlag(), node.getSecGrpDclLineId());
                if(!companyOuInvorgMap.containsKey(ou.getId())){
                    companyOuInvorgMap.put(ou.getId(),ou);
                }
            }
        }
        //解析组织
        if(listMap.get(CompanyOuInvorgNodeDTO.INVORG)!=null){
            for(CompanyOuInvorgNodeDTO node:listMap.get(CompanyOuInvorgNodeDTO.INVORG)){
                CompanyOuInvorgDTO company = new CompanyOuInvorgDTO(Constants.AUTHORITY_TYPE_CODE.COMPANY + node.getCompanyId(),
                        Constants.AUTHORITY_TYPE_CODE.COMPANY, node.getCompanyId(), node.getCompanyNum(),
                        node.getCompanyName(), null, null,null);
                if(!companyOuInvorgMap.containsKey(company.getId())){
                    companyOuInvorgMap.put(company.getId(),company);
                }

                CompanyOuInvorgDTO ou = new CompanyOuInvorgDTO(Constants.AUTHORITY_TYPE_CODE.OU + node.getOuId(),
                        Constants.AUTHORITY_TYPE_CODE.OU, node.getOuId(), node.getOuCode(), node.getOuName(),
                        node.getCompanyId(), null, null);
                if(!companyOuInvorgMap.containsKey(ou.getId())){
                    companyOuInvorgMap.put(ou.getId(),ou);
                }

                CompanyOuInvorgDTO invorg = new CompanyOuInvorgDTO(Constants.AUTHORITY_TYPE_CODE.INVORG + node.getOrganizationId(),
                        Constants.AUTHORITY_TYPE_CODE.INVORG, node.getOrganizationId(),
                        node.getOrganizationCode(), node.getOrganizationName(), node.getOuId(),
                        node.getShieldFlag(), node.getSecGrpDclLineId());
                if(!companyOuInvorgMap.containsKey(invorg.getId())){
                    companyOuInvorgMap.put(invorg.getId(),invorg);
                }
            }
        }


        return new ArrayList<>(companyOuInvorgMap.values());
    }

    @Override
    public void initDocAuthCache() {
        // 依次缓存角色、用户的权限数据
        this.initRoleAuthHeaderCache();
        this.initRoleAuthLineCache();
        this.initUserAuthLineCache();
    }

    /**
     * 初始化角色头权限缓存
     */
    private void initRoleAuthHeaderCache() {
        LOGGER.info("=====================>>start init role authority headers<<=====================");
        int total = 0;
        Page<RoleAuthority> roleAuthorities;
        PageRequest pageRequest = new PageRequest(0, 1000, new Sort(Sort.Direction.ASC, RoleAuthority.FIELD_ROLE_AUTH_ID));
        do {
            roleAuthorities = PageHelper.doPageAndSort(pageRequest, () -> roleAuthRepository.selectDocRoleAuth());
            roleAuthorities.forEach(roleAuthority -> DocRedisUtils.setDocRoleAuthHeaderRedis(roleAuthority.getAuthDocTypeId(), roleAuthority.getAuthScopeCode(), roleAuthority.getRoleId()));
            total += roleAuthorities.size();
            pageRequest.setPage(pageRequest.getPage() + 1);
        } while(!roleAuthorities.isEmpty());
        LOGGER.info("=====================>>success init role authority headers, total is : {}<<=====================", total);
    }

    /**
     * 初始化角色行权限缓存
     */
    private void initRoleAuthLineCache() {
        LOGGER.info("=====================>>start init role authority Lines<<=====================");
        int total = 0;
        Page<RoleAuthorityLine> roleAuthorityLines;
        PageRequest pageRequest = new PageRequest(0, 1000, new Sort(Sort.Direction.ASC, RoleAuthorityLine.FIELD_ROLE_AUTH_LINE_ID));
        do {
            roleAuthorityLines = PageHelper.doPageAndSort(pageRequest, () -> roleAuthLineRepository.selectDocRoleAuthLine());
            roleAuthorityLines.forEach(roleAuthorityLine -> DocRedisUtils.setDocRoleAuthLineRedis(
                    roleAuthorityLine.getDocTypeId(),
                    roleAuthorityLine.getDimensionType(),
                    roleAuthorityLine.getAuthTypeCode(),
                    roleAuthorityLine.getRoleId()));
            total += roleAuthorityLines.size();
            pageRequest.setPage(pageRequest.getPage() + 1);
        } while(!roleAuthorityLines.isEmpty());
        LOGGER.info("=====================>>success init role authority Lines, total is : {}<<=====================", total);
    }

    /**
     * 初始化用户权限缓存
     */
    private void initUserAuthLineCache() {
        LOGGER.info("=====================>>start init user authorities<<=====================");
        int total = 0;
        Page<UserAuthority> userAuthorities;
        PageRequest pageRequest = new PageRequest(0, 1000, new Sort(Sort.Direction.ASC, UserAuthority.FIELD_AUTHORITY_ID));
        do {
            userAuthorities = PageHelper.doPageAndSort(pageRequest, () -> userAuthorityRepository.selectDocUserAuth());
            userAuthorities.forEach(userAuthority -> DocRedisUtils.setDocUserAuthRedis(
                    userAuthority.getTenantId(),
                    userAuthority.getAuthorityTypeCode(),
                    userAuthority.getUserId()));
            total += userAuthorities.size();
            pageRequest.setPage(pageRequest.getPage() + 1);
        } while(!userAuthorities.isEmpty());
        LOGGER.info("=====================>>success init user authorities, total is : {}<<=====================", total);
    }

}
