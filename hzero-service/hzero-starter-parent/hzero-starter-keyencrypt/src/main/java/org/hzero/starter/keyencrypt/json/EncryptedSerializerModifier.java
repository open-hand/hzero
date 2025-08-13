package org.hzero.starter.keyencrypt.json;

import com.fasterxml.jackson.databind.BeanDescription;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializationConfig;
import com.fasterxml.jackson.databind.ser.BeanPropertyWriter;
import com.fasterxml.jackson.databind.ser.BeanSerializerModifier;
import org.hzero.starter.keyencrypt.core.Encrypt;
import org.hzero.starter.keyencrypt.core.IEncryptionService;

import java.util.ArrayList;
import java.util.List;

/**
 * @author xiangyu.qi01@hand-china.com on 2019-11-29.
 */
public class EncryptedSerializerModifier extends BeanSerializerModifier {

    private final IEncryptionService encryptionService;

    /**
     * Constructor
     *
     * @param encryptionService Encryption services to use to handle {@link Encrypt} marked fields
     */
    public EncryptedSerializerModifier(final IEncryptionService encryptionService) {
        this.encryptionService = encryptionService;
    }

    /**
     * {@inheritDoc}
     * <p>
     * Add serialization functionality for {@link Encrypt} marked fields
     */
    @Override
    public List<BeanPropertyWriter> changeProperties(final SerializationConfig config, final BeanDescription beanDescription, final List<BeanPropertyWriter> beanProperties) {
        List<BeanPropertyWriter> newWriters = new ArrayList<>();

        for (final BeanPropertyWriter writer : beanProperties) {
            Encrypt encrypt = writer.getAnnotation(Encrypt.class);
            if (null == encrypt) {
                newWriters.add(writer);
            } else {
                try {
                    JsonSerializer<Object> encryptSer = new EncryptedJsonSerializer(encryptionService, writer.getSerializer(), encrypt, writer.getType());
                    newWriters.add(new EncryptedPropertyWriter(writer, encryptSer));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        return newWriters;
    }

    static class EncryptedPropertyWriter extends BeanPropertyWriter {
        EncryptedPropertyWriter(final BeanPropertyWriter base, final JsonSerializer<Object> deserializer) {
            super(base);
            this._serializer = deserializer;
        }
    }
}


