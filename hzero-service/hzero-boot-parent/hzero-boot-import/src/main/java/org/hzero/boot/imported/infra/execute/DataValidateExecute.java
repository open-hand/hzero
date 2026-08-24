package org.hzero.boot.imported.infra.execute;


import java.util.*;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.hzero.boot.imported.app.service.ValidatorHandler;
import org.hzero.boot.imported.domain.entity.Import;
import org.hzero.boot.imported.domain.entity.ImportData;
import org.hzero.boot.imported.domain.entity.Template;
import org.hzero.boot.imported.domain.entity.TemplatePage;
import org.hzero.boot.imported.domain.repository.ImportDataRepository;
import org.hzero.boot.imported.domain.repository.ImportRepository;
import org.hzero.boot.imported.infra.constant.HimpBootConstants;
import org.hzero.boot.imported.infra.enums.DataStatus;
import org.hzero.boot.imported.infra.redis.AmountRedis;
import org.hzero.boot.imported.infra.registry.ImportRegistry;
import org.hzero.boot.imported.infra.util.StepUtils;
import org.hzero.boot.platform.rule.entity.ScriptResult;
import org.hzero.boot.platform.rule.helper.RuleEngineHelper;
import org.hzero.core.message.MessageAccessor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.Assert;

import com.baidu.unbiz.fluentvalidator.*;

import io.choerodon.core.domain.Page;
import io.choerodon.core.exception.CommonException;
import io.choerodon.core.oauth.DetailsHelper;
import io.choerodon.mybatis.pagehelper.domain.PageRequest;

/**
 * 验证数据执行器
 *
 * @author shuangfei.zhu@hand-china.com 2018/12/14 9:10
 */
public class DataValidateExecute implements Runnable {

    private static final Logger logger = LoggerFactory.getLogger(DataValidateExecute.class);
    private static final String NEW_LINE = System.getProperty("line.separator");

    private final Template template;
    private final String batch;
    private final Import imported;
    private final ImportDataRepository dataRepository;
    private final ImportRepository importRepository;
    private final Integer batchNumber;
    private final Authentication authentication;
    private final Map<String, Object> args;
    private Integer readyCount = 0;
    private Integer stepSize;

    public DataValidateExecute(Template template,
                               Import imported,
                               Integer batchNumber,
                               ImportRepository importRepository,
                               ImportDataRepository dataRepository,
                               Authentication authentication,
                               Map<String, Object> args) {
        Assert.notNull(template, HimpBootConstants.ErrorCode.LOCAL_TEMPLATE_NOT_EXISTS);
        Assert.notEmpty(template.getTemplatePageList(), HimpBootConstants.ErrorCode.TEMPLATE_PAGE_NOT_EXISTS);
        this.template = template;
        this.batch = imported.getBatch();
        this.imported = imported;
        this.batchNumber = batchNumber;
        this.importRepository = importRepository;
        this.dataRepository = dataRepository;
        this.authentication = authentication;
        this.args = args;
    }

    @Override
    public void run() {
        if (SecurityContextHolder.getContext() != null) {
            SecurityContextHolder.getContext().setAuthentication(authentication);
        }
        // 记录数据总数
        int count = dataRepository.selectCount(new ImportData().setBatch(batch));
        AmountRedis.refreshCount(null, batch, count);
        // 进度刷新步长
        stepSize = StepUtils.getStepSize(count);
        try {
            long start = System.nanoTime();
            logger.debug("<<<<<<<<<<<<<<<     batch {} start to validate     >>>>>>>>>>>>>>>", batch);
            final PageRequest pageRequest = new PageRequest(0, batchNumber);
            template.getTemplatePageList().forEach(templatePage -> {
                Page<ImportData> importDataList;
                // 平台校验器
                List<ValidatorHandler> validatorList = getValidators(template.getTemplateCode(), templatePage);
                // 百度校验器
                List<Validator> baiDuValidatorList = getBaiDuValidators(template.getTemplateCode(), templatePage);
                do {
                    importDataList = dataRepository.pageData(template.getTemplateCode(), batch, templatePage.getSheetIndex(), DataStatus.NEW, pageRequest);
                    if (importDataList.isEmpty()) {
                        break;
                    }
                    validateData(validatorList, baiDuValidatorList, template.getTenantId(), templatePage, importDataList.getContent());
                    // 刷新进度
                    readyCount += importDataList.size();
                    AmountRedis.refresh(null, batch, readyCount);
                } while (!importDataList.isEmpty());
            });
            logger.debug("<<<<<<<<<<<<<<<     batch {} validate success , time consuming : {}     >>>>>>>>>>>>>>>>", batch, System.nanoTime() - start);
        } catch (Exception e) {
            logger.error("<<<<<<<<<<<<<<<     batch {} validate failed     >>>>>>>>>>>>>>>", batch);
            logger.error("validate failed", e);
        } finally {
            // 更新状态
            importRepository.updateOptional(imported.setStatus(HimpBootConstants.ImportStatus.CHECKED), Import.FIELD_STATUS);
            // 清除进度缓存
            AmountRedis.clear(null, batch);
        }
    }

