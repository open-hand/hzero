package io.choerodon.swagger.controller;

import io.choerodon.swagger.swagger.CustomSwagger;
import io.choerodon.swagger.swagger.ForwardedHeader;
import io.choerodon.swagger.swagger.extra.ExtraData;
import io.choerodon.swagger.swagger.extra.ExtraDataInitialization;
import io.swagger.models.Swagger;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import org.springframework.web.util.UriComponents;
import springfox.documentation.annotations.ApiIgnore;
import springfox.documentation.service.Documentation;
import springfox.documentation.spring.web.DocumentationCache;
import springfox.documentation.spring.web.json.Json;
import springfox.documentation.spring.web.json.JsonSerializer;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.mappers.ServiceModelToSwagger2Mapper;

import javax.servlet.http.HttpServletRequest;
import java.util.Optional;

import static com.google.common.base.Strings.isNullOrEmpty;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static org.springframework.util.StringUtils.commaDelimitedListToStringArray;
import static org.springframework.util.StringUtils.hasText;
import static org.springframework.util.StringUtils.split;

/**
 * @author wuguokai
 */
@Controller
@ApiIgnore
public class CustomController {

    private static final String CUSTOM_SWAGGER_URL = "/v2/choerodon/api-docs";

    private static final String HAL_MEDIA_TYPE = "application/hal+json";

    @Value("${springfox.documentation.swagger.v2.host:DEFAULT}")
    private String hostNameOverride;

    private final JsonSerializer jsonSerializer;
    private final DocumentationCache documentationCache;
    private final ServiceModelToSwagger2Mapper mapper;
    private final ExtraDataInitialization extraDataInitialization;

    public CustomController(JsonSerializer jsonSerializer,
                            DocumentationCache documentationCache,
                            ServiceModelToSwagger2Mapper mapper,
                            ExtraDataInitialization extraDataInitialization) {
        this.jsonSerializer = jsonSerializer;
        this.documentationCache = documentationCache;
        this.mapper = mapper;
        this.extraDataInitialization = extraDataInitialization;
    }

    @ResponseBody
    @GetMapping(value = CUSTOM_SWAGGER_URL, produces = {APPLICATION_JSON_VALUE, HAL_MEDIA_TYPE})
    public ResponseEntity<Json> getDocumentation(
            @RequestParam(value = "group", required = false) String swaggerGroup,
            HttpServletRequest servletRequest) {
        String groupName = Optional.ofNullable(swaggerGroup).orElse(Docket.DEFAULT_GROUP_NAME);
        Documentation documentation = documentationCache.documentationByGroup(groupName);
        if (documentation == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        Swagger swagger = mapper.mapDocumentation(documentation);
        if (isNullOrEmpty(swagger.getHost())) {
            final UriComponents uriComponents = componentsFrom(servletRequest);
            swagger.basePath(isNullOrEmpty(uriComponents.getPath()) ? "/" : uriComponents.getPath());
            swagger.host(hostName(uriComponents));
        }
        CustomSwagger customSwagger = new CustomSwagger();
        BeanUtils.copyProperties(swagger, customSwagger);
        customSwagger.setExtraData(postProcessExtraData());
        return new ResponseEntity<>(jsonSerializer.toJson(customSwagger), HttpStatus.OK);
    }

    private ExtraData postProcessExtraData() {
        return extraDataInitialization.getExtraData();
    }

    private String hostName(UriComponents uriComponents) {
        if ("DEFAULT".equals(hostNameOverride)) {
            String host = uriComponents.getHost();
            int port = uriComponents.getPort();
            if (port > -1) {
                return String.format("%s:%d", host, port);
            }
            return host;
        }
        return hostNameOverride;
    }

    static UriComponents componentsFrom(HttpServletRequest request) {
        ServletUriComponentsBuilder builder = ServletUriComponentsBuilder.fromServletMapping(request);

        ForwardedHeader forwarded = ForwardedHeader.of(request.getHeader(ForwardedHeader.NAME));
        String proto = hasText(forwarded.getProto()) ? forwarded.getProto() : request.getHeader("X-Forwarded-Proto");
        String forwardedSsl = request.getHeader("X-Forwarded-Ssl");

        if (hasText(proto)) {
            builder.scheme(proto);
        } else if (hasText(forwardedSsl) && "on".equalsIgnoreCase(forwardedSsl)) {
            builder.scheme("https");
        }

        String host = forwarded.getHost();
        host = hasText(host) ? host : request.getHeader("X-Forwarded-Host");

        if (!hasText(host)) {
            return builder.build();
        }

        // 修复forwarded头信息解析失败导致的swagger文档拉取失败的问题
        if (host.startsWith("\"") && host.endsWith("\"")) {
            host = host.substring(1, host.length() - 1);
        }

        String[] hosts = commaDelimitedListToStringArray(host);
        String hostToUse = hosts[0];

        if (hostToUse.contains(":")) {

            String[] hostAndPort = split(hostToUse, ":");

            assert hostAndPort != null;
            builder.host(hostAndPort[0]);
            try {
                builder.port(Integer.parseInt(hostAndPort[1]));
            } catch (NumberFormatException e) {
                builder.port(-1);
            }
        } else {
            builder.host(hostToUse);
            builder.port(-1); // reset port if it was forwarded from default port
        }

        String port = request.getHeader("X-Forwarded-Port");

        if (hasText(port)) {
            try {
                builder.port(Integer.parseInt(port));
            } catch (NumberFormatException e) {
                builder.port(-1);
            }
        }

        return builder.build();
    }

}
