package org.hzero.dd.service.impl;

import org.hzero.dd.constant.DingUrl;
import org.hzero.dd.dto.*;
import org.hzero.dd.service.DingFileStorageService;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import javax.annotation.Resource;

@Service
public class DingFileStorageServiceImp implements DingFileStorageService {
    @Resource(name = "wdRestTemplate")
    protected RestTemplate restTemplate;

    @Override
    public UploadMediaDTO uploadMedia(String accessToken, String type, String media) {
        return restTemplate.postForObject(DingUrl.UPLOAD_MEDIA_URL + "?access_token=" + accessToken + "&type=" + type,  new HttpEntity<>(media, buildHttpHeaders()), UploadMediaDTO.class);
    }

    @Override
    public DefaultResultDTO sendFileToUser(String accessToken, String agent_id, String userid, String media_id, String file_name) {
        return restTemplate.getForObject(DingUrl.SEND_FILE_TO_USER_URL + "?access_token=" + accessToken + "&agent_id=" + agent_id + "userid&=" + userid + "&media_id=" +media_id + "file_name=" + file_name
                , DefaultResultDTO.class);
    }

    @Override
    public AddFileDTO addFile(String accessToken, String agent_id, String code, String media_id, String space_id, String folder_id, String name, Boolean overwrite) {
        return restTemplate.getForObject(DingUrl.ADD_FILE_URL + "?access_token=" + accessToken + "&agent_id=" + agent_id + "code&=" + code + "&media_id=" +media_id + "&space_id=" + space_id +"&folder_id" + folder_id
        +"&name" +name+ "&overwrite"+ overwrite,  AddFileDTO.class);
    }

    @Override
    public GetEnterprisesSpaceDTO getEnterprisesSpace(String accessToken, String domain, String agent_id) {
        return restTemplate.getForObject(DingUrl.GET_ENTERPRISES_SPACE_URL + "?access_token=" + accessToken + "&agent_id=" + agent_id + "&domain=" + domain,GetEnterprisesSpaceDTO.class);
    }

    @Override
    public GetEnterprisesSpaceInfoResultDTO getEnterprisesSpaceInfo(String accessToken, String domain, String agent_id) {
        return restTemplate.postForObject(DingUrl.GET_ENTERPRISES_SPACE_INFO_URL + "?access_token=" + accessToken + "&domain=" +domain + "&agent_id" + agent_id,  new HttpEntity<>(null, buildHttpHeaders()), GetEnterprisesSpaceInfoResultDTO.class);
    }

    @Override
    public DefaultResultDTO getAuthorizedUserSpace(String accessToken, String agent_id, String domain, String type, String userid, String path, String fileids, Integer duration) {
        return restTemplate.getForObject(DingUrl.GET_AUTHORIZED_USER_SPACE_URL + "?access_token=" + accessToken + "&agent_id=" + agent_id + "&domain=" + domain +"&type="+ type + "&userid=" + userid + "&path=" +path + "&fileids=" + fileids + "&duration=" + duration , GetEnterprisesSpaceDTO.class);
    }

    @Override
    public UploadFileDTO singleUploadFile(String accessToken, String agent_id, Integer file_size) {
        return restTemplate.getForObject(DingUrl.SINGLE_UPLOAD_FILE_URL + "?access_token=" + accessToken + "&agent_id=" + agent_id + "&file_size=" + file_size , UploadFileDTO.class);
    }

    @Override
    public UploadFileTransactionDTO openUploadFileTransaction(String accessToken, String agent_id, Integer file_size, Integer chunk_numbers) {
        return restTemplate.getForObject(DingUrl.OPEN_UPLOAD_FILE_TRANSACTION_URL + "?access_token=" + accessToken + "&agent_id=" + agent_id + "&file_size=" + file_size + "&chunk_numbers=" + chunk_numbers, UploadFileTransactionDTO.class);
    }

    @Override
    public DefaultResultDTO chunkUploadFile(String accessToken, String agent_id, String upload_id, Integer chunk_sequence) {
        return restTemplate.getForObject(DingUrl.CHUNK_UPLOAD_FILE + "?access_token=" + accessToken + "&agent_id=" + agent_id + "&upload_id=" + upload_id + "&chunk_sequence=" + chunk_sequence, DefaultResultDTO.class);
    }

    @Override
    public UploadFileDTO uploadFileTransaction(String accessToken, String agent_id, Integer file_size, Integer chunk_numbers, String upload_id) {
        return restTemplate.getForObject(DingUrl.UPLOAD_FILE_TRANSACTION_URL + "?access_token=" + accessToken + "&agent_id=" + agent_id + "&file_size=" + file_size + "&chunk_numbers=" + chunk_numbers + "&upload_id=" +upload_id, UploadFileDTO.class);
    }

    protected HttpHeaders buildHttpHeaders() {
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.set("Content-Type", "application/json");
        return httpHeaders;
    }
}
