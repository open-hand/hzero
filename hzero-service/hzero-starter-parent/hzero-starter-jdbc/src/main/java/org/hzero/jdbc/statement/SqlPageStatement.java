package org.hzero.jdbc.statement;


/**
 * SQL分页参数信息
 *
 * @author xianzhi.chen@hand-china.com 2018年12月17日下午6:56:05
 */
public class SqlPageStatement {

    private int page;
    private int size;
    private int begin;
    private int end;
    private Integer max;
    private boolean count = false;

    public SqlPageStatement() {

    }

    public SqlPageStatement(int maxRows, int page, int size, boolean count) {
        this.page = page;
        // 最大分页条数不允许大于最大限定值
        this.size = Math.min(size, maxRows);
        this.begin = page * this.size;
        this.end = begin + this.size;
        this.count = count;
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

    public int getBegin() {
        return begin;
    }

    public void setBegin(int begin) {
        this.begin = begin;
    }

    public int getEnd() {
        return end;
    }

    public void setEnd(int end) {
        this.end = end;
    }

    public boolean isCount() {
        return count;
    }

    public void setCount(boolean count) {
        this.count = count;
    }

    public Integer getMax() {
        return max;
    }

    public SqlPageStatement setMax(Integer max) {
        this.max = max;
        return this;
    }
}
