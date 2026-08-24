package org.hzero.platform.infra.repository.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.choerodon.core.exception.CommonException;
import io.choerodon.mybatis.helper.LanguageHelper;
import org.apache.commons.lang3.StringUtils;
import org.hzero.core.base.BaseConstants;
import org.hzero.core.redis.RedisHelper;
import org.hzero.core.util.AssertUtils;
import org.hzero.mybatis.base.impl.BaseRepositoryImpl;
import org.hzero.platform.domain.entity.StaticTextValue;
import org.hzero.platform.domain.repository.StaticTextValueRepository;
import org.hzero.platform.domain.vo.StaticTextValueVO;
import org.hzero.platform.infra.constant.FndConstants;
import org.hzero.platform.infra.constant.HpfmMsgCodeConstants;
import org.hzero.platform.infra.mapper.StaticTextValueMapper;
import org.hzero.platform.infra.util.Dates;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Date;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.TimeUnit;


/**
 * 平台静态信息 资源库实现
 *
 * @author fatao.liu@hand-china.com 2018-07-23 14:14:29
 */
@Component
public class StaticTextValueRepositoryImpl extends BaseRepositoryImpl<StaticTextValue> implements StaticTextValueRepository {

    @Autowired
    private StaticTextValueMapper staticTextValueMapper;
    @Autowired
    private RedisHelper redisHelper;
    @Autowired
    private ObjectMapper objectMapper;

    private static final Logger logger = LoggerFactory.getLogger(StaticTextValueRepositoryImpl.class);

    @Override
    public StaticTextValueVO getTextValue(Long organizationId, Long companyId, String textCode, String lang) {
        StaticTextValueVO staticTextValue = getStaticTextValue(organizationId, companyId, textCode, lang);
        if (staticTextValue == null) {
            // 查询0租户
            staticTextValue =
                    getStaticTextValue(BaseConstants.DEFAULT_TENANT_ID, companyId, textCode, lang);
            if (staticTextValue == null) {
                // 平台未定义该静态文本，返回空即可
                return null;
            }
        }
        return staticTextValue;

    }

    @Override
    public StaticTextValue selectTextValueById(Long textId) {
        return staticTextValueMapper.selectTextValueById(textId);
    }

    @Override
    public StaticTextValueVO getTextNullAble(Long organizationId, Long companyId, String textCode, String lang) {
        StaticTextValueVO staticTextValue = getStaticTextValue(organizationId, companyId, textCode, lang);
        if (staticTextValue == null) {
            staticTextValue =
                    getStaticTextValue(BaseConstants.DEFAULT_TENANT_ID, companyId, textCode, lang);
        }
        return staticTextValue;
    }

    private void setCache(String key, StaticTextValueVO value) {
        try {
            long expire = Dates.getSecondsFromNowToDate(value.getEndDate());
            value.setEndDate(null);
            redisHelper.strSet(key, objectMapper.writeValueAsString(value), expire, TimeUnit.SECONDS);
        } catch (JsonProcessingException e) {
            logger.error("getTextValue objectMapper serialize error.", e);
        }
    }

    private StaticTextValueVO getStaticTextValue(Long organizationId, Long companyId, String textCode, String lang) {
        if (StringUtils.isBlank(textCode)) {
            return null;
        }
        if (companyId == null) {
            companyId = BaseConstants.DEFAULT_TENANT_ID;
        }
        lang = Optional.ofNullable(lang).orElse(LanguageHelper.language());
        String key = FndConstants.CacheKey.STATIC_TEXT_LINE + ":" + organizationId + ":" + companyId + ":" + textCode + "." + lang;
        String json = redisHelper.strGet(key);
        if (StringUtils.isNotBlank(json)) {
            try {
                return objectMapper.readValue(json, StaticTextValueVO.class);
            } catch (IOException e) {
                logger.error("getTextValue objectMapper serialize error.", e);
            }
        }
        // json为空，说明缓存中无数据，查询数据库获取参数
        StaticTextValueVO value = staticTextValueMapper.selectTextByCode(organizationId, companyId, textCode, lang, new Date());
        if (value != null) {
            // 数据库中存在，将数据库中参数添加到缓存中并返回
            setCache(key, value);
            return value;
        } else {
            return null;
        }
    }
}
