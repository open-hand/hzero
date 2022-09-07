package org.hzero.swagger.app;

import java.util.Map;

import org.hzero.swagger.api.dto.swagger.ControllerDTO;

import io.choerodon.core.domain.Page;
import io.choerodon.mybatis.pagehelper.domain.PageRequest;

/**
 * @author superlee
 */
public interface ApiService {

    Page<ControllerDTO> getControllers(String name, String version, PageRequest pageRequest, Map<String, Object> map);

}
