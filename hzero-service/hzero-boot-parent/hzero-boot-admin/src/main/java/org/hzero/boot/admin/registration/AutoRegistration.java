package org.hzero.boot.admin.registration;

/**
 * @author XCXCXCXCX
 * @date 2020/6/10 5:18 下午
 */
public interface AutoRegistration {

    /**
     * 注册
     */
    void register(Registration registration);

    /**
     * 注销
     */
    void unregister(Registration registration);

}
