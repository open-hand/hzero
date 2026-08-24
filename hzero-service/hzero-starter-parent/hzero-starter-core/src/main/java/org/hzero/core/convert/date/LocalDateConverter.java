package org.hzero.core.convert.date;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import org.springframework.core.convert.converter.Converter;
import org.springframework.util.StringUtils;

/**
 * <p>
 * Local Date 自定义转换
 * </p>
 *
 * @author qingsheng.chen 2018/9/30 星期日 10:22
 */
public class LocalDateConverter implements Converter<String, LocalDate> {
    private static final String DATE_FORMAT = "yyyy-MM-dd";

    @Override
    public LocalDate convert(String localDate) {
        if (!StringUtils.hasText(localDate)) {
            return null;
        }
        return LocalDate.parse(localDate, DateTimeFormatter.ofPattern(DATE_FORMAT));
    }
}
