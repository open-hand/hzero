package org.hzero.iam.infra.feign;

import org.hzero.common.HZeroService;
import org.hzero.core.util.Pair;
import org.hzero.iam.domain.vo.DataPermissionRangeVO;
import org.hzero.iam.domain.vo.DataPermissionRuleVO;
import org.hzero.iam.infra.feign.fallback.PermissionRuleFeignFallbackImpl;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.Collection;
import java.util.List;

/**
 * 单据规则feign调用
 *
 * @author min.wang01@hand-china.com 2018/08/15 11:55
 */
@FeignClient(value = HZeroService.Platform.NAME, fallback = PermissionRuleFeignFallbackImpl.class)
public interface PermissionRuleFeignClient {

    /**
     * 批量保存数据权限
     *
     * @param tenantId           租户ID
     * @param dataPermissionList 数据权限列表
     * @return 数据权限列表
     */
    @PostMapping("/v1/{organizationId}/permission-rules/permission-ranges/save")
    ResponseEntity<String> save(@PathVariable("organizationId") Long tenantId,
                                @RequestBody List<Pair<DataPermissionRangeVO, DataPermissionRuleVO>> dataPermissionList);


    /**
     * 批量禁用数据权限
     *
     * @param tenantId                  租户ID
     * @param disablePermissionRuleList 禁用权限规则列表
     */
    @DeleteMapping("/v1/{organizationId}/permission-rules/permission-ranges/disable")
    void disablePermission(@PathVariable("organizationId") Long tenantId,
                           @RequestBody List<Long> disablePermissionRuleList);

    /**
     * 禁用数据权限
     *
     * @param disableRel 禁用数据权限列表
     */
    @DeleteMapping("/v1/{organizationId}/permission-rules/permission-ranges/rel/disable")
    void disableRel(@PathVariable("organizationId") Long tenantId,
                    @RequestBody Collection<Pair<Long, Long>> disableRel);
}
