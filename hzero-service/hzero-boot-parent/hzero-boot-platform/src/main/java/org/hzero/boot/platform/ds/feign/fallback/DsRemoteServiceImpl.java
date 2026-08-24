package org.hzero.boot.platform.ds.feign.fallback;

import org.hzero.boot.platform.ds.feign.DsRemoteService;
import org.hzero.core.util.Results;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

/**
 * description
 *
 * @author shuangfei.zhu@hand-china.com 2019/05/08 9:52
 */
@Component
public class DsRemoteServiceImpl implements DsRemoteService {

    @Override
    public ResponseEntity<String> getByUnique(Long organizationId, String datasourceCode, String dsPurposeCode) {
        return Results.error();
    }
}
