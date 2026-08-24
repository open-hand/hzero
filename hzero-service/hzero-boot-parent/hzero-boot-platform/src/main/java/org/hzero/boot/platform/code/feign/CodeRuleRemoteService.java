package org.hzero.boot.platform.code.feign;

import java.util.List;

import org.hzero.boot.platform.code.entity.CodeRule;
import org.hzero.boot.platform.code.entity.CodeRuleDetail;
import org.hzero.boot.platform.code.entity.CodeRuleDist;
import org.hzero.boot.platform.code.feign.failback.CodeRuleRemoteServiceImpl;
import org.hzero.common.HZeroService;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * <p>
 * 编码规则远程feign调用客户端
 * </p>
 *
 * @author yunxiang.zhou01@hand-china.com 2018/09/07 17:11
 */
@FeignClient(value = HZeroService.Platform.NAME, fallback = CodeRuleRemoteServiceImpl.class, path = "/v1")
public interface CodeRuleRemoteService {

    /**
     * 根据主键查询
     *
     * @param tenantId     租户ID
     * @param ruleDetailId ruleDetailId
     * @return CodeRuleDetail
     */
    @GetMapping("/{organizationId}/code-rule/code-rule-detail/{ruleDetailId}")
    CodeRuleDetail selectByPrimaryKey(@PathVariable("organizationId") Long tenantId, @PathVariable("ruleDetailId") Long ruleDetailId);

    /**
     * 根据主键更新
     *
     * @param tenantId       租户ID
     * @param codeRuleDetail codeRuleDetail
     */
    @PutMapping("/{organizationId}/code-rule/code-rule-detail")
    void updateByPrimaryKey(@PathVariable("organizationId") Long tenantId, @RequestBody CodeRuleDetail codeRuleDetail);

    /**
     * 更新编码规则分配的使用标记为使用
     *
     * @param tenantId   租户ID
     * @param ruleDistId 编码规则分配id
     */
    @PutMapping("/{organizationId}/code-rule/code-rule-dist/{ruleDistId}")
    void updateDistUseFlag(@PathVariable("organizationId") Long tenantId, @PathVariable("ruleDistId") Long ruleDistId);


    /**
     * 查询获取编码规则、编码规则分配、编码规则明细
     *
     * @param tenantId   租户ID
     * @param codeRuleId 编码规则ID
     * @return 编码规则
     */
    @GetMapping("/{organizationId}/code-rule/one")
    ResponseEntity<String> getCodeRule(@PathVariable("organizationId") Long tenantId, @RequestParam("codeRuleId") long codeRuleId);

    /**
     * 查询获取编码规则、编码规则分配、编码规则明细
     *
     * @param tenantId 租户ID
     * @param ruleCode 编码规则编码
     * @return 编码规则
     */
    @GetMapping("/{organizationId}/code-rule/rule-code")
    ResponseEntity<String> getCodeRule(@PathVariable("organizationId") Long tenantId, @RequestParam("ruleCode") String ruleCode);

    /**
     * 租户新增(如果编码规则ID为null)/更新(如果编码规则ID不为null)编码规则、编码规则分配、编码规则明细
     *
     * @param tenantId 租户ID
     * @param codeRule 编码规则明细
     * @return 保存编码规则
     */
    @PostMapping("/{organizationId}/code-rule")
    ResponseEntity<String> saveCodeRule(@PathVariable("organizationId") long tenantId, @RequestBody CodeRule codeRule);

    /**
     * 保存编码规则分配、编码规则明细
     *
     * @param tenantId     租户ID
     * @param codeRuleDist 编码规则分配
     * @return 编码规则分配
     */
    @PostMapping("/{organizationId}/code-rule-dist")
    ResponseEntity<String> saveCodeRuleDist(@PathVariable("organizationId") long tenantId, @RequestBody CodeRuleDist codeRuleDist);

    /**
     * 保存编码规则明细
     *
     * @param tenantId       租户ID
     * @param codeRuleDetail 编码规则明细
     * @return 编码规则明细
     */
    @PostMapping("/{organizationId}/code-rule-detail")
    ResponseEntity<String> saveCodeRuleDetail(@PathVariable("organizationId") long tenantId, @RequestBody CodeRuleDetail codeRuleDetail);

    /**
     * 删除编码规则、编码规则分配、编码规则明细
     *
     * @param tenantId 租户ID
     * @param codeRule 编码规则
     * @return 204
     */
    @DeleteMapping("/{organizationId}/code-rule")
    ResponseEntity<String> deleteCodeRule(@PathVariable("organizationId") long tenantId, @RequestBody List<CodeRule> codeRule);

    /**
     * 删除编码规则分配、编码规则明细
     *
     * @param tenantId     租户ID
     * @param codeRuleDist 编码规则分配
     * @return 204
     */
    @DeleteMapping("/{organizationId}/code-rule-dist")
    ResponseEntity<String> deleteCodeRuleDist(@PathVariable("organizationId") long tenantId, @RequestBody List<CodeRuleDist> codeRuleDist);

    /**
     * 删除编码规则明细
     *
     * @param tenantId       租户ID
     * @param codeRuleDetail 编码规则明细
     * @return 204
     */
    @DeleteMapping("/{organizationId}/code-rule-detail")
    ResponseEntity<String> deleteCodeRuleDetail(@PathVariable("organizationId") long tenantId, @RequestBody List<CodeRuleDetail> codeRuleDetail);

    /**
     * 根据编码获取规则明细
     *
     * @param organizationId     当前用户请求的编码规则的租户ID，因为降级会导致 tenantId 可能转为0,所以单独传
     * @param tenantId           租户ID
     * @param ruleCode           编码
     * @param ruleLevel          层级 P/T
     * @param levelCode          GLOBAL/COM
     * @param levelValue         层级值
     * @param previousRuleLevel  实际请求层级
     * @param previousLevelValue 实际层级值
     * @return List<CodeRuleDetail>
     */
    @GetMapping("/{organizationId}/code-rule-details/previous")
    ResponseEntity<String> listCodeRule(@PathVariable("organizationId") Long organizationId,
                                        @RequestParam("tenantId") Long tenantId,
                                        @RequestParam("ruleCode") String ruleCode,
                                        @RequestParam("ruleLevel") String ruleLevel,
                                        @RequestParam("levelCode") String levelCode,
                                        @RequestParam("levelValue") String levelValue,
                                        @RequestParam(value = "previousRuleLevel", required = false) String previousRuleLevel,
                                        @RequestParam(value = "previousLevelValue", required = false) String previousLevelValue);
}
