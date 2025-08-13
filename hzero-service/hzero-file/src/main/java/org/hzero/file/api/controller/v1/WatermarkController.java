package org.hzero.file.api.controller.v1;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.itextpdf.text.DocumentException;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.hzero.file.app.service.WatermarkService;
import org.hzero.file.config.FileSwaggerApiConfig;
import org.hzero.file.infra.util.CodeUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import io.choerodon.core.iam.ResourceLevel;
import io.choerodon.swagger.annotation.Permission;

/**
 * 文件水印API
 *
 * @author shuangfei.zhu@hand-china.com 2020-02-13 13:45:12
 */
@Api(tags = FileSwaggerApiConfig.WATERMARK)
@RestController("watermarkController.v1")
@RequestMapping("/v1/{organizationId}/watermark")
public class WatermarkController {

    private final WatermarkService watermarkService;

    @Autowired
    public WatermarkController(WatermarkService watermarkService) {
        this.watermarkService = watermarkService;
    }

    @Permission(permissionLogin = true, level = ResourceLevel.ORGANIZATION)
    @ApiOperation(value = "根据文件key为pdf并添加水印")
    @GetMapping("/with-config/key")
    public void watermarkWithConfigByKey(HttpServletRequest request, HttpServletResponse response,
                                         @ApiParam(value = "租户ID", required = true) @PathVariable Long organizationId,
                                         @RequestParam String fileKey,
                                         @RequestParam String watermarkCode,
                                         String context) {
        fileKey = CodeUtils.decode(fileKey);
        watermarkService.watermarkWithConfigByKey(request, response, organizationId, fileKey, watermarkCode, context);
    }

    @Permission(permissionLogin = true, level = ResourceLevel.ORGANIZATION)
    @ApiOperation(value = "根据文件url为pdf并添加水印")
    @GetMapping("/with-config/url")
    public void watermarkWithConfigByUrl(HttpServletRequest request, HttpServletResponse response,
                                         @ApiParam(value = "租户ID", required = true) @PathVariable Long organizationId,
                                         @RequestParam String bucketName,
                                         String storageCode,
                                         @RequestParam String url,
                                         @RequestParam String watermarkCode,
                                         String context,
                                         String contextBucket) {
        url = CodeUtils.decode(url);
        watermarkService.watermarkWithConfigByUrl(request, response, organizationId, storageCode, bucketName, url, watermarkCode, context, contextBucket);
    }

    @Permission(permissionLogin = true, level = ResourceLevel.ORGANIZATION)
    @ApiOperation(value = "水印预览")
    @GetMapping("/preview")
    public void preview(HttpServletRequest request, HttpServletResponse response, @PathVariable Long organizationId, @RequestParam String watermarkCode) throws DocumentException {
        watermarkService.preview(request, response, organizationId, watermarkCode);
    }
}
