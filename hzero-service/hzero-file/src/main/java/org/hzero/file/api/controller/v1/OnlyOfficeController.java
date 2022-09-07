package org.hzero.file.api.controller.v1;

import org.hzero.boot.file.dto.GenerateHtmlByKeyDTO;
import org.hzero.boot.file.dto.GenerateHtmlByUrlDTO;
import org.hzero.core.util.Results;
import org.hzero.file.api.dto.OnlyOfficeCallbackDTO;
import org.hzero.file.api.dto.SaveCallbackParamDTO;
import org.hzero.file.app.service.OnlyOfficeService;
import org.hzero.file.config.FileSwaggerApiConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import io.choerodon.core.iam.ResourceLevel;
import io.choerodon.swagger.annotation.Permission;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * onlyOffice接口
 *
 * @author shuangfei.zhu@hand-china.com 2019/06/26 17:14
 */
@Api(tags = FileSwaggerApiConfig.ONLY_OFFICE)
@RestController("onlyOfficeController.v1")
@RequestMapping("/v1")
public class OnlyOfficeController {

    private final OnlyOfficeService onlyOfficeService;

    @Autowired
    public OnlyOfficeController(OnlyOfficeService onlyOfficeService) {
        this.onlyOfficeService = onlyOfficeService;
    }

    @ApiOperation(value = "根据文件url获取在线编辑页面")
    @Permission(level = ResourceLevel.ORGANIZATION, permissionLogin = true)
    @PostMapping("/{organizationId}/only-office/url")
    public ResponseEntity<String> generateHtmlByUrl(@PathVariable Long organizationId, @RequestBody GenerateHtmlByUrlDTO generateHtmlParam) {
        return Results.success(onlyOfficeService.generateHtmlByUrl(organizationId, generateHtmlParam));
    }

    @ApiOperation(value = "根据文件key获取在线编辑页面")
    @Permission(level = ResourceLevel.ORGANIZATION, permissionLogin = true)
    @PostMapping("/{organizationId}/only-office/key")
    public ResponseEntity<String> generateHtmlByKey(@PathVariable Long organizationId, @RequestBody GenerateHtmlByKeyDTO generateHtmlParam) {
        return Results.success(onlyOfficeService.generateHtmlByKey(organizationId, generateHtmlParam));
    }

    /**
     * 这个接口的参数和请求方式是固定的
     */
    @ApiOperation(value = "编辑文件回调接口")
    @Permission(permissionPublic = true)
    @PostMapping("/only-office/save")
    public ResponseEntity<Void> updateFile(@RequestBody SaveCallbackParamDTO saveCallbackParam) {
        onlyOfficeService.updateFile(saveCallbackParam);
        return Results.success();
    }

    /**
     * 这个接口的参数和请求方式是固定的
     */
    @ApiOperation(value = "编辑文件回调接口")
    @Permission(permissionPublic = true)
    @PostMapping("/only-office/save/file")
    public ResponseEntity<Void> update(@RequestBody OnlyOfficeCallbackDTO callback, HttpServletResponse response) {
        onlyOfficeService.update(callback);
        PrintWriter writer;
        try {
            writer = response.getWriter();
            writer.write("{\"error\":0}");
            writer.close();
        } catch (IOException e) {
            return Results.success();
        }
        return Results.success();
    }
}
