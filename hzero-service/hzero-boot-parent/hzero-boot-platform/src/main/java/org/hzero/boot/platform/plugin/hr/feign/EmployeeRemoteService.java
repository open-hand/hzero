package org.hzero.boot.platform.plugin.hr.feign;

import org.hzero.boot.platform.plugin.hr.feign.fallback.EmployeeRemoteServiceImpl;
import org.hzero.common.HZeroService;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

/**
 * @author qingsheng.chen@hand-china.com 2019-05-21 16:41
 */
@FeignClient(value = HZeroService.Platform.NAME, path = "/v1", fallback = EmployeeRemoteServiceImpl.class)
public interface EmployeeRemoteService {

    /**
     * 获取员工信息
     *
     * @param organizationId 租户ID
     * @param enabledFlag    是否启用
     * @param userId         用户ID
     * @return 员工信息
     */
    @GetMapping("/{organizationId}/employee-users/employee")
    ResponseEntity<String> getEmployeeUser(@PathVariable("organizationId") Long organizationId,
                                           @RequestParam("enabledFlag") Integer enabledFlag,
                                           @RequestParam("userId") Long userId);

    /**
     * 批量查询员工信息
     *
     * @param organizationId 租户ID
     * @param employeeIds    员工ID列表
     * @return 员工信息列表
     */
    @GetMapping("/{organizationId}/employees/ids")
    ResponseEntity<String> getEmployee(@PathVariable("organizationId") Long organizationId,
                                       @RequestParam("employeeIds") List<Long> employeeIds);

    /**
     * 根据员工编码获取员工信息
     *
     * @param organizationId 租户ID
     * @param employeeNum    员工编码
     * @return 员工信息
     */
    @GetMapping("/{organizationId}/employees/employee-num")
    ResponseEntity<String> queryEmployee(@PathVariable("organizationId") Long organizationId,
                                         @RequestParam("employeeNum") String employeeNum);

    /**
     * 根据员工ID获取员工信息
     *
     * @param organizationId 租户ID
     * @param employeeId     员工编码
     * @return 员工信息
     */
    @GetMapping("/{organizationId}/employees/id")
    ResponseEntity<String> queryEmployeeById(@PathVariable("organizationId") Long organizationId,
                                             @RequestParam("employeeId") Long employeeId,
                                             @RequestParam("enabledFlag") Integer enabledFlag);
}
