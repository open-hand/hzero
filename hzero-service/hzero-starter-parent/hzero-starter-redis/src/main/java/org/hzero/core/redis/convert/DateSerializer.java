package org.hzero.core.redis.convert;

import java.io.IOException;
import java.util.Date;

import org.apache.commons.lang3.time.FastDateFormat;
import org.hzero.core.redis.RedisHelper;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

/**
 * <p>
 * 带时区的序列化
 * </p>
 *
 * @author qingsheng.chen 2018/9/4 星期二 19:01
 */
public class DateSerializer extends JsonSerializer<Date> {

    private static final FastDateFormat DATE_FORMAT = FastDateFormat.getInstance(RedisHelper.DATE_FORMAT);

    @Override
    public void serialize(Date date, JsonGenerator jsonGenerator, SerializerProvider serializers) throws IOException {
        jsonGenerator.writeString(DATE_FORMAT.format(date));
    }
}
