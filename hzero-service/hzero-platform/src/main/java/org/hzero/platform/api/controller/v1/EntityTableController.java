package org.hzero.platform.api.controller.v1;

import io.choerodon.core.domain.Page;
import io.choerodon.mybatis.pagehelper.annotation.SortDefault;
import io.choerodon.mybatis.pagehelper.domain.PageRequest;
import io.choerodon.swagger.annotation.Permission;
import io.swagger.annotations.ApiOperation;
import org.hzero.boot.platform.entity.dto.RegistParam;
import org.hzero.core.base.BaseController;
import org.hzero.core.util.Results;
import org.hzero.platform.app.service.EntityTableService;
import org.hzero.platform.domain.entity.EntityColumn;
import org.hzero.platform.domain.entity.EntityTable;
import org.hzero.platform.domain.repository.EntityColumnRepository;
import org.hzero.platform.domain.repository.EntityTableRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

import java.util.List;

/**
 * 实体表 管理 API
 *
 * @author xingxing.wu@hand-china.com 2019-07-08 15:56:36
 */
@RestController("entityTableController.v1")
@RequestMapping("/v1/entity-tables")
public class EntityTableController extends BaseController {
    @Autowired
    private EntityTableService entityTableService;
    @Autowired
    private EntityTableRepository entityTableRepository;
    @Autowired
    private EntityColumnRepository entityColumnRepository;

    @ApiOperation(value = "实体注册")
    @Permission(permissionWithin = true)
    @PostMapping("/register")
    public ResponseEntity register(@RequestBody RegistParam param) {
        entityTableService.doRegist(param);
        return Results.success();
    }

    @ApiOperation(value = "分页查询实体(内部调用)")
    @Permission(permissionWithin = true)
    @GetMapping
    public ResponseEntity<Page<EntityTable>> pageRegistryEntity(EntityTable entityTable,
                                                                @ApiIgnore @SortDefault(value = EntityTable.FIELD_ENTITY_TABLE_ID) PageRequest pageRequest) {
        return Results.success(entityTableRepository.pageEntityTable(pageRequest, entityTable));
    }

    @ApiOperation(value = "查询实体详情(内部调用)")
    @Permission(permissionWithin = true)
    @GetMapping("/{entityTableId}")
    public ResponseEntity<EntityTable> detailEntityTable(@PathVariable("entityTableId") Long entityTableId) {
        return Results.success(entityTableRepository.selectByPrimaryKey(entityTableId));
    }

    @ApiOperation(value = "查询实体字段列表内部调用(内部调用)")
    @Permission(permissionWithin = true)
    @GetMapping("/{entityTableId}/column")
    public ResponseEntity<List<EntityColumn>> pageRegistryEntityColumn(@PathVariable("entityTableId") Long entityTableId) {
        EntityColumn entityColumn = new EntityColumn();
        entityColumn.setEntityTableId(entityTableId);
        return Results.success(entityColumnRepository.select(entityColumn));
    }

    @ApiOperation(value = "查询实体字段详情(内部调用)")
    @Permission(permissionWithin = true)
    @GetMapping("/{entityTableId}/column/{entityColumnId}")
    public ResponseEntity<EntityColumn> detailEntityTableColumn(@PathVariable("entityTableId") Long entityTableId,
                                                                @PathVariable("entityColumnId") Long entityColumnId) {
        return Results.success(entityColumnRepository.selectByPrimaryKey(entityColumnId));
    }

}
