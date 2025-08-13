package org.hzero.boot.admin.transport;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.hzero.boot.admin.exception.TransportException;
import org.hzero.boot.admin.registration.Registration;
import org.hzero.common.HZeroService;
import org.hzero.core.endpoint.client.BaseHttpTransporter;
import org.hzero.core.endpoint.request.StaticEndpoint;
import org.hzero.core.endpoint.request.StaticEndpointHttpRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.http.client.SimpleClientHttpRequestFactory;

import java.util.List;

/**
 * @author XCXCXCXCX
 * @date 2020/6/11 9:02 上午
 */
public class AdminTransport implements Transport {

    private static final Logger LOGGER = LoggerFactory.getLogger(AdminTransport.class);

    private AddressService addressService;

    private BaseHttpTransporter<Response> httpTransporter;

    private StaticEndpoint staticEndpoint;

    private final ObjectMapper objectMapper = new ObjectMapper();

    public AdminTransport(AddressService addressService, StaticEndpoint staticEndpoint, int connectTimeout, int readTimeout) {
        this.addressService = addressService;
        this.httpTransporter = new BaseHttpTransporter<>();
        this.httpTransporter.configure(restTemplate ->
                {
                    restTemplate.getMessageConverters().add(new RegistrationHttpMessageConverter());
                    SimpleClientHttpRequestFactory requestFactory = new SimpleClientHttpRequestFactory();
                    requestFactory.setConnectTimeout(connectTimeout);
                    requestFactory.setReadTimeout(readTimeout);
                    restTemplate.setRequestFactory(requestFactory);
                }
        );
        this.staticEndpoint = staticEndpoint;
    }

    @Override
    public void transport(Registration registration) throws TransportException {
        requestAdmin(registration, staticEndpoint);
    }

    private void requestAdmin(Registration registration, StaticEndpoint staticEndpoint) throws TransportException {
        String adminService = HZeroService.getRealName(HZeroService.Admin.NAME);
        List<ServiceInstance> instances = addressService.getInstances();
        byte[] requestBody = toBytes(registration);
        for (ServiceInstance instance : instances) {
            Response response = null;
            try {
                response = httpTransporter.transport(new StaticEndpointHttpRequest<>(instance, staticEndpoint, requestBody, Response.class));
            } catch (Exception e) {
                LOGGER.error("request [" + adminService + "|" + instance.getHost() + ":" + instance.getPort() + "] failed, try to request other instance.", e);
                continue;
            }
            if (!response.isSuccess()) {
                LOGGER.error("request [" + adminService + "|" + instance.getHost() + ":" + instance.getPort() + "] failed, try to request other instance. msg:\n {}", response.getMessage());
                continue;
            }
            return;
        }
        throw new TransportException("request [" + adminService + "|ALL_INSTANCES] failed");
    }

    private byte[] toBytes(Registration registration) {
        try {
            return objectMapper.writeValueAsBytes(registration);
        } catch (JsonProcessingException e) {
            return new byte[0];
        }
    }
}
