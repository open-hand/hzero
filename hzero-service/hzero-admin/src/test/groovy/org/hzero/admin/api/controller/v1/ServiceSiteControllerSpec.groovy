package org.hzero.admin.api.controller.v1


import org.hzero.admin.domain.entity.HService
import org.hzero.admin.domain.repository.ServiceRepository
import org.mockito.internal.matchers.Contains
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders
import org.springframework.test.web.servlet.result.MockMvcResultHandlers
import org.springframework.test.web.servlet.result.MockMvcResultMatchers
import org.springframework.test.web.servlet.setup.MockMvcBuilders

/**
 * @author XCXCXCXCX* @date 2020/6/23 8:48 下午
 */
class ServiceSiteControllerSpec extends ControllerSpec {

    @Autowired
    private ServiceSiteController controller;
    @Autowired
    private ServiceRepository serviceRepository;

    private MockMvc mockMvc;
    private HService service;

    def setup() {
        mockMvc = MockMvcBuilders.standaloneSetup(controller).build();
        service = new HService();
        service.serviceCode = "hzero-test";
        service.serviceName = "hzero-test";
        service.serviceLogo = "test-logo";
    }

    def "分页查询服务列表"() {
        given:
        serviceRepository.insertSelective(service);

        expect:
        mockMvc.perform(MockMvcRequestBuilders.get("/v1/services")
                .param("serviceCode", service.serviceCode))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().string(new Contains(result)));

        where:
        serviceCode << "hzero-test"
        result      << "hzero-test"
    }

    def "创建服务"() {
        expect:
        mockMvc.perform(MockMvcRequestBuilders.post("/v1/services")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(service)))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isOk());
    }

    def "修改服务"() {
        given:
        serviceRepository.insertSelective(service);
        service.setServiceLogo(serviceLogo);

        expect:
        mockMvc.perform(MockMvcRequestBuilders.put("/v1/services")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(service)))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().string(new Contains(result)));

        where:
        serviceLogo << "test-logo2"
        result      << "test-logo2"
    }

    def "删除服务"() {
        given:
        serviceRepository.insertSelective(service);

        expect:
        mockMvc.perform(MockMvcRequestBuilders.delete("/v1/services")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(service)))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().is2xxSuccessful());

    }

}
