package org.hzero.plugin.platform.hr.app.service.impl;

import java.io.IOException;
import java.util.Collections;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.hzero.boot.imported.app.service.IDoImportService;
import org.hzero.boot.imported.infra.validator.annotation.ImportService;
import org.hzero.core.base.BaseConstants;
import org.hzero.plugin.platform.hr.api.dto.EmployeeAssignImportDTO;
import org.hzero.plugin.platform.hr.domain.entity.Employee;
import org.hzero.plugin.platform.hr.domain.entity.EmployeeAssign;
import org.hzero.plugin.platform.hr.domain.entity.Position;
import org.hzero.plugin.platform.hr.domain.repository.EmployeeAssignRepository;
import org.hzero.plugin.platform.hr.domain.repository.EmployeeRepository;
import org.hzero.plugin.platform.hr.domain.repository.PositionRepository;
import org.hzero.plugin.platform.hr.domain.service.IEmployeeAssignDomainService;
import org.hzero.plugin.platform.hr.infra.constant.PlatformHrConstants;
import org.springframework.beans.factory.annotation.Autowired;

import com.fasterxml.jackson.databind.ObjectMapper;

import io.choerodon.core.exception.CommonException;
import io.choerodon.core.oauth.DetailsHelper;

/**
 * 员工定义岗位分配导入实现类
 *
 * @author yuqing.zhang@hand-china.com 2020/04/21 14:51
 */
@ImportService(templateCode = PlatformHrConstants.ImportTemplateCode.EMPLOYEE_ASSIGN_TEMP)
public class EmployeeAssignImportServiceImpl implements IDoImportService {

    private ObjectMapper objectMapper;
    private EmployeeRepository employeeRepository;
    private PositionRepository positionRepository;
    private EmployeeAssignRepository employeeAssignRepository;
    private IEmployeeAssignDomainService domainService;

    @Autowired
    public EmployeeAssignImportServiceImpl(ObjectMapper objectMapper, EmployeeRepository employeeRepository,
                    PositionRepository positionRepository, EmployeeAssignRepository employeeAssignRepository,
                    IEmployeeAssignDomainService domainService) {
        this.objectMapper = objectMapper;
        this.employeeRepository = employeeRepository;
        this.positionRepository = positionRepository;
        this.employeeAssignRepository = employeeAssignRepository;
        this.domainService = domainService;
    }

    @Override
    public Boolean doImport(String data) {
        // 转实体
        EmployeeAssignImportDTO employeeAssignImportDTO;
        try {
            employeeAssignImportDTO = objectMapper.readValue(data, EmployeeAssignImportDTO.class);
        } catch (IOException e) {
            throw new CommonException(e);
        }
        final Long tenantId = DetailsHelper.getUserDetails().getTenantId();
        final String employeeNum = employeeAssignImportDTO.getEmployeeNum();
        final String positionCode = employeeAssignImportDTO.getPositionCode();
        final Integer isPrimaryPositionFlag = PlatformHrConstants.EmployeeAssignImport.IS_PRIMARY_POSITION
                        .equals(employeeAssignImportDTO.getPrimaryPositionFlag()) ? BaseConstants.Flag.YES
                                        : BaseConstants.Flag.NO;

        // 查询员工和岗位
        Employee employee = employeeRepository.queryEmployeeByEmployeeNum(tenantId, employeeNum);
        if (employee == null) {
            throw new CommonException(PlatformHrConstants.ErrorCode.EMPLOYEE_NOT_EXIST);
        }
        Position position = positionRepository.queryPositionByCode(positionCode, tenantId);
        if (position == null) {
            throw new CommonException(PlatformHrConstants.ErrorCode.POSITION_NOT_EXIST);
        }

        final Long employeeId = employee.getEmployeeId();
        EmployeeAssign employeeAssign = new EmployeeAssign(employeeId, position.getUnitCompanyId(),
                        position.getUnitId(), position.getPositionId(), BaseConstants.Flag.YES, isPrimaryPositionFlag,
                        tenantId);
        employeeRepository.updateLastUpdateTime(Collections.singletonList(employeeId));
        // 校验关联关系是否唯一
        validateUniqueEmployeeAndPosition(tenantId, position, employeeId);
        // 导入数据为主岗且员工已有主岗时，取消原岗位主岗
        validatePrimaryPositionFlag(tenantId, employeeId, isPrimaryPositionFlag);
        // 插入员工岗位关联到数据库
        employeeAssignRepository.insertSelective(employeeAssign);
        // 校验主岗位是否唯一,如果没有主岗则默认为第一个岗位
        domainService.validatePrimaryPositionFlagRepeat(tenantId, employeeId);
        // 添加缓存，先清除缓存，再插入
        employeeAssignRepository.updateEmpAssignCache(employeeId, tenantId);

        return true;
    }

    /**
     * 校验员工分配岗位唯一性
     *
     * @param tenantId 租户Id
     * @param position 岗位信息
     * @param employeeId 员工Id
     */
    private void validateUniqueEmployeeAndPosition(Long tenantId, Position position, Long employeeId) {
        EmployeeAssign temp = new EmployeeAssign(employeeId, tenantId);
        temp.setPositionId(position.getPositionId());
        int count = employeeAssignRepository.selectCount(temp);
        if (count > 0) {
            throw new CommonException(BaseConstants.ErrorCode.ERROR_CODE_REPEAT);
        }
    }

    /**
     * 导入数据为主岗且员工已有主岗时，取消原岗位主岗
     *
     * @param tenantId 租户Id
     * @param employeeId 员工Id
     * @param currentPrimaryPositionFlag 导入数据是否为主岗
     */
    private void validatePrimaryPositionFlag(Long tenantId, Long employeeId, Integer currentPrimaryPositionFlag) {
        if (BaseConstants.Flag.YES.equals(currentPrimaryPositionFlag)) {
            EmployeeAssign temp =
                            new EmployeeAssign(employeeId, tenantId, BaseConstants.Flag.YES, BaseConstants.Flag.YES);
            List<EmployeeAssign> employeeAssignList = employeeAssignRepository.select(temp);
            if (CollectionUtils.isNotEmpty(employeeAssignList)) {
                employeeAssignList.forEach(
                                employeeAssign -> employeeAssign.setPrimaryPositionFlag(BaseConstants.Flag.NO));
                employeeAssignRepository.batchUpdateOptional(employeeAssignList,
                                EmployeeAssign.FIELD_PRIMARY_POSITION_FLAG);
            }
        }
    }
}
