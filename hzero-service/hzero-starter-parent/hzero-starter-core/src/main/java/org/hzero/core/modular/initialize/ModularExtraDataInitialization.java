package org.hzero.core.modular.initialize;

import org.hzero.core.modular.ModularProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.mvc.condition.PatternsRequestCondition;
import org.springframework.web.servlet.mvc.method.RequestMappingInfo;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import io.choerodon.swagger.swagger.extra.ExtraData;
import io.choerodon.swagger.swagger.extra.ExtraDataInitialization;

/**
 * 服务合并时的模块化处理，并把数据保存在ExtraData
 *
 * @author XCXCXCXCX
 * @date 2019/8/26
 */
public class ModularExtraDataInitialization implements ExtraDataInitialization {

    private final Map<String, String> urlModularMap = new HashMap<>(64);

    private final ModularProperties properties;

    @Autowired(required = false)
    private RequestMappingHandlerMapping requestMappingHandlerMapping;

    public ModularExtraDataInitialization(ModularProperties properties) {
        this.properties = properties;
    }

    @Override
    public void init(ExtraData extraData) {
        if (properties.isEnable() && requestMappingHandlerMapping != null) {
            Map<String, String> modularPackagesMap = (Map<String, String>) extraData.get(MergeExtraDataInitialization.MODULAR_PACKAGES_MAP_KEY);
            Set<Map.Entry<String, String>> modularEntries = modularPackagesMap.entrySet();
            Map<RequestMappingInfo, HandlerMethod> map = requestMappingHandlerMapping.getHandlerMethods();
            for (Map.Entry<RequestMappingInfo, HandlerMethod> entry : map.entrySet()) {
                HandlerMethod handlerMethod = entry.getValue();
                Class handlerType = handlerMethod.getBeanType();
                String className = handlerType.getName();
                for (Map.Entry<String, String> module : modularEntries) {
                    String pac = module.getKey();
                    if (className.startsWith(pac)) {
                        RequestMappingInfo requestMappingInfo = entry.getKey();
                        PatternsRequestCondition condition = requestMappingInfo.getPatternsCondition();
                        Set<String> patterns = condition.getPatterns();
                        for (String pattern : patterns) {
                            urlModularMap.put(pattern, module.getValue());
                        }
                        break;
                    }
                }
            }
            extraData.put(ExtraData.URL_MODULAR_MAP_KEY, urlModularMap);
        }
    }

    @Override
    public int getOrder() {
        return 1;
    }
}
