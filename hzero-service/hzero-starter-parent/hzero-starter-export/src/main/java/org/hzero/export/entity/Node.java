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

    public Node(Object data, int rowIndex, int colIndex, ExportColumn exportColumn, Node parent, Node child) {
        this.data = data;
        this.rowIndex = rowIndex;
        this.colIndex = colIndex;
        this.exportColumn = exportColumn;
        this.parent = parent;
        this.child = child;
    }

    /**
     * 当前数据
     */
    private Object data;
    /**
     * 行下标
     */
    private int rowIndex;
    /**
     * 起始列下表
     */
    private int colIndex;
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

    public int getRowIndex() {
        return rowIndex;
    }

    public Node setRowIndex(int rowIndex) {
        this.rowIndex = rowIndex;
        return this;
    }

    public int getColIndex() {
        return colIndex;
    }

    public Node setColIndex(int colIndex) {
        this.colIndex = colIndex;
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
