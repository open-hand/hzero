package org.hzero.admin.api.controller.v1

import io.choerodon.core.exception.ExceptionResponse
import org.hzero.admin.domain.entity.HService
import org.hzero.admin.domain.entity.ServiceRoute
import org.hzero.admin.domain.repository.ServiceRepository
import org.hzero.admin.domain.repository.ServiceRouteRepository
import org.hzero.core.exception.DangerousRouteException
import org.hzero.core.message.MessageAccessor
import org.mockito.internal.matchers.Contains
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders
import org.springframework.test.web.servlet.result.MockMvcResultMatchers
import org.springframework.test.web.servlet.setup.MockMvcBuilders
import spock.lang.Title
import spock.lang.Unroll

/**
 * @author XCXCXCXCX* @date 2020/6/23 4:52 下午
 */
@Title("路由管理接口测试")
@Unroll
class RouteSiteControllerSpec extends ControllerSpec {

    @Autowired
    private RouteSiteController controller;
    @Autowired
    private ServiceRepository serviceRepository;
    @Autowired
    private ServiceRouteRepository routeRepository;

    private MockMvc mockMvc;

    def setup() {
        mockMvc = MockMvcBuilders.standaloneSetup(controller).build();
    }

    def "创建路由"() {
        given:
        HService service = new HService();
        service.serviceCode = "hzero-test";
        service.serviceName = "hzero-test";
        service.serviceLogo = "test-logo";
        serviceRepository.insertSelective(service);

        ServiceRoute route = new ServiceRoute();
        route.serviceCode = service.getServiceCode();
        route.serviceName = service.getServiceName();
        route.serviceId = service.serviceId;
        route.name = "test-route";
        route.path = "/**";
        route.stripPrefix = 0;

        when:
        mockMvc.perform(MockMvcRequestBuilders.post("/v1/routes")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsBytes(route)));

        then:
        def ex = thrown(Exception);
        ex.getCause().class == DangerousRouteException.class;
        DangerousRouteException targetEx = ex.getCause() as DangerousRouteException;
        ExceptionResponse er = new ExceptionResponse(MessageAccessor.getMessage(targetEx.getCode(), targetEx.getParameters()));
        println("message tip: " + er.getMessage());
    }

    def "更新路由"() {
        given:
        HService service = new HService();
        service.serviceCode = "hzero-test";
        service.serviceName = "hzero-test";
        service.serviceLogo = "test-logo";
        serviceRepository.insertSelective(service);

        ServiceRoute route = new ServiceRoute();
        route.serviceCode = service.getServiceCode();
        route.serviceName = service.getServiceName();
        route.serviceId = service.serviceId;
        route.name = "test-route";
        route.path = "/test-route/**";
        route.stripPrefix = 0;
        routeRepository.insertSelective(route);
        route.setPath("/**");

        when:
        mockMvc.perform(MockMvcRequestBuilders.put("/v1/routes")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsBytes(route)));

        then:
        def ex = thrown(Exception);
        ex.getCause().class == DangerousRouteException.class;
        DangerousRouteException targetEx = ex.getCause() as DangerousRouteException;
        ExceptionResponse er = new ExceptionResponse(MessageAccessor.getMessage(targetEx.getCode(), targetEx.getParameters()));
        println("message tip: " + er.getMessage());
    }

    def "根据ID查询服务路由 #id"() {
        given:
        HService service = new HService();
        service.serviceCode = "hzero-test";
        service.serviceName = "hzero-test";
        service.serviceLogo = "test-logo";
        serviceRepository.insertSelective(service);

        ServiceRoute route1 = new ServiceRoute();
        route1.setServiceRouteId(19991);
        route1.serviceCode = service.getServiceCode();
        route1.serviceName = service.getServiceName();
        route1.serviceId = service.serviceId;
        route1.name = "test-route1";
        route1.path = "/test-route1/**";
        route1.stripPrefix = 0;
        routeRepository.insertSelective(route1);
        ServiceRoute route2 = new ServiceRoute();
        route2.setServiceRouteId(19992);
        route2.serviceCode = service.getServiceCode();
        route2.serviceName = service.getServiceName();
        route2.serviceId = service.serviceId;
        route2.name = "test-route2";
        route2.path = "/test-route2/**";
        route2.stripPrefix = 0;
        routeRepository.insertSelective(route2);
        ServiceRoute route3 = new ServiceRoute();
        route3.setServiceRouteId(19993);
        route3.serviceCode = service.getServiceCode();
        route3.serviceName = service.getServiceName();
        route3.serviceId = service.serviceId;
        route3.name = "test-route3";
        route3.path = "/test-route3/**";
        route3.stripPrefix = 0;
        routeRepository.insertSelective(route3);

        expect:
        mockMvc.perform(MockMvcRequestBuilders.get("/v1/routes/" + id))
                .andExpect(MockMvcResultMatchers.content().string(new Contains(expectResult)));

        where:
        id     ||  expectResult
        19991  ||  "test-route1"
        19992  ||  "test-route2"
        19993  ||  "test-route3"

    }

}
