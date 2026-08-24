package org.hzero.platform.api.controller.v1;

import java.util.Map;

import org.hzero.boot.platform.encrypt.vo.EncryptVO;
import org.hzero.core.util.Results;
import org.hzero.platform.app.service.ToolPassService;
import org.hzero.platform.config.PlatformSwaggerApiConfig;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import io.choerodon.swagger.annotation.Permission;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

/**
 * 密码加密工具接口
 *
 * @author bojiangzhou
 */
@Api(tags = PlatformSwaggerApiConfig.TOOL_PASSWORD)
@RequestMapping("/v1/tool/pass")
@RestController("toolPassController.v1")
public class ToolPassController {

    private final ToolPassService toolPassService;

    public ToolPassController(ToolPassService toolPassService) {
        this.toolPassService = toolPassService;
    }

    @ApiOperation(value = "获取密码加密的公钥")
    @Permission(permissionLogin = true)
    @GetMapping("/public-key")
    public ResponseEntity<EncryptVO> getPublicKey() {
        return Results.success(toolPassService.getPublicKey());
    }

    @ApiOperation(value = "获取加密后的密码")
    @Permission(permissionLogin = true)
    @PostMapping("/encrypt")
    public ResponseEntity<Map<String, String>> encryptPass(@RequestParam("pass") String pass) {

        return Results.success(toolPassService.encryptPass(pass));
    }

}
