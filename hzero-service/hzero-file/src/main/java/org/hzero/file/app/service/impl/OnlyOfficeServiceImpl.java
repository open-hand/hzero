package org.hzero.file.app.service.impl;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.Date;
import java.util.List;
import java.util.Objects;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.hzero.boot.file.OnlyOfficeClient;
import org.hzero.boot.file.constant.BootFileConstant;
import org.hzero.boot.file.dto.ExtraDTO;
import org.hzero.boot.file.dto.GenerateHtmlByKeyDTO;
import org.hzero.boot.file.dto.GenerateHtmlByUrlDTO;
import org.hzero.boot.file.dto.OnlyOfficeFileDTO;
import org.hzero.boot.file.service.OnlyOfficeCacheService;
import org.hzero.boot.file.util.HttpUtils;
import org.hzero.core.base.BaseConstants;
import org.hzero.file.api.dto.OnlyOfficeCallbackDTO;
import org.hzero.file.api.dto.ReviewChangeDTO;
import org.hzero.file.api.dto.SaveCallbackParamDTO;
import org.hzero.file.app.service.CapacityUsedService;
import org.hzero.file.app.service.OnlyOfficeService;
import org.hzero.file.domain.entity.File;
import org.hzero.file.domain.entity.FileEditLog;
import org.hzero.file.domain.repository.FileEditLogRepository;
import org.hzero.file.domain.repository.FileRepository;
import org.hzero.file.domain.service.factory.StoreFactory;
import org.hzero.file.domain.service.factory.StoreService;
import org.hzero.file.infra.constant.HfleMessageConstant;
import org.hzero.starter.file.entity.FileInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;
import org.springframework.util.DigestUtils;

import io.choerodon.core.exception.CommonException;

/**
 * onlyOffice服务
 *
 * @author shuangfei.zhu@hand-china.com 2019/06/27 15:58
 */
@Service
public class OnlyOfficeServiceImpl implements OnlyOfficeService {

    private static final Logger logger = LoggerFactory.getLogger(OnlyOfficeServiceImpl.class);

    private final StoreFactory factory;
    private final ObjectMapper objectMapper;
    private final FileRepository fileRepository;
    private final FileEditLogRepository fileEditLogRepository;
    private final CapacityUsedService residualCapacityService;
    private final OnlyOfficeClient onlyOfficeClient;
    private final OnlyOfficeCacheService onlyOfficeCacheService;

