package org.hzero.file.api.controller.v1;

import java.util.Date;

import org.hzero.boot.platform.lov.annotation.ProcessLovValue;
import org.hzero.core.base.BaseConstants;
import org.hzero.core.base.BaseController;
import org.hzero.core.util.Results;
import org.hzero.file.config.FileSwaggerApiConfig;
import org.hzero.file.domain.entity.FileEditLog;
import org.hzero.file.domain.repository.FileEditLogRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import springfox.documentation.annotations.ApiIgnore;

import io.choerodon.core.domain.Page;
import io.choerodon.core.iam.ResourceLevel;
import io.choerodon.mybatis.pagehelper.annotation.SortDefault;
import io.choerodon.mybatis.pagehelper.domain.PageRequest;
import io.choerodon.mybatis.pagehelper.domain.Sort;
import io.choerodon.swagger.annotation.Permission;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;

/**
 * 文件编辑日志 管理 API
 *
 * @author shuangfei.zhu@hand-china.com 2019-06-28 11:36:32
 */
@Api(tags = FileSwaggerApiConfig.FILE_EDIT_LOG)
@RestController("fileEditLogController.v1")
@RequestMapping("/v1/{organizationId}/file-edit-logs")
public class FileEditLogController extends BaseController {

    private final FileEditLogRepository fileEditLogRepository;

    @Autowired
    public FileEditLogController(FileEditLogRepository fileEditLogRepository) {
        this.fileEditLogRepository = fileEditLogRepository;
    }

    @ApiOperation(value = "文件编辑日志列表")
    @Permission(level = ResourceLevel.ORGANIZATION)
    @GetMapping
    @ApiImplicitParams({
            @ApiImplicitParam(name = "fileName", value = "文件名", paramType = "query"),
            @ApiImplicitParam(name = "realName", value = "提交人", paramType = "query"),
            @ApiImplicitParam(name = "editType", value = "编辑类型", paramType = "query"),
            @ApiImplicitParam(name = "changeDateFrom", value = "变更时间从", paramType = "query"),
            @ApiImplicitParam(name = "changeDateTo", value = "变更时间至", paramType = "query")
    })
    @ProcessLovValue(targetField = BaseConstants.FIELD_BODY)
    public ResponseEntity<Page<FileEditLog>> list(@PathVariable Long organizationId, String fileName, String realName, String editType, Date changeDateFrom, Date changeDateTo,
                                                  @ApiIgnore @SortDefault(value = FileEditLog.FIELD_LOG_ID, direction = Sort.Direction.DESC) PageRequest pageRequest) {
        return Results.success(fileEditLogRepository.pageEditLog(pageRequest, organizationId, fileName, realName, editType, changeDateFrom, changeDateTo));
    }
}
