package org.hzero.platform.app.service.impl;

import org.apache.commons.collections4.CollectionUtils;
import org.hzero.boot.platform.entity.dto.EntityColumnDTO;
import org.hzero.boot.platform.entity.dto.EntityColumnProcessType;
import org.hzero.boot.platform.entity.dto.EntityTableDTO;
import org.hzero.boot.platform.entity.dto.RegistParam;
import org.hzero.boot.platform.entity.redis.EntityRedisService;
import org.hzero.core.redis.RedisHelper;
import org.hzero.mybatis.helper.SecurityTokenHelper;
import org.hzero.platform.app.service.EntityRegistOperateService;
import org.hzero.platform.app.service.EntityTableService;
import org.hzero.platform.domain.entity.EntityColumn;
import org.hzero.platform.domain.entity.EntityTable;
import org.hzero.platform.domain.repository.EntityColumnRepository;
import org.hzero.platform.domain.repository.EntityTableRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 实体表应用服务默认实现
 *
 * @author xingxing.wu@hand-china.com 2019-07-08 15:56:36
 */
@Service
public class EntityTableServiceImpl implements EntityTableService {
    private static final Logger logger = LoggerFactory.getLogger(EntityTableServiceImpl.class);
    @Autowired
    private EntityTableRepository entityTableRepository;
    @Autowired
    private EntityColumnRepository entityColumnRepository;
    @Autowired
    private EntityRegistOperateService entityRegistOperateService;
    @Autowired
    private RedisHelper redisHelper;

    @Override
    public void doRegist(RegistParam param) {
        if (param == null) {
            return;
        }
        //注意单独提取entityRegistOperateService接口目的是为了实现事务。经过测试@Transactional是需要通过接口调用才会起作用（AOP）

        //处理EntityTable新增
        if (param.getEntityTableInsertFlag() && CollectionUtils.isNotEmpty(param.getEntityTableInsertList())) {
            List<EntityTableDTO> insertList = param.getEntityTableInsertList();
            entityRegistOperateService.doEntityTableInsert(EntityTable.copyProperty(insertList));
        }

        //处理EntityTable删除
        if (param.getEntityTableDeleteFlag() && CollectionUtils.isNotEmpty(param.getEntityTableDeleteList())) {
            List<EntityTableDTO> deleteList = param.getEntityTableDeleteList();
            entityRegistOperateService.doEntityTableDelete(EntityTable.copyProperty(deleteList));
        }

        //处理EntityTable更新
        if (param.getEntityTableUpdateFlag() && CollectionUtils.isNotEmpty(param.getEntityTableUpdateList())) {
            List<EntityTableDTO> updateList = param.getEntityTableUpdateList();
            entityRegistOperateService.doEntityTableUpdate(EntityTable.copyProperty(updateList));
        }
        //处理EntityColumn新增
        if (param.getEntityColumnInsertFlag() && CollectionUtils.isNotEmpty(param.getEntityColumnInsertList())) {
            List<EntityColumnDTO> insertList = param.getEntityColumnInsertList();
            entityRegistOperateService.doEntityColumnOperate(EntityColumn.copyProperty(insertList), EntityColumnProcessType.INSERT);
        }
        //处理EntityColumn更新
        if (param.getEntityColumnUpdateFlag() && CollectionUtils.isNotEmpty(param.getEntityColumnUpdateList())) {
            List<EntityColumnDTO> updateList = param.getEntityColumnUpdateList();
            entityRegistOperateService.doEntityColumnOperate(EntityColumn.copyProperty(updateList), EntityColumnProcessType.UPDATE);
        }
        //处理EntityColumn删除
        if (param.getEntityColumnDeleteFlag() && CollectionUtils.isNotEmpty(param.getEntityColumnDeleteList())) {
            List<EntityColumnDTO> deleteList = param.getEntityColumnDeleteList();
            entityRegistOperateService.doEntityColumnOperate(EntityColumn.copyProperty(deleteList), EntityColumnProcessType.DELETE);
        }
    }

    @Override
    public void initAllDataToRedis() {
        logger.info("[Entity] Database entity cache initialization begins.");
        SecurityTokenHelper.close();
        Set<String> serviceNames = entityTableRepository.selectServices();
        logger.info("[Entity] Initialize service list : {}", serviceNames);
        AtomicInteger totalEntity = new AtomicInteger();
        serviceNames.forEach(serviceName -> {
            SecurityTokenHelper.close();
            List<EntityTable> entityTables = entityTableRepository.select(EntityTable.FIELD_SERVICE_NAME, serviceName);
            logger.info("[Entity] The number of entities obtained by service {} : {}", serviceName, entityTables.size());
            totalEntity.addAndGet(entityTables.size());
            if (CollectionUtils.isNotEmpty(entityTables)) {
                Map<String, String> entityTableJsonMap = new HashMap<>(entityTables.size());
                for (EntityTable entityTable : entityTables) {
                    SecurityTokenHelper.close();
                    List<EntityColumn> entityColumns = entityColumnRepository.select(EntityColumn.FIELD_ENTITY_TABLE_ID, entityTable.getEntityTableId());
                    logger.debug("[Entity] The number of columns obtained by entity {} : {}", entityTable.getTableName(), entityColumns.size());
                    entityTable.setEntityColumnList(entityColumns);
                    entityTableJsonMap.put(entityTable.getTableName(), redisHelper.toJson(entityTable));
                }
                //先删除该服务的所有缓存
                redisHelper.delKey(EntityRedisService.buildCatchKey(serviceName));
                EntityRedisService.addEntityTableToRedis(serviceName, redisHelper, entityTableJsonMap);
            }
        });
        SecurityTokenHelper.clear();
        logger.info("[Entity] The database entity cache is initialized successfully : {}", totalEntity.get());
    }
}
