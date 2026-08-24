package org.hzero.config.domain.service.impl;

import io.choerodon.core.exception.CommonException;
import org.hzero.config.app.service.ServiceConfigService;
import org.hzero.config.domain.service.PullConfigService;
import org.hzero.config.domain.vo.Config;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author flyleft
 */
@Service
public class PullConfigServiceImpl implements PullConfigService {

    private static final Logger LOGGER = LoggerFactory.getLogger(PullConfigService.class);

    @Autowired
    private ServiceConfigService configService;

    @Override
    public Config getConfig(String serviceName, String label) {
        Config config = configService.selectConfig(serviceName, label);

        if (config == null || config.getName() == null) {
            LOGGER.warn("error.pullConfig serviceName {} configVersion {}", serviceName, label);
            throw new CommonException("error.pullConfig serviceName " + serviceName + " configVersion " + label);
        } else {
            LOGGER.debug("pullConfig success, serviceName {} configVersion{} config {}", serviceName, label, config);
        }
        return config;
    }

}
