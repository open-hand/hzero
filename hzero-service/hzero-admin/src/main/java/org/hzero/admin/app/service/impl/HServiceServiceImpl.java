package org.hzero.admin.app.service.impl;

import io.choerodon.core.domain.Page;
import io.choerodon.core.exception.CommonException;
import io.choerodon.mybatis.pagehelper.PageHelper;
import io.choerodon.mybatis.pagehelper.domain.PageRequest;
import org.hzero.admin.app.service.HServiceService;
import org.hzero.admin.domain.entity.HService;
import org.hzero.admin.domain.entity.ServiceRoute;
import org.hzero.admin.domain.entity.ServiceVersion;
import org.hzero.admin.domain.repository.ServiceRepository;
import org.hzero.admin.domain.repository.ServiceRouteRepository;
import org.hzero.admin.domain.repository.ServiceVersionRepository;
import org.hzero.core.base.BaseAppService;
import org.hzero.core.util.FilenameUtils;
import org.hzero.mybatis.helper.SecurityTokenHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.representer.Representer;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 应用服务应用服务默认实现
 *
 * @author bojiangzhou
 * @author zhiying.dong@hand-china.com 2018-12-14 10:35:51
 */
@Service
public class HServiceServiceImpl extends BaseAppService implements HServiceService {

    @Lazy
    @Autowired
    private ServiceRepository serviceRepository;
    @Lazy
    @Autowired
    private ServiceRouteRepository routeRepository;
    @Lazy
    @Autowired
    private ServiceVersionRepository versionRepository;

    @Override
    @Transactional(propagation = Propagation.SUPPORTS, rollbackFor = Exception.class)
    public HService createService(HService service) {
        validObject(service);

        HService param = new HService();
        param.setServiceCode(service.getServiceCode());
        HService self = serviceRepository.selectOne(param);
        if (self == null) {
            serviceRepository.insertSelective(service);
            self = service;
        } else {
            throw new CommonException("hadm.error.service.serviceCodeExists", service.getServiceCode());
        }
        return self;
    }

    @Override
    public Page<HService> pageService(HService param, PageRequest pageRequest) {
        return PageHelper.doPage(pageRequest.getPage(), pageRequest.getSize(), () -> selectServices(param));
    }

    @Override
    public List<HService> selectServices(HService param) {
        return serviceRepository.selectDefaultServices(param);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public HService updateService(HService service) {
        SecurityTokenHelper.validToken(service);

        serviceRepository.updateOptional(service, HService.FIELD_SERVICE_CODE, HService.FIELD_SERVICE_NAME, HService.FIELD_SERVICE_LOGO);
        return service;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void removeService(HService hService) {
        SecurityTokenHelper.validToken(hService);

        hService.checkServiceRef(routeRepository);
        serviceRepository.deleteByPrimaryKey(hService.getServiceId());
        /**
         * 1. 删除关联路由
         */
        routeRepository.delete(new ServiceRoute().setServiceId(hService.getServiceId()));

        /**
         * 2. 删除关联版本
         */
        versionRepository.delete(new ServiceVersion().setServiceId(hService.getServiceId()));

    }

    @Override
    public void downloadYml(HService service, HttpServletRequest request, HttpServletResponse response) {
        // 查询服务版本信息
        List<HService> serviceList = serviceRepository.selectDefaultServicesWithVersion(service);
        List<Map<String, Object>> list = new ArrayList<>();
        serviceList.forEach(item -> {
            Map<String, Object> map = new HashMap<>(6);
            map.put(HService.FIELD_SERVICE_CODE, item.getServiceCode());
            map.put(HService.FIELD_SERVICE_NAME, item.getServiceName());
            map.put(HService.FIELD_VERSION_NUMBER, item.getVersionNumber());
            list.add(map);
        });
        try {
            String encodeFilename = FilenameUtils.encodeFileName(request, "hzero.yml");
            response.setHeader("Content-disposition", "attachment; filename=" + encodeFilename);
            response.setContentType("application/octet-stream;charset=UTF-8");
            response.addHeader("Pragma", "no-cache");
            response.addHeader("Cache-Control", "no-cache");
            Representer representer = new Representer();
            representer.setDefaultFlowStyle(DumperOptions.FlowStyle.BLOCK);
            Yaml yaml = new Yaml(representer);
            Map<String, Object> result = new HashMap<>(3);
            result.put("service", list);
            yaml.dump(result, new OutputStreamWriter(response.getOutputStream(), StandardCharsets.UTF_8));
        } catch (IOException e) {
            throw new CommonException(e);
        }
    }

    @Override
    public HService selectServiceDetails(Long serviceId) {
        return serviceRepository.selectServiceDetails(serviceId);
    }
}