    @Autowired
    public OnlyOfficeServiceImpl(StoreFactory factory,
                                 ObjectMapper objectMapper,
                                 FileRepository fileRepository,
                                 FileEditLogRepository fileEditLogRepository,
                                 CapacityUsedService residualCapacityService,
                                 OnlyOfficeClient onlyOfficeClient,
                                 OnlyOfficeCacheService onlyOfficeCacheService) {
        this.factory = factory;
        this.objectMapper = objectMapper;
        this.fileRepository = fileRepository;
        this.fileEditLogRepository = fileEditLogRepository;
        this.residualCapacityService = residualCapacityService;
        this.onlyOfficeClient = onlyOfficeClient;
        this.onlyOfficeCacheService = onlyOfficeCacheService;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateFile(SaveCallbackParamDTO saveCallbackParam) {
        // 参数解密
        String param = saveCallbackParam.getExtra();
        try {
            ExtraDTO extraDTO = objectMapper.readValue(param, ExtraDTO.class);
            // 获取文件
            byte[] data = HttpUtils.downloadData(saveCallbackParam.getUrl());
            Long fileId = null;
            switch (extraDTO.getType()) {
                case BootFileConstant.FileIdType.KEY:
                    logger.debug("only office update file, fileKey : {}", saveCallbackParam.getFileId());
                    // 更新文件
                    fileId = updateByKey(extraDTO.getTenantId(), saveCallbackParam.getFileId(), data);
                    break;
                case BootFileConstant.FileIdType.URL:
                    logger.debug("only office update file, fileUrl : {}", saveCallbackParam.getFileId());
                    fileId = updateByUrl(extraDTO.getTenantId(), extraDTO.getBucketName(), saveCallbackParam.getFileId(), extraDTO.getStorageCode(), data);
                    break;
                default:
                    break;
            }
            if (fileId != null) {
                File file = fileRepository.selectByPrimaryKey(fileId);
                // 文件保存后记录日志
                List<ReviewChangeDTO> changeList = saveCallbackParam.getReviewChanges();
                for (ReviewChangeDTO item : changeList) {
                    FileEditLog fileEditLog = new FileEditLog()
                            .setTenantId(file.getTenantId())
                            .setFileId(fileId)
                            .setUserId(item.getUserId() == null ? 0L : item.getUserId())
                            .setEditType(item.getType())
                            .setChangeDate(item.getDate() == null ? new Date() : new Date(item.getDate()));
                    fileEditLogRepository.insertSelective(fileEditLog);
                }
            }
        } catch (Exception e) {
            logger.error("only office update text failed, saveCallbackParam : {} and fileId : {}", saveCallbackParam, saveCallbackParam.getFileId());
            throw new CommonException(BootFileConstant.ErrorCode.UPDATE_FILE, e);
        }
    }

    private Long updateByKey(Long tenantId, String fileKey, byte[] data) {
        File newFile = new File();
        try (InputStream inputStream = new ByteArrayInputStream(data)) {
            // 组合文件对象
            newFile.setMd5(DigestUtils.md5DigestAsHex(inputStream)).setFileKey(fileKey).setFileSize((long) data.length);
            File oldFile = fileRepository.getFile(fileKey);
            StoreService abstractFileService = factory.build(tenantId, oldFile.getStorageCode());
            Assert.notNull(abstractFileService, HfleMessageConstant.ERROR_FILE_STORE_CONFIG);
            abstractFileService.updateFileByKey(oldFile, newFile, new ByteArrayInputStream(data));
            return oldFile.getFileId();
        } catch (Exception e) {
            throw new CommonException(BaseConstants.ErrorCode.ERROR, e);
        }
    }

    private Long updateByUrl(Long tenantId, String bucketName, String url, String storageCode, byte[] data) {
        // 组合文件对象
        File file = new File()
                .setFileUrl(url)
                .setBucketName(bucketName)
                .setFileSize((long) data.length)
                .setTenantId(tenantId);
        StoreService fileService = factory.build(tenantId, storageCode);
        Assert.notNull(fileService, HfleMessageConstant.ERROR_FILE_STORE_CONFIG);
        // 获取文件信息
        File fileInfo = fileRepository.getFileByUrlAndLock(file.getTenantId(), file.getBucketName(), file.getFileUrl());
        Assert.notNull(fileInfo, HfleMessageConstant.ERROR_DATA_NOT_EXISTS);
        Long oldSize = fileInfo.getFileSize();
        fileInfo.setFileSize(file.getFileSize());
        // 更新文件文档
        FileInfo fileMsg = new FileInfo();
        BeanUtils.copyProperties(fileInfo, fileMsg);
        String fileUrl = fileService.getAbstractFileService().upload(fileMsg, new ByteArrayInputStream(data));
        fileInfo.setFileUrl(fileUrl);
        // 更新文件信息
        fileRepository.updateByPrimaryKeySelective(fileInfo);
        // 修改租户已使用存储容量
        residualCapacityService.refreshCache(file.getTenantId(), file.getFileSize() - oldSize);
        return fileInfo.getFileId();
    }

    @Override
    public void update(OnlyOfficeCallbackDTO callback) {
        if (!Objects.equals(callback.getStatus(), BaseConstants.Digital.TWO)) {
            // 未更新 不处理
            return;
        }
        OnlyOfficeFileDTO fileDTO = null;
        try {
            fileDTO = onlyOfficeCacheService.getFile(onlyOfficeCacheService.getFileKey(callback.getKey()));
            // 获取文件
            byte[] data = HttpUtils.downloadData(callback.getUrl());
            switch (fileDTO.getType()) {
                case BootFileConstant.FileIdType.KEY:
                    logger.debug("only office update file, fileKey : {}", callback.getKey());
                    // 更新文件
                    updateByKey(fileDTO.getTenantId(), fileDTO.getFileKey(), data);
                    break;
                case BootFileConstant.FileIdType.URL:
                    logger.debug("only office update file, fileUrl : {}", callback.getUrl());
                    updateByUrl(fileDTO.getTenantId(), fileDTO.getBucketName(), fileDTO.getUrl(), fileDTO.getStorageCode(), data);
                    break;
                default:
                    break;
            }
            onlyOfficeCacheService.refreshKey(fileDTO);
        } catch (Exception e) {
            if (fileDTO != null) {
                onlyOfficeCacheService.refreshKey(fileDTO);
            }
            logger.error("only office update text failed, callback : {} and key : {}", callback, callback.getKey());
            throw new CommonException(BootFileConstant.ErrorCode.UPDATE_FILE, e);
        }

    }

    @Override
    public String generateHtmlByUrl(Long tenantId, GenerateHtmlByUrlDTO generateHtmlParam) {
        Assert.notNull(onlyOfficeClient, HfleMessageConstant.ONLY_OFFICE_CONFIG);
        return onlyOfficeClient.generateHtmlByUrl(tenantId, generateHtmlParam.getBucketName(),
                generateHtmlParam.getStorageCode(), generateHtmlParam.getUrl(), generateHtmlParam.getPermission());
    }

    @Override
    public String generateHtmlByKey(Long tenantId, GenerateHtmlByKeyDTO generateHtmlParam) {
        Assert.notNull(onlyOfficeClient, HfleMessageConstant.ONLY_OFFICE_CONFIG);
        return onlyOfficeClient.generateHtmlByKey(tenantId, generateHtmlParam.getFileKey(), generateHtmlParam.getPermission());
    }
}
