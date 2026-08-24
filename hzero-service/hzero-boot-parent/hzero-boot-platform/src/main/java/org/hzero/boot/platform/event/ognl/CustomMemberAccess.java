package org.hzero.boot.platform.event.ognl;

import java.lang.reflect.Member;
import java.util.Map;

import ognl.MemberAccess;
import ognl.OgnlContext;

/**
 * 由于ognl没有提供 {@link MemberAccess} 的实现，而且创建 {@link OgnlContext} 时需要提供 {@link MemberAccess}
 * 参数，没有则会抛出异常，因此提供默认实现。并设置 {@link MemberAccess#isAccessible} 始终返回true，可以访问私有变量.
 *
 * @author jiangzhou.bo@hand-china.com 2018/06/13 11:25
 */
public class CustomMemberAccess implements MemberAccess {

    @Override
    public Object setup(@SuppressWarnings("rawtypes") Map context, Object target, Member member, String propertyName) {
        return null;
    }

    @Override
    public void restore(@SuppressWarnings("rawtypes") Map context, Object target, Member member, String propertyName, Object state) {
        // Do nothing because of ...
    }

    @Override
    public boolean isAccessible(@SuppressWarnings("rawtypes") Map context, Object target, Member member, String propertyName) {
        return true;
    }
}
