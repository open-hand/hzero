package org.hzero.boot.platform.event.helper;

import java.util.Map;

/**
 * 规则匹配器，判断规则与条件是否匹配
 *
 * @author jiangzhou.bo@hand-china.com 2018/06/16 22:35
 */
public interface RuleMatcher {

    /**
     * 判断传入的规则与条件是否匹配
     *
     * @param rule 规则
     * @param condition 条件
     * @return 规则是否匹配
     *         <ul>
     *         <li>true - 规则匹配</li>
     *         <li>false - 规则不匹配</li>
     *         <ul/>
     */
    boolean matches(String rule, Map<String, Object> condition);

}
