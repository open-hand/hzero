package io.choerodon.swagger.swagger;

import java.util.Arrays;
import java.util.stream.Collectors;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.annotation.Order;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spi.service.OperationBuilderPlugin;
import springfox.documentation.spi.service.contexts.OperationContext;
import springfox.documentation.swagger.common.SwaggerPluginSupport;

import io.choerodon.core.swagger.LabelData;
import io.choerodon.core.swagger.PermissionData;
import io.choerodon.core.swagger.SwaggerExtraData;
import io.choerodon.swagger.annotation.Label;
import io.choerodon.swagger.annotation.Permission;

import org.hzero.core.base.BaseConstants;
import org.hzero.core.swagger.PermissionStatus;

/**
 * 解析接口的@Permission注解，将权限信息将乳到swagger的描述节点
 *
 * @author xausky
 */
@Order(SwaggerPluginSupport.SWAGGER_PLUGIN_ORDER + 1000)
public class CustomSwaggerOperationPlugin implements OperationBuilderPlugin {

    private static final Logger LOGGER = LoggerFactory.getLogger(CustomSwaggerOperationPlugin.class);

    private final ObjectMapper mapper = new ObjectMapper();

    @Override
    public void apply(OperationContext context) {
        SwaggerExtraData swaggerExtraData = new SwaggerExtraData();
        Permission permission = context.findAnnotation(Permission.class).orNull();
        if (permission != null) {
            PermissionData permissionData = new PermissionData();
            permissionData.setCode(permission.code());
            permissionData.setAction(context.getName());
            permissionData.setPermissionLevel(permission.level().value());
            permissionData.setPermissionLogin(permission.permissionLogin());
            permissionData.setPermissionPublic(permission.permissionPublic());
            permissionData.setPermissionWithin(permission.permissionWithin());
            permissionData.setPermissionSign(permission.permissionSign());
            permissionData.setRoles(permission.roles());
            permissionData.setTags(permission.tags());

            // 加入升级API
            String status = Arrays.stream(permission.status())
                    .map(PermissionStatus::code)
                    .collect(Collectors.joining(BaseConstants.Symbol.COMMA));
            permissionData.setStatus(status);
            permissionData.setUpgradeApiPath(permission.upgradeApiPath());
            permissionData.setUpgradeApiMethod(permission.upgradeApiMethod());

            swaggerExtraData.setPermission(permissionData);
        }
        Label label = context.findAnnotation(Label.class).orNull();
        if (label != null) {
            LabelData labelData = new LabelData();
            labelData.setRoleName(label.roleName());
            labelData.setType(label.type());
            labelData.setLevel(label.level());
            labelData.setLabelName(label.labelName());
            swaggerExtraData.setLabel(labelData);
        }
        if (swaggerExtraData.getPermission() != null || swaggerExtraData.getLabel() != null) {
            try {
                context.operationBuilder().notes(mapper.writeValueAsString(swaggerExtraData));
            } catch (JsonProcessingException e) {
                LOGGER.info(e.getMessage());
            }
        }
    }

    @Override
    public boolean supports(DocumentationType delimiter) {
        return true;
    }

}
