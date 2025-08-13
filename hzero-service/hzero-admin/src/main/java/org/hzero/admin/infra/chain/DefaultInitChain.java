package org.hzero.admin.infra.chain;

import org.hzero.admin.domain.vo.InitChainContext;
import org.springframework.core.OrderComparator;

import java.util.List;

/**
 * 默认初始化链
 *
 * @author XCXCXCXCX
 * @date 2020/6/28 10:22 上午
 */
public class DefaultInitChain implements InitChain {

    private List<InitFilter> initFilters;

    private int currentIndex = 0;

    public DefaultInitChain(/*@NotNull*/ List<InitFilter> initFilters) {
        this.initFilters = initFilters;
        this.initFilters.sort(new OrderComparator());
    }

    @Override
    public void doChain(InitChainContext context) {
        initNext(this, context);
    }

    @Override
    public void initNext(InitChain chain, InitChainContext context) {
        if (currentIndex < initFilters.size()) {
            initFilters.get(currentIndex++).doFilter(chain, context);
        }
    }

}
