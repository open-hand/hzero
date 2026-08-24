package org.hzero.swagger.domain.repository;

import org.hzero.mybatis.base.BaseRepository;
import org.hzero.swagger.domain.entity.Swagger;

/**
 * Swagger
 * 
 * @author bojiangzhou 2018/12/13
 */
public interface SwaggerRepository extends BaseRepository<Swagger> {

    /**
     * 查询主键ID
     * @param param 参数
     */
    Swagger selectId(Swagger param);

}
