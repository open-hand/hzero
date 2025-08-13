package org.hzero.export;

import java.util.Arrays;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.annotation.Order;
import org.springframework.util.Assert;

import io.choerodon.core.exception.CommonException;
import io.choerodon.core.exception.ExceptionResponse;

import org.hzero.export.annotation.ExcelExport;
import org.hzero.export.constant.ExportType;
import org.hzero.export.constant.FileType;
import org.hzero.export.util.ResponseWriter;
import org.hzero.export.vo.ExportColumn;
import org.hzero.export.vo.ExportParam;

/**
 * 拦截 {@link ExcelExport}，处理导出列或导出数据
 *
 * @author bojiangzhou 2018/07/25
 */
@Aspect
@Order(10)
public class ExcelExportAop {

    private static final Logger LOGGER = LoggerFactory.getLogger(ExcelExportAop.class);

    private final ExportDataHelper exportDataHelper;
    private final ExportColumnHelper exportColumnHelper;

    public ExcelExportAop(ExportDataHelper exportDataHelper,
                          ExportColumnHelper exportColumnHelper) {
        this.exportDataHelper = exportDataHelper;
        this.exportColumnHelper = exportColumnHelper;
    }

    @Around(value = "@annotation(excelExport)")
    public Object excelExport(ProceedingJoinPoint joinPoint, ExcelExport excelExport) throws Throwable {
        HttpServletResponse response = null;
        ExportParam exportParam = null;
        Object[] args = joinPoint.getArgs();

        for (Object arg : args) {
            if (arg instanceof HttpServletResponse) {
                response = (HttpServletResponse) arg;
            } else if (arg instanceof ExportParam) {
                exportParam = (ExportParam) arg;
            }
        }

        if (exportParam == null || exportParam.getExportType() == null || !ExportType.match(exportParam.getExportType())) {
            return joinPoint.proceed();
        }
        if (!ExportType.COLUMN.equals(exportParam.getExportType())) {
            // 校验导出文件格式
            Assert.isTrue(Arrays.asList(excelExport.exportFileType()).contains(FileType.of(exportParam.getFileType())), "Unsupported file type.");
            // 校验文件填充方式
            Assert.isTrue(Arrays.asList(excelExport.fillType()).contains(exportParam.getFillerType()), "Unsupported fill type.");
        }
        Assert.notNull(response, "HttpServletResponse must not be null.");

        if (ExportType.COLUMN.equals(exportParam.getExportType())) {
            ExportColumn exportColumn = exportColumnHelper.getExportColumn(excelExport, exportParam);
            ResponseWriter.write(response, exportColumn);
        } else {
            boolean emptyColumns = CollectionUtils.isEmpty(exportParam.getIds()) && CollectionUtils.isEmpty(exportParam.getColumns());
            if (StringUtils.isBlank(excelExport.templateCode()) && emptyColumns) {
                ExceptionResponse exceptionResponse = new ExceptionResponse("export.column.least-one");
                LOGGER.warn(exceptionResponse.getMessage());
                ResponseWriter.write(response, exceptionResponse);
                return null;
            }

            try {

                doExportByType(exportParam, joinPoint, excelExport, response);

            } catch (CommonException e) {
                LOGGER.warn("excel export error.", e);
                ExceptionResponse exceptionResponse = new ExceptionResponse(e.getCode());
                ResponseWriter.write(response, exceptionResponse);
            } catch (Exception e) {
                LOGGER.warn("excel export error.", e);
                ExceptionResponse exceptionResponse = new ExceptionResponse("export.error");
                exceptionResponse.setException(e.getMessage());
                ResponseWriter.write(response, exceptionResponse);
            }
        }
        return null;
    }

    private void doExportByType(ExportParam exportParam, ProceedingJoinPoint joinPoint, ExcelExport excelExport, HttpServletResponse response) {
        switch (exportParam.getExportType()) {
            case DATA:
                exportDataHelper.exportFile(joinPoint, excelExport, response, FileType.of(exportParam.getFileType()));
                break;
            case TEMPLATE:
                exportDataHelper.exportTemplate(joinPoint, excelExport, response, FileType.of(exportParam.getFileType()));
                break;
            case COLUMN:
            default:
                break;
        }
    }
}