package org.hzero.file.domain.service.impl;

import java.io.*;
import java.net.URLEncoder;
import java.util.Objects;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.hzero.boot.file.util.AesUtils;
import org.hzero.core.util.FilenameUtils;
import org.hzero.file.infra.constant.HfleConstant;
import org.hzero.starter.file.constant.FileConstant;
import org.hzero.starter.file.constant.FileMessageConstant;
import org.hzero.starter.file.entity.FileInfo;
import org.hzero.starter.file.entity.StoreConfig;
import org.hzero.starter.file.service.AbstractFileService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.Assert;

import io.choerodon.core.exception.CommonException;

/**
 * 本地文件存储服务
 *
 * @author shuangfei.zhu@hand-china.com 2018/12/17 14:10
 */
public class LocalFileServiceImpl extends AbstractFileService {

    private static final Logger LOGGER = LoggerFactory.getLogger(LocalFileServiceImpl.class);

    private String path;

    @Override
    public AbstractFileService init(StoreConfig config) {
        this.config = config;
        this.path = config.getEndPoint().endsWith(FileConstant.DIRECTORY_SEPARATOR) ? config.getEndPoint() : config.getEndPoint() + FileConstant.DIRECTORY_SEPARATOR;
        return this;
    }

    @Override
    public String upload(FileInfo file, String filePath) {
        String fileKey = file.getFileKey();
        buildDir(file);
        // 复制文件到存储目录
        String savePath = path + file.getBucketName() + FileConstant.DIRECTORY_SEPARATOR + fileKey;
        try {
            // 复制文件
            FileUtils.copyFile(new File(filePath), new File(savePath));
            return getObjectPrefixUrl(file.getBucketName()) + fileKey;
        } catch (IOException e) {
            LOGGER.info(e.getMessage());
            // 删除文件
            try {
                deleteFile(file.getBucketName(), null, fileKey);
            } catch (Exception ex) {
                LOGGER.error(ex.getMessage());
            }
            throw new CommonException(FileMessageConstant.ERROR_FILE_UPDATE, e);
        }
    }

    @Override
    public String upload(FileInfo file, InputStream inputStream) {
        String fileKey = file.getFileKey();
        buildDir(file);
        // 输出的文件流保存到本地文件
        String filePath = path + file.getBucketName() + FileConstant.DIRECTORY_SEPARATOR + fileKey;
        try (FileOutputStream fos = new FileOutputStream(filePath)) {
            // 保存到临时文件, 数据缓冲
            byte[] bs = new byte[1024];
            // 读取到的数据长度
            int len;
            // 开始读取
            while ((len = inputStream.read(bs)) != -1) {
                fos.write(bs, 0, len);
            }
            return getObjectPrefixUrl(file.getBucketName()) + fileKey;
        } catch (Exception e) {
            LOGGER.info(e.getMessage());
            // 删除文件
            try {
                deleteFile(file.getBucketName(), null, fileKey);
            } catch (Exception ex) {
                LOGGER.error(ex.getMessage());
            }
            throw new CommonException(FileMessageConstant.ERROR_FILE_UPDATE, e);
        } finally {
            try {
                inputStream.close();
            } catch (Exception e) {
                LOGGER.info(e.getMessage());
            }
        }
    }

    /**
     * 创建文件夹
     */
    private void buildDir(FileInfo file) {
        java.io.File fileIo;
        // 判断桶对应的文件夹是否存在(租户文件夹一并创建)
        if (Objects.equals(config.getPrefixStrategy(), HfleConstant.PrefixStrategy.FOLDER)) {
            fileIo = new java.io.File(path + file.getBucketName() + FileConstant.DIRECTORY_SEPARATOR +
                    file.getDirectory() + file.getTenantId() + FileConstant.DIRECTORY_SEPARATOR + file.getFileKey().split(FileConstant.DIRECTORY_SEPARATOR)[1]);
        } else {
            fileIo = new java.io.File(path + file.getBucketName() + FileConstant.DIRECTORY_SEPARATOR +
                    file.getDirectory() + file.getTenantId());
        }
        if (!fileIo.exists()) {
            Assert.isTrue(fileIo.mkdirs(), "Create file directory error.");
        }
    }

