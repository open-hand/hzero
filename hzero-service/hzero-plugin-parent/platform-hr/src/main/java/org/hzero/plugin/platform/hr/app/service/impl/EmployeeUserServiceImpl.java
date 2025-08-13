package org.hzero.plugin.platform.hr.app.service.impl;

import org.apache.commons.collections4.CollectionUtils;
import org.hzero.boot.platform.plugin.hr.EmployeeHelper;
import org.hzero.core.redis.RedisHelper;
import org.hzero.plugin.platform.hr.api.dto.EmployeeUserDTO;
import org.hzero.plugin.platform.hr.app.service.EmployeeUserService;
import org.hzero.plugin.platform.hr.domain.entity.EmployeeUser;
import org.hzero.plugin.platform.hr.domain.repository.EmployeeRepository;
import org.hzero.plugin.platform.hr.domain.repository.EmployeeUserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;

/**
 * 员工用户关系应用服务默认实现
 *
 * @author like.zhang@hand-china.com 2018-09-17 11:20:55
 */
@Service
public class EmployeeUserServiceImpl implements EmployeeUserService {

    private EmployeeUserRepository employeeUserRepository;
    private EmployeeRepository employeeRepository;
    private RedisHelper redisHelper;

    @Autowired
    public EmployeeUserServiceImpl(EmployeeUserRepository employeeUserRepository, EmployeeRepository employeeRepository, RedisHelper redisHelper) {
        this.employeeUserRepository = employeeUserRepository;
        this.employeeRepository = employeeRepository;
        this.redisHelper = redisHelper;
    }

    @Override
    public EmployeeUserDTO getEmployee(long userId, long tenantId, Integer enabledFlag) {
        return employeeUserRepository.getEmployee(userId, tenantId, enabledFlag);
    }

    @Override
    public List<EmployeeUserDTO> listEmployee(List<Long> userIdList, long tenantId) {
        return employeeUserRepository.listEmployee(userIdList, tenantId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public List<EmployeeUser> batchInsertEmployees(List<EmployeeUser> employeeUsers) {

        if (CollectionUtils.isNotEmpty(employeeUsers)) {
            employeeUsers.forEach(employeeUser -> {
                employeeUser.validateEmployee(employeeRepository);
                employeeUser.validate(employeeUserRepository);
            });
            employeeUserRepository.batchInsertEmployees(employeeUsers);
        }

        return employeeUsers;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void batchRemoveEmployeeUsers(Long organizationId, List<EmployeeUser> employeeUsers) {
        if (CollectionUtils.isNotEmpty(employeeUsers)) {
            employeeUsers.forEach(employeeUser -> {
                employeeUser.setTenantId(organizationId);
                employeeUserRepository.delete(employeeUser);
                EmployeeHelper.setRedisHelper(redisHelper);
                if (employeeUser.getUserId() == null) {
                    employeeUser = employeeUserRepository.selectByPrimaryKey(employeeUser.getEmployeeId());
                    EmployeeHelper.deleteEmployeeUser(employeeUser.getUserId(), organizationId);
                }
            });
        }
    }

    @Override
    public List<String> getOpenUserIdByIds(List<Long> ids, String thirdPlatformType) {
        if (CollectionUtils.isEmpty(ids)) {
            return Collections.emptyList();
        }
        return employeeUserRepository.getOpenUserIdByIds(ids, thirdPlatformType);
    }
}
