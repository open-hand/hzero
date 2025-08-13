package org.hzero.boot.platform.lov.handler;

import io.choerodon.core.exception.CommonException;

/**
 * sql校验器
 *
 * @author gaokuo.dai@hand-china.com 2018年6月27日上午9:50:00
 */
public interface SqlChecker {

    /**
     * sql关键词过滤
     *
     * @param sql sql
     * @throws CommonException 校验未通过
     */
    void doFilter(String sql);
}
