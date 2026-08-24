//package org.hzero.swagger.domain.service.impl;
//
//import java.util.Arrays;
//import java.util.Map;
//
//import org.apache.commons.lang3.StringUtils;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.cloud.client.ServiceInstance;
//import org.springframework.stereotype.Component;
//
//import io.choerodon.core.exception.CommonException;
//
//import org.hzero.register.event.event.InstanceAddedEvent;
//import org.hzero.swagger.app.DocumentService;
//import org.hzero.swagger.app.SwaggerService;
//import org.hzero.swagger.config.SwaggerProperties;
//import org.hzero.swagger.domain.service.ParseSwaggerService;
//
///**
// *
// * @author bojiangzhou 2019/01/04
// */
//@Component
//public class ParseSwaggerServiceImpl implements ParseSwaggerService {
//
//    private static final Logger LOGGER = LoggerFactory.getLogger(ParseSwaggerServiceImpl.class);
//    private static final String VERSION_FIELD = "VERSION";
//
//    @Autowired
//    private SwaggerService swaggerService;
//    @Autowired
//    private DocumentService documentService;
//    @Autowired
//    private SwaggerProperties swaggerProperties;
//
//    @Override
//    public void parser(InstanceAddedEvent event) {
//        ServiceInstance instance = event.getServiceInstance();
//        String serviceName = event.getServiceName().toLowerCase();
//        // 跳过刷新
//        if (Arrays.stream(swaggerProperties.getSkipService()).anyMatch((skip) -> serviceName.contains(skip.trim()))) {
//            return;
//        }
//
//        String address = instance.getHost() + ":" + instance.getPort();
//        Map<String, String> metadata = instance.getMetadata();
//        String version = metadata.get(VERSION_FIELD);
//        LOGGER.info("receive service: {} message, version: {}, ip: {}", serviceName, version, address);
//        String json = null;
//        try {
//            json = documentService.fetchSwaggerJsonByIp(instance);
//            if (!StringUtils.isEmpty(serviceName) && !StringUtils.isEmpty(json)) {
//                try {
//                    swaggerService.updateOrInsertSwagger(serviceName, version, json);
//                } catch (Exception e) {
//                    LOGGER.warn("message has bean consumed failed when updateOrInsertSwagger, e {}", e.getMessage());
//                }
//            }
//        } catch (Exception e) {
//            LOGGER.error("refresh service route error. event={}", serviceName + "," + address + "," + metadata + "," + json, e);
//            throw new CommonException("refresh service route error. serviceName={}", serviceName);
//        }
//    }
//
//
//}
