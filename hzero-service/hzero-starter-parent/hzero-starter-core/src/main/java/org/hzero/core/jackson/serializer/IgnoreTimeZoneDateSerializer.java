package org.hzero.core.jackson.serializer;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.hzero.core.jackson.JacksonConstant;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

/**
 * <p>
 * 忽略时区时间序列化
 * </p>
 *
 * @author qingsheng.chen 2018/8/9 星期四 13:33
 */
public class IgnoreTimeZoneDateSerializer extends JsonSerializer<Date> {

    @Override
    public void serialize(Date date, JsonGenerator jsonGenerator, SerializerProvider serializers) throws IOException {
        SimpleDateFormat dateFormatGmt = new SimpleDateFormat(JacksonConstant.DEFAULT_DATE_FORMAT);
        jsonGenerator.writeString(dateFormatGmt.format(date));
    }
}
