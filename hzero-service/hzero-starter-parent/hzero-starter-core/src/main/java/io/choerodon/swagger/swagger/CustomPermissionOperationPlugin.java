package io.choerodon.swagger.swagger;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.choerodon.core.swagger.LabelData;
import io.choerodon.core.swagger.PermissionData;
import io.choerodon.core.swagger.SwaggerExtraData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.annotation.Order;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spi.service.OperationBuilderPlugin;
import springfox.documentation.spi.service.contexts.OperationContext;
import springfox.documentation.swagger.common.SwaggerPluginSupport;

/**
 * 允许在没有@Permission注解的接口上自定义权限信息
 *
 * @author XCXCXCXCX
 */
@Order(SwaggerPluginSupport.SWAGGER_PLUGIN_ORDER + 500)
public class CustomPermissionOperationPlugin implements OperationBuilderPlugin {

    private static final Logger LOGGER = LoggerFactory.getLogger(CustomPermissionOperationPlugin.class);

    private final PermissionRegistry permissionRegistry;
    private final LabelRegistry labelRegistry;

    private final ObjectMapper mapper = new ObjectMapper();

    public CustomPermissionOperationPlugin(PermissionRegistry permissionRegistry, LabelRegistry labelRegistry) {
        this.permissionRegistry = permissionRegistry;
        this.labelRegistry = labelRegistry;
    }

    @Override
    public void apply(OperationContext context) {
        PermissionData permissionData = permissionRegistry.read(context.requestMappingPattern() + "@" + context.httpMethod());
        LabelData labelData = labelRegistry.read(context.requestMappingPattern() + "@" + context.httpMethod());
        if (permissionData != null || labelData != null) {
            SwaggerExtraData swaggerExtraData = new SwaggerExtraData();
            if (permissionData != null){
                permissionData.setAction(context.getName());
            }
            swaggerExtraData.setPermission(permissionData);
            swaggerExtraData.setLabel(labelData);
            try {
                context.operationBuilder().notes(mapper.writeValueAsString(swaggerExtraData));
            } catch (JsonProcessingException e) {
                LOGGER.info(e.getMessage());
            }
        }
    }

    @Override
    public boolean supports(DocumentationType documentationType) {
        return true;
    }
}
