package org.hzero.fragment.controller;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

import io.choerodon.swagger.annotation.Permission;

import org.hzero.core.base.TokenConstants;
import org.hzero.core.util.Results;
import org.hzero.core.util.TokenUtils;
import org.hzero.fragment.config.FragmentConfig;
import org.hzero.fragment.service.FragmentService;
import org.hzero.mybatis.helper.DataSecurityHelper;

/**
 * 分片上传相关接口
 *
 * @author shuangfei.zhu@hand-china.com 2020/02/18 10:23
 */
@Controller("fragmentController.v1")
public class FragmentController {

    @Autowired
    private FragmentService fragmentService;
    @Autowired
    private FragmentConfig fragmentConfig;
    @Autowired
    private ObjectMapper objectMapper;

    @GetMapping("/v1/{organizationId}/upload")
    @Permission(permissionLogin = true)
    public ModelAndView indexPage(ModelAndView modelAndView, @PathVariable Long organizationId, HttpServletRequest request) throws JsonProcessingException {
        Map<String, String> params = new HashMap<>(16);
        request.getParameterMap().forEach((k, v) -> {
            if (!"access_token".equals(k)) {
                params.put(k, v[0]);
            }
        });
        modelAndView.addObject("authHeaderName", TokenConstants.HEADER_AUTH);
        modelAndView.addObject("accessToken", TokenUtils.getToken());
        modelAndView.addObject("organizationId", organizationId);
        modelAndView.addObject("gatewayPath", fragmentConfig.getGatewayPath());
        modelAndView.addObject("args", DataSecurityHelper.encrypt(objectMapper.writeValueAsString(params)));
        modelAndView.setViewName("index");
        return modelAndView;
    }

    @PostMapping("/v1/{organizationId}/upload/check-block")
    @ApiOperation(value = "查看当前分片是否上传")
    @Permission(permissionLogin = true)
    @ResponseBody
    public ResponseEntity<Integer> checkMd5(@PathVariable Long organizationId, String chunk, String chunkSize, String guid) {
        return Results.success(fragmentService.checkMd5(chunk, chunkSize, guid));
    }

    @PostMapping("/v1/{organizationId}/upload/save")
    @ApiOperation(value = "上传分片")
    @Permission(permissionLogin = true)
    @ResponseBody
    public ResponseEntity<Void> upload(@PathVariable Long organizationId, @RequestParam MultipartFile file, Integer chunk, String guid) {
        fragmentService.upload(file, chunk, guid);
        return Results.success();
    }

    @PostMapping("/v1/{organizationId}/upload/combine")
    @ApiOperation(value = "合并文件(前后端不分离使用)")
    @Permission(permissionLogin = true)
    @ResponseBody
    public ResponseEntity<String> combineBlock(@PathVariable Long organizationId, String guid, String fileName, String args) throws IOException {
        return Results.success(fragmentService.combineUpload(guid, organizationId, fileName, objectMapper.readValue(DataSecurityHelper.decrypt(args), new TypeReference<Map<String, String>>() {
        })));
    }

    @PostMapping("/v1/{organizationId}/upload/fragment-combine")
    @ApiOperation(value = "分片文件合并(独立前端使用)")
    @Permission(permissionLogin = true)
    @ResponseBody
    public ResponseEntity<String> fragmentCombineBlock(@PathVariable Long organizationId, String guid, String fileName, @RequestBody(required = false) Map<String, String> args) {
        return Results.success(fragmentService.combineUpload(guid, organizationId, fileName, args));
    }
}
