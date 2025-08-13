package org.hzero.wechat.service;

import org.hzero.wechat.dto.*;
import org.springframework.http.ResponseEntity;


public interface WechatMaterialManageService {

    /**
     *  新增临时素材
     * @param access_token
     * @param type
     * @param media
     * @return
     */
    UploadMediaResultDTO uploadMedia(String access_token, String type, MediaDTO media);

    /**
     * 获取临时素材
     * @param access_token
     * @param media_id
     * @return
     */
    ResponseEntity<GetMediaResulDTO> getMedia(String access_token, String media_id);

    /**
     *  获取高清语音素材
     * @param access_token
     * @param media_id
     * @return
     */
    ResponseEntity<DefaultResultDTO> getHDVoice(String access_token, String media_id);

    /**
     *  新增永久图文素材
     * @param access_token
     * @param addMediaDTO
     * @return
     */
    AddMediaResultDTO addMedia(String access_token, AddMediaDTO addMediaDTO);

    /**
     *  上传图文消息内的图片获取URL
     * @param access_token
     * @param mediaDTO
     * @return
     */
    UploadImageMediaResultDTO uploadImageMediaResultDTO (String access_token, MediaDTO mediaDTO);

    /**
     * 新增其他类型永久素材
     * @return
     */
    AddMaterialResultDTO addMaterial(String access_token, String type, MediaDTO mediaDTO, DescriptionDTO description);

    /**
     * 获取永久素材
     * @param access_token
     * @param media_id
     * @return
     */
    GetMaterialResultDTO getMaterial(String access_token, String media_id);

    /**
     * 删除永久素材
     * @param access_token
     * @param media_id
     * @return
     */
    DefaultResultDTO deleteMaterial(String access_token, String media_id);

    /**
     * 修改永久图文素材
     * @param access_token
     * @param updateMaterialDTO
     * @return
     */
    DefaultResultDTO updateMaterial(String access_token,UpdateMaterialDTO updateMaterialDTO );

    /**
     * 获取素材总数
     * @param access_token
     * @return
     */
    GetMaterialCountResultDTO getMaterialCount(String  access_token);

    /**
     *  获取素材列表
     * @param access_token
     * @param updateMaterialDTO
     * @return
     */
    GetMaterialListResultDTO getMaterialList(String access_token, GetMaterialListDTO updateMaterialDTO);


}
