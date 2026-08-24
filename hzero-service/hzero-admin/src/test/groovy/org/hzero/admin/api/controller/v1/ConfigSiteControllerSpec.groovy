package org.hzero.admin.api.controller.v1


import org.hzero.admin.domain.entity.HService
import org.hzero.admin.domain.entity.ServiceConfig
import org.hzero.admin.domain.repository.ServiceConfigRepository
import org.hzero.admin.domain.repository.ServiceRepository
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
 * @author XCXCXCXCX* @date 2020/6/23 3:25 下午
 */
@Title("配置管理接口测试")
class ConfigSiteControllerSpec extends ControllerSpec {

    @Autowired
    private ConfigSiteController controller;
    @Autowired
    private ServiceRepository serviceRepository;
    @Autowired
    private ServiceConfigRepository configRepository;

    private MockMvc mockMvc;
    private HService service;

    def setup() {
        mockMvc = MockMvcBuilders.standaloneSetup(controller).build();
        service = new HService();
        service.serviceCode = "hzero-test";
        service.serviceName = "hzero-test";
        service.serviceLogo = "test-logo";
        serviceRepository.insertSelective(service);
    }

    def "创建配置"() {

        given:
        ServiceConfig config = new ServiceConfig();
        config.serviceId = service.getServiceId();
        config.serviceCode = service.getServiceCode();
        config.configVersion = "1.1.1";
        config.configYaml = "test: test";

        when:
        def insert = mockMvc.perform(MockMvcRequestBuilders.post("/v1/configs")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsBytes(config)));

        then:
        insert.andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andReturn();
    }

    def "更新配置"() {

        given:
        ServiceConfig config = new ServiceConfig();
        config.serviceId = service.getServiceId();
        config.serviceCode = service.getServiceCode();
        config.configVersion = "1.1.1";
        config.configYaml = "test: test";
        configRepository.insertSelective(config);

        when:
        config.configYaml = "test: test1";
        def update = mockMvc.perform(MockMvcRequestBuilders.put("/v1/configs")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsBytes(config)));

        then:
        update.andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().string(new Contains("test: test1")))
                .andReturn();
    }

    def "删除配置"() {

        given:
        ServiceConfig config = new ServiceConfig();
        config.serviceId = service.getServiceId();
        config.serviceCode = service.getServiceCode();
        config.configVersion = "1.1.1";
        config.configYaml = "test: test";
        configRepository.insertSelective(config);

        when:
        def delete = mockMvc.perform(MockMvcRequestBuilders.delete("/v1/configs")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsBytes(config)));

        then:
        delete.andExpect(MockMvcResultMatchers.status().is2xxSuccessful());
    }

}
