package org.hzero.iam.api.controller.v1;

import org.hzero.core.util.Results;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

/**
 *
 * @author mingwei.liu@hand-china.com 2018/9/19
 */
@Api("openApiController")
@RestController("openApiController.v1")
public class OpenApiController {
    private final static Logger LOGGER = LoggerFactory.getLogger(OpenApiController.class);

    @ApiOperation("开放接口调用")
    @GetMapping("/v1/api/rest/invoke")
    public ResponseEntity<?> invoke(@RequestParam(value = "organizationId") Long organizationId,
                                 @RequestParam("serverCode") String serverCode,
                                 @RequestParam("interfaceCode") String interfaceCode) {
        return Results.success(String.format("organizationId=%s, serverCode=%s, interfaceCode=%s",
                organizationId, serverCode, interfaceCode));
    }

    @ApiOperation("OAuth2Authentication验证")
    @GetMapping("/v1/oauth2/verify")
    public ResponseEntity<?> verify() {
        OAuth2Authentication oAuth2Authentication = this.getOAuth2Authentication();
        if (oAuth2Authentication != null) {
            LOGGER.info("principal={}", oAuth2Authentication.getPrincipal().toString());
            LOGGER.info("user auth={}", oAuth2Authentication.getUserAuthentication().toString());
            LOGGER.info("clientId={}", oAuth2Authentication.getOAuth2Request().getClientId());
            LOGGER.info("clientId-RequestParams={}", oAuth2Authentication.getOAuth2Request().getRequestParameters().get("client_id"));
            return Results.success(oAuth2Authentication.getOAuth2Request().getRequestParameters().get("client_id"));
        }
        return Results.success("authentication is null");
    }

    /**
     * 获取OAuth2Authentication
     *
     * @return OAUTH2: OAuth2Authentication, 其他: null
     */
    private OAuth2Authentication getOAuth2Authentication() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication instanceof OAuth2Authentication) {
            OAuth2Authentication oAuth2Authentication = (OAuth2Authentication) authentication;

            return oAuth2Authentication;
        }

        return null;
    }
}
