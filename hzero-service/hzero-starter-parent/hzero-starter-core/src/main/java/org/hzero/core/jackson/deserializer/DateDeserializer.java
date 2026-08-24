package org.hzero.core.jackson.deserializer;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.BeanProperty;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.deser.ContextualDeserializer;
import io.choerodon.core.oauth.CustomUserDetails;
import io.choerodon.core.oauth.DetailsHelper;
import org.hzero.core.jackson.JacksonConstant;
import org.hzero.core.jackson.annotation.IgnoreTimeZone;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

/**
 * <p>
 * 时间反序列化，按照用户首选项设置的时区对时间进行转换
 * 如果属性被标记为忽略时区，则不做时区转换
 * </p>
 *
 * @author qingsheng.chen 2018/8/27 星期一 9:57
 * @see IgnoreTimeZone
 */
public class DateDeserializer extends JsonDeserializer<Date> implements ContextualDeserializer {
    private static final Logger LOGGER = LoggerFactory.getLogger(DateDeserializer.class);
    private boolean ignoreTimeZone = false;
    private DateTimeFormat dateTimeFormat;

    @Override
    public Date deserialize(JsonParser jsonParser, DeserializationContext deserializationContext) throws IOException {
        try {
            String dateString = jsonParser.getValueAsString();
            if (StringUtils.isEmpty(dateString)) {
                return null;
            }
            SimpleDateFormat dateFormatGmt = new SimpleDateFormat(getFormat());
            if (ignoreTimeZone) {
                return dateFormatGmt.parse(dateString);
            }
            CustomUserDetails details = DetailsHelper.getUserDetails();
            if (details != null && details.getTimeZone() != null) {
                dateFormatGmt.setTimeZone(TimeZone.getTimeZone(details.getTimeZone()));
            }
            return dateFormatGmt.parse(dateString);
        } catch (Exception e) {
            LOGGER.warn("date format error", e);
            return null;
        }
    }

    private String getFormat() {
        if (dateTimeFormat != null && StringUtils.hasText(dateTimeFormat.pattern())) {
            return dateTimeFormat.pattern();
        }
        return JacksonConstant.DEFAULT_DATE_FORMAT;
    }

    @Override
    public JsonDeserializer<?> createContextual(DeserializationContext ctxt, BeanProperty property) {
        return new DateDeserializer()
                .setIgnoreTimeZone(property != null && property.getMember().hasAnnotation(IgnoreTimeZone.class))
                .setDateTimeFormat(property != null ? property.getAnnotation(DateTimeFormat.class) : null);
    }

    private DateDeserializer setIgnoreTimeZone(boolean ignoreTimeZone) {
        this.ignoreTimeZone = ignoreTimeZone;
        return this;
    }

    public DateDeserializer setDateTimeFormat(DateTimeFormat dateTimeFormat) {
        this.dateTimeFormat = dateTimeFormat;
        return this;
    }
}
