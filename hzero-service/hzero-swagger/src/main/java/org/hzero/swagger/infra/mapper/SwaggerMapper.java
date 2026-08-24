package org.hzero.swagger.infra.mapper;

import java.util.List;

import org.hzero.swagger.domain.entity.Swagger;

import io.choerodon.mybatis.common.BaseMapper;

/**
 * @date 2018/1/24
 */
public interface SwaggerMapper extends BaseMapper<Swagger> {

    List<Swagger> selectSwaggerId(Swagger swagger);

}
