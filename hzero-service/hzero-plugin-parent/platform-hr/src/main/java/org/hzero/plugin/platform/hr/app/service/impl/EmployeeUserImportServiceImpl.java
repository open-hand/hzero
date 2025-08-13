package org.hzero.plugin.platform.hr.app.service.impl;

import java.io.IOException;
import java.util.Collections;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.hzero.boot.imported.app.service.IDoImportService;
import org.hzero.boot.imported.infra.validator.annotation.ImportService;
import org.hzero.boot.platform.plugin.hr.EmployeeHelper;
import org.hzero.core.redis.RedisHelper;
import org.hzero.plugin.platform.hr.api.dto.EmployeeUserImportDTO;
import org.hzero.plugin.platform.hr.api.dto.UserDTO;
import org.hzero.plugin.platform.hr.domain.entity.Employee;
import org.hzero.plugin.platform.hr.domain.entity.EmployeeUser;
import org.hzero.plugin.platform.hr.domain.repository.EmployeeRepository;
import org.hzero.plugin.platform.hr.domain.repository.EmployeeUserRepository;
import org.hzero.plugin.platform.hr.infra.constant.PlatformHrConstants;
import org.hzero.plugin.platform.hr.infra.feign.HiamUserRemoteService;
import org.springframework.beans.factory.annotation.Autowired;

import com.fasterxml.jackson.databind.ObjectMapper;

import io.choerodon.core.exception.CommonException;
import io.choerodon.core.oauth.DetailsHelper;

/**
 * 员工定义用户分配导入实现类
 *
 * @author yuqing.zhang@hand-china.com 2020/04/22 9:32
 */
@ImportService(templateCode = PlatformHrConstants.ImportTemplateCode.EMPLOYEE_USER_TEMP)
public class EmployeeUserImportServiceImpl implements IDoImportService {

    private ObjectMapper objectMapper;
    private EmployeeRepository employeeRepository;
    private EmployeeUserRepository employeeUserRepository;
    private RedisHelper redisHelper;

    @Autowired
    public EmployeeUserImportServiceImpl(ObjectMapper objectMapper, EmployeeRepository employeeRepository,
                    HiamUserRemoteService hiamUserRemoteService, EmployeeUserRepository employeeUserRepository,
                    RedisHelper redisHelper) {
        this.objectMapper = objectMapper;
        this.employeeRepository = employeeRepository;
        this.employeeUserRepository = employeeUserRepository;
        this.redisHelper = redisHelper;
    }

    @Override
    public Boolean doImport(String data) {
        // 转实体
        EmployeeUserImportDTO employeeUserImportDTO;
        try {
            employeeUserImportDTO = objectMapper.readValue(data, EmployeeUserImportDTO.class);
        } catch (IOException e) {
            throw new CommonException(e);
        }
        final Long tenantId = DetailsHelper.getUserDetails().getTenantId();
        final String employeeNum = employeeUserImportDTO.getEmployeeNum();
        final String loginName = employeeUserImportDTO.getLoginName();

        // 查询员工和用户
        Employee employee = employeeRepository.queryEmployeeByEmployeeNum(tenantId, employeeNum);
        if (employee == null) {
            throw new CommonException(PlatformHrConstants.ErrorCode.EMPLOYEE_NOT_EXIST);
        }
        List<UserDTO> userDTOs =
                        employeeUserRepository.selectUsersByLoginName(tenantId, Collections.singletonList(loginName));
        if (CollectionUtils.isEmpty(userDTOs)) {
            throw new CommonException(PlatformHrConstants.ErrorCode.USER_NOT_EXISTS_OR_ALREADY_LINKED);
        }
        UserDTO userDTO = userDTOs.get(0);

        final Long employeeId = employee.getEmployeeId();
        final Long userId = userDTO.getId();

        EmployeeUser employeeUser = getEmployeeUser(tenantId, employeeId, userId);
        // 校验员工是否存在和关联关系唯一性
        employeeUser.validateEmployee(employeeRepository);
        employeeUser.validate(employeeUserRepository);
        // 插入员工用户关联到数据库
        employeeUserRepository.insertSelective(employeeUser);
        // 添加缓存
        EmployeeHelper.setRedisHelper(redisHelper);
        EmployeeHelper.storeEmployeeUser(userId, tenantId, employeeId);

        return true;
    }

    /**
     * 构造EmployeeUser对象
     * 
     * @param tenantId 租户ID
     * @param employeeId 员工ID
     * @param userId 用户ID
     * @return EmployeeUser对象
     */
    private EmployeeUser getEmployeeUser(Long tenantId, Long employeeId, Long userId) {
        EmployeeUser employeeUser = new EmployeeUser();
        employeeUser.setTenantId(tenantId);
        employeeUser.setEmployeeId(employeeId);
        employeeUser.setUserId(userId);
        return employeeUser;
    }
}
