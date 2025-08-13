package org.hzero.dd.service.impl;

import com.alibaba.fastjson.JSON;
import org.apache.commons.lang3.StringUtils;
import org.hzero.dd.constant.DingUrl;
import org.hzero.dd.dto.*;
import org.hzero.dd.service.DingCorpAddressService;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import javax.annotation.Resource;
@Service
public class DingCorpAddressServiceImpl implements DingCorpAddressService {

    @Resource(name = "wdRestTemplate")
    protected RestTemplate restTemplate;

    @Override
    public UserCreateResultDTO createUser( String accessToken, DingSyncUserDTO userDTO) {
        return  restTemplate.postForObject(DingUrl.CREATE_USER_URL+"?access_token=" + accessToken,new HttpEntity<>(JSON.toJSONString(userDTO), buildHttpHeaders()), UserCreateResultDTO.class);
    }

    @Override
    public DefaultResultDTO updateUser( String accessToken, DingSyncUserDTO userDTO) {
        return restTemplate.postForObject(DingUrl.UPDATE_USER_URL+"?access_token="+accessToken,   new HttpEntity<>(JSON.toJSONString(userDTO),  buildHttpHeaders()), DefaultResultDTO.class);
    }

    @Override
    public DefaultResultDTO deleteUserById(String accessToken,String userid ) {
        return restTemplate.getForObject(DingUrl.DELETE_USERS_URL + "?access_token=" + accessToken + "&userid=" + userid, DefaultResultDTO.class);
    }

    @Override
    public GetUserDTO getUserInfoByUserId(String accessToken, String userid,  String lang) {
        String url = DingUrl.GET_USER_INFO_URL + accessToken + "&userid=" + userid;
        if(!StringUtils.isBlank(lang)) {
            url = url +  "&lang=" + lang;
        }
        return restTemplate.getForObject(url, GetUserDTO.class);
    }

    @Override
    public GetUserListDTO getUserListByDeptId( String accessToken, String deptId) {
        return restTemplate.getForObject(DingUrl.GET_USER_URL + "?access_token=" + accessToken + "&deptId=" + deptId,  GetUserListDTO.class);
    }


    @Override
    public GetDeptUserInfoResultDTO getDeptUserInfo(String accessToken, String lang, Long department_id, Long offset, Long size, String order) {
        return restTemplate.getForObject(DingUrl.GET_DEPT_USER_INFO_URL  + accessToken +"&lang=" + lang + "&department_id=" + department_id + "&offset=" + offset + "&size=" + size + "&order=" + order, GetDeptUserInfoResultDTO.class);

    }

    @Override
    public GetDeptUserResultDTO getDeptUser(String accessToken, String lang, Long department_id, Long offset, Long size, String order) {
        return restTemplate.getForObject(DingUrl.GET_DEPT_USER_URL  + accessToken +"&lang=" + lang + "&department_id=" + department_id + "&offset=" + offset + "&size=" + size + "&order=" + order, GetDeptUserResultDTO.class);

    }

    @Override
    public GetDeptUserResultDTO getDeptUser(String accessToken, Long department_id) {
        return restTemplate.getForObject(DingUrl.GET_DEPT_USER_URL  + accessToken + "&department_id=" + department_id, GetDeptUserResultDTO.class);

    }

    @Override
    public GetAdminListResultDTO getAdminList(String accessToken) {
        return restTemplate.getForObject(DingUrl.GET_ADMIN_LIST_URL  + accessToken , GetAdminListResultDTO.class);

    }

    @Override
    public GetAdminScopeResultDTO getAdminScope(String accessToken, String userid) {
        return restTemplate.getForObject(DingUrl.GET_ADMIN_SCOPE_URL  + accessToken + "&userid=" + userid, GetAdminScopeResultDTO.class);

    }

    @Override
    public GetUseridByUnionidResultDTO getUseridByUnionid(String accessToken, String unionid) {
        return restTemplate.getForObject(DingUrl.GET_USERID_BYUNIONID_URL  + accessToken + "&unionid=" +unionid, GetUseridByUnionidResultDTO.class);
    }

    @Override
    public GetUseridByMobileResultDTO getUseridByMobile(String accessToken, String mobile) {
        return restTemplate.getForObject(DingUrl.GET_USER_BY_MOBILE_URL  + accessToken + "&mobile=" + mobile, GetUseridByMobileResultDTO.class);
    }

