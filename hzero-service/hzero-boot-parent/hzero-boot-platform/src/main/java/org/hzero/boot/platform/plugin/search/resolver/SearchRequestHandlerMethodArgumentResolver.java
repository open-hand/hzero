package org.hzero.boot.platform.plugin.search.resolver;

import org.hzero.boot.platform.plugin.search.entity.SearchRequest;
import org.springframework.core.MethodParameter;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

public class SearchRequestHandlerMethodArgumentResolver implements HandlerMethodArgumentResolver {
    private static final String PARAM_SEARCH_NAME = "search";

    private SearchConditionsHandlerMethodArgumentResolver conditionsResolver;
    private SearchOrdersHandlerMethodArgumentResolver orderResolver;

    public SearchRequestHandlerMethodArgumentResolver(SearchConditionsHandlerMethodArgumentResolver conditionsResolver,
                                                      SearchOrdersHandlerMethodArgumentResolver orderResolver) {
        this.conditionsResolver = conditionsResolver;
        this.orderResolver = orderResolver;
    }

    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        return SearchRequest.class.equals(parameter.getParameterType());
    }

    @Override
    public SearchRequest resolveArgument(MethodParameter parameter, ModelAndViewContainer mavContainer, NativeWebRequest webRequest, WebDataBinderFactory binderFactory) throws Exception {
        String searchCode = webRequest.getParameter(PARAM_SEARCH_NAME);
        if (!StringUtils.hasText(searchCode)) {
            return null;
        }
        return new SearchRequest().setSearchCode(searchCode)
                .setConditions(conditionsResolver.resolveArgument(parameter, mavContainer, webRequest, binderFactory))
                .setOrders(orderResolver.resolveArgument(parameter, mavContainer, webRequest, binderFactory));
    }
}
