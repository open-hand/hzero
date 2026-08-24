package org.hzero.wechat.enterprise.service;

import org.hzero.wechat.enterprise.dto.*;

/**
 * 企业微信 应用管理API
 *
 * @Author J
 * @Date 2019/10/21
 */
public interface WechatCorpAgentManageService {


    /**
     * 获取指定的应用详情
     *
     * @param agentId 应用id
     * @return
     */
    AgentDTO getAgentByID(String agentId,String accessToken);


    /**
     * 获取access_token对应的应用列表
     *
     * @return
     */
    AgentListDTO getAgentList(String accessToken);


    /**
     * 设置应用
     * @param setAgentDTO
     * @return
     */
    DefaultResultDTO setAgent(SetAgentDTO setAgentDTO,String accessToken);

    /**
     * 创建菜单
     * @param accessToken
     * @param agentid
     * @param menuDTO
     * @return
     */
    DefaultResultDTO createMenu(String accessToken, String agentid, MenuDTO  menuDTO);

    /**
     * 获取菜单
     * @param accessToken
     * @param agentid
     * @return
     */
    MenuDTO getMenu(String accessToken, String agentid);

    /**
     * 删除菜单
     * @param accessToken
     * @param agentid
     * @return
     */
    DefaultResultDTO deleteMenu(String accessToken, String agentid );
}
