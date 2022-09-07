package io.choerodon.mybatis.pagehelper.feign.encoder;

import feign.RequestTemplate;
import feign.codec.EncodeException;
import feign.codec.Encoder;
import io.choerodon.mybatis.pagehelper.domain.PageRequest;
import io.choerodon.mybatis.pagehelper.domain.Sort;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class PageRequestQueryEncoder implements Encoder {

    private final Encoder delegate;

    public PageRequestQueryEncoder(Encoder delegate) {
        this.delegate = delegate;
    }

    @Override
    public void encode(Object object, Type type, RequestTemplate requestTemplate) throws EncodeException {
        if (object instanceof PageRequest) {
            PageRequest pageRequest = (PageRequest) object;
            requestTemplate.query("page", pageRequest.getPage() + "");
            requestTemplate.query("size", pageRequest.getSize() + "");
            if (pageRequest.getSort() != null) {
                Collection<String> existingSorts = requestTemplate.queries().get("sort");
                List<String> sortQueries = existingSorts != null ? new ArrayList<>(existingSorts) : new ArrayList<>();
                for (Sort.Order order : pageRequest.getSort()) {
                    sortQueries.add(order.getProperty() + "," + order.getDirection());
                }
                requestTemplate.query("sort", sortQueries);
            }
        } else {
            delegate.encode(object, type, requestTemplate);
        }
    }
}
