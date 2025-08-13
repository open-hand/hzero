package org.hzero.platform.api.controller.v1;

import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.hzero.boot.platform.code.constant.CodeConstants;
import org.hzero.core.base.BaseConstants;
import org.hzero.core.base.BaseController;
import org.hzero.core.util.Results;
import org.hzero.mybatis.helper.SecurityTokenHelper;
import org.hzero.platform.api.dto.CodeRuleDTO;
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
import org.springframework.util.Assert;
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
 * 编码规则租户级Controller
 * </p>
 *
 * @author yunxiang.zhou01@hand-china.com 2018/06/27 14:26
 */
@Api(tags = PlatformSwaggerApiConfig.CODE_RULE_MANAGE)
@RestController("codeRuleManagerController.v1")
@RequestMapping("/v1")
public class CodeRuleManagerController extends BaseController {

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

    @ApiOperation("查询获取编码规则、编码规则分配、编码规则明细")
    @GetMapping("/{organizationId}/code-rule/one")
    @Permission(level = ResourceLevel.ORGANIZATION, permissionWithin = true)
    public ResponseEntity<CodeRule> getCodeRule(@PathVariable("organizationId") Long tenantId, @RequestParam long codeRuleId) {
        return Results.success(codeRuleService.getCodeRule(tenantId, codeRuleId));
    }

    @ApiOperation("查询获取编码规则、编码规则分配、编码规则明细")
    @GetMapping("/{organizationId}/code-rule/rule-code")
    @Permission(level = ResourceLevel.ORGANIZATION, permissionWithin = true)
    public ResponseEntity<CodeRule> getCodeRuleByCode(@PathVariable("organizationId") Long tenantId, @RequestParam String ruleCode) {
        return Results.success(codeRuleService.getCodeRule(tenantId, ruleCode));
    }

    @ApiOperation("批量查询获取编码规则、编码规则分配")
    @PostMapping("/{organizationId}/code-rule/rule-code-list")
    @Permission(level = ResourceLevel.ORGANIZATION, permissionWithin = true)
    public ResponseEntity<List<CodeRuleDTO>> getCodeRuleByCodeList(@PathVariable("organizationId") Long tenantId, @RequestParam("ruleCodeList") List<String> ruleCodeList) {
        return Results.success(codeRuleService.getCodeRuleList(tenantId, ruleCodeList));
    }

