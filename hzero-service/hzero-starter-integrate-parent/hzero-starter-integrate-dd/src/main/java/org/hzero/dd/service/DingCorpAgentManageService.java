package org.hzero.dd.service;

import org.hzero.dd.dto.DefaultResultDTO;
import org.hzero.dd.dto.GetMicroappListDTO;
import org.hzero.dd.dto.GetMicroappVisibleDTO;
import org.hzero.dd.dto.SetMicroappVisibleDTO;

public interface DingCorpAgentManageService {


    /**
     * 获取应用列表
     * @param accessToken
     * @return
     */
    GetMicroappListDTO getAllMicroappList(String accessToken);

    /**
     * 获取员工可见的应用列表
     * @param accessToken
     * @param userid
     * @return
     */

    GetMicroappListDTO getMicroappListByUserId(String accessToken, String userid);

    /**
     * 获取应用的可见范围
     * @param accessToken
     * @param agentId
     * @return
     */

    GetMicroappVisibleDTO getMicroappVisible(String accessToken, Long agentId);

    /**
     * 设置应用的可见范围
     * @param accessToken
     * @param setMicroappVisibleDTO
     * @return
     */

    DefaultResultDTO   setMicroappVisible(String accessToken, SetMicroappVisibleDTO setMicroappVisibleDTO);
}
