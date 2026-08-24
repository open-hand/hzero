package org.hzero.excel.helper;

import java.io.File;
import java.io.InputStream;
import java.util.List;

import com.alibaba.fastjson.JSONObject;
import com.monitorjbl.xlsx.StreamingReader;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.hzero.excel.config.ExcelConfig;
import org.hzero.excel.entity.Column;
import org.hzero.excel.service.ExcelReadListener;
import org.hzero.excel.supporter.ExcelReader;
import org.hzero.excel.supporter.ExcelWriter;

import io.choerodon.core.convertor.ApplicationContextHelper;

/**
 * excel工具方法
 *
 * @author shuangfei.zhu@hand-china.com 2020/02/03 20:56
 */
public class ExcelHelper {

    private ExcelHelper() {
    }

    /**
     * 读取excel文件
     *
     * @param file     文件
     * @param listener 数据处理类
     * @param names    字段名称
     * @param sheetNo  sheet页 0开始
     * @param startNo  起始行 0开始
     */
    public static void read(File file, ExcelReadListener listener, List<Column> names, Integer sheetNo, Integer startNo) {
        ExcelConfig excelConfig = ApplicationContextHelper.getContext().getBean(ExcelConfig.class);
        Workbook workbook = StreamingReader.builder()
                .rowCacheSize(excelConfig.getCacheSize())
                // 读取资源时，缓存到内存的字节大小，默认是1024
                .bufferSize(excelConfig.getBufferSize())
                // 打开资源，必须，可以是InputStream或者是File，注意：只能打开XLSX格式的文件
                .open(file);
        readDate(workbook, listener, names, sheetNo, startNo);
    }

    /**
     * 读取excel文件
     *
     * @param inputStream 文件输入流
     * @param listener    数据处理类
     * @param names       字段名称
     * @param sheetNo     sheet页 0开始
     * @param startNo     起始行 0开始
     */
    public static void read(InputStream inputStream, ExcelReadListener listener, List<Column> names, Integer sheetNo, Integer startNo) {
        ExcelConfig excelConfig = ApplicationContextHelper.getContext().getBean(ExcelConfig.class);
        Workbook workbook = StreamingReader.builder()
                .rowCacheSize(excelConfig.getCacheSize())
                // 读取资源时，缓存到内存的字节大小，默认是1024
                .bufferSize(excelConfig.getBufferSize())
                // 打开资源，必须，可以是InputStream或者是File，注意：只能打开XLSX格式的文件
                .open(inputStream);
        readDate(workbook, listener, names, sheetNo, startNo);
    }

    private static void readDate(Workbook workbook, ExcelReadListener listener, List<Column> names, Integer sheetNo, Integer startNo) {
        Sheet sheet = workbook.getSheetAt(sheetNo);
        ExcelReader excelReader = new ExcelReader();
        int index = -1;
        listener.onStart();
        for (Row row : sheet) {
            index++;
            if (index < startNo) {
                continue;
            }
            JSONObject object = excelReader.readDataRow(row, names);
            if (!object.isEmpty()) {
                listener.invoke(object);
            }
        }
        listener.onFinish();
    }

    /**
     * 写excel
     */
    public static ExcelWriter write() {
        return ExcelWriter.createExcel();
    }

    /**
     * 写excel
     */
    public static ExcelWriter write(ExcelWriter.ExcelVersion excelVersion) {
        return ExcelWriter.createExcel(excelVersion);
    }

    /**
     * 写excel
     */
    public static ExcelWriter write(ExcelWriter.ExcelVersion excelVersion, int rowAccessWindowSize) {
        return ExcelWriter.createExcel(excelVersion, rowAccessWindowSize);
    }
}