    @Override
    public GetOrgUserCountResultDTO getOrgUserCount(String accessToken, Long onlyActive) {
        return restTemplate.getForObject(DingUrl.GET_ORG_USER_COUNT_URL  + accessToken + "&onlyActive=" + onlyActive, GetOrgUserCountResultDTO.class);
    }

    @Override
    public GetInactiveUserResultDTO getInactiveUser(String accessToken, GetInactiveUserDTO getInactiveUserDTO) {
        return restTemplate.postForObject(DingUrl.GET_USER_INACTIVE_URL+ accessToken,  new HttpEntity<>(getInactiveUserDTO, buildHttpHeaders()), GetInactiveUserResultDTO.class );
    }


    @Override
    public GetUserInfoByCodeResultDTO getUserInfoByCode(String accessToken, String code) {
        return restTemplate.getForObject(DingUrl.GET_USER_INFO_BY_CODE_URL  + accessToken + "&code=" + code, GetUserInfoByCodeResultDTO.class);

    }

    @Override
    public CreateDeptResultDTO createDept( String accessToken,DingSyncDeptDTO deptDTO) {
        return restTemplate.postForObject(DingUrl.CREATE_DEPT_URL+"?access_token=" + accessToken, new HttpEntity<>(deptDTO, buildHttpHeaders()), CreateDeptResultDTO.class);
    }

    @Override
    public UpdateDeptResultDTO updateDept(String accessToken, DingSyncDeptDTO deptDTO ) {
        return restTemplate.postForObject(DingUrl.UPDATE_DEPT_URL+"?access_token=" + accessToken,  new HttpEntity<>(deptDTO, buildHttpHeaders()), UpdateDeptResultDTO.class );
    }

    @Override
    public DefaultResultDTO deleteDeptById(String accessToken,String id ) {
        return restTemplate.getForObject(DingUrl.DELETE_DEPT_URL + "?access_token=" + accessToken + "&id=" + id, DefaultResultDTO.class);
    }

    @Override
    public DeptListDTO getDeptList(Long id, String accessToken,String lang, String fetch_child) {
        return restTemplate.getForObject(DingUrl.GET_DEPT_LIST_URL + "?access_token=" + accessToken + "&id=" + id+ "&lang=" + lang +"&fetch_child=" + fetch_child, DeptListDTO.class);
    }

    @Override
    public DeptListDTO getDeptList(Long deptId, String accessToken) {
        return restTemplate.getForObject(DingUrl.GET_DEPT_LIST_URL + "?access_token=" + accessToken, DeptListDTO.class);
    }

    @Override
    public GetDeptDTO getDeptById(String accessToken, String id,  String lang) {
        return restTemplate.getForObject(DingUrl.GET_DEPT_URL + "?access_token=" + accessToken + "&id=" + id + "&lang="+lang, GetDeptDTO.class);
    }

    @Override
    public GetDeptDTO getDeptById(String accessToken, String id) {
        return restTemplate.getForObject(DingUrl.GET_DEPT_URL + "?access_token=" + accessToken + "&id=" + id, GetDeptDTO.class);
    }

    @Override
    public GetSubDeptDTO getSubDeptbyId(String accessToken,String id) {
        return restTemplate.getForObject(DingUrl.GEt_SUB_DEPT_URL + "?access_token=" + accessToken + "&id=" + id, GetSubDeptDTO.class);
    }


    @Override
    public GetListParentDeptsByDeptIdResultDTO getListParentDeptsByDeptId(String accessToken, String id) {
        return restTemplate.getForObject(DingUrl.LIST_PARENT_DEPTS_BY_DEPT_URL  + accessToken + "&id=" + id, GetListParentDeptsByDeptIdResultDTO.class);

    }

    @Override
    public GetListParentDeptsByUserId getListParentDeptsByUserId(String accessToken, String userId) {
        return restTemplate.getForObject(DingUrl.LIST_PARENT_DEPTS_BY_URL  + accessToken + "&userId=" + userId, GetListParentDeptsByUserId.class);
    }

    protected HttpHeaders buildHttpHeaders() {
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.set("Content-Type", "application/json");
        return httpHeaders;
    }
}
