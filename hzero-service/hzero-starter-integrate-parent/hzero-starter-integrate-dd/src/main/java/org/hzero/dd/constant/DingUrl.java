package org.hzero.dd.constant;

/**
 * @Author J
 * @Date 2019/8/28
 */
public interface DingUrl {
    /**
     * token
     **/
    String GET_TOKEN_URL = "https://oapi.dingtalk.com/gettoken";



    /**
     * 创建用户
     **/
    String CREATE_USER_URL = "https://oapi.dingtalk.com/user/create";
    /**
     * 更新用户
     **/
    String UPDATE_USER_URL = "https://oapi.dingtalk.com/user/update";

    /**
     * 删除用户
     **/
    String DELETE_USERS_URL = "https://oapi.dingtalk.com/user/delete";

    /**
     * 获取用户详情
     */
    String GET_USER_INFO_URL = "https://oapi.dingtalk.com/user/get?access_token=";

    /**
     * 获取部门用户userid列表
     **/
    String GET_USER_URL = "https://oapi.dingtalk.com/user/getDeptMember";

    /**
     * 获取部门用户
     */
    String GET_DEPT_USER_URL = "https://oapi.dingtalk.com/user/simplelist?access_token=";

    /**
     * 获取部门用户详情
     **/
    String GET_DEPT_USER_INFO_URL = "https://oapi.dingtalk.com/user/listbypage?access_token=";

    /**
     * 获取管理员列表
     */
    String GET_ADMIN_LIST_URL = "https://oapi.dingtalk.com/user/get_admin?access_token=";

    /**
     * 获取管理员通讯录权限范围
     */
    String GET_ADMIN_SCOPE_URL = "https://oapi.dingtalk.com/topapi/user/get_admin_scope?access_token=";

    /**
     * 根据unionid获取userid
     */
    String GET_USERID_BYUNIONID_URL = "https://oapi.dingtalk.com/user/getUseridByUnionid?access_token=";

    /**
     * 根据手机号获取userid
     */
    String GET_USER_BY_MOBILE_URL = "https://oapi.dingtalk.com/user/get_by_mobile?access_token=";
    /**
     * 获取企业员工人数
     */
    String GET_ORG_USER_COUNT_URL = "https://oapi.dingtalk.com/user/get_org_user_count?access_token=";

    /**
     * 未登录钉钉的员工列表
     */
    String GET_USER_INACTIVE_URL = "https://oapi.dingtalk.com/topapi/inactive/user/get?access_token=";

    /**
     * 获取访问用户身份
     */
    String GET_USER_INFO_BY_CODE_URL = "https://oapi.dingtalk.com/user/getuserinfo?access_token=";




    /**
     * 创建部门
     **/
    String CREATE_DEPT_URL = "https://oapi.dingtalk.com/department/create";
    /**
     * 更新部门
     **/
    String UPDATE_DEPT_URL = "https://oapi.dingtalk.com/department/update";
    /**
     * 删除部门
     **/
    String DELETE_DEPT_URL = "https://oapi.dingtalk.com/department/delete";
    /**
     * 获取部门详情
     **/
    String GET_DEPT_URL = "https://oapi.dingtalk.com/department/get";
    /**
     * 获取子部门ID列表
     */
    String GEt_SUB_DEPT_URL = "https://oapi.dingtalk.com/department/list_ids";
    /**
     * 获取部门列表
     **/
    String GET_DEPT_LIST_URL = "https://oapi.dingtalk.com/department/list";

    /**
     * 查询部门的所有上级父部门路径
     */
    String LIST_PARENT_DEPTS_BY_DEPT_URL = "https://oapi.dingtalk.com/department/list_parent_depts_by_dept?access_token=";

    /**
     * 查询指定用户的所有上级父部门路径
     */
    String LIST_PARENT_DEPTS_BY_URL = "https://oapi.dingtalk.com/department/list_parent_depts?access_token=";







