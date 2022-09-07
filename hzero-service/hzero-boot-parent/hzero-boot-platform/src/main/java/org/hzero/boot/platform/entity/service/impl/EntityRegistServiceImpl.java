package org.hzero.boot.platform.entity.service.impl;

import com.google.common.collect.Maps;
import io.choerodon.mybatis.domain.EntityColumn;
import io.choerodon.mybatis.domain.EntityTable;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.hzero.boot.platform.entity.dto.EntityColumnDTO;
import org.hzero.boot.platform.entity.dto.EntityTableDTO;
import org.hzero.boot.platform.entity.dto.RegistParam;
import org.hzero.boot.platform.entity.dto.SupportTypeEnum;
import org.hzero.boot.platform.entity.redis.EntityRedisService;
import org.hzero.boot.platform.entity.service.EntityRegistService;
import org.hzero.boot.platform.entity.service.EntityRegisterExecutor;
import org.hzero.common.HZeroService;
import org.hzero.core.base.BaseConstants;
import org.hzero.core.redis.RedisHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import java.util.*;

/**
 * 执行entity的注册
 *
 * @author xingxing.wu@hand-china.com 2019/07/08 16:30
 */
@Service
public class EntityRegistServiceImpl implements EntityRegistService {
    private static final String PROPERTY_SERVICE_NAME = "spring.application.name";
    private static final Logger LOGGER = LoggerFactory.getLogger(EntityRegistServiceImpl.class);


    @Autowired
    private Environment environment;
    @Autowired
    private RedisHelper redisHelper;

    @Autowired
    private List<EntityRegisterExecutor> entityRegisterExecutors;

    @Override
    @Async("entityRegistAsyncTaskExecutor")
    public void doRegist(Map<Class<?>, EntityTable> entityClassTableMap) {
        LOGGER.info("==>start invoke entity regist asyncTask");
        if (MapUtils.isEmpty(entityClassTableMap)) {
            LOGGER.info("==>entity Class Table Map is empty");
            return;
        }
        Map<String, EntityTableDTO> entityTableMap = new HashMap<>(entityClassTableMap.size());
        for (Map.Entry<Class<?>, EntityTable> entry : entityClassTableMap.entrySet()) {
            EntityTable entityTable = entry.getValue();
            EntityTableDTO entityTableDTO = buildEntityTable(entityTable);
            if (StringUtils.isEmpty(entityTableDTO.getTableName())) {
                //正常情况表名不为null，如果为null，直接舍弃
                LOGGER.error("can not get tableName from :" + entityTableDTO.toString());
                continue;
            }
            entityTableMap.put(entityTableDTO.getTableName(), entityTableDTO);
        }
        if (MapUtils.isEmpty(entityTableMap)) {
            return;
        }
        //构造注册参数
        LOGGER.info("==>start to build register parameter");
        RegistParam param = buildRegistParamForEntityTable(entityTableMap);
        //执行注册
        if (!param.isNeedRegist()) {
            LOGGER.info("==>no thing need to regist");
            return;
        }
        SupportTypeEnum supportType;
        // 服务名
        String serviceName = environment.getProperty(PROPERTY_SERVICE_NAME);
        if (StringUtils.contains(serviceName, HZeroService.getRealName(HZeroService.Platform.NAME))) {
            supportType = SupportTypeEnum.INTERNAL;
        } else {
            supportType = SupportTypeEnum.FEIGN;
        }
        //选择对应的注册器进行注册
        if (CollectionUtils.isNotEmpty(entityRegisterExecutors)) {
            for (EntityRegisterExecutor entityRegisterExecutor : entityRegisterExecutors) {
                if (supportType.equals(entityRegisterExecutor.getSupportType())) {
                    LOGGER.info("============start to do regist by executor of :" + entityRegisterExecutor.getClass());
                    entityRegisterExecutor.doRegist(param);
                    return;
                }
            }
        }
        LOGGER.info("============no support  entityRegisterExecutor find ============");

    }

