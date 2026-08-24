package org.hzero.boot.platform.common.infra.feign.impl;

import org.hzero.boot.platform.common.infra.feign.CommonTemplateRemoteService;
import org.hzero.boot.platform.common.infra.feign.vo.RenderParameterVO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

/**
 * 通用模板远程服务实现
 *
 * @author bergturing 2020/08/05 10:16
 */
@Component
public class CommonTemplateRemoteServiceImpl implements CommonTemplateRemoteService {
    /**
     * 日志对象
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(CommonTemplateRemoteServiceImpl.class);

    @Override
    public ResponseEntity<String> render(Long organizationId, RenderParameterVO parameter) {
        LOGGER.error("Error Get Common Template, Params[organizationId = {} renderParameter = {}]", organizationId, parameter);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
    }
}
