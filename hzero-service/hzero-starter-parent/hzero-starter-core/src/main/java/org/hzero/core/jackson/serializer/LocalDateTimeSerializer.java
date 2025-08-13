package org.hzero.core.jackson.serializer;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.BeanProperty;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.ContextualSerializer;
import io.choerodon.core.oauth.CustomUserDetails;
import io.choerodon.core.oauth.DetailsHelper;
import org.hzero.core.jackson.JacksonConstant;
import org.hzero.core.jackson.annotation.IgnoreTimeZone;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

/**
 * <p>
 * 时间序列化，按照用户首选项设置的时区对时间进行转换
 * 如果属性被标记为忽略时区，则不做时区转换
 *
 * @author qingsheng.chen 2018/8/27 星期一 9:27
 * @see IgnoreTimeZone
 * </p>
 */
public class LocalDateTimeSerializer extends JsonSerializer<LocalDateTime> implements ContextualSerializer {
    private boolean ignoreTimeZone = false;
    private JsonFormat format;

    @Override
    public void serialize(LocalDateTime date, JsonGenerator jsonGenerator, SerializerProvider serializerProvider) throws IOException {
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern(getFormat());
        if (!ignoreTimeZone) {
            if (format != null && !JsonFormat.DEFAULT_TIMEZONE.equals(format.timezone())) {
                dateTimeFormatter.withZone(ZoneId.of(format.timezone()));
            } else {
                CustomUserDetails details = DetailsHelper.getUserDetails();
                if (details != null && details.getTimeZone() != null) {
                    dateTimeFormatter.withZone(ZoneId.of(details.getTimeZone()));
                }
            }
        }
        jsonGenerator.writeString(dateTimeFormatter.format(date));
    }

    private String getFormat() {
        if (format != null && StringUtils.hasText(format.pattern())) {
            return format.pattern();
        }
        return JacksonConstant.DEFAULT_DATE_FORMAT;
    }


    private LocalDateTimeSerializer setIgnoreTimeZone(boolean ignoreTimeZone) {
        this.ignoreTimeZone = ignoreTimeZone;
        return this;
    }

    public LocalDateTimeSerializer setFormat(JsonFormat format) {
        this.format = format;
        return this;
    }

    @Override
    public JsonSerializer<?> createContextual(SerializerProvider prov, BeanProperty property) {
        return new LocalDateTimeSerializer()
                .setIgnoreTimeZone(property != null && property.getMember().hasAnnotation(IgnoreTimeZone.class))
                .setFormat(property != null ? property.getAnnotation(JsonFormat.class) : null);
    }
}
