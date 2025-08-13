package org.hzero.feign.interceptor;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

import feign.RequestInterceptor;
import feign.RequestTemplate;
import org.apache.commons.collections4.CollectionUtils;


/**
 * Composite feign request interceptor
 */
public class CompositeRequestInterceptor implements RequestInterceptor {

    private List<FeignRequestInterceptor> interceptors;

    public CompositeRequestInterceptor(List<FeignRequestInterceptor> interceptors) {
        this.interceptors = Optional.ofNullable(interceptors).orElse(new ArrayList<>());
        sortInterceptors(this.interceptors);
    }

    private void sortInterceptors(List<FeignRequestInterceptor> interceptors) {
        if(CollectionUtils.isNotEmpty(interceptors)){
            interceptors.sort(Comparator.comparingInt(FeignRequestInterceptor::getOrder));
        }
    }

    public void addInterceptor(FeignRequestInterceptor interceptor){
        interceptors.add(interceptor);
        sortInterceptors(interceptors);
    }

    @Override
    public void apply(RequestTemplate template) {
        interceptors.forEach(interceptor -> interceptor.apply(template));
    }

}
