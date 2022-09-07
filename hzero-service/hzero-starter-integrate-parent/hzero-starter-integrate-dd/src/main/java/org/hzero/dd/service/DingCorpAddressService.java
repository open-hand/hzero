package org.hzero.dd.service;

import org.hzero.dd.dto.*;

/**
 * 钉钉 通讯录管理api
 *
 * @Author J
 * @Date 2019/10/21
 */
public interface DingCorpAddressService {


    /**
     * 创建用户
     * @param userDTO
     * @param accessToken
     * @return
     */
    UserCreateResultDTO createUser( String accessToken,DingSyncUserDTO userDTO);

    /**
     * 更新用户
     * @param userDTO
     * @param accessToken
     * @return
     */
    DefaultResultDTO updateUser( String accessToken,DingSyncUserDTO userDTO);

    /**
     * 删除用户
     * @param userid
     * @param accessToken
     * @return
     */
    DefaultResultDTO deleteUserById( String accessToken, String userid);


    /**
     * 获取用户详情
     * @param userid
     * @param accessToken
     * @return
     */
    GetUserDTO getUserInfoByUserId(String accessToken, String userid,  String lang);

    /**
     * 通过部门ID获取用户userid列表
     * @param deptId
     * @param accessToken
     * @return
     */
    GetUserListDTO getUserListByDeptId( String accessToken, String deptId);

    /**
     *  获取部门用户详情
     * @param  accessToken
     * @param lang
     * @param department_id
     * @param offset
     * @param size
     * @param order
     * @return
     */
    GetDeptUserInfoResultDTO getDeptUserInfo(String  accessToken, String lang, Long department_id, Long offset, Long size, String order);

    /**
     * 获取部门用户
     * @param  accessToken
     * @param lang
     * @param department_id
     * @param offset
     * @param size
     * @param order
     * @return
     */
    GetDeptUserResultDTO getDeptUser(String  accessToken, String lang, Long department_id, Long offset, Long size, String order);

    /**
     * 获取部门用户
     * @param  accessToken
     * @param department_id
     * @return
     */
    GetDeptUserResultDTO getDeptUser(String  accessToken,  Long department_id);

    /**
     * 获取管理员列表
     * @param  accessToken
     * @return
     */
    GetAdminListResultDTO getAdminList(String  accessToken);

    /**
     * 获取管理员通讯录权限范围
     * @param accessToken
     * @param userid
     * @return
     */
    GetAdminScopeResultDTO getAdminScope(String accessToken, String userid);

    /**
     * 根据unionid获取userid
     * @param accessToken
     * @param unionid
     * @return
     */
    GetUseridByUnionidResultDTO getUseridByUnionid(String accessToken, String unionid);

    /**
     * 根据手机号获取userid
     * @param  accessToken
     * @param mobile
     * @return
     */
    GetUseridByMobileResultDTO getUseridByMobile(String  accessToken, String mobile);

    /**
     * 获取企业员工人数
     * @param  accessToken
     * @param onlyActive
     * @return
     */
    GetOrgUserCountResultDTO getOrgUserCount(String  accessToken, Long onlyActive);

    /**
     * 未登录钉钉的员工列表
     * @param  accessToken
     * @param getInactiveUserDTO
     * @return
     */
    GetInactiveUserResultDTO getInactiveUser(String  accessToken, GetInactiveUserDTO getInactiveUserDTO);

    /**
     *
     * @param accessToken
     * @param code
     * @return
     */
    GetUserInfoByCodeResultDTO  getUserInfoByCode(String accessToken, String code);




    /**
     * 创建部门
     * @param deptDTO
     * @param accessToken
     * @return
     */
    CreateDeptResultDTO createDept( String accessToken,DingSyncDeptDTO deptDTO);


    /**
     * 更新部门
     * @param deptDTO
     * @param accessToken
     * @return
     */
   UpdateDeptResultDTO updateDept( String accessToken,DingSyncDeptDTO deptDTO);


    /**
     * 删除部门
     * @param id
     * @param accessToken
     * @return
     */

    DefaultResultDTO deleteDeptById(String accessToken, String id);


    /**
     * 获取部门列表
     * @param deptId
     * @param accessToken
     * @return
     */
    DeptListDTO getDeptList(Long deptId, String accessToken, String lang, String fetch_child);

    /**
     * 获取部门列表
     * @param deptId
     * @param accessToken
     * @return
     */
    DeptListDTO getDeptList(Long deptId, String accessToken);


    /**
     * 获取部门详情
     * @param id
     * @param accessToken
     * @return
     */
    GetDeptDTO getDeptById(String accessToken,String id,  String lang);

    /**
     * 获取部门详情
     * @param id
     * @param accessToken
     * @return
     */
    GetDeptDTO getDeptById(String accessToken, String id);


    /**
     * 获取子部门ID列表
     * @param id
     * @param accessToken
     * @return
     */

    GetSubDeptDTO getSubDeptbyId(String accessToken,String id );

    /**
     * 查询部门的所有上级父部门路径
     * @param accessToken
     * @param id
     * @return
     */
    GetListParentDeptsByDeptIdResultDTO getListParentDeptsByDeptId(String accessToken, String id);

    /**
     * 查询指定用户的所有上级父部门路径
     * @param accessToken
     * @param userId
     * @return
     */
    GetListParentDeptsByUserId getListParentDeptsByUserId(String accessToken, String userId);









}
