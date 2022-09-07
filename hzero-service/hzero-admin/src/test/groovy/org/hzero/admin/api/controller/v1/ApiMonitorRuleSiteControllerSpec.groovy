package org.hzero.admin.api.controller.v1


import io.choerodon.core.exception.CommonException
import org.hzero.admin.domain.entity.ApiMonitorRule
import org.hzero.admin.domain.repository.ApiMonitorRuleRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders
import org.springframework.test.web.servlet.result.MockMvcResultHandlers
import org.springframework.test.web.servlet.result.MockMvcResultMatchers
import org.springframework.test.web.servlet.setup.MockMvcBuilders
import spock.lang.Title

/**
 * @author XCXCXCXCX* @date 2020/6/22 8:45 下午
 */
@Title("API监控接口测试")
class ApiMonitorRuleSiteControllerSpec extends ControllerSpec {

    @Autowired
    private ApiMonitorRuleSiteController controller;
    @Autowired
    private ApiMonitorRuleRepository repository;

    private MockMvc mockMvc;

    void setup() {
        mockMvc = MockMvcBuilders.standaloneSetup(controller).build();
    }

    def "API监控配置删除"() {

        given:
        ApiMonitorRule rule1 = new ApiMonitorRule();
        rule1.setUrlPattern("*");
        rule1.setTimeWindowSize(1000);
        ApiMonitorRule rule2 = new ApiMonitorRule();
        rule2.setUrlPattern("*");
        rule2.setTimeWindowSize(1000);
        repository.insertSelective(rule1);
        repository.insertSelective(rule2);

        when:
        def delete = mockMvc.perform(
                MockMvcRequestBuilders.delete("/v1/api-monitor-rules")
                        .param("monitorRuleIds", rule1.monitorRuleId + "," + rule2.monitorRuleId));

        then:
        delete.andDo(MockMvcResultHandlers.print()).andExpect(MockMvcResultMatchers.status().isOk()).andReturn();


    }

    def "API监控配置新增"() {

        given:
        ApiMonitorRule rule1 = new ApiMonitorRule();
        rule1.setUrlPattern("*");
        rule1.setTimeWindowSize(1000);
        ApiMonitorRule rule2 = new ApiMonitorRule();
        rule2.setUrlPattern("/**");
        rule2.setTimeWindowSize(1000);

        when:
        mockMvc.perform(
                MockMvcRequestBuilders.post("/v1/api-monitor-rules")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(rule1)));

        then:
        def ex = thrown(Exception);
        ex.getCause().getClass() == CommonException.class

        when:
        def insert = mockMvc.perform(
                MockMvcRequestBuilders.post("/v1/api-monitor-rules")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(rule2)));


        then:
        insert.andDo(MockMvcResultHandlers.print()).andExpect(MockMvcResultMatchers.status().isOk()).andReturn();

    }

    def "API监控配置查询"() {

        given:
        ApiMonitorRule rule = new ApiMonitorRule();
        rule.setUrlPattern("*");
        rule.setTimeWindowSize(1000);
        repository.insertSelective(rule);

        when:
        def query = mockMvc.perform(
                MockMvcRequestBuilders.get("/v1/api-monitor-rules")
                        .param("monitorRuleId", String.valueOf(rule.monitorRuleId)));

        then:
        query.andDo(MockMvcResultHandlers.print()).andExpect(MockMvcResultMatchers.status().isOk()).andReturn();

    }

}
