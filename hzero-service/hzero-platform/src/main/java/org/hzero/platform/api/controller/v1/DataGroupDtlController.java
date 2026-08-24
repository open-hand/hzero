package org.hzero.platform.api.controller.v1;

import java.util.List;

import org.hzero.core.base.BaseController;
import org.hzero.core.util.Results;
import org.hzero.platform.config.PlatformSwaggerApiConfig;
import org.hzero.platform.domain.entity.DataGroupDtl;
import org.hzero.platform.domain.repository.DataGroupDtlRepository;
import org.hzero.starter.keyencrypt.core.Encrypt;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import springfox.documentation.annotations.ApiIgnore;

import io.choerodon.core.domain.Page;
import io.choerodon.core.iam.ResourceLevel;
import io.choerodon.mybatis.pagehelper.annotation.SortDefault;
import io.choerodon.mybatis.pagehelper.domain.PageRequest;
import io.choerodon.mybatis.pagehelper.domain.Sort;
import io.choerodon.swagger.annotation.Permission;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

/**
 * 数据组明细定义 管理 API
 *
 * @author jianbo.li@hand-china.com 2019-07-17 15:48:48
 */
@Api(tags = PlatformSwaggerApiConfig.DATA_GROUP_LINE_DTL)
@RestController("dataGroupDtlController.v1")
@RequestMapping("/v1/{organizationId}/data-dtls")
public class DataGroupDtlController extends BaseController {

    @Autowired
    private DataGroupDtlRepository dataGroupDtlRepository;


    @ApiOperation(value = "数据组明细定义列表")
    @Permission(level = ResourceLevel.ORGANIZATION)
    @GetMapping("/{dataLineId}")
    public ResponseEntity<Page<DataGroupDtl>> list(@PathVariable("organizationId") Long tenantId,
                                                   @PathVariable("dataLineId") @Encrypt Long dataLineId,
                                                   @Encrypt DataGroupDtl dataGroupDtl,
                                                   @ApiIgnore @SortDefault(value = DataGroupDtl.FIELD_GROUP_DTL_ID, direction = Sort.Direction.DESC) PageRequest pageRequest) {
        dataGroupDtl.setTenantId(tenantId);
        dataGroupDtl.setGroupLineId(dataLineId);
        Page<DataGroupDtl> list = dataGroupDtlRepository.pageDataGroupDtl(pageRequest, dataGroupDtl);
        return Results.success(list);
    }

    @ApiOperation(value = "创建数据组明细定义")
    @Permission(level = ResourceLevel.ORGANIZATION)
    @PostMapping("/{dataLineId}")
    public ResponseEntity<List<DataGroupDtl>> create(@PathVariable("organizationId") Long tenantId,
                                                     @PathVariable("dataLineId") @Encrypt Long dataLineId,
                                                     @RequestBody @Encrypt List<DataGroupDtl> dataGroupDtlList) {
        dataGroupDtlList.forEach(item -> {
            item.setTenantId(tenantId);
            item.setGroupLineId(dataLineId);
        });
        validList(dataGroupDtlList);
        dataGroupDtlRepository.createDataGroupDtl(dataGroupDtlList);
        return Results.success(dataGroupDtlList);
    }


    @ApiOperation(value = "删除数据组明细定义")
    @Permission(level = ResourceLevel.ORGANIZATION)
    @DeleteMapping("/{dataLineId}")
    public ResponseEntity<?> remove(@PathVariable("dataLineId") @Encrypt Long dataLineId, @RequestBody @Encrypt List<DataGroupDtl> dataGroupDtlList) {
        dataGroupDtlRepository.deleteDataGroupDtl(dataGroupDtlList);
        return Results.success();
    }

}
