package io.choerodon.mybatis.pagehelper.parser;

import io.choerodon.mybatis.pagehelper.domain.Sort;
import org.apache.ibatis.mapping.MappedStatement;

/**
 * order by语句解析器
 *
 * @author XCXCXCXCX
 * @date 2020/2/26 1:31 下午
 */
public interface IOrderByParser {

    /**
     * 是否包含order by关键字
     * @param sql
     * @return
     */
    boolean containOrderBy(String sql);

    /**
     * Sort对象转sql
     *
     * @param sort sort
     * @param ms   MappedStatement
     * @return Sort对象转sql
     */
    String sortToString(Sort sort, MappedStatement ms);
}
