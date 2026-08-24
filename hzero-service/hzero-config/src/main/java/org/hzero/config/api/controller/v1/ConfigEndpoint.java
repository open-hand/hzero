package org.hzero.config.api.controller.v1;

import org.hzero.config.api.dto.ConfigItemPublishDTO;
import org.hzero.config.api.dto.ConfigListenDTO;
import org.hzero.config.api.dto.ConfigPublishDTO;
import org.hzero.config.app.service.ServiceConfigService;
import org.hzero.config.app.service.impl.DefaultConfigListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/**
 * @author XCXCXCXCX
 * @date 2020/4/28 11:45 上午
 */
@RestController
@RequestMapping("config")
public class ConfigEndpoint {

    @Autowired
    private ServiceConfigService configService;

    @GetMapping("/fetch")
    public Map<String, Object> fetch(@RequestParam("serviceName") String serviceName,
                                     @RequestParam("label") String label) {
        return configService.getConfig(serviceName, label);
    }

    @PostMapping("/publish")
    public void publish(@RequestBody ConfigPublishDTO dto) {
        configService.publishConfig(dto.getServiceName(), dto.getLabel(), dto.getFileType(), dto.getContent());
    }

    @PostMapping("/publish-kv")
    public void publishKv(@RequestBody ConfigItemPublishDTO dto) {
        configService.publishConfigItem(dto.getServiceName(), dto.getLabel(), dto.getKey(), dto.getValue());
    }

    @PostMapping("/listen")
    public void listen(@RequestBody ConfigListenDTO dto) {
        configService.registerListener(dto.getServiceName(), dto.getLabel(), new DefaultConfigListener(dto.getKeys(), dto.getNotifyAddr()));
    }
}
