package org.hzero.iam.app.service.impl;

import org.hzero.core.base.BaseConstants;
import org.hzero.iam.api.dto.RoleAuthDataDTO;
import org.hzero.iam.api.dto.UserAuthorityDTO;
import org.hzero.iam.app.service.DataAuthManagerService;
import org.hzero.iam.app.service.RoleAuthDataService;
import org.hzero.iam.app.service.UserAuthorityService;
import org.hzero.iam.domain.entity.RoleAuthData;
import org.hzero.iam.domain.entity.UserAuthority;
import org.hzero.iam.domain.repository.RoleAuthDataRepository;
import org.hzero.iam.domain.repository.UserAuthorityRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;

import java.util.List;

/**
 * @author jianbo.li
 * @date 2019/11/15 14:58
 */
@Service
public class DataAuthManagerServiceImpl implements DataAuthManagerService {

    @Autowired
    private RoleAuthDataRepository roleAuthDataRepository;
    @Autowired
    private RoleAuthDataService roleAuthDataService;
    @Autowired
    private UserAuthorityRepository userAuthorityRepository;
    @Autowired
    private UserAuthorityService userAuthorityService;
    @Override
    public RoleAuthDataDTO saveRoleAuthData(Long tenantId, RoleAuthDataDTO roleAuthDataDTO) {

        // 检查头唯一性索引是否存在，如果存在，代表是更新，否则新建
        //      因为同一个头下会有多行、权限维护新建可能只是新建某行
        if (roleAuthDataDTO.getRoleAuthData().getTenantId() == null) {
            roleAuthDataDTO.getRoleAuthData().setTenantId(tenantId);
        }
        RoleAuthData uniqueIndex = new RoleAuthData();
        uniqueIndex.setTenantId(roleAuthDataDTO.getRoleAuthData().getTenantId())
                .setAuthorityTypeCode(roleAuthDataDTO.getRoleAuthData().getAuthorityTypeCode())
                .setRoleId(roleAuthDataDTO.getRoleAuthData().getRoleId());
        RoleAuthData dbRoleAuthData = roleAuthDataRepository.selectOne(uniqueIndex);

        // 将DB中的关键信息赋值
        if (dbRoleAuthData != null) {
            roleAuthDataDTO.getRoleAuthData().setAuthDataId(dbRoleAuthData.getAuthDataId());
            roleAuthDataDTO.getRoleAuthData().setIncludeAllFlag(dbRoleAuthData.getIncludeAllFlag());
            roleAuthDataDTO.getRoleAuthData().setObjectVersionNumber(dbRoleAuthData.getObjectVersionNumber());
        }

        // 处理行上的租户id 与 头上保持一致
        if(!CollectionUtils.isEmpty(roleAuthDataDTO.getRoleAuthDataLineList())){
            roleAuthDataDTO.getRoleAuthDataLineList().forEach(item->item.setTenantId(roleAuthDataDTO.getRoleAuthData().getTenantId()));
        }

        return roleAuthDataService.createRoleAuthDataLine(roleAuthDataDTO);
    }

    @Override
    public List<UserAuthorityDTO> saveUserAuthData(Long tenantId, List<UserAuthorityDTO> userAuthorityDTOList) {
        userAuthorityDTOList.forEach(item -> {
            item.getUserAuthority().setTenantId(tenantId);
            // 通过唯一性索引检查传递进来的头部是否存在，若存在则不用新建 否则 新建
            Assert.notNull(item.getUserAuthority().getUserId(), BaseConstants.ErrorCode.DATA_INVALID);
            Assert.notNull(item.getUserAuthority().getAuthorityTypeCode(), BaseConstants.ErrorCode.DATA_INVALID);

            UserAuthority uniqueIndex = new UserAuthority();
            uniqueIndex.setAuthorityTypeCode(item.getUserAuthority().getAuthorityTypeCode());
            uniqueIndex.setUserId(item.getUserAuthority().getUserId());
            uniqueIndex.setTenantId(item.getUserAuthority().getTenantId());
            UserAuthority dbUserAuthority = userAuthorityRepository.selectOne(uniqueIndex);
            if (dbUserAuthority != null) {
                item.getUserAuthority().setAuthorityId(dbUserAuthority.getAuthorityId());
                item.getUserAuthority().setIncludeAllFlag(dbUserAuthority.getIncludeAllFlag());
                item.getUserAuthority().setObjectVersionNumber(dbUserAuthority.getObjectVersionNumber());
            }
        });
        return userAuthorityService.batchCreateUserAuthority(userAuthorityDTOList);
    }
}
