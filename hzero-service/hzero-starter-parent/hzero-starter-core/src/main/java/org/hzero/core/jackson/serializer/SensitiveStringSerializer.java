package org.hzero.core.jackson.serializer;

import java.io.IOException;

import org.hzero.core.jackson.annotation.Sensitive;
import org.hzero.core.jackson.cache.SensitiveCache;
import org.hzero.core.jackson.sensitive.SensitiveHelper;
import org.hzero.core.util.SensitiveUtils;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.BeanProperty;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.ContextualSerializer;
import com.fasterxml.jackson.databind.ser.std.StringSerializer;

/**
 * <p>
 * 数据加敏
 * </p>
 *
 * @author qingsheng.chen 2018/9/17 星期一 13:53
 */
public class SensitiveStringSerializer extends JsonSerializer<String> implements ContextualSerializer {
    private Sensitive sensitive;

    public SensitiveStringSerializer setSensitive(Sensitive sensitive) {
        this.sensitive = sensitive;
        return this;
    }

    @Override
    public void serialize(String value, JsonGenerator gen, SerializerProvider serializers) throws IOException {
        gen.writeString(SensitiveUtils.generateCipherText(sensitive, value));
    }

    @Override
    public JsonSerializer<?> createContextual(SerializerProvider prov, BeanProperty property) {
        if (property != null && property.getMember() != null
                && SensitiveHelper.isOpen() && property.getMember().hasAnnotation(Sensitive.class)) {
            Sensitive sensitiveAnnotation = property.getMember().getAnnotation(Sensitive.class);
            SensitiveCache.put(property.getMember().getDeclaringClass(), property.getName(), sensitiveAnnotation);
            return new SensitiveStringSerializer().setSensitive(sensitiveAnnotation);
        }
        return new StringSerializer();
    }
}
