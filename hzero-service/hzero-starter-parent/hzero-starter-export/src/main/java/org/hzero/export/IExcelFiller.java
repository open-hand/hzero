package org.hzero.export;

import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.hzero.export.vo.ExportColumn;

import java.util.List;

/**
 * @author XCXCXCXCX
 * @date 2020/7/21 2:25 下午
 */
public interface IExcelFiller {

    void configure(int singleExcelMaxSheetNum, int singleSheetMaxRow);

    String getFillerType();

    void fillTitle();

    void fillData(List<?> data);

    List<SXSSFWorkbook> getWorkbooks();

    ExportColumn getRootExportColumn();
}
