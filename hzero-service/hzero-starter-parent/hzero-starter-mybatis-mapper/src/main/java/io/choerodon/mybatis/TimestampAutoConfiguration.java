package io.choerodon.mybatis;

import oracle.sql.TIMESTAMP;
import org.hzero.mybatis.serializer.TimestampSerializerPostProcess;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author qingsheng.chen@hand-china.com
 */
@Configuration
@ConditionalOnClass(TIMESTAMP.class)
public class TimestampAutoConfiguration {
    @Bean
    public TimestampSerializerPostProcess timestampSerializerPostProcess() {
        return new TimestampSerializerPostProcess();
    }
}
