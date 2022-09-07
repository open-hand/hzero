package org.hzero.platform.app.service.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.commons.collections4.CollectionUtils;
import org.hzero.boot.platform.entity.dto.EntityColumnProcessType;
import org.hzero.boot.platform.entity.redis.EntityRedisService;
import org.hzero.core.base.BaseConstants;
import org.hzero.core.redis.RedisHelper;
import org.hzero.platform.app.service.EntityColumnService;
import org.hzero.platform.app.service.EntityRegistOperateService;
import org.hzero.platform.domain.entity.EntityColumn;
import org.hzero.platform.domain.entity.EntityTable;
import org.hzero.platform.domain.repository.EntityColumnRepository;
import org.hzero.platform.domain.repository.EntityTableRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * description
 *
 * @author xingxing.wu@hand-china.com 2019/07/19 15:02
 */
@Service
public class EntityRegistOperateServiceImpl implements EntityRegistOperateService {
    @Autowired
    private EntityTableRepository entityTableRepository;
    @Autowired
    private EntityColumnRepository entityColumnRepository;
    @Autowired
    private EntityColumnService entityColumnService;
    @Autowired
    private RedisHelper redisHelper;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void doEntityTableInsert(List<EntityTable> entityTableList) {
        Map<String, String> entityTableJsonMap = new HashMap<>(entityTableList.size());
        for (EntityTable entityTable : entityTableList) {
            entityTableRepository.insertSelective(entityTable);
            if (CollectionUtils.isNotEmpty(entityTable.getEntityColumnList())) {
                List<EntityColumn> entityColumnList = entityTable.getEntityColumnList();
                for (EntityColumn entityColumn : entityColumnList) {
                    entityColumn.setEntityTableId(entityTable.getEntityTableId());
                    entityColumnRepository.insertSelective(entityColumn);
                }
            }
            entityTableJsonMap.put(entityTable.getTableName(), redisHelper.toJson(entityTable));
        }
        //写入缓存
        String serviceName = entityTableList.get(0).getServiceName();
        EntityRedisService.addEntityTableToRedis(serviceName, redisHelper, entityTableJsonMap);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void doEntityTableDelete(List<EntityTable> entityTableList) {
        Map<String, String> entityTableJsonMap = new HashMap<>(entityTableList.size());
        for (EntityTable entityTable : entityTableList) {
            entityTable.setDeletedFlag(BaseConstants.Flag.YES);
            entityTableRepository.updateByPrimaryKey(entityTable);
            List<EntityColumn> entityColumnList = entityTable.getEntityColumnList();
            if (CollectionUtils.isNotEmpty(entityColumnList)) {
                entityColumnList.forEach(item -> item.setDeletedFlag(BaseConstants.Flag.YES));
                entityColumnRepository.batchUpdateByPrimaryKey(entityColumnList);
            }
            entityTableJsonMap.put(entityTable.getTableName(), redisHelper.toJson(entityTable));
        }
        //写入缓存
        String serviceName = entityTableList.get(0).getServiceName();
        EntityRedisService.addEntityTableToRedis(serviceName, redisHelper, entityTableJsonMap);

    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void doEntityTableUpdate(List<EntityTable> entityTableList) {
        Map<String, String> entityTableJsonMap = new HashMap<>(entityTableList.size());
        for (EntityTable entityTable : entityTableList) {
            entityTableRepository.updateByPrimaryKey(entityTable);
            entityTable.setEntityColumnList(entityColumnRepository.select(EntityColumn.FIELD_ENTITY_TABLE_ID, entityTable.getEntityTableId()));
            entityTableJsonMap.put(entityTable.getTableName(), redisHelper.toJson(entityTable));
        }
        //写入缓存
        String serviceName = entityTableList.get(0).getServiceName();
        EntityRedisService.addEntityTableToRedis(serviceName, redisHelper, entityTableJsonMap);

    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void doEntityColumnOperate(List<EntityColumn> entityColumnList, EntityColumnProcessType processType) {
        //按EntityTableId分组
        Map<Long, List<EntityColumn>> listMap = entityColumnList.stream().collect(Collectors.groupingBy(EntityColumn -> EntityColumn.getEntityTableId()));
        Map<String, String> entityTableJsonMap = new HashMap<>(listMap.size());
        final String[] serviceName = {null};
        listMap.forEach((entityTableId, entityColumns) -> {
            //查询entityTable
            EntityTable entityTable = entityTableRepository.selectByPrimaryKey(entityTableId);
            if (entityTable == null) {
                return;
            }
            if (serviceName[0] == null) {
                serviceName[0] = entityTable.getServiceName();
            }
            if (CollectionUtils.isEmpty(entityColumns)) {
                return;
            }
            //持久化到数据库
            if (EntityColumnProcessType.INSERT.equals(processType)) {
                entityColumnRepository.batchInsertSelective(entityColumns);
            } else if (EntityColumnProcessType.UPDATE.equals(processType)) {
                entityColumnRepository.batchUpdateByPrimaryKey(entityColumns);
            } else if (EntityColumnProcessType.DELETE.equals(processType)) {
                //2019/7/23修改为进行逻辑删除
                entityColumns.forEach(item -> item.setDeletedFlag(BaseConstants.Flag.YES));
                entityColumnRepository.batchUpdateByPrimaryKey(entityColumns);
            }
            //获取最新的数据放入缓存
            entityTable.setEntityColumnList(entityColumnRepository.select(EntityColumn.FIELD_ENTITY_TABLE_ID, entityTableId));
            entityTableJsonMap.put(entityTable.getTableName(), redisHelper.toJson(entityTable));
        });

        //写入缓存
        EntityRedisService.addEntityTableToRedis(serviceName[0], redisHelper, entityTableJsonMap);
    }
}
