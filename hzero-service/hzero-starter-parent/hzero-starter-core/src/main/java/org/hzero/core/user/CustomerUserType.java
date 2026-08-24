package org.hzero.core.user;

/**
 * C端用户
 *
 * @author bojiangzhou 2019/10/28
 */
public final class CustomerUserType extends UserType {

    private static final String USER_TYPE = "C";

    @Override
    public String value() {
        return USER_TYPE;
    }
}
