package org.hzero.admin.api.controller.v1


import org.hzero.admin.api.dto.ApiLimitDTO
import org.hzero.admin.domain.entity.ApiMonitorRule
import org.hzero.admin.domain.repository.ApiMonitorRuleRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders
import org.springframework.test.web.servlet.result.MockMvcResultHandlers
import org.springframework.test.web.servlet.result.MockMvcResultMatchers
import org.springframework.test.web.servlet.setup.MockMvcBuilders
import spock.lang.Title

/**
 * @author XCXCXCXCX* @date 2020/6/22 5:35 下午
 */
@Title("接口限制接口测试")
class ApiLimitSiteControllerSpec extends ControllerSpec {

    @Autowired
    private ApiMonitorRuleRepository repository;
    @Autowired
    private ApiLimitSiteController controller;

    private MockMvc mockMvc;
    private Long monitorRuleId;

    void setup() {
        mockMvc = MockMvcBuilders.standaloneSetup(controller).build();
        ApiMonitorRule rule = new ApiMonitorRule();
        rule.setUrlPattern("*");
        rule.setTimeWindowSize(1000);
        repository.insertSelective(rule);
        monitorRuleId = rule.getMonitorRuleId();
    }

    def "创建或更新接口限制"() {
        ApiLimitDTO requestBody = new ApiLimitDTO();
        requestBody.setMonitorRuleId(monitorRuleId);
        requestBody.setListMode("BLACK");
        requestBody.setEnabledFlag(true);
        when:
        ApiLimitDTO responseBody = objectMapper.readValue(mockMvc.perform(MockMvcRequestBuilders.post("/v1/api-limits")
                .contentType("application/json")
                .content(objectMapper.writeValueAsBytes(requestBody)))
                .andExpect(MockMvcResultMatchers.status().is(200))
                .andDo(MockMvcResultHandlers.print())
                .andReturn().getResponse().getContentAsByteArray(), ApiLimitDTO.class);

        then:
        requestBody.monitorRuleId == responseBody.monitorRuleId
        requestBody.listMode == responseBody.listMode
        requestBody.enabledFlag == responseBody.enabledFlag
    }

    def "编辑查看接口限制"() {
        ApiLimitDTO requestBody = new ApiLimitDTO();
        requestBody.setMonitorRuleId(monitorRuleId);
        requestBody.setListMode("BLACK");
        requestBody.setEnabledFlag(true);
        ApiLimitDTO apiLimitDTO = objectMapper.readValue(mockMvc.perform(MockMvcRequestBuilders.post("/v1/api-limits")
                .contentType("application/json")
                .content(objectMapper.writeValueAsBytes(requestBody)))
                .andExpect(MockMvcResultMatchers.status().is(200))
                .andDo(MockMvcResultHandlers.print())
                .andReturn().getResponse().getContentAsByteArray(), ApiLimitDTO.class);

        when:
        ApiLimitDTO responseBody = objectMapper.readValue(mockMvc.perform(MockMvcRequestBuilders.get("/v1/api-limits")
                .param("monitorRuleId", String.valueOf(apiLimitDTO.monitorRuleId))
                .contentType("application/json"))
                .andExpect(MockMvcResultMatchers.status().is(200))
                .andDo(MockMvcResultHandlers.print())
                .andReturn().getResponse().getContentAsByteArray(), ApiLimitDTO.class);

        then:
        apiLimitDTO.monitorRuleId == responseBody.monitorRuleId
        apiLimitDTO.listMode == responseBody.listMode
        apiLimitDTO.enabledFlag == responseBody.enabledFlag
    }


}
