package org.hzero.message.infra.feign;

import java.util.List;

import org.hzero.common.HZeroService;
import org.hzero.message.api.dto.UnitUserDTO;
import org.hzero.message.infra.feign.fallback.UnitServiceFallBackImpl;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(value = HZeroService.Platform.NAME, fallback = UnitServiceFallBackImpl.class, path = "/v1")
public interface UnitService {

    /**
     * "批量查询部门下的用户Id及用户所属租户Id-用于消息公告管理界面"
     *
     * @param units 组织列表
     * @return Set<Receiver>
     */
    @PostMapping("/units/user-ids")
    ResponseEntity<String> listUnitUsers(@RequestBody List<UnitUserDTO> units);

    /**
     * "批量查询部门下的用户对应的第三方平台用户ID"
     *
     * @param units             组织列表
     * @param thirdPlatformType 三方平台类型
     * @return Set<Receiver>
     */
    @PostMapping("/units/open/user-ids")
    ResponseEntity<String> listOpenUnitUsers(@RequestBody List<UnitUserDTO> units, @RequestParam("thirdPlatformType") String thirdPlatformType);

}
