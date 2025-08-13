package org.hzero.boot.platform.entity.initializer;

import io.choerodon.mybatis.domain.Config;
import io.choerodon.mybatis.domain.EntityTable;
import io.choerodon.mybatis.helper.EntityHelper;
import io.choerodon.mybatis.spring.CommonMapperScannerConfigurer;
import org.apache.commons.collections4.MapUtils;
import org.hzero.boot.platform.entity.autoconfigure.EntityRegistProperties;
import org.hzero.boot.platform.entity.helper.EntityRegisterHelper;
import org.hzero.boot.platform.entity.service.EntityRegistService;
import org.mybatis.spring.mapper.MapperScannerConfigurer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * entity注册启动类
 *
 * @author xingxing.wu@hand-china.com 2019/07/08 15:33
 */
@Component
public class EntityRegistInitializer implements ApplicationRunner {
    private static final Logger LOGGER = LoggerFactory.getLogger(EntityRegistInitializer.class);
    private static final String PROPERTY_NAME = "entityClassTableMap";

    @Autowired
    private EntityRegistService entityRegistService;
    @Autowired
    private MapperScannerConfigurer configurer;
    @Autowired
    private EntityRegistProperties entityRegistProperties;


    @Override
    public void run(ApplicationArguments args) {
        this.entityRegisterInit();
    }
    public void entityRegisterInit() {
        if (entityRegistProperties == null || !entityRegistProperties.isEnable()) {
            LOGGER.info("will not do entity regist");
            return;
        }
        //通过上下文的CommonMapperScannerConfigurer类获取到猪齿鱼的mybatis配置类
        Config config = ((CommonMapperScannerConfigurer) configurer).getMapperHelper().getConfig();
        Assert.isTrue(config != null, "can not get  config");
        Set<Class<?>> entityClassSet = EntityRegisterHelper.getEntityClassSet();
        //为空直接返回
        if (entityClassSet == null || entityClassSet.size() == 0) {
            LOGGER.info("entityClass set is null");
            return;
        }
        Map<Class<?>, EntityTable> tempEntityClassTableMap = new HashMap<>(entityClassSet.size());
        //获取EntityHelper上下文已经缓存好的map
        Map<Class<?>, EntityTable> entityClassTableMap = this.getEntityClassTableMap();
        //获取需要注册的entity对应的EntityTable，优先从EntityHelper上下文获取，如果没有自己构造
        for (Class<?> entityClass : entityClassSet) {
            EntityTable entityTable = entityClassTableMap.get(entityClass);
            if (entityTable == null) {
                entityTable = EntityRegisterHelper.initEntityNameMap(entityClass, config);
            }
            tempEntityClassTableMap.put(entityClass, entityTable);
        }
        //执行注册的入口
        if (MapUtils.isNotEmpty(tempEntityClassTableMap)) {
            entityRegistService.doRegist(tempEntityClassTableMap);
        }
    }

    /**
     * 反射获取包含实体类和table类的map
     *
     * @return 包含实体类和table类的map
     */
    Map<Class<?>, EntityTable> getEntityClassTableMap() {
        Map<Class<?>, EntityTable> entityClassTableMap = new HashMap<>(2);
        Field[] declaredFields = EntityHelper.class.getDeclaredFields();
        try {
            for (Field field : declaredFields) {
                if (field.getName().equals(PROPERTY_NAME)) {
                    field.setAccessible(true);
                    entityClassTableMap = (Map<Class<?>, EntityTable>) field.get(EntityHelper.class);
                }
            }
        } catch (Exception e) {
            LOGGER.error("get entity class table map with error", e);
        }

        return entityClassTableMap;
    }

}