    /**
     * 数据校验
     *
     * @param validatorList 校验器列表
     * @param tenantId      租户ID
     * @param templatePage  模板页
     * @param dataList      校验数据
     */
    private void validateData(List<ValidatorHandler> validatorList, List<Validator> baiDuValidatorList, Long tenantId, TemplatePage templatePage, List<ImportData> dataList) {
        // 没有任何校验规则
        if (CollectionUtils.isEmpty(validatorList) && CollectionUtils.isEmpty(baiDuValidatorList) && !StringUtils.isNotBlank(templatePage.getRuleScriptCode())) {
            dataList.forEach(data -> data.setDataStatus(DataStatus.VALID_SUCCESS));
            return;
        }
        // 初始化百度校验器
        ValidatorChain validatorChain = new ValidatorChain();
        validatorChain.setValidators(baiDuValidatorList);

        int index = -1;
        for (ImportData data : dataList) {
            index++;
            try {
                // 校验成功标识，一旦为false，不可改为true
                boolean flag = true;
                // 平台校验器
                for (ValidatorHandler validatorHandler : validatorList) {
                    validatorHandler = validatorHandler.setArgs(args).setContext(data).setTemplatePage(templatePage);
                    // 校验
                    flag = validatorHandler.validate(data.getData()) && flag;
                    // 兼容老版本，记录错误信息
                    String errorMsg = validatorHandler.getDataContext().getErrorMsg();
                    if (StringUtils.isNotBlank(errorMsg)) {
                        validatorHandler.getContext().setErrorMsg(errorMsg);
                    }
                    // 清除线程变量
                    validatorHandler.clear();
                }
                // 百度校验器
                Result ret = FluentValidator.checkAll()
                        .failOver()
                        .putAttribute2Context(HimpBootConstants.TEMPLATE_PAGE, templatePage)
                        .putAttribute2Context(HimpBootConstants.DATA_ID, data.getId())
                        .putAttribute2Context(HimpBootConstants.ARGS, args)
                        .on(data.getData(), validatorChain)
                        .doValidate()
                        .result(ResultCollectors.toSimple());
                if (!ret.isSuccess()) {
                    data.addErrorMsg(org.springframework.util.StringUtils.collectionToDelimitedString(Optional.ofNullable(ret.getErrors()).orElse(Collections.emptyList()), NEW_LINE));
                    flag = false;
                }

                if (!flag) {
                    data.setDataStatus(DataStatus.VALID_FAILED);
                } else if (StringUtils.isNotBlank(templatePage.getRuleScriptCode())) {
                    // 规则引擎校验
                    Map<String, Object> scriptArgMap = new HashMap<>(2);
                    scriptArgMap.put(HimpBootConstants.TEMPLATE_PAGE, templatePage);
                    scriptArgMap.put("data", data);
                    if (args != null) {
                        scriptArgMap.putAll(args);
                    }
                    ScriptResult scriptResult = RuleEngineHelper.runScript(templatePage.getRuleScriptCode(), tenantId, scriptArgMap);
                    if (Boolean.FALSE.equals(scriptResult.getFailed()) && "true".equals(scriptResult.getContent())) {
                        data.setDataStatus(DataStatus.VALID_SUCCESS);
                    } else {
                        data.setDataStatus(DataStatus.VALID_FAILED);
                        data.setErrorMsg(StringUtils.abbreviate(MessageAccessor.getMessage("himp.error.script", new Object[]{templatePage.getRuleScriptCode(), scriptResult.getContent()}).desc(), 250));
                    }
                } else {
                    data.setDataStatus(DataStatus.VALID_SUCCESS);
                }
            } catch (CommonException e) {
                logger.error("exception", e);
                data.setDataStatus(DataStatus.VALID_FAILED).addErrorMsg(StringUtils.isNotBlank(e.getCode()) ? MessageAccessor.getMessage(e.getCode(), e.getParameters()).desc() : e.toString());
            } catch (Exception e) {
                logger.error("exception", e);
                if (e.getCause() instanceof CommonException && ((CommonException) e.getCause()).getCode() != null) {
                    data.setDataStatus(DataStatus.VALID_FAILED).addErrorMsg(MessageAccessor.getMessage(((CommonException) e.getCause()).getCode()).desc());
                } else {
                    data.setDataStatus(DataStatus.VALID_FAILED).addErrorMsg(e.getMessage());
                }
            } finally {
                dataRepository.updateOptional(data, ImportData.FIELD_DATA_STATUS, ImportData.FIELD_ERROR_MSG, ImportData.FIELD_BACK_INFO);
                // 刷新进度
                if (index > 0 && index % stepSize == 0) {
                    AmountRedis.refreshReady(null, batch, stepSize);
                }
            }
        }
    }

    private List<ValidatorHandler> getValidators(String templateCode, TemplatePage templatePage) {
        List<ValidatorHandler> validatorList = ImportRegistry.getCommonValidatorList();
        List<ValidatorHandler> cusValidatorList = ImportRegistry.getValidatorList(templateCode, DetailsHelper.getUserDetails().getTenantNum(), templatePage.getSheetIndex(), templatePage.getSheetName());
        if (CollectionUtils.isEmpty(validatorList) && CollectionUtils.isEmpty(cusValidatorList)) {
            return Collections.emptyList();
        } else if (CollectionUtils.isEmpty(validatorList)) {
            return cusValidatorList;
        } else if (CollectionUtils.isEmpty(cusValidatorList)) {
            return validatorList;
        } else {
            List<ValidatorHandler> validators = new ArrayList<>(validatorList);
            validators.addAll(cusValidatorList);
            return validators;
        }
    }


    private List<Validator> getBaiDuValidators(String templateCode, TemplatePage templatePage) {
        List<Validator> cusValidatorList = ImportRegistry.getBaiDuValidatorList(templateCode, DetailsHelper.getUserDetails().getTenantNum(), templatePage.getSheetIndex(), templatePage.getSheetName());
        if (cusValidatorList == null) {
            return Collections.emptyList();
        } else {
            return cusValidatorList;
        }
    }
}
