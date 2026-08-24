package org.hzero.boot.platform.event.helper.impl;

import java.util.Map;

import ognl.MemberAccess;
import ognl.Ognl;
import ognl.OgnlContext;
import ognl.OgnlException;
import org.hzero.boot.platform.event.helper.RuleMatcher;
import org.hzero.boot.platform.event.ognl.CustomMemberAccess;

/**
 * 规则匹配器默认实现，使用Ognl匹配规则，需要 OgnlContext。
 *
 * @author jiangzhou.bo@hand-china.com 2018/06/16 22:41
 */
public class DefaultRuleMatcher implements RuleMatcher {

    private OgnlContext ognlContext;

    public DefaultRuleMatcher() {
        MemberAccess memberAccess = new CustomMemberAccess();
        this.ognlContext = new OgnlContext(null, null, memberAccess);
    }

    @Override
    public boolean matches(String rule, Map<String, Object> condition) {
        boolean result;
        try {
            result = (boolean) Ognl.getValue(rule, ognlContext, condition);
        } catch (OgnlException e) {
            throw new IllegalArgumentException(String.format("解析规则[%s]错误", rule));
        }
        return result;
    }
}
