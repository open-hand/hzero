package org.hzero.swagger.infra.util;

import org.hzero.swagger.infra.feign.ConfigServerClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * 配置刷新操作
 *
 * @author bojiangzhou
 * @author wuguokai
 */
@Component
public class RefreshUtil {
    private static final Logger LOGGER = LoggerFactory.getLogger(RefreshUtil.class);

    private final ExecutorService asyncExecutor = Executors.newSingleThreadExecutor();

    @Autowired
    private ConfigServerClient configServerClient;

    /**
     * 通知config-server刷新配置
     */
    public void refreshRoute() {
        LOGGER.debug("Notify gateway refresh route.");
        asyncExecutor.submit(() -> {
            ResponseEntity<String> responseEntity = configServerClient.refreshRoute();
            if (responseEntity.getStatusCode().isError()) {
                LOGGER.error("Error notify gateway refresh route {}:{}.", responseEntity.getStatusCodeValue(), responseEntity.getBody());
            }
        });
    }
}
