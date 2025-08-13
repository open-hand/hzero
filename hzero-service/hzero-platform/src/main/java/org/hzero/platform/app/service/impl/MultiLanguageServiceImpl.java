package org.hzero.platform.app.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.choerodon.core.exception.CommonException;
import org.apache.commons.lang3.StringUtils;
import org.hzero.core.base.BaseConstants;
import org.hzero.core.util.ResponseUtils;
import org.hzero.mybatis.domian.MultiLanguage;
import org.hzero.mybatis.domian.SecurityTokenEntity;
import org.hzero.mybatis.util.SecurityTokenUtils;
import org.hzero.platform.app.service.MultiLanguageService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.List;

/**
 * <p>
 * 多语言接口实现类
 * </p>
 *
 * @author qingsheng.chen 2018/9/16 星期日 16:49
 */
@Service
public class MultiLanguageServiceImpl implements MultiLanguageService {
    private static final Logger logger = LoggerFactory.getLogger(MultiLanguageServiceImpl.class);
    private RestTemplate restTemplate;
    private ObjectMapper objectMapper;
    @Value("${hzero.platform.multi-language.request.protocol:http}")
    private String protocol;

    @Autowired
    public MultiLanguageServiceImpl(RestTemplate restTemplate, ObjectMapper objectMapper) {
        this.restTemplate = restTemplate;
        this.objectMapper = objectMapper;
    }

    @Override
    public List<MultiLanguage> responseListMultiLanguage(String token, String fieldName) throws JsonProcessingException, UnsupportedEncodingException {
        if (StringUtils.isEmpty(token)) {
            return org.hzero.mybatis.service.impl.MultiLanguageServiceImpl.emptyMultiLanguage();
        }
        SecurityTokenEntity securityTokenEntity = SecurityTokenUtils.getEntity(token);
        logger.debug("Select MultiLanguage : {}", securityTokenEntity);
        if (securityTokenEntity == null) {
            throw new CommonException(BaseConstants.ErrorCode.DATA_INVALID);
        }
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON_UTF8);
        ResponseEntity<String> result = restTemplate.exchange(protocol + "://"
                        + securityTokenEntity.getApplicationName()
                        + (StringUtils.isNotBlank(securityTokenEntity.getContextPath()) ? ("/" + StringUtils.strip(securityTokenEntity.getContextPath(), "/")) : "")
                        + "/v1/hidden/multi-language?className="
                        + URLEncoder.encode(securityTokenEntity.getClassName(), BaseConstants.DEFAULT_CHARSET)
                        + "&fieldName=" + fieldName,
                HttpMethod.POST,
                new HttpEntity<>(objectMapper.writeValueAsString(securityTokenEntity.getPkValue()), headers),
                String.class);
        return ResponseUtils.getResponse(result, new TypeReference<List<MultiLanguage>>() {
        });
    }
}
