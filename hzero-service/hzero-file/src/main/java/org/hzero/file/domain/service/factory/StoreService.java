package org.hzero.file.domain.service.factory;

import java.io.InputStream;
import java.util.*;

import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.hzero.core.base.BaseConstants;
import org.hzero.core.util.Pair;
import org.hzero.core.util.UUIDUtils;
import org.hzero.file.api.dto.FileDTO;
import org.hzero.file.api.dto.FileParamsDTO;
import org.hzero.file.app.service.CapacityUsedService;
import org.hzero.file.domain.entity.File;
import org.hzero.file.domain.repository.FileRepository;
import org.hzero.file.infra.constant.HfleConstant;
import org.hzero.file.infra.constant.HfleMessageConstant;
import org.hzero.starter.file.entity.FileInfo;
import org.hzero.starter.file.entity.StoreConfig;
import org.hzero.starter.file.service.AbstractFileService;
import org.springframework.beans.BeanUtils;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;

/**
 * 文件服务抽象类 description
 *
 * @author xianzhi.chen@hand-china.com 2018年6月22日下午4:54:40
 */
public class StoreService {

    /**
     * 文件存储配置
     */
    private final StoreConfig config;

    private final AbstractFileService abstractFileService;

    private final FileRepository fileRepository;

    /**
     * 租户已使用容量redis工具
     */
    private final CapacityUsedService residualCapacityService;

    public StoreConfig getConfig() {
        return config;
    }

    public StoreService(AbstractFileService abstractFileService, StoreConfig config, FileRepository fileRepository, CapacityUsedService residualCapacityService) {
        this.config = config;
        this.abstractFileService = abstractFileService;
        this.fileRepository = fileRepository;
        this.residualCapacityService = residualCapacityService;
    }

    public AbstractFileService getAbstractFileService() {
        return abstractFileService;
    }

    private FileInfo change(File file) {
        FileInfo fileInfo = new FileInfo();
        BeanUtils.copyProperties(file, fileInfo);
        return fileInfo;
    }

    /**
     * 附件复制
     */
    @Transactional(rollbackFor = Exception.class)
    public Map<String, String> copyAttachment(FileParamsDTO fileParamsDTO) {
        Long tenantId = fileParamsDTO.getTenantId();
        Map<String, String> uuidMap = new HashMap<>(16);
        fileParamsDTO.getUuidList().forEach(uuid -> {
            if (StringUtils.isNotBlank(uuid)) {
                fileParamsDTO.setAttachmentUUID(uuid);
                String newUuid = UUIDUtils.generateTenantUUID(fileParamsDTO.getTenantId());
                List<File> fileList = fileRepository.listFile(fileParamsDTO);
                if (tenantId != null && CollectionUtils.isEmpty(fileList)) {
                    // 允许租户复制平台文件到自己的租户
                    fileList = fileRepository.listFile(fileParamsDTO.setTenantId(BaseConstants.DEFAULT_TENANT_ID));
                    fileList.forEach(file -> copy(file.setTenantId(tenantId), newUuid, file.getBucketName(), file.getFileName()));
                } else {
                    fileList.forEach(file -> copy(file, newUuid, file.getBucketName(), file.getFileName()));
                }
                uuidMap.put(uuid, newUuid);
            }
        });
        return uuidMap;
    }

    /**
     * 单文件复制
     *
     * @param file 原文件信息
     * @return 新文件信息
     */
    @Transactional(rollbackFor = Exception.class)
    public File copy(File file, String newAttachmentUuid, String newBucketName, String newFileName) {
        String oldFileKey = file.getFileKey();
        String oldBucketName = file.getBucketName();
        // 组装新文件数据
        file.setFileId(null);
        file.setAttachmentUuid(newAttachmentUuid);
        file.initFileKey(buildFileName(newFileName));
        file.setBucketName(newBucketName);
        file.setFileName(newFileName);
        String fileUrl = abstractFileService.copyFile(change(file), oldFileKey, oldBucketName);
        file.setFileUrl(fileUrl);
        fileRepository.insertSelective(file);
        // 修改租户已使用存储容量
        residualCapacityService.refreshCache(file.getTenantId(), file.getFileSize());
        return file;
    }

    /**
     * 文件上传
     *
     * @param file        文件信息
     * @param inputStream 输入流
     * @return 文件URL
     */
    @Transactional(rollbackFor = Exception.class)
    public File uploadFile(File file, InputStream inputStream) {
        file.setSourceType(String.valueOf(config.getStorageType()));
        file.initFileKey(buildFileName(file.getFileName()));
        String fileUrl = abstractFileService.upload(change(file), inputStream);
        file.setFileUrl(fileUrl);
        // 校验文件url长度
        file.validateSize(this);
        long size = 0L;
        // 获取文件名前缀策略
        if (Objects.equals(HfleConstant.PrefixStrategy.NONE, config.getPrefixStrategy())) {
            // 查询大小
            Long oldSize = ObjectUtils.defaultIfNull(fileRepository.selectFileSize(file.getTenantId(), file.getBucketName(), file.getAttachmentUuid(), fileUrl), 0L);
            size = size - oldSize;
            // 删除表中数据
            fileRepository.delete(new File().setFileUrl(fileUrl));
        }
        fileRepository.insertSelective(file);
        size = size + file.getFileSize();
        // 修改租户已使用存储容量
        residualCapacityService.refreshCache(file.getTenantId(), size);
        return file;
    }

