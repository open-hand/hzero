package org.hzero.export;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import io.choerodon.core.exception.CommonException;

import org.hzero.export.filler.CsvFiller;
import org.hzero.export.filler.ExcelFiller;
import org.hzero.export.filler.ICsvFiller;
import org.hzero.export.filler.IExcelFiller;
import org.hzero.export.vo.ExportColumn;
import org.hzero.export.vo.ExportProperty;

/**
 * Filler holder
 *
 * @author bojiangzhou 2018/08/08
 */
public class FillerHolder implements ApplicationContextAware {

    private static final Map<String, ExcelFiller> EXCEL_FILLER_HOLDER = new ConcurrentHashMap<>();
    private static final Map<String, CsvFiller> CSV_FILLER_HOLDER = new ConcurrentHashMap<>();

    private ApplicationContext context;

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) {
        this.context = applicationContext;
    }

    public void init() {
        context.getBeansOfType(ExcelFiller.class).forEach((k, filler) -> EXCEL_FILLER_HOLDER.put(filler.getFillerType(), filler));
        context.getBeansOfType(CsvFiller.class).forEach((k, filler) -> CSV_FILLER_HOLDER.put(filler.getFillerType(), filler));
    }

    public static IExcelFiller getExcelFiller(String fillerType, ExportColumn exportColumn, ExportProperty exportProperty) {
        ExcelFiller filler = EXCEL_FILLER_HOLDER.get(fillerType);
        try {
            // 使用两个参数的构造
            return filler.getClass().getConstructor(ExportColumn.class, ExportProperty.class).newInstance(exportColumn, exportProperty);
        } catch (Exception e) {
            try {
                // 兼容老版本，使用一个参数的构造
                return filler.getClass().getConstructor(ExportColumn.class).newInstance(exportColumn);
            } catch (Exception exception) {
                throw new CommonException("not found", exception);
            }
        }
    }

    public static ICsvFiller getCsvFiller(String fillerType, ExportColumn exportColumn, ExportProperty exportProperty) {
        CsvFiller filler = CSV_FILLER_HOLDER.get(fillerType);
        try {
            return filler.getClass().getConstructor(ExportColumn.class, ExportProperty.class).newInstance(exportColumn, exportProperty);
        } catch (Exception e) {
            throw new CommonException("not found", e);
        }
    }
}