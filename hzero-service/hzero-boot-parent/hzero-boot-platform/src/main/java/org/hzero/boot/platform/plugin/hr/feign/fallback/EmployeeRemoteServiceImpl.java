package org.hzero.boot.platform.plugin.hr.feign.fallback;

import org.hzero.boot.platform.plugin.hr.feign.EmployeeRemoteService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @author qingsheng.chen@hand-china.com 2019-05-21 16:43
 */
@Component
public class EmployeeRemoteServiceImpl implements EmployeeRemoteService {
    public static final Logger logger = LoggerFactory.getLogger(EmployeeRemoteServiceImpl.class);

    @Override
    public ResponseEntity<String> getEmployeeUser(Long organizationId, Integer enabledFlag, Long userId) {
        logger.error("Error get employee user : organizationId = {}, userId = {}", organizationId, userId);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
    }


    @Override
    public ResponseEntity<String> getEmployee(Long organizationId, List<Long> employeeIds) {
        logger.error("Error get employee : organizationId = {}, employeeIds = {}", organizationId, employeeIds);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
    }

    @Override
    public ResponseEntity<String> queryEmployee(Long organizationId, String employeeNum) {
        logger.error("Error get employee : organizationId = {}, employeeNum = {}", organizationId, employeeNum);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
    }

    @Override
    public ResponseEntity<String> queryEmployeeById(Long organizationId, Long employeeId, Integer enabledFlag) {
        logger.error("Error get employee : organizationId = {}, employeeId = {}", organizationId, employeeId);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
    }
}
