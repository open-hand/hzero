package org.hzero.platform


import org.hzero.boot.platform.code.builder.CodeRuleBuilder
import org.hzero.boot.platform.code.constant.CodeConstants
import org.hzero.platform.app.service.CodeRuleDistService
import org.hzero.platform.app.service.CodeRuleService
import org.hzero.platform.domain.entity.CodeRule
import org.hzero.platform.domain.entity.CodeRuleDetail
import org.hzero.platform.domain.entity.CodeRuleDist
import org.hzero.platform.domain.repository.CodeRuleRepository
import org.hzero.platform.infra.mapper.CodeRuleDistMapper
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootContextLoader
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ContextConfiguration
import org.springframework.test.context.web.WebAppConfiguration
import spock.lang.Specification
import spock.lang.Title

/**
 * <p>
 * 编码规则测试类
 * </p>
 *
 * @author qingsheng.chen 2019/4/8 星期一 17:16
 */
@Title("编码规则测试")
@WebAppConfiguration
@SpringBootTest(classes = PlatformApplication.class)
@ContextConfiguration(loader = SpringBootContextLoader.class)
class CodeRuleTest extends Specification {

    @Autowired
    CodeRuleBuilder codeRuleBuilder

    @Autowired
    CodeRuleService codeRuleService

    @Autowired
    CodeRuleDistService codeRuleDistService

    @Autowired
    CodeRuleRepository codeRuleRepository

    @Autowired
    CodeRuleDistMapper codeRuleDistMapper

