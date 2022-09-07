package org.hzero.wechat.constant;

/**
 * @Author J
 * @Date 2019/8/28
 */
public interface WechatApi {
    /** token **/
    String GET_TOKEN_URL = "https://api.weixin.qq.com/cgi-bin/token?grant_type=client_credential";

    /** 获得模板ID **/
    String GET_TEMPLATE = "https://api.weixin.qq.com/cgi-bin/template/api_add_template?access_token=";

    /** 获取模板列表 **/
    String GET_ALL_TEMPLATE = "https://api.weixin.qq.com/cgi-bin/template/get_all_private_template?access_token=";

    /** 删除模板 **/
    String DEL_TEMPLATE = "https://api.weixin.qq.com/cgi-bin/template/del_private_template?access_token=";

    /** 发送模板消息 **/
    String SEND_TEMPLATE = "https://api.weixin.qq.com/cgi-bin/message/template/send?access_token=";


    /**
     * 创建菜单
     */
    String CREATE_MENU_URL = " https://api.weixin.qq.com/cgi-bin/menu/create?access_token=";
    /**
     * 查询菜单
     */
    String GET_MENU = "https://api.weixin.qq.com/cgi-bin/get_current_selfmenu_info?access_token=";
    /**
     * 删除菜单
     */
    String DELETE_MENU = "https://api.weixin.qq.com/cgi-bin/menu/delete?access_token=";

    /**
     * 创建个性化菜单
     */
    String CREATE_ADDCONDITIONAL_MENU_URL = "https://api.weixin.qq.com/cgi-bin/menu/addconditional?access_token=";

    /**
     * 删除个性化菜单接口
     */
    String DELETE_ADDCONDITIONAL_MENU_URL = "https://api.weixin.qq.com/cgi-bin/menu/delconditional?access_token=";

    /**
     * 测试个性化菜单匹配结果
     */
    String TEST_MATCH_MENU_URL = "https://api.weixin.qq.com/cgi-bin/menu/trymatch?access_token=";

    /**
     * 获取自定义菜单配置
     */
    String GET_MENU_CONFIGURATION = "https://api.weixin.qq.com/cgi-bin/menu/get?access_token=";





    /**
     * 新增临时素材
     */
    String UPLOAD_MEDIA_URL = "https://api.weixin.qq.com/cgi-bin/media/upload?access_token=";

    /**
     * 获取临时素材
     */
   String GET_MEDIA_URL = "https://api.weixin.qq.com/cgi-bin/media/get?access_token=";

    /**
     * 高清语音素材获取接口
     */
    String GET_HD_VOICE_URL = "https://api.weixin.qq.com/cgi-bin/media/get/jssdk?";

    /**
     * 新增永久图文素材
     */
    String ADD_MEDIA_URL = "https://api.weixin.qq.com/cgi-bin/material/add_news?access_token=";

    /**
     * 上传图文消息内的图片获取URL
     */
    String UPLOAD_IMAGE_MEDIA_URL =  "https://api.weixin.qq.com/cgi-bin/media/uploadimg?access_token=";

    /**
     * 新增其他类型永久素材
     */
    String ADD_MATERIAL_URL = "https://api.weixin.qq.com/cgi-bin/material/add_material?access_token=";


    /**
     * 获取永久素材
     */
    String GET_MATERIAL_URL = "https://api.weixin.qq.com/cgi-bin/material/get_material?access_token=";

    /**
     * 删除永久素材
     */
    String DELETE_MATERIAL_URL = "https://api.weixin.qq.com/cgi-bin/material/del_material?access_token=";


    /**
     * 修改永久图文素材
     */
    String UPDATE_MATERIAL_URL = "https://api.weixin.qq.com/cgi-bin/material/update_news?access_token=";

    /**
     * 获取素材总数
     */
    String GET_MATERIAL_COUNT_URL = "https://api.weixin.qq.com/cgi-bin/material/get_materialcount?access_token=";

    /**
     * 获取素材列表
     */
    String GET_MATERIAL_LIST_URL = "https://api.weixin.qq.com/cgi-bin/material/batchget_material?access_token=";




