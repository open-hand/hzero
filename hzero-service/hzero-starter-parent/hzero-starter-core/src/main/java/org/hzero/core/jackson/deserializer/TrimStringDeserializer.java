package org.hzero.core.jackson.deserializer;

import java.io.IOException;

import org.hzero.core.jackson.annotation.Trim;
import org.hzero.core.util.TrimUtils;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.BeanProperty;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.deser.ContextualDeserializer;
import com.fasterxml.jackson.databind.deser.std.StringDeserializer;

/**
 * <p>
 * 过滤空格序列化
 * </p>
 *
 * @author qingsheng.chen 2018/10/16 星期二 13:53
 */
public class TrimStringDeserializer extends JsonDeserializer<String> implements ContextualDeserializer {
    private Trim trim;

    public TrimStringDeserializer setTrim(Trim trim) {
        this.trim = trim;
        return this;
    }

    @Override
    public String deserialize(JsonParser jsonParser, DeserializationContext ctxt) throws IOException {
        return TrimUtils.trim(trim, jsonParser.getValueAsString());
    }

    @Override
    public JsonDeserializer<?> createContextual(DeserializationContext ctxt, BeanProperty property) {
        if (property != null && property.getMember() != null && property.getMember().hasAnnotation(Trim.class)){
            return new TrimStringDeserializer().setTrim(property.getMember().getAnnotation(Trim.class));
        }
        return new StringDeserializer();
    }
}
