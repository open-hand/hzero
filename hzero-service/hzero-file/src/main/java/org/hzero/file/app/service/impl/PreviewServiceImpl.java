package org.hzero.file.app.service.impl;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.hzero.boot.file.OnlyOfficeClient;
import org.hzero.boot.file.util.HttpUtils;
import org.hzero.core.base.BaseConstants;
import org.hzero.core.util.FileUtils;
import org.hzero.core.util.FilenameUtils;
import org.hzero.file.api.dto.FileSimpleDTO;
import org.hzero.file.app.service.FileService;
import org.hzero.file.app.service.PreviewService;
import org.hzero.file.domain.entity.File;
import org.hzero.file.domain.repository.FileRepository;
import org.hzero.file.domain.service.factory.StoreFactory;
import org.hzero.file.domain.service.factory.StoreService;
import org.hzero.file.infra.config.FileConfig;
import org.hzero.file.infra.constant.HfleConstant;
import org.hzero.file.infra.constant.HfleMessageConstant;
import org.hzero.file.infra.util.TextUtils;
import org.hzero.mybatis.common.Criteria;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import io.choerodon.core.exception.CommonException;

/**
 * 文件预览应用服务实现类
 *
 * @author xianzhi.chen@hand-china.com 2019年2月15日上午11:04:46
 */
@Service
public class PreviewServiceImpl implements PreviewService {

    private static final Logger logger = LoggerFactory.getLogger(PreviewServiceImpl.class);

    private final StoreFactory factory;
    private final FileConfig fileConfig;
    private final FileRepository fileRepository;
    private final FileService fileService;
    private final OnlyOfficeClient onlyOfficeClient;

    private static final List<String> IMAGE_TYPE = Arrays.asList("jpeg", "jpg", "png");
    private static final List<String> AS_TYPE = Arrays.asList("doc", "docx", "pdf", "txt");
    private static final List<String> ONLY_TYPE = Arrays.asList("doc", "docm", "docx", "dot", "dotm",
            "dotx", "epub", "fodt", "html", "mht", "odt", "ott", "pdf", "rtf", "txt", "xps", "csv", "fods",
            "ods", "ots", "xls", "xlsm", "xlsx", "xlt", "xltm", "xltx", "fodp", "odp", "otp", "pot", "potm",
            "potx", "pps", "ppsm", "ppsx", "ppt", "pptm", "pptx");


    private static final String PDF = "pdf";

    @Autowired
    public PreviewServiceImpl(StoreFactory factory,
                              FileConfig fileConfig,
                              FileRepository fileRepository,
                              FileService fileService,
                              OnlyOfficeClient onlyOfficeClient) {
        this.factory = factory;
        this.fileConfig = fileConfig;
        this.fileRepository = fileRepository;
        this.fileService = fileService;
        this.onlyOfficeClient = onlyOfficeClient;
    }

    @Override
    public void previewFileByKey(HttpServletRequest request, HttpServletResponse response, Long tenantId, String fileKey) {
        String previewFileType = fileConfig.getPreviewType();
        try {
            switch (previewFileType) {
                case HfleConstant.PreviewType.AS:
                    asPreviewByKey(request, response, tenantId, fileKey);
                    break;
                case HfleConstant.PreviewType.KK:
                    kkPreviewByKey(request, response, tenantId, fileKey);
                    break;
                case HfleConstant.PreviewType.ON:
                    onlyPreviewByKey(request, response, tenantId, fileKey);
                    break;
                default:
                    break;
            }
        } catch (Exception e) {
            throw new CommonException(BaseConstants.ErrorCode.ERROR, e);
        }
    }

    @Override
    public void previewFileByUrl(HttpServletRequest request, HttpServletResponse response, Long tenantId, String storageCode, String bucketName, String url) {
        String previewFileType = fileConfig.getPreviewType();
        try {
            switch (previewFileType) {
                case HfleConstant.PreviewType.AS:
                    asPreviewByUrl(request, response, tenantId, storageCode, bucketName, url);
                    break;
                case HfleConstant.PreviewType.KK:
                    kkPreviewByUrl(request, response, tenantId, storageCode, bucketName, url);
                    break;
                case HfleConstant.PreviewType.ON:
                    onlyPreviewByUrl(request, response, tenantId, storageCode, bucketName, url);
                    break;
                default:
                    break;
            }
        } catch (Exception e) {
            throw new CommonException(BaseConstants.ErrorCode.ERROR, e);
        }
    }

    private void asPreviewByKey(HttpServletRequest request, HttpServletResponse response, Long tenantId, String fileKey) throws IOException {
        String[] str = fileKey.split("\\.");
        String suffix = str[str.length - BaseConstants.Digital.ONE].toLowerCase();
        if (IMAGE_TYPE.contains(suffix)) {
            previewImageByKey(request, response, tenantId, fileKey);
        } else if (AS_TYPE.contains(suffix)) {
            InputStream inputStream = downloadFileByKey(request, tenantId, fileKey);
            buildPdf(request, response, inputStream, FileUtils.getFileName(fileKey), suffix);
        } else {
            throw new CommonException(HfleMessageConstant.ERROR_FILE_FORMAT_NOT_SITE);
        }
    }

