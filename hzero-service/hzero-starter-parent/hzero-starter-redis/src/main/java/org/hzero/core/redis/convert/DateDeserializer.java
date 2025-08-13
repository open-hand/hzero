package org.hzero.core.redis.convert;

import java.io.IOException;
import java.text.ParseException;
import java.util.Date;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.apache.commons.lang3.time.FastDateFormat;
import org.hzero.core.redis.RedisHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * <p>
 * 带时区的反序列化
 * </p>
 *
 * @author qingsheng.chen 2018/9/4 星期二 19:01
 */
public class DateDeserializer extends JsonDeserializer<Date> {
    private static final Logger logger = LoggerFactory.getLogger(DateDeserializer.class);

    private static final FastDateFormat DATE_FORMAT = FastDateFormat.getInstance(RedisHelper.DATE_FORMAT);

    @Override
    public Date deserialize(JsonParser jsonParser, DeserializationContext ctxt) throws IOException {
        try {
            return DATE_FORMAT.parse(jsonParser.getValueAsString());
        } catch (ParseException e) {
            logger.warn("date format error : {}", ExceptionUtils.getStackTrace(e));
            return null;
        }
    }
}
