package org.hzero.export;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;

/**
 * @author XCXCXCXCX
 * @date 2020/7/21 2:18 下午
 */
public interface IExcelExporter extends AutoCloseable {

    String TXT_SUFFIX = ".txt";
    String ZIP_SUFFIX = ".zip";
    String EXCEL_SUFFIX = ".xlsx";

    void fillTitle();

    void fillSheet(List<?> exportData);

    void setError(String errorMessage);

    String getError();

    InputStream export() throws IOException;

    byte[] exportBytes() throws IOException;

    void export(OutputStream outputStream) throws IOException;

    String getTitle();

    String getOutputFileSuffix();

}
