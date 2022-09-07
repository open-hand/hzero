package org.hzero.platform.app.service.impl;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;

import org.apache.commons.lang3.StringUtils;
import org.hzero.boot.imported.app.service.ImportHandler;
import org.hzero.boot.imported.infra.validator.annotation.ImportService;
import org.hzero.core.base.BaseConstants;
import org.hzero.platform.domain.entity.Lov;
import org.hzero.platform.domain.repository.LovRepository;
import org.hzero.platform.domain.service.LovDomainService;
import org.hzero.platform.infra.constant.Constants;
import org.hzero.platform.infra.constant.HpfmMsgCodeConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.fasterxml.jackson.databind.ObjectMapper;

import io.choerodon.core.exception.CommonException;
import io.choerodon.core.oauth.DetailsHelper;

/**
 * 值集导入
 *
 * @author xiaoyu.zhao@hand-china.com 2020/06/23 20:34
 */
@ImportService(templateCode = Constants.ImportTemplateCode.LOV_TEMP)
public class LovImportServiceImpl extends ImportHandler {

    private static final Logger LOGGER = LoggerFactory.getLogger(LovImportServiceImpl.class);

    private final ObjectMapper objectMapper;
    private final LovDomainService domainService;
    private final LovRepository lovRepository;

    @Autowired
    public LovImportServiceImpl(ObjectMapper objectMapper, LovDomainService domainService,
            LovRepository lovRepository) {
        this.objectMapper = objectMapper;
        this.domainService = domainService;
        this.lovRepository = lovRepository;
    }

    @Override
    public Boolean doImport(String data) {
        Long tenantId = DetailsHelper.getUserDetails() != null ?
                DetailsHelper.getUserDetails().getTenantId() :
                BaseConstants.DEFAULT_TENANT_ID;
        Lov dataLov;
        try{
            dataLov = objectMapper.readValue(data, Lov.class);
        }catch (Exception e) {
            LOGGER.error("objectMapper convert data to lov entity failed, data is :{}", data);
            throw new CommonException(BaseConstants.ErrorCode.DATA_INVALID);
        }
        // 构造租户IdMap，key为language，value为租户Id值
        Map<String, String> tlsMap = new LinkedHashMap<>();
        dataLov.setTenantId(tenantId);
        dataLov.setEnabledFlag(BaseConstants.Flag.YES);
        // 设置租户多语言数据
        Map<String, Map<String, String>> tls = dataLov.get_tls();
        tls.forEach((key, valueMap) -> {
            valueMap.forEach((lang, value) -> {
                tlsMap.put(lang, tenantId.toString());
            });
        });
        tls.put(Lov.FIELD_TENANT_ID, tlsMap);
        // 判断值集是否存在，存在则更新，不存在则新增
        Lov existLov = lovRepository.selectOne(new Lov().setTenantId(tenantId).setLovCode(dataLov.getLovCode()));
        if (Objects.isNull(existLov)) {
            // 新增，判断是否存在父级值集，若存在则设置父级值集租户Id
            processParentLovTenantId(dataLov);
            domainService.addLov(dataLov);
        } else if(!Objects.equals(existLov.getLovTypeCode(), dataLov.getLovTypeCode())) {
            // 校验值集类型是否匹配，若匹配则可导入，否则不允许导入
            throw new CommonException(HpfmMsgCodeConstants.ERROR_LOV_TYPE_NOT_MATCH, existLov.getLovCode());
        } else {
            // 更新值集内容
            processParentLovTenantId(dataLov);
            dataLov.setLovId(existLov.getLovId()).setObjectVersionNumber(existLov.getObjectVersionNumber());
            // 更新值集数据
            domainService.updateLov(dataLov);
        }
        return true;
    }

    /**
     * 处理父级值集租户Id
     *
     * @param dataLov 导入值集数据
     */
    private void processParentLovTenantId(Lov dataLov) {
        if (StringUtils.isBlank(dataLov.getParentLovCode())) {
            // 不存在父级值集编码，返回即可
            return ;
        }
        Lov selectLov = new Lov().setLovCode(dataLov.getParentLovCode()).setTenantId(dataLov.getTenantId());
        // 先判断当前租户下是否存在该父级值集
        Lov parentLov =
                lovRepository.selectOne(selectLov);
        if (Objects.isNull(parentLov) && !BaseConstants.DEFAULT_TENANT_ID.equals(dataLov.getTenantId())) {
            parentLov = lovRepository.selectOne(selectLov.setTenantId(BaseConstants.DEFAULT_TENANT_ID));
        }
        if (Objects.isNull(parentLov)) {
            throw new CommonException(HpfmMsgCodeConstants.ERROR_PARENT_LOV_NOT_NULL, dataLov.getLovCode());
        }
        // 设置父级值集租户ID
        dataLov.setParentTenantId(parentLov.getTenantId());
    }
}
