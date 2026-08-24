package org.hzero.admin.api.controller.v1

import org.hzero.admin.domain.entity.Maintain
import org.hzero.admin.domain.entity.MaintainTable
import org.hzero.admin.domain.repository.MaintainRepository
import org.hzero.admin.domain.repository.MaintainTableRepository
import org.mockito.internal.matchers.Contains
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders
import org.springframework.test.web.servlet.result.MockMvcResultHandlers
import org.springframework.test.web.servlet.result.MockMvcResultMatchers
import org.springframework.test.web.servlet.setup.MockMvcBuilders
import spock.lang.Title

/**
 * @author XCXCXCXCX* @date 2020/6/24 3:29 下午
 */
@Title("运维规则接口测试")
class MaintainSiteControllerSpec extends ControllerSpec {

    @Autowired
    private MaintainSiteController controller;
    @Autowired
    private MaintainRepository maintainRepository;
    @Autowired
    private MaintainTableRepository maintainTableRepository;

    private MockMvc mockMvc;
    private Maintain maintain;

    def setup(){
        mockMvc = MockMvcBuilders.standaloneSetup(controller).build();
        maintain = new Maintain();
        maintain.setMaintainVersion("unit_test_version");
        maintain.setDescription("单元测试数据");
        maintain.setState("UNUSED");
    }

    def "创建运维规则"() {

        expect:
        mockMvc.perform(MockMvcRequestBuilders.post("/v1/maintains")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(maintain)))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().is2xxSuccessful());
    }

    def "更新运维规则"() {
        given:
        maintainRepository.insertSelective(maintain);
        maintain.setDescription("单元测试数据更新");

        expect:
        mockMvc.perform(MockMvcRequestBuilders.put("/v1/maintains")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(maintain)))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().is2xxSuccessful())
                .andExpect(MockMvcResultMatchers.content().string(new Contains("单元测试数据更新")));
    }

    def "删除运维规则"() {
        given:
        maintainRepository.insertSelective(maintain);

        expect:
        mockMvc.perform(MockMvcRequestBuilders.delete("/v1/maintains")
                .contentType(MediaType.APPLICATION_JSON)
                .param("maintainId", String.valueOf(maintain.maintainId)))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().is2xxSuccessful());
    }

    def "分页查询运维规则"() {
        given:
        maintainRepository.insertSelective(maintain);

        expect:
        mockMvc.perform(MockMvcRequestBuilders.get("/v1/maintains")
                .contentType(MediaType.APPLICATION_JSON)
                .param("maintainVersion", maintain.maintainVersion))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().is2xxSuccessful())
                .andExpect(MockMvcResultMatchers.content().string(new Contains("单元测试数据")));
    }

    def "更新运维状态"() {
        given:
        maintainRepository.insertSelective(maintain);

        expect:
        mockMvc.perform(MockMvcRequestBuilders.post("/v1/maintains/state")
                .contentType(MediaType.APPLICATION_JSON)
                .param("maintainId", String.valueOf(maintain.maintainId))
                .param("from", maintain.state)
                .param("to", Maintain.State.UNUSED.name()))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().is2xxSuccessful());
        Maintain result = maintainRepository.selectByPrimaryKey(maintain.maintainId);
        result.state == Maintain.State.UNUSED.name()
    }

    def "获取运维服务"() {
        given:
        maintainRepository.insertSelective(maintain);
        MaintainTable table = new MaintainTable();
        table.setMaintainId(maintain.maintainId);
        table.setServiceCode(serviceName);
        table.setMaintainMode("READ");
        table.setTableName("unit_test_table");
        maintainTableRepository.insertSelective(table);

        expect:
        mockMvc.perform(MockMvcRequestBuilders.get("/v1/maintains/services")
                .contentType(MediaType.APPLICATION_JSON)
                .param("maintainId", String.valueOf(maintain.maintainId)))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().is2xxSuccessful())
                .andExpect(MockMvcResultMatchers.content().string(new Contains(serviceName)));

        where:
        serviceName << ["hzero-unit-test1", "hzero-unit-test2", "hzero-unit-test3"]
    }




}
