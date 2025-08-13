package org.hzero.export.annotation;

import java.lang.annotation.*;
import javax.servlet.http.HttpServletResponse;

import io.choerodon.mybatis.pagehelper.domain.PageRequest;

import org.hzero.export.constant.CodeRender;
import org.hzero.export.constant.ExportConstants;
import org.hzero.export.constant.ExportType;
import org.hzero.export.constant.FileType;
import org.hzero.export.vo.ExportParam;

/**
 * 将该注解加在请求数据的接口上：<p></p>
 * <p>
 * 接口方法必须带有 {@link HttpServletResponse} 参数，将通过 {@link HttpServletResponse#getWriter()} 返回数据 <p></p>
 * 接口方法必须带有 {@link ExportParam} 参数：
 *  <ul>
 *      <li>通过 {@link ExportParam#fillerType} 指定导出方式</li>
 *      <li>通过 {@link ExportParam#exportType} 指定导出类型</li>
 *      <ul>
 *          <li>{@link ExportType#COLUMN} 查询导出的列</li>
 *          <li>{@link ExportType#DATA} 导出数据</li>
 *          <li>{@link ExportType#TEMPLATE} 导出模板</li>
 *      </ul>
 *      <li>通过 {@link ExportParam#ids} 或者 {@link ExportParam#columns} 传入选择导出的列</li>
 *  </ul>
 * 接口方法最好带有分页参数 {@link PageRequest}，支持分页查询数据，从而避免大数据量导致内存溢出 <p></p>
 *
 * @author bojiangzhou 2018/07/25
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface ExcelExport {

    /**
     * 导出对象
     */
    Class<?> value() default Object.class;

    /**
     * 分组标识 (处理多个导出使用同一DTO但导出列不同的情况)
     */
    Class<?>[] groups() default {};

    /**
     * 导入模板编码
     */
    String templateCode() default "";

    /**
     * 允许导出的最大数据量 0表示不限制
     */
    long maxDataCount() default 0L;

    /**
     * 默认导出文件名称
     */
    String defaultFileName() default "";

    /**
     * 字段编码渲染方式，按照对应导入模板的解析模式选择，导出文件不会用作导入选择CodeRender.NONE
     * CodeRender.NONE  不渲染
     * CodeRender.HEAD  数据页顶部渲染
     * CodeRender.SHEET  隐藏sheet页渲染
     */
    CodeRender codeRenderMode() default CodeRender.NONE;

    /**
     * 允许的数据填充方式
     */
    String[] fillType() default {ExportConstants.FillerType.SINGLE, ExportConstants.FillerType.MULTI};

    /**
     * 允许的文件导出格式
     */
    FileType[] exportFileType() default {FileType.EXCEL_2003, FileType.EXCEL_2007, FileType.CSV};

    /**
     * 异步阈值(优先级高于 hzero.export.asyncThreshold 配置)
     */
    int asyncThreshold() default 0;
}