    @Override
    public String copyFile(FileInfo file, String oldFileKey, String oldBucketName) {
        String oldFilePath = path + oldBucketName + HfleConstant.DIRECTORY_SEPARATOR + oldFileKey;
        try (InputStream inputStream = new FileInputStream(oldFilePath)) {
            return upload(file, inputStream);
        } catch (Exception e) {
            throw new CommonException(FileMessageConstant.ERROR_FILE_UPDATE, e);
        }
    }

    @Override
    public void deleteFile(String bucketName, String url, String fileKey) {
        try {
            if (StringUtils.isBlank(fileKey)) {
                fileKey = getFileKey(bucketName, url);
            }
            String filePath = path + bucketName + HfleConstant.DIRECTORY_SEPARATOR + fileKey;
            java.io.File file = new java.io.File(filePath);
            if (file.isFile() && file.exists()) {
                boolean flag = file.delete();
                Assert.isTrue(flag, "Delete File Error.");
            }
        } catch (Exception e) {
            throw new CommonException(FileMessageConstant.ERROR_DELETE_FILE, e);
        }
    }

    @Override
    public String getSignedUrl(HttpServletRequest servletRequest, String bucketName, String url, String fileKey, String fileName, boolean download, Long expires) {
        // 对文件名需要做一次encode处理
        if (StringUtils.isBlank(fileKey)) {
            int index = url.lastIndexOf(HfleConstant.DIRECTORY_SEPARATOR) + 1;
            try {
                return url.substring(0, index) + URLEncoder.encode(url.substring(index), HfleConstant.DEFAULT_CHARACTER_SET);
            } catch (UnsupportedEncodingException e) {
                return url;
            }
        } else {
            try {
                int index = fileKey.lastIndexOf(HfleConstant.DIRECTORY_SEPARATOR) + 1;
                return getObjectPrefixUrl(bucketName) + fileKey.substring(0, index) + URLEncoder.encode(fileKey.substring(index), HfleConstant.DEFAULT_CHARACTER_SET);
            } catch (UnsupportedEncodingException e) {
                return getObjectPrefixUrl(bucketName) + fileKey;
            }
        }
    }

    @Override
    public void download(HttpServletRequest request, HttpServletResponse response, String bucketName, String url, String fileKey) {
        downloadByUrl(request, response, bucketName, url, fileKey, null);
    }

    public void downloadByUrl(HttpServletRequest request, HttpServletResponse response, String bucketName, String url, String fileKey, String fileName) {
        if (StringUtils.isBlank(fileName)) {
            fileName = FilenameUtils.getFileName(StringUtils.isBlank(url) ? fileKey : url);
        }
        if (StringUtils.isBlank(fileKey)) {
            fileKey = getFileKey(bucketName, url);
        }
        String filePath = path + bucketName + HfleConstant.DIRECTORY_SEPARATOR + fileKey;
        try (InputStream is = new FileInputStream(new java.io.File(filePath))) {
            byte[] data = IOUtils.toByteArray(is);
            buildResponse(response, data, FilenameUtils.encodeFileName(request, fileName));
        } catch (Exception e) {
            throw new CommonException(FileMessageConstant.ERROR_DOWNLOAD_FILE, e);
        }
    }

    @Override
    public void decryptDownload(HttpServletRequest request, HttpServletResponse response, String bucketName, String url, String fileKey, String password) {
        if (StringUtils.isBlank(fileKey)) {
            fileKey = getFileKey(bucketName, url);
        }
        String filePath = path + bucketName + HfleConstant.DIRECTORY_SEPARATOR + fileKey;
        try (InputStream is = new FileInputStream(new java.io.File(filePath))) {
            byte[] data = IOUtils.toByteArray(is);
            if (StringUtils.isBlank(password)) {
                data = AesUtils.decrypt(data);
            } else {
                data = AesUtils.decrypt(data, password);
            }
            buildResponse(response, data, FilenameUtils.encodeFileName(request, FilenameUtils.getFileName(StringUtils.isBlank(url) ? fileKey : url)));
        } catch (Exception e) {
            throw new CommonException(FileMessageConstant.ERROR_DOWNLOAD_FILE, e);
        }
    }

    @Override
    public String getObjectPrefixUrl(String bucketName) {
        return String.format("%s/%s/", config.getDomain(), bucketName);
    }
}
