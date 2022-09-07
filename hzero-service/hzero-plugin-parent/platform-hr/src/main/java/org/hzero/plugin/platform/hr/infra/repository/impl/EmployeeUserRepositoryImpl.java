package org.hzero.plugin.platform.hr.infra.repository.impl;

import org.apache.commons.collections4.CollectionUtils;
import org.hzero.boot.platform.plugin.hr.EmployeeHelper;
import org.hzero.core.redis.RedisHelper;
import org.hzero.mybatis.base.impl.BaseRepositoryImpl;
import org.hzero.plugin.platform.hr.api.dto.EmployeeUserDTO;
import org.hzero.plugin.platform.hr.api.dto.UserDTO;
import org.hzero.plugin.platform.hr.domain.entity.EmployeeUser;
import org.hzero.plugin.platform.hr.domain.repository.EmployeeUserRepository;
import org.hzero.plugin.platform.hr.infra.mapper.EmployeeUserMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 员工用户关系 资源库实现
 *
 * @author like.zhang@hand-china.com 2018-09-17 11:20:55
 */
@Component
public class EmployeeUserRepositoryImpl extends BaseRepositoryImpl<EmployeeUser> implements EmployeeUserRepository {

    @Autowired
    private EmployeeUserMapper employeeUserMapper;
    @Autowired
    private RedisHelper redisHelper;

    @Override
    public List<EmployeeUserDTO> listByEmployeeId(Long tenantId, Long employeeId) {
        return employeeUserMapper.selectEmployeeUserDTO(tenantId, employeeId);
    }

    @Override
    public List<EmployeeUserDTO> listEmployeeUser(Set<Long> ids) {
        return employeeUserMapper.selectByPrimaryKeys(ids);
    }

    @Override
    public EmployeeUserDTO getEmployee(long userId, long tenantId, Integer enabledFlag) {
        return employeeUserMapper.getEmployee(userId, tenantId, enabledFlag);
    }

    @Override
    public List<EmployeeUserDTO> listEmployee(List<Long> userIdList, long tenantId) {
        return employeeUserMapper.listEmployee(userIdList, tenantId);
    }

    @Override
    public List<EmployeeUserDTO> listByEmployeeNum(Long organizationId, List<String> employeeNums) {
        if (CollectionUtils.isEmpty(employeeNums)) {
            return Collections.emptyList();
        } else {
            List<EmployeeUserDTO> employees = employeeUserMapper.selectEmpUsersByEmpNum(organizationId, employeeNums);
            return employees.stream().distinct().collect(Collectors.toList());
        }
    }

    @Override
    public List<EmployeeUser> batchInsertEmployees(List<EmployeeUser> employeeUsers) {
        batchInsertSelective(employeeUsers);
        // 添加缓存
        EmployeeHelper.setRedisHelper(redisHelper);
        employeeUsers.forEach(employeeUser -> EmployeeHelper.storeEmployeeUser(employeeUser.getUserId(),
                        employeeUser.getTenantId(), employeeUser.getEmployeeId()));
        return employeeUsers;
    }

    @Override
    public List<UserDTO> selectUsersByLoginName(Long organizationId, List<String> loginNames) {
        return employeeUserMapper.selectUsersByLoginName(organizationId, loginNames);
    }

    @Override
    public List<String> getOpenUserIdByIds(List<Long> ids, String thirdPlatformType) {
        return employeeUserMapper.selectOpenUserIdsByUserIds(ids, thirdPlatformType);
    }
}
