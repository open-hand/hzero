package org.hzero.boot.platform.common.domain.service.impl;

import io.choerodon.core.exception.CommonException;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.hzero.boot.platform.common.domain.service.CommonTemplateClientService;
import org.hzero.boot.platform.common.domain.vo.RenderResult;
import org.hzero.boot.platform.common.infra.feign.CommonTemplateRemoteService;
import org.hzero.boot.platform.common.infra.feign.vo.RenderParameterVO;
import org.hzero.core.util.ResponseUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Objects;

/**
 * 通用模板服务实现
 *
 * @author bergturing 2020/08/18 14:45
 */
@Service
public class CommonTemplateClientServiceImpl implements CommonTemplateClientService {
    /**
     * 通用模板远程服务对象
     */
    private final CommonTemplateRemoteService commonTemplateRemoteService;

    /**
     * 构建函数
     *
     * @param commonTemplateRemoteService 通用模板远程服务对象
     */
    @Autowired
    public CommonTemplateClientServiceImpl(CommonTemplateRemoteService commonTemplateRemoteService) {
        this.commonTemplateRemoteService = commonTemplateRemoteService;
    }

    @Override
    public String renderContent(Long tenantId, String templateCode, String lang, Map<String, Object> args) {
        // 渲染数据并获取结果
        RenderResult renderResult = this.render(tenantId, templateCode, lang, args);
        if (renderResult.isSuccess()) {
            // 渲染成功
            return renderResult.getContent();
        } else {
            // 渲染失败
            throw new CommonException(renderResult.getMessage());
        }
    }

    @Override
    public RenderResult render(Long tenantId, String templateCode, String lang, Map<String, Object> args) {
        // 参数校验
        this.validate(tenantId, templateCode, lang);
        try {
            // 渲染模板
            ResponseEntity<String> responseEntity = this.commonTemplateRemoteService
                    .render(tenantId, new RenderParameterVO(tenantId, templateCode, lang, args));

            // 获取响应结果
            return ResponseUtils.getResponse(responseEntity, RenderResult.class,
                    (httpStatus, response) -> {
                        throw new RuntimeException("模板渲染结果状态码不为成功，状态码：" + httpStatus + ", " + response);
                    }, exceptionResponse -> {
                        throw new RuntimeException("模板渲染结果响应失败, " + exceptionResponse);
                    });
        } catch (Exception e) {
            // 处理失败
            return RenderResult.failure(ExceptionUtils.getRootCauseMessage(e));
        }
    }

    /**
     * 参数校验
     *
     * @param tenantId     租户ID
     * @param templateCode 模板编码
     * @param lang         语言
     */
    private void validate(Long tenantId, String templateCode, String lang) {
        if (Objects.isNull(tenantId)) {
            // 通用模板渲染时，通用模板租户ID不能为空
            throw new CommonException("hpfm.boot.common-template.tenant-id.required");
        }

        if (StringUtils.isBlank(templateCode)) {
            // 通用模板渲染时，通用模板编码不能为空
            throw new CommonException("hpfm.boot.common-template.template-code.required");
        }

        if (StringUtils.isBlank(lang)) {
            // 通用模板渲染时，通用模板语言不能为空
            throw new CommonException("hpfm.boot.common-template.lang.required");
        }
    }
}
