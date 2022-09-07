package org.hzero.plugin.platform.hr.app.service.impl;

import java.util.Collections;
import java.util.List;
import java.util.Objects;

import org.apache.commons.collections4.CollectionUtils;
import org.hzero.core.base.BaseConstants;
import org.hzero.plugin.platform.hr.api.dto.EmployeeAssignDTO;
import org.hzero.plugin.platform.hr.app.service.EmployeeAssignService;
import org.hzero.plugin.platform.hr.domain.entity.Employee;
import org.hzero.plugin.platform.hr.domain.entity.EmployeeAssign;
import org.hzero.plugin.platform.hr.domain.entity.Position;
import org.hzero.plugin.platform.hr.domain.repository.EmployeeAssignRepository;
import org.hzero.plugin.platform.hr.domain.repository.EmployeeRepository;
import org.hzero.plugin.platform.hr.domain.repository.PositionRepository;
import org.hzero.plugin.platform.hr.domain.service.IEmployeeAssignDomainService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import io.choerodon.core.domain.Page;
import io.choerodon.core.exception.CommonException;
import io.choerodon.core.oauth.CustomUserDetails;
import io.choerodon.core.oauth.DetailsHelper;
import io.choerodon.mybatis.pagehelper.domain.PageRequest;

/**
 * 员工岗位分配表应用服务默认实现
 *
 * @author liang.jin@hand-china.com 2018-06-20 15:20:44
 */
@Service
public class EmployeeAssignServiceImpl implements EmployeeAssignService {
    private EmployeeAssignRepository employeeAssignRepository;
    private EmployeeRepository employeeRepository;
    private IEmployeeAssignDomainService employeeAssignDomainService;
    private PositionRepository positionRepository;

    @Autowired
    public EmployeeAssignServiceImpl(EmployeeAssignRepository employeeAssignRepository,
                                     IEmployeeAssignDomainService employeeAssignDomainService,
                                     EmployeeRepository employeeRepository,
                                     PositionRepository positionRepository) {
        this.employeeAssignRepository = employeeAssignRepository;
        this.employeeAssignDomainService = employeeAssignDomainService;
        this.employeeRepository = employeeRepository;
        this.positionRepository = positionRepository;
    }

    @Override
    public List<EmployeeAssignDTO> listEmployeeAssign(long tenantId, long employeeId) {
        return employeeAssignRepository.selectEmployeeAssign(tenantId, employeeId);
    }

