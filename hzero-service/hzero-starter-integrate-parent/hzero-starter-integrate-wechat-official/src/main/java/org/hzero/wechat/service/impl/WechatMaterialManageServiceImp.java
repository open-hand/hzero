package org.hzero.wechat.service.impl;

import com.alibaba.fastjson.JSON;
import org.hzero.wechat.constant.WechatApi;
import org.hzero.wechat.dto.*;
import org.hzero.wechat.service.WechatMaterialManageService;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import javax.annotation.Resource;

public class WechatMaterialManageServiceImp implements WechatMaterialManageService {

    @Resource(name = "wdRestTemplate")
    protected RestTemplate restTemplate;

    @Override
    public UploadMediaResultDTO uploadMedia(String access_token, String type, MediaDTO media) {
        MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
        body.add("filename", media.getFilename());
        body.add("Content-Type",media.getContentType());
        body.add("contentLength",media.getFileLength());
        return restTemplate.postForObject(WechatApi.UPLOAD_MEDIA_URL + access_token + "&type=" + type , new HttpEntity<MultiValueMap<String, String>>(body,buildFormDateHttpHeaders()),UploadMediaResultDTO.class );
    }

    @Override
    public ResponseEntity<GetMediaResulDTO> getMedia(String access_token, String media_id) {
        ResponseEntity<GetMediaResulDTO> exchange = restTemplate.exchange(WechatApi.GET_MEDIA_URL + access_token + "&media_id=" +  media_id , HttpMethod.GET, null, GetMediaResulDTO.class);
        return  exchange;
    }

    @Override
    public ResponseEntity<DefaultResultDTO> getHDVoice(String access_token, String media_id) {
        ResponseEntity<DefaultResultDTO> exchange = restTemplate.exchange(WechatApi.GET_HD_VOICE_URL + access_token + "& media_id=" +  media_id , HttpMethod.GET, null, DefaultResultDTO.class);
        return  exchange;
    }

    @Override
    public AddMediaResultDTO addMedia(String access_token, AddMediaDTO addMediaDTO) {
        return restTemplate.postForObject(WechatApi. ADD_MEDIA_URL + access_token, new HttpEntity<>(JSON.toJSONString(addMediaDTO), buildJsonHttpHeaders()), AddMediaResultDTO.class);
    }

    @Override
    public UploadImageMediaResultDTO uploadImageMediaResultDTO(String access_token, MediaDTO mediaDTO) {
        return restTemplate.postForObject(WechatApi.UPLOAD_IMAGE_MEDIA_URL + access_token, new HttpEntity<>(JSON.toJSONString(mediaDTO), buildJsonHttpHeaders()), UploadImageMediaResultDTO.class);
    }

    @Override
    public AddMaterialResultDTO addMaterial(String access_token, String type, MediaDTO media, DescriptionDTO description) {
        MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
        body.add("filename", media.getFilename());
        body.add("Content-Type",media.getContentType());
        if(description!=null) {
            body.add("title",description.getTitle());
            body.add("introduction", description.getIntroduction());
        }
        return restTemplate.postForObject(WechatApi.ADD_MATERIAL_URL + access_token, new HttpEntity(body,buildFormDateHttpHeaders()), AddMaterialResultDTO.class);
    }

    @Override
    public GetMaterialResultDTO getMaterial(String access_token, String media_id) {
        return restTemplate.postForObject(WechatApi.GET_MATERIAL_URL + access_token, new HttpEntity<>(JSON.toJSONString(media_id), buildJsonHttpHeaders()), GetMaterialResultDTO.class);
    }

    @Override
    public DefaultResultDTO deleteMaterial(String access_token, String media_id) {
        return restTemplate.postForObject(WechatApi.DELETE_MATERIAL_URL + access_token, new HttpEntity<>(JSON.toJSONString(media_id), buildJsonHttpHeaders()),  DefaultResultDTO.class);
    }

    @Override
    public DefaultResultDTO updateMaterial(String access_token, UpdateMaterialDTO updateMaterialDTO) {
        return restTemplate.postForObject(WechatApi.UPDATE_MATERIAL_URL + access_token, new HttpEntity<>(JSON.toJSONString(updateMaterialDTO), buildJsonHttpHeaders()),  DefaultResultDTO.class);

    }

    @Override
    public GetMaterialCountResultDTO getMaterialCount(String access_token) {
        return restTemplate.getForObject(WechatApi.GET_MATERIAL_COUNT_URL + access_token,  GetMaterialCountResultDTO.class);

    }

    @Override
    public GetMaterialListResultDTO getMaterialList(String access_token, GetMaterialListDTO updateMaterialDTO) {
        return restTemplate.postForObject(WechatApi.GET_MATERIAL_LIST_URL + access_token, new HttpEntity<>(JSON.toJSONString(updateMaterialDTO), buildJsonHttpHeaders()),  GetMaterialListResultDTO.class);

    }


    protected HttpHeaders buildFormDateHttpHeaders() {
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.set("Content-Type", "multipart/form-data");
        return httpHeaders;
    }

    protected HttpHeaders buildJsonHttpHeaders() {
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.set("Content-Type", "application/json");
        return httpHeaders;
    }



}
