package org.hzero.file.domain.repository;

import java.util.List;
import java.util.Map;

import org.hzero.file.api.dto.FileDTO;
import org.hzero.file.api.dto.FileParamsDTO;
import org.hzero.file.domain.entity.File;
import org.hzero.mybatis.base.BaseRepository;

import io.choerodon.core.domain.Page;
import io.choerodon.mybatis.pagehelper.domain.PageRequest;

/**
 * 上传文件资源库
 *
 * @author xianzhi.chen@hand-china.com 2018-07-11 10:34:37
 */
public interface FileRepository extends BaseRepository<File> {

    /**
     * 根据附件UUID查询文件列表
     *
     * @param tenantId       租户ID
     * @param bucketName     文件目录
     * @param attachmentUUID 附件UUID
     * @param sortDirection  排序顺序
     * @return List<FileDTO>
     */
    List<FileDTO> selectFileByAttachmentUUID(Long tenantId, String bucketName, String attachmentUUID, String sortDirection);

    /**
     * 根据附件UUID查询文件列表
     *
     * @param pageRequest     分页
     * @param tenantId        租户ID
     * @param bucketName      文件目录
     * @param attachmentUUIDs 附件UUID
     * @return List<FileDTO>
     */
    Page<FileDTO> selectFileByAttachmentUUID(PageRequest pageRequest, Long tenantId, String bucketName, List<String> attachmentUUIDs);

    /**
     * 根据附件UUID获取文件个数
     *
     * @param tenantId       租户ID
     * @param bucketName     文件目录
     * @param attachmentUUID UUID
     * @return Integer
     */
    Integer selectFileCountByAttachmentUUID(Long tenantId, String bucketName, String attachmentUUID);

    /**
     * 检查uuid是否有文件
     *
     * @param tenantId   租户id
     * @param bucketName 桶名
     * @param uuidList   uuid
     * @return 结果
     */
    Map<String, Boolean> checkUuid(Long tenantId, String bucketName, List<String> uuidList);

    /**
     * 根据附件UUID获取文件个数并锁定
     *
     * @param tenantId       租户ID
     * @param bucketName     文件目录
     * @param attachmentUUID UUID
     * @return Integer
     */
    Integer selectFileCountByAttachmentUuidAndLock(Long tenantId, String bucketName, String attachmentUUID);

    /**
     * 跨租户批量获取文件信息
     *
     * @param bucketName 文件目录
     * @param urls       URL集合
     * @return List<FileDTO>
     */
    List<FileDTO> selectFileByUrls(String bucketName, List<String> urls);

    /**
     * 批量获取文件信息
     *
     * @param tenantId       租户ID
     * @param bucketName     文件目录
     * @param urls           URL集合
     * @param attachmentUUID 附件ID
     * @return List<FileDTO>
     */
    List<FileDTO> selectFileByUrls(Long tenantId, String bucketName, List<String> urls, String attachmentUUID);

    /**
     * 获取文件大小
     *
     * @param tenantId       租户ID
     * @param bucketName     文件目录
     * @param attachmentUUID UUID
     * @param url            url
     * @return list
     */
    Long selectFileSize(Long tenantId, String bucketName, String attachmentUUID, String url);

    /**
     * 根据URL批量删除文件信息
     *
     * @param tenantId       租户ID
     * @param bucketName     文件目录
     * @param attachmentUUID UUID
     * @param urls           list
     */
    void deleteFileByUrls(Long tenantId, String bucketName, String attachmentUUID, List<String> urls);

    /**
     * 根据附件UUID删除文件信息
     *
     * @param tenantId       租户ID
     * @param bucketName     文件目录
     * @param attachmentUUID UUID
     */
    void deleteFileByAttachmentUUID(Long tenantId, String bucketName, String attachmentUUID);

    /**
     * 查询附件UUID下文件URL集合
     *
     * @param tenantId       租户ID
     * @param bucketName     文件目录
     * @param attachmentUUID UUID
     * @return List<String>
     */
    List<String> selectFileUrlByAttachmentUUID(Long tenantId, String bucketName, String attachmentUUID);


    /**
     * 根据URL获取文件信息并进行锁定
     *
     * @param tenantId   租户ID
     * @param bucketName 文件目录
     * @param fileUrl    url
     * @return File
     */
    File getFileByUrlAndLock(Long tenantId, String bucketName, String fileUrl);

    /**
     * 文件列表汇总
     *
     * @param fileParamsDTO 参数
     * @param pageRequest   PageRequest
     * @return File
     */
    Page<File> pageFileList(FileParamsDTO fileParamsDTO, PageRequest pageRequest);

    /**
     * 文件列表汇总
     *
     * @param fileParamsDTO 参数
     * @return File
     */
    List<File> listFile(FileParamsDTO fileParamsDTO);

    /**
     * 查询租户文件总大小
     *
     * @return 文件集合
     */
    List<File> sumFileSizeByTenantId();

    /**
     * 根据文件key获取文件信息
     *
     * @param fileKey 文件key
     * @return 文件信息
     */
    File getFile(String fileKey);
}
