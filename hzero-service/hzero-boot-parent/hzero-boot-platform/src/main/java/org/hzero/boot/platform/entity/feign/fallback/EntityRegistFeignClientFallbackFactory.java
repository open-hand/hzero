package org.hzero.boot.platform.entity.feign.fallback;

import feign.hystrix.FallbackFactory;
import org.hzero.boot.platform.entity.feign.EntityRegistFeignClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Lov服务Feign客户端fall back工厂类
 *
 * @author xingxing.wu@hand-china.com 2019/07/08 16:15
 */
@Component
public class EntityRegistFeignClientFallbackFactory implements FallbackFactory<EntityRegistFeignClient> {

    private Logger logger = LoggerFactory.getLogger(EntityRegistFeignClientFallbackFactory.class);
    @Autowired
    private EntityRegistFeignClient entiryRegistFeignClient;

    @Override
    public EntityRegistFeignClient create(Throwable throwable) {
        logger.error(throwable.getMessage(), throwable);
        return entiryRegistFeignClient;
    }
}