    def "编码规则测试"() {
        given:
        // 平台级编码规则
        CodeRule codeRuleP = new CodeRule()
        codeRuleP.tenantId = 0
        codeRuleP.ruleCode = "TEST-P-0"
        codeRuleP.ruleName = "平台编码规则测试01"
        codeRuleP.ruleLevel = CodeConstants.Level.TENANT

        CodeRuleDist codeRuleDistP = new CodeRuleDist()
        List<CodeRuleDetail> codeRuleDetailListP = new ArrayList<>()
        CodeRuleDetail fixedValueP = new CodeRuleDetail()
        fixedValueP.orderSeq = 1
        fixedValueP.fieldType = CodeConstants.FieldType.CONSTANT
        fixedValueP.fieldValue = "TEST-P-"
        CodeRuleDetail sequenceP = new CodeRuleDetail()
        sequenceP.orderSeq = 2
        sequenceP.fieldType = CodeConstants.FieldType.SEQUENCE
        sequenceP.seqLength = 6
        sequenceP.startValue = 1
        codeRuleDetailListP.add(fixedValueP)
        codeRuleDetailListP.add(sequenceP)

        // 租户级编码规则
        CodeRule codeRuleT = new CodeRule()
        codeRuleT.tenantId = 1
        codeRuleT.ruleCode = "TEST-P-0"
        codeRuleT.ruleName = "租户编码规则测试01"
        codeRuleT.ruleLevel = "T"

        CodeRuleDist codeRuleDistT = new CodeRuleDist()
        List<CodeRuleDetail> codeRuleDetailListT = new ArrayList<>()
        CodeRuleDetail fixedValueT = new CodeRuleDetail()
        fixedValueT.orderSeq = 1
        fixedValueT.fieldType = CodeConstants.FieldType.CONSTANT
        fixedValueT.fieldValue = "TEST-T-"
        CodeRuleDetail sequenceT = new CodeRuleDetail()
        sequenceT.orderSeq = 2
        sequenceT.fieldType = CodeConstants.FieldType.SEQUENCE
        sequenceT.seqLength = 6
        sequenceT.startValue = 1
        codeRuleDetailListT.add(fixedValueT)
        codeRuleDetailListT.add(sequenceT)

        // 公司级编码规则明细
        CodeRuleDist codeRuleDistC = new CodeRuleDist()
        List<CodeRuleDetail> codeRuleDetailListC = new ArrayList<>()
        CodeRuleDetail fixedValueC = new CodeRuleDetail()
        fixedValueC.orderSeq = 1
        fixedValueC.fieldType = CodeConstants.FieldType.CONSTANT
        fixedValueC.fieldValue = "TEST-C-"
        CodeRuleDetail sequenceC = new CodeRuleDetail()
        sequenceC.orderSeq = 2
        sequenceC.fieldType = CodeConstants.FieldType.SEQUENCE
        sequenceC.seqLength = 6
        sequenceC.startValue = 1
        codeRuleDetailListC.add(fixedValueC)
        codeRuleDetailListC.add(sequenceC)
        codeRuleDistC.setRuleDetailList(codeRuleDetailListC)
        codeRuleDistC.setEnabledFlag(1)
        codeRuleDistC.setLevelCode("COM")
        codeRuleDistC.setLevelValue("SRM")

        when: "创建平台编码规则"
        codeRuleService.insertOrUpdate(codeRuleP)
        then:
        codeRuleP.ruleId != null

        when: "获取平台自动创建编码规则分配"
        codeRuleDistP.ruleId = codeRuleP.ruleId
        List<CodeRuleDist> codeRuleDistListP = codeRuleDistMapper.select(codeRuleDistP)
        if (codeRuleDistListP.size() == 1)
            codeRuleDistP = codeRuleDistListP.get(0)
        then:
        codeRuleDistListP.size() == 1
        codeRuleDistP.ruleDistId != null

        when: "启用平台编码规则分配，生成编码规则明细"
        fixedValueP.ruleDistId = codeRuleDistP.ruleDistId
        sequenceP.ruleDistId = codeRuleDistP.ruleDistId
        codeRuleDistP.ruleDetailList = codeRuleDetailListP
        codeRuleDistP.enabledFlag = 1
        codeRuleDistService.insertOrUpdate(0, codeRuleDistP)
        then:
        for (CodeRuleDetail codeRuleDetail : codeRuleDetailListP) {
            codeRuleDetail.ruleDetailId != null
        }

        when: "获取平台编码规则"
        // 平台编码规则
        String code1 = codeRuleBuilder.generateCode("TEST-P-0", null)
        String code2 = codeRuleBuilder.generateCode("TEST-P-0", null)
        // 租户获取 平台级 平台编码规则 （租户内不连续）
        String code3 = codeRuleBuilder.generateCode(CodeConstants.Level.PLATFORM, 1, "TEST-P-0", "GLOBAL", "GLOBAL", null)
        String code4 = codeRuleBuilder.generateCode(CodeConstants.Level.PLATFORM, 2, "TEST-P-0", "COM", "HAND", null)
        // 租户获取 租户级 平台编码规则 （租户内连续）
        String code5 = codeRuleBuilder.generateCode(CodeConstants.Level.TENANT, 1, "TEST-P-0", "GLOBAL", "GLOBAL", null)
        String code6 = codeRuleBuilder.generateCode(CodeConstants.Level.TENANT, 1, "TEST-P-0", "GLOBAL", "GLOBAL", null)
        String code7 = codeRuleBuilder.generateCode(CodeConstants.Level.TENANT, 2, "TEST-P-0", "GLOBAL", "GLOBAL", null)
        String code8 = codeRuleBuilder.generateCode(CodeConstants.Level.TENANT, 2, "TEST-P-0", "GLOBAL", "GLOBAL", null)
        // 租户获取 公司级 平台编码规则 （公司内连续）
        String code9 = codeRuleBuilder.generateCode(CodeConstants.Level.TENANT, 2, "TEST-P-0", "COM", "SRM", null)
        String code10 = codeRuleBuilder.generateCode(CodeConstants.Level.COMPANY, 2, "TEST-P-0", "COM", "SRM", null)
        then:
        code1 == "TEST-P-000001"
        code2 == "TEST-P-000002"
        code3 == "TEST-P-000003"
        code4 == "TEST-P-000004"
        code5 == "TEST-P-000001"
        code6 == "TEST-P-000002"
        code7 == "TEST-P-000001"
        code8 == "TEST-P-000002"
        code9 == "TEST-P-000001"
        code10 == "TEST-P-000002"

        when: "创建租户级编码规则"
        codeRuleService.insertOrUpdate(codeRuleT)
        then:
        codeRuleT.ruleId != null

        when: "获取租户自动创建编码规则分配"
        codeRuleDistT.ruleId = codeRuleT.ruleId
        List<CodeRuleDist> codeRuleDistListT = codeRuleDistMapper.select(codeRuleDistT)
        if (codeRuleDistListT.size() == 1)
            codeRuleDistT = codeRuleDistListT.get(0)
        then:
        codeRuleDistListT.size() == 1
        codeRuleDistT.ruleDistId != null

        when: "启用租户编码规则分配，生成编码规则明细"
        fixedValueT.ruleDistId = codeRuleDistT.ruleDistId
        sequenceT.ruleDistId = codeRuleDistT.ruleDistId
        codeRuleDistT.ruleDetailList = codeRuleDetailListT
        codeRuleDistT.enabledFlag = 1
        codeRuleDistService.insertOrUpdate(1, codeRuleDistT)
        then:
        for (CodeRuleDetail codeRuleDetail : codeRuleDetailListT) {
            codeRuleDetail.ruleDetailId != null
        }

        when: "获取租户编码规则"
        // 获取租户级编码规则
        String code11 = codeRuleBuilder.generateCode(CodeConstants.Level.TENANT, 1, "TEST-P-0", "GLOBAL", "GLOBAL", null)
        String code12 = codeRuleBuilder.generateCode(CodeConstants.Level.COMPANY, 1, "TEST-P-0", "GLOBAL", "GLOBAL", null)
        // 获取公司级编码规则
        String code13 = codeRuleBuilder.generateCode(CodeConstants.Level.TENANT, 1, "TEST-P-0", "COM", "SRM", null)
        String code14 = codeRuleBuilder.generateCode(CodeConstants.Level.COMPANY, 1, "TEST-P-0", "COM", "SRM", null)
        then:
        code11 == "TEST-T-000001"
        code12 == "TEST-T-000002"
        code13 == "TEST-T-000001"
        code14 == "TEST-T-000002"

        when: "跨租户获取报错"
        String codeUp1 = codeRuleBuilder.generateCode(CodeConstants.Level.TENANT, 2, "TEST-P-0", "GLOBAL", "GLOBAL", null)
        then:
        codeUp1 == "TEST-P-000003"

        when: "平台获取报错"
        String codeUp2 = codeRuleBuilder.generateCode(CodeConstants.Level.PLATFORM, 0, "TEST-P-0", "GLOBAL", "GLOBAL", null)
        then:
        codeUp2 == "TEST-P-000005"

        when: "自定义公司级编码规则分配"
        codeRuleDistC.ruleId = codeRuleT.ruleId
        codeRuleDistService.insertOrUpdate(1, codeRuleDistC)
        then:
        codeRuleDistC.getRuleDistId() != null
        fixedValueC.getRuleDistId() == codeRuleDistC.getRuleDistId()
        fixedValueC.getRuleDetailId() != null
        sequenceC.getRuleDistId() == codeRuleDistC.getRuleDistId()
        sequenceC.getRuleDetailId() != null

        when: "获取公司级编码规则"
        String code15 = codeRuleBuilder.generateCode(CodeConstants.Level.TENANT, 1, "TEST-P-0", "COM", "SRM", null)
        String code16 = codeRuleBuilder.generateCode(CodeConstants.Level.COMPANY, 1, "TEST-P-0", "COM", "SRM", null)
        then:
        code15 == "TEST-C-000001"
        code16 == "TEST-C-000002"


        cleanup: "删除编码规则"
        codeRuleRepository.delete(codeRuleP)
        codeRuleRepository.delete(codeRuleT)
    }

}
