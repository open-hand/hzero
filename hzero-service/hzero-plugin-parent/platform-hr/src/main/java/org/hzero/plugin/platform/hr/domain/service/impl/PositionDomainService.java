package org.hzero.plugin.platform.hr.domain.service.impl;

import java.util.List;
import java.util.Objects;

import org.hzero.core.base.BaseConstants;
import org.hzero.plugin.platform.hr.domain.entity.Position;
import org.hzero.plugin.platform.hr.domain.repository.PositionRepository;
import org.hzero.plugin.platform.hr.domain.service.IPositionDomainService;
import org.hzero.plugin.platform.hr.infra.constant.PlatformHrConstants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import io.choerodon.core.exception.CommonException;

/**
 * 岗位领域服务实现类
 *
 * @author yuqing.zhang@hand-china.com 2020/04/27 17:48
 */
@Service
public class PositionDomainService implements IPositionDomainService {
    private PositionRepository positionRepository;

    @Autowired
    public PositionDomainService(PositionRepository positionRepository) {
        this.positionRepository = positionRepository;
    }

    @Override
    public Position validatePosition(Position position) {
        // 验证岗位Code是否重复
        List<Position> positionList = positionRepository.select(
                        new Position().setTenantId(position.getTenantId()).setPositionCode(position.getPositionCode()));
        Position existed = null;
        if (position.getPositionId() != null) {
            for (Position item : positionList) {
                if (Objects.equals(item.getPositionId(), position.getPositionId())) {
                    existed = item;
                    positionList.remove(existed);
                    break;
                }
            }
        }
        if (!positionList.isEmpty()) {
            throw new CommonException(BaseConstants.ErrorCode.ERROR_CODE_REPEAT);
        }
        // 验证父级岗位是否启用
        if (position.getParentPositionId() != null) {
            Position parent = positionRepository.selectByPrimaryKey(position.getParentPositionId());
            if (BaseConstants.Flag.NO.equals(parent.getEnabledFlag())) {
                throw new CommonException(PlatformHrConstants.ErrorCode.ERROR_POSITION_NOT_ALLOWED);
            }
        }
        // 如果主管岗位标记为true，需要验证是否唯一
        if (BaseConstants.Flag.YES.equals(position.getSupervisorFlag())
                        && positionRepository.querySupervisorPositionFlagCount(position.getTenantId(),
                                        position.getUnitId(), position.getPositionId()) != 0) {
            throw new CommonException(PlatformHrConstants.ErrorCode.ERROR_POSITION_MULTIPLE_SUPERVISOR_EXCEPTIONS);
        }
        return existed;
    }
}
