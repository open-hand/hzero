package io.choerodon.mybatis.spring.resolver;

import io.choerodon.mybatis.pagehelper.domain.PageRequest;
import org.springframework.core.MethodParameter;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

/**
 * 分页、排序请求转pageRequest解析器接口
 *
 * @author NaccOll
 * 2018/1/30
 **/
public interface PageRequestArgumentResolver extends HandlerMethodArgumentResolver {
    @Override
    PageRequest resolveArgument(MethodParameter parameter, ModelAndViewContainer mavContainer,
                                NativeWebRequest webRequest, WebDataBinderFactory binderFactory) throws Exception;
}
