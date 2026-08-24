package io.choerodon.core.domain;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.util.AbstractList;
import java.util.ArrayList;
import java.util.List;


/**
 * Created by xausky on 3/16/17.
 */
@JsonFormat(shape = JsonFormat.Shape.OBJECT)
public class Page<E> extends AbstractList<E> {
    private int totalPages;
    private long totalElements;
    private int numberOfElements;
    private int size;
    private int number;
    private List<E> content;

    public Page() {
        content = new ArrayList<>();
    }

    /**
     * 分页封装对象构造函数
     *
     * @param content  content
     * @param pageInfo pageInfo
     * @param total    total
     */
    public Page(List<E> content, PageInfo pageInfo, long total) {
        this.content = content;
        this.number = pageInfo.getPage();
        this.size = pageInfo.getSize();
        this.totalElements = total;
        this.totalPages = (size > 0 ? ((int) (total - 1) / size + 1) : 0);
        this.numberOfElements = content.size();

    }

    public int getTotalPages() {
        return totalPages;
    }

    public void setTotalPages(int totalPages) {
        this.totalPages = totalPages;
    }

    public long getTotalElements() {
        return totalElements;
    }

    public void setTotalElements(long totalElements) {
        this.totalElements = totalElements;
    }

    public int getNumberOfElements() {
        return numberOfElements;
    }

    public void setNumberOfElements(int numberOfElements) {
        this.numberOfElements = numberOfElements;
    }

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }

    public int getNumber() {
        return number;
    }

    public void setNumber(int number) {
        this.number = number;
    }

    public List<E> getContent() {
        return content;
    }

    public void setContent(List<E> content) {
        this.content = content;
    }

    @Override
    public E get(int i) {
        return content.get(i);
    }

    @Override
    public int size() {
        return content.size();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        if (!super.equals(o)) {
            return false;
        }

        Page<?> page = (Page<?>) o;

        if (totalPages != page.totalPages) {
            return false;
        }
        if (totalElements != page.totalElements) {
            return false;
        }
        if (numberOfElements != page.numberOfElements) {
            return false;
        }
        if (size != page.size) {
            return false;
        }
        if (number != page.number) {
            return false;
        }
        return content != null ? content.equals(page.content) : page.content == null;
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + totalPages;
        result = 31 * result + (int) (totalElements ^ (totalElements >>> 32));
        result = 31 * result + numberOfElements;
        result = 31 * result + size;
        result = 31 * result + number;
        result = 31 * result + (content != null ? content.hashCode() : 0);
        return result;
    }
}
