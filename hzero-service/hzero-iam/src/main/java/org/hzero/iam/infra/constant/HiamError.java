package org.hzero.iam.infra.constant;

/**
 * description
 *
 * @author zhiying.dong@hand-china.com 2018/09/14 13:25
 */
public class HiamError {

    public static class ErrorCode{
        public static final String ACTIVE_USERS_REACHED = "hiam.error.active_users.reached";
        public static final String DOC_TYPE_DIMENSIONS_EMPTY = "hiam.error.doc_type_dimensions.notEmpty";
        public static final String DOC_TYPE_DIMENSIONS_COPY_FAILED = "hiam.error.doc_type_dimensions.copyFailed";
        public static final String COPY_ROLE_LIST_NOT_NULL = "hiam.error.data_permission.copyRole.notNull";
        public static final String MENU_ALREADY_ASSIGN_ROLE = "hiam.error.menuAlreadyAssignRole";
        public static final String ENABLE_MENU_NOT_DELETE = "hiam.error.enableMenuNotDelete";
    }

    /**
     * 标签已经存在
     */
    public static final String LABEL_EXIST = "hiam.error.labelExist";

    public static final String ROLE_CODE_EXISTS = "hiam.warn.role.codeExist";

}
