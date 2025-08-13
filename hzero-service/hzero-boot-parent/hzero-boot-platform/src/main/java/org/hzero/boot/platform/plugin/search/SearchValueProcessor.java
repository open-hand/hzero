package org.hzero.boot.platform.plugin.search;

import io.choerodon.core.oauth.CustomUserDetails;
import io.choerodon.core.oauth.DetailsHelper;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.TimeZone;
import java.util.function.Function;

public class SearchValueProcessor {
    private static Map<Class<?>, Function<String, ?>> processor = new HashMap<>();
    private static final DateTimeFormatter LOCAL_DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    static {
        processor.put(Byte.class, Byte::parseByte);
        processor.put(Short.class, Short::parseShort);
        processor.put(Integer.class, Integer::parseInt);
        processor.put(Long.class, Long::parseLong);
        processor.put(Float.class, Float::parseFloat);
        processor.put(Double.class, Double::parseDouble);
        processor.put(Character.class, value -> {
            if (value.length() != 1) {
                throw new IllegalArgumentException("String value can't translate to Character.class, value = " + value);
            }
            return value.charAt(0);
        });
        processor.put(String.class, value -> value);
        processor.put(Boolean.class, Boolean::parseBoolean);
        processor.put(Date.class, value -> {
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            CustomUserDetails details = DetailsHelper.getUserDetails();
            if (details != null && details.getTimeZone() != null) {
                dateFormat.setTimeZone(TimeZone.getTimeZone(details.getTimeZone()));
            }
            try {
                return dateFormat.parse(value);
            } catch (ParseException e) {
                throw new IllegalArgumentException("String value can't translate to Date.class, value = " + value);
            }
        });
        processor.put(LocalDate.class, value -> LocalDate.parse(value, LOCAL_DATE_FORMATTER));
    }

    public static Object process(String value, Class<?> type) {
        if (value != null && processor.containsKey(type)) {
            return processor.get(type).apply(value);
        }
        return null;
    }

    public static void setProcessor(Class<?> type, Function<String, ?> valueFunction) {
        processor.put(type, valueFunction);
    }

    public static Map<Class<?>, Function<String, ?>> getProcessor() {
        return processor;
    }
}
