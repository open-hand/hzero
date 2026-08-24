package org.hzero.platform.api.controller.v1;

import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.hzero.boot.platform.code.constant.CodeConstants;
import org.hzero.core.base.BaseConstants;
import org.hzero.core.base.BaseController;
import org.hzero.core.util.Results;
import org.hzero.mybatis.helper.SecurityTokenHelper;
import org.hzero.platform.app.service.CodeRuleDetailService;
import org.hzero.platform.app.service.CodeRuleDistService;
import org.hzero.platform.app.service.CodeRuleService;
import org.hzero.platform.config.PlatformSwaggerApiConfig;
import org.hzero.platform.domain.entity.CodeRule;
import org.hzero.platform.domain.entity.CodeRuleDetail;
import org.hzero.platform.domain.entity.CodeRuleDist;
import org.hzero.platform.domain.repository.CodeRuleDetailRepository;
import org.hzero.platform.domain.repository.CodeRuleDistRepository;
import org.hzero.platform.domain.repository.CodeRuleRepository;
import org.hzero.platform.infra.constant.FndConstants;
import org.hzero.starter.keyencrypt.core.Encrypt;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import springfox.documentation.annotations.ApiIgnore;

import io.choerodon.core.iam.ResourceLevel;
import io.choerodon.mybatis.pagehelper.annotation.SortDefault;
import io.choerodon.mybatis.pagehelper.domain.PageRequest;
import io.choerodon.mybatis.pagehelper.domain.Sort;
import io.choerodon.swagger.annotation.CustomPageRequest;
import io.choerodon.swagger.annotation.Permission;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;


/**
 * <p>
 * 编码规则平台管理controller
 * </p>
 *
 * @author yunxiang.zhou01@hand-china.com 2018/06/13 19:06
 */
@Api(tags = PlatformSwaggerApiConfig.CODE_RULE_MANAGE_SITE)
@RestController("codeRuleManagerSiteController.v1")
@RequestMapping("/v1")
public class CodeRuleManagerSiteController extends BaseController {

    @Autowired
    private CodeRuleService codeRuleService;
    @Autowired
    private CodeRuleDistService codeRuleDistService;
    @Autowired
    private CodeRuleDetailService codeRuleDetailService;
    @Autowired
    private CodeRuleRepository codeRuleRepository;
    @Autowired
    private CodeRuleDistRepository codeRuleDistRepository;
    @Autowired
    private CodeRuleDetailRepository codeRuleDetailRepository;

    /**
     * 新增和更新编码规则头
     *
     * @param codeRule 编码规则
     * @return 统一返回结果
     */
    @ApiOperation(value = "新增和更新编码规则头", notes = "新增和更新编码规则头")
    @PostMapping("/code-rule")
    @Permission(level = ResourceLevel.SITE)
    public ResponseEntity insertOrUpdate(@RequestBody @Encrypt CodeRule codeRule) {
        codeRule.setRuleLevel(FndConstants.Level.TENANT);
        validObject(codeRule);
        SecurityTokenHelper.validTokenIgnoreInsert(codeRule);
        return Results.success(codeRuleService.insertOrUpdate(codeRule));
    }

    /**
     * 新增和更新编码规则分配
     *
     * @param codeRuleDist 编码规则分配
     * @return 统一返回结果
     */
    @ApiOperation(value = "新增和更新编码规则分配", notes = "新增和更新编码规则分配")
    @PostMapping("/code-rule-dist")
    @Permission(level = ResourceLevel.SITE)
    public ResponseEntity insertOrUpdateDist(@RequestBody @Encrypt CodeRuleDist codeRuleDist) {
        validObject(codeRuleDist);
        SecurityTokenHelper.validTokenIgnoreInsert(codeRuleDist);
        return Results.success(codeRuleDistService.insertOrUpdate(BaseConstants.DEFAULT_TENANT_ID, codeRuleDist));
    }

    /**
     * 新增和更新编码规则明细
     *
     * @param codeRuleDetail 编码规则明细
     * @return 统一返回结果
     */
    @ApiOperation(value = "新增和更新编码规则明细", notes = "新增和更新编码规则明细")
    @PostMapping("/code-rule-detail")
    @Permission(level = ResourceLevel.SITE)
    public ResponseEntity insertOrUpdateDetail(@RequestBody @Encrypt CodeRuleDetail codeRuleDetail) {
        if (StringUtils.isNotBlank(codeRuleDetail.getFieldType())
                && CodeConstants.FieldType.SEQUENCE.equals(codeRuleDetail.getFieldType())) {
            validObject(codeRuleDetail, CodeRuleDetail.SequenceGroup.class);
        } else {
            validObject(codeRuleDetail);
        }
        SecurityTokenHelper.validTokenIgnoreInsert(codeRuleDetail);
        return Results.success(codeRuleDetailService.insertOrUpdate(BaseConstants.DEFAULT_TENANT_ID, codeRuleDetail));
    }


