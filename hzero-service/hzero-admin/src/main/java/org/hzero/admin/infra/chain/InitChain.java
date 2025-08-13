package org.hzero.admin.infra.chain;

import org.hzero.admin.domain.vo.InitChainContext;

/**
 * @author XCXCXCXCX
 * @date 2020/6/28 10:10 上午
 */
public interface InitChain {

    /**
     * 执行链路（用于初始化）
     * @param context
     */
    void doChain(InitChainContext context);

    /**
     * 链路（用于初始化）中的下一个环节
     * @param chain
     * @param context
     */
    void initNext(InitChain chain, InitChainContext context);
}
