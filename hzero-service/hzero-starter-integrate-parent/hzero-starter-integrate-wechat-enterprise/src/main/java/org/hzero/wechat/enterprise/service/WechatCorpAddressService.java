package org.hzero.wechat.enterprise.service;

import org.hzero.wechat.enterprise.dto.*;

import java.util.List;

/**
 * 企业微信 通讯录管理api
 *
 * @Author J
 * @Date 2019/10/21
 */
public interface WechatCorpAddressService {


    /**
     * 创建成员
     * @param userDTO
     * @param accessToken
     * @return
     */
    DefaultResultDTO createUser(WechatSyncUserDTO userDTO, String accessToken);


    /**
     * 读取成员
     * @param userid
     * @param accessToken
     * @return
     */
    GetUserDTO getUserById(String userid, String accessToken);


    /**
     * 更新成员
     * @param userDTO
     * @param accessToken
     * @return
     */
    DefaultResultDTO updateUser(WechatSyncUserDTO userDTO, String accessToken);


    /**
     * 删除成员
     * @param userid
     * @param accessToken
     * @return
     */
    DefaultResultDTO deleteUserById(String userid, String accessToken);


    /**
     * 批量删除成员
     * @param userids
     * @param accessToken
     * @return
     */
    DefaultResultDTO batchDeleteUser(List<String> userids, String accessToken);


    /**
     * 获取部门成员
     * @param deptid 企业微信部门id
     * @param accessToken
     * @return
     */
    DeptUserDTO getUsersByDeptId(Long deptid, String accessToken,int fetch_child);


    /**
     * 获取部门成员详情
     * @param accessToken
     * @param department_id
     * @param fetch_child
     * @return
     */
    GetUserInfoByDeptIdResultDTO  getUserInfoByDeptId( String accessToken, Long department_id, int fetch_child);

    /**
     *  userid转openid
     * @param accessToken
     * @param userid
     * @return
     */
    ConvertToOpenidResultDTO convertToOpenid(String accessToken, String userid);

    /**
     * userid转openid
     * @param accessToken
     * @param openid
     * @return
     */
    ConvertToUseridResultDTO convertToUserid(String accessToken, String openid);

    /**
     * 二次验证
     * @param accessToken
     * @param userid
     * @return
     */
    DefaultResultDTO secondAuth(String accessToken,  String userid);

    /**
     * 邀请成员
     * @param accessToken
     * @param inviteUserDTO
     * @return
     */
    InviteUserResultDTO inviteUser(String accessToken, InviteUserDTO inviteUserDTO);

    /**
     * 获取加入企业二维码
     * @param accessToken
     * @param size_type
     * @return
     */
    GetJoinQrcodeResultDTO getJoinQrcode(String accessToken, String size_type);

    /**
     * 根据code获取访问用户身份
     * @param accessToken
     * @param code
     * @return
     */

    GetUserInfoByCodeResultDTO getUserInfoByCode(String accessToken, String code );




    /**
     * 创建部门
     * @param deptDTO
     * @param accessToken
     * @return
     */
    CreateDeptResultDTO createDept(WechatSyncDeptDTO deptDTO, String accessToken);


    /**
     * 更新部门
     * @param deptDTO
     * @param accessToken
     * @return
     */
    DefaultResultDTO updateDept(WechatSyncDeptDTO deptDTO, String accessToken);


    /**
     * 删除部门
     * @param deptId
     * @param accessToken
     * @return
     */
    DefaultResultDTO deleteDeptById(Long deptId, String accessToken);


    /**
     * 获取部门列表
     * @param deptId
     * @param accessToken
     * @return
     */
    DeptListDTO getDeptList(Long deptId, String accessToken);


    /**
     * 创建标签
     * @param accessToken
     * @param createTagDTO
     * @return
     */
    CreateTagResultDTO createTag(String accessToken, CreateTagDTO createTagDTO);

    /**
     * 更新标签名字
     * @param accessToken
     * @param updateTagNameDTO
     * @return
     */
    DefaultResultDTO updateTagName(String accessToken, UpdateTagNameDTO updateTagNameDTO);

    /**
     * 删除标签
     * @param accessToken
     * @param tagid
     * @return
     */
    DefaultResultDTO deleteTag(String accessToken, String tagid);

    /**
     * 获取标签成员
     * @param accessToken
     * @param tagid
     * @return
     */
    GetTagUserResultDTO getTagUser(String accessToken, String tagid);

    /**
     * 增加标签成员
     * @param accessToken
     * @param tagUserDTO
     * @return
     */
    TagUserResultDTO addTagUser(String accessToken, TagUserDTO tagUserDTO);

    /**
     * 删除标签成员
     * @param accessToken
     * @param tagUserDTO
     * @return
     */
    TagUserResultDTO deleteTagUser(String accessToken, TagUserDTO tagUserDTO);

    /**
     * 获取标签列表
     * @param accessToken
     * @return
     */
    GetTagUserListResultDTO getTagUserList(String accessToken);

}