    /**
     *添加客服帐号
     */
    String ADD_KF_ACCOUNT_URL = "https://api.weixin.qq.com/customservice/kfaccount/add?access_token=";
    /**
     * 修改客服帐号
     */
    String UPDATE_KF_ACCOUNT_URL = "https://api.weixin.qq.com/customservice/kfaccount/update?access_token=";

    /**
     * 删除客服帐号
     */
    String DELETE_KF_ACCOUNT_URL = "https://api.weixin.qq.com/customservice/kfaccount/del?access_token=";

    /**
     * 设置客服帐号的头像
     */
    String SET_KF_ACCOUNT_HEAD_IMAGE_URL = "http://api.weixin.qq.com/customservice/kfaccount/uploadheadimg?access_token=";

    /**
     * 获取所有客服账号
     */
    String GET_ALL_ACCOUNT_URL = "https://api.weixin.qq.com/cgi-bin/customservice/getkflist?access_token=";
    /**
     * 客服接口-发消息
     */
    String SEND_CUSTOM_MESSAGE_URL = "https://api.weixin.qq.com/cgi-bin/message/custom/send?access_token=";

    /**
     * 客服输入状态
     */
    String CUSTOM_MESSAGE_STATUS_URL = " POST https://api.weixin.qq.com/cgi-bin/message/custom/typing?access_token=";


     /**
     *上传图文消息内的图片获取URL【订阅号与服务号认证后均可用】
     */
    String UPLOADING_MEDIA_URL = "https://api.weixin.qq.com/cgi-bin/media/uploadimg?access_token=";

    /**
     * 上传图文消息素材【订阅号与服务号认证后均可用】
     */
    String UPLOAD_MATERIAL_URL = "https://api.weixin.qq.com/cgi-bin/media/uploadnews?access_token=";

    /**
     * 根据标签进行群发【订阅号与服务号认证后均可用】
     */
    String SEND_MASS_ALL_MESSAGE_URL = "https://api.weixin.qq.com/cgi-bin/message/mass/sendall?access_token=";

    /**
     * 根据OpenID列表群发【订阅号不可用，服务号认证后可用】
     */
    String SEND_MASS_MESSAGE_URL = "https://api.weixin.qq.com/cgi-bin/message/mass/send?access_token=";

    /**
     * 删除群发【订阅号与服务号认证后均可用】
     */
    String DELETE_MASS_MESSAGE_URL = "https://api.weixin.qq.com/cgi-bin/message/mass/delete?access_token=";

    /**
     * 预览接口【订阅号与服务号认证后均可用】
     */
    String PREVIEW_MASS_MESSAGE_URL = "https://api.weixin.qq.com/cgi-bin/message/mass/preview?access_token=";

    /**
     *查询群发消息发送状态【订阅号与服务号认证后均可用】
     */
    String GET_MASS_MESSAGE_STATUS_URL = "https://api.weixin.qq.com/cgi-bin/message/mass/get?access_token=";

    /**
     *控制群发速度
     */
    String GET_MASS_MESSAGE_SPEED_URL = "https://api.weixin.qq.com/cgi-bin/message/mass/speed/get?access_token=";

    /**
     *设置群发速度
     */
    String set_mass_message_speed_url = "https://api.weixin.qq.com/cgi-bin/message/mass/speed/set?access_token=";

    /**
     * 通过API推送订阅模板消息给到授权微信用户
     */
    String   SUBSCRIBE_TEMPLATE_MESSAGE_URL = "https://api.weixin.qq.com/cgi-bin/message/template/subscribe?access_token=";

    /**
     * 公众号调用或第三方平台帮公众号调用对公众号的所有api调用（包括第三方帮其调用）次数进行清零
     */
    String  CLEAR_QUOTA_URL = "https://api.weixin.qq.com/cgi-bin/clear_quota?access_token=";
    /**
     *接口调用请求说明
     */
    String GET_CURRENT_AUTO_REPLY_INFO_URL = "https://api.weixin.qq.com/cgi-bin/get_current_autoreply_info?access_token=";

    /**
     * 获取jsApiTicket
     */
    String GET_JS_API_TICKET_URL = "https://api.weixin.qq.com/cgi-bin/get_current_autoreply_info?access_token=";

}
