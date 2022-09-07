package org.hzero.boot.platform.plugin.search.entity;

import java.util.List;

public class SearchRequest {
    private String searchCode;
    private SearchConditions conditions;
    private SearchOrders orders;

    public String getSearchCode() {
        return searchCode;
    }

    public SearchRequest setSearchCode(String searchCode) {
        this.searchCode = searchCode;
        return this;
    }

    public SearchConditions getConditions() {
        return conditions;
    }

    public SearchRequest setConditions(SearchConditions conditions) {
        this.conditions = conditions;
        return this;
    }

    public SearchOrders getOrders() {
        return orders;
    }

    public SearchRequest setOrders(SearchOrders orders) {
        this.orders = orders;
        return this;
    }
}
