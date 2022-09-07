package org.hzero.actuator.strategy;

import javax.servlet.http.HttpServletRequest;

/**
 * 获取服务信息策略类
 *
 * @author bojiangzhou 2020/03/27
 */
public interface ActuatorStrategy {

    /**
     * 获取处理的数据类型
     *
     * @return 策略类型
     */
    String getType();

    /**
     * 查询监控的数据
     *
     * @param request 可用于获取参数
     * @return 获取到的数据
     */
    Object queryActuatorData(HttpServletRequest request);

}
