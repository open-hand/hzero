package org.hzero.file.api.controller.v1;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.hzero.core.base.BaseConstants;
import org.hzero.file.app.service.WatermarkService;
import org.hzero.file.config.FileSwaggerApiConfig;
import org.hzero.file.infra.util.CodeUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import io.choerodon.core.iam.ResourceLevel;
import io.choerodon.swagger.annotation.Permission;

/**
 * 文件水印API
 *
 * @author shuangfei.zhu@hand-china.com 2020-02-13 13:45:12
 */
@Api(tags = FileSwaggerApiConfig.WATERMARK_SITE)
@RestController("watermarkSiteController.v1")
@RequestMapping("/v1/watermark")
public class WatermarkSiteController {

    private final WatermarkService watermarkService;

    @Autowired
    public WatermarkSiteController(WatermarkService watermarkService) {
        this.watermarkService = watermarkService;
    }

    @Permission(permissionLogin = true, level = ResourceLevel.SITE)
    @ApiOperation(value = "根据文件key为pdf并添加水印")
    @GetMapping("/with-config/key")
    public void watermarkByKey(HttpServletRequest request, HttpServletResponse response,
                               @RequestParam String fileKey,
                               @RequestParam String watermarkCode,
                               String context) {
        fileKey = CodeUtils.decode(fileKey);
        watermarkService.watermarkWithConfigByKey(request, response, BaseConstants.DEFAULT_TENANT_ID, fileKey, watermarkCode, context);
    }

    @Permission(permissionLogin = true, level = ResourceLevel.SITE)
    @ApiOperation(value = "根据文件url为pdf并添加水印")
    @GetMapping("/with-config/url")
    public void watermarkByUrl(HttpServletRequest request, HttpServletResponse response,
                               @RequestParam String bucketName,
                               String storageCode,
                               @RequestParam String url,
                               @RequestParam String watermarkCode,
                               String context,
                               String contextBucket) {
        url = CodeUtils.decode(url);
        watermarkService.watermarkWithConfigByUrl(request, response, BaseConstants.DEFAULT_TENANT_ID, storageCode, bucketName, url, watermarkCode, context, contextBucket);
    }
}
