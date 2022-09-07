package org.hzero.boot.platform.entity.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import org.hzero.boot.platform.entity.dto.RegistParam;
import org.hzero.boot.platform.entity.feign.fallback.EntityRegistFeignClientFallbackFactory;
import org.hzero.common.HZeroService;

/**
 * description
 *
 * @author xingxing.wu@hand-china.com 2019/07/08 16:15
 */
@FeignClient(value = HZeroService.Platform.NAME, fallbackFactory = EntityRegistFeignClientFallbackFactory.class)
public interface EntityRegistFeignClient {

    @PostMapping("/v1/entity-tables/register")
     void register(@RequestBody RegistParam param);

}
