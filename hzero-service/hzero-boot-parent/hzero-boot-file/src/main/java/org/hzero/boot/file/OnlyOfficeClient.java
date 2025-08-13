package org.hzero.boot.file;

import java.io.IOException;
import java.io.InputStream;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.lang3.StringUtils;
import org.hzero.boot.autoconfigure.file.OnlyOfficeConfigProperties;
import org.hzero.boot.file.constant.BootFileConstant;
import org.hzero.boot.file.dto.*;
import org.hzero.boot.file.service.OnlyOfficeCacheService;
import org.hzero.boot.file.service.OnlyOfficeService;
import org.hzero.boot.file.util.HttpUtils;
import org.hzero.core.base.BaseConstants;
import org.hzero.core.util.EncryptionUtils;
import org.hzero.core.util.FileUtils;
import org.hzero.core.util.FilenameUtils;
import org.hzero.core.util.UUIDUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.Assert;

import io.choerodon.core.exception.CommonException;

/**
 * OnlyOffice 客户端
 *
 * @author xianzhi.chen@hand-china.com 2019年4月29日下午4:57:46
 */
public class OnlyOfficeClient {

    private static final Logger logger = LoggerFactory.getLogger(OnlyOfficeClient.class);

    private final FileClient fileClient;
    private final ObjectMapper objectMapper;
    private final OnlyOfficeService onlyOfficeService;
    private final OnlyOfficeConfigProperties onlyOfficeConfig;
    private final OnlyOfficeCacheService onlyOfficeCacheService;

    public OnlyOfficeClient(FileClient fileClient,
                            OnlyOfficeService onlyOfficeService,
                            OnlyOfficeConfigProperties onlyOfficeConfig,
                            ObjectMapper objectMapper,
                            OnlyOfficeCacheService onlyOfficeCacheService) {
        this.fileClient = fileClient;
        this.objectMapper = objectMapper;
        this.onlyOfficeService = onlyOfficeService;
        this.onlyOfficeConfig = onlyOfficeConfig;
        this.onlyOfficeCacheService = onlyOfficeCacheService;
    }

    /**
     * 根据url获取HTML信息
     *
     * @param tenantId    租户
     * @param storageCode 存储编码
     * @param url         文件url
     * @param permission  编辑权限
     * @return html
     */
    public String generateHtmlByUrl(Long tenantId, String bucketName, String storageCode, String url, PermissionDTO permission) {
        if (BootFileConstant.EditType.HOFFICE.equals(onlyOfficeConfig.getEditType())) {
            String tokenUrl = fileClient.getSignedUrl(tenantId, bucketName, storageCode, url);
            String fileName = FilenameUtils.getFileName(url);
            ExtraDTO extraDTO = new ExtraDTO()
                    .setTenantId(tenantId)
                    .setType(BootFileConstant.FileIdType.URL)
                    .setBucketName(bucketName)
                    .setStorageCode(storageCode);
            try {
                return onlyOfficeService.generateHtml(url, tokenUrl, fileName, permission,
                        objectMapper.writeValueAsString(extraDTO), onlyOfficeConfig.getCallBackUrl());
            } catch (Exception e) {
                logger.error("onlyOffice generateHtml method occur IOException,message was:{}", e.getLocalizedMessage());
                throw new CommonException(BootFileConstant.ErrorCode.GENERATE_HTML);
            }
        } else if (BootFileConstant.EditType.ONLY_OFFICE.equals(onlyOfficeConfig.getEditType())) {
            try {
                String fileKey = EncryptionUtils.MD5.encrypt(url);
                OnlyOfficeFileDTO fileDTO = onlyOfficeCacheService.getFile(fileKey);
                if (fileDTO == null) {
                    String key = System.currentTimeMillis() + UUIDUtils.generateUUID().substring(0, 7);
                    fileDTO = new OnlyOfficeFileDTO().setTenantId(tenantId).setBucketName(bucketName)
                            .setType(BootFileConstant.FileIdType.URL).setUrl(url)
                            .setStorageCode(storageCode).setKey(key).setFileKey(fileKey);
                    onlyOfficeCacheService.cacheFile(fileKey, fileDTO);
                }
                String tokenUrl = fileClient.getSignedUrl(tenantId, bucketName, storageCode, url);
                String fileName = FilenameUtils.getFileName(url);
                return onlyOfficeService.generateHtml(fileDTO.getKey(), tokenUrl, fileName, permission);
            } catch (Exception e) {
                logger.error("generate html failed ,e {}", e.getMessage());
            }
        }
        return StringUtils.EMPTY;
    }

