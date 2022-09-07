package org.hzero.platform.app.service.impl;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.hzero.boot.imported.app.service.ImportHandler;
import org.hzero.boot.imported.infra.validator.annotation.ImportService;
import org.hzero.core.base.BaseConstants;
import org.hzero.mybatis.domian.Condition;
import org.hzero.mybatis.util.Sqls;
import org.hzero.platform.domain.entity.Lov;
import org.hzero.platform.domain.entity.LovValue;
import org.hzero.platform.domain.repository.LovRepository;
import org.hzero.platform.domain.repository.LovValueRepository;
import org.hzero.platform.domain.vo.LovValueImportVO;
import org.hzero.platform.infra.constant.Constants;
import org.hzero.platform.infra.constant.HpfmMsgCodeConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;

import com.fasterxml.jackson.databind.ObjectMapper;

import io.choerodon.core.exception.CommonException;
import io.choerodon.core.oauth.DetailsHelper;
import io.choerodon.mybatis.helper.LanguageHelper;

/**
 * 值集值导入
 *
 * @author yuqing.zhang@hand-china.com 2020/05/26 15:26
 */
@ImportService(templateCode = Constants.ImportTemplateCode.LOV_TEMP, sheetIndex = 1)
public class LovValueImportServiceImpl extends ImportHandler {

    private static final Logger LOGGER = LoggerFactory.getLogger(LovValueImportServiceImpl.class);

    private final ObjectMapper objectMapper;
    private final LovRepository lovRepository;
    private final LovValueRepository lovValueRepository;

    public LovValueImportServiceImpl(ObjectMapper objectMapper, LovRepository lovRepository,
                    LovValueRepository lovValueRepository) {
        this.objectMapper = objectMapper;
        this.lovRepository = lovRepository;
        this.lovValueRepository = lovValueRepository;
    }

    @Override
    public Boolean doImport(String data) {
        Long tenantId = DetailsHelper.getUserDetails().getTenantId();
        // 转实体
        LovValueImportVO lovValueImportVO;
        try {
            lovValueImportVO = objectMapper.readValue(data, LovValueImportVO.class);
        } catch (Exception e) {
            throw new CommonException(BaseConstants.ErrorCode.DATA_INVALID);
        }
        LovValue lovValue = new LovValue();
        BeanUtils.copyProperties(lovValueImportVO, lovValue);
        lovValue.setStartDateActive(lovValueImportVO.getDateStartDateActive());
        lovValue.setEndDateActive(lovValueImportVO.getDateEndDateActive());
        Lov select = new Lov().setTenantId(tenantId).setLovCode(lovValue.getLovCode());
        // 获得值集实体
        Lov lov = lovRepository.selectOne(select);
        Assert.notNull(lov, BaseConstants.ErrorCode.DATA_NOT_EXISTS);
        lovValue.setLovId(lov.getLovId());
        lovValue.setTenantId(tenantId);
        lovValue.setEnabledFlag(BaseConstants.Flag.YES);
        // 构造租户IdMap，key为language，value为租户Id值
        Map<String, String> tlsMap = new LinkedHashMap<>();
        // 设置租户多语言数据
        Map<String, Map<String, String>> tls = lovValue.get_tls();
        tls.forEach((key, valueMap) -> {
            valueMap.forEach((lang, value) -> {
                tlsMap.put(lang, tenantId.toString());
            });
        });
        tls.put(LovValue.FIELD_TENANT_ID, tlsMap);
        // 值集值存在父级值集值且存在父级值集编码，此时需验证父级值集值
        if (StringUtils.isNotBlank(lov.getParentLovCode())) {
            validateParentValue(lov, lovValue.getParentValue());
        } else if (StringUtils.isNotBlank(lovValue.getParentValue())) {
            // 不存在父级值集但却存在父级值集值
            throw new CommonException(HpfmMsgCodeConstants.ERROR_LOV_IMPORT_HAVE_NOT_PARENT);
        }
        // 新增或更新值集值
        List<LovValue> dbLovValue = lovValueRepository.selectByCondition(Condition.builder(LovValue.class)
                .andWhere(Sqls.custom()
                        .andEqualTo(LovValue.FIELD_LOV_ID, lovValue.getLovId())
                        .andEqualTo(LovValue.FIELD_VALUE, lovValue.getValue())
                        .andEqualTo(LovValue.FIELD_PARENT_VALUE, lovValue.getParentValue(), true)
                ).build());
        if (CollectionUtils.isEmpty(dbLovValue)) {
            // 插入数据
            lovValueRepository.insertSelective(lovValue);
        } else if (dbLovValue.size() > 1) {
            // 查询结果大于1 抛出异常
            LOGGER.error("find one dbLovValue by lovId, value and parentValue but found :{}, lovId is :{}, value is :{}, parentValue is :{}",
                    dbLovValue.size(), lovValue.getLovId(), lovValue.getValue(), lovValue.getParentValue());
            throw new CommonException(BaseConstants.ErrorCode.ERROR);
        } else {
            // 更新数据
            lovValue.setLovValueId(dbLovValue.get(0).getLovValueId());
            lovValue.setObjectVersionNumber(dbLovValue.get(0).getObjectVersionNumber());
            lovValueRepository.updateOptional(
                    lovValue,
                    LovValue.FIELD_MEANING,
                    LovValue.FIELD_DESCRIPTION,
                    LovValue.FIELD_ORDER_SEQ,
                    LovValue.FIELD_START_DATE_ACTIVE,
                    LovValue.FIELD_END_DATE_ACTIVE,
                    LovValue.FIELD_ENABLED_FLAG,
                    LovValue.FIELD_TAG
            );
        }
        // 清理缓存
        LanguageHelper.languages().forEach(language -> this.lovRepository.cleanCache(lovValue.getLovCode(),
                        lovValue.getTenantId(), language.getCode()));

        return true;
    }

    /**
     * 验证父级值集值
     * 
     * @param lov 值集实体
     * @param parentValue 父级值集值
     * @throws CommonException 父级值集值不存在或未启用
     */
    private void validateParentValue(Lov lov, String parentValue) {
        // 若父级值集值为空，则无需校验父级值集值
        if (StringUtils.isNotBlank(parentValue)) {
            return;
        }
        final LovValue temp = new LovValue();
        temp.setLovCode(lov.getParentLovCode());
        temp.setTenantId(lov.getParentTenantId());
        temp.setEnabledFlag(BaseConstants.Flag.YES);
        temp.setValue(parentValue);
        if (lovValueRepository.selectCount(temp) < BaseConstants.Digital.ONE) {
            throw new CommonException(HpfmMsgCodeConstants.ERROR_LOV_IMPORT_PARENT_VALUE_INVALID);
        }
    }
}
