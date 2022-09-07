package org.hzero.boot.imported.domain.vo;

import org.apache.commons.lang3.StringUtils;
import org.hzero.boot.imported.infra.enums.DataStatus;

/**
 * description
 *
 * @author shuangfei.zhu@hand-china.com 2020/04/10 11:02
 */
public class DataContext {

    private Long id;
    private String batch;
    private String templateCode;
    private Integer sheetIndex;
    private DataStatus dataStatus;
    private String errorMsg;
    private String data;
    private String backInfo;

    @Deprecated
    public Long getId() {
        return id;
    }

    @Deprecated
    public DataContext setId(Long id) {
        this.id = id;
        return this;
    }

    @Deprecated
    public String getBatch() {
        return batch;
    }

    @Deprecated
    public DataContext setBatch(String batch) {
        this.batch = batch;
        return this;
    }

    @Deprecated
    public String getTemplateCode() {
        return templateCode;
    }

    @Deprecated
    public DataContext setTemplateCode(String templateCode) {
        this.templateCode = templateCode;
        return this;
    }

    @Deprecated
    public Integer getSheetIndex() {
        return sheetIndex;
    }

    @Deprecated
    public DataContext setSheetIndex(Integer sheetIndex) {
        this.sheetIndex = sheetIndex;
        return this;
    }

    @Deprecated
    public DataStatus getDataStatus() {
        return dataStatus;
    }

    @Deprecated
    public DataContext setDataStatus(DataStatus dataStatus) {
        this.dataStatus = dataStatus;
        return this;
    }

    public String getErrorMsg() {
        return errorMsg;
    }

    @Deprecated
    public DataContext setErrorMsg(String errorMsg) {
        this.errorMsg = errorMsg;
        return this;
    }

    /**
     * 追加错误信息
     */
    @Deprecated
    public void addErrorMsg(String errorMsg) {
        this.errorMsg = StringUtils.isBlank(this.errorMsg) ? errorMsg : this.errorMsg + ";" + errorMsg;
    }

    @Deprecated
    public String getData() {
        return data;
    }

    @Deprecated
    public DataContext setData(String data) {
        this.data = data;
        return this;
    }

    @Deprecated
    public String getBackInfo() {
        return backInfo;
    }

    @Deprecated
    public DataContext setBackInfo(String backInfo) {
        this.backInfo = backInfo;
        return this;
    }

    @Deprecated
    @Override
    public String toString() {
        return "DataContext{" +
                "id=" + id +
                ", batch='" + batch + '\'' +
                ", templateCode='" + templateCode + '\'' +
                ", sheetIndex=" + sheetIndex +
                ", dataStatus=" + dataStatus +
                ", errorMsg='" + errorMsg + '\'' +
                ", data='" + data + '\'' +
                ", backInfo='" + backInfo + '\'' +
                '}';
    }
}
