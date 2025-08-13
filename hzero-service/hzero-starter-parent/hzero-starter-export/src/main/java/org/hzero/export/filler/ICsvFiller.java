package org.hzero.export.filler;

import java.util.List;

import org.hzero.export.constant.FileType;
import org.hzero.export.entity.Csv;
import org.hzero.export.entity.CsvGroup;
import org.hzero.export.vo.ExportColumn;

/**
 * csv 生成器
 *
 * @author shuangfei.zhu@hand-china.com 2021/08/10 9:47
 */
public interface ICsvFiller {

    /**
     * 记录csv数据限制
     *
     * @param singleMaxPage 单组最大页数
     * @param singleMaxRow  单页最大行数
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
     * 获取csvGroup
     *
     * @return csvGroup
     */
    List<CsvGroup> getCsvGroups();

    /**
     * 获取csvGroup
     *
     * @return csvGroup
     */
    List<Csv> getCsv();

    /**
     * 获取列定义
     *
     * @return 列定义
     */
    ExportColumn getRootExportColumn();
}