    /**
     * 本地文件上传
     *
     * @param file     文件信息
     * @param filePath 文件路径
     * @return 文件URL
     */
    @Transactional(rollbackFor = Exception.class)
    public File uploadFile(File file, String filePath) {
        file.setSourceType(String.valueOf(config.getStorageType()));
        file.initFileKey(buildFileName(file.getFileName()));
        String fileUrl = abstractFileService.upload(change(file), filePath);
        file.setFileUrl(fileUrl);
        // 校验文件url长度
        file.validateSize(this);
        long size = 0L;
        // 获取文件名前缀策略
        if (Objects.equals(HfleConstant.PrefixStrategy.NONE, config.getPrefixStrategy())) {
            // 查询大小
            Long oldSize = ObjectUtils.defaultIfNull(fileRepository.selectFileSize(file.getTenantId(), file.getBucketName(), file.getAttachmentUuid(), fileUrl), 0L);
            size = size - oldSize;
            // 删除表中数据
            fileRepository.delete(new File().setFileUrl(fileUrl));
        }
        fileRepository.insertSelective(file);
        size = size + file.getFileSize();
        // 修改租户已使用存储容量
        residualCapacityService.refreshCache(file.getTenantId(), size);
        return file;
    }

    /**
     * 根据url更新
     *
     * @param file        File
     * @param inputStream InputStream
     * @return 文件URL
     */
    @Transactional(rollbackFor = Exception.class)
    public String updateFileByUrl(File file, InputStream inputStream) {
        // 获取文件信息
        File fileInfo = fileRepository.getFileByUrlAndLock(file.getTenantId(), file.getBucketName(), file.getFileUrl());
        Assert.notNull(fileInfo, HfleMessageConstant.ERROR_DATA_NOT_EXISTS);
        Long oldSize = fileInfo.getFileSize();
        fileInfo.setFileSize(file.getFileSize());
        // 更新文件文档
        String fileUrl = abstractFileService.upload(change(fileInfo), inputStream);
        fileInfo.setFileUrl(fileUrl);
        // 更新文件信息
        fileRepository.updateByPrimaryKeySelective(fileInfo);

        // 修改租户已使用存储容量
        residualCapacityService.refreshCache(file.getTenantId(), file.getFileSize() - oldSize);
        return fileUrl;
    }

    /**
     * 根据文件key更新，并更新md5
     *
     * @param newFile     File
     * @param inputStream InputStream
     * @return 文件MD5
     */
    @Transactional(rollbackFor = Exception.class)
    public String updateFileByKey(File oldFile, File newFile, InputStream inputStream) {
        // 获取文件信息
        Long oldSize = oldFile.getFileSize();
        oldFile.setFileSize(newFile.getFileSize());
        oldFile.setMd5(newFile.getMd5());
        // 更新文件文档
        String fileUrl = abstractFileService.upload(change(oldFile), inputStream);
        oldFile.setFileUrl(fileUrl);
        // 更新文件信息
        fileRepository.updateByPrimaryKeySelective(oldFile);

        // 修改租户已使用存储容量
        residualCapacityService.refreshCache(newFile.getTenantId(), newFile.getFileSize() - oldSize);
        return oldFile.getMd5();
    }

    /**
     * 根据key删除文件
     *
     * @param file     文件key
     * @param tenantId 租户Id
     */
    @Transactional(rollbackFor = Exception.class)
    public void deleteFileByKey(File file, Long tenantId) {
        fileRepository.deleteByPrimaryKey(file.getFileId());
        abstractFileService.deleteFile(file.getBucketName(), file.getFileUrl(), file.getFileKey());
        // 扣除缓存
        Long size = -file.getFileSize();
        residualCapacityService.refreshCache(tenantId, size);
    }

    /**
     * 文件删除
     *
     * @param urls           路径
     * @param tenantId       租户Id
     * @param bucketName     桶
     * @param attachmentUuid uuid
     */
    public void deleteFile(List<String> urls, Long tenantId, String bucketName, String attachmentUuid) {
        if (!CollectionUtils.isEmpty(urls)) {
            List<Pair<String, Long>> list = new ArrayList<>();
            List<FileDTO> fileList = fileRepository.selectFileByUrls(tenantId, bucketName, urls, attachmentUuid);
            for (FileDTO item : fileList) {
                if (bucketName == null) {
                    bucketName = item.getBucketName();
                }
                Long fileSize = item.getFileSize();
                list.add(Pair.of(item.getFileUrl(), Optional.ofNullable(fileSize).orElse(0L)));
            }
            fileRepository.deleteFileByUrls(tenantId, bucketName, attachmentUuid, urls);
            // 删除文档
            for (Pair<String, Long> pair : list) {
                String url = pair.getFirst();
                Long size = pair.getSecond();
                abstractFileService.deleteFile(bucketName, url, null);
                // 扣除缓存
                residualCapacityService.refreshCache(tenantId, -size);
            }
        }
    }

    /**
     * 构建文件名
     *
     * @param fileName 文件名
     * @return 带前缀的文件名
     */
    private String buildFileName(String fileName) {
        String prefixStrategy = StringUtils.isBlank(config.getPrefixStrategy()) ? HfleConstant.PrefixStrategy.UUID : config.getPrefixStrategy();
        // 根据指定策略生成带前缀的文件名
        switch (prefixStrategy) {
            case HfleConstant.PrefixStrategy.NONE:
                return fileName;
            case HfleConstant.PrefixStrategy.FOLDER:
                return UUIDUtils.generateUUID() + HfleConstant.DIRECTORY_SEPARATOR + fileName;
            case HfleConstant.PrefixStrategy.UUID:
            default:
                return UUIDUtils.generateUUID() + HfleConstant.DEFAULT_SPLIT_SYMBOL + fileName;
        }
    }
}