    /**
     * 编码规则list查询
     *
     * @param codeRule    编码规则
     * @param pageRequest 分页工具类
     * @return 统一返回结果
     */
    @ApiOperation(value = "编码规则list查询", notes = "编码规则list查询")
    @GetMapping("/code-rule")
    @CustomPageRequest
    @Permission(level = ResourceLevel.SITE)
    public ResponseEntity list(CodeRule codeRule, @ApiIgnore @SortDefault(value = CodeRule.RULE_ID,
            direction = Sort.Direction.ASC) PageRequest pageRequest) {
        return Results.success(codeRuleRepository.selectCodeRuleList(codeRule, pageRequest));
    }

    /**
     * 根据ruleId查询编码规则
     *
     * @param ruleId      主键id
     * @param pageRequest 分页
     * @return 统一返回结果
     */
    @ApiOperation(value = "根据ruleId查询编码规则", notes = "根据ruleId查询编码规则")
    @GetMapping("/code-rule/{ruleId}")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "ruleId", value = "规则ID", paramType = "path", required = true),
            @ApiImplicitParam(name = "organizationId", value = "组织Id", paramType = "query", required = true)
    })
    @Permission(level = ResourceLevel.SITE)
    public ResponseEntity query(@Encrypt @PathVariable Long ruleId, PageRequest pageRequest) {
        return Results.success(codeRuleRepository.query(null, ruleId, pageRequest));
    }

    /**
     * 根据ruleDistId查询编码规则明细信息
     *
     * @param ruleDistId 主键id
     * @return 统一返回结果
     */
    @ApiOperation(value = "根据ruleDistId查询编码规则明细信息", notes = "根据ruleDistId查询编码规则明细信息")
    @GetMapping("/code-rule-detail/{ruleDistId}")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "ruleDistId", value = "规则分配ID", paramType = "path", required = true),
            @ApiImplicitParam(name = "organizationId", value = "组织Id", paramType = "query", required = true)
    })
    @Permission(level = ResourceLevel.SITE)
    public ResponseEntity queryDetail(@Encrypt @PathVariable Long ruleDistId) {
        return Results.success(codeRuleDistRepository.selectCodeRuleDistAndDetail(null, ruleDistId));
    }

    /**
     * 删除编码规则
     *
     * @param codeRuleList 主键List
     * @return 统一返回结果
     */
    @ApiOperation(value = "删除编码规则", notes = "删除编码规则")
    @DeleteMapping("/code-rule")
    @Permission(level = ResourceLevel.SITE)
    public ResponseEntity delete(@RequestBody @Encrypt List<CodeRule> codeRuleList) {
        SecurityTokenHelper.validTokenIgnoreInsert(codeRuleList);
        codeRuleRepository.delete(codeRuleList);
        return Results.success();
    }

    /**
     * 删除编码规则分配
     *
     * @param ruleDistList 规则分配list
     * @return 统一返回结果
     */
    @ApiOperation(value = "删除编码规则分配", notes = "删除编码规则分配")
    @DeleteMapping("/code-rule-dist")
    @Permission(level = ResourceLevel.SITE)
    public ResponseEntity deleteDist(@RequestBody @Encrypt List<CodeRuleDist> ruleDistList) {
        SecurityTokenHelper.validTokenIgnoreInsert(ruleDistList);
        codeRuleDistRepository.deleteDist(BaseConstants.DEFAULT_TENANT_ID, ruleDistList);
        return Results.success();
    }

    /**
     * 删除编码规则明细
     *
     * @param codeRuleDetailList 规则明细list
     * @return 统一返回结果
     */
    @ApiOperation(value = "删除编码规则明细", notes = "删除编码规则明细")
    @DeleteMapping("/code-rule-detail")
    @Permission(level = ResourceLevel.SITE)
    public ResponseEntity deleteDetail(@RequestBody @Encrypt List<CodeRuleDetail> codeRuleDetailList) {
        SecurityTokenHelper.validTokenIgnoreInsert(codeRuleDetailList);
        codeRuleDetailRepository.deleteDetail(BaseConstants.DEFAULT_TENANT_ID, codeRuleDetailList);
        return Results.success();
    }
}
