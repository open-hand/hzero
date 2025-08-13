package org.hzero.admin.app.service.impl;

import com.alibaba.fastjson.JSON;
import io.choerodon.core.domain.Page;
import io.choerodon.core.exception.CommonException;
import io.choerodon.core.exception.ExceptionResponse;
import io.choerodon.mybatis.pagehelper.domain.PageRequest;
import org.hzero.admin.api.dto.Report;
import org.hzero.admin.api.eventhandler.MaintainServiceListener;
import org.hzero.admin.app.service.MaintainTableService;
import org.hzero.admin.domain.entity.Maintain;
import org.hzero.admin.domain.entity.MaintainTable;
import org.hzero.admin.domain.repository.MaintainRepository;
import org.hzero.admin.domain.repository.MaintainTableRepository;
import org.hzero.core.base.BaseAppService;
import org.hzero.core.endpoint.client.StringHttpTransporter;
import org.hzero.core.endpoint.request.EndpointKey;
import org.hzero.core.endpoint.request.StringEndpointRequest;
import org.hzero.mybatis.util.SecurityTokenUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.context.ResourceLoaderAware;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestClientException;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Properties;

/**
 * @author XCXCXCXCX
 * @date 2020/6/1 1:55 下午
 */
@Service
public class MaintainTableServiceImpl extends BaseAppService implements MaintainTableService, ResourceLoaderAware {

    private static final Logger LOGGER = LoggerFactory.getLogger(MaintainTableServiceImpl.class);

    private static final String ENABLE_KEY = "maintain.enable";
    private static final String RULE_MAP_KEY_PREFIX = "maintain.rule-map.";
    private static final String WRITE_MODE_TABLES = "write-mode-tables";
    private static final String READ_MODE_TABLES = "read-mode-tables";


    @Autowired
    private MaintainRepository maintainRepository;
    @Autowired
    private MaintainTableRepository maintainTableRepository;
    @Autowired
    private DiscoveryClient discoveryClient;
    @Autowired
    private StringHttpTransporter httpTransporter;
    @Autowired
    private MaintainServiceListener maintainServiceListener;
    private ResourceLoader resourceLoader;

    @Override
    public Page<MaintainTable> page(Long maintainId, String serviceCode, String tableName, PageRequest pageRequest) {
        return maintainTableRepository.pageMaintainTable(maintainId, serviceCode, tableName, pageRequest);
    }

    @Override
    public void insert(MaintainTable record) {
        validObject(record);
        assertInActiveState(record.getMaintainId());
        validUnique(record);
        maintainTableRepository.insertSelective(record);
    }

    private void validUnique(MaintainTable record) {
        Long maintainId = record.getMaintainId();
        String serviceCode = record.getServiceCode();
        String tableName = record.getTableName();
        maintainTableRepository.selectMaintainTable(maintainId, serviceCode, tableName);
    }

    @Override
    public void updateByPrimaryKey(MaintainTable record) {
        validObject(record);
        SecurityTokenUtils.validToken(record);
        assertInActiveState(record.getMaintainId());
        validUnique(record);
        maintainTableRepository.updateByPrimaryKey(record);
    }

    @Transactional(rollbackFor = RuntimeException.class)
    @Override
    public void batchUpdate(List<MaintainTable> maintainTables) {
        validList(maintainTables);
        for (MaintainTable table : maintainTables) {
            if (table.getMaintainTableId() == null) {
                insert(table);
            } else {
                updateByPrimaryKey(table);
            }
        }
    }

    @Override
    public void deleteByPrimaryKey(Long maintainId, Long maintainTableId) {
        assertInActiveState(maintainId);
        maintainTableRepository.deleteByPrimaryKey(maintainTableId);
    }

    @Override
    public Report enable(Long maintainId, String serviceCode) {
        assertActiveState(maintainId);
        Properties properties = generateEnableProperties(maintainId, serviceCode);
        Report report = transport(properties, serviceCode);
        maintainServiceListener.registerListener(serviceCode, properties);
        return report;
    }

    @Override
    public Report disable(Long maintainId, String serviceCode) {
        assertActiveState(maintainId);
        Properties properties = generateDisableProperties();
        Report report = transport(properties, serviceCode);
        maintainServiceListener.unregisterListener(serviceCode);
        return report;
    }

    private void assertInActiveState(Long maintainId) {
        Maintain maintain = maintainRepository.selectByPrimaryKey(maintainId);
        if (Maintain.State.valueOf(maintain.getState()) == Maintain.State.ACTIVE) {
            throw new CommonException("error.active.state");
        }
    }

    private void assertActiveState(Long maintainId) {
        Maintain maintain = maintainRepository.selectByPrimaryKey(maintainId);
        if (Maintain.State.valueOf(maintain.getState()) != Maintain.State.ACTIVE) {
            throw new CommonException("error.inactive.state");
        }
    }

    private Properties generateDisableProperties() {
        Properties properties = new Properties();
        properties.setProperty(ENABLE_KEY, "false");
        return properties;
    }

