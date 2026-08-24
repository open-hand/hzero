/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2014-2017 abel533@gmail.com
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */

package io.choerodon.mybatis.pagehelper.dialect;

import static io.choerodon.mybatis.pagehelper.page.PageMethod.clearPage;
import static io.choerodon.mybatis.pagehelper.page.PageMethod.clearSort;

import java.util.List;
import java.util.Properties;

import io.choerodon.core.domain.Page;
import io.choerodon.core.domain.PageInfo;
import io.choerodon.mybatis.pagehelper.PageHelper;
import io.choerodon.mybatis.pagehelper.PageRowBounds;
import io.choerodon.mybatis.pagehelper.parser.ICountSqlParser;
import org.apache.ibatis.cache.CacheKey;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.session.RowBounds;



/**
 * {@inheritDoc}
 * 针对 PageHelper 的实现
 *
 * @author liuzh
 * @since 2016-12-04 14:32
 */
public abstract class AbstractHelperDialect extends AbstractDialect {

    public AbstractHelperDialect(ICountSqlParser parser) {
        super(parser);
    }

    /**
     * {@inheritDoc}
     * 获取分页参数
     *
     * @return PageInfo PageInfo
     */
    public PageInfo getLocalPage() {
        return PageHelper.getLocalPage();
    }

    @Override
    public final boolean skip(MappedStatement ms, Object parameterObject, RowBounds rowBounds) {
        PageInfo info = PageHelper.getLocalPage();
        return info == null;
    }

    @Override
    public boolean beforeCount(MappedStatement ms, Object parameterObject, RowBounds rowBounds) {
        PageInfo info = getLocalPage();
        return info.isCount();
    }

    @Override
    public boolean afterCount(long count, Object parameterObject, RowBounds rowBounds) {
        PageInfo info = getLocalPage();
        info.setTotal(count);
        if (rowBounds instanceof PageRowBounds) {
            ((PageRowBounds) rowBounds).setTotal((long) count);
        }
        //pageSize < 0 的时候，不执行分页查询
        //pageSize = 0 的时候，还需要执行后续查询，但是不会分页
        return info.getSize() >= 0 && count > 0;
    }

    @Override
    public Object processParameterObject(MappedStatement ms, Object parameterObject, BoundSql boundSql, CacheKey pageKey) {
        return parameterObject;
    }

    @Override
    public boolean beforePage(MappedStatement ms, Object parameterObject, RowBounds rowBounds) {
        PageInfo info = getLocalPage();
        return info.getSize() > 0;
    }

    @Override
    public String getPageSql(MappedStatement ms, BoundSql boundSql, Object parameterObject, RowBounds rowBounds, CacheKey pageKey) {
        String sql = boundSql.getSql();
        PageInfo info = getLocalPage();
        return getPageSql(sql, info, pageKey);
    }

    /**
     * {@inheritDoc}
     * 单独处理分页部分
     *
     * @param sql     sql
     * @param info    info
     * @param pageKey pageKey
     * @return String String
     */
    public abstract String getPageSql(String sql, PageInfo info, CacheKey pageKey);

    @Override
    public Object afterPage(List pageList, Object parameterObject, RowBounds rowBounds) {
        PageInfo info = getLocalPage();
        if (info == null) {
            return pageList;
        } else {
            return new Page(pageList, info, info.getTotal());
        }
    }

    @Override
    public void afterAll() {
        clearPage();
        clearSort();
    }

    @Override
    public void setProperties(Properties properties) {

    }
}
