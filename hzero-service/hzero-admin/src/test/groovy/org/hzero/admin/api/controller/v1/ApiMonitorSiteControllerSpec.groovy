package org.hzero.admin.api.controller.v1

import org.hzero.admin.domain.entity.ApiMonitor
import org.hzero.admin.domain.repository.ApiMonitorRepository
import org.mockito.internal.matchers.NotNull
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders
import org.springframework.test.web.servlet.result.MockMvcResultHandlers
import org.springframework.test.web.servlet.result.MockMvcResultMatchers
import org.springframework.test.web.servlet.setup.MockMvcBuilders
import spock.lang.Title

/**
 * @author XCXCXCXCX* @date 2020/6/23 10:03 上午
 */
@Title("API监控接口测试")
class ApiMonitorSiteControllerSpec extends ControllerSpec {

    @Autowired
    private ApiMonitorSiteController controller;
    @Autowired
    private ApiMonitorRepository apiMonitorRepository;

    private MockMvc mockMvc;

    def setup() {
        mockMvc = MockMvcBuilders.standaloneSetup(controller).build();
    }

    def "分页查询API监控分析"() {
        given:
        ApiMonitor apiMonitor = new ApiMonitor();
        apiMonitor.setMonitorRuleId(999);
        apiMonitor.setMonitorUrl("testUrl");
        apiMonitor.setMonitorKey("testKey");
        apiMonitor.setMaxStatistics(1);
        apiMonitor.setMinStatistics(1);
        apiMonitor.setSumStatistics(1);
        apiMonitor.setSumFailedStatistics(1);
        apiMonitor.setAvgCount(1);
        apiMonitor.setAvgStatistics(1);
        apiMonitor.setAvgFailedStatistics(1);
        apiMonitor.setStartDate(new Date());
        apiMonitor.setEndDate(new Date());
        apiMonitorRepository.insertSelective(apiMonitor);

        when:
        def page = mockMvc.perform(MockMvcRequestBuilders.get("/v1/api-monitors")
                .param("monitorRuleId", "999"));

        then:
        page.andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers
                        .status().isOk())
                .andExpect(MockMvcResultMatchers.content().string(NotNull.NOT_NULL))
                .andReturn();

    }

}
