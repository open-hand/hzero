package org.hzero.wechat.service;
import org.hzero.wechat.dto.*;

public interface WeChatMenuService {
    /**
     *创建接口
     * @param accessToken
     * @param createMenuDTO
     * @return
     */
    DefaultResultDTO createMenu(String accessToken, CreateMenuDTO createMenuDTO);

    /**
     *  查询接口
     * @param accessToken
     * @return
     */
    GetMenuResultDTO getMenu(String accessToken);

    /**
     * 删除接口
     * @param accessToken
     * @return
     */
    DefaultResultDTO deleteMenu(String accessToken);

    /**
     * 创建个性化菜单
     * @param accessToken
     * @param  createAddConditionalMenuDTO
     * @return
     */
     CreateAddConditionalMenuResultDTO createAddConditionalMenu(String accessToken,  CreateAddConditionalMenuDTO createAddConditionalMenuDTO);

    /**
     * 删除个性化菜单
     * @param accessToken
     * @param menuid
     * @return
     */
    DefaultResultDTO deleteAddConditionalMenu(String  accessToken, String menuid);

    /**
     * 测试个性化菜单匹配结果
     * @param accessToken
     * @param user_id
     * @return
     */
    TestMatchMenuResultDTO testMatchMenu(String accessToken, String user_id );

    /**
     * 获取自定义菜单配置
     * @param accessToken
     * @return
     */
    GetMenuConfigurationResultDTO getMenuConfiguration(String accessToken);

}
