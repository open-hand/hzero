package org.hzero.report.api.controller.v1;

import java.io.IOException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.hzero.core.base.BaseController;
import org.hzero.core.util.Results;
import org.hzero.report.config.ReportSwaggerApiConfig;
import org.hzero.report.infra.util.CodeUtils;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import io.choerodon.swagger.annotation.Permission;

/**
 * 生成图形码
 *
 * @author shuangfei.zhu@hand-china.com 2019-11-15 15:36:59
 */
@Api(tags = ReportSwaggerApiConfig.CODE)
@RestController("codeController.v1")
@RequestMapping("/v1/codes")
public class CodeController extends BaseController {

    @ApiOperation(value = "获取图形码")
    @Permission(permissionPublic = true)
    @GetMapping
    public ResponseEntity<Void> getGraph(HttpServletResponse response,
                                         @RequestParam String text,
                                         @RequestParam(required = false, defaultValue = "QR") String codeType,
                                         Integer width, Integer height,
                                         @RequestParam(required = false, defaultValue = "UTF-8") String characterEncoding,
                                         @RequestParam(required = false, defaultValue = "code128") String barCodeType) throws IOException {
        ServletOutputStream outputStream = response.getOutputStream();
        outputStream.write(CodeUtils.generateCode(codeType, text, width, height, characterEncoding, barCodeType));
        outputStream.flush();
        outputStream.close();
        return Results.success();
    }

    @ApiOperation(value = "获取二维码图片")
    @Permission(permissionPublic = true)
    @GetMapping("/qr-code")
    public ResponseEntity<Void> getCode(@RequestParam String text, HttpServletResponse response,
                                        @RequestParam(required = false, defaultValue = "240") Integer width,
                                        @RequestParam(required = false, defaultValue = "240") Integer height,
                                        @RequestParam(required = false, defaultValue = "UTF-8") String characterEncoding) throws IOException {
        ServletOutputStream outputStream = response.getOutputStream();
        outputStream.write(CodeUtils.generateQrCode(text, width, height, characterEncoding));
        outputStream.flush();
        outputStream.close();
        return Results.success();
    }

    @ApiOperation(value = "获取二进制二维码图片")
    @Permission(permissionPublic = true)
    @GetMapping("/qr-code/bytes")
    public byte[] getByteQrCode(@RequestParam String text,
                                @RequestParam(required = false, defaultValue = "240") Integer width,
                                @RequestParam(required = false, defaultValue = "240") Integer height,
                                @RequestParam(required = false, defaultValue = "UTF-8") String characterEncoding) {
        return CodeUtils.generateQrCode(text, width, height, characterEncoding);
    }

    @ApiOperation(value = "获取条形码图片")
    @Permission(permissionPublic = true)
    @GetMapping("/bar-code")
    public ResponseEntity<Void> getBarCode(@RequestParam String text, HttpServletResponse response,
                                           @RequestParam(required = false, defaultValue = "300") Integer width,
                                           @RequestParam(required = false, defaultValue = "50") Integer height,
                                           @RequestParam(required = false, defaultValue = "UTF-8") String characterEncoding,
                                           @RequestParam(required = false) String barCodeType) throws IOException {
        ServletOutputStream outputStream = response.getOutputStream();
        outputStream.write(CodeUtils.generateBarCode(text, width, height, characterEncoding, barCodeType));
        outputStream.flush();
        outputStream.close();
        return Results.success();
    }

    @ApiOperation(value = "获取二进制条形码图片")
    @Permission(permissionPublic = true)
    @GetMapping("/bar-code/bytes")
    public byte[] getByteBarCode(@RequestParam String text,
                                 @RequestParam(required = false, defaultValue = "300") Integer width,
                                 @RequestParam(required = false, defaultValue = "50") Integer height,
                                 @RequestParam(required = false, defaultValue = "UTF-8") String characterEncoding,
                                 @RequestParam(required = false) String barCodeType) {
        return CodeUtils.generateBarCode(text, width, height, characterEncoding, barCodeType);
    }
}
