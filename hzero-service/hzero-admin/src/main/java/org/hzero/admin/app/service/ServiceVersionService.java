package org.hzero.admin.app.service;

import io.choerodon.core.domain.Page;
import io.choerodon.mybatis.pagehelper.domain.PageRequest;
import org.hzero.admin.domain.entity.ServiceVersion;

/**
 * @author XCXCXCXCX
 * @version 1.2.0
 * @date 2020/2/21 4:40 下午
 */
public interface ServiceVersionService {
    Page<ServiceVersion> page(PageRequest pageRequest, Long serviceId);
}
