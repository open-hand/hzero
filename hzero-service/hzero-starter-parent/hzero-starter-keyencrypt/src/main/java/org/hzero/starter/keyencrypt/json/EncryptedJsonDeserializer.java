package org.hzero.starter.keyencrypt.json;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.BeanProperty;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.deser.ContextualDeserializer;
import com.google.common.base.Objects;
import org.hzero.starter.keyencrypt.core.Encrypt;
import org.hzero.starter.keyencrypt.core.EncryptContext;
import org.hzero.starter.keyencrypt.core.IEncryptionService;

import java.io.IOException;
import java.util.Arrays;

/**
 * Implementation of  {@link JsonDeserializer} to supply a callback
 * that can be used to create contextual (context-dependent) instances of
 * deserializer - Useful for annotation handling
 *
 * @author xiangyu.qi01@hand-china.com on 2019-11-29.
 */
public class EncryptedJsonDeserializer extends JsonDeserializer<Object> implements ContextualDeserializer {
    private final IEncryptionService service;
    private final JsonDeserializer<Object> baseDeserializer;
    private final BeanProperty property;
    private final Encrypt encrypt;


    public EncryptedJsonDeserializer(final IEncryptionService service, final JsonDeserializer<Object> wrapped, final BeanProperty property, Encrypt encrypt) {
        this.service = service;
        this.baseDeserializer = wrapped;
        this.property = property;
        this.encrypt = encrypt;
    }

    /**
     * {@inheritDoc}
     * <p>
     * Encrypted field deserializer
     */
    @Override
    public Object deserialize(final JsonParser parser, final DeserializationContext context) throws IOException {
        switch (EncryptContext.encryptType()) {
            case ENCRYPT:
                String value = parser.getValueAsString();
                if (Arrays.stream(encrypt.ignoreValue()).noneMatch(item -> Objects.equal(item, value))) {
                    return service.decrypt(parser, baseDeserializer, context, property != null ? property.getType() : null, encrypt);
                }
                break;
            case TO_STRING:
            default:
                break;
        }
        if (baseDeserializer != null) {
            return baseDeserializer.deserialize(parser, context);
        }
        return context.findRootValueDeserializer(property.getType()).deserialize(parser, context);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public JsonDeserializer<?> createContextual(final DeserializationContext context, final BeanProperty property) {
        return new EncryptedJsonDeserializer(service, baseDeserializer, property, encrypt);
    }
}