    /**
     * 根据fileKey获取HTML信息
     *
     * @param tenantId   租户
     * @param fileKey    文件key
     * @param permission 编辑权限
     * @return html
     */
    public String generateHtmlByKey(Long tenantId, String fileKey, PermissionDTO permission) {
        if (BootFileConstant.EditType.HOFFICE.equals(onlyOfficeConfig.getEditType())) {
            FileSimpleDTO fileSimpleDTO = fileClient.getSignedUrl(tenantId, fileKey);
            Assert.notNull(fileSimpleDTO, BaseConstants.ErrorCode.DATA_NOT_EXISTS);
            String fileName = FilenameUtils.getFileName(fileKey);
            ExtraDTO extraDTO = new ExtraDTO().setTenantId(tenantId).setType(BootFileConstant.FileIdType.KEY);
            try {
                return onlyOfficeService.generateHtml(fileKey, fileSimpleDTO.getFileTokenUrl(), fileName, permission,
                        objectMapper.writeValueAsString(extraDTO), onlyOfficeConfig.getCallBackUrl());
            } catch (Exception e) {
                logger.error("onlyOffice generateHtml method occur IOException,message was:{}", e.getLocalizedMessage());
                throw new CommonException(BootFileConstant.ErrorCode.GENERATE_HTML);
            }
        } else if (BootFileConstant.EditType.ONLY_OFFICE.equals(onlyOfficeConfig.getEditType())) {
            try {
                fileKey = EncryptionUtils.MD5.encrypt(fileKey);
                OnlyOfficeFileDTO fileDTO = onlyOfficeCacheService.getFile(fileKey);
                if (fileDTO == null) {
                    String key = System.currentTimeMillis() + UUIDUtils.generateUUID().substring(0, 7);
                    fileDTO = new OnlyOfficeFileDTO().setFileKey(fileKey).setTenantId(tenantId).setKey(key).setType(BootFileConstant.FileIdType.KEY);
                    onlyOfficeCacheService.cacheFile(fileKey, fileDTO);
                }
                FileSimpleDTO fileSimpleDTO = fileClient.getSignedUrl(tenantId, fileKey);
                Assert.notNull(fileSimpleDTO, BaseConstants.ErrorCode.DATA_NOT_EXISTS);
                String fileName = FilenameUtils.getFileName(fileKey);
                return onlyOfficeService.generateHtml(fileDTO.getKey(), fileSimpleDTO.getFileTokenUrl(), fileName, permission);
            } catch (Exception e) {
                logger.error("generate html failed ,e {}", e.getMessage());
            }
        }
        return StringUtils.EMPTY;
    }

    /**
     * 根据key转换文件  https://api.onlyoffice.com/editors/conversionapi
     *
     * @param outType  输出的文件类型
     * @param tenantId 租户
     * @param fileKey  文件key
     * @param suffix   原文件格式
     * @return 转换后的文件
     */
    public byte[] converterByKey(String outType, Long tenantId, String fileKey, String suffix) {
        String tokenUrl = fileClient.getSignedUrl(tenantId, fileKey).getFileTokenUrl();
        try {
            return getData(suffix, outType, FileUtils.getFileName(fileKey), tokenUrl);
        } catch (CommonException e) {
            throw e;
        } catch (Exception e) {
            throw new CommonException(e);
        }
    }

    /**
     * 根据url转换文件
     *
     * @param outType     输出的文件类型
     * @param tenantId    租户
     * @param bucketName  桶
     * @param storageCode 存储编码
     * @param url         文件url
     * @param suffix      原文件格式
     * @return 转换后的文件
     */
    public byte[] converterByUrl(String outType, Long tenantId, String bucketName, String storageCode, String url, String suffix) {
        String tokenUrl = fileClient.getSignedUrl(tenantId, bucketName, storageCode, url);
        try {
            return getData(suffix, outType, FileUtils.getFileName(url), tokenUrl);
        } catch (CommonException e) {
            throw e;
        } catch (Exception e) {
            throw new CommonException(e);
        }
    }

    /**
     * 根据url转换文件
     *
     * @param outType     输出的文件类型
     * @param tenantId    租户
     * @param bucketName  桶
     * @param storageCode 存储编码
     * @param url         文件url
     * @param suffix      原文件格式
     * @return 转换后的文件
     */
    public InputStream converterStreamByUrl(String outType, Long tenantId, String bucketName, String storageCode, String url, String suffix) {
        String tokenUrl = fileClient.getSignedUrl(tenantId, bucketName, storageCode, url);
        try {
            return getInputStream(suffix, outType, FileUtils.getFileName(url), tokenUrl);
        } catch (CommonException e) {
            throw e;
        } catch (Exception e) {
            throw new CommonException(e);
        }
    }

    private byte[] getData(String suffix, String outType, String fileName, String tokenUrl) {
        try {
            ConverterDTO converter = getConverter(suffix, outType, fileName, tokenUrl);
            return HttpUtils.downloadData(converter.getFileUrl());
        } catch (Exception e) {
            throw new CommonException(e);
        }
    }

    private InputStream getInputStream(String suffix, String outType, String fileName, String tokenUrl) {
        try {
            ConverterDTO converter = getConverter(suffix, outType, fileName, tokenUrl);
            return HttpUtils.download(converter.getFileUrl());
        } catch (Exception e) {
            throw new CommonException(e);
        }
    }

    private ConverterDTO getConverter(String suffix, String outType, String fileName, String tokenUrl) throws IOException {
        RequestParamDTO requestParam = new RequestParamDTO()
                .setAccessToken(onlyOfficeConfig.getToken())
                .setDocServerUrl(onlyOfficeConfig.getConverterUrl());
        ConverterParamDTO converterParam = new ConverterParamDTO()
                .setAsync(false)
                .setFiletype(suffix)
                .setKey(System.currentTimeMillis() + UUIDUtils.generateUUID().substring(0, 7))
                .setOutputtype(outType)
                .setTitle(fileName)
                .setUrl(tokenUrl);
        requestParam.setBodyJson(objectMapper.writeValueAsString(converterParam));
        String result = HttpUtils.sendRequestToDocumentServer(requestParam).toString();
        ConverterDTO converter = objectMapper.readValue(result, ConverterDTO.class);
        if (converter.getEndConvert() == null || !converter.getEndConvert()) {
            throw new CommonException(BootFileConstant.ErrorCode.CONVERSION);
        }
        return converter;
    }
}