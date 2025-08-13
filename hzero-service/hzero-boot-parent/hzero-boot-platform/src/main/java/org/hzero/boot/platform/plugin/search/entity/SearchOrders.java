package org.hzero.boot.platform.plugin.search.entity;

import org.springframework.data.domain.Sort;

import java.util.ArrayList;
import java.util.List;

/**
 * 高级检索排序配置
 *
 * @author qingsheng.chen@hand-china.com 2020-03-05 09:25:52
 */
public class SearchOrders {
    private List<Order> orders;

    public SearchOrders() {
    }

    public SearchOrders(int initialCapacity) {
        this.orders = new ArrayList<>(initialCapacity);
    }

    public void appendOrder(String fieldName, String direction) {
        if (this.orders == null) {
            this.orders = new ArrayList<>();
        }
        this.orders.add(new Order().setFieldName(fieldName).setDirection(Sort.Direction.fromString(direction)));
    }
    public void appendOrder(String fieldName, Sort.Direction direction) {
        if (this.orders == null) {
            this.orders = new ArrayList<>();
        }
        this.orders.add(new Order().setFieldName(fieldName).setDirection(direction));
    }

    public List<Order> getOrders() {
        return orders;
    }

    public SearchOrders setOrders(List<Order> orders) {
        this.orders = orders;
        return this;
    }

    public static class Order {
        private String fieldName;
        private Sort.Direction direction;

        public String getFieldName() {
            return fieldName;
        }

        public Order setFieldName(String fieldName) {
            this.fieldName = fieldName;
            return this;
        }

        public Sort.Direction getDirection() {
            return direction;
        }

        public Order setDirection(Sort.Direction direction) {
            this.direction = direction;
            return this;
        }
    }
}
