package org.hzero.plugin.platform.hr.infra.repository.impl;

import java.util.Collections;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;
import org.hzero.common.HZeroService;
import org.hzero.core.base.BaseConstants;
import org.hzero.core.redis.RedisHelper;
import org.hzero.core.redis.safe.SafeRedisHelper;
import org.hzero.mybatis.base.impl.BaseRepositoryImpl;
import org.hzero.plugin.platform.hr.api.dto.EmployeeAssignDTO;
import org.hzero.plugin.platform.hr.domain.entity.Employee;
import org.hzero.plugin.platform.hr.domain.entity.EmployeeAssign;
import org.hzero.plugin.platform.hr.domain.repository.EmployeeAssignRepository;
import org.hzero.plugin.platform.hr.domain.vo.EmployeeAssignVO;
import org.hzero.plugin.platform.hr.infra.constant.PlatformHrConstants;
import org.hzero.plugin.platform.hr.infra.mapper.EmployeeAssignMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import com.fasterxml.jackson.databind.ObjectMapper;

import io.choerodon.core.domain.Page;
import io.choerodon.mybatis.pagehelper.PageHelper;
import io.choerodon.mybatis.pagehelper.domain.PageRequest;

/**
 * 员工岗位分配表 资源库实现
 *
 * @author liang.jin@hand-china.com 2018-06-20 15:20:44
 */
@Component
public class EmployeeAssignRepositoryImpl extends BaseRepositoryImpl<EmployeeAssign>
                implements EmployeeAssignRepository {

    @Autowired
    private EmployeeAssignMapper employeeAssignMapper;
    @Autowired
    private RedisHelper redisHelper;
    @Autowired
    private ObjectMapper objectMapper;

    @Override
    public List<EmployeeAssignDTO> selectEmployeeAssign(long tenantId, long employeeId) {
        return employeeAssignMapper.selectEmployeeAssign(tenantId, employeeId);
    }

    @Override
    public List<EmployeeAssignDTO> selectEmployeeAssign(String language, Long tenantId, Integer primaryPositionFlag,
                    Long userId) {
        return employeeAssignMapper.hcbmSelectEmployeeAssign(language, tenantId, primaryPositionFlag, userId);
    }


    @Override
    public EmployeeAssign get(Long id) {
        return this.selectByPrimaryKey(id);
    }

    @Override
    public void create(EmployeeAssign employeeAssign) {
        this.insertSelective(employeeAssign);
    }

    @Override
    public void update(EmployeeAssign employeeAssign) {
        this.updateByPrimaryKeySelective(employeeAssign);
    }

    @Override
    public void remove(Long employeeAssignId) {
        this.deleteByPrimaryKey(employeeAssignId);
    }


    @Override
    public Page<Employee> employeeNotInPosition(Long tenantId, String employeeNum, String name, Long unitId,
                    Long positionId, PageRequest pageRequest) {
        return PageHelper.doPageAndSort(pageRequest, () -> employeeAssignMapper.employeeNotInPosition(employeeNum, name,
                         positionId, tenantId));

    }

    @Override
    public Page<Employee> employeeInPosition(Long tenantId, String employeeNum, String name, Long unitId,
                    Long positionId, PageRequest pageRequest) {
        return PageHelper.doPageAndSort(pageRequest,
                        () -> employeeAssignMapper.employeeInPosition(employeeNum, name, positionId, tenantId));
    }

    @Override
    public List<Employee> employeeInPosition(Long tenantId, String employeeNum, String name, Long positionId) {
        return employeeAssignMapper.employeeInPosition(employeeNum, name, positionId, tenantId);
    }

    @Override
    public void batchDisableEmployeeAssign(Long tenantId, Long employeeId) {
        employeeAssignMapper.batchDisableEmployeeAssign(tenantId, employeeId);
    }


    @Override
    public List<EmployeeAssign> selectEmployeeAssignList(Long tenantId, Long employeeId, Integer enabledFlag) {
        return employeeAssignMapper.selectEmployeeAssignList(tenantId, employeeId, enabledFlag);
    }

    @Override
    public List<Long> selectUnitIdList(Long tenantId, Long employeeId) {
        return employeeAssignMapper.selectUnitIdList(tenantId, employeeId);
    }

    @Override
    public List<EmployeeAssignDTO> listRecentEmployeeAssign(Set<Long> employeeIds) {
        if (CollectionUtils.isEmpty(employeeIds)) {
            return Collections.emptyList();
        }
        return employeeAssignMapper.listRecentEmployeeAssign(employeeIds);
    }

    @Override
    public void updateEmpAssignCache(Long employeeId, Long tenantId) {
        // 先删除原有缓存再新增
        List<EmployeeAssignVO> cacheVOList = employeeAssignMapper.selectEmployeeAssignForCache(employeeId, tenantId);
        if (!CollectionUtils.isEmpty(cacheVOList)) {
            String cacheKey = this.generateCacheKey(employeeId);
            SafeRedisHelper.execute(HZeroService.Platform.REDIS_DB, redisHelper, () -> {
                redisHelper.delKey(cacheKey);
                for (EmployeeAssignVO cacheData : cacheVOList) {
                    redisHelper.lstRightPush(cacheKey, redisHelper.toJson(cacheData));
                }
            });
        }
    }

    @Override
    public void removeEmpAssignCache(Long employeeId, Long tenantId) {
        String cacheKey = this.generateCacheKey(employeeId);
        SafeRedisHelper.execute(HZeroService.Platform.REDIS_DB, redisHelper,
                        () -> redisHelper.delKey(cacheKey));
    }

    @Override
    public List<Employee> employeeInDept(Long tenantId, List<Long> unitIds, Integer enableFlag) {
        if (CollectionUtils.isEmpty(unitIds)) {
            return Collections.emptyList();
        }
        return employeeAssignMapper.employeeInDept(tenantId, unitIds, enableFlag);
    }

    @Override
    public List<Employee> employeeInCompany(Long tenantId, Long unitCompanyId, Integer enableFlag) {
        return employeeAssignMapper.employeeInCompany(tenantId, unitCompanyId, enableFlag);
    }

    /**
     * 生成缓存Key
     *
     * @param employeeId 员工Id
     */
    private String generateCacheKey(Long employeeId) {
        return StringUtils.join(PlatformHrConstants.EMP_ASSIGN_CACHE_KEY, employeeId);
    }


}
