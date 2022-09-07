package org.hzero.export.annotation;

import java.lang.annotation.*;

import javax.servlet.http.HttpServletResponse;

import org.hzero.export.constant.ExportType;
import org.hzero.export.vo.ExportParam;

import io.choerodon.mybatis.pagehelper.domain.PageRequest;

/**
 * 将该注解加在请求数据的接口上：<p></p>
 *
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
 *      <li>通过 {@link ExportParam#ids} 传入选择导出的列</li>
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
    Class<?> value();

    /**
     * 分组标识
     */
    Class<?>[] groups() default {};

}
