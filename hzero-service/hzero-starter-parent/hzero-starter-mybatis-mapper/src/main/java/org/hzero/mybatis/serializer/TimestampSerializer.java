package org.hzero.mybatis.serializer;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import oracle.sql.TIMESTAMP;
import org.hzero.core.jackson.serializer.DateSerializer;

import java.io.IOException;
import java.sql.SQLException;

/**
 * @author qingsheng.chen@hand-china.com
 */
public class TimestampSerializer extends JsonSerializer<TIMESTAMP> {

    private DateSerializer dateSerializer = new DateSerializer();

    @Override
    public void serialize(TIMESTAMP value, JsonGenerator gen, SerializerProvider serializers) throws IOException {
        try {
            dateSerializer.serialize(value.dateValue(), gen, serializers);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
