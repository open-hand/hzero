package org.hzero.file.infra.repository.impl;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.choerodon.mybatis.pagehelper.domain.Sort;
import org.apache.commons.lang3.StringUtils;
import org.hzero.boot.platform.lov.adapter.LovAdapter;
import org.hzero.boot.platform.lov.dto.LovValueDTO;
import org.hzero.core.base.BaseConstants;
import org.hzero.file.api.dto.FileDTO;
import org.hzero.file.api.dto.FileParamsDTO;
import org.hzero.file.domain.entity.File;
import org.hzero.file.domain.repository.FileRepository;
import org.hzero.file.infra.constant.HfleConstant;
import org.hzero.file.infra.constant.HfleMessageConstant;
import org.hzero.file.infra.mapper.FileMapper;
import org.hzero.mybatis.base.impl.BaseRepositoryImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;

import io.choerodon.core.domain.Page;
import io.choerodon.core.exception.CommonException;
import io.choerodon.core.oauth.DetailsHelper;
import io.choerodon.mybatis.pagehelper.PageHelper;
import io.choerodon.mybatis.pagehelper.domain.PageRequest;

/**
 * 上传文件 资源库实现
 *
 * @author xianzhi.chen@hand-china.com 2018-07-11 10:34:37
 */
@Component("fileRepository")
public class FileRepositoryImpl extends BaseRepositoryImpl<File> implements FileRepository {

    private final FileMapper fileMapper;
    private final LovAdapter lovAdapter;

    @Autowired
    public FileRepositoryImpl(FileMapper fileMapper,
                              LovAdapter lovAdapter) {
        this.fileMapper = fileMapper;
        this.lovAdapter = lovAdapter;
    }

    @Override
    public List<FileDTO> selectFileByAttachmentUUID(Long tenantId, String bucketName, String attachmentUUID,
                                                    String sortDirection) {
        Sort.Direction direction = Sort.Direction.fromStringOrNull(StringUtils.upperCase(sortDirection));
        Sort sort = new Sort((direction == null ? Sort.Direction.DESC : direction), File.FIELD_FILE_ID);
        return PageHelper.doSort(sort, () -> fileMapper.selectFileByAttachmentUUID(bucketName,
                Collections.singletonList(attachmentUUID)));
    }

    @Override
    public Page<FileDTO> selectFileByAttachmentUUID(PageRequest pageRequest, Long tenantId, String bucketName,
                                                    List<String> attachmentUUIDs) {
        return PageHelper.doPageAndSort(pageRequest, () -> fileMapper.selectFileByAttachmentUUID(bucketName,
                attachmentUUIDs));
    }

    @Override
    public Integer selectFileCountByAttachmentUUID(Long tenantId, String bucketName, String attachmentUUID) {
        return fileMapper.selectFileCountByAttachmentUUID(bucketName, attachmentUUID);
    }

    @Override
    public Map<String, Boolean> checkUuid(Long tenantId, String bucketName, List<String> uuidList) {
        Map<String, Boolean> result = new HashMap<>(BaseConstants.Digital.SIXTEEN);
        if (!CollectionUtils.isEmpty(uuidList)) {
            uuidList.forEach(uuid -> result.put(uuid, selectFileCountByAttachmentUUID(tenantId, bucketName, uuid) > 0));
        }
        return result;
    }

    @Override
    public Integer selectFileCountByAttachmentUuidAndLock(Long tenantId, String bucketName, String attachmentUUID) {
        return fileMapper.selectFileCountByAttachmentUuidAndLock(tenantId, bucketName, attachmentUUID);
    }

    @Override
    public List<FileDTO> selectFileByUrls(String bucketName, List<String> urls) {
        return fileMapper.selectFileWithUrls(bucketName, urls);
    }

    @Override
    public List<FileDTO> selectFileByUrls(Long tenantId, String bucketName, List<String> urls, String attachmentUUID) {
        return fileMapper.selectFileByUrls(tenantId, bucketName, urls, attachmentUUID);
    }

    @Override
    public Long selectFileSize(Long tenantId, String bucketName, String attachmentUUID, String url) {
        return fileMapper.selectFileSizeByUrl(tenantId, bucketName, attachmentUUID, url);
    }

    @Override
    public void deleteFileByUrls(Long tenantId, String bucketName, String attachmentUUID, List<String> urls) {
        fileMapper.deleteFileByUrls(tenantId, bucketName, attachmentUUID, urls);
    }

    @Override
    public void deleteFileByAttachmentUUID(Long tenantId, String bucketName, String attachmentUUID) {
        fileMapper.deleteFileByAttachmentUUID(tenantId, bucketName, attachmentUUID);
    }

    @Override
    public File getFileByUrlAndLock(Long tenantId, String bucketName, String fileUrl) {
        return fileMapper.selectFileByUrlAndLock(tenantId, bucketName, fileUrl);
    }

    @Override
    public Page<File> pageFileList(FileParamsDTO fileParamsDTO, PageRequest pageRequest) {
        buildFileSize(fileParamsDTO);
        // 获取值集
        List<LovValueDTO> valueList = lovAdapter.queryLovValue("HFLE.DIRECTORY_MAPPING",
                DetailsHelper.getUserDetails().getTenantId());
        Map<String, String> map = new HashMap<>(16);
        valueList.forEach(item -> map.put(item.getValue(), item.getMeaning()));
        Page<File> filePage = PageHelper.doPageAndSort(pageRequest, () -> fileMapper.listFile(fileParamsDTO));
        filePage.getContent().forEach(item -> item.setTableName(map.get(item.getDirectory())));
        return filePage;
    }

    @Override
    public List<File> listFile(FileParamsDTO fileParamsDTO) {
        buildFileSize(fileParamsDTO);
        return fileMapper.listFile(fileParamsDTO);
    }

    private void buildFileSize(FileParamsDTO fileParamsDTO) {
        Long fileMinSize = fileSizeSwitch(fileParamsDTO.getFileMinSize(), fileParamsDTO.getFileMinUnit());
        Long fileMaxSize = fileSizeSwitch(fileParamsDTO.getFileMaxSize(), fileParamsDTO.getFileMaxUnit());
        if (fileMinSize != null && fileMaxSize != null && fileMinSize > fileMaxSize) {
            throw new CommonException(HfleMessageConstant.ERROR_RANGE);
        }
        fileParamsDTO.setFileMinSize(fileMinSize);
        fileParamsDTO.setFileMaxSize(fileMaxSize);
    }

    private Long fileSizeSwitch(Long fileSize, String fileUnit) {
        if (fileSize != null && fileUnit != null) {
            if (HfleConstant.StorageUnit.KB.equals(fileUnit)) {
                fileSize = fileSize * 1024;
            }
            if (HfleConstant.StorageUnit.MB.equals(fileUnit)) {
                fileSize = fileSize * 1024 * 1024;
            }
        }
        return fileSize;
    }


    @Override
    public List<File> sumFileSizeByTenantId() {
        return fileMapper.sumFileSizeByTenantId();
    }

    @Override
    public List<String> selectFileUrlByAttachmentUUID(Long tenantId, String bucketName, String attachmentUUID) {
        return fileMapper.selectFileUrlByAttachmentUUID(tenantId, bucketName, attachmentUUID);
    }

    @Override
    public File getFile(String fileKey) {
        List<File> fileList = select(new File().setFileKey(fileKey));
        Assert.isTrue(!CollectionUtils.isEmpty(fileList), HfleMessageConstant.ERROR_FILE_URL);
        return fileList.get(0);
    }
}
