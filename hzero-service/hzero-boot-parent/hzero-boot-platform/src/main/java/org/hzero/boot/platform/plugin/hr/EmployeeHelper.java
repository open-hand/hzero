package org.hzero.boot.platform.plugin.hr;

import io.choerodon.core.convertor.ApplicationContextHelper;
import org.hzero.boot.platform.plugin.hr.entity.Employee;
import org.hzero.boot.platform.plugin.hr.feign.EmployeeRemoteService;
import org.hzero.common.HZeroService;
import org.hzero.core.base.BaseConstants;
import org.hzero.core.redis.RedisHelper;
import org.hzero.core.util.ResponseUtils;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

import java.util.Optional;

/**
 * @author qingsheng.chen@hand-china.com 2019-05-21 15:46
 */
public class EmployeeHelper {
    private static volatile RedisHelper redisHelper;
    private static volatile EmployeeRemoteService employeeRemoteService;

    public static RedisHelper getRedisHelper() {
        if (redisHelper == null) {
            synchronized (EmployeeHelper.class) {
                if (redisHelper == null) {
                    redisHelper = ApplicationContextHelper.getContext().getBean(RedisHelper.class);
                }
            }
        }
        return redisHelper;
    }

    public static EmployeeRemoteService getEmployeeRemoteService() {
        if (employeeRemoteService == null) {
            synchronized (EmployeeHelper.class) {
                if (employeeRemoteService == null) {
                    employeeRemoteService = ApplicationContextHelper.getContext().getBean(EmployeeRemoteService.class);
                }
            }
        }
        return employeeRemoteService;
    }

    public static void setRedisHelper(RedisHelper redisHelper) {
        EmployeeHelper.redisHelper = redisHelper;
    }

    public static Employee getEmployee(long userId, long tenantId) {
        // 生成 【用户ID -> 员工ID】 缓存key
        String employeeUserCacheKey = getEmployeeUserCacheKey(userId);
        // 查询 用户在当前租户下是否有员工
        String strEmployeeId = getSomething(redisHelper -> redisHelper.hshGet(employeeUserCacheKey, String.valueOf(tenantId)));
        long employeeId = StringUtils.hasText(strEmployeeId) ? Long.parseLong(strEmployeeId) : -1;
        // 如果没有
        if (employeeId == -1) {
            // 调用接口查询 用户在租户下是否有员工
            Employee employee = ResponseUtils.getResponse(getEmployeeRemoteService().getEmployeeUser(tenantId, BaseConstants.Flag.YES, userId), Employee.class);
            // 如果有员工
            if (employee != null) {
                Assert.notNull(employee.getEmployeeId(), BaseConstants.ErrorCode.DATA_INVALID);
                Assert.notNull(employee.getEmployeeNum(), BaseConstants.ErrorCode.DATA_INVALID);
                Assert.notNull(employee.getName(), BaseConstants.ErrorCode.DATA_INVALID);
                // 缓存 用户与员工的关系
                doSomething(redisHelper -> redisHelper.hshPut(employeeUserCacheKey, String.valueOf(tenantId), String.valueOf(employee.getEmployeeId())));
                // 缓存 员工信息
                doSomething(redisHelper -> {
                    // 缓存 员工ID和员工编码
                    redisHelper.objectSet(getEmployeeCacheKey(employee.getEmployeeId()), employee.getEmployeeNum());
                    redisHelper.objectSet(getEmployeeCacheKey(employee.getEmployeeNum(), tenantId), employee);
                });
            }
            return employee;
        }
        String employeeCode = getSomething(redisHelper -> redisHelper.strGet(getEmployeeCacheKey(employeeId)));
        return StringUtils.hasText(employeeCode) ? getEmployee(employeeCode, tenantId) : getEmployeeByEmployeeId(employeeId, tenantId);
    }

    private static Employee getEmployeeByEmployeeId(long employeeId, long tenantId) {
        Employee employee = ResponseUtils.getResponse(getEmployeeRemoteService().queryEmployeeById(tenantId, employeeId, BaseConstants.Flag.YES), Employee.class);
        if (employee != null) {
            // 缓存 员工信息
            doSomething(redisHelper -> {
                // 缓存 员工ID和员工编码
                redisHelper.objectSet(getEmployeeCacheKey(employee.getEmployeeId()), employee.getEmployeeNum());
                redisHelper.objectSet(getEmployeeCacheKey(employee.getEmployeeNum(), tenantId), employee);
            });
            return employee;
        }
        return null;
    }