    /**
     * 租户级新增和更新编码规则头
     *
     * @param codeRule 编码规则
     * @return 统一返回结果
     */
    @ApiOperation(value = "租户级新增和更新编码规则头", notes = "租户级新增和更新编码规则头")
    @PostMapping("/{organizationId}/code-rule")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "organizationId", value = "租户ID", paramType = "path", required = true)
    })
    @Permission(level = ResourceLevel.ORGANIZATION)
    public ResponseEntity insertOrUpdate(@PathVariable("organizationId") Long organizationId, @RequestBody @Encrypt CodeRule codeRule) {
        codeRule.setTenantId(organizationId);
        codeRule.setRuleLevel(FndConstants.Level.TENANT);
        validObject(codeRule);
        SecurityTokenHelper.validTokenIgnoreInsert(codeRule);
        if (!codeRule.judgeInsert()) {
            // 校验数据合法性
            Assert.isTrue(CodeRule.judgeDataLegality(codeRuleRepository, organizationId, codeRule.getRuleId()),
                    BaseConstants.ErrorCode.DATA_INVALID);
        }
        return Results.success(codeRuleService.insertOrUpdate(codeRule));
    }

    /**
     * 租户级新增和更新编码规则分配
     *
     * @param codeRuleDist 编码规则分配
     * @return 统一返回结果
     */
    @ApiOperation(value = "租户级新增和更新编码规则分配", notes = "租户级新增和更新编码规则分配")
    @PostMapping("/{organizationId}/code-rule-dist")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "organizationId", value = "租户ID", paramType = "path", required = true)
    })
    @Permission(level = ResourceLevel.ORGANIZATION)
    public ResponseEntity insertOrUpdateDist(@PathVariable Long organizationId,
                                             @RequestBody @Encrypt CodeRuleDist codeRuleDist) {
        validObject(codeRuleDist);
        SecurityTokenHelper.validTokenIgnoreInsert(codeRuleDist);
        return Results.success(codeRuleDistService.insertOrUpdate(organizationId, codeRuleDist));
    }

    /**
     * 租户级新增和更新编码规则明细
     *
     * @param codeRuleDetail 编码规则明细
     * @return 统一返回结果
     */
    @ApiOperation(value = "租户级新增和更新编码规则明细", notes = "租户级新增和更新编码规则明细")
    @PostMapping("/{organizationId}/code-rule-detail")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "organizationId", value = "租户ID", paramType = "path", required = true)
    })
    @Permission(level = ResourceLevel.ORGANIZATION)
    public ResponseEntity insertOrUpdateDetail(@PathVariable Long organizationId, @RequestBody @Encrypt CodeRuleDetail codeRuleDetail) {
        SecurityTokenHelper.validTokenIgnoreInsert(codeRuleDetail);
        if (StringUtils.isNotBlank(codeRuleDetail.getFieldType())
                && CodeConstants.FieldType.SEQUENCE.equals(codeRuleDetail.getFieldType())) {
            validObject(codeRuleDetail, CodeRuleDetail.SequenceGroup.class);
        } else {
            validObject(codeRuleDetail);
        }
        return Results.success(codeRuleDetailService.insertOrUpdate(organizationId, codeRuleDetail));
    }


    /**
     * 租户级编码规则list查询
     *
     * @param codeRule    编码规则
     * @param pageRequest 分页工具类
     * @return 统一返回结果
     */
    @ApiOperation(value = "租户级编码规则list查询", notes = "租户级编码规则list查询")
    @GetMapping("/{organizationId}/code-rule")
    @CustomPageRequest
    @ApiImplicitParams({
            @ApiImplicitParam(name = "organizationId", value = "租户ID", paramType = "path", required = true)
    })
    @Permission(level = ResourceLevel.ORGANIZATION)
    public ResponseEntity list(@PathVariable Long organizationId, @Encrypt CodeRule codeRule,
                               @ApiIgnore @SortDefault(value = CodeRule.RULE_ID, direction = Sort.Direction.ASC) PageRequest pageRequest) {
        codeRule.setTenantId(organizationId);
        return Results.success(codeRuleRepository.selectCodeRuleList(codeRule, pageRequest));
    }

    /**
     * 租户级根据ruleId查询编码规则
     *
     * @param ruleId      主键id
     * @param pageRequest 分页
     * @return 统一返回结果
     */
    @ApiOperation(value = "租户级根据ruleId查询编码规则", notes = "租户级根据ruleId查询编码规则")
    @GetMapping("/{organizationId}/code-rule/{ruleId}")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "ruleId", value = "规则ID", paramType = "path", required = true)
    })
    @Permission(level = ResourceLevel.ORGANIZATION)
    public ResponseEntity query(@Encrypt @PathVariable("ruleId") Long ruleId, @PathVariable("organizationId") Long organizationId, PageRequest pageRequest) {
        return Results.success(codeRuleRepository.query(organizationId, ruleId, pageRequest));
    }

    /**
     * 租户级根据ruleDistId查询编码规则明细信息
     *
     * @param ruleDistId 主键id
     * @return 统一返回结果
     */
    @ApiOperation(value = "租户级根据ruleDistId查询编码规则明细信息", notes = "租户级根据ruleDistId查询编码规则明细信息")
    @GetMapping("/{organizationId}/code-rule-detail/{ruleDistId}")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "ruleDistId", value = "规则分配ID", paramType = "path", required = true)
    })
    @Permission(level = ResourceLevel.ORGANIZATION)
    public ResponseEntity queryDetail(@Encrypt @PathVariable("ruleDistId") Long ruleDistId, @PathVariable("organizationId") Long organizationId) {
        return Results.success(codeRuleDistRepository.selectCodeRuleDistAndDetail(organizationId, ruleDistId));
    }

    /**
     * 租户级删除编码规则
     *
     * @param codeRuleList 主键List
     * @return 统一返回结果
     */
    @ApiOperation(value = "租户级删除编码规则", notes = "租户级删除编码规则")
    @DeleteMapping("/{organizationId}/code-rule")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "organizationId", value = "租户ID", paramType = "path", required = true)
    })
    @Permission(level = ResourceLevel.ORGANIZATION)
    public ResponseEntity delete(@PathVariable Long organizationId, @RequestBody @Encrypt List<CodeRule> codeRuleList) {
        CodeRule.injectTenantId(organizationId, codeRuleList);
        SecurityTokenHelper.validToken(codeRuleList);
        codeRuleRepository.delete(codeRuleList);
        return Results.success();
    }

    /**
     * 租户级删除编码规则分配
     *
     * @param ruleDistList 规则分配list
     * @return 统一返回结果
     */
    @ApiOperation(value = "租户级删除编码规则分配", notes = "租户级删除编码规则分配")
    @DeleteMapping("/{organizationId}/code-rule-dist")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "organizationId", value = "租户ID", paramType = "path", required = true)
    })
    @Permission(level = ResourceLevel.ORGANIZATION)
    public ResponseEntity deleteDist(@PathVariable Long organizationId, @RequestBody @Encrypt List<CodeRuleDist> ruleDistList) {
        SecurityTokenHelper.validToken(ruleDistList);
        codeRuleDistRepository.deleteDist(organizationId, ruleDistList);
        return Results.success();
    }

    /**
     * 租户级删除编码规则明细
     *
     * @param codeRuleDetailList 规则明细list
     * @return 统一返回结果
     */
    @ApiOperation(value = "租户级删除编码规则明细", notes = "租户级删除编码规则明细")
    @DeleteMapping("/{organizationId}/code-rule-detail")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "organizationId", value = "租户ID", paramType = "path", required = true)
    })
    @Permission(level = ResourceLevel.ORGANIZATION)
    public ResponseEntity deleteDetail(@PathVariable Long organizationId,
                                       @RequestBody @Encrypt List<CodeRuleDetail> codeRuleDetailList) {
        SecurityTokenHelper.validToken(codeRuleDetailList);
        codeRuleDetailRepository.deleteDetail(organizationId, codeRuleDetailList);
        return Results.success();
    }

    @ApiOperation(value = "根据编码获取规则明细", notes = "根据编码获取规则明细")
    @GetMapping("/code-rule-details")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "tenantId", value = "租户ID", paramType = "query", required = true),
            @ApiImplicitParam(name = "ruleCode", value = "编码", paramType = "query", required = true)
    })
    @Permission(level = ResourceLevel.ORGANIZATION, permissionLogin = true)
    @ApiIgnore
    public ResponseEntity<List<CodeRuleDetail>> queryDetailByRuleCode(@RequestParam Long tenantId,
                                                                      @RequestParam String ruleCode,
                                                                      @RequestParam String ruleLevel,
                                                                      @RequestParam String levelCode,
                                                                      @RequestParam String levelValue) {
        return Results.success(codeRuleDetailRepository.selectDetailListByRuleCode(tenantId, ruleCode, ruleLevel, levelCode, levelValue));
    }
}
