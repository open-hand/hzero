package org.hzero.wechat.enterprise.constant;

/**
 * @Author J
 * @Date 2019/8/28
 */
public interface WechatEnterpriseUrl {
    /**
     * token
     **/
    String GET_TOKEN_URL = "https://qyapi.weixin.qq.com/cgi-bin/gettoken";


    /**
     * 获取部门
     **/
    String GET_DEPT_URL = "https://qyapi.weixin.qq.com/cgi-bin/department/list";
    /**
     * 创建部门
     **/
    String CREATE_DEPT_URL = "https://qyapi.weixin.qq.com/cgi-bin/department/create";
    /**
     * 更新部门
     **/
    String UPDATE_DEPT_URL = "https://qyapi.weixin.qq.com/cgi-bin/department/update";
    /**
     * 删除部门
     **/
    String DELETE_DEPT_URL = "https://qyapi.weixin.qq.com/cgi-bin/department/delete";
    /**
     * 获取部门列表
     */
    String GET_DEPT_LIST="https://qyapi.weixin.qq.com/cgi-bin/department/list";




    /**
     * 新建成员
     **/
    String CREATE_USER_URL = "https://qyapi.weixin.qq.com/cgi-bin/user/create";

    /**
     * 读取成员
     */
    String GET_USER_BY_ID_URL="https://qyapi.weixin.qq.com/cgi-bin/user/get";
    /**
     * 更新用户
     **/
    String UPDATE_USER_URL = "https://qyapi.weixin.qq.com/cgi-bin/user/update";

    /**
     * 根据userid删除用户
     */
    String DELETE_USER_URL= "https://qyapi.weixin.qq.com/cgi-bin/user/delete";

    /**
     * 批量删除用户
     **/
    String DELETE_USERS_URL = "https://qyapi.weixin.qq.com/cgi-bin/user/batchdelete";

    /**
     * 获取部门成员
     **/
    String GET_USER_URL = "https://qyapi.weixin.qq.com/cgi-bin/user/simplelist";

    /**
     * 获取部门成员详情
     */
    String GET_USER_INFO_URL = "https://qyapi.weixin.qq.com/cgi-bin/user/list?access_token=";

    /**
     * userid转openid
     */
    String CONVERT_TO_OPENID_URL = " https://qyapi.weixin.qq.com/cgi-bin/user/convert_to_openid?access_token=";

    /**
     *openid转userid
     */
    String CONVERT_TO_USERID_URL = "https://qyapi.weixin.qq.com/cgi-bin/user/convert_to_userid?access_token=";

    /**
     * 二次验证
     */
    String SECOND_AUTH = "https://qyapi.weixin.qq.com/cgi-bin/user/authsucc?access_token=";

    /**
     * 邀请成员
     */
    String INVITE_USER_URL = "https://qyapi.weixin.qq.com/cgi-bin/batch/invite?access_token=";

    /**
     * 获取加入企业二维码
     */
    String GET_JOIN_QRCODE_URL = " https://qyapi.weixin.qq.com/cgi-bin/corp/get_join_qrcode?access_token=";



    /**
     * 创建标签
     */
    String TAG_CREATE_URL = "https://qyapi.weixin.qq.com/cgi-bin/tag/create?access_token=";

    /**
     * 更新标签名字
     */
    String UPDATE_TAG_URL = "https://qyapi.weixin.qq.com/cgi-bin/tag/update?access_token=";

    /**
     * 删除标签
     */
    String DELETE_TAG_URL = "https://qyapi.weixin.qq.com/cgi-bin/tag/delete?access_token=";

    /**
     * 获取标签成员
     */
    String GET_TAG_USER_URL = "https://qyapi.weixin.qq.com/cgi-bin/tag/get?access_token=";

    /**
     * 增加标签成员
     */
    String ADD_TAG_USERS_URL = "https://qyapi.weixin.qq.com/cgi-bin/tag/addtagusers?access_token=";

    /**
     *删除标签成员
     */
    String DELETE_TAG_USER_URL = "：https://qyapi.weixin.qq.com/cgi-bin/tag/deltagusers?access_token=";

    /**
     * 获取标签列表
     */
    String GET_TAG_LIST_URL = "https://qyapi.weixin.qq.com/cgi-bin/tag/list?access_token=";



    /**
     * 获取指定的应用详情
     **/
    String GET_AGENT_BY_ID_URL = "https://qyapi.weixin.qq.com/cgi-bin/agent/get?access_token=";
    /**
     * 获取access_token对应的应用列表
     **/
    String GET_ALL_AGENTS_URL = "https://qyapi.weixin.qq.com/cgi-bin/agent/list?access_token=";

    /**
     * 设置应用
     **/
    String SET_AGENT_URL = "https://qyapi.weixin.qq.com/cgi-bin/agent/set?access_token=";
    /**
     * 创建菜单
     **/
    String CREATE_MENU_URL = "https://qyapi.weixin.qq.com/cgi-bin/menu/create?access_token=";

    /**
     * 获取菜单列表
     **/
    String GET_MENU_LIST_URL = "https://qyapi.weixin.qq.com/cgi-bin/menu/create?access_token=";
    /**
     * 删除菜单
     **/
    String DELETE_MENU_URL = "https://qyapi.weixin.qq.com/cgi-bin/menu/create?access_token=";


    /**
     * 发送应用消息
     **/
    String SEND_MESSAGE_URL = "https://qyapi.weixin.qq.com/cgi-bin/message/send?access_token=";
    /**
     * 创建群聊会话
     **/
    String CREARE_APP_CHAT_URL = " https://qyapi.weixin.qq.com/cgi-bin/appchat/create?access_token=";
    /**
     * 修改群聊会话
     **/
    String UPDATE_APP_CHAT_URL = "https://qyapi.weixin.qq.com/cgi-bin/appchat/update?access_token=";
    /**
     * 获取群聊会话
     **/
    String GET_APP_CHAT_URL = "https://qyapi.weixin.qq.com/cgi-bin/appchat/get?access_token=";
    /**
     * 发送消息到群聊会话-应用推送消息
     **/
    String SEND_APP_CHAT_URL = "https://qyapi.weixin.qq.com/cgi-bin/appchat/send?access_token=";



    /**
     * 上传临时素材
     */
    String UPLOAD_MEDIA_URL = "https://qyapi.weixin.qq.com/cgi-bin/media/upload?access_token=";

    /**
     * 上传图片
     */
    String UPLOAD_IMAGE_URL = "https://qyapi.weixin.qq.com/cgi-bin/media/uploadimg?access_token=";

    /**
     * 获取临时素材
     */
    String GET_MEDIA_URL = "https://qyapi.weixin.qq.com/cgi-bin/media/get?access_token=";

    /**
     *获取高清语音素材
     */
    String GET_HD_MEDIA_URL = "https://qyapi.weixin.qq.com/cgi-bin/media/get/jssdk?access_token=";


    /**
     * 获取电子发票ticket
     * 获取应用的jsapi_ticket
     */
    String GET_TICKET_URL  = "https://qyapi.weixin.qq.com/cgi-bin/ticket/get?access_token=";

    /**
     * 获取企业的jsapi_ticket
     */
    String GET_JS_API_TICKET_URL = "https://qyapi.weixin.qq.com/cgi-bin/get_jsapi_ticket?access_token=";

    /**
     * 获取访问用户身份
     */
    String GET_USER_INFO_By_CODE_URL = "https://qyapi.weixin.qq.com/cgi-bin/user/getuserinfo?access_token=";


}
