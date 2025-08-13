package io.choerodon.core.domain;

import java.io.Serializable;

/**
 * Created by xausky on 3/15/17.
 */
public class PageInfo implements Serializable {

    private int page;

    private int size;

    private int begin;

    private int end;

    private long total;

    private int pages;

    private boolean count = true;

    public PageInfo(int page, int size) {
        this(page, size, true);
    }

    /**
     * 分页信息类构造函数
     *
     * @param page  page
     * @param size  size
     * @param count count
     */
    public PageInfo(int page, int size, boolean count) {
        this.page = page;
        this.size = size;
        this.begin = page * size;
        this.end = begin + size;
        this.count = count;
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

    public boolean isCount() {
        return count;
    }

    public void setCount(boolean count) {
        this.count = count;
    }

    public long getTotal() {
        return total;
    }

    public void setTotal(long total) {
        this.total = total;
    }

    public int getPages() {
        return pages;
    }

    public void setPages(int pages) {
        this.pages = pages;
    }

}