package org.hzero.admin.infra.chain;

import org.hzero.admin.domain.vo.InitChainContext;

/**
 * 初始化拦截器，实现可增加初始化逻辑
 * @author XCXCXCXCX
 * @date 2020/6/28 10:24 上午
 */
public interface InitFilter {

    /**
     * 执行拦截器操作，用于服务初始化
     * @param chain
     * @param context
     */
    void doFilter(InitChain chain, InitChainContext context);

}
