package org.hzero.file.infra.feign.fallback;

import java.util.List;

import org.hzero.file.infra.feign.PlatformRemoteService;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

/**
 * description
 *
 * @author shuangfei.zhu@hand-china.com 2019/07/05 13:39
 */
@Component
public class PlatformRemoteServiceImpl implements PlatformRemoteService {


    @Override
    public ResponseEntity<String> listByServerIds(Long organizationId, List<Long> serverIdList) {
        return null;
    }

    @Override
    public ResponseEntity<String> listByClusterIds(Long organizationId, List<Long> clustersIdList) {
        return null;
    }
}
