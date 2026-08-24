package org.hzero.report.infra.engine.data;

import java.util.List;
import java.util.Map;

/**
 * SQL XML叶节点
 *
 * @author xianzhi.chen@hand-china.com 2018年11月21日下午3:16:56
 */
public class SqlXmlLeaf {

    /**
     * 节点名称
     */
    private String name;
    /**
     * 拼接好的 SQL
     */
    private String sql;
    /**
     * 结果数据暂存区
     */
    private List<Map<String, Object>> datas;
    /**
     * 子节点
     */
    private List<SqlXmlLeaf> children;

    /**
     * new Leaf
     *
     * @param name 节点名称
     * @param sql  拼接好的SQL
     */
    public SqlXmlLeaf(String name, String sql) {
        this.name = name;
        this.sql = sql;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSql() {
        return sql;
    }

    public void setSql(String sql) {
        this.sql = sql;
    }

    public List<Map<String, Object>> getDatas() {
        return datas;
    }

    public void setDatas(List<Map<String, Object>> datas) {
        this.datas = datas;
    }

    public List<SqlXmlLeaf> getChildren() {
        return children;
    }

    public void setChildren(List<SqlXmlLeaf> children) {
        this.children = children;
    }

    @Override
    public String toString() {
        return "Leaf [name=" + name + ", sql=" + sql + ", children=" + children + "]";
    }

}
