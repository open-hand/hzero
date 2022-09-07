package org.hzero.boot.message;

import java.util.HashMap;
import java.util.Map;

import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.hzero.boot.message.config.MessageClientProperties;
import org.hzero.boot.message.entity.Message;
import org.hzero.boot.message.entity.MessageTemplate;
import org.hzero.boot.message.feign.MessageRemoteService;
import org.hzero.boot.message.service.MessageGenerator;
import org.hzero.boot.message.util.VelocityUtils;
import org.hzero.core.base.BaseConstants;
import org.hzero.core.redis.RedisHelper;
import org.hzero.mybatis.impl.DefaultDynamicSqlMapper;
import org.springframework.context.ApplicationContext;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import io.choerodon.core.convertor.ApplicationContextHelper;
import io.choerodon.core.exception.CommonException;

/**
 * @author qingsheng.chen@hand-china.com 2019-06-20 15:11
 */
public class SqlMessageGenerator extends DefaultMessageGenerator implements MessageGenerator {

    private static final String TENANT_ID = "tenantId";
    private static final String ORGANIZATION_ID = "organizationId";
    private static final String LANG = "lang";

    private SqlSessionFactory sqlSessionFactory;


    public SqlMessageGenerator(MessageClientProperties messageClientProperties,
                               MessageRemoteService messageRemoteService,
                               RedisHelper redisHelper) {
        super(messageClientProperties, messageRemoteService, redisHelper);
        ApplicationContext context = ApplicationContextHelper.getContext();
        if (context != null) {
            sqlSessionFactory = context.getBean(SqlSessionFactory.class);
        }
        if (sqlSessionFactory == null) {
            throw new CommonException("Not found bean typed {}", SqlSessionFactory.class);
        }
    }

    public SqlMessageGenerator(MessageClientProperties messageClientProperties,
                               MessageRemoteService messageRemoteService,
                               RedisHelper redisHelper,
                               SqlSessionFactory sqlSessionFactory) {
        super(messageClientProperties, messageRemoteService, redisHelper);
        this.sqlSessionFactory = sqlSessionFactory;
    }

    public SqlMessageGenerator(DefaultMessageGenerator messageGenerator, Object sqlSessionFactory) {
        super(messageGenerator);
        this.sqlSessionFactory = (SqlSessionFactory) sqlSessionFactory;
    }

    @Override
    public Message generateMessage(long tenantId, String templateCode, String serverTypeCode, Map<String, String> args, boolean sqlEnable, String lang) {
        Map<String, Object> objectArgs = new HashMap<>(args.size());
        if (!CollectionUtils.isEmpty(args)) {
            objectArgs.putAll(args);
        }
        return generateMessageObjectArgs(tenantId, templateCode, serverTypeCode, objectArgs, sqlEnable, lang);
    }

    @Override
    public Message generateMessageObjectArgs(long tenantId, String templateCode, String serverTypeCode, Map<String, Object> objectArgs, boolean sqlEnable, String lang) {
        // 获取消息模板
        lang = getLang(lang);
        MessageTemplate messageTemplate = getMessageTemplate(tenantId, templateCode, lang);
        if (messageTemplate == null || BaseConstants.Flag.NO.equals(messageTemplate.getEnabledFlag())) {
            return null;
        }
        // 拼接消息内容
        Message message = generateMessage(messageTemplate, templateCode, lang, tenantId);

        if (sqlEnable) {
            objectArgs = appendSqlParam(tenantId, templateCode, objectArgs, lang);
        }
        // 从消息内容中替换获取的参数
        return generateMessage(message, objectArgs);
    }

    @Override
    public Map<String, Object> appendSqlParam(long tenantId, String templateCode, Map<String, Object> objectArgs, String lang) {
        // 获取消息模板
        lang = getLang(lang);
        MessageTemplate messageTemplate = getMessageTemplate(tenantId, templateCode, lang);
        if (messageTemplate == null || BaseConstants.Flag.NO.equals(messageTemplate.getEnabledFlag())) {
            return null;
        }
        Map<String, Object> sqlArgs = new HashMap<>(16);
        sqlArgs.putAll(objectArgs);
        // 从 SQL 获取参数
        if (!sqlArgs.containsKey(TENANT_ID)) {
            sqlArgs.put(TENANT_ID, String.valueOf(tenantId));
        }
        if (!sqlArgs.containsKey(ORGANIZATION_ID)) {
            sqlArgs.put(ORGANIZATION_ID, String.valueOf(tenantId));
        }
        if (!sqlArgs.containsKey(LANG)) {
            sqlArgs.put(LANG, lang);
        }
        if (StringUtils.hasText(messageTemplate.getSqlValue())) {
            if (!CollectionUtils.isEmpty(sqlArgs)) {
                // 空替换为 NULL
                for (Map.Entry<String, Object> entry : sqlArgs.entrySet()) {
                    if (!StringUtils.hasText(String.valueOf(entry.getValue()))) {
                        entry.setValue("NULL");
                    }
                }
                messageTemplate.setSqlValue(VelocityUtils.parseObject(messageTemplate.getSqlValue(), sqlArgs));
            }
            // 拆分多条SQL后执行
            String[] sqlValues = messageTemplate.getSqlValue().split(";");
            try (SqlSession session = sqlSessionFactory.openSession()) {
                for (String sql : sqlValues) {
                    Map<String, Object> sqlResult = new DefaultDynamicSqlMapper(session).selectOne(sql);
                    if (!CollectionUtils.isEmpty(sqlResult)) {
                        sqlResult.forEach(objectArgs::put);
                    }
                }
            }
        }
        return objectArgs;
    }
}
