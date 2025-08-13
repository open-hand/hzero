package org.hzero.swagger.app.impl;

import java.io.IOException;
import java.util.*;

import org.hzero.swagger.api.dto.swagger.ControllerDTO;
import org.hzero.swagger.api.dto.swagger.ParameterDTO;
import org.hzero.swagger.api.dto.swagger.PathDTO;
import org.hzero.swagger.api.dto.swagger.ResponseDTO;
import org.hzero.swagger.app.ApiService;
import org.hzero.swagger.app.DocumentService;
import org.hzero.swagger.infra.util.ManualPageHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import io.choerodon.core.domain.Page;
import io.choerodon.core.exception.CommonException;
import io.choerodon.mybatis.pagehelper.domain.PageRequest;

/**
 * @author superlee
 */
@Service
public class ApiServiceImpl implements ApiService {

    private static final Logger logger = LoggerFactory.getLogger(ApiServiceImpl.class);

    private static final String DESCRIPTION = "description";

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Autowired
    private DocumentService documentService;


    @Override
    public Page<ControllerDTO> getControllers(String name, String version, PageRequest pageRequest, Map<String, Object> map) {
        String json;
        try {
            json = documentService.getSwaggerJson(name, version);
        } catch (IOException e) {
            logger.error("fetch swagger json error, service: {}, version: {}, exception: {}", name, version, e.getMessage());
            throw new CommonException("error.service.not.run", name, version);
        }
        return Optional.ofNullable(json)
                .map(j -> ManualPageHelper.postPage(processJson2ControllerDTO(j), pageRequest, map))
                .orElseThrow(() -> new CommonException("error.service.swaggerJson.empty"));
    }

    private List<ControllerDTO> processJson2ControllerDTO(String json) {
        List<ControllerDTO> controllers;
        try {
            JsonNode node = objectMapper.readTree(json);
            controllers = processControllers(node);
            processPaths(node, controllers);
        } catch (IOException e) {
            throw new CommonException("error.parseJson");
        }
        return controllers;
    }

    private void processPaths(JsonNode node, List<ControllerDTO> controllers) {
        JsonNode pathNode = node.get("paths");
        Iterator<String> urlIterator = pathNode.fieldNames();
        while (urlIterator.hasNext()) {
            PathDTO path = new PathDTO();
            String url = urlIterator.next();
            path.setUrl(url);
            JsonNode methodNode = pathNode.get(url);
            Iterator<String> methodIterator = methodNode.fieldNames();
            while (methodIterator.hasNext()) {
                String method = methodIterator.next();
                path.setMethod(method);
                JsonNode jsonNode = methodNode.findValue(method);
                JsonNode tagNode = jsonNode.get("tags");
                for (int i = 0; i < tagNode.size(); i++) {
                    String tag = tagNode.get(i).asText();
                    controllers.forEach(c -> {
                        Set<PathDTO> paths = c.getPaths();
                        if (tag.equals(c.getName())) {
                            path.setRefController(c.getName());
                            paths.add(path);
                        }
                    });
                }
                path.setRemark(Optional.ofNullable(jsonNode.get("summary")).map(JsonNode::asText).orElse(null));
                path.setDescription(Optional.ofNullable(jsonNode.get(DESCRIPTION)).map(JsonNode::asText).orElse(null));
                path.setOperationId(Optional.ofNullable(jsonNode.get("operationId")).map(JsonNode::asText).orElse(null));
                path.setOperationId(jsonNode.get("operationId").asText());
                processConsumes(path, jsonNode);
                processProduces(path, jsonNode);
                processParameters(path, jsonNode);
                processResponses(path, jsonNode);
            }
        }
    }

    private void processResponses(PathDTO path, JsonNode jsonNode) {
        JsonNode responseNode = jsonNode.get("responses");
        List<ResponseDTO> responses = new ArrayList<>();
        Iterator<String> responseIterator = responseNode.fieldNames();
        while (responseIterator.hasNext()) {
            String status = responseIterator.next();
            JsonNode node = responseNode.get(status);
            ResponseDTO response = new ResponseDTO();
            response.setHttpStatus(status);
            response.setDescription(node.get(DESCRIPTION).asText());
            JsonNode schemaNode = node.get("schema");
            List<String> schemas = new ArrayList<>();
            if (schemaNode != null) {
                for (int i = 0; i < schemaNode.size(); i++) {
                    schemas.add(Optional.ofNullable(schemaNode.get(i)).map(s -> s.asText()).orElse(null));
                }
            }
            response.setSchema(schemas);
            responses.add(response);
        }
        path.setResponses(responses);
    }

    private void processConsumes(PathDTO path, JsonNode jsonNode) {
        JsonNode consumeNode = jsonNode.get("consumes");
        List<String> consumes = new ArrayList<>();
        for (int i = 0; i < consumeNode.size(); i++) {
            consumes.add(consumeNode.get(i).asText());
        }
        path.setConsumes(consumes);
    }

    private void processProduces(PathDTO path, JsonNode jsonNode) {
        JsonNode produceNode = jsonNode.get("produces");
        List<String> produces = new ArrayList<>();
        for (int i = 0; i < produceNode.size(); i++) {
            produces.add(produceNode.get(i).asText());
        }
        path.setProduces(produces);
    }

    private void processParameters(PathDTO path, JsonNode jsonNode) {
        JsonNode parameterNode = jsonNode.get("parameters");
        List<ParameterDTO> parameters = new ArrayList<>();
        if (parameterNode != null) {
            for (int i = 0; i < parameterNode.size(); i++) {
                try {
                    ParameterDTO parameter = objectMapper.treeToValue(parameterNode.get(i), ParameterDTO.class);
                    parameters.add(parameter);
                } catch (JsonProcessingException e) {
                    logger.info("jsonNode to parameterDTO failed, exception: {}", e.getMessage());
                }
            }
        }
        path.setParameters(parameters);
    }

    private List<ControllerDTO> processControllers(JsonNode node) {
        List<ControllerDTO> controllers = new ArrayList<>();
        JsonNode tagNodes = node.get("tags");
        for (JsonNode jsonNode : tagNodes) {
            String name = jsonNode.findValue("name").asText();
            String description = jsonNode.findValue(DESCRIPTION).asText();
            ControllerDTO controller = new ControllerDTO();
            controller.setName(name);
            controller.setDescription(description);
            controller.setPaths(new TreeSet<>());
            controllers.add(controller);
        }
        return controllers;
    }
}
