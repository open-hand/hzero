package io.choerodon.core.oauth.resource;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.choerodon.core.oauth.CustomUserDetails;
import io.choerodon.core.oauth.DetailsHelper;

/**
 * 根据用户的时区序列化时间
 *
 * @author wuguokai
 */
public class DateSerializer extends JsonSerializer<Date> {
    private static final Logger LOGGER = LoggerFactory.getLogger(DateSerializer.class);

    @Override
    public void serialize(Date date, JsonGenerator jsonGenerator, SerializerProvider serializerProvider)
            throws IOException {
        try {
            SimpleDateFormat dateFormatGmt = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            CustomUserDetails details = DetailsHelper.getUserDetails();
            if (details != null && details.getTimeZone() != null) {
                dateFormatGmt.setTimeZone(TimeZone.getTimeZone(details.getTimeZone()));
            }
            jsonGenerator.writeString(dateFormatGmt.format(date));
        } catch (Exception e) {
            LOGGER.warn("date format error", e);
            jsonGenerator.writeNull();
        }
    }
}
