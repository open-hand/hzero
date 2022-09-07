package org.hzero.boot.imported.api.dto;

/**
 * description
 *
 * @author shuangfei.zhu@hand-china.com 2018/12/19 16:41
 */

public class ImportDTO {

    private String batch;

    private String status;
    private Integer dataCount;
    private String statusMeaning;

    public String getBatch() {
        return batch;
    }

    public ImportDTO setBatch(String batch) {
        this.batch = batch;
        return this;
    }

    public String getStatus() {
        return status;
    }

    public ImportDTO setStatus(String status) {
        this.status = status;
        return this;
    }

    public Integer getDataCount() {
        return dataCount;
    }

    public ImportDTO setDataCount(Integer dataCount) {
        this.dataCount = dataCount;
        return this;
    }

    public String getStatusMeaning() {
        return statusMeaning;
    }

    public ImportDTO setStatusMeaning(String statusMeaning) {
        this.statusMeaning = statusMeaning;
        return this;
    }

    @Override
    public String toString() {
        return "ImportDTO{" +
                "batch='" + batch + '\'' +
                ", status='" + status + '\'' +
                ", dataCount=" + dataCount +
                ", statusMeaning='" + statusMeaning + '\'' +
                '}';
    }
}