    public static Employee getEmployee(String employeeNum, long tenantId) {
        String employeeJson = getSomething(redisHelper -> redisHelper.strGet(getEmployeeCacheKey(employeeNum, tenantId)));
        if (!StringUtils.hasText(employeeJson)) {
            // 调用接口查询 员工
            Employee employee = ResponseUtils.getResponse(getEmployeeRemoteService().queryEmployee(tenantId, employeeNum), Employee.class);
            if (employee != null) {
                // 缓存 员工信息
                doSomething(redisHelper -> {
                    // 缓存 员工ID和员工编码
                    redisHelper.objectSet(getEmployeeCacheKey(employee.getEmployeeId()), employee.getEmployeeNum());
                    redisHelper.objectSet(getEmployeeCacheKey(employee.getEmployeeNum(), tenantId), employee);
                });
                return employee;
            }
            return null;
        }
        return getSomething(redisHelper -> redisHelper.fromJson(employeeJson, Employee.class));
    }

    public static String getEmployeeNum(long userId, long tenantId) {
        Employee employee = Optional.ofNullable(getEmployee(userId, tenantId)).orElse(null);
        if (employee != null) {
            return employee.getEmployeeNum();
        }
        return null;
    }

    public static String getName(long userId, long tenantId) {
        Employee employee = Optional.ofNullable(getEmployee(userId, tenantId)).orElse(null);
        if (employee != null) {
            return employee.getName();
        }
        return null;
    }

    public static void storeEmployeeUser(long userId, long tenantId, long employeeId) {
        doSomething(redisHelper -> redisHelper.hshPut(getEmployeeUserCacheKey(userId), String.valueOf(tenantId), String.valueOf(employeeId)));
    }

    public static void deleteEmployeeUser(long userId, long tenantId) {
        doSomething(redisHelper -> redisHelper.hshDelete(getEmployeeUserCacheKey(userId), String.valueOf(tenantId)));
    }

    public static void deleteEmployeeUser(long userId) {
        doSomething(redisHelper -> redisHelper.delKey(getEmployeeUserCacheKey(userId)));
    }

    public static void storeEmployee(Employee employee) {
        // 缓存 员工信息
        doSomething(redisHelper -> {
            // 缓存 员工ID和员工编码
            redisHelper.objectSet(getEmployeeCacheKey(employee.getEmployeeId()), employee.getEmployeeNum());
            redisHelper.objectSet(getEmployeeCacheKey(employee.getEmployeeNum(), employee.getTenantId()), employee);
        });
    }

    public static void deleteEmployee(long employeeId, long tenantId) {
        String employeeNum = getSomething(redisHelper -> redisHelper.strGet(getEmployeeCacheKey(employeeId)));
        // 缓存 员工信息
        doSomething(redisHelper -> {
            // 缓存 员工ID和员工编码
            redisHelper.delKey(getEmployeeCacheKey(employeeId));
            redisHelper.delKey(getEmployeeCacheKey(employeeNum, tenantId));
        });
    }

    public static void deleteEmployee(long employeeId, String employeeNum, long tenantId) {
        // 缓存 员工信息
        doSomething(redisHelper -> {
            // 缓存 员工ID和员工编码
            redisHelper.delKey(getEmployeeCacheKey(employeeId));
            redisHelper.delKey(getEmployeeCacheKey(employeeNum, tenantId));
        });
    }

    public static void deleteEmployee(Employee employee) {
        // 缓存 员工信息
        doSomething(redisHelper -> {
            // 缓存 员工ID和员工编码
            redisHelper.delKey(getEmployeeCacheKey(employee.getEmployeeId()));
            redisHelper.delKey(getEmployeeCacheKey(employee.getEmployeeNum(), employee.getTenantId()));
        });
    }

    private static String getEmployeeUserCacheKey(long userId) {
        return HZeroService.Platform.CODE + ":hr:user-employee:" + userId;
    }

    private static String getEmployeeCacheKey(long employeeId) {
        return HZeroService.Platform.CODE + ":hr:employee:" + employeeId;
    }

    private static String getEmployeeCacheKey(String employeeNum, long tenantId) {
        return HZeroService.Platform.CODE + ":hr:employee:" + tenantId + ":" + employeeNum;
    }

    private static <T> T getSomething(GetSomethingWithRedisHelper<T> employeeRedisHelper) {
        RedisHelper redisHelper = getRedisHelper();
        redisHelper.setCurrentDatabase(HZeroService.Platform.REDIS_DB);
        T result = employeeRedisHelper.getSomething(redisHelper);
        redisHelper.clearCurrentDatabase();
        return result;
    }

    public interface GetSomethingWithRedisHelper<T> {
        T getSomething(RedisHelper redisHelper);
    }

    private static void doSomething(DoSomethingWithRedisHelper employeeRedisHelper) {
        RedisHelper redisHelper = getRedisHelper();
        redisHelper.setCurrentDatabase(HZeroService.Platform.REDIS_DB);
        employeeRedisHelper.doSomething(redisHelper);
        redisHelper.clearCurrentDatabase();
    }

    public interface DoSomethingWithRedisHelper {
        void doSomething(RedisHelper redisHelper);
    }

}
