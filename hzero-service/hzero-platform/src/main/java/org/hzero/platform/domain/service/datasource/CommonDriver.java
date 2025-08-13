package org.hzero.platform.domain.service.datasource;

import java.util.HashMap;
import java.util.Map;

import org.hzero.platform.infra.constant.HpfmMsgCodeConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import io.choerodon.core.exception.CommonException;

/**
 * 通用驱动
 *
 * @author xiaoyu.zhao@hand-china.com
 */
@Component
public class CommonDriver implements Driver {

    private static final Map<String, Connection> CONNECTION_MAP = new HashMap<>();
    private static final Logger LOGGER = LoggerFactory.getLogger(CommonDriver.class);

    @Override
    public void register(String type, Connection connection) {
        if (CONNECTION_MAP.containsKey(type)) {
            LOGGER.error("Connection type {} exists", type);
            throw new CommonException(HpfmMsgCodeConstants.ERROR_DATASOURCE_REGISTER_FAIL);
        }
        LOGGER.info("Register Connection type: {}", type);
        CONNECTION_MAP.put(type, connection);
    }

    @Override
    public Connection getConnection(String type) {
        Connection connection = CONNECTION_MAP.get(type);
        if (connection == null) {
            LOGGER.error("No such connection type: {}", type);
            throw new CommonException(HpfmMsgCodeConstants.ERROR_GET_CONNECTION_FAIL, type);
        }
        return connection;
    }

}
