package org.hzero.boot.imported.infra.listener;

import java.util.Map;

import org.hzero.boot.imported.app.service.IBatchImportService;
import org.hzero.boot.imported.app.service.IDoImportService;
import org.hzero.boot.imported.app.service.ValidatorHandler;
import org.hzero.boot.imported.infra.registry.ImportRegistry;
import org.hzero.boot.imported.infra.validator.annotation.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.ApplicationContext;
import org.springframework.data.util.ProxyUtils;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;

import io.choerodon.core.convertor.ApplicationContextHelper;

/**
 * 校验注册Spring监听
 *
 * @author chunqiang.bai@hand-china.com
 */
@Component
public class RegistryApplicationListener implements CommandLineRunner {

    private static final Logger logger = LoggerFactory.getLogger(RegistryApplicationListener.class);

    @Override
    public void run(String... args) throws Exception {
        ApplicationContext applicationContext = ApplicationContextHelper.getContext();
        Map<String, Object> importMap = applicationContext.getBeansWithAnnotation(ImportService.class);
        for (Object service : importMap.values()) {
            if (service instanceof IDoImportService || service instanceof IBatchImportService) {
                ImportService importService = ProxyUtils.getUserClass(service).getAnnotation(ImportService.class);
                if (ObjectUtils.isEmpty(importService)) {
                    logger.debug("could not get target bean , service : {}", service);
                } else {
                    ImportRegistry.setServiceMap(importService.templateCode(), importService.tenantNum(), importService.sheetIndex(), importService.sheetName(), service);
                }
            }
        }

        Map<String, Object> commonImportMap = applicationContext.getBeansWithAnnotation(CommonImportService.class);
        for (Object service : commonImportMap.values()) {
            if (service instanceof IDoImportService || service instanceof IBatchImportService) {
                CommonImportService commonImportService = ProxyUtils.getUserClass(service).getAnnotation(CommonImportService.class);
                if (ObjectUtils.isEmpty(commonImportService)) {
                    logger.debug("could not get target bean , service : {}", service);
                } else {
                    ImportRegistry.setCommonServiceMap(commonImportService.tenantNum(), service);
                }
            }
        }

        Map<String, Object> commons = applicationContext.getBeansWithAnnotation(ImportCommonValidator.class);
        for (Object com : commons.values()) {
            ImportCommonValidator commonValidator = ProxyUtils.getUserClass(com).getAnnotation(ImportCommonValidator.class);
            if (com instanceof ValidatorHandler) {
                ImportRegistry.addCommonValidator((ValidatorHandler) com, commonValidator.order());
            }
        }

        Map<String, Object> beans = applicationContext.getBeansWithAnnotation(ImportValidators.class);
        for (Object bean : beans.values()) {
            if (bean instanceof ValidatorHandler) {
                ImportValidators contractValidator = ProxyUtils.getUserClass(bean).getAnnotation(ImportValidators.class);
                if (ObjectUtils.isEmpty(contractValidator)) {
                    logger.debug("could not get target bean , service : {}", contractValidator);
                } else {
                    ImportValidator[] validators = contractValidator.value();
                    for (ImportValidator validator : validators) {
                        ImportRegistry.addValidator(validator.templateCode(), validator.tenantNum(), validator.sheetIndex(), validator.sheetName(), (ValidatorHandler) bean);
                    }
                }
            } else if (bean instanceof com.baidu.unbiz.fluentvalidator.ValidatorHandler) {
                ImportValidators contractValidator = ProxyUtils.getUserClass(bean).getAnnotation(ImportValidators.class);
                if (ObjectUtils.isEmpty(contractValidator)) {
                    logger.debug("could not get target bean , service : {}", contractValidator);
                } else {
                    ImportValidator[] validators = contractValidator.value();
                    for (ImportValidator validator : validators) {
                        ImportRegistry.addBaiDuValidator(validator.templateCode(), validator.tenantNum(), validator.sheetIndex(), validator.sheetName(), (com.baidu.unbiz.fluentvalidator.ValidatorHandler) bean);
                    }
                }
            }
        }
    }
}