    /**
     * 构建EntityTableDTO对象
     *
     * @param entityTable 实体表对象
     * @return
     */
    private EntityTableDTO buildEntityTable(EntityTable entityTable) {
        EntityTableDTO entityTableDTO = new EntityTableDTO();
        entityTableDTO.copyPropertiesFromEntityTable(entityTable);
        // 服务名
        String serviceName = environment.getProperty(PROPERTY_SERVICE_NAME);
        Assert.isTrue(StringUtils.isNotEmpty(serviceName), "can not find service name");
        entityTableDTO.setServiceName(serviceName);
        //获取EntityColumnDTO
        entityTableDTO.setEntityColumnList(buildEntityColumn(entityTable));
        return entityTableDTO;
    }

    /**
     * 构建entityTable的相关参数
     *
     * @param newEntityTableMap 最新的entityTable数据
     * @return
     */
    private RegistParam buildRegistParamForEntityTable(Map<String, EntityTableDTO> newEntityTableMap) {
        // 服务名
        String serviceName = environment.getProperty(PROPERTY_SERVICE_NAME);
        RegistParam param = new RegistParam();
        Map<String, EntityTableDTO> oldEntityTableMap = EntityRedisService.getCurrencyServiceEntityTableMapFromRedis(serviceName, redisHelper);
        if (MapUtils.isEmpty(oldEntityTableMap)) {
            Collection<EntityTableDTO> values = newEntityTableMap.values();
            param.setEntityTableInsertList(new ArrayList<>(values));
            param.setEntityTableInsertFlag(Boolean.TRUE);
            return param;
        }
        Map<String, EntityTableDTO> oldEntityTableMapForUpdate = new HashMap<>();
        Map<String, EntityTableDTO> newEntityTableMapForUpdate = new HashMap<>();

        //处理新增的entityTable
        newEntityTableMap.forEach((key, value) -> {
            if (!oldEntityTableMap.containsKey(key)) {
                param.addEntityTableInsert(value);
                param.setEntityTableInsertFlag(Boolean.TRUE);
            } else {
                //需要继续判断是否需要更新的EntityTableMap ，使用map方便存取
                oldEntityTableMapForUpdate.put(key, oldEntityTableMap.get(key));
                newEntityTableMapForUpdate.put(key, value);
            }
        });

        //处理删除的entityTable
        oldEntityTableMap.forEach((key, value) -> {
            if (!newEntityTableMap.containsKey(key) && BaseConstants.Flag.NO.equals(value.getDeletedFlag())) {
                param.addEntityTableDelete(value);
                param.setEntityTableDeleteFlag(Boolean.TRUE);
            }
        });
        //处理需要更新的entityTable
        if (MapUtils.isNotEmpty(newEntityTableMapForUpdate)) {
            newEntityTableMapForUpdate.forEach((key, value) -> {
                //先判断EntityTable有没有变化,get()必定存在，无需判空
                EntityTableDTO oldEntityTable = oldEntityTableMapForUpdate.get(key);
                //如果为空，为了不影响整体流程，先跳过，打印日志
                if (value == null || oldEntityTable == null) {
                    LOGGER.debug(".............can not find EntityTable by key of" + key);
                    return;
                }
                if (value.hashCode() != oldEntityTable.hashCode()) {
                    //更新需要注入ID和versionNumber
                    value.setEntityTableId(oldEntityTable.getEntityTableId());
                    value.setObjectVersionNumber(oldEntityTable.getObjectVersionNumber());
                    param.addEntityTableUpdate(value);
                    param.setEntityTableUpdateFlag(Boolean.TRUE);
                }
                //再判断其column是否有变化
                List<EntityColumnDTO> newEntityColumnList = value.getEntityColumnList();
                List<EntityColumnDTO> oldEntityColumnList = oldEntityTable.getEntityColumnList();
                //正常情况不会为null
                if (oldEntityColumnList == null || newEntityColumnList == null) {
                    LOGGER.debug(".............can not find EntityColumn by key of" + key);
                    return;
                }
                //处理column
                buildRegistParamForEntityColumn(param, oldEntityTable.getEntityTableId(), oldEntityColumnList, newEntityColumnList);
            });
        }


        return param;
    }


