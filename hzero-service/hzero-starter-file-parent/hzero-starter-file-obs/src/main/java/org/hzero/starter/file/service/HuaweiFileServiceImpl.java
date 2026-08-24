package org.hzero.starter.file.service;

import java.io.IOException;
import java.io.InputStream;
import java.util.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.obs.services.ObsClient;
import com.obs.services.model.*;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.hzero.core.base.BaseConstants;
import org.hzero.core.util.FilenameUtils;
import org.hzero.starter.file.constant.FileConstant;
import org.hzero.starter.file.constant.FileMessageConstant;
import org.hzero.starter.file.entity.FileInfo;
import org.hzero.starter.file.entity.StoreConfig;
import org.hzero.starter.file.util.AesUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.choerodon.core.exception.CommonException;

/**
 * 华为云OSS存储
 *
 * @author xianzhi.chen@hand-china.com 2018年6月25日下午5:06:29
 */
public class HuaweiFileServiceImpl extends AbstractFileService {

    private static final Logger LOGGER = LoggerFactory.getLogger(HuaweiFileServiceImpl.class);

    private ObsClient client;
    private AccessControlList accessCl;
    private String location;

    @Override
    public AbstractFileService init(StoreConfig config) {
        this.config = config;
        try {
            location = config.getRegion();
            switch (config.getAccessControl()) {
                case FileConstant.HuaweiAccessControl.PRIVATE:
                    this.accessCl = AccessControlList.REST_CANNED_PRIVATE;
                    break;
                case FileConstant.HuaweiAccessControl.PUBLIC_READ:
                    this.accessCl = AccessControlList.REST_CANNED_PUBLIC_READ_DELIVERED;
                    break;
                case FileConstant.HuaweiAccessControl.PUBLIC_READ_WRITE:
                    this.accessCl = AccessControlList.REST_CANNED_PUBLIC_READ_WRITE_DELIVERED;
                    break;
                default:
                    break;
            }
        } catch (Exception e) {
            LOGGER.info(e.getMessage());
        }
        return this;
    }

    private ObsClient getClient() {
        if (client == null) {
            client = new ObsClient(config.getAccessKeyId(), config.getAccessKeySecret(), config.getEndPoint());
        }
        return client;
    }

    @Override
    public void shutdown() {
        if (client != null) {
            try {
                client.close();
            } catch (IOException e) {
                LOGGER.warn(e.getMessage());
            }
        }
    }

    @Override
    public String upload(FileInfo file, String filePath) {
        //获取实际桶名
        String realBucketName = getRealBucketName(file.getBucketName());
        //根据文件url获取文件实际的ObjectKey
        String fileKey = file.getFileKey();
        UploadFileRequest request = new UploadFileRequest(realBucketName, fileKey);
        //设置待上传的本地文件，filePath本地文件的路径
        request.setUploadFile(filePath);
        //设置分段大小
        request.setPartSize(fileConfig.getDefaultSharedSize());
        //开启断点续传模式
        request.setEnableCheckpoint(true);
        try {
            //进行断点续传上传
            getClient().uploadFile(request);
            return getObjectPrefixUrl(realBucketName) + fileKey;
        } catch (Exception e) {
            // 删除文件
            try {
                getClient().deleteObject(realBucketName, fileKey);
            } catch (Exception ex) {
                LOGGER.error(ex.getMessage());
            }
            throw new CommonException(FileMessageConstant.ERROR_FILE_UPDATE, e);
        }
    }

    @Override
    public String upload(FileInfo file, InputStream inputStream) {
        String realBucketName = getRealBucketName(file.getBucketName());
        String fileKey = file.getFileKey();
        try {
            checkAndCreateBucket(realBucketName);
            ObjectMetadata metadata = new ObjectMetadata();
            metadata.setContentType(file.getFileType());
            getClient().putObject(realBucketName, fileKey, inputStream, metadata);
            return getObjectPrefixUrl(realBucketName) + fileKey;
        } catch (CommonException ce) {
            throw ce;
        } catch (Exception e) {
            // 删除文件
            try {
                getClient().deleteObject(realBucketName, fileKey);
            } catch (Exception ex) {
                LOGGER.error(ex.getMessage());
            }
            throw new CommonException(FileMessageConstant.ERROR_FILE_UPDATE, e);
        } finally {
            try {
                inputStream.close();
            } catch (IOException e) {
                LOGGER.warn(e.getMessage());
            }
        }
    }

