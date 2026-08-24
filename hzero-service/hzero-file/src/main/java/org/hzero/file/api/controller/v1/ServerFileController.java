package org.hzero.file.api.controller.v1;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.hzero.core.util.Results;
import org.hzero.file.app.service.ServerFileService;
import org.hzero.file.config.FileSwaggerApiConfig;
import org.hzero.file.domain.entity.File;
import org.hzero.file.infra.util.CodeUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import io.choerodon.core.iam.ResourceLevel;
import io.choerodon.swagger.annotation.Permission;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;

/**
 * 服务器文件管理
 *
 * @author shuangfei.zhu@hand-china.com 2019/07/04 17:39
 */
@CrossOrigin
@Api(tags = FileSwaggerApiConfig.SERVER_FILE)
@Controller("serverFileController.v1")
public class ServerFileController {

    private final ServerFileService serverFileService;

    @Autowired
    public ServerFileController(ServerFileService serverFileService) {
        this.serverFileService = serverFileService;
    }

    @Permission(permissionLogin = true, level = ResourceLevel.ORGANIZATION)
    @ApiOperation(value = "服务器Multipart上传文件")
    @PostMapping("/v1/{organizationId}/server-file/upload/multipart")
    @ResponseBody
    public ResponseEntity<String> uploadFile(@PathVariable Long organizationId,
                                             @ApiParam(value = "服务器上传配置编码", required = true) @RequestParam String configCode,
                                             @ApiParam(value = "上传目录") @RequestParam(value = "path", required = false) String path,
                                             @ApiParam(value = "文件名") @RequestParam(value = "fileName", required = false) String fileName,
                                             @ApiParam(value = "是否覆盖已有文件") @RequestParam(defaultValue = "0") Integer cover,
                                             @ApiParam(value = "上传文件", required = true) @RequestParam MultipartFile file) {
        String uuid = serverFileService.uploadFile(organizationId, configCode, path, fileName, cover, file);
        return Results.success(uuid);
    }

    @Permission(permissionLogin = true, level = ResourceLevel.ORGANIZATION)
    @ApiOperation(value = "根据附件id查询服务器文件记录")
    @GetMapping("/v1/{organizationId}/server-file")
    @ResponseBody
    public ResponseEntity<List<File>> listFile(@PathVariable Long organizationId,
                                               @ApiParam(value = "附件Id") @RequestParam String attachmentId) {
        return Results.success(serverFileService.listFile(organizationId, attachmentId));
    }

    @Permission(permissionLogin = true, level = ResourceLevel.ORGANIZATION)
    @ApiOperation(value = "服务器文件下载")
    @GetMapping("/v1/{organizationId}/server-file/download")
    @ResponseBody
    public void downloadFile(HttpServletRequest request, HttpServletResponse response,
                             @PathVariable Long organizationId,
                             @ApiParam(value = "服务器编码", required = true) @RequestParam String serverCode,
                             @ApiParam(value = "文件路径", required = true) @RequestParam String url) {
        url = CodeUtils.decode(url);
        serverFileService.downloadFile(organizationId, serverCode, url, request, response);
    }
}
