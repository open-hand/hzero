package org.hzero.boot.platform.plugin.search.resolver;

import org.hzero.boot.platform.plugin.search.entity.SearchOrders;
import org.springframework.core.MethodParameter;
import org.springframework.data.domain.Sort;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class SearchOrdersHandlerMethodArgumentResolver implements HandlerMethodArgumentResolver {
    private static final String PARAM_SEARCH_ORDERS_NAME = "search.order";
    private static final String PARAM_SEARCH_ORDERS_SPLIT = ",";
    private static final int PARAM_SEARCH_ORDERS_ITEM_MAX_SIZE = 2;

    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        return SearchOrders.class.equals(parameter.getParameterType());
    }

    @Override
    @SuppressWarnings("DuplicatedCode")
    public SearchOrders resolveArgument(MethodParameter parameter, ModelAndViewContainer mavContainer, NativeWebRequest webRequest, WebDataBinderFactory binderFactory) throws Exception {
        Iterator<String> parameterNames = webRequest.getParameterNames();
        List<String> parameterValues = new ArrayList<>();
        while (parameterNames.hasNext()) {
            String parameterName = parameterNames.next();
            if (parameterName.startsWith(PARAM_SEARCH_ORDERS_NAME)) {
                parameterValues.add(webRequest.getParameter(parameterName));
            }
        }
        if (parameterValues.isEmpty()) {
            return null;
        }
        SearchOrders searchOrders = new SearchOrders(parameterValues.size());
        for (String parameterValue : parameterValues) {
            if (StringUtils.isEmpty(parameterValue)) {
                continue;
            }
            String[] orders = parameterValue.split(PARAM_SEARCH_ORDERS_SPLIT);
            if (orders.length > PARAM_SEARCH_ORDERS_ITEM_MAX_SIZE) {
                throw new IllegalArgumentException("Query order parameters cannot be parsed : " + parameterValue);
            }
            if (orders.length == 1) {
                searchOrders.appendOrder(orders[0], Sort.Direction.ASC);
            } else {
                searchOrders.appendOrder(orders[0], orders[1]);
            }
        }
        return searchOrders;
    }
}
