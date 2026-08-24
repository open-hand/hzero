package io.choerodon.mybatis.service;

import io.choerodon.core.domain.Page;

import java.util.List;

/**
 * Created by xausky on 3/16/17.
 */
public interface BaseService<T> {

    List<T> selectAll();

    int delete(T record);

    int selectCount(T record);

    int insert(T record);

    int deleteByPrimaryKey(Object key);

    int insertSelective(T record);

    T selectOne(T record);

    List<T> select(T record);

    boolean existsWithPrimaryKey(Object key);

    int updateByPrimaryKey(T record);

    int updateByPrimaryKeySelective(T record);

    T selectByPrimaryKey(Object key);

    Page<T> pageAll(int page, int size);

    Page<T> page(T record, int page, int size);

    int insertOptional(T record, String... optionals);

    int updateOptional(T record, String... optionals);

}
