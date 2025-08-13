package org.hzero.admin.infra.factory;

import org.hzero.admin.infra.chain.DefaultInitChain;
import org.hzero.admin.infra.chain.InitChain;
import org.hzero.admin.infra.chain.InitFilter;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * @author XCXCXCXCX
 * @date 2020/6/28 3:31 下午
 */
@Component
public class InitChainFactoryBean implements FactoryBean<InitChain> {

    @Autowired
    private Optional<List<InitFilter>> optionalInitFilters;

    public InitChainFactoryBean() {
    }

    @Override
    public InitChain getObject() {
        return new DefaultInitChain(optionalInitFilters.orElse(new ArrayList<>()));
    }

    @Override
    public Class<?> getObjectType() {
        return DefaultInitChain.class;
    }
}
