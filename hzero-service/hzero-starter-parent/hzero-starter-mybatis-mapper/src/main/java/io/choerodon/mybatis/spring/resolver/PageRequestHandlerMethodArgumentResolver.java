package io.choerodon.mybatis.spring.resolver;

import java.lang.reflect.Method;

import io.choerodon.mybatis.pagehelper.annotation.PageableDefault;
import io.choerodon.mybatis.pagehelper.domain.PageRequest;
import io.choerodon.mybatis.pagehelper.domain.Sort;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.MethodParameter;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.ModelAndViewContainer;



/**
 * 分页、排序请求转pageRequest解析器实现类
 *
 * @author NaccOll
 * 2018/1/30
 **/
@Component
public class PageRequestHandlerMethodArgumentResolver implements PageRequestArgumentResolver {

    private static final String INVALID_DEFAULT_PAGE_SIZE =
            "Invalid default page size configured for method %s! Must not be less than one!";

    private static final String DEFAULT_PAGE_PARAMETER = "page";
    private static final String DEFAULT_SIZE_PARAMETER = "size";
    private static final String DEFAULT_PREFIX = "";
    private static final String DEFAULT_QUALIFIER_DELIMITER = "_";
    private static final int DEFAULT_MAX_PAGE_SIZE = 1000;
    private static final PageRequest DEFAULT_PAGE_REQUEST = new PageRequest(0, 20);

    private PageRequest fallbackPageRequest = DEFAULT_PAGE_REQUEST;
    private SortArgumentResolver sortResolver;
    private String pageParameterName = DEFAULT_PAGE_PARAMETER;
    private String sizeParameterName = DEFAULT_SIZE_PARAMETER;
    private String prefix = DEFAULT_PREFIX;
    private String qualifierDelimiter = DEFAULT_QUALIFIER_DELIMITER;
    private int maxPageSize = DEFAULT_MAX_PAGE_SIZE;
    private boolean oneIndexedParameters = false;

    public PageRequestHandlerMethodArgumentResolver(SortArgumentResolver sortResolver) {
        this.sortResolver = sortResolver;
    }

    public PageRequest getFallbackPageRequest() {
        return fallbackPageRequest;
    }

    public void setFallbackPageRequest(PageRequest fallbackPageRequest) {
        this.fallbackPageRequest = fallbackPageRequest;
    }

    public SortArgumentResolver getSortResolver() {
        return sortResolver;
    }

    public void setSortResolver(SortArgumentResolver sortResolver) {
        this.sortResolver = sortResolver;
    }

    public String getPageParameterName() {
        return pageParameterName;
    }

    public void setPageParameterName(String pageParameterName) {
        this.pageParameterName = pageParameterName;
    }

    public String getSizeParameterName() {
        return sizeParameterName;
    }

    public void setSizeParameterName(String sizeParameterName) {
        this.sizeParameterName = sizeParameterName;
    }

    public String getPrefix() {
        return prefix;
    }

    public void setPrefix(String prefix) {
        this.prefix = prefix;
    }

    public String getQualifierDelimiter() {
        return qualifierDelimiter;
    }

    public void setQualifierDelimiter(String qualifierDelimiter) {
        this.qualifierDelimiter = qualifierDelimiter;
    }

    public int getMaxPageSize() {
        return maxPageSize;
    }

    public void setMaxPageSize(int maxPageSize) {
        this.maxPageSize = maxPageSize;
    }

    public boolean isOneIndexedParameters() {
        return oneIndexedParameters;
    }

    public void setOneIndexedParameters(boolean oneIndexedParameters) {
        this.oneIndexedParameters = oneIndexedParameters;
    }

    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        return PageRequest.class.equals(parameter.getParameterType());
    }

    @Override
    public PageRequest resolveArgument(MethodParameter methodParameter, ModelAndViewContainer mavContainer, NativeWebRequest webRequest, WebDataBinderFactory binderFactory) {
        PageRequest defaultOrFallback = getDefaultFromAnnotationOrFallback(methodParameter);
        String pageString = webRequest.getParameter(getParameterNameToUse(pageParameterName, methodParameter));
        String pageSizeString = webRequest.getParameter(getParameterNameToUse(sizeParameterName, methodParameter));

        boolean pageAndSizeGiven = StringUtils.hasText(pageString) && StringUtils.hasText(pageSizeString);

        if (!pageAndSizeGiven && defaultOrFallback == null) {
            return null;
        }

        int page = StringUtils.hasText(pageString) ? parseAndApplyBoundaries(pageString, Integer.MAX_VALUE, true)
                : defaultOrFallback.getPage();
        int pageSize = StringUtils.hasText(pageSizeString) ? parseAndApplyBoundaries(pageSizeString, maxPageSize, false)
                : defaultOrFallback.getSize();

        pageSize = pageSize < 0 ? defaultOrFallback.getSize() : pageSize;
        pageSize = pageSize > maxPageSize ? maxPageSize : pageSize;

        Sort sort = sortResolver.resolveArgument(methodParameter, mavContainer, webRequest, binderFactory);

        // Default if necessary and default configured
        sort = sort == null && defaultOrFallback != null ? defaultOrFallback.getSort() : sort;

        return new PageRequest(page, pageSize, sort);
    }

    private PageRequest getDefaultFromAnnotationOrFallback(MethodParameter methodParameter) {

        if (methodParameter.hasParameterAnnotation(PageableDefault.class)) {
            return getDefaultPageRequestFrom(methodParameter);
        }

        return fallbackPageRequest;
    }

    private static PageRequest getDefaultPageRequestFrom(MethodParameter parameter) {

        PageableDefault defaults = parameter.getParameterAnnotation(PageableDefault.class);

        Integer defaultPageNumber = defaults.page();
        Integer defaultPageSize = defaults.size();

        if (defaultPageSize < 1) {
            Method annotatedMethod = parameter.getMethod();
            throw new IllegalStateException(String.format(INVALID_DEFAULT_PAGE_SIZE, annotatedMethod));
        }

        if (defaults.sort().length == 0) {
            return new PageRequest(defaultPageNumber, defaultPageSize);
        }

        return new PageRequest(defaultPageNumber, defaultPageSize, defaults.direction(), defaults.sort());
    }

    protected String getParameterNameToUse(String source, MethodParameter parameter) {

        StringBuilder builder = new StringBuilder(prefix);

        if (parameter != null && parameter.hasParameterAnnotation(Qualifier.class)) {
            builder.append(parameter.getParameterAnnotation(Qualifier.class).value());
            builder.append(qualifierDelimiter);
        }

        return builder.append(source).toString();
    }

    private int parseAndApplyBoundaries(String parameter, int upper, boolean shiftIndex) {
        try {
            int parsed = Integer.parseInt(parameter) - (oneIndexedParameters && shiftIndex ? 1 : 0);
            return parsed > upper ? upper : parsed;
        } catch (NumberFormatException e) {
            return 0;
        }
    }
}