    @Override
    public List<EmployeeAssignDTO> listEmployeeAssign(Long tenantId, Integer primaryPositionFlag, Long userId, String language) {
        if(Objects.isNull(userId) && Objects.isNull(language)) {
            CustomUserDetails customUserDetails = DetailsHelper.getUserDetails();
            userId = customUserDetails.getUserId();
            language = customUserDetails.getLanguage();
        }
        return employeeAssignRepository.selectEmployeeAssign(language,
                tenantId, primaryPositionFlag, userId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void update(EmployeeAssign employeeAssign) {
        Assert.notNull(employeeAssign.getEmployeeAssignId(), BaseConstants.ErrorCode.DATA_INVALID);
        EmployeeAssign entity = employeeAssignRepository.selectByPrimaryKey(employeeAssign.getEmployeeAssignId());
        Assert.notNull(entity, BaseConstants.ErrorCode.DATA_NOT_EXISTS);
        employeeAssignRepository.update(employeeAssign);
        employeeRepository.updateLastUpdateTime(Collections.singletonList(employeeAssign.getEmployeeId()));
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void remove(Long employeeAssignId) {
        EmployeeAssign employeeAssign = employeeAssignRepository.selectByPrimaryKey(employeeAssignId);
        Assert.notNull(employeeAssign, BaseConstants.ErrorCode.DATA_NOT_EXISTS);
        employeeAssignRepository.remove(employeeAssignId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateEmployeeAssign(Long tenantId, Long employeeId, List<EmployeeAssign> assignList) {
        employeeRepository.updateLastUpdateTime(Collections.singletonList(employeeId));
        if (CollectionUtils.isNotEmpty(assignList)) {
            // 1.为所有的岗位分配设置公司的ID
            for (EmployeeAssign employeeAssign : assignList) {
                employeeAssign.setUnitCompanyId(getUnitCompanyId(employeeAssign.getPositionId()));
            }
            // 2.根据租户Id和employeeId删除所有当前员工下分配的岗位信息
            EmployeeAssign deleteEmployeeAssign = new EmployeeAssign(employeeId, tenantId);
            employeeAssignRepository.batchDeleteByPrimaryKey(employeeAssignRepository.select(deleteEmployeeAssign));
            // 3.将当前传入的参数插入数据库
            employeeAssignRepository.batchInsertSelective(assignList);
            // 4.校验主岗位是否唯一,如果没有主岗则默认为第一个岗位
            employeeAssignDomainService.validatePrimaryPositionFlagRepeat(tenantId, employeeId);
            // 5.添加缓存，先清除缓存，再插入
            employeeAssignRepository.updateEmpAssignCache(employeeId, tenantId);
        } else {
            // 1.经删除后该员工下无岗位分配信息，需要查询出所有该员工分配的岗位信息然后删除
            List<EmployeeAssign> removeList = employeeAssignRepository.select(new EmployeeAssign(employeeId, tenantId));

            //2.根据参数中包含的employeeAssignId批量删除所有岗位
            employeeAssignRepository.batchDeleteByPrimaryKey(removeList);

            // 3. 删除缓存
            employeeAssignRepository.removeEmpAssignCache(employeeId, tenantId);

        }
    }


    @Override
    public Page<Employee> employeeNotInPosition(Long tenantId, String employeeNum, String name, Long unitId, Long positionId, PageRequest pageRequest) {
        return employeeAssignDomainService.employeeNotInPosition(tenantId, employeeNum, name, unitId, positionId, pageRequest);
    }

    @Override
    public Page<Employee> employeeInPosition(Long tenantId, String employeeNum, String name, Long unitId, Long positionId, PageRequest pageRequest) {
        return employeeAssignDomainService.employeeInPosition(tenantId, employeeNum, name, unitId, positionId, pageRequest);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void batchCreateEmployee(Long tenantId, Long unitId, Long positionId, List<Long> employeeIdList) {
        employeeRepository.updateLastUpdateTime(employeeIdList);
        if (!employeeIdList.isEmpty()) {
            Long unitCompanyId = getUnitCompanyId(positionId);
            for (Long employeeId : employeeIdList) {
                employeeAssignRepository.insertSelective(new EmployeeAssign(employeeId, unitCompanyId, unitId, positionId, BaseConstants.Flag.YES, BaseConstants.Flag.NO, tenantId));
                //主岗唯一性校验
                employeeAssignDomainService.validatePrimaryPositionFlagRepeat(tenantId, employeeId);
            }

        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void batchDeleteEmployee(Long tenantId, Long unitId, Long positionId, List<Long> employeeIdList) {
        employeeRepository.updateLastUpdateTime(employeeIdList);
        if (!employeeIdList.isEmpty()) {
            Long unitCompanyId = getUnitCompanyId(positionId);
            for (Long employeeId : employeeIdList) {
                employeeAssignRepository.delete(new EmployeeAssign(employeeId, unitCompanyId, unitId, positionId, null, null, tenantId));
                //主岗唯一性校验
                employeeAssignDomainService.validatePrimaryPositionFlagRepeat(tenantId, employeeId);
            }
        }

    }

    /**
     * 查询公司ID
     *
     * @param positionId 岗位ID
     */
    private Long getUnitCompanyId(Long positionId) {
        Position position = new Position();
        position.setPositionId(positionId);
        Position selectOne = positionRepository.selectOne(position);
        if (selectOne == null) {
            throw new CommonException(BaseConstants.ErrorCode.ERROR);
        }
        return selectOne.getUnitCompanyId();
    }


}