    private void checkAndCreateBucket(String realBucketName) {
        // 判断桶是否存在
        // ObsClient.headBucket接口只能访问到当前区域下的桶，而ObsClient.listBuckets接口能访问到所有区域下的桶。
        ListBucketsRequest request = new ListBucketsRequest();
        request.setQueryLocation(true);
        List<ObsBucket> buckets = getClient().listBuckets(request);
        boolean isExist = false;
        for (ObsBucket bucket : buckets) {
            if (Objects.equals(bucket.getBucketName(), realBucketName)) {
                isExist = true;
            }
        }
        if (!isExist) {
            if (Objects.equals(config.getCreateBucketFlag(), BaseConstants.Flag.NO)) {
                throw new CommonException(FileMessageConstant.BUCKET_NOT_EXISTS);
            }
            ObsBucket obsBucket = new ObsBucket();
            obsBucket.setBucketName(realBucketName);
            obsBucket.setAcl(accessCl);
            // 设置桶的存储类型为标准存储
            obsBucket.setBucketStorageClass(StorageClassEnum.STANDARD);
            // 设置桶区域位置
            obsBucket.setLocation(location);
            getClient().createBucket(obsBucket);
            // 设置允许跨域
            List<String> list = Collections.singletonList("*");
            BucketCorsRule bucketCorsRule = new BucketCorsRule();
            // 只有 origin header允许使用通配符
            bucketCorsRule.setAllowedOrigin(CollectionUtils.isEmpty(fileConfig.getOrigins()) ? list : fileConfig.getOrigins());
            bucketCorsRule.setAllowedHeader(list);
            ArrayList<String> allowedMethod = new ArrayList<>();
            allowedMethod.add("GET");
            allowedMethod.add("PUT");
            allowedMethod.add("DELETE");
            allowedMethod.add("POST");
            allowedMethod.add("HEAD");
            bucketCorsRule.setAllowedMethod(allowedMethod);
            List<BucketCorsRule> bucketCorsRules = new ArrayList<>();
            bucketCorsRules.add(bucketCorsRule);
            BucketCors bucketCors = new BucketCors();
            bucketCors.setRules(bucketCorsRules);
            getClient().setBucketCors(realBucketName, bucketCors);
        }
    }

    @Override
    public String copyFile(FileInfo file, String oldFileKey, String oldBucketName) {
        String realBucketName = getRealBucketName(file.getBucketName());
        try {
            checkAndCreateBucket(realBucketName);
            getClient().copyObject(getRealBucketName(oldBucketName), oldFileKey, realBucketName, file.getFileKey());
            return getObjectPrefixUrl(realBucketName) + file.getFileKey();
        } catch (Exception e) {
            throw new CommonException(FileMessageConstant.ERROR_FILE_UPDATE, e);
        }
    }

    @Override
    public void deleteFile(String bucketName, String url, String fileKey) {
        String realBucketName = getRealBucketName(bucketName);
        if (StringUtils.isBlank(fileKey)) {
            fileKey = getFileKey(realBucketName, url);
        }
        // 删除附件文档
        try {
            getClient().deleteObject(realBucketName, fileKey);
        } catch (Exception e) {
            throw new CommonException(FileMessageConstant.ERROR_DELETE_FILE, e);
        }
    }

    @Override
    public String getSignedUrl(HttpServletRequest servletRequest, String bucketName, String url, String fileKey, String fileName, boolean download, Long expires) {
        String realBucketName = getRealBucketName(bucketName);
        if (StringUtils.isBlank(fileKey)) {
            fileKey = getFileKey(realBucketName, url);
        }
        try {
            // 路径有效期
            Long expiresTime = expires == null ? fileConfig.getDefaultExpires() : expires;
            TemporarySignatureRequest request = new TemporarySignatureRequest(HttpMethodEnum.GET, expiresTime);
            request.setBucketName(realBucketName);
            request.setObjectKey(fileKey);
            if (download) {
                Map<String, Object> header = new TreeMap<>();
                header.put("response-content-type", FileConstant.DEFAULT_MULTI_TYPE);
                header.put("response-content-disposition", "attachment;filename=" + FilenameUtils.encodeFileName(servletRequest, fileName));
                header.put("response-cache-control", "must-revalidate, post-check=0, pre-check=0");
                header.put("response-expires", String.valueOf(System.currentTimeMillis() + 1000));
                request.setQueryParams(header);
            }
            TemporarySignatureResponse res = getClient().createTemporarySignature(request);
            return res.getSignedUrl();
        } catch (Exception e) {
            LOGGER.error(e.getMessage());
            return getObjectPrefixUrl(realBucketName) + fileKey;
        }
    }

    @Override
    public void download(HttpServletRequest request, HttpServletResponse response, String bucketName, String url, String fileKey) {
        String realBucketName = getRealBucketName(bucketName);
        if (StringUtils.isBlank(fileKey)) {
            fileKey = getFileKey(realBucketName, url);
        }
        try {
            InputStream is = getClient().getObject(realBucketName, fileKey).getObjectContent();
            byte[] data = IOUtils.toByteArray(is);
            buildResponse(response, data, FilenameUtils.encodeFileName(request, FilenameUtils.getFileName(StringUtils.isBlank(url) ? fileKey : url)));
        } catch (Exception e) {
            throw new CommonException(FileMessageConstant.ERROR_DOWNLOAD_FILE, e);
        }
    }

    @Override
    public void decryptDownload(HttpServletRequest request, HttpServletResponse response, String bucketName, String url, String fileKey, String password) {
        String realBucketName = getRealBucketName(bucketName);
        if (StringUtils.isBlank(fileKey)) {
            fileKey = getFileKey(realBucketName, url);
        }
        try {
            InputStream is = getClient().getObject(realBucketName, fileKey).getObjectContent();
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

    /**
     * 获取对象URL
     */
    @Override
    public String getObjectPrefixUrl(String realBucketName) {
        if (StringUtils.isNotBlank(config.getDomain())) {
            return getProxy(realBucketName);
        } else {
            return String.format("https://%s.%s:443/", realBucketName, config.getEndPoint());
        }
    }

    private String getProxy(String realBucketName) {
        String proxy = config.getDomain();
        proxy = proxy.replace(FileConstant.DOMAIN_BUCKET_NAME, realBucketName);
        if (!proxy.startsWith("http")) {
            // 未指定协议，默认使用https
            proxy = "https://" + proxy;
        }
        if (!proxy.endsWith("/")) {
            proxy = proxy + "/";
        }
        return proxy;
    }
}
