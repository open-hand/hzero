package org.hzero.iam.infra.feign.fallback;

import org.hzero.core.util.Pair;
import org.hzero.iam.domain.vo.DataPermissionRangeVO;
import org.hzero.iam.domain.vo.DataPermissionRuleVO;
import org.hzero.iam.infra.feign.PermissionRuleFeignClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.List;

/**
 * 数据权限回调
 *
 * @author min.wang01@hand-china.com 2018/08/15 13:47
 */
@Component
public class PermissionRuleFeignFallbackImpl implements PermissionRuleFeignClient {
    private static final Logger logger = LoggerFactory.getLogger(PermissionRuleFeignFallbackImpl.class);


    @Override
    public ResponseEntity<String> save(Long tenantId, List<Pair<DataPermissionRangeVO, DataPermissionRuleVO>> dataPermissionList) {
        logger.info("Error save permission {}", dataPermissionList);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("[]");
    }

    @Override
    public void disablePermission(Long tenantId, List<Long> disablePermissionRuleList) {
        logger.info("Error disable permission {}", disablePermissionRuleList);
    }

    @Override
    public void disableRel(Long tenantId, Collection<Pair<Long, Long>> disableRel) {
        logger.info("Error disable rel {}", disableRel);
    }
}
