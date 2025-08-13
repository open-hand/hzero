package org.hzero.config.domain.service;

import org.hzero.config.domain.vo.Config;

/**
 * @author flyleft
 */

public interface PullConfigService {

    Config getConfig(String serviceName, String label);

}