    private void asPreviewByUrl(HttpServletRequest request, HttpServletResponse response, Long tenantId, String storageCode, String bucketName, String url) throws IOException {
        String[] str = url.split("\\.");
        String suffix = str[str.length - BaseConstants.Digital.ONE].toLowerCase();
        if (IMAGE_TYPE.contains(suffix)) {
            previewImageByUrl(request, response, tenantId, storageCode, bucketName, url);
        } else if (AS_TYPE.contains(suffix)) {
            InputStream inputStream = downloadFileByUrl(request, tenantId, storageCode, bucketName, url);
            buildPdf(request, response, inputStream, FileUtils.getFileName(url), suffix);
        } else {
            throw new CommonException(HfleMessageConstant.ERROR_FILE_FORMAT_NOT_SITE);
        }
    }

    private void previewImageByKey(HttpServletRequest request, HttpServletResponse response, Long tenantId, String fileKey) throws IOException {
        try (InputStream inputStream = downloadFileByKey(request, tenantId, fileKey)) {
            byte[] data = IOUtils.toByteArray(inputStream);
            response.reset();
            response.setHeader("Content-Disposition", "attachment;filename=" + FilenameUtils.encodeFileName(request, FileUtils.getFileName(fileKey)));
            response.setDateHeader("Expires", (System.currentTimeMillis() + 1000));
            response.setContentType("multipart/form-data");
            response.addHeader("Content-Length", "" + data.length);
            response.setHeader("Pragma", "public");
            response.setHeader("Cache-Control", "must-revalidate, post-check=0, pre-check=0");
            IOUtils.write(data, response.getOutputStream());
        }
    }

    private void previewImageByUrl(HttpServletRequest request, HttpServletResponse response, Long tenantId, String storageCode, String bucketName, String url) throws IOException {
        response.sendRedirect(fileService.getSignedUrl(request, tenantId, bucketName, storageCode, url));
    }

    /**
     * 生成预览pdf
     */
    private void buildPdf(HttpServletRequest request, HttpServletResponse response, InputStream inputStream, String fileName, String suffix) {
        try (OutputStream outputStream = response.getOutputStream()) {
            Assert.notNull(inputStream, HfleMessageConstant.ERROR_FILE_NOT_EXISTS);
            response.setContentType(MediaType.APPLICATION_PDF_VALUE);
            response.setHeader("Content-Disposition", "inline;filename=" + FilenameUtils.encodeFileName(request, fileName));
            if (Objects.equals(PDF, suffix)) {
                byte[] buffer = new byte[1024];
                int len;
                while ((len = inputStream.read(buffer)) != -1) {
                    outputStream.write(buffer, 0, len);
                }
            } else {
                TextUtils.wordToPdf(inputStream, outputStream, null);
            }
        } catch (IOException e) {
            throw new CommonException(BaseConstants.ErrorCode.ERROR, e);
        } finally {
            try {
                if (inputStream != null) {
                    inputStream.close();
                }
            } catch (IOException e) {
                logger.error(e.getMessage());
            }
        }
    }

    /**
     * 下载文件
     *
     * @param fileKey 文件key
     * @return 文件信息
     */
    @Override
    public InputStream downloadFileByKey(HttpServletRequest request, Long tenantId, String fileKey) {
        try {
            // 获取下载文件的URL
            File condition = new File().setTenantId(tenantId).setFileKey(fileKey);
            File file = this.fileRepository.selectOneOptional(condition,
                    new Criteria().select(
                            File.FIELD_FILE_NAME,
                            File.FIELD_BUCKET_NAME,
                            File.FIELD_DIRECTORY,
                            File.FIELD_FILE_URL,
                            File.FIELD_STORAGE_CODE));
            Assert.notNull(file, BaseConstants.ErrorCode.DATA_NOT_EXISTS);
            StoreService storeService = factory.build(tenantId, file.getStorageCode());
            Assert.notNull(storeService, HfleMessageConstant.ERROR_FILE_STORE_CONFIG);
            String tokenUrl = storeService.getAbstractFileService().getSignedUrl(request, file.getBucketName(), file.getFileUrl(), fileKey, file.getFileName(), true, null);
            return HttpUtils.download(tokenUrl);
        } catch (Exception e) {
            throw new CommonException(HfleMessageConstant.ERROR_FILE_FORMAT_NOT_SITE, e);
        }
    }

