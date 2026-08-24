package org.hzero.report.infra.engine.data;

/**
 * 表格|图形报表类
 *
 * @author xianzhi.chen@hand-china.com 2018年10月17日下午8:10:37
 */
public class ReportTable {

    private String htmlText;
    private String sqlText;
    private int metaDataRowCount;
    private int metaDataColumnCount;
    private int metaDataPageSize;
    private long metaDataRowTotal;

    public ReportTable(String htmlText, String sqlText, int metaDataRowCount, int metaDataColumnCount,
                       int metaDataPageSize, long metaDataRowTotal) {
        this.htmlText = htmlText;
        this.sqlText = sqlText;
        this.metaDataRowCount = metaDataRowCount;
        this.metaDataColumnCount = metaDataColumnCount;
        this.metaDataPageSize = metaDataPageSize;
        this.metaDataRowTotal = metaDataRowTotal;
    }

    public String getHtmlText() {
        return this.htmlText;
    }

    public String getSqlText() {
        return this.sqlText;
    }

    public long getMetaDataRowCount() {
        return this.metaDataRowCount;
    }

    public int getMetaDataColumnCount() {
        return this.metaDataColumnCount;
    }

    public int getMetaDataPageSize() {
        return metaDataPageSize;
    }

    public long getMetaDataRowTotal() {
        return metaDataRowTotal;
    }

}
