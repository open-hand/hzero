package org.hzero.plugin.platform.hr.app.service.impl;

import java.io.IOException;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.util.Strings;
import org.hzero.boot.imported.app.service.IDoImportService;
import org.hzero.boot.imported.infra.validator.annotation.ImportService;
import org.hzero.boot.platform.lov.adapter.LovAdapter;
import org.hzero.boot.platform.lov.dto.LovValueDTO;
import org.hzero.core.base.BaseConstants;
import org.hzero.core.convert.CommonConverter;
import org.hzero.plugin.platform.hr.api.dto.UnitImportDTO;
import org.hzero.plugin.platform.hr.domain.entity.Unit;
import org.hzero.plugin.platform.hr.domain.repository.UnitRepository;
import org.hzero.plugin.platform.hr.infra.constant.PlatformHrConstants;
import org.springframework.beans.factory.annotation.Autowired;

import com.fasterxml.jackson.databind.ObjectMapper;

import io.choerodon.core.exception.CommonException;
import io.choerodon.core.oauth.DetailsHelper;

/**
 * 组织导入实现类
 *
 * @author yuqing.zhang@hand-china.com 2020/04/24 17:17
 */
@ImportService(templateCode = PlatformHrConstants.ImportTemplateCode.UNIT_POSITION_TEMP)
public class UnitImportServiceImpl implements IDoImportService {
    private ObjectMapper objectMapper;
    private LovAdapter lovAdapter;
    private UnitRepository unitRepository;

    @Autowired
    public UnitImportServiceImpl(ObjectMapper objectMapper, LovAdapter lovAdapter, UnitRepository unitRepository) {
        this.objectMapper = objectMapper;
        this.lovAdapter = lovAdapter;
        this.unitRepository = unitRepository;
    }

    @Override
    public Boolean doImport(String data) {
        final Long tenantId = DetailsHelper.getUserDetails().getTenantId();
        final Map<String, String> typeLov = lovAdapter
                        .queryLovValue(PlatformHrConstants.UnitType.TOP_UNIT_CODE, BaseConstants.DEFAULT_TENANT_ID)
                        .stream().collect(Collectors.toMap(LovValueDTO::getMeaning, LovValueDTO::getValue));
        // 转实体
        UnitImportDTO unitImportDTO;
        try {
            unitImportDTO = objectMapper.readValue(data, UnitImportDTO.class);
        } catch (IOException e) {
            throw new CommonException(e);
        }
        Unit unit = CommonConverter.beanConvert(Unit.class, unitImportDTO);
        unit.setTenantId(tenantId);
        unit.setUnitTypeCode(typeLov.get(unitImportDTO.getUnitType()));
        // 处理上级组织
        if (Strings.isNotEmpty(unitImportDTO.getParentUnitCode())) {
            Unit parentUnit = unitRepository
                            .selectOne(new Unit().setUnitCode(unitImportDTO.getParentUnitCode()).setTenantId(tenantId));
            if (parentUnit == null) {
                throw new CommonException(PlatformHrConstants.ErrorCode.PARENT_UNIT_NOT_EXIST);

            }
            unit.setParentUnitId(parentUnit.getUnitId());
            if (PlatformHrConstants.UnitType.DEPARTMENT.equals(unit.getUnitTypeCode())) {
                unit.setUnitCompanyId(parentUnit.getUnitCompanyId() != null ? parentUnit.getUnitCompanyId()
                                : parentUnit.getUnitId());
            }
            String levelPath = StringUtils.join(parentUnit.getLevelPath(), PlatformHrConstants.SPLIT,
                            unitImportDTO.getUnitCode());
            unit.setLevelPath(levelPath);
        } else {
            // 组织类型为部门的必输上级组织
            if (PlatformHrConstants.UnitType.DEPARTMENT.equals(unit.getUnitTypeCode())) {
                throw new CommonException(PlatformHrConstants.ErrorCode.DEPARTMENT_MISSING_PARENT_UNIT);

            }
            unit.setLevelPath(unit.getUnitCode());
        }
        unit.generateQuickIndexAndPinyin();
        unit.validate(this.unitRepository);
        unitRepository.insertSelective(unit);

        return true;
    }
}