    private Properties generateEnableProperties(Long maintainId, String serviceCode) {
        @SuppressWarnings("unchecked")
        List<MaintainTable> maintainTables = maintainTableRepository.selectMaintainTables(maintainId, serviceCode);
        if (!CollectionUtils.isEmpty(maintainTables)) {
            Properties properties = new Properties();
            properties.setProperty(ENABLE_KEY, "true");
            for (MaintainTable table : maintainTables) {
                if (Objects.equals(serviceCode, table.getServiceCode())) {
                    String tableName = table.getTableName();
                    String mode = table.getMaintainMode();
                    properties.setProperty(RULE_MAP_KEY_PREFIX + tableName, mode);
                }
            }
            return properties;
        }
        return new Properties();
    }

    private Report transport(Properties properties, String serviceCode) {
        List<ServiceInstance> instances = discoveryClient.getInstances(serviceCode);
        byte[] requestBody = mapToBytes(properties);
        Map<String, String> errorMessages = new HashMap<>();
        for (ServiceInstance instance : instances) {
            String response = null;
            try {
                response = httpTransporter.transport(new StringEndpointRequest(instance, EndpointKey.MAINTAIN_ENDPOINT_KEY, HttpMethod.PUT, requestBody));
            } catch (RestClientException | IllegalArgumentException e) {
                errorMessages.put(instance.getHost(), e.getMessage());
                continue;
            }
            /**
             * 解析响应信息
             */
            if (!StringUtils.isEmpty(response)) {
                try {
                    @SuppressWarnings("unchecked")
                    Map<String, Object> responseMap = JSON.parseObject(response, Map.class);
                    if (responseMap.containsKey(ExceptionResponse.FILED_FAILED) && (Boolean) responseMap.get(ExceptionResponse.FILED_FAILED)) {
                        errorMessages.put(instance.getHost() + ":" + instance.getPort(), (String) responseMap.get(ExceptionResponse.FILED_MESSAGE));
                    }
                } catch (Throwable e) {
                    errorMessages.put(instance.getHost(), "response body [" + response + "] parsing exception");
                }
            }
        }
        return new Report(errorMessages);
    }

    private byte[] mapToBytes(Properties properties) {
        return JSON.toJSONBytes(properties);
    }


    @Override
    public void downloadTemplate(HttpServletResponse response) {
        Resource resource = resourceLoader.getResource("classpath:template/maintain/template.properties");
        if (!resource.exists()) {
            throw new CommonException("hadm.maintain.template-file.not-exist");
        }

        response.setContentType(MediaType.APPLICATION_OCTET_STREAM_VALUE + "; charset=utf-8");
        response.setHeader("Content-Disposition", "attachment; filename=" + (StringUtils.isEmpty(resource.getFilename()) ? "template" : resource.getFilename()));
        try (OutputStream os = response.getOutputStream();
             InputStream is = resource.getInputStream()) {
            int b;
            while ((b = is.read()) != -1) {
                os.write(b);
            }
            os.flush();
        } catch (IOException e) {
            LOGGER.error("download template error", e);
        }
    }

    @Override
    public void importData(Long maintainId, MultipartFile multipartFile) {
        try (InputStream is = multipartFile.getInputStream()) {
            Properties properties = new Properties();
            properties.load(is);
            importData(maintainId, properties);
        } catch (IOException e) {
            throw new CommonException("import data error", e);
        }
    }

    private void importData(Long maintainId, Properties properties) {
        for (String key : properties.stringPropertyNames()) {
            String[] parts = key.split("\\.");
            if (parts.length != 2) {
                throw new CommonException("hadm.abnormal.import-data.format");
            }
            String service = parts[0];
            String mode = parts[1];
            String[] tables = properties.getProperty(key, "").split(",");
            importData(maintainId, service, mode, tables);
        }
    }

    private void importData(Long maintainId, String service, String mode, String[] tables) {
        if (WRITE_MODE_TABLES.equals(mode)) {
            mode = "WRITE";
        } else if (READ_MODE_TABLES.equals(mode)) {
            mode = "READ";
        } else {
            return;
        }
        for (String table : tables) {
            if (StringUtils.isEmpty(table)) {
                continue;
            }
            if (existMaintainTable(maintainId, service, table)) {
                throw new CommonException("hadm.error.service_table_unique");
            }
            maintainTableRepository.insertSelective(buildMaintainTable(maintainId, service, mode, table));
        }
    }

    private boolean existMaintainTable(Long maintainId, String service, String table) {
        return maintainId != null
                && !StringUtils.isEmpty(service)
                && !StringUtils.isEmpty(table)
                && maintainTableRepository.selectMaintainTable(maintainId, service, table) != null;
    }

    private MaintainTable buildMaintainTable(Long maintainId, String service, String mode, String table) {
        MaintainTable maintainTable = new MaintainTable();
        maintainTable.setMaintainId(maintainId);
        maintainTable.setServiceCode(service);
        maintainTable.setTableName(table);
        maintainTable.setMaintainMode(mode);
        return maintainTable;
    }


    @Override
    public void setResourceLoader(ResourceLoader resourceLoader) {
        this.resourceLoader = resourceLoader;
    }
}
