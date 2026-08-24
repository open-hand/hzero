package org.hzero.wechat.enterprise.service;

import org.hzero.wechat.enterprise.dto.DefaultResultDTO;
import org.hzero.wechat.enterprise.dto.UploadImageResultDTO;
import org.hzero.wechat.enterprise.dto.UploadMediaResultDTO;
import org.springframework.http.ResponseEntity;

public interface WechatMaterialManageService {

    /**
     * 上传临时素材
     * @param accessToken
     * @param type
     * @param name
     * @param filename
     * @param filelength
     * @param contentType
     * @return
     */
     UploadMediaResultDTO uploadMedia(String accessToken, String type, String name, String filename, String filelength, String contentType);

    /**
     * 上传图片
     * @param accessToken
     * @param name
     * @param filename
     * @param Content_type
     * @param contentLength
     * @return
     */
     UploadImageResultDTO uploadImage(String accessToken, String name, String filename, String Content_type, String contentLength) ;

    /**
     *  获取临时素材
     * @param accessToken
     * @param media_id
     * @return
     */
     ResponseEntity<DefaultResultDTO> getMedia(String accessToken, String media_id);

    /**
     * 获取高清语音素材
     * @param accessToken
     * @param media_id
     * @return
     */
     ResponseEntity<DefaultResultDTO> getHDMedia(String accessToken, String media_id);



    }
