package org.hzero.platform.api.controller.v1;

import io.choerodon.core.domain.Page;
import io.choerodon.core.iam.ResourceLevel;
import io.choerodon.mybatis.pagehelper.annotation.SortDefault;
import io.choerodon.mybatis.pagehelper.domain.PageRequest;
import io.choerodon.mybatis.pagehelper.domain.Sort;
import io.choerodon.swagger.annotation.Permission;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.hzero.core.base.BaseController;
import org.hzero.core.util.Results;
import org.hzero.platform.config.PlatformSwaggerApiConfig;
import org.hzero.platform.domain.entity.DataGroup;
import org.hzero.platform.domain.entity.Lov;
import org.hzero.platform.domain.entity.LovValue;
import org.hzero.platform.domain.repository.LovRepository;
import org.hzero.platform.domain.repository.LovValueRepository;
import org.hzero.starter.keyencrypt.core.Encrypt;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import springfox.documentation.annotations.ApiIgnore;

/**
 * @author jianbo.li
 * @date 2019/7/25 10:45
 */
@Api(tags = PlatformSwaggerApiConfig.DATA_GROUP_DIMENSION)
@RestController("dataGroupDimensionController.v1")
@RequestMapping("/v1/{organizationId}/data-group-dimension")
public class DataGroupDimensionController extends BaseController {
    @Autowired
    private LovRepository lovRepository;
    @Autowired
    private LovValueRepository lovValueRepository;

    @ApiOperation(value = "分页查询维度")
    @Permission(level = ResourceLevel.ORGANIZATION)
    @GetMapping
    public ResponseEntity<Page<Lov>> list(@PathVariable("organizationId") Long tenantId,
                                          Lov record,
                                          @ApiIgnore @SortDefault(value = Lov.FIELD_LOV_ID, direction = Sort.Direction.DESC) PageRequest pageRequest) {
        record.setTenantId(tenantId);
        return Results.success(lovRepository.pageLovForDataGroupDimension(record, pageRequest));
    }

    @ApiOperation(value = "分页查询维度值")
    @Permission(level = ResourceLevel.ORGANIZATION)
    @GetMapping("/{dimensionId}")
    public ResponseEntity<Page<LovValue>> listDimensionValue(@PathVariable("organizationId") Long tenantId,
                                                         @PathVariable("dimensionId") @Encrypt Long lovId,
                                                         @ApiIgnore @SortDefault(value = LovValue.FIELD_ORDER_SEQ, direction = Sort.Direction.ASC) PageRequest pageRequest) {
        Page<LovValue> result = this.lovValueRepository.pageAndSortByLovIdForDataGroup(pageRequest, lovId, tenantId);
        return Results.success(result);
    }
}