    private void buildRegistParamForEntityColumn(RegistParam param, Long entityTableId, List<EntityColumnDTO> oldEntityColumnList, List<EntityColumnDTO> newEntityColumnList) {
        //转换成map
        Map<String, EntityColumnDTO> oldEntityColumnMap = Maps.uniqueIndex(oldEntityColumnList, EntityColumnDTO::getFieldName);
        Map<String, EntityColumnDTO> newEntityColumnMap = Maps.uniqueIndex(newEntityColumnList, EntityColumnDTO::getFieldName);

        Map<String, EntityColumnDTO> oldEntityColumnMapForUpdate = new HashMap<>();
        Map<String, EntityColumnDTO> newEntityColumnMapForUpdate = new HashMap<>();

        //处理新增的column
        newEntityColumnMap.forEach((key, value) -> {
            if (!oldEntityColumnMap.containsKey(key)) {
                //新增的column需要注入entityTableId
                value.setEntityTableId(entityTableId);
                param.addEntityColumnInsert(value);
                param.setEntityColumnInsertFlag(Boolean.TRUE);
            } else {
                newEntityColumnMapForUpdate.put(key, value);
                oldEntityColumnMapForUpdate.put(key, oldEntityColumnMap.get(key));
            }
        });
        //处理删除的column
        oldEntityColumnMap.forEach((key, value) -> {
            if (!newEntityColumnMap.containsKey(key) && BaseConstants.Flag.NO.equals(value.getDeletedFlag())) {
                param.addEntityColumnDelete(value);
                param.setEntityColumnDeleteFlag(Boolean.TRUE);
            }
        });
        //处理更新的column
        if (MapUtils.isNotEmpty(newEntityColumnMapForUpdate)) {
            newEntityColumnMapForUpdate.forEach((key, value) -> {
                EntityColumnDTO oldEntityColumn = oldEntityColumnMapForUpdate.get(key);

                if (value.hashCode() != oldEntityColumn.hashCode()) {
                    //更新需要注入ID和versionNumber
                    value.setEntityColumnId(oldEntityColumn.getEntityColumnId());
                    value.setEntityTableId(oldEntityColumn.getEntityTableId());
                    value.setObjectVersionNumber(oldEntityColumn.getObjectVersionNumber());
                    param.addEntityColumnUpdate(value);
                    param.setEntityColumnUpdateFlag(Boolean.TRUE);
                }
            });
        }

    }


    /**
     * 构建EntityColumnDTO对象
     *
     * @param entityTable 实体表对象
     * @return
     */
    private List<EntityColumnDTO> buildEntityColumn(EntityTable entityTable) {
        List<EntityColumnDTO> entityColumnList = new ArrayList<>();
        //entity字段
        //2019/7/23修改为不对transient字段进行注册
        Set<EntityColumn> entityClassColumns = entityTable.getEntityClassColumns();
        if (entityClassColumns != null) {
            buildEntityColumnList(entityColumnList, entityClassColumns, entityTable, BaseConstants.Flag.NO);
        }
//        //Transient字段
//        Set<EntityColumn> entityClassTransientColumns = entityTable.getEntityClassTransientColumns();
//        if (entityClassTransientColumns != null) {
//            buildEntityColumnList(entityColumnList, entityClassTransientColumns, entityTable, BaseConstants.Flag.YES);
//        }

        return entityColumnList;
    }


    /**
     * 构建EntityColumnDTO List
     *
     * @param entityClassColumns 待构建的列
     * @param entityTable        entityTable对象
     * @param transientFlag      是否为transient字段
     * @return
     */
    private void buildEntityColumnList(List<EntityColumnDTO> entityColumnList, Set<EntityColumn> entityClassColumns, EntityTable entityTable, Integer transientFlag) {
        //敏感字段
        Set<EntityColumn> dataSecurityColumns = entityTable.getDataSecurityColumns();
        //唯一性字段
        Set<EntityColumn> uniqueColumns = entityTable.getUniqueColumns();
        entityClassColumns.forEach(item -> {
            Integer uniqueFlag = BaseConstants.Flag.NO;
            Integer dataSecurityFlag = BaseConstants.Flag.NO;
            EntityColumnDTO entityColumnDTO = new EntityColumnDTO();
            if (CollectionUtils.isNotEmpty(dataSecurityColumns) && dataSecurityColumns.contains(item)) {
                dataSecurityFlag = BaseConstants.Flag.YES;
            }
            if (CollectionUtils.isNotEmpty(uniqueColumns) && uniqueColumns.contains(item)) {
                uniqueFlag = BaseConstants.Flag.YES;
            }
            entityColumnDTO.copyPropertiesFromEntityColumn(item, uniqueFlag, dataSecurityFlag, transientFlag);
            entityColumnList.add(entityColumnDTO);
        });
    }
}
