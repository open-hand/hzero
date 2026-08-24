package org.hzero.swagger.infra.repository.impl;

import java.util.List;

import org.apache.commons.collections4.CollectionUtils;
import org.hzero.mybatis.base.impl.BaseRepositoryImpl;
import org.hzero.swagger.domain.entity.Swagger;
import org.hzero.swagger.domain.repository.SwaggerRepository;
import org.hzero.swagger.infra.mapper.SwaggerMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Swagger
 * 
 * @author bojiangzhou 2018/12/13
 */
@Component
public class SwaggerRepositoryImpl extends BaseRepositoryImpl<Swagger> implements SwaggerRepository {

    @Autowired
    private SwaggerMapper swaggerMapper;

    @Override
    public Swagger selectId(Swagger param) {
        List<Swagger> swaggers = swaggerMapper.selectSwaggerId(param);
        return CollectionUtils.isNotEmpty(swaggers) ? swaggers.get(0) : null;
    }
}
