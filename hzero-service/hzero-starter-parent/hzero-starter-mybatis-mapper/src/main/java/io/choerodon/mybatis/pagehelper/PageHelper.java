package io.choerodon.mybatis.pagehelper;

import io.choerodon.core.domain.Page;
import io.choerodon.core.domain.PageInfo;
import io.choerodon.mybatis.pagehelper.domain.PageRequest;
import io.choerodon.mybatis.pagehelper.domain.Sort;
import io.choerodon.mybatis.pagehelper.page.PageMethod;

import java.util.List;


/**
 * Created by xausky on 3/23/17.
 */
public class PageHelper extends PageMethod {

    public static <E> Page<E> doPage(int page, int size, Select select) {
        if (page >= 0 && size > 0) {
            startPage(page, size);
            return (Page<E>) select.doSelect();
        } else {
            return selectAllAsOnePage(select);
        }
    }

    public static <E> Page<E> doPage(PageRequest pageRequest, Select select) {
        if (pageRequest.getPage() >= 0 && pageRequest.getSize() > 0) {
            startPage(pageRequest.getPage(), pageRequest.getSize());
            return (Page<E>) select.doSelect();
        } else {
            return selectAllAsOnePage(select);
        }
    }

    /**
     * 只支持单表的分页和排序功能，暂不支持多表联查的情况
     * 多表情况需要手写sql
     *
     * @param <E>         E
     * @param pageRequest pageRequest
     * @param select      select
     * @return Page Page
     */
    public static <E> Page<E> doPageAndSort(PageRequest pageRequest, Select select) {
        if (pageRequest.getPage() >= 0 && pageRequest.getSize() > 0) {
            startPageAndSort(pageRequest);
            return (Page<E>) select.doSelect();
        } else {
            startSort(pageRequest.getSort());
            return selectAllAsOnePage(select);
        }
    }

    /**
     * 只排序，对于一些复杂嵌套查询，pagehelper不支持分页的情况，只做排序操作，然后手动分页
     *
     * @param <E>    E
     * @param select select
     * @param sort   sort
     * @return List List
     */
    public static <E> List<E> doSort(Sort sort, Select select) {
        startSort(sort);
        return (List<E>) select.doSelect();
    }

    private static <E> Page<E> selectAllAsOnePage(Select select) {
        List list = select.doSelect();
        int total = list.size();
        return new Page<E>(list, new PageInfo(0, total == 0 ? 1 : total), total);
    }
}
