package org.hzero.wechat.enterprise.service.impl;

import com.alibaba.fastjson.JSON;
import org.hzero.wechat.enterprise.constant.WechatEnterpriseUrl;
import org.hzero.wechat.enterprise.dto.*;
import org.hzero.wechat.enterprise.service.WechatCorpAddressService;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.web.client.RestTemplate;

import javax.annotation.Resource;
import java.util.List;

/**
 * @Author J
 * @Date 2019/10/22
 */
public class WechatCorpAddressServiceImpl implements WechatCorpAddressService {

    @Resource(name = "wdRestTemplate")
    protected RestTemplate restTemplate;


    @Override
    public DefaultResultDTO createUser(WechatSyncUserDTO userDTO, String accessToken) {
        return restTemplate.postForObject(WechatEnterpriseUrl.CREATE_USER_URL + "?access_token=" + accessToken, new HttpEntity<>(JSON.toJSONString(userDTO), buildHttpHeaders()), DefaultResultDTO.class);
    }

    @Override
    public GetUserDTO getUserById(String userid, String accessToken) {
        return restTemplate.getForObject(WechatEnterpriseUrl.GET_USER_BY_ID_URL + "?access_token=" + accessToken + "&userid=" + userid, GetUserDTO.class);
    }


    @Override
    public DefaultResultDTO updateUser(WechatSyncUserDTO userDTO, String accessToken) {
        return restTemplate.postForObject(WechatEnterpriseUrl.UPDATE_USER_URL + "?access_token=" + accessToken, new HttpEntity<>(JSON.toJSONString(userDTO), buildHttpHeaders()), DefaultResultDTO.class);
    }

    @Override
    public DefaultResultDTO deleteUserById(String userid, String accessToken) {
        return restTemplate.getForObject(WechatEnterpriseUrl.DELETE_USER_URL + "?access_token=" + accessToken + "&userid=" + userid, DefaultResultDTO.class);
    }

    @Override
    public DefaultResultDTO batchDeleteUser(List<String> userids, String accessToken) {
        return restTemplate.postForObject(WechatEnterpriseUrl.DELETE_USERS_URL + "?access_token=" + accessToken, new HttpEntity<>("{ \"useridlist\": " + JSON.toJSONString(userids) + " }", buildHttpHeaders()), DefaultResultDTO.class);
    }

    @Override
    public DeptUserDTO getUsersByDeptId(Long deptid, String accessToken, int fetch_child) {
        return restTemplate.getForObject(WechatEnterpriseUrl.GET_USER_URL + "?access_token=" + accessToken + "&department_id=" + deptid + "&fetch_child=" + fetch_child, DeptUserDTO.class);
    }

    @Override
    public GetUserInfoByDeptIdResultDTO getUserInfoByDeptId(String accessToken, Long department_id, int fetch_child) {
        return restTemplate.getForObject(WechatEnterpriseUrl.GET_USER_INFO_URL + accessToken + "&department_id=" + department_id + "&fetch_child=" + fetch_child, GetUserInfoByDeptIdResultDTO.class);

    }

    @Override
    public ConvertToOpenidResultDTO convertToOpenid(String accessToken, String userid) {
        return restTemplate.postForObject(WechatEnterpriseUrl.CONVERT_TO_OPENID_URL + accessToken, new HttpEntity<>("{\"userid\"：" + JSON.toJSONString(userid) + "}", buildHttpHeaders()), ConvertToOpenidResultDTO.class);
    }

    @Override
    public ConvertToUseridResultDTO convertToUserid(String accessToken, String openid) {
        return restTemplate.postForObject(WechatEnterpriseUrl.CONVERT_TO_USERID_URL + accessToken, new HttpEntity<>("{\"openid\"：" + JSON.toJSONString(openid) + "}", buildHttpHeaders()), ConvertToUseridResultDTO.class);
    }

    @Override
    public DefaultResultDTO secondAuth(String accessToken, String userid) {
        return restTemplate.getForObject(WechatEnterpriseUrl.SECOND_AUTH + accessToken + "&userid=" + userid, ConvertToUseridResultDTO.class);
    }

    @Override
    public InviteUserResultDTO inviteUser(String accessToken, InviteUserDTO inviteUserDTO) {
        return restTemplate.postForObject(WechatEnterpriseUrl.INVITE_USER_URL + accessToken, new HttpEntity<>(JSON.toJSONString(inviteUserDTO), buildHttpHeaders()), InviteUserResultDTO.class);
    }

