package org.hzero.plugin.platform.hr.app.service.impl;

import java.io.IOException;
import java.util.Objects;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.util.Strings;
import org.hzero.boot.imported.app.service.IDoImportService;
import org.hzero.boot.imported.infra.validator.annotation.ImportService;
import org.hzero.core.base.BaseConstants;
import org.hzero.core.convert.CommonConverter;
import org.hzero.plugin.platform.hr.api.dto.PositionImportDTO;
import org.hzero.plugin.platform.hr.domain.entity.Position;
import org.hzero.plugin.platform.hr.domain.entity.Unit;
import org.hzero.plugin.platform.hr.domain.repository.PositionRepository;
import org.hzero.plugin.platform.hr.domain.repository.UnitRepository;
import org.hzero.plugin.platform.hr.domain.service.IPositionDomainService;
import org.hzero.plugin.platform.hr.infra.constant.PlatformHrConstants;
import org.springframework.beans.factory.annotation.Autowired;

import com.fasterxml.jackson.databind.ObjectMapper;

import io.choerodon.core.exception.CommonException;
import io.choerodon.core.oauth.DetailsHelper;

/**
 * 岗位导入实现类
 *
 * @author yuqing.zhang@hand-china.com 2020/04/24 17:17
 */
@ImportService(templateCode = PlatformHrConstants.ImportTemplateCode.UNIT_POSITION_TEMP, sheetIndex = 1)
public class PositionImportServiceImpl implements IDoImportService {
    private ObjectMapper objectMapper;
    private UnitRepository unitRepository;
    private PositionRepository positionRepository;
    private IPositionDomainService positionDomainService;

    @Autowired
    public PositionImportServiceImpl(ObjectMapper objectMapper, UnitRepository unitRepository,
                    PositionRepository positionRepository, IPositionDomainService positionDomainService) {
        this.objectMapper = objectMapper;
        this.unitRepository = unitRepository;
        this.positionRepository = positionRepository;
        this.positionDomainService = positionDomainService;
    }

    @Override
    public Boolean doImport(String data) {
        final Long tenantId = DetailsHelper.getUserDetails().getTenantId();
        // 转实体
        PositionImportDTO positionDTO;
        try {
            positionDTO = objectMapper.readValue(data, PositionImportDTO.class);
        } catch (IOException e) {
            throw new CommonException(e);
        }
        Position position = CommonConverter.beanConvert(Position.class, positionDTO);
        // 获取所属部门信息
        Unit unit = unitRepository.selectOne(new Unit().setUnitCode(positionDTO.getUnitCode()).setTenantId(tenantId));
        if (unit == null || unit.getUnitCompanyId() == null) {
            throw new CommonException(PlatformHrConstants.ErrorCode.DEPARTMENT_MISSING_OR_NOT_DEPARTMENT);
        }
        position.setTenantId(tenantId);
        // 校验上级部门及所属公司是否启用
        if (!checkUnitEnabled(unit.getUnitId()) || !checkUnitEnabled(unit.getUnitCompanyId())) {
            throw new CommonException(PlatformHrConstants.ErrorCode.DEPARTMENT_OR_COMPANY_NOT_ENABLED);
        }
        position.setUnitCompanyId(unit.getUnitCompanyId());
        position.setUnitId(unit.getUnitId());
        // 处理上级岗位
        if (Strings.isNotEmpty(positionDTO.getParentPositionCode())) {
            Position parentPosition = positionRepository.selectOne(
                            new Position().setPositionCode(positionDTO.getParentPositionCode()).setTenantId(tenantId));
            if (parentPosition == null) {
                throw new CommonException(PlatformHrConstants.ErrorCode.PARENT_POSITION_NOT_EXIST);
            }
            position.setParentPositionId(parentPosition.getPositionId());
            String levelPath = StringUtils.join(parentPosition.getLevelPath(), PlatformHrConstants.SPLIT,
                            position.getPositionCode());
            position.setLevelPath(levelPath);
        } else {
            position.setLevelPath(position.getPositionCode());
        }
        positionDomainService.validatePosition(position);
        positionRepository.insertSelective(position);

        return true;
    }

    /**
     * 校验组织是否存在且启用
     *
     * @param unitId 组织ID
     * @return 是否存在且启用
     */
    private boolean checkUnitEnabled(Long unitId) {
        Unit unit = this.unitRepository.selectByPrimaryKey(unitId);
        return unit != null && Objects.equals(BaseConstants.Flag.YES, unit.getEnabledFlag());
    }
}
