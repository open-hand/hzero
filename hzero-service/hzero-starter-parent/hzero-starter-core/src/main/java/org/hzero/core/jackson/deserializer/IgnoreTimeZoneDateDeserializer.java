package org.hzero.core.jackson.deserializer;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import org.hzero.core.jackson.JacksonConstant;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * <p>
 * 忽略时区时间反序列化
 * </p>
 *
 * @author qingsheng.chen 2018/8/9 星期四 13:38
 */
public class IgnoreTimeZoneDateDeserializer extends JsonDeserializer<Date> {
    private static final Logger logger = LoggerFactory.getLogger(IgnoreTimeZoneDateDeserializer.class);

    @Override
    public Date deserialize(JsonParser jsonParser, DeserializationContext deserializationContext) throws IOException {
        SimpleDateFormat dateFormatGmt = new SimpleDateFormat(JacksonConstant.DEFAULT_DATE_FORMAT);
        try {
            String dateString = jsonParser.getValueAsString();
            if (StringUtils.isEmpty(dateString)) {
                return null;
            }
            return dateFormatGmt.parse(dateString);
        } catch (ParseException e) {
            logger.warn("date format error : {}", e);
            return null;
        }
    }
}
