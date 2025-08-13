package org.hzero.scheduler.api.controller.v1;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.hzero.boot.platform.lov.annotation.ProcessLovValue;
import org.hzero.core.base.BaseConstants;
import org.hzero.core.base.BaseController;
import org.hzero.core.util.Results;
import org.hzero.mybatis.helper.SecurityTokenHelper;
import org.hzero.scheduler.config.SchedulerSwaggerApiConfig;
import org.hzero.scheduler.domain.entity.ConcurrentParam;
import org.hzero.scheduler.domain.repository.ConcurrentParamRepository;
import org.hzero.starter.keyencrypt.core.Encrypt;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import io.choerodon.core.iam.ResourceLevel;
import io.choerodon.swagger.annotation.Permission;

/**
 * 并发程序参数 管理 API
 *
 * @author shuangfei.zhu@hand-china.com 2018-09-11 10:20:47
 */
@Api(tags = SchedulerSwaggerApiConfig.CONCURRENT_PARAM_SITE)
@RestController("concurrentParamSiteController.v1")
@RequestMapping("/v1/concurrent-params")
public class ConcurrentParamSiteController extends BaseController {

    private final ConcurrentParamRepository concurrentParamRepository;

    @Autowired
    public ConcurrentParamSiteController(ConcurrentParamRepository concurrentParamRepository) {
        this.concurrentParamRepository = concurrentParamRepository;
    }

    @ApiOperation(value = "并发程序参数列表")
    @Permission(level = ResourceLevel.SITE)
    @GetMapping
    @ProcessLovValue(targetField = BaseConstants.FIELD_BODY)
    public ResponseEntity<List<ConcurrentParam>> listConcurrentParam(@RequestParam @Encrypt Long concurrentId) {
        return Results.success(concurrentParamRepository.select(new ConcurrentParam().setConcurrentId(concurrentId).setEnabledFlag(BaseConstants.Flag.YES)));
    }

    @ApiOperation(value = "删除并发程序参数")
    @Permission(level = ResourceLevel.SITE)
    @DeleteMapping
    public ResponseEntity<Void> deleteConcurrentParam(@RequestBody @Encrypt ConcurrentParam param) {
        SecurityTokenHelper.validToken(param);
        concurrentParamRepository.deleteByPrimaryKey(param.getConcParamId());
        return Results.success();
    }
}
