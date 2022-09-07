package io.choerodon.core.oauth.resource;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.choerodon.core.oauth.CustomUserDetails;
import io.choerodon.core.oauth.DetailsHelper;


/**
 * 根据用户的时区反序列化时间
 *
 * @author wuguokai
 */
public class DateDeserializer extends JsonDeserializer<Date> {
    private static final Logger LOGGER = LoggerFactory.getLogger(DateDeserializer.class);

    @Override
    public Date deserialize(JsonParser jsonParser, DeserializationContext deserializationContext) throws IOException {
        try {
            SimpleDateFormat dateFormatGmt = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            CustomUserDetails details = DetailsHelper.getUserDetails();
            if (details != null && details.getTimeZone() != null) {
                dateFormatGmt.setTimeZone(TimeZone.getTimeZone(details.getTimeZone()));
            }
            return dateFormatGmt.parse(jsonParser.getValueAsString());
        } catch (Exception e) {
            LOGGER.warn("date format error", e);
            return null;
        }
    }
}
