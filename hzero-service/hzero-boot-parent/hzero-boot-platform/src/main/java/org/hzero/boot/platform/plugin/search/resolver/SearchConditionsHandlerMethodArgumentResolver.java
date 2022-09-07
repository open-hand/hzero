package org.hzero.boot.platform.plugin.search.resolver;

import org.hzero.boot.platform.plugin.search.entity.SearchConditions;
import org.springframework.core.MethodParameter;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

public class SearchConditionsHandlerMethodArgumentResolver implements HandlerMethodArgumentResolver {
    private static final String PARAM_SEARCH_CONDITIONS_NAME = "search.condition";
    private static final String PARAM_SEARCH_CONDITIONS_SPLIT = ",";
    private static final int PARAM_SEARCH_CONDITIONS_ITEM_MIN_SIZE = 2;

    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        return SearchConditions.class.equals(parameter.getParameterType());
    }

    @Override
    @SuppressWarnings("DuplicatedCode")
    public SearchConditions resolveArgument(MethodParameter parameter, ModelAndViewContainer mavContainer, NativeWebRequest webRequest, WebDataBinderFactory binderFactory) throws Exception {
        Iterator<String> parameterNames = webRequest.getParameterNames();
        List<String> parameterValues = new ArrayList<>();
        while (parameterNames.hasNext()) {
            String parameterName = parameterNames.next();
            if (parameterName.startsWith(PARAM_SEARCH_CONDITIONS_NAME)) {
                parameterValues.add(webRequest.getParameter(parameterName));
            }
        }
        if (parameterValues.isEmpty()) {
            return null;
        }
        SearchConditions searchConditions = new SearchConditions(parameterValues.size());
        for (String parameterValue : parameterValues) {
            if (StringUtils.isEmpty(parameterValue)) {
                continue;
            }
            String[] conditions = parameterValue.split(PARAM_SEARCH_CONDITIONS_SPLIT);
            if (conditions.length < PARAM_SEARCH_CONDITIONS_ITEM_MIN_SIZE) {
                throw new IllegalArgumentException("Query condition parameters cannot be parsed : " + parameterValue);
            }
            searchConditions.appendCondition(conditions[0], conditions[1],
                    conditions.length == 2 ? null :
                            StringUtils.collectionToCommaDelimitedString(Arrays.asList(Arrays.copyOfRange(conditions, 2, conditions.length))));
        }
        return searchConditions;
    }
}
