package io.choerodon.mybatis.service;

import java.util.Arrays;
import java.util.List;

import io.choerodon.core.domain.Page;
import io.choerodon.mybatis.common.BaseMapper;
import io.choerodon.mybatis.helper.OptionalHelper;
import io.choerodon.mybatis.pagehelper.PageHelper;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Created by xausky on 3/15/17.
 */
public abstract class BaseServiceImpl<T> implements BaseService<T> {
    @Autowired
    private BaseMapper<T> mapper;

    public List<T> selectAll() {
        return mapper.selectAll();
    }

    public int delete(T record) {
        return mapper.delete(record);
    }

    public int selectCount(T record) {
        return mapper.selectCount(record);
    }

    public int insert(T record) {
        return mapper.insert(record);
    }

    public int deleteByPrimaryKey(Object key) {
        return mapper.deleteByPrimaryKey(key);
    }

    public int insertSelective(T record) {
        return mapper.insertSelective(record);
    }

    public T selectOne(T record) {
        return mapper.selectOne(record);
    }

    public List<T> select(T record) {
        return mapper.select(record);
    }

    public boolean existsWithPrimaryKey(Object key) {
        return mapper.existsWithPrimaryKey(key);
    }

    public int updateByPrimaryKey(T record) {
        return mapper.updateByPrimaryKey(record);
    }

    public int updateByPrimaryKeySelective(T record) {
        return mapper.updateByPrimaryKeySelective(record);
    }

    public T selectByPrimaryKey(Object key) {
        return mapper.selectByPrimaryKey(key);
    }

    @Override
    public Page<T> pageAll(int page, int size) {
        return PageHelper.doPage(page, size, this::selectAll);
    }

    @Override
    public Page<T> page(T record, int page, int size) {
        return PageHelper.doPage(page, size, () -> select(record));
    }

    @Override
    public int insertOptional(T record, String... optionals) {
        OptionalHelper.optional(Arrays.asList(optionals));
        return mapper.insertOptional(record);
    }

    @Override
    public int updateOptional(T record, String... optionals) {
        OptionalHelper.optional(Arrays.asList(optionals));
        return mapper.updateOptional(record);
    }

}
