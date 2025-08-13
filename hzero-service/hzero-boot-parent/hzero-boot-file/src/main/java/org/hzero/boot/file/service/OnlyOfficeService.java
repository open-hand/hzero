package org.hzero.boot.file.service;

import java.io.*;
import java.util.HashMap;
import java.util.Map;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.lang3.StringUtils;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.Velocity;
import org.hzero.boot.autoconfigure.file.OnlyOfficeConfigProperties;
import org.hzero.boot.file.constant.BootFileConstant;
import org.hzero.boot.file.constant.DocumentType;
import org.hzero.boot.file.dto.*;
import org.hzero.boot.file.util.HttpUtils;
import org.hzero.core.base.BaseConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.ClassPathResource;

import io.choerodon.core.exception.CommonException;
import io.choerodon.core.oauth.CustomUserDetails;
import io.choerodon.core.oauth.DetailsHelper;

/**
 * OnlyOffice 处理服务类
 *
 * @author xianzhi.chen@hand-china.com 2019年4月29日下午4:57:25
 */
public class OnlyOfficeService {

    private static final Logger logger = LoggerFactory.getLogger(OnlyOfficeService.class);

    private static final String ONLY_OFFICE_TEMPLATE_PATH = "template" + File.separator + "onlyoffice-html-template.html.vm";

    private final OnlyOfficeConfigProperties config;
    private final ObjectMapper objectMapper;

    public OnlyOfficeService(OnlyOfficeConfigProperties config, ObjectMapper objectMapper) {
        this.config = config;
        this.objectMapper = objectMapper;
    }

    public String generateHtml(String fileKey, String tokenUrl, String fileName, PermissionDTO permission, String extraParam, String callBackUrl) {
        return HttpUtils.sendRequestToDocumentServer(this.generateRequestParam(fileKey, tokenUrl, fileName, permission, extraParam, callBackUrl)).toString();
    }

    public String generateHtml(String key, String tokenUrl, String fileName, PermissionDTO permission) {
        String fileType = fileName.substring(fileName.lastIndexOf(BaseConstants.Symbol.POINT) + 1);
        return this.generateHtml(key, tokenUrl, fileName, fileType, permission, config.getCallBackUrl());
    }

    /**
     * 构造生成html页面参数
     *
     * @param fileKey     文件的key
     * @param tokenUrl    带权限的文件url
     * @param fileName    文件名
     * @param permission  权限信息
     * @param extraParam  回调参数
     * @param callBackUrl 回调地址
     * @return documentServer请求参数
     */
    private RequestParamDTO generateRequestParam(String fileKey, String tokenUrl, String fileName, PermissionDTO permission, String extraParam, String callBackUrl) {
        // 文件参数
        DocumentDTO document = new DocumentDTO().setTitle(fileName).setFileId(fileKey).setUrl(tokenUrl);
        CustomUserDetails userDetails = DetailsHelper.getUserDetails();
        // 编辑人参数
        EditorDTO editor = new EditorDTO().setCallbackUrl(callBackUrl).setUserId(String.valueOf(userDetails.getUserId())).setUserName(userDetails.getRealName());
        Customization customization = new Customization().setLogoImage(config.getLogoImage()).setLogoLink(config.getLogoLink()).setPreloaderLogoImage(config.getPreloaderLogoImage());
        GenerateHtmlParamDTO generateHtmlParam = new GenerateHtmlParamDTO()
                .setDocument(document)
                .setEditor(editor)
                .setExtra(extraParam)
                .setCustomization(customization)
                .setPermissions(permission);
        try {
            RequestParamDTO requestParam = new RequestParamDTO()
                    .setAccessToken(config.getToken())
                    .setDocServerUrl(config.getDocServerUrl())
                    .setBodyJson(objectMapper.writeValueAsString(generateHtmlParam));
            logger.debug("generateRequestParam param : {}", requestParam);
            return requestParam;
        } catch (Exception e) {
            logger.error("generateRequestParam Exception: {}", e.getMessage());
            throw new CommonException(BootFileConstant.ErrorCode.GENERATE_HTML);
        }
    }

    /**
     * 生成HTML
     *
     * @param key         onlyOffice文件唯一标识
     * @param tokenUrl    带权限的文件url
     * @param fileName    文件名
     * @param fileType    文件类型
     * @param permission  权限信息
     * @param callBackUrl 回调地址
     * @return html
     */
    private String generateHtml(String key, String tokenUrl, String fileName, String fileType, PermissionDTO permission, String callBackUrl) {
        try (Reader reader = new InputStreamReader(new ClassPathResource(ONLY_OFFICE_TEMPLATE_PATH).getInputStream());
             Writer writer = new StringWriter()) {
            CustomUserDetails userDetails = DetailsHelper.getUserDetails();
            String lang = userDetails.getLanguage();
            Map<String, Object> map = new HashMap<>(16);
            map.put("docServerUrl", config.getDocServerUrl());
            map.put("fileType", fileType);
            map.put("key", key);
            map.put("fileUrl", tokenUrl);
            map.put("comment", permission.getComment() ? "true" : "false");
            map.put("download", permission.getDownload() ? "true" : "false");
            map.put("edit", permission.getEdit() ? "true" : "false");
            map.put("print", permission.getPrint() ? "true" : "false");
            map.put("review", permission.getReview() ? "true" : "false");
            map.put("documentType", DocumentType.getDocumentType(fileType));
            map.put("userId", String.valueOf(userDetails.getUserId()));
            map.put("userName", String.valueOf(userDetails.getRealName()));
            map.put("callbackUrl", callBackUrl);
            map.put("token", config.getToken());
            map.put("title", fileName);
            map.put("lang", StringUtils.isEmpty(lang) ? "zh" : lang.substring(0, 2));

            VelocityContext context = new VelocityContext(map);
            Velocity.evaluate(context, writer, fileName, reader);
            return writer.toString();
        } catch (IOException e) {
            logger.error("generate html failed ,e {}", e.getMessage());
        }
        return StringUtils.EMPTY;
    }
}