    /**
     * 发送工作通知消息
     **/
    String SEND_WORK_MESSAGE_URL = "https://oapi.dingtalk.com/topapi/message/corpconversation/asyncsend_v2";
    /**
     * 查询工作通知消息的发送进度
     **/
    String GET_WORK_PROGRESS_MESSAGE_URL = "https://oapi.dingtalk.com/topapi/message/corpconversation/getsendprogress";
    /**
     * 查询工作通知消息的发送结果
     **/
    String GET_WORK_RESULT_MESSAGE_URL = "https://oapi.dingtalk.com/topapi/message/corpconversation/getsendresult";
    /**
     * 工作通知消息撤回
     **/
    String WORK_MESSAGE_RECALL_URL = "https://oapi.dingtalk.com/topapi/message/corpconversation/recall";


    /**
     * 发送群消息
     **/
    String SEND_GROUP_MESSAGE_URL = "https://oapi.dingtalk.com/chat/send";
    /**
     * 查询群消息已读人员列表
     **/
    String GET_GROUP_MESSAGE_READ_LIST_URL = "https://oapi.dingtalk.com/chat/getReadList";
    /**
     * 创建会话
     **/
    String CREATE_CHAT_URL = "https://oapi.dingtalk.com/chat/create";
    /**
     * 修改会话
     **/
    String UPDATE_CHAT_URL = "https://oapi.dingtalk.com/chat/update";
    /**
     * 获取会话
     **/
    String GET_CHAT_URL = "https://oapi.dingtalk.com/chat/get";

    /**
     * 发送普通消息
     **/
    String SEND_MESSAGE_URL = "https://oapi.dingtalk.com/message/send_to_conversation";


    /**
     * 获取应用列表
     **/
    String GET_ALL_MICROAPP_LIST_URL = "https://oapi.dingtalk.com/microapp/list";
    /**
     * 获取员工可见的应用列表
     **/
    String GET_MICROAPP_LIST_URL = "https://oapi.dingtalk.com/microapp/list_by_userid";
    /**
     * 获取应用的可见范围
     **/
    String GET_MICROAPP_VISIBLE_URL = "https://oapi.dingtalk.com/microapp/visible_scopes";
    /**
     * 设置应用的可见范围
     **/
    String SET_MICROAPP_VISIBLE_URL = "https://oapi.dingtalk.com/microapp/set_visible_scopes";



    /**
     * 上传媒体文件
     **/
    String UPLOAD_MEDIA_URL = "https://oapi.dingtalk.com/media/upload";
    /**
     * 发送钉盘文件给指定用户
     **/
    String SEND_FILE_TO_USER_URL = "https://oapi.dingtalk.com/cspace/add_to_single_chat";
    /**
     * 新增文件到用户钉盘
     **/
    String ADD_FILE_URL = "https://oapi.dingtalk.com/cspace/add";
    /**
     * 获取企业下的自定义空间
     **/
    String GET_ENTERPRISES_SPACE_URL = "https://oapi.dingtalk.com/cspace/get_custom_space";

    /**
     * 获取应用自定义空间使用详情
     */
    String GET_ENTERPRISES_SPACE_INFO_URL = "https://oapi.dingtalk.com/cspace/uesd_info";
    /**
     * 授权用户访问企业自定义空间
     **/
    String GET_AUTHORIZED_USER_SPACE_URL = "https://oapi.dingtalk.com/cspace/grant_custom_space";
    /**
     * 单步上传文件
     **/
    String SINGLE_UPLOAD_FILE_URL = "https://oapi.dingtalk.com/file/upload/single";
    /**
     * 开启分块上传事务
     **/
    String OPEN_UPLOAD_FILE_TRANSACTION_URL = "https://oapi.dingtalk.com/file/upload/transaction";

    /**
     * 上传文件块
     **/
    String CHUNK_UPLOAD_FILE = "https://oapi.dingtalk.com/file/upload/chunk";

    /**
     * 提交文件上传事务
     */
    String UPLOAD_FILE_TRANSACTION_URL = "https://oapi.dingtalk.com/file/upload/transaction";


    /**
     * 获取jsapi_ticket
     */
    String GET_JSAPI_TICKET_URL = "https://oapi.dingtalk.com/get_jsapi_ticket";

}

