package org.hzero.starter.keyencrypt.json;

import java.io.IOException;
import java.io.StringWriter;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.type.CollectionType;
import com.google.common.base.Objects;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.reflect.FieldUtils;
import org.hzero.starter.keyencrypt.core.Encrypt;
import org.hzero.starter.keyencrypt.core.EncryptContext;
import org.hzero.starter.keyencrypt.core.EncryptType;
import org.hzero.starter.keyencrypt.core.IEncryptionService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;

/**
 * @author xiangyu.qi01@hand-china.com on 2019-11-29.
 */
public class EncryptedJsonSerializer extends JsonSerializer<Object> {
    private static final Logger logger = LoggerFactory.getLogger(EncryptedJsonSerializer.class);
    private final JsonSerializer<Object> baseSerializer;
    private final IEncryptionService encryptionService;
    private final Encrypt encrypt;
    private final JavaType javaType;

    public EncryptedJsonSerializer(IEncryptionService encryptionService,
                                   JsonSerializer<Object> baseSerializer,
                                   Encrypt encrypt,
                                   JavaType javaType) {
        this.baseSerializer = baseSerializer;
        this.encryptionService = encryptionService;
        this.encrypt = encrypt;
        this.javaType = javaType;
    }


    @Override
    public void serialize(final Object object, final JsonGenerator generator, final SerializerProvider provider) throws IOException {
        switch (EncryptContext.encryptType()) {
            case ENCRYPT:
            case TO_STRING:
                if (Arrays.stream(encrypt.ignoreValue()).noneMatch(item -> Objects.equal(item, String.valueOf(object)))) {
                    serializeWithEncrypt(object, generator, provider, EncryptContext.encryptType());
                    return;
                }
                break;
            default:
                break;
        }
        if (null == baseSerializer) {
            provider.defaultSerializeValue(object, generator);
        } else {
            baseSerializer.serialize(object, generator, provider);
        }
    }

    private void serializeWithEncrypt(final Object object, final JsonGenerator generator, final SerializerProvider provider, EncryptType encryptType) throws IOException {
        Object obj = object;
        Object currentObj = generator.getCurrentValue();
        if (obj instanceof Collection) {
            Class<?> elementType = javaType instanceof CollectionType ? javaType.getContentType().getRawClass() : Object.class;
            if (BeanUtils.isSimpleValueType(elementType)) {
                writeSimpleCollection((Collection) obj, generator, provider, encryptType, currentObj);
            } else {
                logger.error("@Encrypt annotation is only allowed to add to simple attributes(including its array or collection), current : {} > {}", currentObj.getClass(), obj.getClass());
                writeUnsupported(obj, generator);
            }
        } else if (obj.getClass().isArray()) {
            Class<?> elementType = obj.getClass().getComponentType();
            if (BeanUtils.isSimpleValueType(elementType)) {
                writeSimpleArray(obj, generator, provider, encryptType, currentObj);
            } else {
                logger.error("@Encrypt annotation is only allowed to add to simple attributes(including its array or collection), current : {} > {}", currentObj.getClass(), obj.getClass());
                writeUnsupported(obj, generator);
            }
        } else {
            if (BeanUtils.isSimpleValueType(obj.getClass())) {
                if (EncryptType.TO_STRING.equals(encryptType)) {
                    generator.writeObject(String.valueOf(object));
                } else {
                    generator.writeObject(innerEncrypt(object, generator, provider, currentObj));
                }
            } else {
                logger.error("@Encrypt annotation is only allowed to add to simple attributes(including its array or collection), current : {} > {}", currentObj.getClass(), obj.getClass());
                writeUnsupported(obj, generator);
            }
        }
    }

    private void writeUnsupported(Object obj, JsonGenerator generator) throws IOException {
        generator.writeObject(obj);
    }

    private void writeSimpleCollection(Collection obj, JsonGenerator generator, SerializerProvider provider, EncryptType encryptType, Object currentObj) throws IOException {
        List<String> result = new ArrayList<>();
        for (Object child : obj) {
            if (EncryptType.TO_STRING.equals(encryptType)) {
                result.add(String.valueOf(child));
            } else {
                result.add(innerEncrypt(child, generator, provider, currentObj));
            }
        }
        generator.writeObject(result);
    }

    private void writeSimpleArray(Object obj, JsonGenerator generator, SerializerProvider provider, EncryptType encryptType, Object currentObj) throws IOException {
        List<String> result = new ArrayList<>();
        int length = Array.getLength(obj);
        for (int i = 0; i < length; i++) {
            if (EncryptType.TO_STRING.equals(encryptType)) {
                result.add(String.valueOf(Array.get(obj, i)));
            } else {
                result.add(innerEncrypt(Array.get(obj, i), generator, provider, currentObj));
            }
        }
        generator.writeObject(result);
    }

    protected String innerEncrypt(Object object, final JsonGenerator generator, final SerializerProvider provider, Object bean) throws IOException {
        StringWriter writer = new StringWriter();
        JsonGenerator nestedGenerator = generator.getCodec().getFactory().createGenerator(writer);

        if (null == baseSerializer) {
            provider.defaultSerializeValue(object, nestedGenerator);
        } else {
            baseSerializer.serialize(object, nestedGenerator, provider);
        }
        nestedGenerator.close();
        String id = writer.getBuffer().toString().replace("\"", "");
        String encryptKey = encrypt.value();
        if (StringUtils.isNotBlank(encrypt.fieldName())) {
            try {
                encryptKey = FieldUtils.readField(bean, encrypt.fieldName(), true).toString();
            } catch (IllegalAccessException e) {
                //
            }
        }
        return encryptionService.encrypt(id, encryptKey);
    }
}
