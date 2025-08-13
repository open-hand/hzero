package org.hzero.file.infra.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Param;
import org.hzero.file.api.dto.FileDTO;
import org.hzero.file.api.dto.FileParamsDTO;
import org.hzero.file.domain.entity.File;

import io.choerodon.mybatis.common.BaseMapper;

/**
 * 上传文件Mapper
 *
 * @author xianzhi.chen@hand-china.com 2018-07-11 10:34:37
 */
public interface FileMapper extends BaseMapper<File> {

    /**
     * 文件列表汇总
     *
     * @param fileParamsDTO 参数
     * @return File
     */
    List<File> listFile(FileParamsDTO fileParamsDTO);

    /**
     * 查询附件UUID下的文件
     *
     * @param bucketName     文件目录
     * @param attachmentUUIDs UUID
     * @return List<FileDTO>
     */
    List<FileDTO> selectFileByAttachmentUUID(@Param("bucketName") String bucketName,
                                             @Param("attachmentUUIDs") List<String> attachmentUUIDs);

    /**
     * 查询附件UUID下的文件数量
     *
     * @param bucketName     文件目录
     * @param attachmentUUID UUID
     * @return Integer
     */
    Integer selectFileCountByAttachmentUUID(@Param("bucketName") String bucketName,
                                            @Param("attachmentUUID") String attachmentUUID);

    /**
     * 查询附件UUID下文件数量并锁定
     *
     * @param tenantId       租户ID
     * @param bucketName     文件目录
     * @param attachmentUUID UUID
     * @return Integer
     */
    Integer selectFileCountByAttachmentUuidAndLock(@Param("tenantId") Long tenantId,
                                                   @Param("bucketName") String bucketName, @Param("attachmentUUID") String attachmentUUID);

    /**
     * 获取文件大小
     *
     * @param tenantId       租户ID
     * @param bucketName     文件目录
     * @param attachmentUUID uuid
     * @param url            url
     * @return list
     */
    Long selectFileSizeByUrl(@Param("tenantId") Long tenantId,
                             @Param("bucketName") String bucketName,
                             @Param("attachmentUUID") String attachmentUUID,
                             @Param("url") String url);

    /**
     * 跨租户根据URL批量查询文件信息
     *
     * @param bucketName 文件目录
     * @param urls       url
     * @return List<FileDTO>
     */
    List<FileDTO> selectFileWithUrls(@Param("bucketName") String bucketName,
                                     @Param("urls") List<String> urls);

    /**
     * 根据URL批量查询文件信息
     *
     * @param tenantId       租户Id
     * @param bucketName     文件目录
     * @param urls           url
     * @param attachmentUUID 附件ID
     * @return List<FileDTO>
     */
    List<FileDTO> selectFileByUrls(@Param("tenantId") Long tenantId,
                                   @Param("bucketName") String bucketName,
                                   @Param("urls") List<String> urls,
                                   @Param("attachmentUUID") String attachmentUUID);

    /**
     * 删除文件信息
     *
     * @param tenantId       租户ID
     * @param attachmentUUID UUID
     * @param bucketName     文件目录
     * @param urls           url
     */
    void deleteFileByUrls(@Param("tenantId") Long tenantId, @Param("bucketName") String bucketName,
                          @Param("attachmentUUID") String attachmentUUID, @Param("urls") List<String> urls);

    /**
     * 删除文件信息
     *
     * @param tenantId       租户ID
     * @param attachmentUUID UUID
     * @param bucketName     文件目录
     */
    void deleteFileByAttachmentUUID(@Param("tenantId") Long tenantId, @Param("bucketName") String bucketName,
                                    @Param("attachmentUUID") String attachmentUUID);

    /**
     * 获取附件UUID下文件URL集合
     *
     * @param tenantId       租户ID
     * @param bucketName     文件目录
     * @param attachmentUUID UUID
     * @return List<String>
     */
    List<String> selectFileUrlByAttachmentUUID(@Param("tenantId") Long tenantId, @Param("bucketName") String bucketName,
                                               @Param("attachmentUUID") String attachmentUUID);


    /**
     * 获取文件信息并对数据锁定
     *
     * @param tenantId   租户ID
     * @param bucketName 文件目录
     * @param fileUrl    url
     * @return File
     */
    File selectFileByUrlAndLock(@Param("tenantId") Long tenantId, @Param("bucketName") String bucketName,
                                @Param("fileUrl") String fileUrl);

    /**
     * 查询租户的文件总大小
     *
     * @return 文件列表
     */
    List<File> sumFileSizeByTenantId();
}
