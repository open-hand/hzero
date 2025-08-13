package org.hzero.export.entity;

import org.hzero.export.vo.ExportColumn;

/**
 * 数据链表
 *
 * @author shuangfei.zhu@hand-china.com 2020/12/16 19:49
 */
public class Node {

    public Node() {
    }

    public Node(Object data, ExportColumn exportColumn, Node parent, Node child) {
        this.data = data;
        this.exportColumn = exportColumn;
        this.parent = parent;
        this.child = child;
    }

    /**
     * 当前数据
     */
    private Object data;
    /**
     * 列定义
     */
    private ExportColumn exportColumn;
    /**
     * 父节点
     */
    private Node parent;
    /**
     * 子节点
     */
    private Node child;

    public Object getData() {
        return data;
    }

    public Node setData(Object data) {
        this.data = data;
        return this;
    }

    public ExportColumn getExportColumn() {
        return exportColumn;
    }

    public Node setExportColumn(ExportColumn exportColumn) {
        this.exportColumn = exportColumn;
        return this;
    }

    public Node getParent() {
        return parent;
    }

    public Node setParent(Node parent) {
        this.parent = parent;
        return this;
    }

    public Node getChild() {
        return child;
    }

    public Node setChild(Node child) {
        this.child = child;
        return this;
    }

    public Node pre() {
        return parent;
    }

    public Node next() {
        return child;
    }
}
