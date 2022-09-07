package org.hzero.export;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.hzero.export.filler.ExcelFiller;
import org.hzero.export.vo.ExportColumn;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import io.choerodon.core.exception.CommonException;

/**
 * Filler holder
 *
 * @author bojiangzhou 2018/08/08
 */
public class ExcelFillerHolder implements ApplicationContextAware {

    private static final Map<String, ExcelFiller> FILLER_HOLDER = new ConcurrentHashMap<>();

    private ApplicationContext context;

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) {
        this.context = applicationContext;
    }

    public void init() {
        Map<String, ExcelFiller> beans = context.getBeansOfType(ExcelFiller.class);
        beans.forEach((k, filler) -> FILLER_HOLDER.put(filler.getFillerType(), filler));
    }

    public static IExcelFiller getExcelFiller(String fillerType, ExportColumn exportColumn) {
        ExcelFiller filler = FILLER_HOLDER.get(fillerType);
        try {
            return filler.getClass().getConstructor(ExportColumn.class).newInstance(exportColumn);
        } catch (Exception e) {
            throw new CommonException("not found", e);
        }
    }
}