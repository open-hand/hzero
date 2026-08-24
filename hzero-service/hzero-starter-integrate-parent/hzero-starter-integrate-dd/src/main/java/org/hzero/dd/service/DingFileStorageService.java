package org.hzero.dd.service;

import org.hzero.dd.dto.*;

public interface DingFileStorageService {

    /**
     * 上传媒体文件
     * @param accessToken
     * @param type
     * @param media
     * @return
     */
    UploadMediaDTO uploadMedia(String accessToken, String type, String media);

    /**
     * 发送钉盘文件给指定用户
     * @param accessToken
     * @param agent_id
     * @param userid
     * @param media_id
     * @param file_name
     * @return
     */
    DefaultResultDTO sendFileToUser(String accessToken, String agent_id, String userid, String media_id, String file_name);


    /**
     * 新增文件到用户自定义空间
     * @param accessToken
     * @param agent_id
     * @param code
     * @param media_id
     * @param space_id
     * @param folder_id
     * @param name
     * @param overwrite
     * @return
     */
    AddFileDTO addFile(String accessToken, String agent_id, String code, String media_id, String space_id, String folder_id, String name, Boolean overwrite );


    /**
     * 获取企业下的自定义空间
     * @param accessToken
     * @param domain
     * @param agent_id
     * @return
     */
    GetEnterprisesSpaceDTO getEnterprisesSpace(String accessToken, String domain, String agent_id);

    /**
     * 获取应用自定义空间使用详情
     * @param accessToken
     * @param domain
     * @param agent_id
     * @return
     */
    GetEnterprisesSpaceInfoResultDTO getEnterprisesSpaceInfo(String accessToken, String domain, String agent_id);


    /**
     *  授权用户访问企业自定义空间
     * @param accessToken
     * @param agent_id
     * @param domain
     * @param type
     * @param userid
     * @param path
     * @param fileids
     * @param duration
     * @return
     */
    DefaultResultDTO  getAuthorizedUserSpace(String accessToken, String agent_id, String domain, String type, String userid, String path, String fileids, Integer duration );

    /**
     *  单步上传文件
     * @param accessToken
     * @param agent_id
     * @param file_size
     * @return
     */
    UploadFileDTO singleUploadFile(String accessToken, String agent_id, Integer file_size);

    /**
     * 开启分块上传事务
     * @param accessToken
     * @param agent_id
     * @param file_size
     * @param chunk_numbers
     * @return
     */
    UploadFileTransactionDTO openUploadFileTransaction(String  accessToken, String agent_id, Integer file_size, Integer chunk_numbers);

    /**
     * 上传文件块
     * @param accessToken
     * @param agent_id
     * @param upload_id
     * @param chunk_sequence
     * @return
     */
    DefaultResultDTO chunkUploadFile(String  accessToken, String agent_id, String upload_id, Integer chunk_sequence);

    /**
     * 提交文件上传事务
     * @param accessToken
     * @param agent_id
     * @param file_size
     * @param chunk_numbers
     * @param upload_id
     * @return
     */
    UploadFileDTO uploadFileTransaction(String  accessToken, String agent_id, Integer file_size, Integer chunk_numbers, String upload_id);

}
