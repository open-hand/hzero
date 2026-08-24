package org.hzero.plugin.platform.hr.app.service.impl;

import org.hzero.boot.imported.app.service.IDoImportService;
import org.hzero.boot.imported.infra.validator.annotation.ImportService;
import org.hzero.core.base.BaseConstants;
import org.hzero.plugin.platform.hr.domain.entity.Employee;
import org.hzero.plugin.platform.hr.domain.repository.EmployeeRepository;
import org.springframework.beans.factory.annotation.Autowired;

import com.fasterxml.jackson.databind.ObjectMapper;

import io.choerodon.core.exception.CommonException;
import io.choerodon.core.oauth.DetailsHelper;

/**
 * 员工导入实现类
 *
 * @author shuangfei.zhu@hand-china.com 2019/05/27 10:12
 */
@ImportService(templateCode = "HPFM.EMPLOYEE")
public class EmployeeImportServiceImpl implements IDoImportService {

    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private EmployeeRepository employeeRepository;

    @Override
    public Boolean doImport(String data) {
        Employee employee;
        try {
            employee = objectMapper.readValue(data, Employee.class);
        } catch (Exception e) {
            throw new CommonException(e);
        }
        // 检查拼音和快速索引是否为空，为空则自动生成拼音和快速索引数据
        employee.generateQuickIndexAndPinyin();
        employee.setTenantId(DetailsHelper.getUserDetails().getTenantId());
        employee.setEnabledFlag(BaseConstants.Flag.YES);
        // 唯一性检查
        Employee employeeTemp = new Employee();
        employeeTemp.setEmployeeNum(employee.getEmployeeNum());
        employeeTemp.setTenantId(employee.getTenantId());
        int count = employeeRepository.selectCount(employeeTemp);
        if (count != 0) {
            throw new CommonException(BaseConstants.ErrorCode.ERROR_CODE_REPEAT);
        }
        employeeRepository.insertSelective(employee);
        employeeRepository.saveCache(employee);
        return true;
    }
}
