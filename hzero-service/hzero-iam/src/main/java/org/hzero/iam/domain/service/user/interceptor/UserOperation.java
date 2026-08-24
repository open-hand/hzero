package org.hzero.iam.domain.service.user.interceptor;

import javax.annotation.Nonnull;

import org.hzero.core.interceptor.ChainId;

/**
 * User operations
 *
 * @author bojiangzhou 2020/05/28
 */
public enum  UserOperation implements ChainId {

    /**
     * 创建用户
     */
    CREATE_USER("CREATE_USER"),

    /**
     * 更新用户
     */
    UPDATE_USER("UPDATE_USER"),

    /**
     * 注册用户
     */
    REGISTER_USER("REGISTER_USER"),

    /**
     * 内部更新用户
     */
    UPDATE_USER_INTERNAL("UPDATE_USER_INTERNAL"),

    /**
     * 内部创建用户
     */
    CREATE_USER_INTERNAL("CREATE_USER_INTERNAL"),

    /**
     * 导入用户
     */
    IMPORT_USER("IMPORT_USER")

    ;

    private final String id;

    UserOperation(String id) {
        this.id = id;
    }

    @Override
    @Nonnull
    public String id() {
        return id;
    }

}
