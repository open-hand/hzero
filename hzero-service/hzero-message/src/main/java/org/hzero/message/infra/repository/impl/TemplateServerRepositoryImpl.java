package org.hzero.message.infra.repository.impl;


import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.hzero.message.domain.entity.TemplateServer;
import org.hzero.message.domain.entity.TemplateServerLine;
import org.hzero.message.domain.repository.TemplateServerRepository;
import org.hzero.message.infra.mapper.TemplateServerLineMapper;
import org.hzero.message.infra.mapper.TemplateServerMapper;
import org.hzero.mybatis.base.impl.BaseRepositoryImpl;
import org.hzero.mybatis.domian.Condition;
import org.hzero.mybatis.util.Sqls;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import io.choerodon.core.domain.Page;
import io.choerodon.mybatis.pagehelper.PageHelper;
import io.choerodon.mybatis.pagehelper.domain.PageRequest;

/**
 * 消息模板账户 资源库实现
 *
 * @author zhiying.dong@hand-china.com 2018-09-07 11:09:29
 */
@Component
public class TemplateServerRepositoryImpl extends BaseRepositoryImpl<TemplateServer> implements TemplateServerRepository {

    private final TemplateServerMapper templateServerMapper;
    private final TemplateServerLineMapper templateServerLineMapper;

    @Autowired
    public TemplateServerRepositoryImpl(TemplateServerMapper templateServerMapper,
                                        TemplateServerLineMapper templateServerLineMapper) {
        this.templateServerMapper = templateServerMapper;
        this.templateServerLineMapper = templateServerLineMapper;
    }

    @Override
    public Page<TemplateServer> pageTemplateServer(Long tenantId, String messageCode, String messageName, boolean includeSiteIfQueryByTenantId, PageRequest pageRequest) {
        return PageHelper.doPageAndSort(pageRequest, () -> {
            List<TemplateServer> templateServerList = templateServerMapper.selectTemplateServer(tenantId, messageCode, messageName, includeSiteIfQueryByTenantId);
            if (!CollectionUtils.isEmpty(templateServerList)) {
                Map<Long, List<TemplateServerLine>> serverLineMap = templateServerLineMapper.selectByCondition(Condition.builder(TemplateServerLine.class)
                        .select(TemplateServerLine.FIELD_TEMP_SERVER_LINE_ID, TemplateServerLine.FIELD_TEMP_SERVER_ID,
                                TemplateServerLine.FIELD_TYPE_CODE, TemplateServerLine.FIELD_TRY_TIMES, TemplateServerLine.FIELD_ENABLED_FLAG)
                        .andWhere(Sqls.custom().andIn(TemplateServerLine.FIELD_TEMP_SERVER_ID, templateServerList.stream().map(TemplateServer::getTempServerId).collect(Collectors.toSet())))
                        .build()).stream().collect(Collectors.groupingBy(TemplateServerLine::getTempServerId));
                if (!CollectionUtils.isEmpty(serverLineMap)) {
                    templateServerList.forEach(item -> item.setServerList(serverLineMap.get(item.getTempServerId())));
                }
            }
            return templateServerList;
        });
    }

    @Override
    public TemplateServer getTemplateServer(Long tenantId, long tempServerId) {
        return templateServerMapper.getTemplateServer(tenantId, tempServerId);
    }
}
