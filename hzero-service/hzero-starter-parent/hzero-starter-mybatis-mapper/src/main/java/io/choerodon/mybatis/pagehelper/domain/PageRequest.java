package io.choerodon.mybatis.pagehelper.domain;

import java.util.Iterator;
import java.util.Map;

import io.choerodon.mybatis.util.StringUtil;


/**
 * source : org.springframework.data.domain.PageRequest
 * 分页、排序请求封装对象
 *
 * @author NaccOll
 * 2018/1/30
 **/
public class PageRequest {
    private int page;
    private int size;
    private Sort sort;

    public PageRequest() {
    }

    public PageRequest(int page, int size) {
        this(page, size, null);
    }

    /**
     * 构造方法
     *
     * @param page page
     * @param size size
     * @param sort sort
     */
    public PageRequest(int page, int size, Sort sort) {
        this.page = page;
        this.size = size;
        this.sort = sort;
    }

    public PageRequest(int page, int size, Sort.Direction direction, String... properties) {
        this(page, size, new Sort(direction, properties));
    }

    public int getPage() {
        return page;
    }

    public void setPage(int page) {
        this.page = page;
    }

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }

    public Sort getSort() {
        return sort;
    }

    public void setSort(Sort sort) {
        this.sort = sort;
    }

    /**
     * 关联表字段映射关系，key:前端传入字段，value:对应的数据库别名+字段
     * e.g user表和role表联查，都有name字段，前端传入name字段排序传入为userName和roleName,sql中user别名为u,role别名为r
     * 写法：map.put("userName", "u.name");map.put("roleName", "r.name")重置order中的
     *
     * @param mainTableAlias sql语句主表别名
     * @param map            map
     */
    public void resetOrder(String mainTableAlias, Map<String, String> map) {
        if (this.sort != null) {
            Iterator<Sort.Order> iterator = this.sort.iterator();
            while (iterator.hasNext()) {
                boolean flag = false;
                Sort.Order order = iterator.next();
                for (Map.Entry<String, String> entry : map.entrySet()) {
                    if (entry.getKey().equals(order.getProperty())) {
                        order.setProperty(entry.getValue());
                        flag = true;
                    }
                }
                if (mainTableAlias != null && !flag) {
                    //驼峰转下划线
                    order.setProperty(mainTableAlias + "." + StringUtil.camelhumpToUnderline(order.getProperty()));
                }
            }
        }
    }
}
