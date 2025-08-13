package org.hzero.iam.infra.repository.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.collections4.CollectionUtils;
import org.hzero.boot.platform.data.permission.util.DocRedisUtils;
import org.hzero.iam.api.dto.CompanyOuInvorgNodeDTO;
import org.hzero.iam.api.dto.UserAuthorityDTO;
import org.hzero.iam.domain.entity.UserAuthority;
import org.hzero.iam.domain.repository.UserAuthorityRepository;
import org.hzero.iam.infra.mapper.UserAuthorityMapper;
import org.hzero.mybatis.base.impl.BaseRepositoryImpl;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import io.choerodon.core.domain.Page;
import io.choerodon.mybatis.pagehelper.PageHelper;
import io.choerodon.mybatis.pagehelper.domain.PageRequest;

/**
 * 用户权限管理表 资源库实现
 *
 * @author liang.jin@hand-china.com 2018-07-31 15:45:42
 */
@Component
public class UserAuthorityRepositoryImpl extends BaseRepositoryImpl<UserAuthority> implements UserAuthorityRepository {
    @Autowired
    UserAuthorityMapper userAuthorityMapper;

    @Override
    public List<CompanyOuInvorgNodeDTO> listComanyUoInvorg(Long tenantId, Long userId, String dataCode, String dataName) {
        return userAuthorityMapper.listComanyUoInvorg(tenantId, userId, dataCode, dataName);
    }

    @Override
    public List<String> listUserAuthorityTypeCode(Long tenantId, Long userId) {
        return userAuthorityMapper.listUserAuthorityTypeCode(tenantId, userId);
    }

    @Override
    public Page<UserAuthorityDTO> pageDataAssignedUser(Long dataId, String authorityTypeCode, UserAuthority userAuthority, PageRequest pageRequest) {
        // 返回结果
        Page<UserAuthorityDTO> result = new Page<>();

        // 查询并做参数适配主要是配合前端，让前端得到的数据格式与执行更新等操作格式保持一致
        Page<UserAuthority> userAuthorityPage = PageHelper.doPageAndSort(pageRequest, () -> userAuthorityMapper.listDataAssignedUser(dataId, authorityTypeCode, userAuthority));
        if (CollectionUtils.isNotEmpty(userAuthorityPage)) {
            BeanUtils.copyProperties(userAuthorityPage, result);
            result.setContent(userAuthorityPage.getContent().stream()
                    .map(item -> {
                        UserAuthorityDTO userAuthorityDataDTO = new UserAuthorityDTO();
                        userAuthorityDataDTO.setUserAuthority(item);
                        return userAuthorityDataDTO;
                    })
                    .collect(Collectors.toList()));
        }
        return result;
    }

    @Override
    public List<CompanyOuInvorgNodeDTO> listComanyUoInvorgAll(Long tenantId, String dataCode, String dataName) {
        return userAuthorityMapper.listComanyUoInvorgAll(tenantId, dataCode, dataName);
    }

    @Override
    public List<UserAuthority> listUserAuthorityAssignSecGrp(Long tenantId, Long userId, Long secGrpId) {
        if (tenantId == null || userId == null || secGrpId == null) {
            return new ArrayList<>();
        }
        return userAuthorityMapper.listUserAuthorityAssignSecGrp(tenantId, userId, secGrpId);
    }

    @Override
    public List<UserAuthority> listByUserIdAndTenantId(Long userId, Long tenantId) {
        if (userId == null || tenantId == null) {
            return new ArrayList<>();
        }
        return userAuthorityMapper.listByUserIdAndTenantId(userId, tenantId);
    }

    @Override
    public void processUserAuthorityCache(Long tenantId, Long userId, String authorityTypeCode) {
        UserAuthority userAuthority = new UserAuthority();
        userAuthority.setTenantId(tenantId);
        userAuthority.setUserId(userId);
        userAuthority.setAuthorityTypeCode(authorityTypeCode);
        UserAuthority dbUserAuthority = this.selectOne(userAuthority);
        // 由于用户权限头行并存，即数据权限头存在则行必然存在，头不存在则行必不存在，若不存在头时，则直接清除用户权限数据缓存标识
        if (dbUserAuthority == null) {
            DocRedisUtils.delDocUserAuthRedis(tenantId, authorityTypeCode, userId);
        } else {
            DocRedisUtils.setDocUserAuthRedis(tenantId, authorityTypeCode, userId);
        }
    }

    @Override
    public List<UserAuthority> selectDocUserAuth() {
        return userAuthorityMapper.selectDocUserAuth();
    }
}
