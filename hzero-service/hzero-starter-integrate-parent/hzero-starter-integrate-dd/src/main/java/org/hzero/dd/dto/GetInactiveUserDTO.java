package org.hzero.dd.dto;

/**
 * Created by tx on 2019/11/24 14:23
 */

public class GetInactiveUserDTO {
    private String query_date;
    private Long offset;
    private Long size;

    public String getQuery_date() {
        return query_date;
    }

    public void setQuery_date(String query_date) {
        this.query_date = query_date;
    }

    public Long getOffset() {
        return offset;
    }

    public void setOffset(Long offset) {
        this.offset = offset;
    }

    public Long getSize() {
        return size;
    }

    public void setSize(Long size) {
        this.size = size;
    }
}
