package org.hzero.export.filler;

import java.util.List;

import org.apache.poi.ss.usermodel.Workbook;

import org.hzero.export.constant.FileType;
import org.hzero.export.vo.ExportColumn;

/**
 * excel 生成器
 *
 * @author XCXCXCXCX 2020/7/21 2:25
 */
public interface IExcelFiller {

    /**
     * 记录excel数据限制
     *
     * @param singleMaxPage 单excel最大sheet数
     * @param singleMaxRow  单sheet页最大行数
     * @param fileType      文件类型
     */
    void configure(int singleMaxPage, int singleMaxRow, FileType fileType);

    /**
     * 获取生成器类型
     *
     * @return 生成器类型
     */
    String getFillerType();

    /**
     * 填充标题
     */
    void fillTitle();

    /**
     * 填充数据
     *
     * @param data 数据
     */
    void fillData(List<?> data);

    /**
     * 获取工作簿
     *
     * @return excel工作簿
     */
    List<Workbook> getWorkbooks();

    /**
     * 获取列定义
     *
     * @return 列定义
     */
    ExportColumn getRootExportColumn();
}
