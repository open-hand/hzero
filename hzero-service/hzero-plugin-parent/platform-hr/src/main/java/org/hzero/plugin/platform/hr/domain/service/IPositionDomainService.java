package org.hzero.plugin.platform.hr.domain.service;

import org.hzero.plugin.platform.hr.domain.entity.Position;

/**
 * 岗位领域服务
 *
 * @author yuqing.zhang@hand-china.com 2020/04/27 17:44
 */
public interface IPositionDomainService {
    /**
     * 校验岗位信息
     *
     * @param position 岗位
     * @return Position
     */
    Position validatePosition(Position position);
}
