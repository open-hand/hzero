/*
 *
 *  Copyright 2015-2016 the original author or authors.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 *
 *
 */

package io.choerodon.swagger.swagger;

import com.google.common.base.MoreObjects;
import com.google.common.base.Optional;
import com.google.common.collect.Lists;
import io.choerodon.swagger.annotation.CustomPageRequest;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import springfox.documentation.builders.ParameterBuilder;
import springfox.documentation.schema.ModelRef;
import springfox.documentation.service.AllowableValues;
import springfox.documentation.service.Parameter;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spi.service.OperationBuilderPlugin;
import springfox.documentation.spi.service.contexts.OperationContext;
import springfox.documentation.swagger.common.SwaggerPluginSupport;

import java.util.List;

import static com.google.common.base.Strings.emptyToNull;
import static springfox.documentation.schema.Types.isBaseType;
import static springfox.documentation.swagger.common.SwaggerPluginSupport.pluginDoesApply;
import static springfox.documentation.swagger.schema.ApiModelProperties.allowableValueFromString;

/**
 * @author superlee
 */
@Component
@Order(SwaggerPluginSupport.SWAGGER_PLUGIN_ORDER + 1000)
public class OperationCustomPageRequestReader implements OperationBuilderPlugin {

  @Override
  public void apply(OperationContext context) {
    context.operationBuilder().parameters(readParameters(context));
  }

  @Override
  public boolean supports(DocumentationType delimiter) {
    return pluginDoesApply(delimiter);
  }

  static Parameter implicitParameter(ApiImplicitParam param) {
    ModelRef modelRef = maybeGetModelRef(param);
    return new ParameterBuilder()
            .name(param.name())
            .description(param.value())
            .defaultValue(param.defaultValue())
            .required(param.required())
            .allowMultiple(param.allowMultiple())
            .modelRef(modelRef)
            .allowableValues(allowableValueFromString(param.allowableValues()))
            .parameterType(param.paramType())
            .parameterAccess(param.access())
            .build();
  }

  private static ModelRef maybeGetModelRef(ApiImplicitParam param) {
    String dataType = MoreObjects.firstNonNull(emptyToNull(param.dataType()), "string");
    AllowableValues allowableValues = null;
    if (isBaseType(dataType)) {
      allowableValues = allowableValueFromString(param.allowableValues());
    }
    if (param.allowMultiple()) {
      return new ModelRef("", new ModelRef(dataType, allowableValues));
    }
    return new ModelRef(dataType, allowableValues);
  }

  private List<Parameter> readParameters(OperationContext context) {
    Optional<CustomPageRequest> annotation = context.findAnnotation(CustomPageRequest.class);
    List<Parameter> parameters = Lists.newArrayList();
    if (annotation.isPresent() && null != annotation.get().apiImplicitParams()) {
      ApiImplicitParams apiImplicitParams = annotation.get().apiImplicitParams();
      for (ApiImplicitParam param : apiImplicitParams.value()) {
        parameters.add(OperationCustomPageRequestReader.implicitParameter(param));
      }
    }

    return parameters;
  }
}
