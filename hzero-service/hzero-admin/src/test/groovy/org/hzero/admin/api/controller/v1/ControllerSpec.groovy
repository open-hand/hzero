package org.hzero.admin.api.controller.v1

import com.fasterxml.jackson.databind.ObjectMapper
import org.hzero.admin.AdminApplication
import org.springframework.boot.test.context.SpringBootContextLoader
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.context.annotation.Configuration
import org.springframework.test.context.ContextConfiguration
import org.springframework.transaction.annotation.Transactional
import spock.lang.Specification

/**
 * @author XCXCXCXCX* @date 2020/6/22 7:59 下午
 */
@SpringBootTest(
        classes = AdminApplication.class,
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT
)
@ContextConfiguration(loader = SpringBootContextLoader.class)
@Configuration
@Transactional
class ControllerSpec extends Specification {

    protected static final ObjectMapper objectMapper = new ObjectMapper();

    static {
        System.setProperty("HZERO_METRIC_SYNC_ENABLE", "false");
        System.setProperty("HZERO_TRACE_REDIS_REPORTER_ENABLE", "false");
        System.setProperty("LOG_LEVEL", "info");
    }

}
