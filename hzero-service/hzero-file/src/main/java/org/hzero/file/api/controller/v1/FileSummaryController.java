package org.hzero.file.api.controller.v1;

import org.hzero.boot.platform.lov.annotation.ProcessLovValue;
import org.hzero.core.base.BaseConstants;
import org.hzero.core.base.BaseController;
import org.hzero.core.util.Results;
import org.hzero.core.util.ValidUtils;
import org.hzero.file.api.dto.FileParamsDTO;
import org.hzero.file.config.FileSwaggerApiConfig;
import org.hzero.file.domain.entity.File;
import org.hzero.file.domain.repository.FileRepository;
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
import io.choerodon.swagger.annotation.CustomPageRequest;
import io.choerodon.swagger.annotation.Permission;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

/**
 * 文件汇总查询接口类
 *
 * @author xianzhi.chen@hand-china.com 2018年6月22日下午4:36:37
 */
@Api(tags = FileSwaggerApiConfig.FILE_SUMMARY_LIST)
@RestController("fileSummaryController.v1")
@RequestMapping("/v1/{organizationId}/files/summary")
public class FileSummaryController extends BaseController {

    private final FileRepository fileRepository;

    @Autowired
    public FileSummaryController(FileRepository fileRepository) {
        this.fileRepository = fileRepository;
    }

    @ApiOperation(value = "获取文件列表汇总")
    @Permission(level = ResourceLevel.ORGANIZATION)
    @ProcessLovValue(targetField = BaseConstants.FIELD_BODY)
    @GetMapping
    @CustomPageRequest
    public ResponseEntity<Page<File>> pageFileList(@PathVariable("organizationId") Long organizationId, FileParamsDTO fileParamsDTO,
                                                   @ApiIgnore @SortDefault(value = File.FIELD_FILE_ID, direction = Sort.Direction.DESC) PageRequest pageRequest) {
        fileParamsDTO.setTenantId(organizationId);
        if (fileParamsDTO.getFromCreateDate() != null && fileParamsDTO.getToCreateDate() != null) {
            ValidUtils.isSameOrAfterDay(fileParamsDTO.getFromCreateDate(), fileParamsDTO.getToCreateDate());
        }
        return Results.success(fileRepository.pageFileList(fileParamsDTO, pageRequest));
    }
}
