package org.hzero.wechat.enterprise.service.impl;

import com.alibaba.fastjson.JSON;
import org.hzero.wechat.enterprise.constant.WechatEnterpriseUrl;
import org.hzero.wechat.enterprise.dto.*;
import org.hzero.wechat.enterprise.service.WechatMaterialManageService;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import javax.annotation.Resource;
import java.util.Map;

public class WechatMaterialManageServiceImp implements WechatMaterialManageService {

    @Resource(name = "wdRestTemplate")
    protected RestTemplate restTemplate;

    @Override
    public UploadMediaResultDTO uploadMedia(String accessToken, String type, String name, String filename, String filelength, String contentType) {
        MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
        body.add("name", name);
        body.add("filename", filename);
        body.add("filelength", filelength);
        body.add("Content-Type", contentType );
        HttpEntity<MultiValueMap<String, String>> entity = new HttpEntity<>(body, buildHttpHeaders());
        return restTemplate.postForObject(WechatEnterpriseUrl.UPLOAD_MEDIA_URL +"?access_token=" + accessToken +"&type=" + type,entity,UploadMediaResultDTO.class );
    }

    @Override
    public UploadImageResultDTO uploadImage(String accessToken, String name, String filename, String contentType, String contentLength) {
        MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
        body.add("name",name);
        body.add("filename",filename);
        body.add("Content-Type",contentType);
        body.add("Content-Length",contentLength);
        HttpEntity<MultiValueMap<String, String>> entity = new HttpEntity<>(body, buildHttpHeaders());
        return restTemplate.postForObject(WechatEnterpriseUrl.UPLOAD_IMAGE_URL + accessToken ,entity,UploadImageResultDTO.class );

    }

    @Override
    public ResponseEntity<DefaultResultDTO> getMedia(String accessToken, String media_id) {
        ResponseEntity<DefaultResultDTO> exchange = restTemplate.exchange(WechatEnterpriseUrl.GET_MEDIA_URL  + accessToken + "&media_id" + media_id, HttpMethod.GET, null, DefaultResultDTO.class);
        return exchange;
    }

    @Override
    public ResponseEntity<DefaultResultDTO> getHDMedia(String accessToken, String media_id) {
        ResponseEntity<DefaultResultDTO> exchange = restTemplate.exchange(WechatEnterpriseUrl.GET_HD_MEDIA_URL  + accessToken +"&media_id" + media_id, HttpMethod.GET, null, DefaultResultDTO.class);
        return  exchange;
    }

    protected HttpHeaders buildHttpHeaders() {
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.set("Content-Type", "multipart/form-data");
        return httpHeaders;
    }

}
