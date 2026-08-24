package org.hzero.admin.domain.repository;

import io.choerodon.core.domain.Page;
import org.hzero.admin.domain.entity.ServiceVersion;
import org.hzero.mybatis.base.BaseRepository;
import io.choerodon.mybatis.pagehelper.domain.PageRequest;

/**
 * 服务版本资源库
 *
 * @author shuangfei.zhu@hand-china.com 2019-08-23 14:35:21
 */
public interface ServiceVersionRepository extends BaseRepository<ServiceVersion> {

    Page<ServiceVersion> page(PageRequest pageRequest, ServiceVersion queryParam);

}
