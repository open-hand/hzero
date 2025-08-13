package org.hzero.boot.platform.common.infra.feign;

import org.hzero.boot.platform.common.infra.feign.impl.CommonTemplateRemoteServiceImpl;
import org.hzero.boot.platform.common.infra.feign.vo.RenderParameterVO;
import org.hzero.common.HZeroService;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

/**
 * 通用模板远程服务
 *
 * @author bergturing 2020/08/05 10:01
 */
@FeignClient(
        value = HZeroService.Platform.NAME,
        path = "/v1",
        fallback = CommonTemplateRemoteServiceImpl.class
)
public interface CommonTemplateRemoteService {
    /**
     * 通用模板渲染
     *
     * @param organizationId 租户ID
     * @param parameter      渲染参数对象
     * @return 通用模板渲染结果
     */
    @PostMapping(value = "/{organizationId}/common-templates/render")
    ResponseEntity<String> render(@PathVariable("organizationId") Long organizationId,
                                  @RequestBody RenderParameterVO parameter);
}