    /**
     * 下载文件
     *
     * @param request     request
     * @param tenantId    租户
     * @param storageCode 存储编码
     * @param bucketName  桶名
     * @param url         文件url
     * @return 文件信息
     */
    @Override
    public InputStream downloadFileByUrl(HttpServletRequest request, Long tenantId, String storageCode, String bucketName, String url) {
        try {
            StoreService storeService = factory.build(tenantId, storageCode);
            Assert.notNull(storeService, HfleMessageConstant.ERROR_FILE_STORE_CONFIG);
            String fileName = FilenameUtils.getFileName(url);
            String tokenUrl = storeService.getAbstractFileService().getSignedUrl(request, bucketName, url, null, fileName, true, null);
            return HttpUtils.download(tokenUrl);
        } catch (Exception e) {
            throw new CommonException(HfleMessageConstant.ERROR_FILE_FORMAT_NOT_SITE, e);
        }
    }

    /**
     * kkFileView
     */
    private void kkPreviewByKey(HttpServletRequest request, HttpServletResponse response, Long tenantId, String fileKey) {
        Assert.isTrue(StringUtils.isNotBlank(fileConfig.getKkFileViewUrl()), BaseConstants.ErrorCode.ERROR);
        FileSimpleDTO file = fileService.getSignedUrlByKey(request, tenantId, fileKey);
        Assert.notNull(file, BaseConstants.ErrorCode.DATA_NOT_EXISTS);
        Assert.isTrue(StringUtils.isNotBlank(file.getFileTokenUrl()), BaseConstants.ErrorCode.DATA_NOT_EXISTS);
        try {
            response.sendRedirect(fileConfig.getKkFileViewUrl() + "?url=" + file.getFileTokenUrl());
        } catch (Exception e) {
            throw new CommonException(HfleMessageConstant.PREVIEW, e);
        }
    }

    private void kkPreviewByUrl(HttpServletRequest request, HttpServletResponse response, Long tenantId, String storageCode, String bucketName, String url) {
        Assert.isTrue(StringUtils.isNotBlank(fileConfig.getKkFileViewUrl()), BaseConstants.ErrorCode.ERROR);
        String tokenUrl = fileService.getSignedUrl(request, tenantId, bucketName, storageCode, url);
        Assert.isTrue(StringUtils.isNotBlank(tokenUrl), BaseConstants.ErrorCode.DATA_NOT_EXISTS);
        try {
            response.sendRedirect(fileConfig.getKkFileViewUrl() + "?url=" + tokenUrl);
        } catch (Exception e) {
            throw new CommonException(HfleMessageConstant.PREVIEW, e);
        }
    }

    /**
     * onlyOffice文件预览
     */
    private void onlyPreviewByKey(HttpServletRequest request, HttpServletResponse response, Long tenantId, String fileKey) throws IOException {
        Assert.notNull(onlyOfficeClient, HfleMessageConstant.ONLY_OFFICE_CONFIG);
        String[] str = fileKey.split("\\.");
        String suffix = str[str.length - BaseConstants.Digital.ONE].toLowerCase();
        if (IMAGE_TYPE.contains(suffix)) {
            previewImageByKey(request, response, tenantId, fileKey);
        } else if (ONLY_TYPE.contains(suffix)) {
            byte[] data = onlyOfficeClient.converterByKey(PDF, tenantId, fileKey, suffix);
            buildResponse(request, response, data, FileUtils.getFileName(fileKey));
        } else {
            throw new CommonException(HfleMessageConstant.ERROR_FILE_FORMAT_NOT_SITE);
        }
    }

    private void onlyPreviewByUrl(HttpServletRequest request, HttpServletResponse response, Long tenantId, String storageCode, String bucketName, String url) throws IOException {
        Assert.notNull(onlyOfficeClient, HfleMessageConstant.ONLY_OFFICE_CONFIG);
        String[] str = url.split("\\.");
        Assert.isTrue(str.length > 0, BaseConstants.ErrorCode.DATA_INVALID);
        String suffix = str[str.length - BaseConstants.Digital.ONE].toLowerCase();
        if (IMAGE_TYPE.contains(suffix)) {
            previewImageByUrl(request, response, tenantId, storageCode, bucketName, url);
        } else if (ONLY_TYPE.contains(suffix)) {
            byte[] data = onlyOfficeClient.converterByUrl(PDF, tenantId, bucketName, storageCode, url, suffix);
            buildResponse(request, response, data, FileUtils.getFileName(url));
        } else {
            throw new CommonException(HfleMessageConstant.ERROR_FILE_FORMAT_NOT_SITE);
        }
    }

    private void buildResponse(HttpServletRequest request, HttpServletResponse response, byte[] data, String fileName) throws IOException {
        response.reset();
        response.setHeader("Content-Disposition", "inline;filename=" + FilenameUtils.encodeFileName(request, fileName));
        response.setContentType("application/pdf");
        response.addHeader("Content-Length", "" + data.length);
        response.setHeader("Cache-Control", "must-revalidate, post-check=0, pre-check=0");
        response.setHeader("Pragma", "public");
        response.setDateHeader("Expires", (System.currentTimeMillis() + 1000));
        IOUtils.write(data, response.getOutputStream());
    }
}
