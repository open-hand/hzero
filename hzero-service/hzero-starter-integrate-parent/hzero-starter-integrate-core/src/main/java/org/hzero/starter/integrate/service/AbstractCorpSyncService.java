package org.hzero.starter.integrate.service;

import java.util.List;

import org.hzero.starter.integrate.dto.SyncCorpResultDTO;
import org.hzero.starter.integrate.dto.SyncDeptDTO;
import org.hzero.starter.integrate.dto.SyncUserDTO;
import org.hzero.starter.integrate.entity.CorpHrSync;

/**
 * 同步企业消息
 *
 * @author zifeng.ding@hand-china.com 2020/01/13 10:39
 */
public interface AbstractCorpSyncService {

    /**
     * 获取同步的第三方平台类型
     *
     * @return 返回第三方平台code
     */
    String corpSyncType();

    /**
     * 获取token
     *
     * @param corpHrSync 同步授权对象
     * @return token
     */
    String getAccessToken(CorpHrSync corpHrSync);

    /**
     * 同步企业组织架构
     *
     * @param syncDeptList       部门
     * @param syncUserList       用户
     * @param useGeneratedDeptId 是否使用自动生成的部门id
     * @param corpHrSync         三方平台信息
     * @return 同步结果对象
     */
    SyncCorpResultDTO syncCorp(List<SyncDeptDTO> syncDeptList, List<SyncUserDTO> syncUserList, Boolean useGeneratedDeptId, CorpHrSync corpHrSync);

    /**
     * 获取第三方平台部门信息
     *
     * @param deptId      部门ID
     * @param accessToken token
     * @return 部门列表
     */
    List<SyncDeptDTO> listDept(Long deptId, String accessToken);

    /**
     * 获取第三方平台部门下用户信息
     *
     * @param deptId      部门ID
     * @param accessToken token
     * @return 用户列表
     */
    List<SyncUserDTO> listUser(Long deptId, String accessToken);

}