    @Override
    public GetJoinQrcodeResultDTO getJoinQrcode(String accessToken, String size_type) {
        return restTemplate.getForObject(WechatEnterpriseUrl.GET_JOIN_QRCODE_URL + accessToken + "&size_type=" + size_type, GetJoinQrcodeResultDTO.class);
    }

    @Override
    public GetUserInfoByCodeResultDTO getUserInfoByCode(String accessToken, String code) {
        return restTemplate.getForObject(WechatEnterpriseUrl.GET_USER_INFO_By_CODE_URL + accessToken + "&code=" + code, GetUserInfoByCodeResultDTO.class);

    }

    @Override
    public CreateDeptResultDTO createDept(WechatSyncDeptDTO deptDTO, String accessToken) {
        return restTemplate.postForObject(WechatEnterpriseUrl.CREATE_DEPT_URL + "?access_token=" + accessToken, new HttpEntity<>(deptDTO, buildHttpHeaders()), CreateDeptResultDTO.class);
    }

    @Override
    public DefaultResultDTO updateDept(WechatSyncDeptDTO deptDTO, String accessToken) {
        return restTemplate.postForObject(WechatEnterpriseUrl.UPDATE_DEPT_URL + "?access_token=" + accessToken, new HttpEntity<>(deptDTO, buildHttpHeaders()), DefaultResultDTO.class);
    }

    @Override
    public DefaultResultDTO deleteDeptById(Long deptId, String accessToken) {
        return restTemplate.getForObject(WechatEnterpriseUrl.DELETE_DEPT_URL + "?access_token=" + accessToken + "&id=" + deptId, DefaultResultDTO.class);
    }

    @Override
    public DeptListDTO getDeptList(Long deptId, String accessToken) {
        return restTemplate.getForObject(WechatEnterpriseUrl.GET_DEPT_LIST + "?access_token=" + accessToken + "&id=" + deptId, DeptListDTO.class);

    }


    @Override
    public CreateTagResultDTO createTag(String accessToken, CreateTagDTO createTagDTO) {
        return restTemplate.postForObject(WechatEnterpriseUrl.TAG_CREATE_URL + accessToken, new HttpEntity<>(JSON.toJSONString(createTagDTO), buildHttpHeaders()), CreateTagResultDTO.class);
    }

    @Override
    public DefaultResultDTO updateTagName(String accessToken, UpdateTagNameDTO updateTagNameDTO) {
        return restTemplate.postForObject(WechatEnterpriseUrl.UPDATE_TAG_URL + accessToken, new HttpEntity<>(JSON.toJSONString(updateTagNameDTO), buildHttpHeaders()), DefaultResultDTO.class);
    }

    @Override
    public DefaultResultDTO deleteTag(String accessToken, String tagid) {
        return restTemplate.getForObject(WechatEnterpriseUrl.DELETE_TAG_URL + accessToken + "&tagid=" + tagid, DefaultResultDTO.class);
    }


    @Override
    public GetTagUserResultDTO getTagUser(String accessToken, String tagid) {
        return restTemplate.getForObject(WechatEnterpriseUrl.GET_TAG_USER_URL + accessToken + "&tagid=" + tagid, GetTagUserResultDTO.class);
    }

    @Override
    public TagUserResultDTO addTagUser(String accessToken, TagUserDTO tagUserDTO) {
        return restTemplate.postForObject(WechatEnterpriseUrl.ADD_TAG_USERS_URL + accessToken, new HttpEntity<>(JSON.toJSONString(tagUserDTO), buildHttpHeaders()), TagUserResultDTO.class);
    }

    @Override
    public TagUserResultDTO deleteTagUser(String accessToken, TagUserDTO tagUserDTO) {
        return restTemplate.postForObject(WechatEnterpriseUrl.DELETE_TAG_USER_URL + accessToken, new HttpEntity<>(JSON.toJSONString(tagUserDTO), buildHttpHeaders()), TagUserResultDTO.class);
    }

    @Override
    public GetTagUserListResultDTO getTagUserList(String accessToken) {
        return restTemplate.getForObject(WechatEnterpriseUrl.GET_TAG_LIST_URL + accessToken, GetTagUserListResultDTO.class);
    }


    protected HttpHeaders buildHttpHeaders() {
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.set("Content-Type", "application/json");
        return httpHeaders;
    }
}
